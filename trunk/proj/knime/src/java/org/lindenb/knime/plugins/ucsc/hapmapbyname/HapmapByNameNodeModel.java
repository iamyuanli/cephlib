package org.lindenb.knime.plugins.ucsc.hapmapbyname;


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
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import org.lindenb.knime.plugins.ucsc.sql.UCSCByNameNodeModel;


public class HapmapByNameNodeModel extends UCSCByNameNodeModel
	{
	private static final Logger LOGGER= Logger.getLogger(HapmapByNameNodeModel.class.getName());
	
	static final String[] POPULATIONS=new String[]{"CEU","CHB","JPT","YRI"};
	static final String KEY_POPULATION="hapmap.population";

	/** database */
    private final SettingsModelString m_population= new SettingsModelString(KEY_POPULATION,POPULATIONS[0]);
	
	public HapmapByNameNodeModel()
		{
		super();
		LOGGER.setLevel(Level.ALL);
		addSettings(m_population);
		}
	
	 @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception
        {
    	if (super.m_target_column.getColumnName() == null) {
            setWarningMessage("Columns was not selected.");
        	}
    	
    	if(m_population.getStringValue()==null || m_population.getStringValue().length()==0)
    		{
    		setWarningMessage("Population was not selected.");
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
	        	"select chrom,chromStart,chromEnd,strand,observed,allele1,homoCount1,allele2,homoCount2,heteroCount from hapmapSnps"+
	        		this.m_population.getStringValue().trim()+
	        		" where name=?");
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
	        		DataCell cells[]=new DataCell[row.getNumCells()+10];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell(sqlrow.getString(1));//chrom
	        		cells[row.getNumCells()+1]= new IntCell(sqlrow.getInt(2));//start
	        		cells[row.getNumCells()+2]= new IntCell(sqlrow.getInt(3));//end
	        		cells[row.getNumCells()+3]= new StringCell(sqlrow.getString(4));//strand
	        		cells[row.getNumCells()+4]= new StringCell(sqlrow.getString(5));//observed
	        		cells[row.getNumCells()+5]= new StringCell(sqlrow.getString(6));//a1
	        		cells[row.getNumCells()+6]= new IntCell(sqlrow.getInt(7));//count homo1
	        		cells[row.getNumCells()+7]= new StringCell(sqlrow.getString(8));//a2
	        		cells[row.getNumCells()+8]= new IntCell(sqlrow.getInt(9));//count homo2
	        		cells[row.getNumCells()+9]= new IntCell(sqlrow.getInt(10));//heterocount
	  
	        		
	        		container.addRowToTable(new DefaultRow(rowKey, cells));
	        		}
	        	
	        	if(!found)
	        		{
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+10];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell("#");
	        		cells[row.getNumCells()+1]= new IntCell(-1);
	        		cells[row.getNumCells()+2]= new IntCell(-1);
	        		cells[row.getNumCells()+3]= new StringCell("#");//strand
	        		cells[row.getNumCells()+4]= new StringCell("#");//observed
	        		cells[row.getNumCells()+5]= new StringCell("#");//a1
	        		cells[row.getNumCells()+6]= new IntCell(-1);//count homo1
	        		cells[row.getNumCells()+7]= new StringCell("#");//a2
	        		cells[row.getNumCells()+8]= new IntCell(-1);//count homo2
	        		cells[row.getNumCells()+9]= new IntCell(-1);//heterocount
	        		
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
        	throw err;
        	}
        finally
        	{
        	if(con!=null) con.close();
        	}
        
        container.close();
        return new BufferedDataTable[]{container.getTable()};
        }
	 
	 @Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		 if(inSpecs==null || inSpecs.length==0 || inSpecs[0]==null) throw new InvalidSettingsException("InputTable Missing");
		 if(m_population==null || m_population.getStringValue()==null) throw new InvalidSettingsException("population Missing");
		return new DataTableSpec[]{createTableSpec(inSpecs[0])};
	 	}
	 
	 private DataTableSpec createTableSpec(DataTableSpec in)
	 	{
		
		DataColumnSpec dcs[]=new DataColumnSpec[in.getNumColumns()+10];
		for(int i=0;i< in.getNumColumns();++i)
			{
			dcs[i]= in.getColumnSpec(i);
			}
		String prefix= "hapmap."+m_population.getStringValue().toLowerCase()+".";
		
		dcs[in.getNumColumns()+0]=  new DataColumnSpecCreator(prefix+"chrom", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+1]=  new DataColumnSpecCreator(prefix+"chromStart", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+2]=  new DataColumnSpecCreator(prefix+"chromEnd", org.knime.core.data.def.IntCell.TYPE).createSpec();
		
		dcs[in.getNumColumns()+3]=  new DataColumnSpecCreator(prefix+"strand", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+4]=  new DataColumnSpecCreator(prefix+"observed", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+5]=  new DataColumnSpecCreator(prefix+"allele1", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+6]=  new DataColumnSpecCreator(prefix+"homoCount1", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+7]=  new DataColumnSpecCreator(prefix+"allele2", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+8]=  new DataColumnSpecCreator(prefix+"homoCount2", org.knime.core.data.def.IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+9]=  new DataColumnSpecCreator(prefix+"heteroCount", org.knime.core.data.def.IntCell.TYPE).createSpec();
		
        return new DataTableSpec(dcs);
	 	}
		 
	
	
	}
