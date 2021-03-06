<?xml version="1.0" encoding="UTF-16"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- XHTML output with XML syntax -->
  <xsl:output method="xml" encoding="utf-8" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
  <xsl:output method="html"/>
  <xsl:template match="ROOT">
    <html>
      <head>
        <title>Document Metadata</title>
      </head>
      <body onload="document.forms[0].submit();">
        <form type="hidden" target="myIframe" action="http://10.101.145.239:18080/atrium/getfile" method="post" height="100%" weight="100%">
          <!--input type="hidden" name="templateName" value="SetOfDocs.xindd"/-->
		  <xsl:element name="input">
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">templateName</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="Template_Name"/>
            </xsl:attribute>
          </xsl:element>
          <xsl:element name="input">
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">xml</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="hexCodeDataSource"/>
            </xsl:attribute>
          </xsl:element>
        </form>
        <iFrame src="" name="myIframe" style="height:100%;width:100%;" frameborder="0" marginheight="0" marginwidth="0" scrolling="no" class="cf"/>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>