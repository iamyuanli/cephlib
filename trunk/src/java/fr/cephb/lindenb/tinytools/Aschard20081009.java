package fr.cephb.lindenb.tinytools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.lindenb.io.PreferredDirectory;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.SwingUtils;
import org.lindenb.util.Compilation;
import org.lindenb.util.NamedKey;

import fr.cephb.lindenb.bio.snp.RsId;
import fr.cephb.lindenb.sql.MySQLConstants;

/**
 * GUI  for Aschard20081003
 * @author lindenb
 *
 */
@SuppressWarnings("unchecked")
public class Aschard20081009
	extends JFrame
	{
	private static final long serialVersionUID = 1L;
	private static final NamedKey<String> MYSQL_SOURCES[]= new NamedKey[]{
		new NamedKey<String>(MySQLConstants.URI+"://genome-mysql.cse.ucsc.edu/hg18?user=genome&password=","UCSC"),
		new NamedKey<String>(MySQLConstants.URI+"://localhost/hg18?user=anonymous&password=","localhost")
		};
	private JComboBox comboxMysqlSource;
	private JList rsList;
	private JLabel listInfo;
	
	private Aschard20081009()
		{
		super("About SNP");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e) {
				SwingUtils.center(e.getWindow());
				}
			
			});

		
		
		JMenuBar bar= new JMenuBar();
		setJMenuBar(bar);
		JMenu menu= new JMenu("File");
		bar.add(menu);
		menu.add(new AbstractAction("About")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						Aschard20081009.this,
						new JLabel("<html><body><h1>About SNP</h1><h3>Pierre LIndenbaum PhD 2008</h3><h4>Centre d'Etude du Polymorphisme Humain (CEPH). France</h4><h6>"+Compilation.getLabel()+"</h6></body></html>"),
						"About",
						JOptionPane.PLAIN_MESSAGE,
						null
					);
				}
			});
		menu.add(new JSeparator());
		menu.add(new AbstractAction("Quit")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Aschard20081009.this.setVisible(false);
				Aschard20081009.this.dispose();
				}
			});
		
		JPanel content= new JPanel(new BorderLayout(5,5));
		setContentPane(content);
		
		JPanel top= new JPanel(new FlowLayout(FlowLayout.LEADING));
		content.add(top,BorderLayout.NORTH);
		top.add(new JLabel("Mysql Source:",JLabel.RIGHT));
		top.add(this.comboxMysqlSource= new JComboBox(MYSQL_SOURCES));
		this.comboxMysqlSource.setSelectedIndex(0);
		this.comboxMysqlSource.setToolTipText("");
		this.rsList= new JList(new DefaultListModel());
		content.add(new JScrollPane(this.rsList));
		
		top.add(new JButton(new AbstractAction("Load SNPs")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser= new JFileChooser(PreferredDirectory.getPreferredDirectory());
				chooser.setMultiSelectionEnabled(true);
				if(chooser.showOpenDialog(Aschard20081009.this)!=JFileChooser.APPROVE_OPTION) return;
				Set<RsId> set= getSNPs();
				try
					{
					for(File f: chooser.getSelectedFiles())
						{
						PreferredDirectory.setPreferredDirectory(f);
						set.addAll( RsId.readSNPList(f));
						}
					
					DefaultListModel newmodel= new DefaultListModel();
					for(RsId rs: set) newmodel.addElement(rs);
					rsList.setModel(newmodel);
					listInfo.setText(""+ newmodel.size()+" SNPs");
					} 
				catch(Throwable err)
					{
					ThrowablePane.show(Aschard20081009.this, err);
					return;
					}
				}	
			}));
		
		top.add(new JButton(new AbstractAction("Paste SNPs")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea area= new JTextArea(20,40);
				
				if(JOptionPane.showConfirmDialog(
						Aschard20081009.this,
						new JScrollPane(area),
						"Paste SNP",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE
					)!=JOptionPane.OK_OPTION) return;
				
				Set<RsId> set= getSNPs();
				try
					{
					set.addAll( RsId.readSNPList(new StringReader(area.getText())));
					DefaultListModel newmodel= new DefaultListModel();
					for(RsId rs: set) newmodel.addElement(rs);
					rsList.setModel(newmodel);
					listInfo.setText(""+ newmodel.size()+" SNPs");
					} 
				catch(Throwable err)
					{
					ThrowablePane.show(Aschard20081009.this, err);
					return;
					}
				}	
			}));
		
		
		top.add(new JButton(new AbstractAction("Clear List")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
					DefaultListModel m= (DefaultListModel)rsList.getModel();
					m.setSize(0);
				}	
			}));
		
		
		JPanel bottom=new JPanel(new FlowLayout(FlowLayout.TRAILING));
		content.add(bottom,BorderLayout.SOUTH);
		
		bottom.add(new JButton(new AbstractAction("Fetch Info to File...")
			{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel m= (DefaultListModel)rsList.getModel();
				if(m.getSize()==0) return;
				NamedKey<String> source=(NamedKey<String>)comboxMysqlSource.getSelectedItem();
				if(source==null) return;
				JFileChooser fc= new JFileChooser(PreferredDirectory.getPreferredDirectory());
				if(fc.showSaveDialog(Aschard20081009.this)!=JFileChooser.APPROVE_OPTION) return;
				File f= fc.getSelectedFile();
				if(f.exists() && JOptionPane.showConfirmDialog(Aschard20081009.this, "File "+f+" exists. Overwrite ?","Overwrite?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
				try {
					PreferredDirectory.setPreferredDirectory(f);
					PrintStream out= new PrintStream(f);
					Aschard20081003 handler= new Aschard20081003();
					handler.setJdbcURI(source.getId());
					handler.setOutputStream(out);
					handler.run(getSNPs());
					out.flush();
					out.close();
					JOptionPane.showMessageDialog(Aschard20081009.this, "Done");
					}
				catch (Exception err)
					{
					ThrowablePane.show(Aschard20081009.this, err);
					return;
					}
				}	
			}));
		
		top.add(this.listInfo= new JLabel("No SNP"));
		}
	
	private Set<RsId> getSNPs()
		{
		TreeSet<RsId> set= new TreeSet<RsId>();
		ListModel m= rsList.getModel();
		for(int i=0;i< m.getSize();++i)
			{
			set.add(RsId.class.cast(m.getElementAt(i)));
			}
		return set;
		}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			Class.forName(MySQLConstants.DRIVER);
			Aschard20081009 app = new Aschard20081009();
			SwingUtils.show(app);
			} 
		catch (Throwable err)
			{
			ThrowablePane.show(null, err);
			}

		}

}
