package fr.cephb.lindenb.graph;

import org.w3c.dom.Element;

import fr.cephb.lindenb.xml.ElementWrapper;

public class GraphComponent
	extends ElementWrapper
	{
	private Graph g;
	protected GraphComponent(Element e,Graph g)
		{
		super(e);
		this.g=g;
		}
	public Graph getGraph()
		{
		return g;
		}
	}
