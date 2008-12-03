/**
 * 
 */
package fr.inserm.u794.lindenb.filemanager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.lindenb.swing.SwingUtils;
import org.lindenb.util.TimeUtils;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author lindenb
 *
 */
public class FileManager extends JFrame
	{
	private static final long serialVersionUID = 1L;
	private static final String PREFS_FILE=".inserm-filemanager-prefs.xml";
	private static final String BERKELY_FILE=".inserm-filemanager-db";
	private JDesktopPane desktop;
	private JPanel contentPane;
	private Properties preferences= new Properties();
	private Environment berkeleyEnv;
	private Database snp129DB;
	
	public FileManager()
		{
		super("File Manager");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		try
		{
		File f= new File(System.getProperty("user.home","."),BERKELY_FILE);
		if(!f.exists())
			{
			if(!f.mkdir()) throw new RuntimeException("Cannot create "+f);
			}
		EnvironmentConfig cfg= new EnvironmentConfig();
		cfg.setAllowCreate(true);
		cfg.setReadOnly(false);
		cfg.setTransactional(true);
		this.berkeleyEnv=new Environment(f,cfg);
		
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setReadOnly(false);
		this.snp129DB= this.berkeleyEnv.openDatabase(null, "snp", dbcfg);
		
		
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
		
		JMenuBar bar= new JMenuBar();
		setJMenuBar(bar);
		JMenu menu= new JMenu("File");
		bar.add(menu);
		menu.add(new JSeparator());
		menu.add(new AbstractAction("Quit")
			{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				doMenuQuit();
				}
			});	
		
		addWindowListener(new WindowAdapter()
			{
			@Override
			public void windowOpened(WindowEvent e)
				{
				SwingUtils.center(FileManager.this, 150);
				FileManager.this.validate();
				
				FileListingFrame f= new FileListingFrame(FileManager.this);
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
		} catch (Exception e) {
			
			}
		
		//clean DB
		try { if(snp129DB!=null) snp129DB.close(); } catch(Throwable err) { }
		try { this.berkeleyEnv.compress(); } catch(Throwable err) { }
		try { this.berkeleyEnv.cleanLog(); } catch(Throwable err) { }
		try { this.berkeleyEnv.close(); } catch(Throwable err) { }
		}
	
	JDesktopPane getDesktop() {
		return desktop;
		}
	
	public Properties getPreferences()
		{
		return preferences;
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
	    FileManager f= new FileManager();
	    SwingUtils.show(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
}

}
