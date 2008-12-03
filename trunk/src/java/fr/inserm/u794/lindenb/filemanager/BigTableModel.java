package fr.inserm.u794.lindenb.filemanager;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;


import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import javax.swing.table.AbstractTableModel;

import org.lindenb.io.IOUtils;


public class BigTableModel
	extends AbstractTableModel
	{
	private static final long serialVersionUID = 1L;
	private static final int IO_ERROR=1;
	private static final int EVAL_SIZE=2;
	private static final int IS_CLOSING=3;
	private static final int EVAL_CONTENT=4;
	private int rows=0;
	private int cols=0;
	private JTable table;
	private File file;
	private Delimiter delimiter;
	private Thread evalSizeThread;
	private Thread contentThread;
	private DataRect dataRect=null;
	private int status=EVAL_SIZE;
	private final ReentrantLock lock = new ReentrantLock();

	
	private class DataRect
		{
		int rowStart;
		int colStart;
		int rowEnd;
		int colEnd;
		String content[][]=null;
		DataRect()
			{
			lock.lock();
			try
				{
				Rectangle r= table.getVisibleRect();
				rowStart= table.rowAtPoint(new Point(0,r.y));
				rowEnd  = table.rowAtPoint(new Point(0,r.y+r.height));
				colStart= table.columnAtPoint(new Point(r.x,0));
				colEnd  = table.columnAtPoint(new Point(r.x+r.width,0));
				
				
				if(colEnd==-1 || colStart==-1 || rowStart==-1 || rowEnd==-1)
					{
					colStart=0;
					colEnd=0;
					rowStart=0;
					rowEnd=0;
					}
				this.content=new String[1+rowEnd-rowStart][];
				for(int i=0;i< this.content.length;++i)
					{
					this.content[i]=new String[1+colEnd-colStart];
					}
				} 
			finally
				{
				lock.unlock();
				}
			}
		public boolean contains(int row,int col)
			{
			return row>=rowStart && row<=rowEnd && col>=colStart && col<=colEnd;
			}
		public void setContent(int row,int col,String s)
			{
			this.content[row-this.rowStart][col-this.colStart]=s;
			}
		
		public String getContent(int row,int col)
			{
			if(!contains(row, col)) return "..Wait...";
			return this.content[row-this.rowStart][col-this.colStart];
			}
		
		public void updateModel()
			{
			lock.lock();
			try {	
			BigTableModel.this.dataRect=this;
			BigTableModel.this.fireTableRowsUpdated(this.rowStart, this.rowEnd);
			} catch(Throwable err)
			{
			err.printStackTrace();
			}finally
			{
			lock.unlock();
			}
			
			}
		@Override
		public String toString() {
			return ""+this.rowStart +"-"+this.rowEnd;
			}
		}
	
	
	private class ThreadEvalSize
		extends Thread
		{
		int Nrows=0;
		int Ncols=0;
		Timer updateRowCols= new Timer();
		boolean needUpdateFlag=false;
		
		ThreadEvalSize()
			{
			updateRowCols.scheduleAtFixedRate(new TimerTask()
				{
				@Override
				public void run() {
					updateModel();
					}
				}, 1000L,1000L);
			}
		
		@Override
		public void run()
			{
			needUpdateFlag=false;
			BufferedReader reader=null;
			try {
				reader= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(BigTableModel.this.file))));
				int c;
				int x=1;
				int y=1;
				while((c=reader.read())!=-1)
					{
					if(c==delimiter.charValue())
						{
						x++;
						if(Ncols<x)
							{
							Ncols=x;
							needUpdateFlag=true;
							}
						}
					else if(c=='\n')
						{
						y++;
						x=1;
						if(Ncols<x)//only one col
							{
							Ncols=x;
							needUpdateFlag=true;
							}
						if(Nrows<y)
							{
							Nrows=y;
							needUpdateFlag=true;
							}
						
						if(BigTableModel.this.status== IS_CLOSING)
							{
							break;
							}
						}
					}
				BigTableModel.this.status= BigTableModel.EVAL_CONTENT;
				} 
			catch (Throwable e) {
				BigTableModel.this.status= BigTableModel.IO_ERROR;
				e.printStackTrace();
				}
			finally
				{
				IOUtils.safeClose(reader);
				updateRowCols.cancel();
				updateModel();
				BigTableModel.this.evalSizeThread=null;
				BigTableModel.this.updateCells();
				}
			}
		
		
		
		private void updateModel()
			{
			
			if(!needUpdateFlag) return;
			if(BigTableModel.this.cols==Ncols)
				{
				BigTableModel.this.rows=Nrows;
				BigTableModel.this.fireTableDataChanged();
				}
			else
				{
				BigTableModel.this.cols=Ncols;
				BigTableModel.this.rows=Nrows;
				BigTableModel.this.fireTableStructureChanged();
				}
			}
		
		}
		
		private class ThreadGetContent
		extends Thread
			{
			private DataRect dataRect;
			
			ThreadGetContent(DataRect dataRect)
				{
				this.dataRect=dataRect;
				}
			
			@Override
			public void run()
				{
				BufferedReader reader=null;
				try {
					reader= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(BigTableModel.this.file))));
					int c;
					int x=0;
					int y=0;
					StringBuilder b=(y==this.dataRect.rowStart?new StringBuilder():null);
					while((c=reader.read())!=-1)
						{
						if(BigTableModel.this.contentThread!=this) break;
						if(c==delimiter.charValue())
							{
							if(b!=null)
								{
								if( x>= this.dataRect.colStart && x<=this.dataRect.colEnd)
									{
									this.dataRect.setContent(y,x,b.toString());
									}
								b.setLength(0);
								}
							x++;
							}
						else if(c=='\n')
							{
							if(b!=null && x>= this.dataRect.colStart && x<=this.dataRect.colEnd)
								{
								this.dataRect.setContent(y,x,b.toString());
								b.setLength(0);
								}
							
							y++;
							x=0;
							if(y>this.dataRect.rowEnd)
								{
								break;
								}
							if(y>=this.dataRect.rowStart)
								{
								if(b==null) b= new StringBuilder();
								b.setLength(0);
								}
							
							
							if(BigTableModel.this.status== IS_CLOSING)
								{
								break;
								}
							}
						else
							{
							if(b!=null) b.append((char)c);
							}
						}
					
					if(BigTableModel.this.contentThread==this)
						{
						this.dataRect.updateModel();
						}
					} 
				catch (Throwable e) {
					BigTableModel.this.status= BigTableModel.IO_ERROR;
					e.printStackTrace();
					}
				finally
					{
					IOUtils.safeClose(reader);
					}
				
				}
			

			
			}
	
	BigTableModel(File file,Delimiter delim)
		{
		this.file=file;
		this.delimiter=delim;
		this.evalSizeThread= new ThreadEvalSize();
		this.evalSizeThread.start();
		}
	
	private void updateCells()
		{
		if(this.evalSizeThread!=null)
			{
			return;
			}
		
		this.contentThread=new ThreadGetContent(new DataRect());
		this.contentThread.start();
		}
	
	
		
	public void setTable(JTable table)
		{
		this.table=table;
		this.table.getTableHeader().setReorderingAllowed(false);
		
		//this.table.setAutoCreateColumnsFromModel(false);
		table.addAncestorListener(new AncestorListener()
				{
				@Override
				public void ancestorAdded(AncestorEvent event)
					{
					}
				@Override
				public void ancestorRemoved(AncestorEvent event)
					{
					}
				
				@Override
				public void ancestorMoved(AncestorEvent event)
					{
					if(event.getAncestor()!=BigTableModel.this.table) return;
					if(!(event.getAncestorParent() instanceof JViewport)) return;
					BigTableModel.this.updateCells();
					}
				});
		}
	
	@Override
	public String getColumnName(int column) {
		if(column==0) return "Row";
		return String.valueOf(column-1);
		}
	
	@Override
	public int getColumnCount() {
		return (cols<=0?0:cols+1);
		}
	@Override
	public int getRowCount() {
		return rows;
		}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
		}
	
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
		{
		if(status==EVAL_SIZE) return "...Lookup Size...";
		if(status==IO_ERROR) return "#Error!";
		if(columnIndex==0) return String.valueOf(rowIndex+1);
		if(this.dataRect!=null) 
			{
			lock.lock();
			String s= this.dataRect.getContent(rowIndex, columnIndex-1);
			lock.unlock();
			return s;
			}
		return null;
		}
	
	public void close()
		{
		this.status=IS_CLOSING;
		}
	
	}

/*
public class BigTableModel extends JPanel
	{
	BigTableModel()
		{
		super(new BorderLayout());
		setPreferredSize(new Dimension(600,500));
		BigTableModel tm= new BigTableModel(new File("/home/lindenb/jeter.txt.gz"));
		JTable table= new JTable(tm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tm.setTable(table);
		JScrollPane scroll= new JScrollPane(table);
		this.add(scroll);
		}
	
	public static void main(String[] args)
		{
		JFrame f= new JFrame();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setContentPane(new Test());
		SwingUtils.center(f);
		SwingUtils.show(f);
		}
}*/
