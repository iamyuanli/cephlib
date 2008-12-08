package fr.inserm.u794.lindenb.workbench.table;

import java.util.ArrayList;
import java.util.Iterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class Indexes 
	implements Iterable<Integer>
	{
	public static final TupleBinding<Indexes> BINDING=
		new TupleBinding<Indexes>()
		{
		@Override
		public Indexes entryToObject(TupleInput in)
			{
			int n=in.readInt();
			int array[]=new int[n];
			for(int i=0;i< n;++i) array[i]=in.readInt();
			return new Indexes(array);
			}
		@Override
		public void objectToEntry(Indexes index, TupleOutput out)
			{
			out.writeInt(index.size());
			for(int i=0;i< index.size();++i) out.writeInt(index.at(i));
			}
		};
	
	private int array[]=null;
	private Indexes(int array[]) { this.array=array;}
	public Indexes()	{ this.array=new int[0];}
	public void add(int rowIndex)
		{
		int cp[]=new int[array.length+1];
		System.arraycopy(this.array, 0, cp, 0, this.array.length);
		cp[array.length]=rowIndex;
		this.array=cp;
		}
	public int size()
		{
		return this.array.length;
		}
	
	public int at(int i)
		{
		return this.array[i];
		}
	
	public boolean isEmpty()
		{
		return size()==0;
		}
	
	public int first()
		{
		return at(0);
		}
	
	@Override
	public Iterator<Integer> iterator()
		{
		ArrayList<Integer> list= new ArrayList<Integer>(size());
		for(int i=0;i< size();++i) list.add(at(i));
		return list.iterator();
		}
	}
