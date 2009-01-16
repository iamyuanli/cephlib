<?xml version='1.0' ?>
<xsl:stylesheet
        version='1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
        >

<xsl:output method='html' indent="no" omit-xml-declaration="yes"/>

<xsl:template match="/">
<HTML><BODY><xsl:apply-templates select="success"/></BODY></HTML>
</xsl:template>

<xsl:template match="success">
<h1><xsl:value-of select="accessedResource"/></h1>
<xsl:apply-templates select="data"/>
</xsl:template>

<xsl:template match="data">
<xsl:apply-templates select="classBean"/>
</xsl:template>

<xsl:template match="classBean">
<dl>
<dt>Id</dt><dd><xsl:value-of select="id"/></dd>
<dt>Label</dt><dd><xsl:value-of select="label"/></dd>
<dt>Relations</dt><dd><xsl:apply-templates select="relations"/></dd>
</dl>
</xsl:template>

<xsl:template match="relations">
<ul>
<xsl:for-each select="entry">
<li>
	<xsl:apply-templates/>
</li>
</xsl:for-each>
</ul>
</xsl:template>


<xsl:template match="list"><ul>
<xsl:for-each select="string|int|classBean"><li><xsl:apply-templates/></li></xsl:for-each>
</ul></xsl:template>


<xsl:template match="string">
<span style="color:blue;"><xsl:value-of select="."/></span>
</xsl:template>

<xsl:template match="int">
<xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>

