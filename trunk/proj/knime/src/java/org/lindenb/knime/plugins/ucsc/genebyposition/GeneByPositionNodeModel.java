package org.lindenb.knime.plugins.ucsc.genebyposition;


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
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;

import org.lindenb.knime.plugins.ucsc.sql.UCSCByPositionNodeModel;


public class GeneByPositionNodeModel extends UCSCByPositionNodeModel
	{
	private static final Logger LOGGER= Logger.getLogger(GeneByPositionNodeModel.class.getName());
	

	public GeneByPositionNodeModel()
		{
		super();
		LOGGER.setLevel(Level.ALL);
		}
	
	 @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception
        {
    	 // check input data
        if (inData == null || inData.length != 1
                || inData[0] == null) {
            throw new IllegalArgumentException("No input data available.");
        	}
        
        final BufferedDataTable inputTable = inData[0];
        if (inputTable.getRowCount() < 1) {
            setWarningMessage("Empty input table found");
        	}
        
        final int column4chrom = findColumnByName(this.m_target_chrom,inputTable.getDataTableSpec());
        final int column4start = findColumnByName(this.m_target_chromStart,inputTable.getDataTableSpec()); 
        final int column4end = findColumnByName(this.m_target_chromEnd,inputTable.getDataTableSpec()); 
        final int extend= this.m_target_extend.getIntValue();
        final boolean only_one= this.m_target_only_one.getBooleanValue();
        
        
        
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
	        	"select chrom,strand,txStart,txEnd,cdsStart,cdsEnd,exonCount,name,name2 from refGene " +
	        	" where " +
	        	" chrom=? and "+
	        	" NOT(txEnd < ? or ? > txStart)"
	        	+(only_one?" limit 1":"")	
	        	);
	        for(DataRow row: inputTable)
	        	{
	        	++rowCount;
	        	DataCell keyChrom=row.getCell(column4chrom);
				if(keyChrom.isMissing()) continue;
				
				//chrom
				String chrom= null;
				if((keyChrom instanceof IntCell) )
					{
					chrom="chr"+IntCell.class.cast(keyChrom).getIntValue();
					}
				else if((keyChrom instanceof StringCell) )
					{
					chrom= StringCell.class.cast(keyChrom).getStringValue();
					}
				else
					{
					LOGGER.info("not a chrom cell");
					continue;
					}
				
	        	//seach start value
				int start=-1;
				DataCell keyStart=row.getCell(column4start);
				if(keyStart.isMissing()) continue;
				if((keyStart instanceof IntCell) )
					{
					start=IntCell.class.cast(keyStart).getIntValue();
					}
				else if((keyStart instanceof StringCell) )
					{
					try
						{
						start= Integer.parseInt(StringCell.class.cast(keyStart).getStringValue());
						}
					catch(NumberFormatException err)
						{
						LOGGER.info("not a number "+keyStart);
						continue;
						}
					}
				else
					{
					LOGGER.info("not a number "+keyStart);
					continue;
					}
				start -= extend;
				
				
				//seach e,d value
				int end=-1;
				DataCell keyEnd=row.getCell(column4end);
				if(keyEnd.isMissing()) continue;
				if((keyEnd instanceof IntCell) )
					{
					end=IntCell.class.cast(keyEnd).getIntValue();
					}
				else if((keyEnd instanceof StringCell) )
					{
					try
						{
						end= Integer.parseInt(StringCell.class.cast(keyEnd).getStringValue());
						}
					catch(NumberFormatException err)
						{
						LOGGER.info("not a number "+keyEnd);
						continue;
						}
					}
				else
					{
					LOGGER.info("not a number "+keyEnd);
					continue;
					}
				end += extend;
				
				
	        	
	        	pstmt.setString(1, chrom);
	        	pstmt.setInt(2, start);
	        	pstmt.setInt(3, end);
	        	ResultSet sqlrow = pstmt.executeQuery();
	        	boolean found=false;
	        	while(sqlrow.next())
	        		{
	        		found=true;
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+9];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell(sqlrow.getString(1));//chrom
	        		cells[row.getNumCells()+1]= new StringCell(sqlrow.getString(2));//strand
	        		
	        		cells[row.getNumCells()+2]= new IntCell(sqlrow.getInt(3));//txStart
	        		cells[row.getNumCells()+3]= new IntCell(sqlrow.getInt(4));//txEnd
	        		
	        		cells[row.getNumCells()+4]= new IntCell(sqlrow.getInt(5));//cdsStart
	        		cells[row.getNumCells()+5]= new IntCell(sqlrow.getInt(6));//cdsEnd
	        		
	        		cells[row.getNumCells()+6]= new IntCell(sqlrow.getInt(7));//exonCount
	        		
	        		cells[row.getNumCells()+7]= new StringCell(sqlrow.getString(8));//name1
	        		cells[row.getNumCells()+8]= new StringCell(sqlrow.getString(9));//name1

	        		
	        		container.addRowToTable(new DefaultRow(rowKey, cells));
	        		
	        		if(only_one) break;
	        		}
	        	sqlrow.close();
	        	
	        	if(!found)
	        		{
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+9];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell("#");//chrom
	        		cells[row.getNumCells()+1]= new StringCell("#");//strand
	        		
	        		cells[row.getNumCells()+2]= new IntCell(-1);//txStart
	        		cells[row.getNumCells()+3]= new IntCell(-1);//txEnd
	        		
	        		cells[row.getNumCells()+4]= new IntCell(-1);//cdsStart
	        		cells[row.getNumCells()+5]= new IntCell(-1);//cdsEnd
	        		
	        		cells[row.getNumCells()+6]= new IntCell(-1);//exonCount
	        		
	        		cells[row.getNumCells()+7]= new StringCell("#");//name1
	        		cells[row.getNumCells()+8]= new StringCell("#");//name1
	        		
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
		
		DataColumnSpec dcs[]=new DataColumnSpec[in.getNumColumns()+9];
		for(int i=0;i< in.getNumColumns();++i)
			{
			dcs[i]= in.getColumnSpec(i);
			}
		dcs[in.getNumColumns()+0]=  new DataColumnSpecCreator("refGene.chrom", org.knime.core.data.def.StringCell.TYPE).createSpec();
	
		dcs[in.getNumColumns()+1]=  new DataColumnSpecCreator("refGene.strand", org.knime.core.data.def.StringCell.TYPE).createSpec();
		
		dcs[in.getNumColumns()+2]=  new DataColumnSpecCreator("refGene.txStart", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+3]=  new DataColumnSpecCreator("refGene.txEnd", org.knime.core.data.def.IntCell.TYPE).createSpec();
	
		dcs[in.getNumColumns()+4]=  new DataColumnSpecCreator("refGene.cdsStart", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+5]=  new DataColumnSpecCreator("refGene.cdsEnd", org.knime.core.data.def.IntCell.TYPE).createSpec();
		
		dcs[in.getNumColumns()+6]=  new DataColumnSpecCreator("refGene.exonCount", org.knime.core.data.def.IntCell.TYPE).createSpec();
		
		dcs[in.getNumColumns()+7]=  new DataColumnSpecCreator("refGene.name", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+8]=  new DataColumnSpecCreator("refGene.name2", org.knime.core.data.def.StringCell.TYPE).createSpec();
		
        return new DataTableSpec(dcs);
	 	}
		 
	
	
	}
