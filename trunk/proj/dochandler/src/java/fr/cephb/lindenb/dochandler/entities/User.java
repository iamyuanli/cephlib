package fr.cephb.lindenb.dochandler.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.lindenb.util.SHA1;






@Entity
@Table(name="user")
@SuppressWarnings("unused")
@NamedQueries({
 	@NamedQuery(
 	name="user.find.by.name",
    query="SELECT u FROM User u WHERE u.name = :userName"
	),
	@NamedQuery(
 	name="user.find.by.name_password",
    query="SELECT u FROM User u WHERE u.name = :userName and u.sha1Password= :sha1Password"
	)
})
public class User
	implements Serializable
	{
	private static final long serialVersionUID = 1L;
	/** id */
	private int id;
	/** name of user */
	private String name;
	/** sha1 */
	private String sha1Password;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	@Column(name="name",updatable=false,unique=true,length=50,nullable=false)
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	@Column(name="sha1Password",updatable=false,nullable=false,length=50)
	@Basic(fetch=FetchType.LAZY)
	public String getSha1Password() {
		return sha1Password;
		}
	
	private void setSha1Password(String sha1Password) {
		this.sha1Password = sha1Password;
		}
	
	public static User findByName(String user)
		{
		Query query=EntityMgrSingleton.getEntityManager().createNamedQuery(
				"user.find.by.name");
		query.setParameter("userName",user);
		query.setMaxResults(1);
		
		try
			{
			return User.class.cast(query.getSingleResult());
			}
		catch(javax.persistence.NoResultException err)
			{
			System.err.println("Cannot log as "+user);
			return null;	
			}
		}
	
	@Override
	public String toString() {
		return getName();
		}
	
	public static User findByName(String user,String password)
		{
		Query query=EntityMgrSingleton.getEntityManager().createNamedQuery(
				"user.find.by.name_password");
		query.setParameter("userName",user);
		query.setParameter("sha1Password", SHA1.encrypt(password));
		query.setMaxResults(1);
		try
			{
			return User.class.cast(query.getSingleResult());
			}
		catch(javax.persistence.NoResultException err)
			{
			System.err.println("Cannot log as "+user+" "+SHA1.encrypt(password));
			err.printStackTrace();
			return null;	
			}
		}	
	}
