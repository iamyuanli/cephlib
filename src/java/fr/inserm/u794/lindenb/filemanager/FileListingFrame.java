package fr.inserm.u794.lindenb.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.lindenb.io.PreferredDirectory;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.FileExtensionFilter;
import org.lindenb.swing.layout.InputLayout;
import org.lindenb.swing.table.GenericTableModel;
import org.lindenb.xml.XMLUtilities;

public class FileListingFrame
extends AbstractIFrame
	{
	private ManagedFilesModel tableModel= new ManagedFilesModel();
	private JTable table;
	private JEditorPane editor;
	private AbstractAction actionAddFile;
	private AbstractAction actionOpenFile;
	private AbstractAction actionEditFile;
	
	FileListingFrame(FileManager owner)
		{
		super(owner,"Files");
		setClosable(false);
		
		addInternalFrameListener(new InternalFrameAdapter()
			{
			@Override
			public void internalFrameOpened(InternalFrameEvent e)
				{
				String s=getFileManager().getPreferences().getProperty("managed.files.path",null);
				File file;
				if(s!=null)
					{
					file= new File(s);
					}
				else
					{
					file= new File(System.getProperty("user.home","."),ManagedFile.DEFAULT_FILENAME);
					}
				if(file.exists())
					{
					try {
						tableModel.read(file);
					} catch (Exception e2) {
						e2.printStackTrace();
						}
					}
				removeInternalFrameListener(this);
				}
			});
		
		owner.addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowClosed(WindowEvent e)
				{
				String s=getFileManager().getPreferences().getProperty("managed.files.path",null);
				File file;
				if(s!=null)
					{
					file= new File(s);
					}
				else
					{
					file= new File(System.getProperty("user.home","."),ManagedFile.DEFAULT_FILENAME);
					}
				try {
					tableModel.save(file);
				} catch (Exception e2) {
					e2.printStackTrace();
					}
				}
			});
		
		JPanel pane= new JPanel(new GridLayout(1,0,2,2));
		pane.setBorder(new EmptyBorder(2,2,2,2));
		super.contentPane.add(pane,BorderLayout.CENTER);
		
		JPanel pane2= new JPanel(new BorderLayout());
		pane.add(pane2);
		this.table= new JTable(this.tableModel);
		pane2.add(new JScrollPane(this.table),BorderLayout.CENTER);
		
		pane2= new JPanel(new BorderLayout());
		pane.add(pane2);
		this.editor= new JEditorPane("text/html","<html></html>");
		this.editor.setEditable(false);
		pane2.add(new JScrollPane(editor));
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshEditorPane();
				}
			});
		
		
		this.actionAddFile= new AbstractAction("Add File...")
			{
			@Override
			public void actionPerformed(ActionEvent e)
				{
				JFileChooser fc= new JFileChooser(PreferredDirectory.getPreferredDirectory());
				JPanel pane= new JPanel(new GridLayout(0,1,2,2));
				pane.setBorder(new TitledBorder("Delimiter"));
				fc.setAccessory(pane);
				AbstractButton buttons[]=new AbstractButton[Delimiter.values().length];
				ButtonGroup g= new ButtonGroup();
				for(int i=0;i< buttons.length;++i)
					{
					buttons[i]=new JRadioButton(Delimiter.values()[i].name(),false);
					pane.add(buttons[i]);
					g.add(buttons[i]);
					}
				buttons[0].setSelected(true);
				fc.setFileFilter(new FileExtensionFilter("Delimited Files",".txt",".csv",".txt.gz",".csv.gz"));
				if(fc.showOpenDialog(getFileManager())!=JFileChooser.APPROVE_OPTION) return;
				File f= fc.getSelectedFile();
				PreferredDirectory.setPreferredDirectory(f);
				try {
					Delimiter delim=Delimiter.tab;
					for(int i=0;i< buttons.length;++i) if(buttons[i].isSelected()) delim=Delimiter.values()[i];
					ManagedFile mf= new ManagedFile(f,delim);
					tableModel.addElement(mf);
				} catch (Exception e2) {
					ThrowablePane.show(getFileManager(), e2);
					}
				}
			};
		
		this.actionOpenFile	= new AbstractAction("Open...")
			{
			@Override
			public void actionPerformed(ActionEvent e)
				{
				int index= table.getSelectedRow();
				if(index==-1) return;
				ManagedFile f= tableModel.elementAt(index);
				SpreadSheetFrame frame= new SpreadSheetFrame(getFileManager(),f.getFile(),f.getDelimiter());
				getFileManager().getDesktop().add(frame);
				frame.setVisible(true);
				}
			};
		this.actionOpenFile.setEnabled(false);
		this.actionEditFile	= new AbstractAction("Edit...")
		{
		@Override
		public void actionPerformed(ActionEvent e)
			{
			int index= table.getSelectedRow();
			if(index==-1) return;
			ManagedFile f= tableModel.elementAt(index);
			JPanel pane= new JPanel(new InputLayout());
			JComboBox cb= new JComboBox(Delimiter.values());
			JTextArea tfDesc = new JTextArea(f.getDescription(),10,40);
			
			JTextField tfKw= new JTextField(f.getTagsAsString());
			pane.add(new JLabel("Delimiter:",JLabel.RIGHT));
			pane.add(cb);
			pane.add(new JLabel("Comment:",JLabel.RIGHT));
			pane.add(new JScrollPane(tfDesc));
			pane.add(new JLabel("Keywords:",JLabel.RIGHT));
			pane.add(tfKw);
			pane.setPreferredSize(new Dimension(500,300));
			if(JOptionPane.showConfirmDialog(FileListingFrame.this, pane,"Edit",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
			if(cb.getSelectedIndex()!=-1)
				{
				f.setDelimiter(Delimiter.class.cast(cb.getSelectedItem()));
				}
			
			f.setDescription(tfDesc.getText().trim());
			f.getTags().clear();
			for(String s: tfKw.getText().split("[ \n]+"))
				{
				s=s.trim(); if(s.length()==0) continue;
				f.getTags().add(s);
				}
			tableModel.setElementAt(f,index);
			refreshEditorPane();
			}
		};
		this.actionEditFile.setEnabled(false);
		
		getJMenuBar().getMenu(0).insert(actionOpenFile, 0);
		getJMenuBar().getMenu(0).insert(actionEditFile, 0);
		getJMenuBar().getMenu(0).insert(actionAddFile, 0);
		
		JToolBar toolbar= new JToolBar();
		super.contentPane.add(toolbar,BorderLayout.NORTH);
		toolbar.add(new JButton(this.actionAddFile));
		toolbar.add(new JButton(this.actionOpenFile));
		toolbar.add(new JButton(this.actionEditFile));
		}
	
	private void refreshEditorPane()
		{
		int i= table.getSelectedRow();
		actionOpenFile.setEnabled(i!=-1);
		actionEditFile.setEnabled(i!=-1);
		if(i==-1)
			{
			editor.setText("<html></html>");
			return;
			}
		ManagedFile f=tableModel.elementAt(i);
		StringBuilder b= new StringBuilder("<html><body><div style='width=600px'><h1>");
		b.append(f.getLabel()).append("</h1>");
		b.append("<dl>");
		b.append("<dt>File</dt><dd>").append(f.getFile().getPath()).append("</dd>");
		b.append("<dt>Added</dt><dd>").append(new SimpleDateFormat().format(f.getCreation())).append("</dd>");
		b.append("<dt>Delimiter</dt><dd>").append(f.getDelimiter().name()).append("</dd>");
		b.append("<dt>Hash</dt><dd>").append(f.getSha1sum()).append("</dd>");
		b.append("<dt>Comments</dt><dd>").append("<dd><div style='background:rgb(255,255,200);'>").append(XMLUtilities.escape(f.getDescription())).append("</div></dd>");
		b.append("<dt>Keywords</dt><dd><ul>");
		for(String s: f.getTags()) b.append("<li>").append(XMLUtilities.escape(s)).append("</li>");
		b.append("</ul></dd>");
		b.append("</dl>");
		b.append("</div></body></html>");
		editor.setText(b.toString());
		editor.setCaretPosition(0);
		}
	}
