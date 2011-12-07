<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:indicatorType">
    <div class="indicator">
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
          <td>Factor</td>
          <td>
            <xsl:value-of select="d:factor" />
          </td>
        </tr>
        <tr>
          <td>Number</td>
          <td>
            <xsl:value-of select="d:number" />
          </td>
        </tr>
      </table>

    </div>
  </xsl:template>

</xsl:stylesheet>
