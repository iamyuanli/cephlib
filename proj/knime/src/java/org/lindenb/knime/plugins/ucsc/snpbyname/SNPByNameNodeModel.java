package org.lindenb.knime.plugins.ucsc.snpbyname;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;

import org.lindenb.knime.plugins.ucsc.sql.UCSCByNameNodeModel;


public class SNPByNameNodeModel extends UCSCByNameNodeModel
	{
	private static final Logger LOGGER= Logger.getLogger(SNPByNameNodeModel.class.getName());
	


	public SNPByNameNodeModel()
		{
		super();
		LOGGER.setLevel(Level.ALL);
		}
	
	 @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception
        {
    	if (super.m_target_column.getColumnName() == null) {
            setWarningMessage("Columns was not selected.");
        	}
    	 // check input data
        if (inData == null || inData.length != 1
                || inData[0] == null) {
            throw new IllegalArgumentException("No input data available.");
        	}
        
        final BufferedDataTable inputTable = inData[0];
        if (inputTable.getRowCount() < 1) {
            setWarningMessage("Empty input table found");
        	}
        
        final int columnIndex = inputTable.getDataTableSpec().findColumnIndex(this.m_target_column.getColumnName());
        if(columnIndex==-1) throw new IllegalArgumentException("No column data available column selected was :"+m_target_column.getColumnName());
        LOGGER.info("ColumnIndex="+columnIndex);
        
        
        int rowCountInput= inputTable.getRowCount();
        int rowCount=-1;
        int rowIndex=0;
        DataTableSpec dataTableSpec= createTableSpec(inData[0].getDataTableSpec());
        BufferedDataContainer container=exec.createDataContainer(dataTableSpec);
        
        Connection con= null;
        try
	        {
        	con=getConnection();
	        PreparedStatement pstmt= con.prepareStatement(
	        	"select chrom,chromStart,chromEnd,class,avHet,func from snp129 where name=?");
	        for(DataRow row: inputTable)
	        	{
	        	++rowCount;
	        	DataCell keyCell=row.getCell(columnIndex);
				if(keyCell.isMissing())
					{
					LOGGER.info("empty cell");
					continue;
					}
				String term= null;
				if((keyCell instanceof org.knime.core.data.def.StringCell) )
					{
					term=StringCell.class.cast(keyCell).getStringValue();
					}
				else if((keyCell instanceof org.knime.core.data.def.IntCell) )
					{
					term="rs"+IntCell.class.cast(keyCell).getIntValue();
					}
				else
					{
					LOGGER.info("not a rs cell");
					continue;
					}
	        	
	        	if(term==null || term.length()==0) continue;
	        	pstmt.setString(1, term);
	        	ResultSet sqlrow = pstmt.executeQuery();
	        	boolean found=false;
	        	while(sqlrow.next())
	        		{
	        		found=true;
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+6];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell(sqlrow.getString(1));
	        		cells[row.getNumCells()+1]= new IntCell(sqlrow.getInt(2));
	        		cells[row.getNumCells()+2]= new IntCell(sqlrow.getInt(3));
	        		cells[row.getNumCells()+3]= new StringCell(sqlrow.getString(4));
	        		cells[row.getNumCells()+4]= new DoubleCell(sqlrow.getDouble(5));
	        		cells[row.getNumCells()+5]= new StringCell(sqlrow.getString(6));
	        		
	        		container.addRowToTable(new DefaultRow(rowKey, cells));
	        		}
	        	
	        	if(!found)
	        		{
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+6];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell("#");
	        		cells[row.getNumCells()+1]= new IntCell(-1);
	        		cells[row.getNumCells()+2]= new IntCell(-1);
	        		cells[row.getNumCells()+3]= new StringCell("#");
	        		cells[row.getNumCells()+4]= new DoubleCell(-1);
	        		cells[row.getNumCells()+5]= new StringCell("#");
	        		
	        		container.addRowToTable(new DefaultRow(rowKey, cells));
	        		}
	        	
	        	exec.checkCanceled();
		        exec.setProgress(rowCount / (double)rowCountInput, 
		                "Adding row " + rowIndex);
	        	}
	        }
        catch(Exception err)
        	{
        	LOGGER.info(err.getMessage());
        	err.printStackTrace();
        	throw err;
        	}
        finally
        	{
        	container.close();
        	if(con!=null) con.close();
        	}
        
        
        return new BufferedDataTable[]{container.getTable()};
        }
	 
	 @Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException
		{
		
		if(inSpecs==null || inSpecs.length==0 || inSpecs[0]==null) throw new InvalidSettingsException("InputTable Missing");
		 
		 return new DataTableSpec[]{createTableSpec(inSpecs[0])};
	 	
		}
	 
	 private DataTableSpec createTableSpec(DataTableSpec in)
	 	{
		
		DataColumnSpec dcs[]=new DataColumnSpec[in.getNumColumns()+6];
		for(int i=0;i< in.getNumColumns();++i)
			{
			dcs[i]= in.getColumnSpec(i);
			}
		String prefix="snp129.";
		
		dcs[in.getNumColumns()+0]=  new DataColumnSpecCreator(prefix+"chrom", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+1]=  new DataColumnSpecCreator(prefix+"chromStart", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+2]=  new DataColumnSpecCreator(prefix+"chromEnd", org.knime.core.data.def.IntCell.TYPE).createSpec();
		
		dcs[in.getNumColumns()+3]=  new DataColumnSpecCreator(prefix+"class", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+4]=  new DataColumnSpecCreator(prefix+"avHet", org.knime.core.data.def.DoubleCell.TYPE).createSpec();
		dcs[in.getNumColumns()+5]=  new DataColumnSpecCreator(prefix+"func", org.knime.core.data.def.StringCell.TYPE).createSpec();
	
		
        return new DataTableSpec(dcs);
	 	}
		 
	
	
	}
