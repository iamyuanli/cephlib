package fr.cephb.lindenb.dochandler.entities;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityMgrSingleton
{
private static EntityManagerFactory EMF=null;
private EntityMgrSingleton()
	{
	}
public static EntityManager getEntityManager()
	{
	if(EMF==null)
		{
		synchronized (EntityMgrSingleton.class)
			{
			if(EMF==null)
				{
				 EMF = javax.persistence.Persistence.createEntityManagerFactory(
							"docsPU"
							);
				 
				}
			}
		}
	if(!EMF.isOpen())
		{
		EMF=null;
		return getEntityManager();
		}
	return EMF.createEntityManager();
	}

public static void close()
	{
	if(EMF!=null)
		{
		synchronized (EntityMgrSingleton.class)
			{
			if(EMF!=null) EMF.close();
			EMF=null;
			}
		}
	}

}
