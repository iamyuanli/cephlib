/**
 * 
 */
package fr.inserm.u794.lindenb.workbench;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;


import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;


import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.io.IOUtils;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.sql.SQLUtilities;
import org.lindenb.swing.SimpleDialog;
import org.lindenb.swing.SwingUtils;
import org.lindenb.swing.layout.InputLayout;
import org.lindenb.util.Cast;
import org.lindenb.util.Debug;
import org.lindenb.util.NamedObject;
import org.lindenb.util.TimeUtils;
import org.lindenb.util.iterator.CloseableIterator;


import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;

import fr.inserm.u794.lindenb.workbench.frame.RefFrame;
import fr.inserm.u794.lindenb.workbench.frame.TableFrame;
import fr.inserm.u794.lindenb.workbench.sql.SQLSource;
import fr.inserm.u794.lindenb.workbench.table.ChromPos;
import fr.inserm.u794.lindenb.workbench.table.Column;
import fr.inserm.u794.lindenb.workbench.table.Indexes;
import fr.inserm.u794.lindenb.workbench.table.Row;
import fr.inserm.u794.lindenb.workbench.table.Table;
import fr.inserm.u794.lindenb.workbench.table.TableRef;

/**
 * @author lindenb
 *
 */
public class Workbench extends JFrame
	{

	private static final String ACTION_LOAD_TABLE= "action.load.table";
	private static final String ACTION_LOAD_URL= "action.load.url";
	private static final String ACTION_LOAD_SQL= "action.load.sql";
	private static final String ACTION_TABLE_CONCAT= "action.table.concat";
	private static final String ACTION_TABLE_OVERLAP= "action.table.overlap";
	private static final String ACTION_QUIT= "action.quit";
	private static final long serialVersionUID = 1L;
	private static final String PREFS_FILE=".inserm-workbench-prefs.xml";
	
	private JDesktopPane desktop;
	private JPanel contentPane;
	private Properties preferences= new Properties();
	private ActionMap actionMap=new ActionMap();
	/** all table Ref */
	private TableRef.Model tableRefModel= new TableRef.Model();
	
	public Workbench()
		{
		super("File Manager");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		try
		{
		
		
		
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setReadOnly(false);
		//this.snp129DB= this.berkeleyEnv.openDatabase(null, "snp", dbcfg);
		
		
		}catch(Throwable er)
		{
			er.printStackTrace();
		}
		
		
		try {
			File f= new File(System.getProperty("user.home","."),PREFS_FILE);
			if(f.exists())
				{
				InputStream in= new FileInputStream(f);
 				this.preferences.loadFromXML(in);
 				in.close();
				}
			} 
		catch (Exception e)
			{
			preferences= new Properties();
			}
		
		this.contentPane= new JPanel(new BorderLayout());
		
		this.desktop= new JDesktopPane();
		this.desktop.setBorder(new EmptyBorder(5,5,5,5));
		this.contentPane.add(this.desktop,BorderLayout.CENTER);
		setContentPane(this.contentPane);
		
		
		/** actions */
		AbstractAction action=new AbstractAction("Load Table")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuLoadTable();
				}
			};
		this.actionMap.put(ACTION_LOAD_TABLE,action);
		
		action=new AbstractAction("Load URL...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuLoadURL();
				}
			};
		this.actionMap.put(ACTION_LOAD_URL,action);
		
		action=new AbstractAction("Quit")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuQuit();
				}
			};
		this.actionMap.put(ACTION_QUIT,action);
		//
		action=new AbstractAction("Load SQL...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuLoadSQL();
				}
			};
		this.actionMap.put(ACTION_LOAD_SQL,action);
		//
		action=new AbstractAction("Concat tables...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuConcat();
				}
			};
		this.actionMap.put(ACTION_TABLE_CONCAT,action);
		//
		action=new AbstractAction("Genomic Overlap")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuOverlap();
				}
			};
		this.actionMap.put(ACTION_TABLE_OVERLAP,action);
		
		
		/* MENUS */
		JMenuBar bar= new JMenuBar();
		setJMenuBar(bar);
		JMenu menu= new JMenu("File");
		bar.add(menu);
		JMenu sub= new JMenu("Load Table");
		menu.add(sub);
		sub.add(this.actionMap.get(ACTION_LOAD_TABLE));
		sub.add(this.actionMap.get(ACTION_LOAD_URL));
		sub.add(this.actionMap.get(ACTION_LOAD_SQL));
		menu.add(new JSeparator());
		menu.add(this.actionMap.get(ACTION_QUIT));
		menu= new JMenu("Tables");
		bar.add(menu);
		menu.add(this.actionMap.get(ACTION_LOAD_TABLE));
		menu.add(this.actionMap.get(ACTION_LOAD_URL));
		menu.add(this.actionMap.get(ACTION_LOAD_SQL));
		menu.add(new JSeparator());
		menu.add(this.actionMap.get(ACTION_TABLE_CONCAT));
		menu.add(this.actionMap.get(ACTION_TABLE_OVERLAP));
		
		/* TOOLBAR */
		JToolBar toolbar= new JToolBar(JToolBar.VERTICAL);
		this.contentPane.add(toolbar,BorderLayout.WEST);
		toolbar.add(new JButton(this.actionMap.get(ACTION_LOAD_TABLE)));
		toolbar.add(new JButton(this.actionMap.get(ACTION_LOAD_URL)));
		toolbar.add(new JButton(this.actionMap.get(ACTION_LOAD_SQL)));
		
		
		this.addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e)
				{
				RefFrame f= new RefFrame(Workbench.this,Workbench.this.tableRefModel);
				getDesktop().add(f);
				f.setVisible(true);
				}
			@Override
			public void windowClosing(WindowEvent e)
				{
				doMenuQuit();	
				}
			});
		
		}
	
	public void doMenuQuit()
		{
		this.setVisible(false);
		this.dispose();

		
		for(JInternalFrame ifr:getDesktop().getAllFrames())
			{
			ifr.setVisible(false);
			}
		//save preferences
		try {
			File f= new File(System.getProperty("user.home","."),PREFS_FILE);
			FileOutputStream out= new FileOutputStream(f);
			getPreferences().storeToXML(out, "Save on "+TimeUtils.toYYYYMMDD(), "UTF-8");
			out.flush();out.close();
			} 
		catch (Exception e)
			{
			
			}
		
		
		}
	
	public JDesktopPane getDesktop() {
		return desktop;
		}
	
	public Properties getPreferences()
		{
		return preferences;
		}
	
	
	public void doMenuLoadTable()
		{
		FileLoader loader= new FileLoader();
		if(JOptionPane.showConfirmDialog(this, loader,"Select...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		if(loader.getSelectedFile()==null || loader.getColumns().isEmpty()) return;
		
		
		try {
			Table table= new Table();
			table.setColumns(loader.getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName(loader.getSelectedFile().getName());
			
			BufferedReader r= null;
			try
				{
				r=IOUtils.openFile(loader.getSelectedFile());
				String line;
				if(loader.isFirstLineHeader())
					{
					line= r.readLine();
					if(line==null) throw new IOException("Cannot get header");
					//then ignore
					}
				
				int nLine=0;
				while((line=r.readLine())!=null)
					{
					String tokens[]=loader.getDelimiter().split(line);
					Row row= new Row(tokens);
					while(row.size()< table.getColumns().size()) row=row.add("");
					table.getDatabase().put(
							nLine,
							row
							);
					nLine++;
					}
				table.setRowCount(nLine);
				r.close();
				
				TableFrame frame= new TableFrame(this,table);
				getDesktop().add(frame);
				frame.setVisible(true);
				}
			catch(Exception err)
				{
				err.printStackTrace();
				throw err;
				}
			finally
				{
				IOUtils.safeClose(r);
				}
		} catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		}
	
	
	private class OverlapMaker
		extends JPanel
		{
		private JComboBox sourceA;
		private GenomicColumnSelector selectorA;
		private JComboBox sourceB;
		private GenomicColumnSelector selectorB;
		private SpinnerNumberModel extend;
		private JCheckBox limitOneMatch;
		private JCheckBox includeNonMatchingA;
		OverlapMaker()
			{
			super(new InputLayout());
			add(new JLabel("Source A"));
			add(sourceA=new JComboBox(getTableFrameAsDefaultComboModel()));
			add(new JLabel("Columns A"));
			add(selectorA=new GenomicColumnSelector());
			add(new JLabel("Source B"));
			add(sourceB=new JComboBox(getTableFrameAsDefaultComboModel()));
			add(new JLabel("Columns B"));
			add(selectorB=new GenomicColumnSelector());
			add(new JLabel("Extends"));
			add(new JSpinner(extend=new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1)));
			add(new JLabel("Limit"));
			add(limitOneMatch=new JCheckBox("Limit to one match",true));
			add(new JLabel("Include"));
			add(includeNonMatchingA=new JCheckBox("Include (A) items alone",true));
			
			sourceA.addActionListener(new ActionListener()
				{
				@Override
				public void actionPerformed(ActionEvent e) {
					Table tf=getTableA();
					selectorA.setTable(tf==null?null:tf);
					}
				});
			
			sourceB.addActionListener(new ActionListener()
				{
				@Override
				public void actionPerformed(ActionEvent e) {
					Table tf=getTableB();
					selectorB.setTable(tf==null?null:tf);
					}
				});
			sourceA.setSelectedIndex(-1);
			sourceB.setSelectedIndex(-1);
			}
		private Table getTable(JComboBox box)
			{
			NamedObject<?> o=NamedObject.class.cast(box.getSelectedItem());
			if(o==null) return null;
			return TableFrame.class.cast(o.getObject()).getTableModel().getTable();
			}
		
		Table getTableA()
			{
			return getTable(sourceA);
			}
		
		Table getTableB()
			{
			return getTable(sourceB);
			}
		public boolean isFormValid()
			{
			return getTableA()!=null && getTableB()!=null && selectorA.isFormValid() && selectorB.isFormValid();
			}
		}
	
	private static String normalizeChromosome(String s)
		{
		if(s==null) return null;
		s=s.toLowerCase().trim();
		if(s.startsWith("K"))
			{
			s=s.substring(1);
			}
		else if(s.startsWith("chrom"))
			{
			s=s.substring(5);
			}
		else if(s.startsWith("chr"))
			{
			s=s.substring(3);
			}
		return s;
		}
	
	private void doMenuOverlap()
		{
		OverlapMaker maker=new OverlapMaker();
		SimpleDialog dialog= new SimpleDialog(this,"Maker");
		dialog.getContentPane().add(maker,BorderLayout.CENTER);
		if(dialog.showDialog()!=SimpleDialog.OK_OPTION) return;
		
		if(!maker.isFormValid()) return;
		int extension= maker.extend.getNumber().intValue();
		
		List<Column> cols= new ArrayList<Column>(maker.getTableA().getColumns());
		for(Column c: maker.getTableB().getColumns())
			{
			cols.add(new Column(cols.size(),c.getLabel()));
			}
		SingleMapDatabase<ChromPos, Indexes> indexes=null;
		try
			{
			//create an index of the table B
			indexes=Berkeley.getInstance().createPositionIndex();
			for(int indexB= 0;indexB < maker.getTableB().getRowCount();++indexB)
				{
				Row rowB= maker.getTableB().getDatabase().get(indexB);
				if(rowB==null) continue;
				String chromB= normalizeChromosome(rowB.at(maker.selectorB.getColumnForChromosome()));
				if(chromB==null || chromB.length()==0) continue;
				String pos= rowB.at(maker.selectorB.getColumnForStart());
				if(!Cast.Long.isA(pos)) continue;
				long startB = Cast.Long.cast(pos);
				
				ChromPos chrompos= new ChromPos(chromB,startB);
				System.err.println("Indexing \""+chrompos+"\" \""+chrompos.getChrom()+"\"");
				Indexes idx= indexes.get(chrompos);
				if(idx==null) idx= new Indexes();
				idx.add(indexB);
				indexes.put(chrompos,idx);
				}
			
			
			
			Table t= new Table();
			t.setDatabase(Berkeley.getInstance().createTable());
			t.setColumns(cols);
			t.setName("Overlap");
			
			//loop over A
			int nLine=0;
			for(int indexA= 0;indexA < maker.getTableA().getRowCount();++indexA)
				{
				Row rowA= maker.getTableA().getDatabase().get(indexA);
				String chromA= normalizeChromosome(rowA.at(maker.selectorA.getColumnForChromosome()));
				if(chromA==null || chromA.length()==0) continue;
				String pos= rowA.at(maker.selectorA.getColumnForStart());
				if(!Cast.Long.isA(pos)) continue;
				long startA = Cast.Long.cast(pos);
				pos= rowA.at(maker.selectorA.getColumnForEnd());
				if(!Cast.Long.isA(pos)) continue;
				long endA = Cast.Long.cast(pos);
				if(startA>endA)
					{
					long tmp= startA;
					startA=endA;
					endA=tmp;
					}
				startA-=extension;
				endA+=extension;
				
				
				
				boolean found=false;
				//loop over the previous indexed database
				ChromPos chromPosA=new ChromPos(chromA,startA);
				DatabaseEntry chromPosEntry=  chromPosA.toEntry();
				DatabaseEntry indexesEntry= new DatabaseEntry();
				Cursor c= indexes.cursor();
				
				
				
				while(c.getNext(chromPosEntry, indexesEntry,null)==OperationStatus.SUCCESS)
					{
					ChromPos cp= ChromPos.BINDING.entryToObject(chromPosEntry);
					if(!cp.getChrom().equals(chromA))
						{
						continue;
						}
					if(cp.getPosition()> endA)
						{
						break;
						}
					if(cp.getPosition()< startA)
						{
						continue;
						}
					Indexes rowIdx= Indexes.BINDING.entryToObject(indexesEntry);
					for(int indexB: rowIdx)
						{
						Row rowB= maker.getTableB().getDatabase().get(indexB);
						String chromB= rowB.at(maker.selectorB.getColumnForChromosome());
						if(chromB.trim().length()==0 || !chromA.equalsIgnoreCase(chromB)) continue;
						
						pos= rowB.at(maker.selectorB.getColumnForStart());
						if(!Cast.Long.isA(pos)) continue;
						long startB = Cast.Long.cast(pos);
										
						pos= rowB.at(maker.selectorB.getColumnForEnd());
						if(!Cast.Long.isA(pos)) continue;
						long endB = Cast.Long.cast(pos);
						
						if(endA<startB || endB<startA) continue;
						Row joined= new Row(rowA);
						for(String s:rowB) joined=joined.add(s);
						t.getDatabase().put(nLine, joined);
						++nLine;
						found=true;
						if(maker.limitOneMatch.isSelected()) break;
						}
					}
				c.close();
				if(!found && maker.includeNonMatchingA.isSelected())
					{
					Row joined= new Row(rowA);
					while(joined.size() < t.getColumns().size()) joined=joined.add("");
					t.getDatabase().put(nLine, joined);
					++nLine;
					}
				}
			t.setRowCount(nLine);
			TableFrame frame= new TableFrame(this,t);
			getDesktop().add(frame);
			frame.setVisible(true);
			}
		catch(Throwable err)
			{
			err.printStackTrace();
			ThrowablePane.show(this, err);
			}
		finally
			{
			if(indexes!=null) try { indexes.close(); } catch(DatabaseException err){ }
			}
		}
	
	
	public void doMenuLoadURL()
		{
		UrlLoader loader= new UrlLoader();
		if(JOptionPane.showConfirmDialog(this, loader,"Select...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		if(loader.getSelectedURL()==null || loader.getColumns().isEmpty()) return;
		
		
		try {
			Table table= new Table();
			table.setColumns(loader.getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName(loader.getSelectedURL().toString());
			
			BufferedReader r= null;
			try
				{
				InputStream in= loader.getSelectedURL().openStream();
				if(loader.getSelectedURL().getPath().toLowerCase().endsWith(".gz"))
					{
					in= new GZIPInputStream(in);
					}
				r=new BufferedReader(new InputStreamReader(in));
				String line;
				if(loader.isFirstLineHeader())
					{
					line= r.readLine();
					if(line==null) throw new IOException("Cannot get header");
					//then ignore
					}
				
				int nLine=0;
				while((line=r.readLine())!=null)
					{
					String tokens[]=loader.getDelimiter().split(line);
					Row row= new Row(tokens);
					while(row.size()< table.getColumns().size()) row=row.add("");
					table.getDatabase().put(
							nLine,
							row
							);
					nLine++;
					}
				table.setRowCount(nLine);
				r.close();
				
				TableFrame frame= new TableFrame(this,table);
				getDesktop().add(frame);
				frame.setVisible(true);
				}
			catch(Exception err)
				{
				err.printStackTrace();
				throw err;
				}
			finally
				{
				IOUtils.safeClose(r);
				}
		} catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		}
	
	
	public void doMenuLoadSQL()
		{
		SQLLoader loader= new SQLLoader();
		if(JOptionPane.showConfirmDialog(this, loader,"Select...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		if(loader.getSqlSource()==null || loader.getQuery().length()==0 || loader.getColumns().isEmpty()) return;
		SQLSource sqlSource= loader.getSqlSource();
		
		try {
			Table table= new Table();
			table.setColumns(loader.getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName(loader.getQuery().toString());
			
			Connection con= null;
			try
				{
				con= DriverManager.getConnection(sqlSource.getJdbcUri(), sqlSource.getlogin(), sqlSource.getPassword());
				Statement stmt= con.createStatement();
		
				ResultSet result= stmt.executeQuery(loader.getQuery());
				
				int nLine=0;
				while(result.next())
					{
					String tokens[]= new String[result.getMetaData().getColumnCount()];
					for(int i=0;i<tokens.length;++i)
						{
						tokens[i]= result.getString(i+1);
						if(tokens[i]==null) tokens[i]="";
						}
					Row row= new Row(tokens);
					table.getDatabase().put(
							nLine,
							row
							);
					nLine++;
					}

				table.setRowCount(nLine);
				
				TableFrame frame= new TableFrame(this,table);
				getDesktop().add(frame);
				frame.setVisible(true);
				}
			catch(Exception err)
				{
				err.printStackTrace();
				throw err;
				}
			finally
				{
				SQLUtilities.safeClose(con);
				}
		} catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		}
	
	public void reloadTableRefModel() 
		{
		try
			{
			SingleMapDatabase<Long, TableRef> db= Berkeley.getInstance().getTableRefDB();
			List<TableRef> items= new ArrayList<TableRef>();
			CloseableIterator<TableRef> r=db.listValues();
			while(r.hasNext())
				{
				items.add(r.next());
				}
			r.close();
			this.tableRefModel.clear();
			this.tableRefModel.addAll(items);
			}
		catch(DatabaseException err)
			{
			ThrowablePane.show(this, err);
			}
		}
	
	private DefaultListModel getTableFrameAsDefaultListModel()
		{
		DefaultListModel lm= new DefaultListModel();
		for(JInternalFrame f: getDesktop().getAllFrames())
			{
			if(!(f instanceof TableFrame)) continue;
			TableFrame tf=TableFrame.class.cast(f);
			lm.addElement(new NamedObject<TableFrame>(tf,"Id."+tf.getID()+" "+tf.getTableModel().getTable().getName()));
			}
		return lm;
		}
	
	
	private DefaultComboBoxModel getTableFrameAsDefaultComboModel()
		{
		DefaultComboBoxModel lm= new DefaultComboBoxModel();
		for(JInternalFrame f: getDesktop().getAllFrames())
			{
			if(!(f instanceof TableFrame)) continue;
			TableFrame tf=TableFrame.class.cast(f);
			lm.addElement(new NamedObject<TableFrame>(tf,"Id."+tf.getID()+" "+tf.getTableModel().getTable().getName()));
			}
		return lm;
		}
	
	public void doMenuConcat()
		{
		
		JList list = new JList(getTableFrameAsDefaultListModel());
		JPanel pane= new JPanel(new BorderLayout());
		pane.add(new JScrollPane(list));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		if(JOptionPane.showConfirmDialog(this, pane,"Select Table to be Concatened",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION)
			{
			return;
			}
		if(list.getSelectedValues().length==0) return;
		int colCount=-1;
		List<Table> tables= new ArrayList<Table>();
		for(Object o:list.getSelectedValues())
			{
			TableFrame tf=TableFrame.class.cast(NamedObject.class.cast(o).getObject());
			tables.add(tf.getTableModel().getTable());
			if(colCount==-1)
				{
				colCount=tf.getTableModel().getColumnCount();
				}
			else if(colCount!=tf.getTableModel().getColumnCount())
				{
				JOptionPane.showMessageDialog(this, "All tables should have the same number of columns","Error",JOptionPane.ERROR_MESSAGE,null);
				return;
				}
			}
		try {
			Table table= new Table();
			table.setColumns(tables.get(0).getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName("Concat "+tables.size()+" tables.");
			
			
			int nLine=0;
			for(Table src: tables)
				{
				for(int i=0;i< src.getRowCount();++i)
					{
					Row row=src.getDatabase().get(i);
					if(row==null) continue;
					table.getDatabase().put(nLine, row);
					nLine++;
					}
				}
			table.setRowCount(nLine);
			
			TableFrame frame= new TableFrame(this,table);
			getDesktop().add(frame);
			frame.setVisible(true);
			}
		catch(DatabaseException err)
			{
			ThrowablePane.show(this, err);
			}
			
		}
	
	public static void main(String[] args) {
		try {
			int optind=0;
		    while(optind<args.length)
				{
				if(args[optind].equals("-h"))
					{
					System.err.println("Pierre Lindenbaum PhD.");
					System.err.println("-h this screen");
					return;
					}
				 else if (args[optind].equals("--"))
				     {
				     ++optind;
				     break;
				     }
				else if (args[optind].startsWith("-"))
				     {
				     System.err.println("bad argument " + args[optind]);
				     System.exit(-1);
				     }
				else
				     {
				     break;
				     }
				++optind;
				}
		    Debug.setDebugging(true);
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Workbench f= new Workbench();
		SwingUtils.center(f,100,100);
	    SwingUtils.show(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
}

}
