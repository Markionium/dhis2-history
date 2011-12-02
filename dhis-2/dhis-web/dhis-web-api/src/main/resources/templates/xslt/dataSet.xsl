<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:include href="html-wrapper.xsl" />
  <xsl:include href="identifiable-row.xsl" />

  <xsl:template match="dataSet">
    <div class="dataSet">
      <h2>
        <xsl:value-of select="@name" />
      </h2>
      <table border="1">
        <tr>
          <td>ID</td>
          <td>
            <xsl:value-of select="@id" />
          </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td>
            <xsl:value-of select="@lastUpdated" />
          </td>
        </tr>
        <tr>
          <td>Short Name</td>
          <td>
            <xsl:value-of select="shortName" />
          </td>
        </tr>
        <tr>
          <td>Version</td>
          <td>
            <xsl:value-of select="version" />
          </td>
        </tr>
        <tr>
          <td>Mobile</td>
          <td>
            <xsl:value-of select="mobile" />
          </td>
        </tr>

      </table>

      <xsl:apply-templates select="dataElements|indicators|sources" />

    </div>
  </xsl:template>

  <xsl:template match="dataElements">
    <xsl:if test="count(child::*) > 0">
      <h3>DataElements</h3>
      <table border="1" class="dataElements">
        <xsl:for-each select="dataElement">
          <xsl:call-template name="identifiable-row">
            <xsl:with-param name="root">../dataElements</xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="indicators">
    <xsl:if test="count(child::*) > 0">
      <h3>Indicators</h3>
      <table border="1" class="indicator">
        <xsl:for-each select="indicator">
          <xsl:call-template name="identifiable-row">
            <xsl:with-param name="root">../indicators</xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sources">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnits</h3>
      <table border="1" class="sources">
        <xsl:for-each select="source">
          <xsl:call-template name="identifiable-row">
            <xsl:with-param name="root">../sources</xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
