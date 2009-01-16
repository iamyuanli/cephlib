package fr.cephb.lindenb.dochandler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.lindenb.io.IOUtils;
import org.lindenb.io.PreferredDirectory;
import org.lindenb.lang.IllegalInputException;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.ObjectAction;
import org.lindenb.swing.SwingUtils;
import org.lindenb.swing.layout.InputLayout;
import org.lindenb.swing.table.GenericTableModel;
import org.lindenb.util.Cast;
import org.lindenb.util.Debug;
import org.lindenb.util.Pair;
import org.lindenb.util.TimeUtils;


import fr.cephb.lindenb.bio.ncbo.bioportal.NCBOSearchBean;
import fr.cephb.lindenb.bio.ncbo.bioportal.NCBOSearchPane;
import fr.cephb.lindenb.dochandler.entities.Cell;
import fr.cephb.lindenb.dochandler.entities.ColumnSpec;
import fr.cephb.lindenb.dochandler.entities.Document;
import fr.cephb.lindenb.dochandler.entities.DocumentMetaData;
import fr.cephb.lindenb.dochandler.entities.EntityMgrSingleton;
import fr.cephb.lindenb.dochandler.entities.OntClass;
import fr.cephb.lindenb.dochandler.entities.Ontology;
import fr.cephb.lindenb.dochandler.entities.User;


class SuggestConceptButton extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private JTextField  tfConcept;
	private String concept;
	public SuggestConceptButton()
		{
		super(new BorderLayout());
		
		JButton button= new JButton(new AbstractAction("[+]")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				openConceptDialog();
				}
			});
		button.setPreferredSize(new Dimension(16,16));
		add(button,BorderLayout.WEST);
		add(this.tfConcept= new JTextField(20),BorderLayout.CENTER);
		this.tfConcept.setEnabled(false);
		}
	
	private void openConceptDialog()
		{
		NCBOSearchPane dialog = new NCBOSearchPane();
		if(JOptionPane.showConfirmDialog(this, dialog,"Choose...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		NCBOSearchBean bean=dialog.getSelectedBean();
		if(bean==null)
			{
			return;
			}
		this.tfConcept.setToolTipText(bean.getPreferredName());
		this.tfConcept.setText(bean.getPreferredName());
		this.tfConcept.setCaretPosition(0);
		this.concept= bean.getConceptId();
		}
	
	public String getConcept()
		{
		return this.concept;
		}
	}

/**
 * 
 * OntologyPane
 *
 */
class OntologyPane
	extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private int ontologyId;
	private Ontology ontology;
	OntologyPane(int ontologyId)
		{
		super(new BorderLayout());
		this.ontologyId=ontologyId;
		this.ontology=EntityMgrSingleton.getEntityManager().find(Ontology.class, ontologyId);
		TreeModel treeModel= ontology.asTreeModel();
		this.add(new JScrollPane(this.tree= new JTree(treeModel)
			{
			
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent event)
				{
				TreePath path= getPathForLocation(event.getX(), event.getY());
				if(path==null) return null;
				Object o=path.getLastPathComponent();
				if(!(o instanceof DefaultMutableTreeNode)) return null;
				o=DefaultMutableTreeNode.class.cast(o).getUserObject();
				if(!(o instanceof OntClass)) return null;
				OntClass clazz= OntClass.class.cast(o);
				return clazz.getDescription();
				}
			}));
		this.tree.addMouseListener(new MouseAdapter()
			{
			@Override
			public void mousePressed(MouseEvent e) {
				if(!e.isPopupTrigger()) return;
				TreePath path= tree.getPathForLocation(e.getX(), e.getY());
				if(path==null) return ;
				Object o=path.getLastPathComponent();
				if(!(o instanceof DefaultMutableTreeNode)) return;
				o=DefaultMutableTreeNode.class.cast(o).getUserObject();
				if(!(o instanceof OntClass)) return;
				OntClass clazz= OntClass.class.cast(o);
				JPopupMenu menu= new JPopupMenu();
				AbstractAction action = new ObjectAction<OntClass>(clazz,"Append Child "+clazz.getName())
					{
					
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e)
						{
						JPanel pane= new JPanel(new InputLayout());
						JTextField tfName=new JTextField("",20);
						JTextField tfDesc=new JTextField("",20);
						SuggestConceptButton conceptButton= new SuggestConceptButton();
						
						pane.add(new JLabel("Name:",JLabel.RIGHT));
						pane.add(tfName);
						pane.add(new JLabel("Description:",JLabel.RIGHT));
						pane.add(tfDesc);
						pane.add(new JLabel("Concept:",JLabel.RIGHT));
						pane.add(conceptButton);
						
						Pattern namePattern= Pattern.compile("[a-z][a-z0-9_]*",Pattern.CASE_INSENSITIVE);
						while(true)
							{
							if(JOptionPane.showConfirmDialog(
									OntologyPane.this, pane,"New Node",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
							
							String label= tfName.getText().trim();
							if(!namePattern.matcher(label).matches())
								{
								JOptionPane.showMessageDialog(OntologyPane.this, "Bad name");
								continue;
								}
							String desc= tfDesc.getText().trim();
							if(desc.length()==0) desc=label;
							
							OntClass newclass= new OntClass(
									ontology,
									getObject(),
									label,
									desc
								);
							newclass.setConceptURI(conceptButton.getConcept());
							EntityManager mgr=EntityMgrSingleton.getEntityManager();
						
							EntityTransaction tx=mgr.getTransaction();
							try {Debug.debug();
								tx.begin();Debug.debug();
								mgr.persist(newclass);Debug.debug();
								tx.commit();Debug.debug();
								break;
							} catch (Exception e2) {
								tx.rollback();
								e2.printStackTrace();
								ThrowablePane.show(OntologyPane.this,e2);
								}
							finally
								{Debug.debug();
								mgr.close();Debug.debug();
								reloadOntology();
								}
							}
					
						}
					};
				menu.add(action);
				menu.show(tree, e.getX(), e.getY());
				}
			});
		}
	
	private void reloadOntology()
		{
		Debug.debug(System.currentTimeMillis());
		EntityManager mgr=EntityMgrSingleton.getEntityManager();
		Debug.debug();
		this.ontology= EntityMgrSingleton.getEntityManager().find(Ontology.class, ontologyId);Debug.debug();
		TreeModel treeModel= this.ontology.asTreeModel();Debug.debug();
		this.tree.setModel(treeModel);
		mgr.close();
		Debug.debug(System.currentTimeMillis());
		}
	
	public Set<OntClass> getSelectedClasses()
		{
		Set<OntClass> set= new HashSet<OntClass>();
		TreePath sel[]=this.tree.getSelectionPaths();
		if(sel==null) return set;
		for(TreePath path:sel)
			{
			Object o=path.getLastPathComponent();
			if(!(o instanceof DefaultMutableTreeNode)) continue;
			o=DefaultMutableTreeNode.class.cast(o).getUserObject();
			if(!(o instanceof OntClass)) continue;
			OntClass clazz= OntClass.class.cast(o);
			set.add(clazz);
			}
		return set;
		}
	}

