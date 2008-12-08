package fr.inserm.u794.lindenb.workbench.table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.lindenb.swing.table.GenericTableModel;


import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class TableRef
{
public static final TupleBinding<TableRef> BINDING= new TupleBinding<TableRef>()
	{
	private SimpleDateFormat fmt= new SimpleDateFormat();
	@Override
	public TableRef entryToObject(TupleInput in)
		{
		TableRef t= new TableRef();
		t.setId(in.readLong());
		t.setName(in.readString());
		t.setDescription(in.readString());
		try {
			t.setCreation(fmt.parse(in.readString()));
		} catch (Throwable e) {
			t.setCreation(new Date());
			}
		int n= in.readInt();
		for(int i=0;i< n;++i) t.tags.add(in.readString());
		t.rowCount= in.readInt();
		return t;
		}
	@Override
	public void objectToEntry(TableRef t, TupleOutput out)
		{
		out.writeLong(t.getId()).
			writeString(t.getName()).
			writeString(t.getDescription()).
			writeString(fmt.format(t.getCreation())).
			writeInt(t.tags.size())
			;
		for(String s:t.tags) out.writeString(s);
		out.writeInt(t.getRowCount());
		}
	};
	
public static class Model extends GenericTableModel<TableRef>
	{
	private static final long serialVersionUID = 1L;

	public Model()
		{
		}
	
	@Override
	public int getColumnCount() {
		return 5;
		}
	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
		}
	
	@Override
	public String getColumnName(int column)
		{
		switch(column)
			{
			case 0: return "Name";
			case 1: return "Description";
			case 2: return "Creation";
			case 3: return "Tags";
			case 4: return "Rows";
			}
		return null;
		}
	
	@Override
	public Object getValueOf(TableRef object, int column) {
		switch(column)
			{
			case 0: return object.getName();
			case 1: return object.getDescription();
			case 2: return object.getCreation();
			case 3: return object.getTagsAsString();
			case 4: return String.valueOf(object.getRowCount());
			}
		return null;
		}
	
	}
private long id=0L;
private String name="";
private String description="";
private Date creation=new Date();
private Set<String> tags=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
private int rowCount=0;

public TableRef()
	{
	
	}

public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public Set<String> getTags() {
	return new TreeSet<String>(tags);
}
public void setTags(Set<String> tags){
	this.tags.clear();
	this.tags.addAll(tags);
}

public void setCreation(Date creation) {
	this.creation = creation;
	}

public Date getCreation() {
	return creation;
	}

public String getTagsAsString()
	{
	StringBuilder b= new StringBuilder();
	for(String s:getTags())
		{
		if(b.length()!=0) b.append(" ");
		b.append(s);
		}
	return b.toString();
	}

@Override
public int hashCode() {
	return new Long(getId()).hashCode();
	}

public int getRowCount() {
	return rowCount;
	}

public void setRowCount(int rowCount) {
	this.rowCount = rowCount;
	}

@Override
	public String toString() {
		return getName();
		}
}
