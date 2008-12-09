package fr.inserm.u794.lindenb.workbench.table;


import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class RowId
{


public static TupleBinding<RowId> BINDING = new TupleBinding<RowId>()
	{
	@Override
	public RowId entryToObject(TupleInput input)
		{
		long n= input.readLong();
		int r= input.readInt();
		return new RowId(n,r);
		}
	@Override
	public void objectToEntry(RowId object, TupleOutput output)
		{
		output.writeLong(object.getDocumentId());
		output.writeInt(object.getRowIndex());
		}
	};	
private long documentId;
private int rowIndex;

public RowId(long docId, int rowIndex)
	{
	this.documentId=docId;
	this.rowIndex=rowIndex;
	}

public long getDocumentId() {
	return documentId;
	}

public int getRowIndex() {
	return rowIndex;
	}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (documentId ^ (documentId >>> 32));
	result = prime * result + (int) (rowIndex);
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
	RowId other = (RowId) obj;
	if (documentId != other.documentId)
		return false;
	if (rowIndex != other.rowIndex)
		return false;
	return true;
}

public DatabaseEntry toEntry()
	{
	DatabaseEntry entry= new DatabaseEntry();
	BINDING.objectToEntry(this, entry);
	return entry;
	}

@Override
	public String toString() {
		return "doc-Id:"+getDocumentId()+" row:"+getRowIndex();
		}

}
