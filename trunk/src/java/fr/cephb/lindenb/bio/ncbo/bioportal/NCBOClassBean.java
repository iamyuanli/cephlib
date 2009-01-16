package fr.cephb.lindenb.bio.ncbo.bioportal;

import java.util.List;

public interface NCBOClassBean extends NCBOConceptBean
	{
	public NCBOConceptBean getSuperClass();
	public List<NCBOConceptBean> getSubClasses();
	}
