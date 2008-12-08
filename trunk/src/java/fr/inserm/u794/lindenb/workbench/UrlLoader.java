package fr.inserm.u794.lindenb.workbench;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import org.lindenb.io.IOUtils;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.ObjectAction;
import org.lindenb.util.C;
import org.lindenb.util.Cast;

import fr.inserm.u794.lindenb.workbench.table.Column;



public class UrlLoader extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport changeSupport =new PropertyChangeSupport(this);
	private JTable table;
	private JTextField txURLAdress;
	private URL inputURL;
	private DefaultTableModel tableModel;
	private JCheckBox firstLineIsHeader;
	private Pattern pattern=Pattern.compile("[\t]");
	
	public UrlLoader()
		{
		super(new BorderLayout());
		this.setBorder(new TitledBorder("URL Loader"));
	
		JPanel top= new JPanel(new GridLayout(0,1,5,5));
		this.add(top,BorderLayout.NORTH);
		
		JPanel pane=new JPanel(new FlowLayout(FlowLayout.LEADING));	
		top.add(pane);
		pane.add(new JLabel("URL:",JLabel.RIGHT));
		pane.add(this.txURLAdress= new JTextField(30));
		AbstractAction chooseURL=new AbstractAction("Choose")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				if(!Cast.URL.isA(txURLAdress.getText()))
					{
					inputURL= null;
					}
				else
					{
					inputURL=Cast.URL.cast(txURLAdress.getText());
					}
				reloadTable();
				}
			};
		this.txURLAdress.addActionListener(chooseURL);
		pane.add(new JButton(chooseURL));
		
		pane=new JPanel(new FlowLayout(FlowLayout.LEADING));	
		top.add(pane);
		pane.add(new JLabel("Delimiter:",JLabel.RIGHT));
		JTextField txPattern=new JTextField(C.escape("[\t]"),20);
		pane.add(txPattern);
		Action action=new ObjectAction<JTextField>(txPattern,"Set")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				Pattern pat;
				try {
					pat= Pattern.compile(getObject().getText());
					UrlLoader.this.pattern=pat;
					reloadTable();
				} catch (PatternSyntaxException e2) {
					ThrowablePane.show(UrlLoader.this, e2);
					}
				}
			};
		txPattern.addActionListener(action);
		pane.add(new JButton(action));
		
		pane=new JPanel(new FlowLayout(FlowLayout.LEADING));	
		top.add(pane);
		pane.add(new JLabel("Delimiter:",JLabel.RIGHT));
		pane.add(firstLineIsHeader=new JCheckBox("First Column contains the Header",true));
		firstLineIsHeader.addActionListener(new ActionListener()
			{
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadTable();
				}
			});
		
		this.table= new JTable(this.tableModel=new DefaultTableModel());
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add(new JScrollPane(this.table),BorderLayout.CENTER);
		
		this.changeSupport.addPropertyChangeListener("url", new PropertyChangeListener()
			{
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				txURLAdress.setText(inputURL.getPath());
				txURLAdress.setCaretPosition(0);
				reloadTable();
				}
			});
		}
	
	
	public URL getSelectedURL()
		{
		return this.inputURL;
		}
	
	
	public Pattern getDelimiter()
		{
		return this.pattern;
		}
	
	public boolean isFirstLineHeader()
		{
		return this.firstLineIsHeader.isSelected();
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
	
	

	private void reloadTable()
		{
		this.tableModel.setRowCount(0);
		this.tableModel.setColumnCount(0);
		
		if(this.inputURL==null)
			{
			return;
			}
		else
			{
			BufferedReader r= null;
			try
				{
				InputStream in= this.inputURL.openStream();
				if(this.inputURL.getPath().toLowerCase().endsWith(".gz"))
					{
					in= new GZIPInputStream(in);
					}
				
				r= new BufferedReader(new InputStreamReader(in));
				String line=null;
				String header[]=null;
				while((line=r.readLine())!=null && this.tableModel.getRowCount()<20)
					{
					String tokens[]=pattern.split(line);
					if(header==null)
						{
						if(firstLineIsHeader.isSelected())
							{
							header=tokens;
							this.tableModel.setColumnIdentifiers(header);
							continue;
							}
						else
							{
							header=new String[tokens.length];
							for(int i=0;i< tokens.length;++i)
								{
								header[i]=String.valueOf(i+1);
								}
							this.tableModel.setColumnIdentifiers(header);
							}
						}
					this.tableModel.addRow(tokens);
					}
				}
			catch (IOException e) {
				ThrowablePane.show(UrlLoader.this, e);
				this.inputURL=null;
				}
			finally
				{
				IOUtils.safeClose(r);
				}
			}
		}
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, new UrlLoader());
		}
	}
