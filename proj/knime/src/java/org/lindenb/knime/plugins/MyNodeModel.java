/**
 * 
 */
package org.lindenb.knime.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;

/**
 * @author lindenb
 *
 */
public abstract class MyNodeModel extends NodeModel
	{
	protected ArrayList<SettingsModel> settingsModel=new ArrayList<SettingsModel>();
	
	
	protected MyNodeModel addSettings(SettingsModel settings)
		{
		this.settingsModel.add(settings);
		log("addSettings");
		return this;
		}
	
	@Override
	protected void reset() {
		log("reset");
		}
	
	protected void log(Object o)
		{
		log(Level.INFO,o);
		}
	
	protected void log(Level level,Object o)
		{
		System.err.print("["+level+"]:");
		System.err.print(getClass());
		System.err.print(":");
		System.err.println(o);
		}
	
	protected MyNodeModel(int nrInDataPorts, int nrOutDataPorts)
		{
		super(nrInDataPorts, nrOutDataPorts);
		log("constructor");
		}
	
	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		
		}
	
	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		log("saveInternals");
		}
	
	
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings)
			throws InvalidSettingsException {
		log("loadValidatedSettings");
		for(SettingsModel s:this.settingsModel)
			{
			
			s.loadSettingsFrom(settings);
			}
		}
	
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
		{
		log("saveSettingsTo");
		for(SettingsModel s:this.settingsModel)
			{
			s.saveSettingsTo(settings);
			}
		}
	
	@Override
	protected void validateSettings(NodeSettingsRO settings)
			throws InvalidSettingsException
		{
		log("validateSettings");
		for(SettingsModel s:this.settingsModel)
			{
			s.validateSettings(settings);
			}
		}
	
	protected int findColumnByName(SettingsModelColumnName colName,DataTableSpec dts) throws IllegalArgumentException
		{
		if (colName==null || colName.getColumnName() == null) {
			throw new IllegalArgumentException("Columns was not selected. "+getClass());
        	}
		int index = dts.findColumnIndex(colName.getColumnName());
	    if(index==-1) throw new IllegalArgumentException("No column data available "+getClass());
	    return index;
		}
	
	
	protected Object  dataCellToObject(DataCell cell)
		{
		if(cell==null || cell.isMissing())
			{
			return null;
			}
		else if(cell instanceof DoubleCell )
			{
			return DoubleCell.class.cast(cell).getDoubleValue();
			}
		else if(cell instanceof IntCell )
			{
			return IntCell.class.cast(cell).getIntValue();
			}
		else if(cell instanceof StringCell )
			{
			return StringCell.class.cast(cell).getStringValue();
			}
		log("Cannot convert cell.class="+cell.getClass()+" to java.lang.Object");
		return null;
		}
	
	}
