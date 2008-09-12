<?xml version='1.0' ?>
<xsl:stylesheet
        version='1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
        >

<xsl:output method='html' indent="no"/>

<xsl:template match="/">
<div><xsl:apply-templates select="Entrezgene-Set"/></div>
</xsl:template>

<xsl:template match="Entrezgene-Set">
<div><xsl:apply-templates select="Entrezgene"/></div>
</xsl:template>

<xsl:template match="Entrezgene">
<div>
<xsl:apply-templates select="Entrezgene_track-info"/>
<xsl:apply-templates select="Entrezgene_gene"/>
<xsl:apply-templates select="Entrezgene_summary"/>
<xsl:apply-templates select="Entrezgene_locus"/>
</div>
<hr/>
</xsl:template>

<xsl:template match="Entrezgene_track-info">
<div>
<b>Gene ID</b>:<xsl:value-of select="Gene-track/Gene-track_geneid"/><br/>
</div>
</xsl:template>


<xsl:template match="Entrezgene_gene">
<div>
<xsl:apply-templates select="Gene-ref"/>
</div>
</xsl:template>

<xsl:template match="Gene-ref">
<b>Locus</b>:<xsl:value-of select="Gene-ref_locus"/><br/>
<b>Desc</b>:<xsl:value-of select="Gene-ref_desc"/><br/>
<b>MapLoc</b>:<xsl:value-of select="Gene-ref_maploc"/><br/>
</xsl:template>

<xsl:template match="Entrezgene_summary">
<!-- <b>Summary</b>:<xsl:value-of select="."/><br/> -->
</xsl:template>

<xsl:template match="Entrezgene_locus">

</xsl:template>


</xsl:stylesheet>

