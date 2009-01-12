package org.lindenb.knime.plugins.ncbi.omim;



import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



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

import org.lindenb.knime.plugins.ncbi.NCBIEFetchNodeModel;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class OmimNodeModel extends NCBIEFetchNodeModel
	{
	private static final Logger LOGGER= Logger.getLogger(OmimNodeModel.class.getName());
	

	public OmimNodeModel()
		{
		super();
		LOGGER.setLevel(Level.ALL);
		}
	
	
	static class Publication
		{
		String title="";
		int year=0;
		int pmid=0;
		String journal="";
		}
	
	
	static class OmimHandler
		extends DefaultHandler
		{
		ArrayList<Publication> publications= new ArrayList<Publication>();
		StringBuilder b= new StringBuilder();
		String title="";
		Publication currentPub=null;
		
		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException
			{
			if(name.equals("Mim-reference"))
				{
				currentPub=new Publication();
				}
			b.setLength(0);
			}
		
		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException
			{
			if(name.equals("Mim-entry_title"))
				{
				title= b.toString();
				}
			else if(name.equals("Mim-reference") && currentPub!=null)
				{
				publications.add(currentPub);
				currentPub=null;
				}
			else if(currentPub!=null)
				{
				if(name.equals("Mim-reference_citationTitle"))
					{
					currentPub.title= b.toString();
					}
				else if(name.equals("Mim-date_year"))
					{
					currentPub.year= new Integer(b.toString());
					}
				else if(name.equals("Mim-reference_pubmedUID"))
					{
					currentPub.pmid= new Integer(b.toString());
					}
				else if(name.equals("Mim-reference_journal"))
					{
					currentPub.journal= b.toString();
					}
				}
			b.setLength(0);
			}
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			b.append(ch, start, length);
			}
		
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
        
        if(super.m_column_id.getColumnName()==null ||
        	super.m_column_id.getColumnName().trim().length()==0)
        	{
        	throw new InvalidSettingsException("column for omim id is undefined");
        	}
        
        final int column4id = findColumnByName(this.m_column_id,inputTable.getDataTableSpec());
        
        
        SAXParserFactory saxParserFactory= SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        SAXParser parser= saxParserFactory.newSAXParser();
        
        int rowCountInput= inputTable.getRowCount();
        int rowCount=-1;
        int rowIndex=0;
        DataTableSpec dataTableSpec= createTableSpec(inData[0].getDataTableSpec());
        BufferedDataContainer container=exec.createDataContainer(dataTableSpec);
        
      
        try
	        {
        	
	        for(DataRow row: inputTable)
	        	{
	        	++rowCount;
	        	DataCell keyChrom=row.getCell(column4id);
				if(keyChrom.isMissing()) continue;
				

				if(!(keyChrom instanceof IntCell) )
					{
					LOGGER.info("not a int cell");
					continue;
					}
				int omimid= IntCell.class.cast(keyChrom).getIntValue();
				if(omimid<=0) continue;
				String url=  "http://www.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=omim&id="+omimid+"&retmode=xml&rettype=full";
				OmimHandler handler= new OmimHandler();
				parser.parse(url, handler);

	   
	        	for(Publication pub: handler.publications)
	        		{
	        		RowKey rowKey= RowKey.createRowKey(++rowIndex);
	        		DataCell cells[]=new DataCell[row.getNumCells()+5];
	        		for(int i=0;i< row.getNumCells();++i)
	        			{
	        			cells[i]= row.getCell(i);
	        			}
	        		cells[row.getNumCells()+0]= new StringCell(handler.title);//title
	        		cells[row.getNumCells()+1]= new StringCell(pub.title);//title
	        		cells[row.getNumCells()+2]= new StringCell(pub.journal);//journal
	        		cells[row.getNumCells()+3]= new IntCell(pub.year);//year
	        		cells[row.getNumCells()+4]= new IntCell(pub.pmid);//pmid

	        		
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
		
		DataColumnSpec dcs[]=new DataColumnSpec[in.getNumColumns()+5];
		for(int i=0;i< in.getNumColumns();++i)
			{
			dcs[i]= in.getColumnSpec(i);
			}
		dcs[in.getNumColumns()+0]=  new DataColumnSpecCreator("omim.title", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+1]=  new DataColumnSpecCreator("omim.pubmed.title", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+2]=  new DataColumnSpecCreator("omim.pubmed.journal", org.knime.core.data.def.StringCell.TYPE).createSpec();
		dcs[in.getNumColumns()+3]=  new DataColumnSpecCreator("omim.pubmed.year", IntCell.TYPE).createSpec();
		dcs[in.getNumColumns()+4]=  new DataColumnSpecCreator("omim.pubmed.pmid",IntCell.TYPE).createSpec();
		
	
        return new DataTableSpec(dcs);
	 	}
		 
	
	
	}
