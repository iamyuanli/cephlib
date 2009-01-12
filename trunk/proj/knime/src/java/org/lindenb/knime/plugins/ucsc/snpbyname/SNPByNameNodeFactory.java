package org.lindenb.knime.plugins.ucsc.snpbyname;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

public class SNPByNameNodeFactory extends MyNodeFactory<SNPByNameNodeModel> {
	
	public SNPByNameNodeFactory()
		{
		}	

	@Override
	protected NodeDialogPane createNodeDialogPane()
		{
		return new SNPByNameNodeDialog();
		}

	@Override
	public SNPByNameNodeModel createNodeModel() {
		return new SNPByNameNodeModel();
		}

	@Override
	protected boolean hasDialog() {
		return true;
	}
}
