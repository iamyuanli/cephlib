<?xml version='1.0' ?>
<xsl:stylesheet
        version='1.0'
	xmlns:ncbi="http://www.ncbi.nlm.nih.gov/SNP/docsum"
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
        >

<xsl:output method='html' indent="no"/>

<xsl:template match="/">
<div><xsl:apply-templates select="ncbi:ExchangeSet"/></div>
</xsl:template>

<xsl:template match="ncbi:ExchangeSet">
<div><xsl:apply-templates select="ncbi:Rs"/></div>
</xsl:template>

<xsl:template match="ncbi:Rs">
<div>
<h1>Rs<xsl:value-of select="ncbi:Rs_rsId"/></h1>

<table>
    <tr>
	<th>Organism</th>
	<td>
		<xsl:value-of select="ncbi:Rs_organism"/>
	</td>
   </tr>
	<tr><th>Class</th><td><xsl:value-of select="ncbi:Rs_snpClass/@value"/></td></tr>
	<tr><th>Type</th><td><xsl:value-of select="ncbi:Rs_snpType/@value"/></td></tr>
	<tr><th>molType</th><td><xsl:value-of select="ncbi:Rs_molType/@value"/></td></tr>
	<xsl:for-each select="ncbi:Rs_het">
		<tr><th>het. type</th><td><xsl:value-of select="ncbi:Rs_het_type/@value"/></td></tr>
		<tr><th>het. value</th><td><xsl:value-of select="ncbi:Rs_het_value"/></td></tr>
		<tr><th>het. std.Err.</th><td><xsl:value-of select="ncbi:Rs_het_stdError"/></td></tr>
	</xsl:for-each>
</table>



<xsl:apply-templates select="ncbi:Rs_validation"/>
<xsl:apply-templates select="ncbi:Rs_sequence"/>
<xsl:apply-templates select="ncbi:Rs_ss"/>
<xsl:apply-templates select="ncbi:Rs_assembly"/>

</div>
<hr/>
</xsl:template>


<xsl:template match="ncbi:Rs_validation">
<div>
<h3>Validation</h3>
<table>
 <tr><th>Validation By Cluster</th><td><xsl:value-of select="ncbi:Rs_validation_byCluster/@value"/></td></tr>
 <tr><th>Validation By Frequency</th><td><xsl:value-of select="ncbi:Rs_validation_byFrequency/@value"/></td></tr>
 <tr><th>Validation By Other Pop.</th><td><xsl:value-of select="ncbi:Rs_validation_byOtherPop/@value"/></td></tr>
 <tr><th>Validation By Hit2Allele</th><td><xsl:value-of select="ncbi:Rs_validation_by2Hit2Allele/@value"/></td></tr>
 <tr><th>Validation By Hapmap</th><td><xsl:value-of select="ncbi:Rs_validation_byHapMap/@value"/></td></tr>
</table>
</div>
</xsl:template>

<xsl:template match="ncbi:Rs_sequence">
<div>
<h3>Sequence</h3>
<div><xsl:value-of select="ncbi:Rs_sequence_seq5"/><b><xsl:value-of select="ncbi:Rs_sequence_observed"/></b><xsl:value-of select="ncbi:Rs_sequence_seq3"/></div>
</div>
</xsl:template>

<xsl:template match="ncbi:Rs_ss">
<h3>SS</h3>
<table>
 <tr>
  <th>id</th>
  <th>handle</th>
  <th>batchId</th>
  <th>locSnpId</th>
  <th>class</th>
  <th>orient</th>
  <th>buildId</th>
  <th>methodClass</th>
  <th>validated</th>
  <th>sequence</th>
 </tr>
