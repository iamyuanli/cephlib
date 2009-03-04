package fr.cephb.lindenb.bio.ucsc.hg18;


/**
 * Hg18KnownGene
 * Genes based on RefSeq, GenBank, and UniProt."
 *
 * This file was automaticaly generated
 * DO NOT EDIT
 * 
 */
public class Hg18KnownGene
	{
	/** column Headers */
	public static final String COLUMNS[]=new String[]{
         "name"
         ,"chrom"
         ,"strand"
         ,"txStart"
         ,"txEnd"
         ,"cdsStart"
         ,"cdsEnd"
         ,"exonCount"
         ,"exonStarts"
         ,"exonEnds"
         ,"proteinID"
         ,"alignID"
 		};
	
    /**
	 * A tableModel for Swing
	 *
	 */
	public static class TableModel
		extends org.lindenb.swing.table.GenericTableModel<Hg18KnownGene>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public int getColumnCount() {
			return COLUMNS.length;
			}
		@Override
		public Object getValueOf(Hg18KnownGene object, int column) {
			return object.getValueAt(column);
			}
		@Override
		public String getColumnName(int column) {
			return COLUMNS[column];
			}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex)
				{
                case 0:
				 return String.class;
				                case 1:
				 return String.class;
				                case 2:
				  return Character.class;
                case 3:
				 return Integer.class;
				                case 4:
				 return Integer.class;
				                case 5:
				 return Integer.class;
				                case 6:
				 return Integer.class;
				                case 7:
				 return Integer.class;
				                case 8:
				 return String.class;
				                case 9:
				 return String.class;
				                case 10:
				 return String.class;
				                case 11:
				 return String.class;
								default:break;
				}
			throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
			}
		}
	
	
                                                    	
		/** Name of gene */
	private String _name = "" ;
		/** Reference sequence chromosome or scaffold */
	private String _chrom = "" ;
		/** + or - for strand */
	private char _strand = ' ' ;
		/** Transcription start position */
	private int _txStart = 0 ;
		/** Transcription end position */
	private int _txEnd = 0 ;
		/** Coding region start */
	private int _cdsStart = 0 ;
		/** Coding region end */
	private int _cdsEnd = 0 ;
		/** Number of exons */
	private int _exonCount = 0 ;
		/** Exon start positions */
	private String _exonStarts = "" ;
		/** Exon end positions */
	private String _exonEnds = "" ;
		/** UniProt display ID for Known Genes,  UniProt accession or RefSeq protein ID for UCSC Genes */
	private String _proteinID = "" ;
		/** Unique identifier for each (known gene, alignment position) pair */
	private String _alignID = "" ;
	    /**
	 * Hg18KnownGene
	 * constructor
	 */
	public Hg18KnownGene()
		{
		}
	
    /**
	 * Hg18KnownGene
	 * constructor from sql
	 * @param row the current sql row
	 */
	public Hg18KnownGene(java.sql.ResultSet row) throws java.sql.SQLException
		{
		this._name= row.getString("name");

		this._chrom= row.getString("chrom");

		this._strand= row.getString("strand").charAt(0);

		this._txStart= row.getInt("txStart");
		
		this._txEnd= row.getInt("txEnd");
		
		this._cdsStart= row.getInt("cdsStart");
		
		this._cdsEnd= row.getInt("cdsEnd");
		
		this._exonCount= row.getInt("exonCount");
		
	try
		{
		this._exonStarts = org.lindenb.io.IOUtils.getReaderContent(row.getCharacterStream("exonStarts"));
		}
	catch(java.io.IOException _err)
		{
		throw new java.sql.SQLException(_err);
		}

	try
		{
		this._exonEnds = org.lindenb.io.IOUtils.getReaderContent(row.getCharacterStream("exonEnds"));
		}
	catch(java.io.IOException _err)
		{
		throw new java.sql.SQLException(_err);
		}

		this._proteinID= row.getString("proteinID");

		this._alignID= row.getString("alignID");

		}	
	
	
		/**
	 * getter for name
	 * Name of gene
	 * @return the value of name
	 */
	public  String getName()
		{
		return this._name;
		}
		/**
	 * getter for chrom
	 * Reference sequence chromosome or scaffold
	 * @return the value of chrom
	 */
	public  String getChrom()
		{
		return this._chrom;
		}
		/**
	 * getter for strand
	 * + or - for strand
	 * @return the value of strand
	 */
	public  char getStrand()
		{
		return this._strand;
		}
		/**
	 * getter for txStart
	 * Transcription start position
	 * @return the value of txStart
	 */
	public  int getTxStart()
		{
		return this._txStart;
		}
		/**
	 * getter for txEnd
	 * Transcription end position
	 * @return the value of txEnd
	 */
	public  int getTxEnd()
		{
		return this._txEnd;
		}
		/**
	 * getter for cdsStart
	 * Coding region start
	 * @return the value of cdsStart
	 */
	public  int getCdsStart()
		{
		return this._cdsStart;
		}
		/**
	 * getter for cdsEnd
	 * Coding region end
	 * @return the value of cdsEnd
	 */
	public  int getCdsEnd()
		{
		return this._cdsEnd;
		}
		/**
	 * getter for exonCount
	 * Number of exons
	 * @return the value of exonCount
	 */
	public  int getExonCount()
		{
		return this._exonCount;
		}
		/**
	 * getter for exonStarts
	 * Exon start positions
	 * @return the value of exonStarts
	 */
	public  String getExonStarts()
		{
		return this._exonStarts;
		}
		/**
	 * getter for exonEnds
	 * Exon end positions
	 * @return the value of exonEnds
	 */
	public  String getExonEnds()
		{
		return this._exonEnds;
		}
		/**
	 * getter for proteinID
	 * UniProt display ID for Known Genes,  UniProt accession or RefSeq protein ID for UCSC Genes
	 * @return the value of proteinID
	 */
	public  String getProteinID()
		{
		return this._proteinID;
		}
		/**
	 * getter for alignID
	 * Unique identifier for each (known gene, alignment position) pair
	 * @return the value of alignID
	 */
	public  String getAlignID()
		{
		return this._alignID;
		}
	
    /**
     * print this object to a stream
     * @param out the stream
     */
	public void print(java.io.PrintWriter out)
		{
		for(int i=0;i< COLUMNS.length;++i)
			{
			if(i >0)out.print('\t');
			out.print(String.valueOf(getValueAt(i)));
			}
		}

    /**
     * print this object to a stream and add a CR at the end
     * @param out the stream
     */	
	public void println(java.io.PrintWriter out)
		{
		print(out);out.println();
		}
		
    /**
     * print this object to a stream
     * @param out the stream
     */
	public void print(java.io.PrintStream out)
		{
		for(int i=0;i< COLUMNS.length;++i)
			{
			if(i >0)out.print('\t');
			out.print(String.valueOf(getValueAt(i)));
			}
		}

    /**
     * print this object to a stream and add a CR at the end
     * @param out the stream
     */	
	public void println(java.io.PrintStream out)
		{
		print(out);out.println();
		}

	
	@Override
	public String toString()
		{
		StringBuilder b=new StringBuilder("Hg18KnownGene{");
		for(int i=0;i< COLUMNS.length;++i)
			{
			if(i >0) b.append(',');
			b.append(COLUMNS[i]).append(":").
			  append(String.valueOf(getValueAt(i)));
			}
		b.append("\n}");
		return b.toString();
		}
	
	/**
	 * return a value at given column
	 * function should be used in, e.g. , in a TableModel
	 * @param columnIndex the column index
	 * @return value at given index
	 */
	public Object getValueAt(int columnIndex)
		{
		switch(columnIndex)
			{
				        case 0:
					    return getName();
	 			        case 1:
					    return getChrom();
	 			        case 2:
					    return getStrand();
	 			        case 3:
					    return getTxStart();
	 			        case 4:
					    return getTxEnd();
	 			        case 5:
					    return getCdsStart();
	 			        case 6:
					    return getCdsEnd();
	 			        case 7:
					    return getExonCount();
	 			        case 8:
					    return getExonStarts();
	 			        case 9:
					    return getExonEnds();
	 			        case 10:
					    return getProteinID();
	 			        case 11:
					    return getAlignID();
	 			 		default:break;
 			}
 		throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
		}
	
	/**
	 * create a KnownGene from a SQL row
	 * @param row the resultset
	 */
	public static Hg18KnownGene create(java.sql.ResultSet row) throws java.sql.SQLException
		{
		return new Hg18KnownGene(row);
		}
		
		
		/**
	 * select one a Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18KnownGene found
	 */
	public static Hg18KnownGene selectOne(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18KnownGene _value=selectOneOrZero(row);
		if(_value==null) throw new  java.sql.SQLException("empty result");
		return _value;
		}
	
	/**
	 * select one or zero Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18KnownGene found or NULL
	 */
	public static Hg18KnownGene selectOneOrZero(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18KnownGene _value=null;
		while(row.next())
			{
			if(_value!=null) throw new  java.sql.SQLException("found twice");
			_value= new Hg18KnownGene(row);
			}
		return _value;
		}
	/**
	 * select all the results in a java.sql.ResultSet
	 * @param row the resultset
	 * @return a collection containing all the items
	 */
	public static java.util.Collection<Hg18KnownGene> select(java.sql.ResultSet row) throws java.sql.SQLException
		{
		java.util.ArrayList<Hg18KnownGene> _v=new java.util.ArrayList<Hg18KnownGene>();
		while(row.next())
			{
			_v.add(new Hg18KnownGene(row));
			}
		return _v;
		}	
		
	}