package fr.inserm.u794.lindenb.workbench.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.lindenb.util.Cast;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class Row
implements Iterable<String>,Comparable<Row>
	{
	
	
	/**
	 * 
	 * COMPARATOR
	 *
	 */
	public static class COMPARATOR
		implements Comparator<byte[]>
		{
		private Comparator<Row> comparator=null;
		
		public COMPARATOR(Comparator<Row> comparator)
			{
			this.comparator=comparator;
			}
		
		public COMPARATOR()
			{
			this(null);
			}
		public int compare(byte[] o1, byte[] o2)
			{
			Row r1= BINDING.entryToObject(new DatabaseEntry(o1));
			Row r2= BINDING.entryToObject(new DatabaseEntry(o2));
			return comparator!=null
					? comparator.compare(r1, r2)
					: r1.compareTo(r2);
			};
		}
	
	/**
	 * BINDING
	 */
	public static final TupleBinding<Row> BINDING=new TupleBinding<Row>()
		{
		@Override
		public Row entryToObject(TupleInput input)
			{
			int n= input.readInt();
			String tokens[]=new String[n];
			for(int i=0;i< tokens.length;++i) tokens[i]= input.readString();
			return new Row(tokens);
			}
		
		@Override
		public void objectToEntry(Row row, TupleOutput out) {
			out.writeInt(row.size());
			for(int i=0;i< row.size();++i)
				{
				out.writeString(row.at(i));
				}
			}
		};
	private String tokens[];
	
	public Row(String tokens[])
		{
		this.tokens=tokens;
		}
	public Row(Row cp)
		{
		this.tokens=cp.toArray();
		}
	public int size()
		{
		return tokens.length;
		}
	
	public String at(int index)
		{
		return index<size()?this.tokens[index]:"";
		}
	
	
	public Double getDouble(int index)
		{
		return Cast.Double.cast(at(index));
		}
	
	public Float getFloat(int index)
		{
		return Cast.Float.cast(at(index));
		}
	
	public Integer getInteger(int index)
		{
		return Cast.Integer.cast(at(index));
		}
	
	public Long getLong(int index)
		{
		return Cast.Long.cast(at(index));
		}
	
	
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(tokens);	
		}

	
	@Override
	public boolean equals(Object obj)
		{
		if(obj==this) return true;
		if(obj==null || getClass()!=obj.getClass()) return false;
		Row cp=Row.class.cast(obj);
		return Arrays.equals(tokens, cp.tokens);
		}
	
	
	@Override
	public Iterator<String> iterator()
		{
		return Arrays.asList(this.tokens).iterator();
		}
	
	@Override
	public String toString() {
		StringBuilder b= new StringBuilder();
		for(int i=0;i< size();++i)
			{
			if(i>0) b.append("\t");
			b.append(at(i));
			}
		return b.toString(); 
		}
	
	public Row insert(String newtokens[],int index)
		{
		String array[]=new String[size()+newtokens.length];
		System.arraycopy(this.tokens, 0, array, 0, index);
		System.arraycopy(newtokens, 0, array, index, newtokens.length);
		System.arraycopy(this.tokens, index, array, index+newtokens.length,this.size()-index);
		return new Row(array);
		}
	
	public Row add(String s)
		{
		String array[]=new String[size()+1];
		System.arraycopy(this.tokens, 0, array, 0, size());
		array[size()]=s;
		return new Row(array);
		}
	
	public String[] toArray()
		{
		String array[]=new String[size()];
		for(int i=0;i<size();++i) array[i]=at(i);
		return array;
		}
	
	@Override
	public int compareTo(Row r2)
		{
		int i= this.size()-r2.size();
		if(i!=0) return i;
		for(int j=0;j< this.size();++j)
			{
			i= this.at(j).compareTo(r2.at(j));
			if(i!=0) return i;
			}
		return i;
		}
	
	public int compareToIgnoreCase(Row r2)
		{
		int i= this.size()-r2.size();
		if(i!=0) return i;
		for(int j=0;j< this.size();++j)
			{
			i= this.at(j).compareToIgnoreCase(r2.at(j));
			if(i!=0) return i;
			}
		return i;
		}
	
	public DatabaseEntry toDatabaseEntry()
		{
		DatabaseEntry entry=new DatabaseEntry();
		BINDING.objectToEntry(this, entry);
		return entry;
		}
	
	public Row subRowFromIndexes(List<Integer> columns)
		{
		String tokens[]=new String[columns.size()];
		for(int i=0;i< columns.size();++i)
			{
			tokens[i]=at(columns.get(i));
			}
		return new Row(tokens);
		}
	
	public Row subRowFromColumns(List<Column> columns)
		{
		List<Integer> ints= new ArrayList<Integer>(columns.size());
		for(Column c: columns) ints.add(c.getIndex());
		return subRowFromIndexes(ints);
		}
	
	
	public Row toLowerCase()
		{
		String tokens[]=new String[size()];
		for(int i=0;i< size();++i)
			{
			tokens[i]=at(i).toLowerCase();
			}
		return new Row(tokens);
		}
}
