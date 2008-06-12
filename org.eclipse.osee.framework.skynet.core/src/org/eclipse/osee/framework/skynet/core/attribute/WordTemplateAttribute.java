/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class WordTemplateAttribute extends WordAttribute {

   public WordTemplateAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   //   /* (non-Javadoc)
   //    * @see org.eclipse.osee.framework.skynet.core.attribute.StringAttribute#initializeDefaultValue()
   //    */
   //   @Override
   //   public void initializeToDefaultValue() {
   //      String value = getAttributeType().getDefaultValue();
   //      StringBuilder strB = new StringBuilder(300);
   //      strB.append("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\">\n\t<w:r>\n\t\t<w:t>");
   //      if (value != null) {
   //         value = value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
   //         strB.append(value);
   //      }
   //      strB.append("</w:t>\n\t\t</w:r>\n\t</w:p>");
   //      subClassSetValue(strB.toString());
   //   }
}