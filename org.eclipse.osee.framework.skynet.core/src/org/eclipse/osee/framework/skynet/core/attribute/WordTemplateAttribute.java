/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

/**
 * @author Jeff C. Phillips
 */
public class WordTemplateAttribute extends WordAttribute {

   /**
    * @param attributeType
    * @param value
    */
   public WordTemplateAttribute(DynamicAttributeDescriptor attributeType, String value) {
      super(attributeType, value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.WordAttribute#swagValue(java.lang.String)
    */
   @Override
   protected void swagValue(String value) {
      StringBuilder strB = new StringBuilder(300);
      strB.append("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\">\n\t<w:r>\n\t\t<w:t>");
      if (value != null) {
         value = value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
         strB.append(value);
      }
      strB.append("</w:t>\n\t\t</w:r>\n\t</w:p>");
      setValue(strB.toString());
   }

}
