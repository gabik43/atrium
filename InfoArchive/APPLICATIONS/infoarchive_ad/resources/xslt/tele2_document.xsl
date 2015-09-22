<?xml version="1.0" encoding="UTF-16"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- XHTML output with XML syntax -->
  <xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
  <xsl:output method="html"/>
  <xsl:template match="result">
    <html>
      <head>
        <title>Document Metadata</title>
      </head>
      <body>
        <!--a><xsl:attribute name="href">BlobServlet?uri=dds://DOMAIN=data;DATASET=infoarchive_ad/DCTM/FILES/<xsl:value-of select="object/r_object_id"/>.pdf</xsl:attribute>
Download Content:</a-->
        <object type="application/pdf" width="100%" height="262px">
          <xsl:attribute name="data">BlobServlet?uri=dds://DOMAIN=data;DATASET=infoarchive_ad/TELE2_DEMO/FILES/<xsl:value-of select="concat(FILE_NAME_TRANSLIT,'.pdf')"/>
          </xsl:attribute>
        </object>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>