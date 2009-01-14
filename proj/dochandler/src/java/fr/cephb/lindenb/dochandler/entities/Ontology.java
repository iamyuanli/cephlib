package fr.cephb.lindenb.dochandler.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.lindenb.util.Debug;




@Entity
@Table(name="ontology")
public class Ontology
	{
	/** id */
	private int id;
	/** name of this ontology */
	private String name;
	/** prefix of this ontology */
	private String prefix;
	/** nodes */
	private List<OntClass> nodes= new ArrayList<OntClass>(1);
	
	@Id
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
	
	@Column(name="prefix")
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="ontology",targetEntity=OntClass.class)
	public List<OntClass> getNodes()
		{
		return nodes;
		}
	
	@SuppressWarnings("unused")
	private void setNodes(List<OntClass> nodes) {
		this.nodes = nodes;
		}
	
	@Transient
	public OntClass getRoot()
		{
		for(OntClass c:getNodes())
			{
			if(c.getParent()==null) return c;
			}
		return null;
		}
	
	public TreeModel asTreeModel()
		{
		System.err.println(System.currentTimeMillis());
		OntClass root=null;
		HashMap<Integer, List<OntClass>> hash= new HashMap<Integer, List<OntClass>>();
		
		for(OntClass c:getNodes())
			{
			OntClass parent= c.getParent();
			if(parent==null)
				{
				root= c;
				}
			else
				{
				List<OntClass> children= hash.get(parent.getId());
				if(children==null)
					{
					children=new ArrayList<OntClass>();
					hash.put(parent.getId(),children);
					}
				children.add(c);
				}
			}
		DefaultMutableTreeNode n= new DefaultMutableTreeNode(root);
		fill_(root,n,hash);
		System.err.println(System.currentTimeMillis());
		return new DefaultTreeModel(n);
		}
	
	private void fill_(OntClass root,DefaultMutableTreeNode n,HashMap<Integer, List<OntClass>> hash)
		{
		List<OntClass> L= hash.get(root.getId());
		if(L==null) return;
		for(OntClass c: hash.get(root.getId()))
			{
			DefaultMutableTreeNode n2= new DefaultMutableTreeNode(c);
			n.add(n2);
			fill_(c,n2,hash);
			}
		}
	
	public static void main(String[] args)
		{
		try {
			Debug.setDebugging(true);
			Debug.debug("Start");
			EntityManager mgr= EntityMgrSingleton.getEntityManager();
			Debug.debug("Find");
			Ontology o=mgr.find(Ontology.class, 1);
			if(o==null) return;
			Debug.debug(o.getName());
			Debug.debug(o.getRoot());
			for(OntClass c: o.getRoot().getChildren())
				{
				Debug.debug(c.getName());
				}
			} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		}

}
