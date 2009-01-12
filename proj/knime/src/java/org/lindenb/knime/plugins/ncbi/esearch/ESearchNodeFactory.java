package org.lindenb.knime.plugins.ncbi.esearch;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

public class ESearchNodeFactory extends MyNodeFactory<ESearchNodeModel> {
	
	public ESearchNodeFactory()
		{
		}	

	@Override
	protected NodeDialogPane createNodeDialogPane()
		{
		return new ESearchNodeDialog();
		}

	@Override
	public ESearchNodeModel createNodeModel() {
		return new ESearchNodeModel();
		}

	@Override
	protected boolean hasDialog() {
		return true;
	}

}
