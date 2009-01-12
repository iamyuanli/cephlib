package org.lindenb.knime.plugins.ncbi.omim;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

public class OmimNodeFactory extends MyNodeFactory<OmimNodeModel> {
	
	public OmimNodeFactory()
		{
		}	

	@Override
	protected NodeDialogPane createNodeDialogPane()
		{
		return new OmimNodeDialog();
		}

	@Override
	public OmimNodeModel createNodeModel() {
		return new OmimNodeModel();
		}

	@Override
	protected boolean hasDialog() {
		return true;
	}
}
