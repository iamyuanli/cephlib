package org.lindenb.knime.plugins.ucsc.hapmapbyname;

import org.knime.core.node.NodeDialogPane;
import org.lindenb.knime.plugins.MyNodeFactory;

public class HapmapByNameNodeFactory extends MyNodeFactory<HapmapByNameNodeModel> {
	
	public HapmapByNameNodeFactory()
		{
		}	

	@Override
	protected NodeDialogPane createNodeDialogPane()
		{
		return new HapmapByNameNodeDialog();
		}

	@Override
	public HapmapByNameNodeModel createNodeModel() {
		return new HapmapByNameNodeModel();
		}

	@Override
	protected boolean hasDialog() {
		return true;
	}
}
