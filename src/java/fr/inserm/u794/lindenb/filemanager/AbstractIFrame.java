package fr.inserm.u794.lindenb.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;


public class AbstractIFrame extends JInternalFrame
	{
	private static final long serialVersionUID = 1L;
	private static int ID_GENERATOR=0;
	private int id=(++ID_GENERATOR);
	private FileManager owner;
	protected JPanel contentPane;
	protected AbstractIFrame(FileManager owner,String title)
		{
		super(title,true,true,true,true);
		this.owner=owner;
		addInternalFrameListener(new InternalFrameAdapter()
			{
			@Override
			public void internalFrameOpened(InternalFrameEvent e)
				{
				Dimension d=getFileManager().getDesktop().getSize();
				Dimension d2= new Dimension(0,0);
				d2.width=(int)(d.width*0.8);
				d2.height=(int)(d.height*0.8);
				AbstractIFrame.this.setBounds(
					(int)((d.width-d2.width)*Math.random()),
					(int)((d.height-d2.height)*Math.random()),
					d2.width,
					d2.height
					);
				}
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				doMenuClose();
				}
			});
		JMenuBar bar= new JMenuBar();
		setJMenuBar(bar);
		JMenu menu= new JMenu("File");
		bar.add(menu);
		
		this.contentPane=new JPanel(new BorderLayout());
		setContentPane(this.contentPane);
		}
	
	public FileManager getFileManager() {
		return owner;
		}
	
	public int getID()
		{
		return this.id;
		}
	
	public void doMenuClose()
		{
		this.setVisible(false);
		this.dispose();
		}
	
	}
