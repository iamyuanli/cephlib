/**
 * 
 */
package fr.inserm.u794.lindenb.workbench;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.zip.GZIPInputStream;


import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;


import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.io.IOUtils;
import org.lindenb.lang.ThrowablePane;
import org.lindenb.swing.SwingUtils;
import org.lindenb.util.Debug;
import org.lindenb.util.TimeUtils;


import com.sleepycat.je.DatabaseConfig;

import fr.inserm.u794.lindenb.workbench.frame.RefFrame;
import fr.inserm.u794.lindenb.workbench.frame.TableFrame;
import fr.inserm.u794.lindenb.workbench.table.Row;
import fr.inserm.u794.lindenb.workbench.table.Table;
import fr.inserm.u794.lindenb.workbench.table.TableRef;

/**
 * @author lindenb
 *
 */
public class Workbench extends JFrame
	{

	private static final String ACTION_LOAD_TABLE= "action.load.table";
	private static final String ACTION_LOAD_URL= "action.load.url";
	private static final String ACTION_QUIT= "action.quit";
	private static final long serialVersionUID = 1L;
	private static final String PREFS_FILE=".inserm-workbench-prefs.xml";
	
	private JDesktopPane desktop;
	private JPanel contentPane;
	private Properties preferences= new Properties();
	private ActionMap actionMap=new ActionMap();
	/** all table Ref */
	private TableRef.Model tableRefModel= new TableRef.Model();
	
	public Workbench()
		{
		super("File Manager");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		try
		{
		
		
		
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setReadOnly(false);
		//this.snp129DB= this.berkeleyEnv.openDatabase(null, "snp", dbcfg);
		
		
		}catch(Throwable er)
		{
			er.printStackTrace();
		}
		
		
		try {
			File f= new File(System.getProperty("user.home","."),PREFS_FILE);
			if(f.exists())
				{
				InputStream in= new FileInputStream(f);
 				this.preferences.loadFromXML(in);
 				in.close();
				}
			} 
		catch (Exception e)
			{
			preferences= new Properties();
			}
		
		this.contentPane= new JPanel(new BorderLayout());
		
		this.desktop= new JDesktopPane();
		this.desktop.setBorder(new EmptyBorder(5,5,5,5));
		this.contentPane.add(this.desktop,BorderLayout.CENTER);
		setContentPane(this.contentPane);
		
		
		/** actions */
		AbstractAction action=new AbstractAction("Load Table")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuLoadTable();
				}
			};
		this.actionMap.put(ACTION_LOAD_TABLE,action);
		
		action=new AbstractAction("Load URL...")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuLoadURL();
				}
			};
		this.actionMap.put(ACTION_LOAD_URL,action);
		
		action=new AbstractAction("Quit")
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuQuit();
				}
			};
		this.actionMap.put(ACTION_QUIT,action);
		
		/* MENUS */
		JMenuBar bar= new JMenuBar();
		setJMenuBar(bar);
		JMenu menu= new JMenu("File");
		bar.add(menu);
		JMenu sub= new JMenu("Load Table");
		menu.add(sub);
		sub.add(this.actionMap.get(ACTION_LOAD_TABLE));
		sub.add(this.actionMap.get(ACTION_LOAD_URL));
		menu.add(new JSeparator());
		menu.add(this.actionMap.get(ACTION_QUIT));
		
		/* TOOLBAR */
		JToolBar toolbar= new JToolBar(JToolBar.VERTICAL);
		this.contentPane.add(toolbar,BorderLayout.WEST);
		toolbar.add(new JButton(this.actionMap.get(ACTION_LOAD_TABLE)));
		
		
		this.addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e)
				{
				RefFrame f= new RefFrame(Workbench.this,Workbench.this.tableRefModel);
				getDesktop().add(f);
				f.setVisible(true);
				}
			@Override
			public void windowClosing(WindowEvent e)
				{
				doMenuQuit();	
				}
			});
		
		}
	
	public void doMenuQuit()
		{
		this.setVisible(false);
		this.dispose();

		
		for(JInternalFrame ifr:getDesktop().getAllFrames())
			{
			ifr.setVisible(false);
			}
		//save preferences
		try {
			File f= new File(System.getProperty("user.home","."),PREFS_FILE);
			FileOutputStream out= new FileOutputStream(f);
			getPreferences().storeToXML(out, "Save on "+TimeUtils.toYYYYMMDD(), "UTF-8");
			out.flush();out.close();
			} 
		catch (Exception e)
			{
			
			}
		
		
		}
	
	public JDesktopPane getDesktop() {
		return desktop;
		}
	
	public Properties getPreferences()
		{
		return preferences;
		}
	
	
	public void doMenuLoadTable()
		{
		FileLoader loader= new FileLoader();
		if(JOptionPane.showConfirmDialog(this, loader,"Select...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		if(loader.getSelectedFile()==null || loader.getColumns().isEmpty()) return;
		
		
		try {
			Table table= new Table();
			table.setColumnLabels(loader.getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName(loader.getSelectedFile().getName());
			
			BufferedReader r= null;
			try
				{
				r=IOUtils.openFile(loader.getSelectedFile());
				String line;
				if(loader.isFirstLineHeader())
					{
					line= r.readLine();
					if(line==null) throw new IOException("Cannot get header");
					//then ignore
					}
				
				int nLine=0;
				while((line=r.readLine())!=null)
					{
					String tokens[]=loader.getDelimiter().split(line);
					Row row= new Row(tokens);
					while(row.size()< table.getColumns().size()) row=row.add("");
					table.getDatabase().put(
							nLine,
							row
							);
					nLine++;
					}
				table.setRowCount(nLine);
				r.close();
				
				TableFrame frame= new TableFrame(this,table);
				getDesktop().add(frame);
				frame.setVisible(true);
				}
			catch(Exception err)
				{
				err.printStackTrace();
				throw err;
				}
			finally
				{
				IOUtils.safeClose(r);
				}
		} catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		}
	
	public void doMenuLoadURL()
		{
		UrlLoader loader= new UrlLoader();
		if(JOptionPane.showConfirmDialog(this, loader,"Select...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null)!=JOptionPane.OK_OPTION) return;
		if(loader.getSelectedURL()==null || loader.getColumns().isEmpty()) return;
		
		
		try {
			Table table= new Table();
			table.setColumnLabels(loader.getColumns());
			table.setDatabase(Berkeley.getInstance().createTable());
			table.setName(loader.getSelectedURL().toString());
			
			BufferedReader r= null;
			try
				{
				InputStream in= loader.getSelectedURL().openStream();
				if(loader.getSelectedURL().getPath().toLowerCase().endsWith(".gz"))
					{
					in= new GZIPInputStream(in);
					}
				r=new BufferedReader(new InputStreamReader(in));
				String line;
				if(loader.isFirstLineHeader())
					{
					line= r.readLine();
					if(line==null) throw new IOException("Cannot get header");
					//then ignore
					}
				
				int nLine=0;
				while((line=r.readLine())!=null)
					{
					String tokens[]=loader.getDelimiter().split(line);
					Row row= new Row(tokens);
					while(row.size()< table.getColumns().size()) row=row.add("");
					table.getDatabase().put(
							nLine,
							row
							);
					nLine++;
					}
				table.setRowCount(nLine);
				r.close();
				
				TableFrame frame= new TableFrame(this,table);
				getDesktop().add(frame);
				frame.setVisible(true);
				}
			catch(Exception err)
				{
				err.printStackTrace();
				throw err;
				}
			finally
				{
				IOUtils.safeClose(r);
				}
		} catch (Exception e)
			{
			ThrowablePane.show(this, e);
			}
		}
	
	
	public static void main(String[] args) {
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Workbench f= new Workbench();
		SwingUtils.center(f,100,100);
	    SwingUtils.show(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
}

}
