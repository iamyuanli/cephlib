package org.lindenb.knime.plugins.ncbi;

import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.lindenb.knime.plugins.MyNodeModel;


public class NCBIEFetchNodeModel extends MyNodeModel
	{
	static final String CFG_KEY_COLUMN_ID="ncbi.efetch.id";
	/** the column in the input containing the key */
    protected final SettingsModelColumnName m_column_id =new SettingsModelColumnName(CFG_KEY_COLUMN_ID,"");
    
	@SuppressWarnings("unchecked")
	public NCBIEFetchNodeModel()
		{
		super(1,1);
		addSettings(m_column_id);
		}
	
	
	}
