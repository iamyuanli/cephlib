package fr.cephb.lindenb.dochandler.entities;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name="columnSpec")
public class ColumnSpec
	{
	private int id=0;
	private Document document;
	private String name;
	private String description;
	private int columnIndex=-1;
	private Set<OntClass> ontClasses= new HashSet<OntClass>(1);
	
	public ColumnSpec()
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
	
	@Column(name="name")
	public String getName() {
		return name;
		}
	
	public void setName(String name) {
		this.name = name;
		}
	
	public String getDescription() {
		return description;
		}
	
	public void setDescription(String description) {
		this.description = description;
		}
	
	@ManyToOne
	@JoinColumn(name="document_id")
	public Document getDocument()
		{
		return this.document;
		}
	
	public void setDocument(Document document) {
		this.document = document;
		}
	
	@Column(name="colIndex")
	public int getColumnIndex() {
		return columnIndex;
		}
	
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
		}
	
	@Override
	public int hashCode() {
		return getColumnIndex()*31+1;
		}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) return true;
		if(obj==null || !obj.getClass().equals(getClass())) return false;
		return ColumnSpec.class.cast(obj).getColumnIndex()==getColumnIndex();
		}
	
	

	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="column2class",
        joinColumns=
            @JoinColumn(name="column_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="class_id", referencedColumnName="id")
        )
	public Set<OntClass> getOntClasses() {
		return ontClasses;
		}
	
	public void setOntClasses(Set<OntClass> ontClasses) {
		this.ontClasses = ontClasses;
		}
	
	
	@Override
	public String toString() {
		return getName();
		}
	
	}
