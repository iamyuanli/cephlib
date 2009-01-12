package org.lindenb.knime.plugins.ucsc.sql;

import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;

public class UCSCByNameNodeModel extends UCSCNodeModel
	{
	static final String KEY_COLUMN="ucsc.find.by.name.column";
	/** the column in the input containing the key */
    protected final SettingsModelColumnName m_target_column =new SettingsModelColumnName(KEY_COLUMN,"");
	
	public UCSCByNameNodeModel() {
		super(1, 1);
		addSettings(m_target_column);
		}
	
	
	}
