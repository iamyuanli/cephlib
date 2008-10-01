package fr.cephb.lindenb.ncbi;

import java.net.URL;

import org.lindenb.util.Cast;

public enum Database
	{
	pubmed
		{
		@Override
		public String getURI(int id)
			{
			return "http://www.ncbi.nlm.nih.gov/pubmed/"+id;
			}
		}
	,snp
		{
		@Override
		public String getURI(int id)
			{
			return "http://www.ncbi.nlm.nih.gov/SNP/snp_ref.cgi?rs="+id;
			}
		},
	omim
		{
		@Override
		public String getURI(int id) {
			return "http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id="+id;
			}
		} ,
	gene
		{
		@Override
		public String getURI(int id) {
			return "http://www.ncbi.nlm.nih.gov/sites/entrez?Db=gene&Cmd=ShowDetailView&TermToSearch="+id;
			} 
		},
	unigene
		{
		@Override
		public String getURI(int id) {
			return "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?UGID="+id;
			}
		} 
	;
	public abstract String  getURI(int id);
	public URL getURL(int id) { return Cast.URL.cast(getURI(id));}
	}