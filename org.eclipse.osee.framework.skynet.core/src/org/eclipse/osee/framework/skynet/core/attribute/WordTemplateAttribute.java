/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Jeff C. Phillips
 */
public class WordTemplateAttribute extends WordAttribute {

   public WordTemplateAttribute(DynamicAttributeDescriptor attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType, dataProvider);
      setDefaultValue(attributeType.getDefaultValue());
   }

   protected void setDefaultValue(String value) {
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
