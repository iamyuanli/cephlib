package org.lindenb.knime.plugins.ucsc.sql;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

public class UCSCByPositionNodeModel extends UCSCNodeModel
	{
	static final String KEY_CHROM="ucsc.find.by.pos.chrom";
	static final String KEY_CHROM_START="ucsc.find.by.pos.chromStart";
	static final String KEY_CHROM_END="ucsc.find.by.pos.chromEnd";
	static final String KEY_EXTEND="ucsc.find.by.pos.extend";
	static final String KEY_ONLY_ONE="ucsc.find.by.pos.onlyone";
	/** the column in the input containing the chrom */
    protected final SettingsModelColumnName m_target_chrom =new SettingsModelColumnName(KEY_CHROM,"chrom");
    /** the column in the input containing the chromStart */
    protected final SettingsModelColumnName m_target_chromStart =new SettingsModelColumnName(KEY_CHROM_START,"chromStart");
    /** the column in the input containing the chromEnd */
    protected final SettingsModelColumnName m_target_chromEnd =new SettingsModelColumnName(KEY_CHROM_END,"chromEnd");
    /** extends bound */
    protected final SettingsModelInteger m_target_extend =new SettingsModelInteger(KEY_EXTEND,0);
    /** only one */
    protected final SettingsModelBoolean m_target_only_one =new SettingsModelBoolean(KEY_ONLY_ONE,true);
    
	public UCSCByPositionNodeModel() {
		super(1, 1);
		addSettings(m_target_chrom);
		addSettings(m_target_chromStart);	
		addSettings(m_target_chromEnd);
		addSettings(m_target_extend);
		addSettings(m_target_only_one);
		}
	
	
	}
