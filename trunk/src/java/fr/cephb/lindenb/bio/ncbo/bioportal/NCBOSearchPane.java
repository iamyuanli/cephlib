package fr.cephb.lindenb.bio.ncbo.bioportal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lindenb.swing.SwingUtils;
import org.lindenb.swing.table.GenericTableModel;



public class NCBOSearchPane
extends JPanel
	{
	private static final long serialVersionUID = 1L;

	private static class TermTableModel
		extends GenericTableModel<NCBOSearchBean>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public int getColumnCount() {
			return 8;
			}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex<2 ? Integer.class : String.class;
			}
		
		@Override
		public String getColumnName(int column) {
			switch(column)
			{
			case 0: return "Ontology Version-Id";
			case 1: return "Ontology-Id";
			case 2: return "Ontology Label";
			case 3: return "Record-Type";
			case 4: return "Concept-Id";
			case 5: return "ConceptId Short";
			case 6: return "Preferred Name";
			case 7: return "Contents";
			}
		return null;
			}
		
		@Override
		public Object getValueOf(NCBOSearchBean bean, int column)
			{
			switch(column)
				{
				case 0: return bean.getOntologyVersionId();
				case 1: return bean.getOntologyId();
				case 2: return bean.getOntologyLabel();
				case 3: return bean.getRecordType();
				case 4: return bean.getConceptId();
				case 5: return bean.getConceptIdShort();
				case 6: return bean.getPreferredName();
				case 7: return bean.getContents();
				}
			return null;
			}
		
		}
	
	private JTextField tfTerm;
	private TermTableModel tableModel;
	private AbstractAction searchAction;
	private JTable table;
	private  Runner thread=null;
	private JCheckBox cbPerfect;
	private SpinnerNumberModel pageIndex;
	private SpinnerNumberModel pageCount;
	private JTextField tfIndo;
	private JProgressBar progressBar;
	private JTextArea beanInfo;
	private BeanInfoRunner beanInfoThread=null;
	
	private class Runner
	extends Thread
		{
		NCBOSearch engine= new NCBOSearch();
		String term;
		Runner(String term)
			{
			this.term=term;
			this.engine.setExactmatch(cbPerfect.isSelected());
			this.engine.setPageIndex(pageIndex.getNumber().intValue());
			this.engine.setResultCount(pageCount.getNumber().intValue());
			}
		@Override
		public void run()
			{
			try
				{
				progressBar.setIndeterminate(true);
				tfIndo.setText("Searching "+term);
				tfIndo.setCaretPosition(0);
				List<NCBOSearchBean> items=this.engine.search(this.term);
				if(thread==this)
					{
					tableModel.clear();
					tableModel.addAll(items);
					thread=null;
					}
				tfIndo.setText("");
				}
			catch(Throwable err)
				{
				tfIndo.setText(""+err.getMessage());
				java.awt.Toolkit.getDefaultToolkit().beep();
				err.printStackTrace();
				}
			finally
				{
				thread=null;
				progressBar.setIndeterminate(false);
				tfIndo.setCaretPosition(0);
				}
			}
		}
	
	private class BeanInfoRunner
	extends Thread
		{
		NCBOSearchBean term;
		BeanInfoRunner(NCBOSearchBean term)
			{
			this.term=term;
			}
		@Override
		public void run()
			{
			NCBOConcept engine= new NCBOConcept();
			try
				{
				NCBOClassBean classBean=engine.search(
						term.getOntologyVersionId(),
						term.getConceptIdShort()
						);
				if(classBean==null || beanInfoThread!=this) return;
				StringBuilder b= new StringBuilder("Id: ");
				b.append(classBean.getId()).append("\n");
				b.append("Label: ").append(classBean.getLabel()).append("\n");
				b.append("Description: ").append(classBean.getDescription()).append("\n");
				if(classBean.getSuperClass()!=null)
					{
					b.append("\nSuperClass:");
					b.append("\n\tid: "+classBean.getSuperClass().getId());
					b.append("\n\tlabel: "+classBean.getSuperClass().getLabel());
					}
				
				if(!classBean.getSubClasses().isEmpty())
					{
					b.append("\nSubclasses:");
					for(NCBOConceptBean sub: classBean.getSubClasses())
						{
						b.append("\n\tid: "+sub.getId());
						b.append("\n\tlabel: "+sub.getLabel());
						}
					}
				beanInfo.setText(b.toString());
				}
			catch(Throwable err)
				{
				beanInfo.setText(""+err.getMessage());
				java.awt.Toolkit.getDefaultToolkit().beep();
				err.printStackTrace();
				}
			finally
				{
				beanInfoThread=null;
				beanInfo.setCaretPosition(0);
				}
			}
		}
	
	public NCBOSearchPane()
		{
		super(new BorderLayout());
		setBorder(new EmptyBorder(5,5,5,5));
		JPanel pane= new JPanel(new FlowLayout(FlowLayout.LEADING));
		add(pane,BorderLayout.NORTH);
		pane.add(new JLabel("Search:",JLabel.RIGHT));
		pane.add(this.tfTerm=new JTextField(20));
		this.searchAction= new AbstractAction("Go")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String term= tfTerm.getText().trim();
				search(term);
				}
			};
		this.tfTerm.addActionListener(this.searchAction);
		pane.add(new JButton(this.searchAction));
		pane.add(this.cbPerfect=new JCheckBox("Perfect",false));
		pane.add(new JCheckBox("Properties",false));
		pane.add(new JLabel("Page:",JLabel.RIGHT));
		pane.add(new JSpinner(this.pageIndex=new SpinnerNumberModel(1,1,100,1)));
		pane.add(new JLabel("Count:",JLabel.RIGHT));
		pane.add(new JSpinner(this.pageCount=new SpinnerNumberModel(100,1,1000,1)));
		pane= new JPanel(new BorderLayout());
		
		this.tableModel=new TermTableModel();
		this.table= new JTable(this.tableModel);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		pane.add(new JScrollPane(table),BorderLayout.CENTER);
		this.add(pane,BorderLayout.CENTER);
		
		pane= new JPanel(new FlowLayout(FlowLayout.LEADING));
		this.add(pane,BorderLayout.SOUTH);
		pane.add(this.tfIndo=new JTextField(50));
		this.tfIndo.setEditable(false);
		pane.add(this.progressBar= new JProgressBar());
		this.progressBar.setPreferredSize(new Dimension(100,20));
		
		pane= new JPanel(new BorderLayout());
		
		this.add(pane,BorderLayout.EAST);
		pane.setPreferredSize(new Dimension(200,100));
		JScrollPane scroll=new JScrollPane(this.beanInfo= new JTextArea());
		pane.add(scroll, BorderLayout.CENTER);
		this.beanInfo.setEditable(false);
		
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
			@Override
			public void valueChanged(ListSelectionEvent e) {
				search(getSelectedBean());
				}
			});
		}
	
	public NCBOSearchBean getSelectedBean()
		{
		int i=table.getSelectedRow();
		if(i==-1) return null;
		NCBOSearchBean bean=tableModel.elementAt(i);
		return bean;
		}
	
	public void search(String term)
		{
		if(term==null || term.length()==0) return;
		if(thread!=null)
			{
			try {
				thread.interrupt();
			} catch (Exception e)
				{
				
				}
			return;
			}
		this.thread= new  Runner(term);
		
		this.thread.start();
		}
	
	public void search(NCBOSearchBean bean)
		{
		if(bean==null)
			{
			beanInfo.setText("");
			return;
			}
		if(beanInfoThread!=null)
			{
			try {
				beanInfoThread.interrupt();
			} catch (Exception e)
				{
				
				}
			return;
			}
		this.beanInfoThread= new  BeanInfoRunner(bean);
		
		this.beanInfoThread.start();
		}
	
	public static void main(String[] args) {
		try {
			JFrame f= new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.add(new NCBOSearchPane());
			SwingUtils.center(f,200,200);
			SwingUtils.show(f);
		} catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}
