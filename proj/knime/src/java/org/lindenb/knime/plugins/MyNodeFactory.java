package org.lindenb.knime.plugins;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;


public abstract class MyNodeFactory<T extends NodeModel>
extends NodeFactory<T>
	{
	protected MyNodeFactory()
		{
		}
	
	@Override
	protected int getNrNodeViews() {
		return 0;
		}
	
	@Override
	public NodeView<T> createNodeView(int viewIndex, T nodeModel) {
		throw new RuntimeException("Cannot create a view for "+getClass()+" getNrNodeViews="+getNrNodeViews());
		}
	
	}
