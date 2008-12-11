/**
 * 
 */
package fr.inserm.u794.lindenb.workbench.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lindenb.berkeley.BerkeleyUtils;
import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.lang.ThrowablePane;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import fr.inserm.u794.lindenb.workbench.Berkeley;
import fr.inserm.u794.lindenb.workbench.Workbench;
import fr.inserm.u794.lindenb.workbench.table.Row;
import fr.inserm.u794.lindenb.workbench.table.RowId;
import fr.inserm.u794.lindenb.workbench.table.Table;
import fr.inserm.u794.lindenb.workbench.table.TableRef;

/**
 * @author pierre
 *
 */
public class RefFrame extends AbstractIFrame
	{
	private static final long serialVersionUID = 1L;
	private JTable table;
	private TableRef.Model tableRefModel;
	private static final String ACTION_RELOAD="ref.frame.reload";
	private static final String ACTION_OPEN="ref.frame.open";
	private static final String ACTION_DELETE="ref.frame.delete";
	
	public RefFrame(Workbench owner,TableRef.Model tableRefModel)
		{
		super(owner,"Saved Tables");
		this.setClosable(false);
		this.tableRefModel= tableRefModel;
		this.table= new JTable(tableRefModel);
		
		AbstractAction action= new AbstractAction("Reload")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent ae)
				{
				reloadContent();
				}
			};
		super.actionMap.put(ACTION_RELOAD, action);
		
		action= new AbstractAction("Open")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent ae)
				{
				openContent();
				}
			};
		action.setEnabled(false);
		super.actionMap.put(ACTION_OPEN, action);
		
		
		action= new AbstractAction("Remove")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent ae)
				{
				removeContent();
				}
			};
		action.setEnabled(false);
		super.actionMap.put(ACTION_DELETE, action);
		
		JToolBar bar= new JToolBar();
		super.contentPane.add(bar,BorderLayout.NORTH);
		bar.add(new JButton(super.actionMap.get(ACTION_RELOAD)));
		bar.add(new JButton(super.actionMap.get(ACTION_OPEN)));
		bar.add(new JButton(super.actionMap.get(ACTION_DELETE)));
		
		
		
		super.contentPane.add(new JScrollPane(this.table),BorderLayout.CENTER);
		
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e)
				{
				actionMap.get(ACTION_OPEN).setEnabled(table.getSelectedRow()!=-1);
				actionMap.get(ACTION_DELETE).setEnabled(table.getSelectedRow()!=-1);
				}
			});
		
		this.table.addMouseListener(new MouseAdapter()
			{
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()<2) return;
				openContent();
				}
			});
		
		addInternalFrameListener(new InternalFrameAdapter()
			{
			@Override
			public void internalFrameOpened(InternalFrameEvent arg0) {
				reloadContent();
				}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				reloadContent();
				}
			});
		}
	
	
	public void removeContent()
		{
		int i= table.getSelectedRow();
		if(i==-1) return;
		TableRef t= this.tableRefModel.elementAt(i);
		if(t==null) return;
		long id= t.getId();
		Database db=null;
		Transaction txn=null;
		try {
			txn= Berkeley.getInstance().getEnvironment().beginTransaction(null, null);
			DatabaseConfig dbConfig= new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setExclusiveCreate(false);
			dbConfig.setReadOnly(false);
			dbConfig.setTransactional(true);
			db= Berkeley.getInstance().getEnvironment().openDatabase(txn,Berkeley.DB_STORED_TABLE_NAME, dbConfig);
			for( i=0;i< t.getRowCount();++i)
				{
				db.delete(txn, new RowId(id,i).toEntry());
				}
			
			Berkeley.getInstance().getTableRefDB().remove(txn,id);
			
			txn.commit();
			} 
		catch (Exception e)
			{
			BerkeleyUtils.safeAbort(txn);
			ThrowablePane.show(this, e);
			}
		finally
			{
			BerkeleyUtils.safeClose(db);
			getWorkbench().reloadTableRefModel();
			}
		}
	
	public void openContent()
		{
		int i= table.getSelectedRow();
		if(i==-1) return;
		TableRef t= this.tableRefModel.elementAt(i);
		if(t==null) return;
		long id= t.getId();
		Database db=null;
		try {
			SingleMapDatabase<Integer, Row> storage=Berkeley.getInstance().createTable();
			DatabaseEntry data= new DatabaseEntry();
			
			DatabaseConfig dbConfig= new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setExclusiveCreate(false);
			dbConfig.setReadOnly(false);
			db= Berkeley.getInstance().getEnvironment().openDatabase(null,Berkeley.DB_STORED_TABLE_NAME, dbConfig);
			int nLine=0;
			for( i=0;i< t.getRowCount();++i)
				{
				RowId rid= new RowId(id,i);
				if(db.get(null, rid.toEntry(), data, null)!=OperationStatus.SUCCESS)
					{
					continue;
					}
				Row row= Row.BINDING.entryToObject(data);
				storage.put(nLine, row);
				nLine++;
				}
			Table copy= new Table(t);
			copy.setRowCount(nLine);
			copy.setDatabase(storage);
			TableFrame frame= new TableFrame(getWorkbench(),copy);
			getWorkbench().getDesktop().add(frame);
			frame.setVisible(true);
			} 
		catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		finally
			{
			BerkeleyUtils.safeClose(db);
			}
		}
	
	public void reloadContent()
		{
		getWorkbench().reloadTableRefModel();
		}
	
	
	@Override
	public void doMenuClose() {
		
		super.doMenuClose();
		}
	
	}
