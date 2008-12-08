/**
 * 
 */
package fr.inserm.u794.lindenb.workbench.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.util.iterator.CloseableIterator;

import fr.inserm.u794.lindenb.workbench.Berkeley;
import fr.inserm.u794.lindenb.workbench.Workbench;
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
	
	public RefFrame(Workbench owner,TableRef.Model tableRefModel)
		{
		super(owner,"Saved Tables");
		this.setClosable(false);
		this.tableRefModel= new TableRef.Model();
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
		
		JToolBar bar= new JToolBar();
		super.contentPane.add(bar,BorderLayout.NORTH);
		bar.add(new JButton(super.actionMap.get(ACTION_RELOAD)));
		bar.add(new JButton(super.actionMap.get(ACTION_OPEN)));
		
		
		
		super.contentPane.add(new JScrollPane(this.table),BorderLayout.CENTER);
		
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e)
				{
				actionMap.get(ACTION_OPEN).setEnabled(table.getSelectedRow()!=-1);
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
	
	public void openContent()
		{
		int i= table.getSelectedRow();
		if(i==-1) return;
		TableRef t= RefFrame.this.tableRefModel.elementAt(i);
		}
	
	public void reloadContent()
		{
		try {
			SingleMapDatabase<Long, TableRef> db=Berkeley.getInstance().getTableRefDB();
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
		catch (Exception e)
			{
			ThrowablePane.show(getWorkbench(), e);
			}
		}
	}
