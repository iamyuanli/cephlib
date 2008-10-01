package fr.cephb.lindenb.graph.nodes;

import org.w3c.dom.Element;

import fr.cephb.lindenb.graph.Graph;
import fr.cephb.lindenb.graph.Node;
import fr.cephb.lindenb.ncbi.Database;

public class Pubmed extends Node
{

public Pubmed(Element e,Graph g,int id)
	{
	super(e,g,id);
	}

@Override
public Database getDatabase()
	{
	return Database.pubmed;
	}


}
