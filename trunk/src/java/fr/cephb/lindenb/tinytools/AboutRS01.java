/**
 * 
 */
package fr.cephb.lindenb.tinytools;

import java.util.Set;

import org.lindenb.util.Compilation;

import fr.cephb.lindenb.bio.snp.RsId;

/**
 * @author lindenb
 *
 */
public class AboutRS01
	{
	public void run(Set<RsId> set)
		{
		
		}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		try {
			int optind=0;
		    while(optind<args.length)
				{
				if(args[optind].equals("-h"))
					{
					System.err.println(Compilation.getLabel());
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
		    
			} 
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

}
