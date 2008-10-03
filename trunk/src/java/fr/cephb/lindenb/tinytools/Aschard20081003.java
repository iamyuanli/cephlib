package fr.cephb.lindenb.tinytools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.lindenb.io.IOUtils;

import fr.cephb.lindenb.bio.snp.RsId;
import fr.cephb.lindenb.bio.ucsc.hg18.Hg18CytoBand;
import fr.cephb.lindenb.bio.ucsc.hg18.Hg18HapmapSnps;
import fr.cephb.lindenb.bio.ucsc.hg18.Hg18RefGene;
import fr.cephb.lindenb.bio.ucsc.hg18.Hg18Snp129;
import fr.cephb.lindenb.sql.MySQLConstants;

/**
 * small soft asked by Hugo Aschard
 * taskes as input a list of snp. finds cytoband, hapmap frequencies etc...
 * @author lindenb
 *
 */
public class Aschard20081003
{
public static final String HAPMAPDB[]=new String[]{"hapmapSnpsCEU","hapmapSnpsCHB","hapmapSnpsJPT","hapmapSnpsYRI"};

private PrintStream out= System.out;
private PrintStream cout()
	{
	return out;
	}
/*
private static String collection2String(Collection<?> col)
	{
	if(col==null || col.isEmpty()) return "";
	StringBuilder b=new StringBuilder();
	for()
	}*/

private void run(Set<RsId> rsSet) throws SQLException
	{
	final char TAB='\t';
	Connection con= DriverManager.getConnection(
			MySQLConstants.URI+"://localhost/hg18",
			"anonymous",
			""
			);
	cout().print(
		"#rs" +TAB+
		"chromosome"  +TAB+
		"position"  +TAB+
		"strand" +TAB+
		"AvHet" +TAB+
		"AvHetSE" +TAB+
		"Observed" +TAB+
		"refNCBI" +TAB+
		"class" +TAB+
		"function" +TAB+
		"locType" +TAB+
		"molType" +TAB+
		"valid" +TAB+
		"cytoband" +TAB+
		"genes" +TAB
		);
	
	for(String hapmapDb:HAPMAPDB)
		{
		cout().print(
				hapmapDb+"-Observed"+TAB+
				hapmapDb+"-Allele1"+TAB+
				hapmapDb+"-Allele2"+TAB+
				hapmapDb+"-HomoCount1"+TAB+
				hapmapDb+"-HeteroCount"+TAB+
				hapmapDb+"-HomoCount2"+TAB);
		}
	cout().println();
	
	for(RsId rsid: rsSet)
		{
		PreparedStatement pstmt=con.prepareStatement("select * from snp129 where name=?");
		pstmt.setString(1, rsid.getName());
		Hg18Snp129 snp= Hg18Snp129.selectOneOrZero(pstmt.executeQuery());
		if(snp==null)
			{
			cout().println(rsid.getName()+TAB+"##Not FOUND");
			continue;
			}
		
		cout().print(snp.getName()); cout().print(TAB);
		cout().print(snp.getChrom()); cout().print(TAB);
		cout().print(snp.getChromStart()); cout().print(TAB);
		cout().print(snp.getStrand()); cout().print(TAB);
		cout().print(snp.getAvHet()); cout().print(TAB);
		cout().print(snp.getAvHetSE()); cout().print(TAB);
		cout().print(snp.getObserved()); cout().print(TAB);
		cout().print(snp.getRefNCBI()); cout().print(TAB);
		cout().print(snp.getClazz()); cout().print(TAB);
		cout().print(snp.getFuncs()); cout().print(TAB);
		cout().print(snp.getLocType()); cout().print(TAB);
		cout().print(snp.getMolType()); cout().print(TAB);
		cout().print(snp.getValids()); cout().print(TAB);
		
		pstmt=con.prepareStatement("select * from cytoBand where chrom=? and chromStart<=? and chromEnd>=?");
		pstmt.setString(1, snp.getChrom());
		pstmt.setInt(2, snp.getChromStart());
		pstmt.setInt(3, snp.getChromEnd());

		int i=0;
		for(Hg18CytoBand band:Hg18CytoBand.select(pstmt.executeQuery()))
			{
			if(i>0) cout().print(",");
			cout().print(band.getName());
			++i;
			}
		 cout().print(TAB);
		pstmt=con.prepareStatement("select * from refGene where chrom=? and txStart<=? and txEnd>=?");
		pstmt.setString(1, snp.getChrom());
		pstmt.setInt(2, snp.getChromStart());
		pstmt.setInt(3, snp.getChromEnd());
		i=0;
		for(Hg18RefGene  gene : Hg18RefGene.select(pstmt.executeQuery()))
			{
			if(i>0) cout().print(",");
			cout().print(gene.getName()+"/"+gene.getName2());
			++i;
			}
		 cout().print(TAB);
		
		for(String hapmapDb:HAPMAPDB)
			{
			pstmt=con.prepareStatement("select * from "+hapmapDb+" where name=?");
			pstmt.setString(1, snp.getName());
			Hg18HapmapSnps hapmap= Hg18HapmapSnps.selectOneOrZero(pstmt.executeQuery());
			if(hapmap==null)
				{
				cout().print(
						"N/A"+TAB+
						"N/A"+TAB+
						"N/A"+TAB+
						"N/A"+TAB+
						"N/A"+TAB+
						"N/A"+TAB
						);
				}
			else
				{
				cout().print(
					hapmap.getObserved()+TAB+
					hapmap.getAllele1()+TAB+
					hapmap.getAllele2()+TAB+
					hapmap.getHomoCount1()+TAB+
					hapmap.getHeteroCount()+TAB+
					hapmap.getHomoCount2()+TAB
					);
				}
			}
		cout().println();
		}
	}
	
public static void main(String[] args) {
	try {
		Class.forName(MySQLConstants.DRIVER);
	    Aschard20081003 app= new Aschard20081003();
		int optind=0;
		String fileout=null;
	    while(optind<args.length)
			{
			if(args[optind].equals("-h"))
				{
				System.err.println("2008 Pierre Lindenbaum PhD.");
				System.err.println("-h this screen");
				System.err.println("-o file out");
				return;
				}
			else if (args[optind].equals("-o"))
			     {
			     fileout=args[++optind];
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
	    HashSet<RsId> rsSet= new HashSet<RsId>();
	    if(args.length==optind)
	    	{
	    	rsSet.addAll(RsId.readSNPList(System.in));
	    	}
	    else
	    	{
	    	while(optind<args.length)
	    		{
	    		BufferedReader r= IOUtils.openReader(args[optind++]);
	    		rsSet.addAll(RsId.readSNPList(r));
	    		r.close();
	    		}
	    	}
	    if(rsSet.isEmpty())
	    	{
	    	System.err.println("Empty Input");
	    	}
	    app.out=System.out;
	    if(fileout!=null) app.out=new PrintStream(new FileOutputStream(fileout));
	    app.run(rsSet);
	    if(fileout!=null) { app.out.flush(); app.out.close();}
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
