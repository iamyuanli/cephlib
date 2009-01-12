package org.lindenb.knime.plugins.ncbi;

import org.lindenb.knime.plugins.MyNodeModel;

public abstract class NCBINodeModel extends MyNodeModel
	{
	public NCBINodeModel(int nrInDataPorts, int nrOutDataPorts)
		{
		super(nrInDataPorts, nrOutDataPorts);
		}
	}
