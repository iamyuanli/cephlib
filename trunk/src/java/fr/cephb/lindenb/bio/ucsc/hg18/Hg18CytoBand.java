package fr.cephb.lindenb.bio.ucsc.hg18;


/**
 * Hg18CytoBand
 * Describes the positions of cytogenetic bands with a chromosome"
 *
 * This file was automaticaly generated
 * DO NOT EDIT
 * 
 */
public class Hg18CytoBand
	{
	/** column Headers */
	public static final String COLUMNS[]=new String[]{
         "chrom"
         ,"chromStart"
         ,"chromEnd"
         ,"name"
         ,"gieStain"
 		};
	
    /**
	 * A tableModel for Swing
	 *
	 */
	public static class TableModel
		extends org.lindenb.swing.table.GenericTableModel<Hg18CytoBand>
		{
		private static final long serialVersionUID = 1L;
		@Override
		public int getColumnCount() {
			return COLUMNS.length;
			}
		@Override
		public Object getValueOf(Hg18CytoBand object, int column) {
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
				 return Integer.class;
				                case 2:
				 return Integer.class;
				                case 3:
				 return String.class;
				                case 4:
				 return String.class;
								default:break;
				}
			throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
			}
		}
	
	
                        	
		/** Reference sequence chromosome or scaffold */
	private String _chrom = "" ;
		/** Start position in genoSeq */
	private int _chromStart = 0 ;
		/** End position in genoSeq */
	private int _chromEnd = 0 ;
		/** Name of cytogenetic band */
	private String _name = "" ;
		/** Giemsa stain results */
	private String _gieStain = "" ;
	    /**
	 * Hg18CytoBand
	 * constructor
	 */
	public Hg18CytoBand()
		{
		}
	
    /**
	 * Hg18CytoBand
	 * constructor from sql
	 * @param row the current sql row
	 */
	public Hg18CytoBand(java.sql.ResultSet row) throws java.sql.SQLException
		{
		this._chrom= row.getString("chrom");

		this._chromStart= row.getInt("chromStart");
		
		this._chromEnd= row.getInt("chromEnd");
		
		this._name= row.getString("name");

		this._gieStain= row.getString("gieStain");

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
	 * Start position in genoSeq
	 * @return the value of chromStart
	 */
	public  int getChromStart()
		{
		return this._chromStart;
		}
		/**
	 * getter for chromEnd
	 * End position in genoSeq
	 * @return the value of chromEnd
	 */
	public  int getChromEnd()
		{
		return this._chromEnd;
		}
		/**
	 * getter for name
	 * Name of cytogenetic band
	 * @return the value of name
	 */
	public  String getName()
		{
		return this._name;
		}
		/**
	 * getter for gieStain
	 * Giemsa stain results
	 * @return the value of gieStain
	 */
	public  String getGieStain()
		{
		return this._gieStain;
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
		StringBuilder b=new StringBuilder("Hg18CytoBand{");
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
					    return getChrom();
	 			        case 1:
					    return getChromStart();
	 			        case 2:
					    return getChromEnd();
	 			        case 3:
					    return getName();
	 			        case 4:
					    return getGieStain();
	 			 		default:break;
 			}
 		throw new java.lang.IndexOutOfBoundsException(""+columnIndex);	
		}
	
	/**
	 * create a CytoBand from a SQL row
	 * @param row the resultset
	 */
	public static Hg18CytoBand create(java.sql.ResultSet row) throws java.sql.SQLException
		{
		return new Hg18CytoBand(row);
		}
		
		
		/**
	 * select one a Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18CytoBand found
	 */
	public static Hg18CytoBand selectOne(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18CytoBand _value=selectOneOrZero(row);
		if(_value==null) throw new  java.sql.SQLException("empty result");
		return _value;
		}
	
	/**
	 * select one or zero Snp129 from java.sql.ResultSet
	 * @param row the resultset
	 * @return the Hg18CytoBand found or NULL
	 */
	public static Hg18CytoBand selectOneOrZero(java.sql.ResultSet row) throws java.sql.SQLException
		{
		Hg18CytoBand _value=null;
		while(row.next())
			{
			if(_value!=null) throw new  java.sql.SQLException("found twice");
			_value= new Hg18CytoBand(row);
			}
		return _value;
		}
	/**
	 * select all the results in a java.sql.ResultSet
	 * @param row the resultset
	 * @return a collection containing all the items
	 */
	public static java.util.Collection<Hg18CytoBand> select(java.sql.ResultSet row) throws java.sql.SQLException
		{
		java.util.ArrayList<Hg18CytoBand> _v=new java.util.ArrayList<Hg18CytoBand>();
		while(row.next())
			{
			_v.add(new Hg18CytoBand(row));
			}
		return _v;
		}	
		
	}