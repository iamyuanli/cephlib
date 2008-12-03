/**
 * 
 */
package fr.inserm.u794.lindenb.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.lindenb.sw.vocabulary.DC;
import org.lindenb.sw.vocabulary.RDF;
import org.lindenb.swing.table.GenericTableModel;

public class ManagedFilesModel extends GenericTableModel<ManagedFile>
	{
	ManagedFilesModel()
		{
		
		}

	
	public void read(File file) throws IOException,XMLStreamException
		{
		XMLInputFactory f= XMLInputFactory.newInstance();
		f.setProperty(XMLInputFactory.IS_COALESCING, true);
		f.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
		f.setProperty(XMLInputFactory.IS_VALIDATING, false);
		f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		f.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
		List<ManagedFile> newitems= new ArrayList<ManagedFile>();
		InputStream in=new FileInputStream(file);
		XMLEventReader reader= f.createXMLEventReader(in);
		while(reader.hasNext())
			{
			XMLEvent evt=reader.nextEvent();
			if(evt.isStartElement())
				{
				StartElement e= evt.asStartElement();
				String localName=e.getName().getLocalPart();
				if(localName.equals("File"))
					{
					newitems.add(new ManagedFile(reader,e));
					}
				}
			}
		in.close();
		clear();
		addAll(newitems);
		}
	public void save(File file) throws IOException,XMLStreamException
		{
		FileOutputStream fout= new FileOutputStream(file);
		save(fout);
		fout.close();
		}
	
	public void save(OutputStream fout) throws IOException,XMLStreamException
		{
		XMLOutputFactory f=  XMLOutputFactory.newInstance();
		XMLStreamWriter out=f.createXMLStreamWriter(fout, "UTF-8");
		out.writeStartDocument();
		
		out.writeStartElement("rdf", "RDF", RDF.NS);
		out.writeNamespace("rdf",RDF.NS);
		out.writeNamespace("dc",DC.NS);
		out.writeNamespace(null,"http://stats.cephb.fr/");
		
		for(int i=0;i< getElementCount();++i)
			{
			elementAt(i).writeXML(out);
			}
		
		out.writeEndElement();
		out.writeEndDocument();
		out.flush();
		out.close();
		}
	
	/* (non-Javadoc)
	 * @see org.lindenb.swing.table.AbstractGenericTableModel#getValueOf(java.lang.Object, int)
	 */
	@Override
	public Object getValueOf(ManagedFile f, int column)
		{
		switch(column)
			{
			case 0: return f.getLabel();
			case 1: return f.getFile().getPath();
			case 2: return f.getCreation();
			case 3: return f.getDelimiter().name();
			case 4: return f.getDescription();
			}
		return null;
		}

	@Override
	public String getColumnName(int column) {
		switch(column)
		{
		case 0: return "Label";
		case 1: return "Path";
		case 2: return "Creation";
		case 3: return "Delimiter";
		case 4: return "Comment";
		}
		return null;
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}

}
