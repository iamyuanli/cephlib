package fr.cephb.lindenb.graph;

import org.w3c.dom.Element;

public class Link extends GraphComponent
{
private Node n1;
private Node n2;
public Link(Graph g,Element e,Node n1,Node n2)
	{
	super(e,g);
	this.n1=n1;
	this.n2=n2;
	}

public Node first() { return n1;}
public Node second() { return n2;}

@Override
public int hashCode() {
	return 31+(first().hashCode()*second().hashCode());
	}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Link other = (Link) obj;
	return  (first().equals(other.first()) &&  second().equals(other.second())) ||
			(first().equals(other.second()) && second().equals(other.first()))
			;
	}
@Override
	public String toString()
	{
	return first().toString()+"--"+second().toString();
	}

public boolean contains(Node n)
	{
	return first().equals(n) || second().equals(n);
	}

public Node complement(Node n)
	{
	if(first().equals(n)) return second();
	if(second().equals(n)) return first();
	return null;
	}

}
