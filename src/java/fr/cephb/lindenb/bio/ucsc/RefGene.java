package fr.cephb.lindenb.bio.ucsc;

import org.lindenb.bio.ChromosomePosition;
import org.lindenb.bio.Strand;

public class RefGene
	{
	public String name;
	public String name2;
	public ChromosomePosition transcription;
	public ChromosomePosition translation;
	public Strand strand;
	public int exonStart[];
	public int exonEnd[];
	}
