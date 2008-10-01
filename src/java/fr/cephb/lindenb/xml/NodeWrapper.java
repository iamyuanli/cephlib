package fr.cephb.lindenb.xml;

import org.w3c.dom.Node;

public class NodeWrapper {
private Node node;
public NodeWrapper(Node node)
	{
	this.node=node;
	}

public Node getNode() {
	return node;
	}

}
