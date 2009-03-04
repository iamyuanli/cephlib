package fr.cephb.lindenb.bio.ucsc.hg18;


/**
 * Hg18HapmapSnps
 * HapMap genotype summary"
 *
 * This file was automaticaly generated
 * DO NOT EDIT
 * 
 */
public class Hg18HapmapSnps
	{
	/** column Headers */
	public static final String COLUMNS[]=new String[]{
         "bin"
         ,"chrom"
         ,"chromStart"
         ,"chromEnd"
         ,"name"
         ,"score"
         ,"strand"
         ,"observed"
         ,"allele1"
         ,"homoCount1"
         ,"allele2"
         ,"homoCount2"
         ,"heteroCount"
 		};
	
    /**
	 * A tableModel for Swing
	 *
	 */
	public static class TableModel
		extends org.lindenb.swing.table.GenericTableModel<Hg18HapmapSnps>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public int getColumnCount() {
			return COLUMNS.length;
			}
		@Override
		public Object getValueOf(Hg18HapmapSnps object, int column) {
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
				 return Integer.class;
				                case 1:
				 return String.class;
				                case 2:
				 return Integer.class;
				                case 3:
				 return Integer.class;
				                case 4:
				 return String.class;
				                case 5:
				 return Integer.class;
				                case 6:
				 return Strand.class;
				                case 7:
				 return String.class;
				                case 8:
				 return Allele1.class;
				                case 9:
				 return Integer.class;
				                case 10:
				 return Allele2.class;
				                case 11:
				 return Integer.class;
				                case 12:
				 return Integer.class;
								default:break;
				}
			throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
			}
		}
	
	
                               /**
    * strand
    * Which genomic strand contains the observed alleles
    */     
    public static enum Strand
		{
        PLUS
        	{
        	@Override
        	public String getSQLName() { return "+"; }
        	}
        ,MINUS
        	{
        	@Override
        	public String getSQLName() { return "-"; }
        	}
        ,UNKNOWN
        	{
        	@Override
        	public String getSQLName() { return "?"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Strand */
    private static java.util.Map<String,Strand> _SQLNAME2Strand;
    /* static initialization of SQLNAME2Strand */
    static	{
		_SQLNAME2Strand=new java.util.HashMap<String,Strand>();
		for(Strand _x:Strand.values()) _SQLNAME2Strand.put(_x.getSQLName(),_x);
		}
	/** returns a  Strand from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Strand or null if not found
	 */
	public static Strand getStrand(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Strand.get(sqlName);
		}
	           /**
    * allele1
    * This allele has been observed
    */     
    public static enum Allele1
		{
        A
        	{
        	@Override
        	public String getSQLName() { return "A"; }
        	}
        ,C
        	{
        	@Override
        	public String getSQLName() { return "C"; }
        	}
        ,G
        	{
        	@Override
        	public String getSQLName() { return "G"; }
        	}
        ,T
        	{
        	@Override
        	public String getSQLName() { return "T"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Allele1 */
    private static java.util.Map<String,Allele1> _SQLNAME2Allele1;
    /* static initialization of SQLNAME2Allele1 */
    static	{
		_SQLNAME2Allele1=new java.util.HashMap<String,Allele1>();
		for(Allele1 _x:Allele1.values()) _SQLNAME2Allele1.put(_x.getSQLName(),_x);
		}
	/** returns a  Allele1 from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Allele1 or null if not found
	 */
	public static Allele1 getAllele1(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Allele1.get(sqlName);
		}
	           /**
    * allele2
    * This allele may not have been observed
    */     
    public static enum Allele2
		{
        C
        	{
        	@Override
        	public String getSQLName() { return "C"; }
        	}
        ,G
        	{
        	@Override
        	public String getSQLName() { return "G"; }
        	}
        ,T
        	{
        	@Override
        	public String getSQLName() { return "T"; }
        	}
        ,none
        	{
        	@Override
        	public String getSQLName() { return "none"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Allele2 */
    private static java.util.Map<String,Allele2> _SQLNAME2Allele2;
    /* static initialization of SQLNAME2Allele2 */
    static	{
		_SQLNAME2Allele2=new java.util.HashMap<String,Allele2>();
		for(Allele2 _x:Allele2.values()) _SQLNAME2Allele2.put(_x.getSQLName(),_x);
		}
	/** returns a  Allele2 from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Allele2 or null if not found
	 */
	public static Allele2 getAllele2(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Allele2.get(sqlName);
		}
	            	
		/** bin */
	private int _bin = 0 ;
		/** Chromosome */
	private String _chrom = "" ;
		/** Start position in chrom (0 based) */
	private int _chromStart = 0 ;
		/** End position in chrom (1 based) */
	private int _chromEnd = 0 ;
		/** Reference SNP identifier from dbSnp */
	private String _name = "" ;
		/** Minor allele frequency normalized (0-500) */
	private int _score = 0 ;
		/** Which genomic strand contains the observed alleles */
	private Strand _strand = Strand.UNKNOWN ;
		/** Observed string from genotype file */
	private String _observed = "" ;
		/** This allele has been observed */
	private Allele1 _allele1 = Allele1.A ;
		/** Count of individuals who are homozygous for allele1 */
	private int _homoCount1 = 0 ;
		/** This allele may not have been observed */
	private Allele2 _allele2 = Allele2.C ;
		/** Count of individuals who are homozygous for allele2 */
	private int _homoCount2 = 0 ;
		/** Count of individuals who are heterozygous */
	private int _heteroCount = 0 ;
	    /**
	 * Hg18HapmapSnps
	 * constructor
	 */
	public Hg18HapmapSnps()
		{
		}
	
    /**
	 * Hg18HapmapSnps
	 * constructor from sql
	 * @param row the current sql row
	 */
	public Hg18HapmapSnps(java.sql.ResultSet row) throws java.sql.SQLException
		{
		this._bin= row.getInt("bin");
		
		this._chrom= row.getString("chrom");

		this._chromStart= row.getInt("chromStart");
		
		this._chromEnd= row.getInt("chromEnd");
		
		this._name= row.getString("name");

		this._score= row.getInt("score");
		
		this._strand= getStrand(row.getString("strand"));

		this._observed= row.getString("observed");

		this._allele1= getAllele1(row.getString("allele1"));

		this._homoCount1= row.getInt("homoCount1");
		
		this._allele2= getAllele2(row.getString("allele2"));

		this._homoCount2= row.getInt("homoCount2");
		
		this._heteroCount= row.getInt("heteroCount");
		
		}	
	
	
		/**
	 * getter for bin
	 * bin
	 * @return the value of bin
	 */
	public  int getBin()
		{
		return this._bin;
		}
		/**
	 * getter for chrom
	 * Chromosome
	 * @return the value of chrom
	 */
	public  String getChrom()
		{
		return this._chrom;
		}
		/**
	 * getter for chromStart
	 * Start position in chrom (0 based)
	 * @return the value of chromStart
	 */
	public  int getChromStart()
		{
		return this._chromStart;
		}
		/**
	 * getter for chromEnd
	 * End position in chrom (1 based)
	 * @return the value of chromEnd
	 */
	public  int getChromEnd()
		{
		return this._chromEnd;
		}
		/**
	 * getter for name
	 * Reference SNP identifier from dbSnp
	 * @return the value of name
	 */
	public  String getName()
		{
		return this._name;
		}
		/**
	 * getter for score
	 * Minor allele frequency normalized (0-500)
	 * @return the value of score
	 */
	public  int getScore()
		{
		return this._score;
		}
		/**
	 * getter for strand
	 * Which genomic strand contains the observed alleles
	 * @return the value of strand
	 */
	public  Strand getStrand()
		{
		return this._strand;
		}
		/**
	 * getter for observed
	 * Observed string from genotype file
	 * @return the value of observed
	 */
	public  String getObserved()
		{
		return this._observed;
		}
		/**
	 * getter for allele1
	 * This allele has been observed
	 * @return the value of allele1
	 */
	public  Allele1 getAllele1()
		{
		return this._allele1;
		}
		/**
	 * getter for homoCount1
	 * Count of individuals who are homozygous for allele1
	 * @return the value of homoCount1
	 */
	public  int getHomoCount1()
		{
		return this._homoCount1;
		}
		/**
	 * getter for allele2
	 * This allele may not have been observed
	 * @return the value of allele2
	 */
	public  Allele2 getAllele2()
		{
		return this._allele2;
		}
		/**
	 * getter for homoCount2
	 * Count of individuals who are homozygous for allele2
	 * @return the value of homoCount2
	 */
	public  int getHomoCount2()
		{
		return this._homoCount2;
		}
		/**
	 * getter for heteroCount
	 * Count of individuals who are heterozygous
	 * @return the value of heteroCount
	 */
	public  int getHeteroCount()
		{
		return this._heteroCount;
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
		StringBuilder b=new StringBuilder("Hg18HapmapSnps{");
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
					    return getBin();
	 			        case 1:
					    return getChrom();
	 			        case 2:
					    return getChromStart();
	 			        case 3:
					    return getChromEnd();
	 			        case 4:
					    return getName();
	 			        case 5:
					    return getScore();
	 			        case 6:
					    return getStrand();
	 			        case 7:
					    return getObserved();
	 			        case 8:
					    return getAllele1();
	 			        case 9:
					    return getHomoCount1();
	 			        case 10:
					    return getAllele2();
	 			        case 11:
					    return getHomoCount2();
	 			        case 12:
					    return getHeteroCount();
	 			 		default:break;
 			}
 		throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
		}
	
	/**
	 * create a HapmapSnpsCEU from a SQL row
	 * @param row the resultset
	 */
	public static Hg18HapmapSnps create(java.sql.ResultSet row) throws java.sql.SQLException
		{
		return new Hg18HapmapSnps(row);
		}
		
		
		/**
	 * select one a Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18HapmapSnps found
	 */
	public static Hg18HapmapSnps selectOne(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18HapmapSnps _value=selectOneOrZero(row);
		if(_value==null) throw new  java.sql.SQLException("empty result");
		return _value;
		}
	
	/**
	 * select one or zero Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18HapmapSnps found or NULL
	 */
	public static Hg18HapmapSnps selectOneOrZero(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18HapmapSnps _value=null;
		while(row.next())
			{
			if(_value!=null) throw new  java.sql.SQLException("found twice");
			_value= new Hg18HapmapSnps(row);
			}
		return _value;
		}
	/**
	 * select all the results in a java.sql.ResultSet
	 * @param row the resultset
	 * @return a collection containing all the items
	 */
	public static java.util.Collection<Hg18HapmapSnps> select(java.sql.ResultSet row) throws java.sql.SQLException
		{
		java.util.ArrayList<Hg18HapmapSnps> _v=new java.util.ArrayList<Hg18HapmapSnps>();
		while(row.next())
			{
			_v.add(new Hg18HapmapSnps(row));
			}
		return _v;
		}	
		
	}