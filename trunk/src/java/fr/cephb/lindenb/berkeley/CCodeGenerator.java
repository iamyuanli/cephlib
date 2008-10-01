package fr.cephb.lindenb.berkeley;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.lindenb.util.TimeUtils;

public class CCodeGenerator extends AbstractCodeGenerator
	{
	private static String TEMPLATE_ROOT="/env/ceph/home/lindenb/src/cephlib/src/java/fr/cephb/lindenb/berkeley/";

	private CCodeGenerator() throws Exception
		{
		Properties props = new Properties();
		props.put("input.encoding", "utf-8");
		props.put("file.resource.loader.path",TEMPLATE_ROOT);
		Velocity.init(props);
		
	   
		}
	
	
	private void makeH(PrintWriter out,DefinitionFile def)
			throws Exception
		{
		Template template  = Velocity.getTemplate(
				"templateH.vm"
				);
		
		VelocityContext context = new VelocityContext();
		context.put( "today", TimeUtils.toYYYYMMDD());
		context.put( "filename", def.getName());
		context.put( "deffile", def);
		
		
		template.merge( context, out);
	    out.flush();
		
	
		
		}
	
	private void makeC(PrintWriter out,DefinitionFile def)
	throws Exception
		{
		Template template  = Velocity.getTemplate(
				"templateC.vm"
				);
		
		VelocityContext context = new VelocityContext();
		context.put( "today", TimeUtils.toYYYYMMDD());
		context.put( "filename", def.getName());
		context.put( "deffile", def);
		
		
		template.merge( context, out);
	    out.flush();
		}

	@Override
	protected String getSnippetResourceName() {
		return "c-code.xml";
		}
	
	@Override
	public void makeCode() throws Exception
		{
		for(DefinitionFile file: this.definitionFiles)
			{
			PrintWriter out= new PrintWriter(new File(getOutputDir(),file.getName()+".h"),"UTF-8");
			makeH(out,file);
			out.flush();
			out.close();
			
			out= new PrintWriter(new File(getOutputDir(),file.getName()+".c"),"UTF-8");
			makeC(out,file);
			out.flush();
			out.close();
			}
		}
	
	
	
	public static void main(String[] args)
		{
		try {
			CCodeGenerator app= new CCodeGenerator();
			int optind=0;
			while(optind<args.length)
				{
				if(args[optind].equals("-h"))
					{
					System.err.println("Pierre Lindenbaum PhD. C code generator for Berkeley");
					System.err.println("-d output directory");
					System.err.println("-h this screen");
					return;
					}
				else if (args[optind].equals("-d"))
				     {
				     app.setOutputDir(new File(args[++optind]));
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
			while(optind< args.length)
				{
				
				app.parse(new File(args[optind++]));
				}
			app.makeCode();
			} 
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}
