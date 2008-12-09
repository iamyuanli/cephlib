package fr.inserm.u794.lindenb.workbench;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import org.lindenb.io.IOUtils;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.sql.SQLUtilities;
import org.lindenb.swing.ObjectAction;
import org.lindenb.swing.layout.InputLayout;

import fr.cephb.lindenb.sql.MySQLConstants;
import fr.inserm.u794.lindenb.workbench.sql.SQLSource;
import fr.inserm.u794.lindenb.workbench.table.Column;



public class SQLLoader extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JComboBox  cbDriver;
	private JTextField txJdbcUri;
	private JTextField txLogin;
	private JPasswordField txPassword;
	private SQLSource sqlSource=null;
	private JTextArea txQuery;
	private DefaultTableModel tableModel;

	
	public SQLLoader()
		{
		super(new BorderLayout());
		this.setBorder(new TitledBorder("SQL Loader"));
	
		JPanel top= new JPanel(new GridLayout(1,0,5,5));
		this.add(top,BorderLayout.NORTH);
		
		JPanel pane=new JPanel(new InputLayout());
		pane.setBorder(new TitledBorder("SQL Parameters"));
		top.add(pane);
		JComboBox sources=new JComboBox(new SQLSource[]{null,SQLSource.LOCALHOST_HG18,SQLSource.UCSC_HG18});
		pane.add(new JLabel("Connection",JLabel.RIGHT));
		pane.add(sources);
		
		pane.add(new JLabel("JDBC URI",JLabel.RIGHT));
		pane.add(this.cbDriver= new JComboBox(new String[]{MySQLConstants.DRIVER}));
		this.cbDriver.setSelectedIndex(0);
		pane.add(new JLabel("JDBC URI",JLabel.RIGHT));
		pane.add(this.txJdbcUri= new JTextField(30));
		pane.add(new JLabel("Login",JLabel.RIGHT));
		pane.add(this.txLogin= new JTextField(30));
		pane.add(new JLabel("Password",JLabel.RIGHT));
		pane.add(this.txPassword= new JPasswordField(30));
		
		sources.addActionListener(new ObjectAction<JComboBox>(sources)
				{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					SQLSource src=(SQLSource)getObject().getSelectedItem();
					if(src==null) return;
					cbDriver.setSelectedItem(src.getDriver());
					txJdbcUri.setText(src.getJdbcUri());
					txLogin.setText(src.getlogin());
					txPassword.setText(src.getPassword());
					}
				});
		
		AbstractAction action =new AbstractAction("Test")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				reloadTable();
				}
			};
		pane.add(new JLabel());
		pane.add(new JButton(action));
			
		pane= new JPanel(new BorderLayout());
		pane.setBorder(new TitledBorder("Query"));
		top.add(pane);
		pane.add(new JScrollPane(this.txQuery=new JTextArea(10,30)));
		
		

		
		this.table= new JTable(this.tableModel=new DefaultTableModel());
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add(new JScrollPane(this.table),BorderLayout.CENTER);
		
		}
	
	private SQLSource createSQLSource()
		{
		return new SQLSource()
			{
			@Override
			public String getDriver() {
				return String.valueOf(cbDriver.getSelectedItem());
				}
			@Override
			public String getJdbcUri() {
				return txJdbcUri.getText().trim();
				}
			
			@Override
			public String getLabel() {
					return "My Connection";
					}
			@Override
			public String getlogin() {
				return txLogin.getText().trim();
				}
			@Override
				public String getPassword() {
					return new String(txPassword.getPassword()).trim();
					}
			
			};
		}
	
	
	public List<Column> getColumns()
		{
		List<Column> cols= new ArrayList<Column>(tableModel.getColumnCount());
		for(int i=0;i< tableModel.getColumnCount();++i)
			{
			cols.add(new Column(i,this.tableModel.getColumnName(i)));
			}
		return cols;
		}
	
	public String getQuery()
		{
		return this.txQuery.getText().trim();
		}
	

	public SQLSource getSqlSource() {
		return sqlSource;
		}
	
	private void reloadTable()
		{
		this.tableModel.setRowCount(0);
		this.tableModel.setColumnCount(0);
		if(getQuery().length()==0) return;
		SQLSource sqlSource= createSQLSource();
		this.sqlSource=null;
		try {
			Class.forName(sqlSource.getDriver());
		} catch (Exception e) {
			ThrowablePane.show(this, e);
			}
		
		if(this.txQuery.getText().trim().length()==0)
			{
			return;
			}
		else
			{
			Connection con= null;
			try
				{
				con= DriverManager.getConnection(sqlSource.getJdbcUri(), sqlSource.getlogin(), sqlSource.getPassword());
				Statement stmt= con.createStatement();
				stmt.setMaxRows(20);
				ResultSet row= stmt.executeQuery(getQuery());
				this.tableModel.setColumnCount(row.getMetaData().getColumnCount());
				String tokens[]= new String[row.getMetaData().getColumnCount()];
				for(int i=0;i<tokens.length;++i)
					{
					tokens[i]= row.getMetaData().getColumnLabel(i+1);
					if(tokens[i]==null) tokens[i]=String.valueOf(1+i);
					}
				this.tableModel.setColumnIdentifiers(tokens);
				
				while(row.next())
					{
					tokens= new String[row.getMetaData().getColumnCount()];
					for(int i=0;i<tokens.length;++i)
						{
						tokens[i]= row.getString(i+1);
						if(tokens[i]==null) tokens[i]="";
						}
					this.tableModel.addRow(tokens);
					}
				this.sqlSource= sqlSource;
				}
			catch (SQLException e) {
				ThrowablePane.show(SQLLoader.this, e);
				}
			finally
				{
				SQLUtilities.safeClose(con);
				}
			}
		}
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, new SQLLoader());
		}
	}
