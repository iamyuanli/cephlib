package fr.inserm.u794.lindenb.workbench.table;



import java.util.ArrayList;
import java.util.List;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;


public class Column
{
public static TupleBinding<Column> BINDING = new TupleBinding<Column>()
	{
	@Override
	public Column entryToObject(TupleInput input)
		{
		return new Column(input.readInt(), input.readString());
		}
	@Override
	public void objectToEntry(Column object, TupleOutput output)
		{
		output.writeInt(object.getIndex())
			  .writeString(object.getLabel());
		}
	};

private int index=0;
private String label;


public Column(int index,String label)
	{
	this.index=index;
	this.label=label;
	}

public Column(int index)
	{
	this(index,String.valueOf(index+1));
	}

public int getIndex() {
	return index;
	}

public String getLabel() {
	return label;
	}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + index;
	result = prime * result + ((label == null) ? 0 : label.hashCode());
	return result;
	}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!(obj instanceof Column))
		return false;
	Column other = (Column) obj;
	if (index != other.index)
		return false;
	if (label == null) {
		if (other.label != null)
			return false;
	} else if (!label.equals(other.label))
		return false;
	return true;
	}

@Override
public String toString() {
	return getLabel();
	}

public static List<Column> insert(List<Column> src,int insertPosition,List<Column> toBeInserted)
	{
	List<Column> columns= new ArrayList<Column>(src.size()+toBeInserted.size());
	for(int i=0;i<insertPosition;++i)
		{
		columns.add(src.get(i));
		}
	for(int i=0;i< toBeInserted.size();++i)
		{
		columns.add(new Column(insertPosition+i,toBeInserted.get(i).getLabel()));
		}
	for(int i=insertPosition;i<src.size();++i)
		{
		columns.add(new Column(insertPosition+i+toBeInserted.size(),src.get(i).getLabel()));
		}
	
	return columns;
	}

}