<xsl:for-each select="ncbi:Ss">
 <tr>
  <td><xsl:value-of select="ncbi:Ss_ssId"/></td>
  <td><xsl:value-of select="ncbi:Ss_handle"/></td>
  <td><xsl:value-of select="ncbi:Ss_batchId"/></td>
  <td><xsl:value-of select="ncbi:Ss_locSnpId"/></td>
  <td><xsl:value-of select="ncbi:Ss_subSnpClass"/></td>
  <td><xsl:value-of select="ncbi:Ss_orient/@value"/></td>
  <td><xsl:value-of select="ncbi:Ss_strand/@value"/></td>
  <td><xsl:value-of select="ncbi:Ss_molType/@value"/></td>
  <td><xsl:value-of select="ncbi:Ss_buildId/@value"/></td>
  <td><xsl:value-of select="ncbi:Ss_methodClass/@value"/></td>
  <td><xsl:value-of select="ncbi:Ss_validated/@value"/></td>
  <td><xsl:apply-templates select="ncbi:SS_sequence"/></td>
 </tr>
</xsl:for-each>
</table>
</xsl:template>

<xsl:template match="ncbi:Ss_sequence">
<div><xsl:value-of select="ncbi:Ss_sequence_seq5"/><b><xsl:value-of select="ncbi:Ss_sequence_observed"/></b><xsl:value-of select="ncbi:Ss_sequence_seq3"/></div>
</xsl:template>


<xsl:template match="ncbi:Rs_assembly">
<h3>Assembly</h3>
<table>
<tr>
 <th>dbsnp</th>
 <th>build</th>
 <th>group</th>
 <th>current</th>
 <th>components</th>
</tr>
<xsl:for-each select="ncbi:Assembly">
 <tr>
  <td><xsl:value-of select="ncbi:Assembly_dbSnpBuild"/></td>
  <td><xsl:value-of select="ncbi:Assembly_genomeBuild"/></td>
  <td><xsl:value-of select="ncbi:Assembly_groupLabel"/></td>
  <td><xsl:value-of select="ncbi:Assembly_current//@value"/></td>
  <td><xsl:apply-templates select="ncbi:Assembly_component"/></td>
 </tr>
</xsl:for-each>
</table>
</xsl:template>

<xsl:template match="ncbi:Assembly_component">
<table>
 <tr>
  <td><xsl:value-of select="ncbi:Component_componentType"/></td>
  <td><xsl:value-of select="ncbi:Component_ctgId"/></td>
  <td><xsl:value-of select="ncbi:Component_accession"/></td>
  <td><xsl:value-of select="ncbi:Component_name"/></td>
  <td><xsl:value-of select="ncbi:Component_chromosome"/></td>
  <td><xsl:value-of select="ncbi:Component_start"/></td>
  <td><xsl:value-of select="ncbi:Component_end"/></td>
  <td><xsl:value-of select="ncbi:Component_orientation/@value"/></td>
  <td><xsl:value-of select="ncbi:Component_gi"/></td>
  <td><xsl:value-of select="ncbi:Component_groupTerm"/></td>
  <td><xsl:value-of select="ncbi:Component_contigLabel"/></td>
  <td><xsl:apply-templates select="ncbi:Component_MapLoc"/></td>
 </tr>
</table>
</xsl:template>

<xsl:template match="ncbi:Component_MapLoc">
<table>
<xsl:for-each select="ncbi:MapLoc">
 <tr>
  <td><xsl:value-of select="ncbi:MapLoc_asnFrom"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_asnTo"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_locType/@value"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_alnQuality"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_orient/@value"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_physMapInt"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_leftFlankNeighborPos"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_rightFlankNeighborPos"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_leftContigNeighborPos"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_rightContigNeighborPos"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_numberOfMismatches"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_numberOfDeletions"/></td>
  <td><xsl:value-of select="ncbi:MapLoc_numberOfInsertions"/></td>
  <td><xsl:apply-templates select="ncbi:MapLoc_fxnSet"/></td>
 </tr>
</xsl:for-each>
</table>
</xsl:template>

<xsl:template match="ncbi:MapLoc_fxnSet">
<table>
<xsl:for-each select="ncbi:FxnSet">
 <tr>
  <td><xsl:value-of select="ncbi:FxnSet_geneId"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_symbol"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_mrnaAcc"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_mrnaVer"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_protAcc"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_protVer"/></td>
  <td><xsl:value-of select="ncbi:FxnSet_fxnClass/@value"/></td>
 </tr>
</xsl:for-each>
</table>
</xsl:template>


</xsl:stylesheet>

