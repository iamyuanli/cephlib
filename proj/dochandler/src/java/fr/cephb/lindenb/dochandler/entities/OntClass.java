package fr.cephb.lindenb.dochandler.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;





@Entity
@Table(name="ontclass")
public class OntClass
	{
	/** id */
	private int id;
	/** name of this class */
	private String name;
	/** description of this class */
	private String description;
	/** ontology */
	private Ontology ontology;
	/** parent */
	private OntClass parent;
	/** children */
	private List<OntClass> children;
	
	public OntClass()
		{
		
		}
	
	public OntClass(Ontology ontology,OntClass parent,String name,String description)
		{
		setOntology(ontology);
		setParent(parent);
		setName(name);
		setDescription(description);
		}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)  
	@Column(name="id")
	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
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
	
	
	@ManyToOne
	@JoinColumn(name="ontology_id")
	public Ontology getOntology() {
		return ontology;
		}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	public OntClass getParent()
		{
		return this.parent;
		}
	
	public void setParent(OntClass parent) {
		this.parent = parent;
		}
	
	@Transient
	public String getQName()
		{
		Ontology ont= getOntology();
		return ont==null?getName():ont.getPrefix()+":"+getName();
		}
	
	private void setOntology(Ontology ontology) {
		this.ontology = ontology;
		}
	
	public String getDescription()
		{
		return this.description==null?getName():this.description;
		}
	
	public void setDescription(String s)
		{
		this.description=s;
		}
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="parent",targetEntity=OntClass.class)
	public List<OntClass> getChildren() {
		return children;
		}
	
	public void setChildren(List<OntClass> children) {
		this.children = children;
		}
	
	@Transient
	public boolean isLeaf()
		{	
		List<OntClass> list=getChildren();
		return list==null || list.isEmpty();
		}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		OntClass other = (OntClass) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getQName();
		}
}
