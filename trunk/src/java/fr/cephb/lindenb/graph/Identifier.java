package fr.cephb.lindenb.graph;

import java.net.URL;

import fr.cephb.lindenb.bio.ncbi.Database;

public class Identifier
{
private Database db;
private int id;
public Identifier(Database db,int id)
	{
	this.db=db;
	this.id=id;
	}

public Identifier(String s)
	{
	int loc= s.indexOf(':');
	if(loc==-1) throw new IllegalArgumentException("bad identifier id");
	this.db= Database.valueOf(s.substring(0,loc).trim().toLowerCase());
	this.id= Integer.parseInt(s.substring(loc+1).trim());
	}

public Database getDatabase()
	{
	return db;
	}

public int getId()
	{
	return id;
	}


@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + getDatabase().hashCode();
	result = prime * result + getId();
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
	Identifier other = (Identifier) obj;
	return getDatabase().equals(other.getDatabase()) &&
		   getId()==other.getId()
		   ;
	}

public URL getURL() {
	return getDatabase().getURL(getId());
	}

public String getURI() {
	return getDatabase().toString()+":"+getId();
	}
@Override
public String toString() {
	return getURI();
	}
}
