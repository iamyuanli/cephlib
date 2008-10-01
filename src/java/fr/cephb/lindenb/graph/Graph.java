package fr.cephb.lindenb.graph;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lindenb.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.cephb.lindenb.graph.nodes.Gene;
import fr.cephb.lindenb.graph.nodes.Omim;
import fr.cephb.lindenb.graph.nodes.Pubmed;
import fr.cephb.lindenb.graph.nodes.SNP;
import fr.cephb.lindenb.graph.nodes.Unigene;
import fr.cephb.lindenb.xml.ElementWrapper;

public class Graph
extends ElementWrapper
{
private HashMap<Identifier, Node> id2node= new HashMap<Identifier, Node>();
private Set<Link> links= new HashSet<Link>();
private String id;

private Graph(Element e) throws IOException
	{
	super(e);
	Attr attid= e.getAttributeNode("id");
	if(attid==null) throw new IOException("Graph is missing id");
	this.id=attid.getValue();
	for(Element node:XMLUtilities.elements(e,GraphML.NS,"node"))
		{
		Attr att= node.getAttributeNode("id");
		if(att==null) throw new IOException("Node is missing id");
		Identifier id= new Identifier(att.getValue());
		if(id2node.containsKey(id)) throw new IOException("Node defined twice");
		Node n=null;
		switch(id.getDatabase())
			{
			case pubmed: n= new Pubmed(node,this,id.getId()); break;
			case snp: n= new SNP(node,this,id.getId()); break;
			case unigene: n= new Unigene(node,this,id.getId()); break;
			case gene: n= new Gene(node,this,id.getId()); break;
			case omim: n= new Omim(node,this,id.getId()); break;
			default: throw new IOException("Database Not Handled "+id.getDatabase());
			}
		this.id2node.put(n.getIdentifier(), n);
		}
	
	for(Element node:XMLUtilities.elements(e,GraphML.NS,"edge"))
		{
		Attr att1= node.getAttributeNode("source");
		if(att1==null) throw new IOException("edge is missing source");
		Identifier id1= new Identifier(att1.getValue());
		Node node1= id2node.get(id1);
		if(node1==null) throw new IOException("node undefined "+id1);
		
		Attr att2= node.getAttributeNode("target");
		if(att2==null) throw new IOException("edge is missing target");
		Identifier id2= new Identifier(att2.getValue());
		Node node2= id2node.get(id2);
		if(node2==null) throw new IOException("node undefined "+id2);
		Link L= new Link(this,node,node1,node2) ;
		if(links.contains(L)) throw new IOException("edge defined twice");
		this.links.add(L);
		}
	}

public Collection<Link> getLinks()
	{
	return this.links;
	}

public Collection<Node> getNodes()
	{
	return this.id2node.values();
	}

public String getId()
	{
	return id;
	}

	
public static Collection<Graph> parse(File file) throws IOException,SAXException 
	{
	try
		{
		DocumentBuilderFactory f= DocumentBuilderFactory.newInstance();
		f.setCoalescing(true);
		f.setExpandEntityReferences(true);
		f.setNamespaceAware(true);
		f.setIgnoringComments(true);
		f.setValidating(false);
		DocumentBuilder b= f.newDocumentBuilder();
		Document doc=b.parse(file);
		Element root= doc.getDocumentElement();
		if(root==null) throw new IOException("No root in "+file);
		Vector<Graph> graphs= new Vector<Graph>();
		for(Element g: XMLUtilities.elements(root, GraphML.NS, "graph"))
			{
			graphs.addElement(  new Graph(g) );
			}
		return graphs;
		}
	catch(ParserConfigurationException err)
		{
		throw new IOException(err);
		}
	
	}
}
