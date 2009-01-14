package fr.cephb.lindenb.dochandler.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;


@Entity
@Table(name="document")
@NamedQueries({
 	@NamedQuery(
 	name="document.find.cell",
    query="SELECT c FROM Cell c WHERE c.documentId = :documentId and c.row= :row and c.column= :column"
	),
	@NamedQuery(
 	name="document.find.row",
    query="SELECT c FROM Cell c WHERE c.documentId = :documentId and c.row= :row"
	)
})
@SuppressWarnings("unused")
public class Document
	{
	/** document id */
	private int id;
	/** columns */
	private int columnCount=-1;
	/** rows */
	private int rowCount=-1;
	/** columns specs */
	private List<ColumnSpec> columns=new ArrayList<ColumnSpec>(1);
	/** ontology class */
	private Set<OntClass> ontClasses= new HashSet<OntClass>(1);
	/** name */
	private String filename;
	/** description */
	private String description;
	
	public Document()
		{
		
		}	
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
		}
	
	
	private void setId(int id) {
		this.id = id;
		}
	
	@Column(name="columnCount")
	public int getColumnCount() {
		return columnCount;
		}
	
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		}
	
	@Column(name="rowCount")
	public int getRowCount() {
		return rowCount;
		}
	
	
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="document",targetEntity=ColumnSpec.class)
	public List<ColumnSpec> getColumnSpecs()
		{
		return this.columns;
		}
	
	public String getFilename() {
		return filename;
		}
	
	
	public void setFilename(String filename) {
		this.filename = filename;
		}
	
	public String getDescription() {
		return description;
		}
	
	public void setDescription(String description) {
		this.description = description;
		}
	
	public void setColumnSpecs(List<ColumnSpec> columns) {
		this.columns = columns;
		}
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="document2class",
        joinColumns=
            @JoinColumn(name="document_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="class_id", referencedColumnName="id")
        )
	public Set<OntClass> getOntClasses() {
		return ontClasses;
		}
	
	public void setOntClasses(Set<OntClass> ontClasses) {
		this.ontClasses = ontClasses;
		}
	
	
	public ColumnSpec getColumnSpec(int i)
		{
		for(ColumnSpec spec: getColumnSpecs())
			{
			if(spec.getColumnIndex()==i) return spec;
			}
		return null;
		}
	
	
	
	public String getCell(EntityManager mgr,int rowIndex,int columnIndex)
		{
		Query query= mgr.createNamedQuery("document.find.cell");
		query.setParameter("documentId", getId());
		query.setParameter("row", rowIndex);
		query.setParameter("column", columnIndex);
		query.setMaxResults(1);
		Cell cell= Cell.class.cast(query.getSingleResult());
		if(cell==null) return null;
		return cell.getContent();
		}
	
	public String[] getRow(EntityManager mgr,int rowIndex)
		{
		String[] row= new String[getColumnCount()];
		Query query= mgr.createNamedQuery("document.find.row");
		query.setParameter("documentId", getId());
		query.setParameter("row", rowIndex);
		query.setMaxResults(getColumnCount());
		List<?> L=query.getResultList();
		for(Object o:L)
			{
			Cell cell= Cell.class.cast(o);
			row[cell.getColumn()]=cell.getContent();
			}
		return row;
		}
	
	}
