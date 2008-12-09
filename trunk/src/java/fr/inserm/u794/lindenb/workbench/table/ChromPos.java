package fr.inserm.u794.lindenb.workbench.table;

import java.util.Comparator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class ChromPos
implements Comparable<ChromPos>
{

public static class COMPARATOR
	implements Comparator<byte[]>
	{
	public COMPARATOR()
		{
		
		}
		
	@Override
	public int compare(byte[] o1, byte[] o2)
		{
		ChromPos c1= BINDING.entryToObject(new DatabaseEntry(o1));
		ChromPos c2= BINDING.entryToObject(new DatabaseEntry(o2));

		return c1.compareTo(c2);
		}
	}
	
public static final TupleBinding<ChromPos> BINDING= new TupleBinding<ChromPos>()
	{
	@Override
	public ChromPos entryToObject(TupleInput input) {
		String s= input.readString();
		long L= input.readLong();
		ChromPos object= new ChromPos(s,L);
		return object;
		}
	
	
	@Override
		public void objectToEntry(ChromPos object, TupleOutput output) {

			output.writeString(object.getChrom());
			output.writeLong(object.getPosition());
		}
	};
	
private String chrom;
private long position;
public ChromPos(String chrom,long position)
	{
	this.chrom=chrom;
	this.position=position;
	}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
	result = prime * result + (int) (position ^ (position >>> 32));
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
	ChromPos other = (ChromPos) obj;
	if (chrom == null) {
		if (other.chrom != null)
			return false;
	} else if (!chrom.equals(other.chrom))
		return false;
	if (position != other.position)
		return false;
	return true;
}
public String getChrom() {
	return chrom;
}
public long getPosition() {
	return position;
}

@Override
	public int compareTo(ChromPos o)
		{
		int i= getChrom().compareToIgnoreCase(o.getChrom());
		if(i!=0) return i;
		long n= getPosition()-o.getPosition();
		return (n==0L?0:n<0L?-1:1);
		}

public DatabaseEntry toEntry()
	{
	DatabaseEntry e= new DatabaseEntry();
	BINDING.objectToEntry(this, e);
	return e;
	}

@Override
	public String toString() {
		return getChrom()+":"+getPosition();
		}

}
