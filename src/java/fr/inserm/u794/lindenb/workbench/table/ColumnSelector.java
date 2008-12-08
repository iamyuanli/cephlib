package fr.inserm.u794.lindenb.workbench.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ColumnSelector extends JPanel
{
private static final long serialVersionUID = 1L;
private ColumnModel left;
private ColumnModel right;
private JTable leftTable;
private JTable rightTable;
private AbstractAction addAction;
private AbstractAction removeAction;
public ColumnSelector(List<Column> columns)
	{
	this();
	setSource(columns);
	}

public ColumnSelector()
		{
		super(new GridLayout(1,0,5,5));
		this.left= new ColumnModel();
		this.right= new ColumnModel();
		
		JPanel pane= new JPanel(new BorderLayout());
		this.add(pane);
		JScrollPane scroll;
		pane.add(scroll=new JScrollPane(this.leftTable=new JTable(this.left)),BorderLayout.CENTER);
		scroll.setPreferredSize(new Dimension(300,200));
		this.leftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addAction= new AbstractAction("Add")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int i= leftTable.getSelectedRow(); if(i==-1) return;
				Column c= left.elementAt(i);
				if(!right.contains(c)) right.addElement(c);
				}
			};
		this.addAction.setEnabled(false);
		pane.add(new JButton(this.addAction),BorderLayout.SOUTH);
		leftTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				addAction.setEnabled(leftTable.getSelectedRow()!=-1);	
				}
			});
		
		
		pane= new JPanel(new BorderLayout());
		this.add(pane);
		pane.add(scroll=new JScrollPane(this.rightTable=new JTable(this.right)),BorderLayout.CENTER);
		scroll.setPreferredSize(new Dimension(300,200));
		this.rightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.removeAction= new AbstractAction("Remove")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int i= rightTable.getSelectedRow(); if(i==-1) return;
				right.removeElementAt(i);
				}
			};
		pane.add(new JButton(this.removeAction),BorderLayout.SOUTH);
		this.removeAction.setEnabled(false);
		rightTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeAction.setEnabled(leftTable.getSelectedRow()!=-1);	
				}
			});
		}
public void setSource(List<Column> columns)
	{
	this.left.clear();
	this.right.clear();
	this.left.addAll(columns);
	}

public List<Column> getSelectedColumns()
	{
	return this.right.elements();
	}

public static void main(String[] args) {
	ColumnSelector sel=new ColumnSelector();
	List<Column> list= new ArrayList<Column>();
	for(int i=0;i< 100;++i) list.add(new Column(i,"A"+i));
	
	sel.setSource(list);
	JOptionPane.showMessageDialog(null, sel);
	}

}
