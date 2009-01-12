package org.lindenb.knime.plugins.linkage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.knime.plugins.MyNodeModel;
import org.lindenb.knime.plugins.berkeley.Berkeley;
import org.lindenb.lang.IllegalInputException;

import com.sleepycat.bind.tuple.BooleanBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;

public class LinkageNodeModel
	extends MyNodeModel
	{
	private Pattern TAB= Pattern.compile("[\t]");
	private Pattern GENOTYPE_PATTERN= Pattern.compile("[0-9]+[ ][0-9]+");
	private long ID_GENERATOR = System.currentTimeMillis();
	
	/** Marker */
	public static class Marker
		{
		private String name;
		private int index;
		private boolean ignore=false;
		//private Map<Genotype, Integer> genotypes=new HashMap<Genotype, Integer>();
		
		public String getName() {
			return name;
			}
		
		public int getIndex() {
			return index;
			}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Marker other = (Marker) obj;
			if (index != other.index)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getName();
			}
		}
	
	/** MarkerBinding */
	private static class MarkerBinding extends TupleBinding<Marker>
		{
		@Override
		public Marker entryToObject(TupleInput in)
			{
			Marker m= new Marker();
			m.index= in.readInt();
			m.name= in.readString();
			m.ignore= in.readBoolean();
			/*int n= in.readInt();
			for(int i=0;i<n;++i)
				{
				Genotype g= new Genotype(in.readShort(),in.readShort());
				m.genotypes.put(g, in.readInt());
				}*/
			return m;
			}
		@Override
		public void objectToEntry(Marker o, TupleOutput out)
			{
			out.writeInt(o.index);
			out.writeString(o.name);
			out.writeBoolean(o.ignore);
			/*int n= o.genotypes.size();
			for(Genotype g:o.genotypes.keySet())
				{
				out.writeShort(g.a);
				out.writeShort(g.b);
				out.writeInt(o.genotypes.get(g));
				}*/
			}
		};
		
	
	/** genotype */
	private static class Genotype
		{
		Genotype(short a,short b)
			{
			if(a<=b)
				{
				this.a=a;
				this.b=b;
				}
			else
				{
				this.a=b;
				this.b=a;
				}
			}

		
		boolean isNull()
			{
			return a==0 || b==0;
			}
		
		@Override
		public int hashCode()
			{
			final int prime = 31;
			int result = 1;
			result = prime * result + a;
			result = prime * result + b;
			return result;
			}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Genotype other = (Genotype) obj;
			if (a != other.a)
				return false;
			if (b != other.b)
				return false;
			return true;
		}


		public short A(int index) { return index==0?A1():A2();}
		public short A1() { return this.a;}
		public short A2() { return this.b;}
		@Override
		public String toString() {
			return ""+A1()+"-"+A2();
			}
		
		private short a;
		private short b;
		
		
		}
	
	
	/** Name */
	private static class Name
		{
		protected String family;
		protected String name;
		
		
		
		public Name(String family,String name)
			{
			this.family=family;
			this.name=name;
			}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((family == null) ? 0 : family.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
			}
		
		@Override
		public boolean equals(Object obj)
			{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Name other = (Name) obj;
			if (family == null) {
				if (other.family != null)
					return false;
			} else if (!family.equals(other.family))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
			}
		
		public String getName() {
			return name;
			}
		
		public String getFamily() {
			return family;
			}
		
		
		@Override
		public String toString() {
			return this.family+" "+this.name;
			}
		}
	
	/** NameBinding */
	static class NameBinding extends TupleBinding<Name>
			{
			@Override
			public Name entryToObject(TupleInput in) {
			return new Name(in.readString(),in.readString());
			}
			@Override
			public void objectToEntry(Name o, TupleOutput out) {
				out.writeString(o.family);
				out.writeString(o.name);
			}
		};
	
	/** individual */
	static class Individual
		extends Name
		{
		public Individual(String family,String name)
			{
			super(family,name);
			}
		private String father;
		private String mother;
		private Integer gender;
		private ArrayList<Integer> statuses=new ArrayList<Integer>();
		private ArrayList<Genotype> genotypes= new ArrayList<Genotype>();
		}
	
	/** individual */
	static private class IndividualBinding
		extends TupleBinding<Individual>
		{
		@Override
		public Individual entryToObject(TupleInput in)
			{
			Individual m= new Individual(in.readString(),in.readString());
			m.father=in.readString();
			m.mother=in.readString();
			m.gender= in.readInt();
			int nstatus= in.readInt();
			m.statuses= new ArrayList<Integer>(nstatus);
			for(int i=0;i< nstatus;++i)
				{
				m.statuses.add(in.readInt());
				}
			int ngenotypes= in.readInt();
			m.genotypes= new ArrayList<Genotype>(ngenotypes);
			for(int i=0;i< ngenotypes;++i)
				{
				m.genotypes.add(new Genotype(in.readShort(),in.readShort()));
				}
			return m;
			}
		@Override
		public void objectToEntry(Individual o, TupleOutput out)
			{
			out.writeString(o.family);
			out.writeString(o.name);
			out.writeString(o.father);
			out.writeString(o.mother);
			out.writeInt(o.gender);
			out.writeInt(o.statuses.size());
			for(Integer i: o.statuses) out.writeInt(i);
			out.writeInt(o.genotypes.size());
			for(Genotype g: o.genotypes)
				{
				out.writeShort(g.a);
				out.writeShort(g.b);
				}
			}
		};
	
		
	private static final Genotype GENOTYPE_11= new Genotype((short)1,(short)1);
	private static final Genotype GENOTYPE_12= new Genotype((short)1,(short)2);
	private static final Genotype GENOTYPE_22= new Genotype((short)2,(short)2);
	
	/**
	 * MarkerInfo
	 * @author lindenb
	 * 
	 */
	private static class MarkerInfo
		{
		private HashMap<Genotype, Integer> genotype2count=new HashMap<Genotype, Integer>();
		private int genotyped=0;
		
		@Override
		public String toString() {
			return genotype2count.toString();
			}
		
		public double chi2()
			{
			//System.err.println(genotype2count+" "+genotyped);
			if(genotype2count.isEmpty() || genotype2count.size()>3) return -1;

			Integer nAA= genotype2count.get(GENOTYPE_11);
			if(nAA==null) nAA=0;
			Integer nAB= genotype2count.get(GENOTYPE_12);
			if(nAB==null) nAB=0;
			Integer nBB= genotype2count.get(GENOTYPE_22);
			if(nBB==null) nBB=0;
			double N = nAA+nAB+nBB;
			
			double p= (double)(2*nAA+nAB)/(double)(2*N);
			double q= 1.0-p;
			double expectAA=Math.pow(p,2)*(double)(N);
			double expectAB=(2.0*p*q)*(double)(N);
			double expectBB= Math.pow(q,2)*(double)(N);
			return 					
					(Math.pow(((double)(nAA)-expectAA),2))/expectAA
				+	(Math.pow(((double)(nAB)-expectAB),2))/expectAB
				+	(Math.pow(((double)(nBB)-expectBB),2))/expectBB
				;
			}
		
	
		}
		
		
	static final String CFGKEY_FILENAME = "linkage.file.input";
	final private  SettingsModelString m_filename = new SettingsModelString(CFGKEY_FILENAME,"");

	
	
	public LinkageNodeModel()
		{
		super(2,2);
		addSettings(m_filename);
		}
	
	
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception
		{
		if(this.m_filename.getStringValue()==null || this.m_filename.getStringValue().trim().length()==0)
			{
			throw new InvalidSettingsException(CFGKEY_FILENAME+" misssing");
			}
		File linkageFile= new File(this.m_filename.getStringValue());
		if(!linkageFile.exists())
			{
			throw new FileNotFoundException(this.m_filename.getStringValue());
			}
		BufferedReader r=null;
		SingleMapDatabase.INTEGER<Marker> markersDB=null;
		SingleMapDatabase.STRING<Integer> marker2index=null;
		SingleMapDatabase.STRING<Boolean> excludedMarkers=null;
		SingleMapDatabase.INTEGER<Name> index2name =null;
		SingleMapDatabase<Name,Individual> individualsDB=null;
		SingleMapDatabase<Name,Boolean> excludeIndividuals=null;
		Environment env=null;
		
		try
		{
		env = Berkeley.pushInstance();
		
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setExclusiveCreate(true);
		dbcfg.setReadOnly(false);
		
		dbcfg.setTemporary(true);
		markersDB=new SingleMapDatabase.INTEGER<Marker>(
				env.openDatabase(null, "m"+(++ID_GENERATOR), dbcfg),
				new MarkerBinding()
				);
		
		marker2index= new SingleMapDatabase.STRING<Integer>(
				env.openDatabase(null, "midx"+(++ID_GENERATOR), dbcfg),
				new IntegerBinding()
				);
		index2name =new SingleMapDatabase.INTEGER<Name>(
				env.openDatabase(null, "iidex"+(++ID_GENERATOR), dbcfg),
				new NameBinding()
			);
		
		individualsDB=new SingleMapDatabase<Name,Individual>(
				env.openDatabase(null, "indi"+(++ID_GENERATOR), dbcfg),
				new NameBinding(),
				new IndividualBinding()
			);
		
		
		
		
		//build excluded markers if needed
		if(inData!=null && inData.length>0 && inData[0]!=null)
			{
			excludedMarkers=new  SingleMapDatabase.STRING<Boolean>(
					env.openDatabase(null, "mex"+(++ID_GENERATOR), dbcfg),
					new BooleanBinding()
					);
			
			BufferedDataTable exclude= BufferedDataTable.class.cast(inData[0]);
			for(DataRow row:exclude)
				{
				if(row.getNumCells()<1) throw new IllegalInputException("Expected at least one column to exclude markers !");
				DataCell cell=row.getCell(0);
				if(!(cell instanceof StringCell))  throw new IllegalInputException("Expected a StringCell but found "+cell.getClass());
				excludedMarkers.put(StringCell.class.cast(cell).getStringValue(), true);
				}
			}
		
		//build excluded individuals if needed
		if(inData!=null && inData.length>1 && inData[1]!=null)
			{
			excludeIndividuals=new  SingleMapDatabase<Name,Boolean>(
					env.openDatabase(null, "indiex"+(++ID_GENERATOR), dbcfg),
					new NameBinding(),
					new BooleanBinding()
					);
			
			BufferedDataTable exclude= BufferedDataTable.class.cast(inData[1]);
			for(DataRow row:exclude)
				{
				if(row.getNumCells()<2) throw new IllegalInputException("Expected at least one column to exclude individuals !");
				DataCell fam=row.getCell(0);
				if(!(fam instanceof StringCell))  throw new IllegalInputException("Expected a StringCell for Family but found "+fam.getClass());
				DataCell indi=row.getCell(1);
				if(!(indi instanceof StringCell))  throw new IllegalInputException("Expected a StringCell for Individual but found "+indi.getClass());
				excludeIndividuals.put(
						new Name(StringCell.class.cast(fam).getStringValue(),StringCell.class.cast(indi).getStringValue())
						, true);
				}
			}
		
		
		if(linkageFile.getName().endsWith(".txt.gz"))
			{
			r= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(linkageFile))));
			}
		else
			{
			r= new BufferedReader(new FileReader(linkageFile));
			}
		//first Line contains markers
		
		int nMarkers=0;
		int nMarkerExcluded=0;
		String line= r.readLine();
		if(line==null) throw new IOException("First line missing");
		String tokens[]=TAB.split(line);
		for(String s:tokens)
			{
			Marker m= new Marker();
			m.name=s;
			m.index=nMarkers;
			m.ignore=false;
			if(marker2index.get(m.name)!=null)
				{
				throw new org.lindenb.lang.IllegalInputException("Snp \""+m.name+"\" is defined twice");
				}
		
			if(excludedMarkers!=null && excludedMarkers.get(m.name)!=null)
				{
				log("excluding "+m.name);
				m.ignore=true;
				nMarkerExcluded++;
				}
			markersDB.put(nMarkers,m);
			marker2index.put(m.name, nMarkers);
			++nMarkers;
			}
		log("nMarkers "+nMarkers+"-"+nMarkerExcluded);

		MarkerInfo markersInfo[]=new MarkerInfo[nMarkers];
		for(int i=0;i< markersInfo.length;++i)
			{
			Marker m= markersDB.get(i);
			if(m==null) throw new RuntimeException();
			if(m.ignore)
				{
				markersInfo[i]=null;
				continue;
				}
			markersInfo[i]=new MarkerInfo();
			}
		
		
		if((nMarkers-nMarkerExcluded)==0) throw new IllegalInputException("No Marker was defined");
		int nIndividuals=0;
		int nLine=0;
		int nStatus=-1;
		while((line=r.readLine())!=null)
			{
			++nLine;
			if(line.trim().length()==0) continue;	
			tokens=TAB.split(line);
			if(tokens.length< 5) throw new IllegalInputException("Expected at least 5 columns line "+nLine);
			if(tokens.length-5< nMarkers) throw new IllegalInputException("Expected at least "+(5+nMarkers)+" columns line "+nLine);
			
			int i= tokens.length-(nMarkers+5);
			if(nStatus==-1)
				{
				nStatus=i;
				}
			else if(nStatus!=i)
				{
				 throw new IllegalInputException("Illegal number of columns "+nLine);
				}
			
			Individual currentIndividual=new Individual(tokens[0],tokens[1]);
			Name name=new Name(tokens[0],tokens[1]);
			
			//ignore if individual was excluded
			if(excludeIndividuals!=null && excludeIndividuals.get(name)!=null)
				{
				continue;
				}
			
			//check no duplicate
			if(individualsDB.get(name)!=null)
				{
				throw new IllegalInputException("Individual "+tokens[0]+":"+tokens[1]+" was defined twice");
				}
			
			currentIndividual.father= tokens[2];
			currentIndividual.mother= tokens[3];
			try
			{
			currentIndividual.gender= Integer.parseInt(tokens[4]);
			if(currentIndividual.gender<0 || currentIndividual.gender>2) throw new NumberFormatException();
			} catch(NumberFormatException err2) {throw new NumberFormatException("Bad gender for "+tokens[0]+":"+tokens[1]);}
			
			//parse status
			for(i =0;i< nStatus;++i)
				{
				try
					{
					int status = Integer.parseInt( tokens[5+i]);
					currentIndividual.statuses.add(status);
					} catch(NumberFormatException err2) {throw new NumberFormatException("Bad status "+tokens[5+i]+" for "+tokens[0]+":"+tokens[1]);}
				}
			
			
			for(i=0;i< nMarkers;++i)
				{
				Genotype g= null;
				if(markersInfo[i]==null)
					{
					//marker was excluded
					g= new Genotype((short)0,(short)0);
					}
				else
					{
					String s= tokens[5+nStatus+i];
					if(!GENOTYPE_PATTERN.matcher(s).matches()) throw new IllegalInputException("Not a genotype :"+s+" line "+nLine);
					int loc= s.indexOf(' ');
					g= new Genotype(
							Short.parseShort(s.substring(0,loc)),
							Short.parseShort(s.substring(loc+1))
							);
					}
				currentIndividual.genotypes.add(g);
				}
			index2name.put(nIndividuals, name);
			individualsDB.put(name, currentIndividual);
			++nIndividuals;
			}
		r.close();
		r=null;
		
		/** QC on individual */
		
		
		exec.setMessage("QC Individuals...");
		BufferedDataContainer container =  exec.createDataContainer(qcIndividualsDataTableSpec());
		try
			{
			
			for(int i=0;i< nIndividuals;++i)
				{
				Name name= index2name.get(i);
				if(name==null) throw new NullPointerException("Cannot get name "+i);
				Individual indi= individualsDB.get(name);
				if(indi==null) throw new NullPointerException("Cannot get indi "+name);
				RowKey rowkey= new RowKey(indi.getFamily()+"_"+indi.getName());
				DataCell cells[]=new DataCell[5+3];
				int genotyped=0;
				
				for(int index=0;index< nMarkers;++index)
					{
					Genotype g= indi.genotypes.get(index);
					MarkerInfo markerInfo = markersInfo[index];
					
					//System.err.println(name+" "+index+" "+g+" "+markerInfo);
					
					
					if(markerInfo==null //marker excluded
						|| g.isNull()) //not genotyped
						{
						continue;
						}
					
					
					markerInfo.genotyped++;
					Integer count= markerInfo.genotype2count.get(g);
					if(count==null) count=new Integer(0);
					markerInfo.genotype2count.put(g,1+count);
					
					genotyped++;
					}
				cells[0]= new StringCell(indi.family);
				cells[1]= new StringCell(indi.name);
				cells[2]= new StringCell(indi.father);
				cells[3]= new StringCell(indi.mother);
				cells[4]= new IntCell(indi.gender);
				cells[5]= new IntCell(genotyped);
				cells[6]= new IntCell(nMarkers-nMarkerExcluded);
				cells[7]= new DoubleCell(nMarkers==0?-1.0:genotyped/(double)(nMarkers-nMarkerExcluded));
				container.addRowToTable(new DefaultRow(rowkey,cells));
				exec.checkCanceled();
				//exec.setProgress(i/(double)nIndividuals);
				}
			}
		catch(Exception err )
			{
			err.printStackTrace();
			throw err;
			}
		finally
			{
			container.close();
			}
		
		BufferedDataTable qcIndividual= container.getTable();
		
		
		
		
		exec.setMessage("QC Markers...");
		
		container =  exec.createDataContainer(qcMarkersDataTableSpec());
		try
			{
			
			for(int n=0;n< nMarkers;++n)
				{
				MarkerInfo info= markersInfo[n];
				if(info==null ) continue;//marker excluded 
				
				Marker marker= markersDB.get(n);
				
				if(marker==null || info==null) throw new NullPointerException("Cannot get marker "+n);
				DataCell cells[]=new DataCell[9];

				RowKey rowkey= new RowKey(marker.name);
				
				
				cells[0]= new StringCell(marker.name);
				cells[1]= new IntCell(marker.index);
				cells[2]= new IntCell(info.genotyped);
				cells[3]= new IntCell(nIndividuals);
				cells[4]= new DoubleCell(nIndividuals==0?-1.0:info.genotyped/(double)nIndividuals);
				
				Integer count= info.genotype2count.get(GENOTYPE_11);
				if(count==null) count=0;
				cells[5]= new IntCell(count);
				count= info.genotype2count.get(GENOTYPE_12);
				if(count==null) count=0;
				cells[6]= new IntCell(count);
				count= info.genotype2count.get(GENOTYPE_22);
				if(count==null) count=0;
				cells[7]= new IntCell(count);
				
				cells[8]= new DoubleCell(info.chi2());
				
				
				container.addRowToTable(new DefaultRow(rowkey,cells));
				exec.checkCanceled();
				//exec.setProgress(n/(double)nMarkers);
				}
			}
		catch(Exception err )
			{
			err.printStackTrace();
			throw err;
			}
		finally
			{
			container.close();
			}
		BufferedDataTable qcMarkers= container.getTable();

		return new BufferedDataTable[]{qcIndividual,qcMarkers};
		}
	catch(Exception err )
		{
		err.printStackTrace();
		throw err;
		}
	finally
		{
		if(r!=null) r.close();
		if( markersDB!=null) markersDB.close();
		if( marker2index!=null) marker2index.close();
		if( index2name!=null) index2name.close();
		if( individualsDB!=null) individualsDB.close();
		if(excludedMarkers!=null) excludedMarkers.close();
		if(excludeIndividuals!=null) excludeIndividuals.close();
		if(env!=null) Berkeley.popInstance();
		}
	}
	
	

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		if(this.m_filename.getStringValue()==null) throw new InvalidSettingsException("Filename not specified");
		return new DataTableSpec[]{qcIndividualsDataTableSpec(),qcMarkersDataTableSpec()};
		}
	
	
	private DataTableSpec qcIndividualsDataTableSpec()
		{
		DataColumnSpec cds[]=new DataColumnSpec[5+3];
		cds[0]= new DataColumnSpecCreator("Family",StringCell.TYPE).createSpec();
		cds[1]= new DataColumnSpecCreator("Name",StringCell.TYPE).createSpec();
		cds[2]= new DataColumnSpecCreator("Father",StringCell.TYPE).createSpec();
		cds[3]= new DataColumnSpecCreator("Mother",StringCell.TYPE).createSpec();
		cds[4]= new DataColumnSpecCreator("Gender",IntCell.TYPE).createSpec();
		cds[5]= new DataColumnSpecCreator("Genotyped",IntCell.TYPE).createSpec();
		cds[6]= new DataColumnSpecCreator("Total",IntCell.TYPE).createSpec();
		cds[7]= new DataColumnSpecCreator("Percent Genotyped",DoubleCell.TYPE).createSpec();
		return new DataTableSpec(cds);
		}
	
	private DataTableSpec qcMarkersDataTableSpec()
		{
		DataColumnSpec cds[]=new DataColumnSpec[9];
		cds[0]= new DataColumnSpecCreator("Marker-Name",StringCell.TYPE).createSpec();
		cds[1]= new DataColumnSpecCreator("Marker-Index",IntCell.TYPE).createSpec();
		cds[2]= new DataColumnSpecCreator("Genotyped",IntCell.TYPE).createSpec();
		cds[3]= new DataColumnSpecCreator("Total",IntCell.TYPE).createSpec();
		cds[4]= new DataColumnSpecCreator("Percent Genotyped",DoubleCell.TYPE).createSpec();
		
		cds[5]= new DataColumnSpecCreator("count(1-1)",IntCell.TYPE).createSpec();
		cds[6]= new DataColumnSpecCreator("count(1-2)",IntCell.TYPE).createSpec();
		cds[7]= new DataColumnSpecCreator("count(2-2)",IntCell.TYPE).createSpec();
		
		cds[8]= new DataColumnSpecCreator("Hardy Weinberg chi2",DoubleCell.TYPE).createSpec();
		return new DataTableSpec(cds);
		}
	
	
	}
