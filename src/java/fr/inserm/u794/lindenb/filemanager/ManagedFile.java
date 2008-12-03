package fr.inserm.u794.lindenb.filemanager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.lindenb.sw.vocabulary.DC;
import org.lindenb.sw.vocabulary.RDF;
import org.lindenb.util.SHA1;

@XmlRootElement(name="file")
public class ManagedFile implements Serializable
	{
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_FILENAME=".managed-files.xml";
	private static final String FILE_PREFIX="file://";
	private Set<String> tags=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	private File file;
	private String sha1sum ="";
	private String label="";
	private Date creation=new Date();
	private String description="";
	private Delimiter delimiter=Delimiter.tab;
	
	public ManagedFile(File file,Delimiter delimiter) throws IOException
		{
		this.file=file;
		if(!this.file.exists()) throw new IOException(file.toString()+" does not exists");
		if(!this.file.isFile()) throw new IOException(file.toString()+" is not a file");
		this.label=file.getName();
		this.creation=new Date();
		this.description=file.getName();
		this.sha1sum= SHA1.encrypt(file);
		this.delimiter=delimiter;
		}
	
	public ManagedFile(XMLEventReader reader,StartElement start) throws XMLStreamException
		{
		final QName dcSubject= new QName(DC.NS,"subject","dc");
		final QName dcDate= new QName(DC.NS,"date","dc");
		Attribute att= start.getAttributeByName(new QName(RDF.NS,"about"));
		if(att==null || !att.getValue().startsWith(FILE_PREFIX))
			{
			throw new XMLStreamException("@rdf;about missing");
			}
		this.file= new File(att.getValue().substring(FILE_PREFIX.length()));
		this.label=this.file.getName();
		while(reader.hasNext())
			{
			XMLEvent evt=reader.nextEvent();
			if(evt.isStartElement())
				{
				StartElement e= evt.asStartElement();
				String localName=e.getName().getLocalPart();
				if(e.getName().equals(dcSubject))
					{
					this.tags.add(reader.getElementText());
					}
				else if(localName.equals("sha1sum"))
					{
					this.sha1sum= reader.getElementText();
					}
				else if(localName.equals("description"))
					{
					this.description= reader.getElementText();
					}
				else if(localName.equals("label"))
					{
					this.label= reader.getElementText();
					}
				else if(localName.equals("delimiter"))
					{
					this.delimiter= Delimiter.valueOf(reader.getElementText());
					}
				else if(e.getName().equals(dcDate))
					{
					SimpleDateFormat fmt= new SimpleDateFormat();
					try {
						this.creation= fmt.parse(reader.getElementText());
						} 
					catch (ParseException e1)
						{
						throw new XMLStreamException(e1);
						}
					}
				}
			else if(evt.isEndElement())
				{
				EndElement e= evt.asEndElement();
				String localName=e.getName().getLocalPart();
				if(localName.equals("File"))
					{
					return;
					}
				}
			}
		}
	
	public void writeXML(XMLStreamWriter out) throws XMLStreamException
		{
		out.writeStartElement("File");
		out.writeAttribute("rdf",RDF.NS,"about",FILE_PREFIX+this.file.getPath());
		for(String s:this.tags)
			{
			out.writeStartElement("dc","subject",DC.NS);
			out.writeCharacters(s);
			out.writeEndElement();
			}
		SimpleDateFormat fmt= new SimpleDateFormat();
		out.writeStartElement("dc","date",DC.NS);out.writeCharacters(fmt.format(this.creation));out.writeEndElement();
		out.writeStartElement("delimiter"); out.writeCharacters(this.delimiter.name());out.writeEndElement();
		out.writeStartElement("label"); out.writeCharacters(this.label);out.writeEndElement();
		out.writeStartElement("sha1sum"); out.writeCharacters(this.sha1sum);out.writeEndElement();
		out.writeStartElement("description"); out.writeCharacters(this.description);out.writeEndElement();
		
		out.writeEndElement();
		}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Delimiter getDelimiter() {
		return delimiter;
	}


	public void setDelimiter(Delimiter delimiter) {
		this.delimiter = delimiter;
		}
	
	public Set<String> getTags() {
		return tags;
	}
	
	public String getTagsAsString() {
		StringBuilder b= new StringBuilder();
		for(String s:getTags())
			{
			if(b.length()!=0) b.append(" ");
			b.append(s);
			}
		return b.toString();
	}

	public File getFile() {
		return file;
	}

	public String getSha1sum() {
		return sha1sum;
	}

	public String getLabel() {
		return label;
	}

	public Date getCreation() {
		return creation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManagedFile other = (ManagedFile) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getFile().toString();
		}
	
	}
