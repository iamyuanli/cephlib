package fr.cephb.lindenb.graph;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.lindenb.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import fr.cephb.lindenb.ncbi.Database;

public abstract class Node
extends GraphComponent
{
private	Identifier id;
private String title=null;
protected Node(Element e,Graph g,int id)
	{
	super(e,g);
	this.id= new Identifier(getDatabase(),id);
	if(this.id.getId()<=0) throw new IllegalArgumentException("Bad id <=0 : "+this.id);
	for(Element c: XMLUtilities.elements(e, GraphML.NS, "data"))
		{
		Attr att= c.getAttributeNode("key");
		if(att!=null)
			{
			this.title=c.getTextContent().trim();
			}
		}
	}

public Identifier getIdentifier() {return id;}

public Database getDatabase() { return getIdentifier().getDatabase();}

public String getTitle()
	{
	return (this.title==null || title.length()==0?getIdentifier().getURI():this.title);
	}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	Node other = (Node) obj;
	return other.getIdentifier().equals(this.getIdentifier());
	}

public URL getURL()
	{
	return getIdentifier().getDatabase().getURL(getIdentifier().getId());
	}

public String getURI()
	{
	return getIdentifier().getURI();
	}

@Override
public String toString() {
	return getURI();
	}

public Collection<Link> getLinks()
	{
	HashSet<Link> link= new HashSet<Link>();
	for(Link L: getGraph().getLinks())
		{
		if(L.contains(this)) link.add(L);
		}
	return link;
	}

public Set<Node> getPartners()
	{
	HashSet<Node> nodes = new HashSet<Node>();
	for(Link L: getGraph().getLinks())
		{
		if(L.contains(this)) nodes.add(L.complement(this));
		}
	return nodes;
	}

}
