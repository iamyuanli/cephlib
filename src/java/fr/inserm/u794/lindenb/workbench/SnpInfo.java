package fr.inserm.u794.lindenb.workbench;

import java.awt.BorderLayout;
import java.awt.FlowLayout;


import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;


import fr.inserm.u794.lindenb.workbench.sql.SQLSource;
import fr.inserm.u794.lindenb.workbench.table.ColumnModel;
import fr.inserm.u794.lindenb.workbench.table.Table;

public class SnpInfo extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private ColumnModel model= new  ColumnModel();
	private JTable jtable;
	private SQLSource sqlSources[]=new SQLSource[]{SQLSource.UCSC_HG18,SQLSource.LOCALHOST_HG18};
	private JComboBox comboSQL;
	public SnpInfo(Table table)
		{
		super(new BorderLayout());
		setBorder(new TitledBorder("Select the column of the SNP"));
		this.model.addAll(table.getColumns());
		this.jtable = new JTable(this.model);
		this.jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel top=new JPanel(new FlowLayout(FlowLayout.LEADING));
		this.add(top,BorderLayout.NORTH);
		top.add(new JLabel("Data Source:",JLabel.RIGHT));
		
		top.add(this.comboSQL=new JComboBox(this.sqlSources));
		this.comboSQL.setSelectedIndex(0);
		add(new JScrollPane(this.jtable),BorderLayout.CENTER);
		}
	
	public SQLSource getSQLSource()
		{
		return SQLSource.class.cast(this.comboSQL.getSelectedItem());
		}
		
	public 	int getColumn()
		{
		return jtable.getSelectedRow();
		}
		
	}
