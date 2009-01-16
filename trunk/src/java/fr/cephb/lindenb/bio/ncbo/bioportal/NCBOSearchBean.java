package fr.cephb.lindenb.bio.ncbo.bioportal;

public interface NCBOSearchBean
	{
	public int getOntologyVersionId();
	public int getOntologyId();
	public String getOntologyLabel();
	public String getRecordType();
	public String getConceptId();
	public String getConceptIdShort();
	public String getPreferredName();
	public String getContents();
	}