class MetaPane
	extends JPanel
	{
	private static final long serialVersionUID = 1L;
	private DefaultTableModel tModel= new DefaultTableModel(new String[]{"Property","Value"},0);
	private JTextField valueField;
	private JComboBox cbox;
	MetaPane()
		{
		super(new BorderLayout());
		JPanel top=new JPanel(new FlowLayout(FlowLayout.LEADING));
		this.add(top,BorderLayout.NORTH);
		AbstractAction action=new AbstractAction("OK")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String prop= String.valueOf(cbox.getSelectedItem());
				if(prop==null) prop="";
				prop=prop.trim();
				String value= valueField.getText().trim();
				if(prop.length()>0 && value.length()>0)
					{
					tModel.addRow(new String[]{prop,value});
					cbox.setSelectedIndex(-1);
					valueField.setText("");
					}
				
				}
			};
			
		EntityManager mgr=EntityMgrSingleton.getEntityManager();
		Query q=mgr.createNativeQuery("select distinct property from docmeta order by 1");
		Vector<?> items= new Vector(q.getResultList());
		mgr.close();
		
		this.cbox= new JComboBox(items);
		this.cbox.setEditable(true);
		top.add(new JLabel("Add:",JLabel.RIGHT));
		top.add(cbox);
		top.add(this.valueField=new JTextField(20));
		this.valueField.addActionListener(action);
		top.add(new JButton(action));
		
		JTable table= new JTable(tModel);
		add(new JScrollPane(table));
		}
	
	public void addProperty(String prop,String value)
		{
		tModel.addRow(new String[]{prop,value});
		}
	
	List<Pair<String,String>> getProperties()
		{
		List<Pair<String,String>> prop= new ArrayList<Pair<String,String>>();
		for(int i=0;i< tModel.getRowCount();++i)
			{
			prop.add(new Pair<String,String>(
					String.valueOf(tModel.getValueAt(i, 0)),
					String.valueOf(tModel.getValueAt(i, 1))
					));
			}
		return prop;
		}
	
	}



