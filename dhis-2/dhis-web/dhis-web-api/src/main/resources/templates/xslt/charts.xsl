<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="list.xsl" />

  <xsl:param name="title">Charts</xsl:param>
  <xsl:param name="elements">charts</xsl:param>

  <xsl:template match="/">
    <xsl:apply-imports />
  </xsl:template>
</xsl:stylesheet>
