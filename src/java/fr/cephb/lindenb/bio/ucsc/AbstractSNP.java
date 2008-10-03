package fr.cephb.lindenb.bio.ucsc;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.lindenb.bio.Chromosome;
import org.lindenb.bio.ChromosomePosition;
import org.lindenb.bio.Strand;
import org.lindenb.io.IOUtils;

import fr.cephb.lindenb.bio.snp.RsId;

public abstract class AbstractSNP
implements Serializable
{
private static final long serialVersionUID = 1L;
private ChromosomePosition position;
private RsId name;
private int score;
private Strand strand;
private String refNCBI;
private String refUCSC;
private String observed;
private String moltype;
private String clazz;
private String valid;
private float avHet;
private float aHetSE;
private String func;
private int weight;

protected AbstractSNP(ResultSet row) throws SQLException,IOException
	{
	IOUtils.getReaderContent(row.getCharacterStream(1));
	String chrom	= row.getString("chrom");
	int start	= row.getInt("chromStart");
	int end	= row.getInt("chromEnd");
	this.name= new RsId(row.getString("name"));
	this.position= new ChromosomePosition(
			Chromosome.newInstance(chrom),
			start,end
			);
	this.score = row.getInt("score");
	String s= row.getString("strand");
	this.strand= Strand.newInstance(s==null?'?':s.charAt(0));
	this.refNCBI= row.getString("refNCBI");
	this.refUCSC= row.getString("refUCSC");
	this.observed= row.getString("observed");
	this.moltype= row.getString("moltype");
	this.clazz= row.getString("clazz");
	this.valid= row.getString("valid");
	this.avHet= row.getFloat("avHet");
	this.aHetSE= row.getFloat("aHetSE");
	this.func= row.getString("func");
	this.weight= row.getInt("weight");
	}

}
