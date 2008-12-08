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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import org.lindenb.io.IOUtils;
import org.lindenb.io.PreferredDirectory;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.FileExtensionFilter;
import org.lindenb.swing.ObjectAction;
import org.lindenb.util.C;

import fr.inserm.u794.lindenb.workbench.table.Column;



public class FileLoader extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport changeSupport =new PropertyChangeSupport(this);
	private JTable table;
	private JTextField txFilePath;
	private File inputFile;
	private DefaultTableModel tableModel;
	private JCheckBox firstLineIsHeader;
	private Pattern pattern=Pattern.compile("[\t]");
	
	public FileLoader()
		{
		super(new BorderLayout());
		this.setBorder(new TitledBorder("File Loader"));
	
		JPanel top= new JPanel(new GridLayout(0,1,5,5));
		this.add(top,BorderLayout.NORTH);
		
		JPanel pane=new JPanel(new FlowLayout(FlowLayout.LEADING));	
		top.add(pane);
		pane.add(new JLabel("File:",JLabel.RIGHT));
		pane.add(this.txFilePath= new JTextField(30));
		this.txFilePath.setEditable(false);
		pane.add(new JButton(new AbstractAction("Choose")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				doMenuChoose();
				}
			}));
		
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
					FileLoader.this.pattern=pat;
					reloadTable();
				} catch (PatternSyntaxException e2) {
					ThrowablePane.show(FileLoader.this, e2);
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
		
		this.changeSupport.addPropertyChangeListener("file", new PropertyChangeListener()
			{
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				txFilePath.setText(inputFile.getPath());
				txFilePath.setCaretPosition(0);
				reloadTable();
				}
			});
		}
	
	private void doMenuChoose()
		{
		JFileChooser chooser= new JFileChooser(PreferredDirectory.getPreferredDirectory());
		chooser.setFileFilter(new FileExtensionFilter("Text Files",".txt",".csv",".txt.gz",".csv.gz"));
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
		this.inputFile = chooser.getSelectedFile();
		PreferredDirectory.setPreferredDirectory(this.inputFile );
		this.changeSupport.firePropertyChange("file", "", this.inputFile);
		}
	
	public File getSelectedFile()
		{
		return this.inputFile;
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
		
		if(this.inputFile==null)
			{
			return;
			}
		else
			{
			BufferedReader r= null;
			try
				{
			
				r=IOUtils.openFile(this.inputFile);
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
				ThrowablePane.show(FileLoader.this, e);
				}
			finally
				{
				IOUtils.safeClose(r);
				}
			}
		}
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, new FileLoader());
		}
	}
