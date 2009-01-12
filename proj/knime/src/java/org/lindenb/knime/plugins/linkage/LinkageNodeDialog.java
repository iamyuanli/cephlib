package org.lindenb.knime.plugins.linkage;


import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.knime.plugins.MyNodeDialog;


public class LinkageNodeDialog extends MyNodeDialog {

    protected LinkageNodeDialog() {
    	
    	addDialogComponent(
    			new DialogComponentFileChooser(new SettingsModelString(
                     LinkageNodeModel.CFGKEY_FILENAME,
                    ""
                     ), getClass().toString(),JFileChooser.OPEN_DIALOG,".|.ped|text"
                     ));
    	 
    }
}

