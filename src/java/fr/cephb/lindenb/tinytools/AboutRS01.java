/**
 * 
 */
package fr.cephb.lindenb.tinytools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

import org.lindenb.util.Compilation;

import fr.cephb.lindenb.bio.snp.RsId;
import fr.cephb.lindenb.bio.ucsc.UCSCConstants;
import fr.cephb.lindenb.sql.MySQLConstants;

/**
 * @author lindenb
 *
 */
public class AboutRS01
	{
	public void run(Set<RsId> set) throws SQLException
		{
		
		Vector<RsId> snps= new Vector<RsId>(set);
		int start=0;
		while(!snps.isEmpty())
			{
			
		
			}
		}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		try {
			Class.forName(MySQLConstants.DRIVER);
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
