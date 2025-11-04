<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:g="http://gems.lab2"
                version="1.0">
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>Diamond Fund Gems</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    table { border-collapse: collapse; width: 80%; }
                    th, td { border: 1px solid #aaa; padding: 6px 10px; text-align: left; }
                    th { background: #eee; }
                </style>
            </head>
            <body>
                <h2>Diamond Fund Collection</h2>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Origin</th>
                        <th>Color</th>
                        <th>Transparency</th>
                        <th>Cutting</th>
                        <th>Value (ct)</th>
                    </tr>
                    <xsl:for-each select="g:Gem/g:Stone">
                        <tr>
                            <td><xsl:value-of select="@id"/></td>
                            <td><xsl:value-of select="g:name"/></td>
                            <td><xsl:value-of select="g:preciousness"/></td>
                            <td><xsl:value-of select="g:origin"/></td>
                            <td><xsl:value-of select="g:visualParameters/g:color"/></td>
                            <td><xsl:value-of select="g:visualParameters/g:transparency"/></td>
                            <td><xsl:value-of select="g:visualParameters/g:cutting"/></td>
                            <td><xsl:value-of select="g:value"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
