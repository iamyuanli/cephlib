package fr.cephb.lindenb.bio.ucsc.hg18;


/**
 * Hg18Snp129
 * Polymorphism data from dbSnp database or genotyping arrays"
 *
 * This file was automaticaly generated
 * DO NOT EDIT
 * 
 */
public class Hg18Snp129
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
         ,"refNCBI"
         ,"refUCSC"
         ,"observed"
         ,"molType"
         ,"class"
         ,"valid"
         ,"avHet"
         ,"avHetSE"
         ,"func"
         ,"locType"
         ,"weight"
 		};
	
    /**
	 * A tableModel for Swing
	 *
	 */
	public static class TableModel
		extends org.lindenb.swing.table.GenericTableModel<Hg18Snp129>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public int getColumnCount() {
			return COLUMNS.length;
			}
		@Override
		public Object getValueOf(Hg18Snp129 object, int column) {
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
				 return Short.class;
				                case 1:
				 return String.class;
				                case 2:
				 return Integer.class;
				                case 3:
				 return Integer.class;
				                case 4:
				 return String.class;
				                case 5:
				 return Short.class;
				                case 6:
				 return Strand.class;
				                case 7:
				 return String.class;
				                case 8:
				 return String.class;
				                case 9:
				 return String.class;
				                case 10:
				 return MolType.class;
				                case 11:
				 return Clazz.class;
				                case 12:
				 return java.util.Set.class;
				                case 13:
				 return Float.class;
				                case 14:
				 return Float.class;
				                case 15:
				 return java.util.Set.class;
				                case 16:
				 return LocType.class;
				                case 17:
				 return Integer.class;
								default:break;
				}
			throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
			}
		}
	
	
                               /**
    * strand
    * Which DNA strand contains the observed alleles
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
    * molType
    * Sample type from exemplar ss
    */     
    public static enum MolType
		{
        genomic
        	{
        	@Override
        	public String getSQLName() { return "genomic"; }
        	}
        ,cDNA
        	{
        	@Override
        	public String getSQLName() { return "cDNA"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 MolType */
    private static java.util.Map<String,MolType> _SQLNAME2MolType;
    /* static initialization of SQLNAME2MolType */
    static	{
		_SQLNAME2MolType=new java.util.HashMap<String,MolType>();
		for(MolType _x:MolType.values()) _SQLNAME2MolType.put(_x.getSQLName(),_x);
		}
	/** returns a  MolType from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as MolType or null if not found
	 */
	public static MolType getMolType(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2MolType.get(sqlName);
		}
	       /**
    * class
    * The class of variant (simple, insertion, deletion, range, etc.)
    */     
    public static enum Clazz
		{
        unknown
        	{
        	@Override
        	public String getSQLName() { return "unknown"; }
        	}
        ,single
        	{
        	@Override
        	public String getSQLName() { return "single"; }
        	}
        ,in_del
        	{
        	@Override
        	public String getSQLName() { return "in-del"; }
        	}
        ,het
        	{
        	@Override
        	public String getSQLName() { return "het"; }
        	}
        ,microsatellite
        	{
        	@Override
        	public String getSQLName() { return "microsatellite"; }
        	}
        ,named
        	{
        	@Override
        	public String getSQLName() { return "named"; }
        	}
        ,mixed
        	{
        	@Override
        	public String getSQLName() { return "mixed"; }
        	}
        ,mnp
        	{
        	@Override
        	public String getSQLName() { return "mnp"; }
        	}
        ,insertion
        	{
        	@Override
        	public String getSQLName() { return "insertion"; }
        	}
        ,deletion
        	{
        	@Override
        	public String getSQLName() { return "deletion"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Clazz */
    private static java.util.Map<String,Clazz> _SQLNAME2Clazz;
    /* static initialization of SQLNAME2Clazz */
    static	{
		_SQLNAME2Clazz=new java.util.HashMap<String,Clazz>();
		for(Clazz _x:Clazz.values()) _SQLNAME2Clazz.put(_x.getSQLName(),_x);
		}
	/** returns a  Clazz from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Clazz or null if not found
	 */
	public static Clazz getClazz(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Clazz.get(sqlName);
		}
	       /**
    * valid
    * The validation status of the SNP
    */     
    public static enum Valid
		{
        unknown
        	{
        	@Override
        	public String getSQLName() { return "unknown"; }
			@Override
            public int getIndex() { return 0; }
        	}
        ,by_cluster
        	{
        	@Override
        	public String getSQLName() { return "by-cluster"; }
			@Override
            public int getIndex() { return 1; }
        	}
        ,by_frequency
        	{
        	@Override
        	public String getSQLName() { return "by-frequency"; }
			@Override
            public int getIndex() { return 2; }
        	}
        ,by_submitter
        	{
        	@Override
        	public String getSQLName() { return "by-submitter"; }
			@Override
            public int getIndex() { return 3; }
        	}
        ,by_2hit_2allele
        	{
        	@Override
        	public String getSQLName() { return "by-2hit-2allele"; }
			@Override
            public int getIndex() { return 4; }
        	}
        ,by_hapmap
        	{
        	@Override
        	public String getSQLName() { return "by-hapmap"; }
			@Override
            public int getIndex() { return 5; }
        	}
        			;
            public abstract int getIndex();
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Valid */
    private static java.util.Map<String,Valid> _SQLNAME2Valid;
    /* static initialization of SQLNAME2Valid */
    static	{
		_SQLNAME2Valid=new java.util.HashMap<String,Valid>();
		for(Valid _x:Valid.values()) _SQLNAME2Valid.put(_x.getSQLName(),_x);
		}
	/** returns a  Valid from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Valid or null if not found
	 */
	public static Valid getValid(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Valid.get(sqlName);
		}
	               /**
    * func
    * The functional category of the SNP (coding-synon, coding-nonsynon, intron, etc.)
    */     
    public static enum Func
		{
        unknown
        	{
        	@Override
        	public String getSQLName() { return "unknown"; }
			@Override
            public int getIndex() { return 0; }
        	}
        ,coding_synon
        	{
        	@Override
        	public String getSQLName() { return "coding-synon"; }
			@Override
            public int getIndex() { return 1; }
        	}
        ,intron
        	{
        	@Override
        	public String getSQLName() { return "intron"; }
			@Override
            public int getIndex() { return 2; }
        	}
        ,cds_reference
        	{
        	@Override
        	public String getSQLName() { return "cds-reference"; }
			@Override
            public int getIndex() { return 3; }
        	}
        ,near_gene_3
        	{
        	@Override
        	public String getSQLName() { return "near-gene-3"; }
			@Override
            public int getIndex() { return 4; }
        	}
        ,near_gene_5
        	{
        	@Override
        	public String getSQLName() { return "near-gene-5"; }
			@Override
            public int getIndex() { return 5; }
        	}
        ,nonsense
        	{
        	@Override
        	public String getSQLName() { return "nonsense"; }
			@Override
            public int getIndex() { return 6; }
        	}
        ,missense
        	{
        	@Override
        	public String getSQLName() { return "missense"; }
			@Override
            public int getIndex() { return 7; }
        	}
        ,frameshift
        	{
        	@Override
        	public String getSQLName() { return "frameshift"; }
			@Override
            public int getIndex() { return 8; }
        	}
        ,untranslated_3
        	{
        	@Override
        	public String getSQLName() { return "untranslated-3"; }
			@Override
            public int getIndex() { return 9; }
        	}
        ,untranslated_5
        	{
        	@Override
        	public String getSQLName() { return "untranslated-5"; }
			@Override
            public int getIndex() { return 10; }
        	}
        ,splice_3
        	{
        	@Override
        	public String getSQLName() { return "splice-3"; }
			@Override
            public int getIndex() { return 11; }
        	}
        ,splice_5
        	{
        	@Override
        	public String getSQLName() { return "splice-5"; }
			@Override
            public int getIndex() { return 12; }
        	}
        			;
            public abstract int getIndex();
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 Func */
    private static java.util.Map<String,Func> _SQLNAME2Func;
    /* static initialization of SQLNAME2Func */
    static	{
		_SQLNAME2Func=new java.util.HashMap<String,Func>();
		for(Func _x:Func.values()) _SQLNAME2Func.put(_x.getSQLName(),_x);
		}
	/** returns a  Func from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as Func or null if not found
	 */
	public static Func getFunc(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2Func.get(sqlName);
		}
	       /**
    * locType
    * How the variant affects the reference sequence
    */     
    public static enum LocType
		{
        range
        	{
        	@Override
        	public String getSQLName() { return "range"; }
        	}
        ,exact
        	{
        	@Override
        	public String getSQLName() { return "exact"; }
        	}
        ,between
        	{
        	@Override
        	public String getSQLName() { return "between"; }
        	}
        ,rangeInsertion
        	{
        	@Override
        	public String getSQLName() { return "rangeInsertion"; }
        	}
        ,rangeSubstitution
        	{
        	@Override
        	public String getSQLName() { return "rangeSubstitution"; }
        	}
        ,rangeDeletion
        	{
        	@Override
        	public String getSQLName() { return "rangeDeletion"; }
        	}
        			;
        public abstract String getSQLName();
		}
    /* table mapping sqlname 2 LocType */
    private static java.util.Map<String,LocType> _SQLNAME2LocType;
    /* static initialization of SQLNAME2LocType */
    static	{
		_SQLNAME2LocType=new java.util.HashMap<String,LocType>();
		for(LocType _x:LocType.values()) _SQLNAME2LocType.put(_x.getSQLName(),_x);
		}
	/** returns a  LocType from its sql value
	 * @param sqlName the name as defined in mysqsql
	 * @return the value as LocType or null if not found
	 */
	public static LocType getLocType(String sqlName)
		{
		if(sqlName==null) return null;
		return _SQLNAME2LocType.get(sqlName);
		}
	        	
		/** bin */
	private short _bin = 0 ;
		/** Reference sequence chromosome or scaffold */
	private String _chrom = "" ;
		/** Start position in chrom */
	private int _chromStart = 0 ;
		/** End position in chrom */
	private int _chromEnd = 0 ;
		/** Reference SNP identifier or Affy SNP name */
	private String _name = "" ;
		/** Not used */
	private short _score = 0 ;
		/** Which DNA strand contains the observed alleles */
	private Strand _strand = null ;
		/** Reference genomic from dbSNP */
	private String _refNCBI = "" ;
		/** Reference genomic from nib lookup */
	private String _refUCSC = "" ;
		/** The sequences of the observed alleles from rs-fasta files */
	private String _observed = "" ;
		/** Sample type from exemplar ss */
	private MolType _molType = null ;
		/** The class of variant (simple, insertion, deletion, range, etc.) */
	private Clazz _class = Clazz.unknown ;
		/** The validation status of the SNP */
	private boolean[] _valid=new boolean[6];
		/** The average heterozygosity from all observations */
	private float _avHet = 0 ;
		/** The Standard Error for the average heterozygosity */
	private float _avHetSE = 0 ;
		/** The functional category of the SNP (coding-synon, coding-nonsynon, intron, etc.) */
	private boolean[] _func=new boolean[13];
		/** How the variant affects the reference sequence */
	private LocType _locType = null ;
		/** The quality of the alignment */
	private int _weight = 0 ;
	    /**
	 * Hg18Snp129
	 * constructor
	 */
	public Hg18Snp129()
		{
		}
	
    /**
	 * Hg18Snp129
	 * constructor from sql
	 * @param row the current sql row
	 */
	public Hg18Snp129(java.sql.ResultSet row) throws java.sql.SQLException
		{
		this._bin= row.getShort("bin");
		
		this._chrom= row.getString("chrom");

		this._chromStart= row.getInt("chromStart");
		
		this._chromEnd= row.getInt("chromEnd");
		
		this._name= row.getString("name");

		this._score= row.getShort("score");
		
		this._strand= getStrand(row.getString("strand"));

	try
		{
		this._refNCBI = org.lindenb.io.IOUtils.getReaderContent(row.getCharacterStream("refNCBI"));
		}
	catch(java.io.IOException _err)
		{
		throw new java.sql.SQLException(_err);
		}

	try
		{
		this._refUCSC = org.lindenb.io.IOUtils.getReaderContent(row.getCharacterStream("refUCSC"));
		}
	catch(java.io.IOException _err)
		{
		throw new java.sql.SQLException(_err);
		}

		this._observed= row.getString("observed");

		this._molType= getMolType(row.getString("molType"));

		this._class= getClazz(row.getString("class"));


		for(String _s: row.getString("valid").split(","))
			{
        	this._valid[getValid(_s).getIndex()]=true;
        	}

		this._avHet= row.getFloat("avHet");
		
		this._avHetSE= row.getFloat("avHetSE");
		

		for(String _s: row.getString("func").split(","))
			{
        	this._func[getFunc(_s).getIndex()]=true;
        	}

		this._locType= getLocType(row.getString("locType"));

		this._weight= row.getInt("weight");
		
		}	
	
	
		/**
	 * getter for bin
	 * bin
	 * @return the value of bin
	 */
	public  short getBin()
		{
		return this._bin;
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
	 * getter for chromStart
	 * Start position in chrom
	 * @return the value of chromStart
	 */
	public  int getChromStart()
		{
		return this._chromStart;
		}
		/**
	 * getter for chromEnd
	 * End position in chrom
	 * @return the value of chromEnd
	 */
	public  int getChromEnd()
		{
		return this._chromEnd;
		}
		/**
	 * getter for name
	 * Reference SNP identifier or Affy SNP name
	 * @return the value of name
	 */
	public  String getName()
		{
		return this._name;
		}
		/**
	 * getter for score
	 * Not used
	 * @return the value of score
	 */
	public  short getScore()
		{
		return this._score;
		}
		/**
	 * getter for strand
	 * Which DNA strand contains the observed alleles
	 * @return the value of strand
	 */
	public  Strand getStrand()
		{
		return this._strand;
		}
		/**
	 * getter for refNCBI
	 * Reference genomic from dbSNP
	 * @return the value of refNCBI
	 */
	public  String getRefNCBI()
		{
		return this._refNCBI;
		}
		/**
	 * getter for refUCSC
	 * Reference genomic from nib lookup
	 * @return the value of refUCSC
	 */
	public  String getRefUCSC()
		{
		return this._refUCSC;
		}
		/**
	 * getter for observed
	 * The sequences of the observed alleles from rs-fasta files
	 * @return the value of observed
	 */
	public  String getObserved()
		{
		return this._observed;
		}
		/**
	 * getter for molType
	 * Sample type from exemplar ss
	 * @return the value of molType
	 */
	public  MolType getMolType()
		{
		return this._molType;
		}
		/**
	 * getter for class
	 * The class of variant (simple, insertion, deletion, range, etc.)
	 * @return the value of class
	 */
	public  Clazz getClazz()
		{
		return this._class;
		}
		/**
	 * test if a valid is set
	 * @param valid the field to test
	 * @return true if the field was set
	 */
	public boolean isValidSet(Valid valid)
		{
		return this._valid[ valid.getIndex() ];
		}
	
	/**
	 * @return all the Valid
	 */
	public java.util.Set<Valid> getValids()
		{
		java.util.Set<Valid> _set=new java.util.HashSet<Valid>();
		for(Valid _item : Valid.values())
			{
			if(isValidSet(_item))
				{
				_set.add(_item);
				}
			}
		return _set;
		}
		/**
	 * getter for avHet
	 * The average heterozygosity from all observations
	 * @return the value of avHet
	 */
	public  float getAvHet()
		{
		return this._avHet;
		}
		/**
	 * getter for avHetSE
	 * The Standard Error for the average heterozygosity
	 * @return the value of avHetSE
	 */
	public  float getAvHetSE()
		{
		return this._avHetSE;
		}
		/**
	 * test if a func is set
	 * @param func the field to test
	 * @return true if the field was set
	 */
	public boolean isFuncSet(Func func)
		{
		return this._func[ func.getIndex() ];
		}
	
	/**
	 * @return all the Func
	 */
	public java.util.Set<Func> getFuncs()
		{
		java.util.Set<Func> _set=new java.util.HashSet<Func>();
		for(Func _item : Func.values())
			{
			if(isFuncSet(_item))
				{
				_set.add(_item);
				}
			}
		return _set;
		}
		/**
	 * getter for locType
	 * How the variant affects the reference sequence
	 * @return the value of locType
	 */
	public  LocType getLocType()
		{
		return this._locType;
		}
		/**
	 * getter for weight
	 * The quality of the alignment
	 * @return the value of weight
	 */
	public  int getWeight()
		{
		return this._weight;
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
		StringBuilder b=new StringBuilder("Hg18Snp129{");
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
					    return getRefNCBI();
	 			        case 8:
					    return getRefUCSC();
	 			        case 9:
					    return getObserved();
	 			        case 10:
					    return getMolType();
	 			        case 11:
					    return getClazz();
	 			        case 12:
					    return getValids();
	 			        case 13:
					    return getAvHet();
	 			        case 14:
					    return getAvHetSE();
	 			        case 15:
					    return getFuncs();
	 			        case 16:
					    return getLocType();
	 			        case 17:
					    return getWeight();
	 			 		default:break;
 			}
 		throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
		}
	
	/**
	 * create a Snp129 from a SQL row
	 * @param row the resultset
	 */
	public static Hg18Snp129 create(java.sql.ResultSet row) throws java.sql.SQLException
		{
		return new Hg18Snp129(row);
		}
		
		
		/**
	 * select one a Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18Snp129 found
	 */
	public static Hg18Snp129 selectOne(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18Snp129 _value=selectOneOrZero(row);
		if(_value==null) throw new  java.sql.SQLException("empty result");
		return _value;
		}
	
	/**
	 * select one or zero Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18Snp129 found or NULL
	 */
	public static Hg18Snp129 selectOneOrZero(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18Snp129 _value=null;
		while(row.next())
			{
			if(_value!=null) throw new  java.sql.SQLException("found twice");
			_value= new Hg18Snp129(row);
			}
		return _value;
		}
	/**
	 * select all the results in a java.sql.ResultSet
	 * @param row the resultset
	 * @return a collection containing all the items
	 */
	public static java.util.Collection<Hg18Snp129> select(java.sql.ResultSet row) throws java.sql.SQLException
		{
		java.util.ArrayList<Hg18Snp129> _v=new java.util.ArrayList<Hg18Snp129>();
		while(row.next())
			{
			_v.add(new Hg18Snp129(row));
			}
		return _v;
		}	
		
	}