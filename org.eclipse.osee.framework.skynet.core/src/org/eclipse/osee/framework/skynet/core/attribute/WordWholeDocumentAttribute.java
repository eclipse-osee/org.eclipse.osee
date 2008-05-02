/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

/**
 * @author Jeff C. Phillips
 */
public class WordWholeDocumentAttribute extends WordAttribute {

   /**
    * @param attributeType
    * @param value
    */
   public WordWholeDocumentAttribute(DynamicAttributeDescriptor attributeType, String value) {
      super(attributeType, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.WordAttribute#swagValue(java.lang.String)
    */
   @Override
   protected void swagValue(String value) {
      if (value == null || value.matches("")) {
         String wordLeader1 =
               "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<?mso-application progid='Word.Document'?>";
         String wordLeader2 =
               "<w:wordDocument xmlns:w='http://schemas.microsoft.com/office/word/2003/wordml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:sl='http://schemas.microsoft.com/schemaLibrary/2003/core' xmlns:aml='http://schemas.microsoft.com/aml/2001/core' xmlns:wx='http://schemas.microsoft.com/office/word/2003/auxHint' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882' xmlns:wsp='http://schemas.microsoft.com/office/word/2003/wordml/sp2' xmlns:ns0='http://www.w3.org/2001/XMLSchema' xmlns:ns1='http://eclipse.org/artifact.xsd' xmlns:st1='urn:schemas-microsoft-com:office:smarttags' w:macrosPresent='no' w:embeddedObjPresent='no' w:ocxPresent='no' xml:space='preserve'>";
         String wordBody = "<w:body></w:body>";
         String wordTrailer = "</w:wordDocument> ";
         value = wordLeader1 + wordLeader2 + wordBody + wordTrailer;
      }
      setValue(value);
   }
}