/**
 * 
 * DocumentHandler
 *
 */
public class DocumentHandler extends JFrame
	{
	private static final long serialVersionUID = 1L;
	private final int DOCUMENT_ONTOLOGY_ID=2;
	private final int COLUMN_ONTOLOGY_ID=3;
	
	private static final String PROPERTY_USER="property.user";
	private static final String ACTION_QUIT="action.quit";
	private static final String ACTION_PROMPT_PASSWORD="action.prompt.password";
	private static final String ACTION_LOGOFF="action.logoff";
	private static final String ACTION_LOAD_NEW="action.load.new";
	private static final String ACTION_LIST_DOCUMENTS="action.list.documents";
	private User user=null;
	private JDesktopPane desktop;
	private ActionMap actionMap=new ActionMap();
	private PropertyChangeSupport pptyChangeSupport= new PropertyChangeSupport(this);
	
	
	/**
	 * DocumentTableModel
	 */
	private class DocumentTableModel
		extends GenericTableModel<Document>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public String getColumnName(int column) {
			switch(column)
				{
				case 0: return "Filename";
				case 1: return "Description";
				case 2: return "Rows";
				case 3: return "Columns";
				}
			return null;
			}
		
		@Override
		public Class<?> getColumnClass(int column) {
			switch(column)
				{
				case 0: return String.class;
				case 1: return String.class;
				case 2: return Integer.class;
				case 3: return Integer.class;
				}
			return null;
			}
		
		@Override
		public int getColumnCount() {
			return 4;
			}
		@Override
		public Object getValueOf(Document doc, int column) {
			switch(column)
				{
				case 0: return doc.getFilename();
				case 1: return doc.getDescription();
				case 2: return doc.getRowCount();
				case 3: return doc.getColumnCount();
				}
			return null;
			}
		}
	
	/**
	 * 
	 * MyInternalFrame
	 *
	 */
	private class MyInternalFrame
	extends JInternalFrame
		{
		private static final long serialVersionUID = 1L;

		MyInternalFrame()
			{
			super("",true,true,true,true);
			}
		}
	
	/**
	 * 
	 * DocumentListFrame
	 *
	 */
	private class DocumentListFrame
	extends MyInternalFrame
		{
		private static final long serialVersionUID = 1L;
		private DocumentTableModel tableModel=new DocumentTableModel();
		private JTable table;
		DocumentListFrame()
			{
			setTitle("Documents");
			JPanel panel= new JPanel(new BorderLayout());
			this.table=new JTable(tableModel);
			panel.add(new JScrollPane(this.table));
			setContentPane(panel);
			addInternalFrameListener(new InternalFrameAdapter()
				{
				@Override
				public void internalFrameOpened(InternalFrameEvent e)
					{
					DocumentListFrame.this.setBounds(10, 10, 500, 300);
					reloadModel();
					}
				});
			this.table.addMouseListener(new MouseAdapter()
				{
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()<2) return;
					int i= table.getSelectedRow();
					openDocumentAt(i);
					}
				});
			JMenuBar bar= new JMenuBar();
			setJMenuBar(bar);
			JMenu menu= new JMenu("File");
			bar.add(menu);
			}
		
		private void openDocumentAt(int rowIndex)
			{
			if(rowIndex==-1) return;
			Document doc=tableModel.elementAt(rowIndex);
			DocumentFrame frame= new DocumentFrame(doc.getId());
			desktop.add(frame);
			frame.setVisible(true);
			}
		
		@SuppressWarnings("unchecked")
		void reloadModel()
			{
			EntityManager mgr=EntityMgrSingleton.getEntityManager();
			Query q=mgr.createQuery("SELECT c FROM Document c");
			tableModel.clear();
			tableModel.addAll(q.getResultList());
			mgr.close();
			}
		}
	
	/**
	 * 
	 * DocumentFrame
	 *
	 */
	private class DocumentFrame
	extends MyInternalFrame
		{
		private static final long serialVersionUID = 1L;
		private DefaultTableModel tableModel;
		private JTable table;
		DocumentFrame(int docId)
			{
			EntityManager mgr=EntityMgrSingleton.getEntityManager();
			Document doc= mgr.find(Document.class, docId);
			
			setTitle(doc.getFilename());
			List<ColumnSpec> cols=doc.getColumnSpecs();
			String header[]=new String[cols.size()];
			for(ColumnSpec cs:cols) header[cs.getColumnIndex()]=cs.getName();
			
			this.tableModel= new DefaultTableModel(doc.getRowCount(),doc.getColumnCount());
			this.tableModel.setColumnIdentifiers(header);
			
			
			Query q=mgr.createQuery("SELECT c FROM Cell c where c.documentId=:docId");
			q.setParameter("docId", doc.getId());
			for(Object o:q.getResultList())
				{
				Cell c= Cell.class.cast(o);
				if(c.getRow()<0 || c.getRow()>= tableModel.getRowCount() || 
						c.getColumn()<0 || c.getColumn()>= tableModel.getColumnCount()) continue;
				tableModel.setValueAt(c, c.getRow(), c.getColumn());
				}
			mgr.close();
			
			
			JPanel panel= new JPanel(new BorderLayout());
			this.table=new JTable(tableModel);
			panel.add(new JScrollPane(this.table));
			setContentPane(panel);
			addInternalFrameListener(new InternalFrameAdapter()
				{
				@Override
				public void internalFrameOpened(InternalFrameEvent e)
					{
					DocumentFrame.this.setBounds(40, 40, 500, 300);
					}
				});
			
			JMenuBar bar= new JMenuBar();
			setJMenuBar(bar);
			JMenu menu= new JMenu("File");
			bar.add(menu);
			menu.add(new JMenuItem("Save As..."));
			}
	
		}
	
	/**
	 * 
	 * DocumentHandler
	 * 
	 */
	private DocumentHandler()
		{
		super("Document Handler");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel contentPane= new JPanel(new BorderLayout());
		setContentPane(contentPane);
		contentPane.add(desktop= new JDesktopPane());
		
		addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e)
				{
				doMenuPromptPassword();
				}
			
			@Override
			public void windowClosing(WindowEvent e) {
				doMenuClose();
				}
			});
		
		
		AbstractAction action = new AbstractAction("Quit")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
				{
				doMenuClose();
				}
			};
		this.actionMap.put(ACTION_QUIT, action);
		//
		action = new AbstractAction("Login")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				doMenuPromptPassword();
				}
			};
		this.actionMap.put(ACTION_PROMPT_PASSWORD, action);
		//
		action = new AbstractAction("Logoff")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				setUser(null);
				}
			};
		this.actionMap.put(ACTION_LOGOFF, action);
		//
		action = new AbstractAction("Load New...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				doMenuLoadNew();
				}
			};
		this.actionMap.put(ACTION_LOAD_NEW, action);
		//
		action = new AbstractAction("Documents")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
				{
				DocumentListFrame f= new DocumentListFrame();
				desktop.add(f);
				f.setVisible(true);
				}
			};
		this.actionMap.put(ACTION_LIST_DOCUMENTS, action);
		
		pptyChangeSupport.addPropertyChangeListener(PROPERTY_USER, new PropertyChangeListener()
			{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
				{
				JMenuBar bar= new JMenuBar();
				DocumentHandler.this.setJMenuBar(bar);
				JMenu menu= new JMenu("File");
				bar.add(menu);
				if(user==null)
					{
					menu.add(actionMap.get(ACTION_PROMPT_PASSWORD));
					menu.add(actionMap.get(ACTION_QUIT));
					}
				else
					{
					menu.add(actionMap.get(ACTION_QUIT));
					menu.add(actionMap.get(ACTION_LOAD_NEW));
					menu.add(actionMap.get(ACTION_LIST_DOCUMENTS));
					}
				DocumentHandler.this.validate();
				}
			});
		
		}
	
	private void doMenuLoadNew()
		{
		JFileChooser chooser= new JFileChooser(PreferredDirectory.getPreferredDirectory());
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
		File file= chooser.getSelectedFile();
		PreferredDirectory.setPreferredDirectory(file);
		JTextField tfRegex=new JTextField("[\\t]",20);
		JCheckBox cbFirstLineHeader=new JCheckBox("First Line is Header",true);
		JPanel pane= new JPanel(new InputLayout());
		pane.add(new JLabel("First Line is header:",JLabel.RIGHT));
		pane.add(tfRegex);
		pane.add(new JLabel("Regex Delimiter",JLabel.RIGHT));
		pane.add(cbFirstLineHeader);
		while(true)
			{
			if(JOptionPane.showConfirmDialog(this, pane,file.getName(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
			if(Cast.Pattern.isA(tfRegex.getText())) break;
			JOptionPane.showMessageDialog(this, "Not a valid Regular exception");
			}
		Pattern delimiter= Cast.Pattern.cast(tfRegex.getText());
		String headerLabels[]=null;
		int row=0;
		int col=0;
		try {
			String line;
			BufferedReader r=IOUtils.openFile(file);
			if(cbFirstLineHeader.isSelected())
				{
				line=r.readLine();
				if(line==null) throw new IllegalInputException("Header missing in "+file);
				if(line.startsWith("#")) line=line.substring(1);
				headerLabels= delimiter.split(line);
				for(String s:headerLabels)
					{
					if(s.length()==0) throw new IllegalInputException("Empty Header in "+file);
					}
				col=headerLabels.length;
				}
			while((line=r.readLine())!=null)
				{
				if(line.trim().length()==0) continue;
				row++;
				String tokens[]= delimiter.split(line);
				if(headerLabels==null)
					{
					col= Math.max(col,tokens.length);
					}
				else if(col> headerLabels.length )
					{
					 throw new IllegalInputException("Extra columns in "+file+" line "+row);
					}
				}
			r.close();
			
			if(headerLabels==null)
				{
				headerLabels=new String[col];
				for(int i=0;i< col;++i) headerLabels[i]="$"+(1+i);
				}
			
		} catch (Exception e) {
			ThrowablePane.show(this, e);
			return;
			}
		
		Document document= new Document();
		document.setColumnCount(col);
		document.setRowCount(row);
		document.setFilename(file.getName());
		List<ColumnSpec> list= new ArrayList<ColumnSpec>();
		document.setColumnSpecs(list);
		
		
		//meta info
		MetaPane metapane= new MetaPane();
		metapane.addProperty("dc:date",TimeUtils.toYYYYMMDD());
		metapane.addProperty("dc:creator",System.getProperty("user.name", ""));
		metapane.addProperty("dc:title",file.getName());
		
		if(JOptionPane.showConfirmDialog(this, metapane,"Meta",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		
		
		
		//loop over columns
		for(int i=0;i< headerLabels.length;++i)
			{
			ColumnSpec columnSpec= new ColumnSpec();
			columnSpec.setColumnIndex(i);
			list.add(columnSpec);
			
			Dimension dim= new Dimension(250,200);
			pane= new JPanel(new BorderLayout());
			pane.add(new JLabel("Define Column "+(i+1)+"/"+headerLabels.length+":"+headerLabels[i],JLabel.CENTER),BorderLayout.NORTH);
			JPanel pane2= new JPanel(new GridLayout(1,0,3,3));
			pane.add(pane2);
			
			DefaultListModel dlm= new DefaultListModel();
			try
				{
				String line=null;
				BufferedReader r=IOUtils.openFile(file);
				while((line=r.readLine())!=null)
					{
					String tokens[]=delimiter.split(line);
					dlm.addElement(tokens[i]);
					if(dlm.getSize()>50) break;
					}
				r.close();
				}
			catch (Exception e) {
				ThrowablePane.show(this, e);
				return;
				}
			JPanel pane3= new JPanel(new BorderLayout());
			pane2.add(pane3);
			pane3.setPreferredSize(dim);
			JList jlist=new JList(dlm);
			jlist.setEnabled(false);
			pane3.add(new JScrollPane(jlist),BorderLayout.CENTER);
			
			
			pane3= new JPanel(new InputLayout());
			pane2.add(pane3);
			pane3.setPreferredSize(dim);
			JTextField label= new JTextField(headerLabels[i],20);
			JTextField desc= new JTextField(headerLabels[i],20);
			pane3.add(new JLabel("Name:",JLabel.RIGHT));
			pane3.add(label);
			pane3.add(new JLabel("Comment:",JLabel.RIGHT));
			pane3.add(desc);
			
			pane3= new JPanel(new BorderLayout());
			pane2.add(pane3);
			pane3.setPreferredSize(dim);
			OntologyPane ont=new OntologyPane(COLUMN_ONTOLOGY_ID);
			pane3.add(ont,BorderLayout.CENTER);
			
			if(JOptionPane.showConfirmDialog(this, pane,"Column "+(i+1),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
			
			columnSpec.setName(label.getText().trim());
			columnSpec.setDescription(desc.getText().trim());
			columnSpec.setOntClasses(ont.getSelectedClasses());
			}

		pane= new JPanel(new GridLayout());
		JTextArea comment= new JTextArea(20,30);
		pane.add(new JScrollPane(comment));
		OntologyPane docOnt=new OntologyPane(DOCUMENT_ONTOLOGY_ID);
		pane.add(docOnt);
		if(JOptionPane.showConfirmDialog(this, pane,"About",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		document.setDescription(comment.getText().trim());
		document.setOntClasses(docOnt.getSelectedClasses());
		
		
		EntityManager mgr= EntityMgrSingleton.getEntityManager();
		EntityTransaction tx=mgr.getTransaction();
		try {
			tx.begin();
			mgr.persist(document);
			
			for(Pair<String,String> pair:metapane.getProperties())
				{
				//document
				DocumentMetaData meta= new DocumentMetaData();
				meta.setDocument(document);
				meta.setProperty(pair.first());
				meta.setValue(pair.second());
				mgr.persist(meta);
				}
			
			for(ColumnSpec spec:document.getColumnSpecs())
				{
				spec.setDocument(document);
				mgr.persist(spec);
				}
			String line;
			BufferedReader r=IOUtils.openFile(file);
			if(cbFirstLineHeader.isSelected())
				{
				line=r.readLine();//ignore first
				}
			row=-1;
			while((line=r.readLine())!=null)
				{
				if(line.trim().length()==0) continue;
				row++;
				
				Debug.debug(row);
				String tokens[]= delimiter.split(line);
				for(int i=0;i< tokens.length;++i)
					{
					Cell cell= new Cell(document.getId(),i,row,tokens[i]);
					mgr.persist(cell);
					}
				}
			r.close();
			tx.commit();
		} catch (Exception e)
			{
			e.printStackTrace();
			tx.rollback();
			ThrowablePane.show(this,e);
			}
		finally
			{
			mgr.close();
			}
		modelChanged();
		}
	
	private void modelChanged()
		{
		for(JInternalFrame iframe: this.desktop.getAllFrames())
			{
			if((iframe instanceof DocumentListFrame))
				{
				DocumentListFrame.class.cast(iframe).reloadModel();
				}
			
			}
		}
	
	
	public void doMenuClose()
		{
		this.setVisible(false);
		this.dispose();
		}
	
		
	public void doMenuPromptPassword()
		{
		if(1==1)
			{
			setUser(EntityMgrSingleton.getEntityManager().find(User.class, 1));
			return;
			}
		
		JTextField tfLogin;
		JPasswordField tfPass;
		JPanel pane= new JPanel(new InputLayout());
		pane.add(new JLabel("Login:",JLabel.RIGHT));
		pane.add(tfLogin=new JTextField(20));
		pane.add(new JLabel("Password:",JLabel.RIGHT));
		pane.add(tfPass=new JPasswordField(20));
		if(JOptionPane.showConfirmDialog(this, pane,"Login",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		String log= tfLogin.getText().trim();
		if(log.length()==0) return;
		User user= User.findByName(log,new String(tfPass.getPassword()));
		setUser(user);
		}
	
	public void setUser(User user)
		{
		User old= this.user;
		this.user=user;
		System.err.println("user="+user);
		this.pptyChangeSupport.firePropertyChange(PROPERTY_USER, old, this.user);
		}
	
	public static void main(String[] args)
		{
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
			    
			    
			    EntityMgrSingleton.getEntityManager();
			    JFrame.setDefaultLookAndFeelDecorated(true);
			    JDialog.setDefaultLookAndFeelDecorated(true);
			    DocumentHandler app= new DocumentHandler();
			    SwingUtils.center(app,150);
			    SwingUtils.show(app);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
