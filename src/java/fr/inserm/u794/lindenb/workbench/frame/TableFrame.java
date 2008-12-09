package fr.inserm.u794.lindenb.workbench.frame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.lindenb.berkeley.BerkeleyUtils;
import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.io.PreferredDirectory;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.sql.SQLUtilities;
import org.lindenb.swing.layout.InputLayout;
import org.lindenb.util.NamedObject;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import fr.inserm.u794.lindenb.workbench.Berkeley;
import fr.inserm.u794.lindenb.workbench.SnpInfo;
import fr.inserm.u794.lindenb.workbench.Workbench;
import fr.inserm.u794.lindenb.workbench.sql.SQLSource;
import fr.inserm.u794.lindenb.workbench.table.Column;
import fr.inserm.u794.lindenb.workbench.table.ColumnSelector;
import fr.inserm.u794.lindenb.workbench.table.Indexes;
import fr.inserm.u794.lindenb.workbench.table.Row;
import fr.inserm.u794.lindenb.workbench.table.RowId;
import fr.inserm.u794.lindenb.workbench.table.Table;
import fr.inserm.u794.lindenb.workbench.table.TableModel;
import fr.inserm.u794.lindenb.workbench.table.TableRef;


public class TableFrame
	extends AbstractIFrame
	{
	private static final long serialVersionUID = 1L;
	private static final String ACTION_PERSIST="worbench.tableframe.persist";
	private static final String ACTION_EXPORT="worbench.tableframe.export";
	private static final String ACTION_SNPINFO="worbench.tableframe.snpinfo";
	private static final String ACTION_JOIN="worbench.tableframe.join";
	private static final String ACTION_JS_EXECUTE="worbench.tableframe.javacript.execute";
	private static final String ACTION_UNIQ="worbench.tableframe.uniq";
	private JTable table;
	private TableModel tableModel;
	private Table model;
	private JTextField informationField;
	
	public TableFrame(
			Workbench owner,
			Table model
			)
		{
		super(owner,model.getName());
		JToolBar toolbar= new JToolBar();
		super.contentPane.add(toolbar,BorderLayout.NORTH);
		JPanel bottom= new JPanel(new FlowLayout(FlowLayout.LEADING));
		super.contentPane.add(bottom,BorderLayout.SOUTH);
		bottom.add(this.informationField= new JTextField(50));
		this.informationField.setEditable(false);
		
		
		this.model=model;
		this.table= new JTable(this.tableModel= new TableModel(this.model));
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		super.contentPane.add(new JScrollPane(this.table));
		this.tableModel.addTableModelListener(new TableModelListener()
			{
			@Override
			public void tableChanged(TableModelEvent e) {
				updateInfoBox();
				}
			});
		
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateInfoBox();
				}
			});
		
		
		addInternalFrameListener(new InternalFrameAdapter()
			{
			@Override
				public void internalFrameOpened(InternalFrameEvent e) {
					updateInfoBox();
					}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent e)
				{
				try
					{
					TableFrame.this.tableModel.close();
					}
				catch(DatabaseException err)
					{
					System.err.println(err.getMessage());
					}
				}
			});
		AbstractAction action = new AbstractAction("Export...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuExport();
				}
			};
			
		super.actionMap.put(ACTION_EXPORT,action);
		
		action = new AbstractAction("Join Snp Info...")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuJoinSNPInfo();
				}
			};
		super.actionMap.put(ACTION_SNPINFO,action);
		
		action = new AbstractAction("Persists")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuPersists();
				}
			};
		super.actionMap.put(ACTION_PERSIST,action);
		
		
		action = new AbstractAction("Join Table...")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuJoin();
				}
			};
		super.actionMap.put(ACTION_JOIN,action);
		
		action = new AbstractAction("Filter by Javascript...")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuJavaScriptFilter();
				}
			};
		super.actionMap.put(ACTION_JS_EXECUTE,action);
			
			
		action = new AbstractAction("Uniq...")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuUniq();
				}
			};
		super.actionMap.put(ACTION_UNIQ,action);
		
		
		
		getJMenuBar().getMenu(0).add(super.actionMap.get(ACTION_EXPORT));
		getJMenuBar().getMenu(0).add(super.actionMap.get(ACTION_PERSIST));
		
		JMenu menu= new JMenu("Table");
		getJMenuBar().add(menu);
		menu.add(super.actionMap.get(ACTION_SNPINFO));
		menu.add(super.actionMap.get(ACTION_JOIN));
		menu.add(super.actionMap.get(ACTION_JS_EXECUTE));
		menu.add(super.actionMap.get(ACTION_UNIQ));
		
		toolbar.add(new JButton(super.actionMap.get(ACTION_EXPORT)));
		toolbar.add(new JButton(super.actionMap.get(ACTION_JOIN)));
		}
	
	
	protected Table getTable()
		{
		return this.model;
		}
	
	public TableModel getTableModel()
		{
		return this.tableModel;
		}
	
	private void updateInfoBox()
		{
		StringBuilder b= new StringBuilder();
		if(table.getSelectedRow()!=-1) b.append("Selected Row: "+table.getSelectedRow()+" ");
		 b.append(" Rows: "+table.getModel().getRowCount());
		 b.append(" Columns: "+table.getModel().getColumnCount());
		this.informationField.setText(b.toString());
		this.informationField.setCaretPosition(0);
		}
	
	private void doMenuExport()
		{
		JFileChooser fc= new JFileChooser(PreferredDirectory.getPreferredDirectory());
		if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return;
		File f= fc.getSelectedFile();
		if(f.exists() && JOptionPane.showConfirmDialog(this, "File "+f+" exists. Overwrite ?","Overwrite?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		try {
			PreferredDirectory.setPreferredDirectory(f);
			PrintStream out= null;
			if(f.getName().toLowerCase().endsWith(".gz"))
				{
				out= new PrintStream(new GZIPOutputStream(new FileOutputStream(f)));
				}
			else
				{
				out=new PrintStream(f);
				}
			char delim= '\t';
			if(f.getName().toLowerCase().endsWith(".csv.gz") || f.getName().toLowerCase().endsWith(".csv"))
				{
				delim=',';
				}
			for(int i=0;i< tableModel.getTable().getColumns().size();++i)
				{
				out.print(i==0?'#':delim);
				out.print(tableModel.getTable().getColumns().get(i).getLabel());
				}
			out.println();
			
			
			for(int i=0;i< tableModel.getTable().getRowCount();++i)
				{
				Row row= tableModel.getTable().getDatabase().get(i);
				if(row==null) continue;
				for(int j=0;j< row.size();++j)
					{
					if(j>0) out.print(delim);
					out.print(row.at(j));;
					}
				out.println();
				}
			out.flush();
			out.close();
			JOptionPane.showMessageDialog(this, "Done");
			}
		catch (Exception err)
			{
			ThrowablePane.show(this, err);
			return;
			}
		}
	
	private void doMenuJoinSNPInfo()
		{
		SnpInfo pane= new SnpInfo(TableFrame.this.model);
		while(true)
			{
			if(JOptionPane.showConfirmDialog(TableFrame.this, pane,"Select Row...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
			if(pane.getSQLSource()!=null && pane.getColumn()>=0) break;
			}
		int colIndex=pane.getColumn();
		SQLSource sqlSource= pane.getSQLSource();
		try {
			Class.forName(pane.getSQLSource().getDriver());
		} catch (ClassNotFoundException e) {
			ThrowablePane.show(getWorkbench(),"Cannot get SQL driver:"+sqlSource.getDriver(),e);
			}
		
		List<Column> columns= new ArrayList<Column>();
		columns.add(new Column( 0,"Chromosome"));
		columns.add(new Column( 1,"Start"));
		columns.add(new Column( 2,"End"));
		columns.add(new Column( 3,"Strand"));
		columns.add(new Column( 4,"Class"));
		columns.add(new Column( 5,"Validation"));
		columns.add(new Column( 6,"avHet"));
		columns.add(new Column( 7,"avHetSE"));
		columns.add(new Column( 8,"function"));
		columns.add(new Column( 9,"refNCBI"));
		columns.add(new Column(10,"refUCSC"));
		
		columns= Column.insert(tableModel.getTable().getColumns(), colIndex+1, columns);
		
		
		Connection con=null;
		try {
			final int NEW_COLUMNS_COUNT=11;
			Table copy= new Table();
			copy.setName("SnpInfo");
			copy.setColumns(columns);
			copy.setDatabase(Berkeley.getInstance().createTable());
			
			con=DriverManager.getConnection(sqlSource.getJdbcUri(), sqlSource.getlogin(),sqlSource.getPassword());
			PreparedStatement pstmt= con.prepareStatement(
				"select chrom,chromStart,chromEnd,strand,class,valid,avHet,avHetSE,func,refNCBI,refUCSC " +
				" from snp129 where name=?"
				);
			int nLine=0;
			String tokens[];
			for(int i=0;i< TableFrame.this.model.getRowCount();++i)
				{
				Row row= TableFrame.this.model.getDatabase().get(i);
				if(row==null) continue;
				pstmt.setString(1, row.at(colIndex));
				ResultSet sqlrow = pstmt.executeQuery();
				boolean found=false;
				while(sqlrow.next())
					{
					found=true;
					tokens= new String[ NEW_COLUMNS_COUNT];
					for(int j=0;j< NEW_COLUMNS_COUNT;++j)
						{
						tokens[j]= sqlrow.getString(j+1);
						if(tokens[j]==null)tokens[j]="";
						}
					Row newrow= row.insert(tokens, colIndex+1);
					copy.getDatabase().put(nLine, newrow);
					++nLine;
					}
				if(!found)
					{
					tokens= new String[ NEW_COLUMNS_COUNT];
					for(int j=0;j< NEW_COLUMNS_COUNT;++j) tokens[j]="";
					copy.getDatabase().put(nLine, row.insert(tokens, colIndex+1));
					++nLine;
					}
				}
			copy.setRowCount(nLine);
			pstmt.close();
			
			TableFrame frame= new TableFrame(getWorkbench(),copy);
			getWorkbench().getDesktop().add(frame);
			frame.setVisible(true);
		} catch (DatabaseException err) {
			ThrowablePane.show(TableFrame.this, err);
		} catch (SQLException err) {
			ThrowablePane.show(TableFrame.this, err);
			}
		finally
			{
			SQLUtilities.safeClose(con);
			}
		}
	
	
	private void doMenuJavaScriptFilter()
		{
		String program= JOptionPane.showInputDialog(this, " row[1].equals('chr2')");
		if(program==null) return;
		//String statements[]=null;
		try {
			//get a javascript engine
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine scriptEngine = sem.getEngineByName("js");
			//ScriptEngineFactory scriptEngineFactory= scriptEngine.getFactory();
			//String program = scriptEngineFactory.getProgram(statements);

			CompiledScript compiledScript=((Compilable) scriptEngine).compile(program);
			SimpleBindings bindings= new SimpleBindings();
		
			
			Table copy= new Table();
			copy.setName("JavaScript Filter");
			copy.setColumns(tableModel.getTable().getColumns());
			copy.setDatabase(Berkeley.getInstance().createTable());
			
			
			int nLine=0;
			for(int i=0;i< TableFrame.this.model.getRowCount();++i)
				{
				Row row= TableFrame.this.model.getDatabase().get(i);
				if(row==null) continue;
				
				bindings.put("row", row.toArray());
				bindings.put("rowIndex", i);
				//invoke the script with the current binding and get the result
				Object o= compiledScript.eval(bindings);
				if(o==null || !(o instanceof Boolean)) throw new ScriptException("Script should return a boolean");
				if(!Boolean.class.cast(o)) continue;
				copy.getDatabase().put(nLine, row);
				++nLine;
				}
			copy.setRowCount(nLine);
		
			
			TableFrame frame= new TableFrame(getWorkbench(),copy);
			getWorkbench().getDesktop().add(frame);
			frame.setVisible(true);
		} catch (DatabaseException err) {
			ThrowablePane.show(TableFrame.this, err);
			} 
		catch(ScriptException err)
			{
			ThrowablePane.show(TableFrame.this, err);
			}
		finally
			{
			}
		}
	
	private void doMenuUniq()
		{
		JPanel pane= new JPanel(new BorderLayout());
		ColumnSelector selector= new ColumnSelector(getTable().getColumns());
		pane.add(selector,BorderLayout.CENTER);
		JPanel bot= new JPanel(new FlowLayout(FlowLayout.LEADING));
		pane.add(bot,BorderLayout.SOUTH);
		JCheckBox cbCaseSensible= new JCheckBox("Case Sensible",true);
		bot.add(cbCaseSensible);
		JCheckBox cbCount= new JCheckBox("Count",false);
		bot.add(cbCount);
		JComboBox comboWay= new JComboBox(new String[]{"Uniq","Only duplicated","Only Uniq"});
		comboWay.setSelectedIndex(0);
		bot.add(comboWay);
		while(true)
			{
			if(JOptionPane.showConfirmDialog(this, pane,"Select columns",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
			if(!selector.getSelectedColumns().isEmpty()) break;
			}
		boolean casesensible=cbCaseSensible.isSelected();
		boolean appendcount=cbCount.isSelected();
		SingleMapDatabase<Row, Indexes> indexes=null;
		try {
			List<Column> columns=selector.getSelectedColumns();
			//build index
			indexes= Berkeley.getInstance().createIndex();
			
			for(int i=0;i< getTable().getRowCount();++i)
				{
				Row row= getTable().getDatabase().get(i);
				if(row==null) continue;
				Row sub= row.subRowFromColumns(columns);
				if(!casesensible) sub=sub.toLowerCase();
				Indexes idx= indexes.get(sub);
				if(idx==null) idx= new Indexes();
				idx.add(i);
				indexes.put(sub,idx);
				}
			
			Table copy= new Table();
			copy.setName("JavaScript Filter");
			List<Column> copycols= new ArrayList<Column>(tableModel.getTable().getColumns());
			if(appendcount)
				{
				copycols.add(new Column(copycols.size(),"Count"));
				}
				
			copy.setColumns(copycols);
			copy.setDatabase(Berkeley.getInstance().createTable());
			
			int combochoice= comboWay.getSelectedIndex();
			Cursor c=indexes.cursor();
			DatabaseEntry k= new DatabaseEntry();
			DatabaseEntry v= new DatabaseEntry();
			int nLine=0;
			while(c.getNext(k, v, null)==OperationStatus.SUCCESS)
				{
				Indexes idx= Indexes.BINDING.entryToObject(v);
				if(!(combochoice==0 ||
				   (combochoice==1 && idx.size()>1) ||
				   (combochoice==2 && idx.size()==1)
				   )) continue;
				
				Row row=  getTable().getDatabase().get(idx.first());
				if(appendcount) row=row.add(String.valueOf(idx.size()));
				copy.getDatabase().put(nLine, row);
				++nLine;
				}
			c.close();
			copy.setRowCount(nLine);
			
			indexes.close();
			TableFrame frame= new TableFrame(getWorkbench(),copy);
			getWorkbench().getDesktop().add(frame);
			frame.setVisible(true);
			}
		catch (Exception e) {
			ThrowablePane.show(this, e);
			}
		finally
			{
			if(indexes!=null) try { indexes.close();} catch(Throwable err) {}
			}
		}
	
	@SuppressWarnings("unchecked")
	private void doMenuJoin()
		{
		JoinPane joinPane= new JoinPane();
		while(true)
			{
			if(JOptionPane.showConfirmDialog(this, joinPane,"Select Tables...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION)return;
			if( joinPane.getOtherFrame()==null ||
				joinPane.left.getSelectedColumns().isEmpty() ||
				joinPane.left.getSelectedColumns().size()!=joinPane.right.getSelectedColumns().size())
				{
				continue;
				}
			break;
			}
		SingleMapDatabase<Row, Indexes> indexes[]=new SingleMapDatabase[2];
		boolean casesensible= joinPane.caseSensible.isSelected();
		boolean showjoin= joinPane.showJoin.isSelected();
		boolean showleftalone= joinPane.showMissingLeft.isSelected();
		boolean showrightalone= joinPane.showMissingRight.isSelected();
		TableFrame otherFrame= joinPane.getOtherFrame();
		List<Column> joinedColumns= new ArrayList<Column>();
		try {
			for(int side=0;side<2;++side)
				{
				List<Column> columns= (side==0 ?joinPane.left:joinPane.right).getSelectedColumns();
				
				
				//build index
				indexes[side]= Berkeley.getInstance().createIndex();
				TableFrame tFrame=(side==0?this:otherFrame);
				
				for(Column c: tFrame.getTable().getColumns())
					{
					joinedColumns.add(new Column(joinedColumns.size(),(side==0?"L-":"R-")+c.getLabel()));
					}
				
				for(int i=0;i< tFrame.getTable().getRowCount();++i)
					{
					Row row= tFrame.getTable().getDatabase().get(i);
					if(row==null) continue;
					Row sub= row.subRowFromColumns(columns);
					if(!casesensible) sub=sub.toLowerCase();
					Indexes idx= indexes[side].get(sub);
					if(idx==null) idx= new Indexes();
					idx.add(i);
					indexes[side].put(sub,idx);
					}
				}
			
			
			Table copy= new Table();
			copy.setName("Join");
			copy.setColumns(joinedColumns);
			copy.setDatabase(Berkeley.getInstance().createTable());
			
			
			
			
		
			Cursor c=indexes[0].cursor();
			DatabaseEntry kL= new DatabaseEntry();
			DatabaseEntry vL= new DatabaseEntry();
			int nLine=0;
			while(c.getNext(kL, vL, null)==OperationStatus.SUCCESS)
				{
				Indexes idxL= Indexes.BINDING.entryToObject(vL);
				Row keyL= Row.BINDING.entryToObject(kL);
				Indexes idxR= indexes[1].get(keyL);
				if(idxR==null && showleftalone)//no join
					{
					for(int rowNumber: idxL)
						{
						Row row= getTable().getDatabase().get(rowNumber);
						while(row.size()< copy.getColumns().size()) row=row.add("");
						copy.getDatabase().put(nLine, row);
						++nLine;
						}
					}
				else if(idxR!=null && showjoin)
					{
					for(int rowNumberL: idxL)
						{
						Row rowL= this.getTable().getDatabase().get(rowNumberL);
						for(int rowNumberR: idxR)
							{
							Row rowR= otherFrame.getTable().getDatabase().get(rowNumberR);
							
							Row rowjoined =rowL.insert(rowR.toArray(), rowL.size());
							copy.getDatabase().put(nLine, rowjoined);
							++nLine;
							}
						}
					}
				}
			c.close();
			
			
			c=indexes[1].cursor();
			DatabaseEntry kR= new DatabaseEntry();
			DatabaseEntry vR= new DatabaseEntry();
			while(showrightalone && c.getNext(kR, vR, null)==OperationStatus.SUCCESS)
				{
				Indexes idxR= Indexes.BINDING.entryToObject(vR);
				Row keyR= Row.BINDING.entryToObject(kR);
				Indexes idxL= indexes[0].get(keyR);
				if(idxL!=null) continue;
				
				for(int rowNumber: idxR)
					{
					Row row1= new Row(new String[0]);
					while(row1.size()< this.getTable().getColumns().size()) row1=row1.add("");
					Row row= otherFrame.getTable().getDatabase().get(rowNumber);
					row1= row1.insert(row.toArray(), row1.size());
					copy.getDatabase().put(nLine, row1);
					++nLine;
					}
				}
			c.close();
			
			
			copy.setRowCount(nLine);
			
			indexes[0].close();
			indexes[1].close();
			
			TableFrame frame= new TableFrame(getWorkbench(),copy);
			getWorkbench().getDesktop().add(frame);
			frame.setVisible(true);
			}
		catch (Exception e) {
			ThrowablePane.show(this, e);
			}
		finally
			{
			
			}
		}
	
	
	private void doMenuPersists()
		{
		JPanel pane= new JPanel(new BorderLayout());
		JPanel grid= new JPanel(new InputLayout());
		pane.add(grid,BorderLayout.CENTER);
		grid.add(new JLabel("Name:",JLabel.RIGHT));
		JTextField tfName= new JTextField(getTable().getName(),30);
		grid.add(tfName);
		grid.add(new JLabel("Desc:",JLabel.RIGHT));
		JTextField tfDesc= new JTextField(getTable().getName(),30);
		grid.add(tfDesc);
		grid.add(new JLabel("Keywords:",JLabel.RIGHT));
		JTextField tfTags= new JTextField("",30);
		grid.add(tfTags);
		if(JOptionPane.showConfirmDialog(this, pane,"Save...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION)
			{
			return;
			}
		Transaction txn= null;
		try {
			txn=Berkeley.getInstance().getEnvironment().beginTransaction(null,null);
			SingleMapDatabase<Long, TableRef> dbref =Berkeley.getInstance().getTableRefDB();
			Long id = System.currentTimeMillis();
			/* find db new id */
			while(dbref.get(txn,id)!=null) { id++;}
			
			/* create new ref */
			TableRef tref= new TableRef();
			tref.setId(id);
			tref.setName(tfName.getText());
			tref.setDescription(tfDesc.getText());
			tref.setCreation(new Date());
			tref.setRowCount(getTable().getRowCount());
			tref.setColumns(getTable().getColumns());
			Set<String> set= new HashSet<String>();
			for(String tag: tfTags.getText().split("[ \n\t,;]+"))
				{
				if(tag.length()==0) continue;
				set.add(tag);
				}
			tref.setTags(set);
			
			/* save rows */
			DatabaseConfig dbConfig= new DatabaseConfig();
			dbConfig.setTransactional(true);
			dbConfig.setAllowCreate(true);
			dbConfig.setExclusiveCreate(false);
			dbConfig.setReadOnly(false);
			Database dbrows= Berkeley.getInstance().getEnvironment().openDatabase(txn,Berkeley.DB_STORED_TABLE_NAME, dbConfig);
			int nLine=0;
			for(int i=0;i< getTable().getRowCount();++i)
				{
				RowId rid= new RowId(id,i);
				Row row = this.getTable().getDatabase().get(i);
				if(dbrows.put(txn, rid.toEntry(), row.toDatabaseEntry())!=OperationStatus.SUCCESS)
					{
					throw new DatabaseException("Cannot insert row at "+i+" "+row);
					}
				nLine++;
				}
			dbrows.close();
			
			/** save ref */
			tref.setRowCount(nLine);
			dbref.put(txn,id, tref);
			
			txn.commit();
			}
		catch (DatabaseException err)
			{
			BerkeleyUtils.safeAbort(txn);
			ThrowablePane.show(this, err);
			}
		finally
			{
			txn=null;
			getWorkbench().reloadTableRefModel();
			}
		}
	
	private class JoinPane
		extends JPanel
		{
		private static final long serialVersionUID = 1L;
		private ColumnSelector left;
		private ColumnSelector right;
		private JComboBox iFrameCombo;
		private JCheckBox caseSensible;
		private JCheckBox showJoin;
		private JCheckBox showMissingLeft;
		private JCheckBox showMissingRight;
		JoinPane()
			{
			super(new BorderLayout());
			JPanel top= new JPanel(new FlowLayout(FlowLayout.LEADING));
			this.add(top,BorderLayout.NORTH);
			top.add(new JLabel("Table:",JLabel.RIGHT));
			Vector<NamedObject<TableFrame>> files= new Vector<NamedObject<TableFrame>>();
			for(JInternalFrame iframe: getWorkbench().getDesktop().getAllFrames())
				{
				if(!(iframe instanceof TableFrame)) continue;
				TableFrame tf=TableFrame.class.cast(iframe);
				files.add(new NamedObject<TableFrame>(tf,"ID. "+tf.getID()+" "+tf.tableModel.getTable().getName()));
				}
			
			top.add(iFrameCombo=new JComboBox(files));
			iFrameCombo.setSelectedIndex(-1);
			
			JPanel grid= new JPanel(new GridLayout(0,1,10,2));
			this.add(grid,BorderLayout.CENTER);
			this.left= new ColumnSelector();
			grid.add(left);
			this.left.setSource(tableModel.getTable().getColumns());
			this.right= new ColumnSelector();
			grid.add(right);
			
			JPanel bot= new JPanel(new FlowLayout(FlowLayout.LEADING));
			this.add(bot,BorderLayout.SOUTH);
			this.caseSensible= new JCheckBox("Case Sensible",true);
			bot.add(this.caseSensible);
			bot.add(this.showJoin=new JCheckBox("Show Join"));
			bot.add(this.showMissingLeft=new JCheckBox("Show Missing Left"));
			bot.add(this.showMissingRight=new JCheckBox("Show Missing Right"));
			this.iFrameCombo.addActionListener(new AbstractAction()
				{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
					{
					TableFrame obj=getOtherFrame();
					if(obj==null) 
						{
						right.setSource(new ArrayList<Column>());
						}
					else
						{
						right.setSource(obj.tableModel.getTable().getColumns());
						}
					}
				});
			
			}
		@SuppressWarnings("unchecked")
		public TableFrame getOtherFrame()
			{
			return ((NamedObject<TableFrame>)iFrameCombo.getSelectedItem()).getObject();
			}
		}
	
	
	}
