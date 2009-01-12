package org.lindenb.knime.plugins.ucsc.genebyposition;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

public class GeneByPositionNodeFactory extends MyNodeFactory<GeneByPositionNodeModel> {
	
	public GeneByPositionNodeFactory()
		{
		}	

	@Override
	protected NodeDialogPane createNodeDialogPane()
		{
		return new GeneByPositionNodeDialog();
		}

	@Override
	public GeneByPositionNodeModel createNodeModel() {
		return new GeneByPositionNodeModel();
		}

	@Override
	protected boolean hasDialog() {
		return true;
	}
}
