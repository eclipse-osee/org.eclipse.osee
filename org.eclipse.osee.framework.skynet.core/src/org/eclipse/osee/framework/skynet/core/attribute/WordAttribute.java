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

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   public static final String WORD_TEMPLATE_CONTENT = "Word Template Content";
   public static final String WHOLE_WORD_CONTENT = "Whole Word Content";
   public static final String OLE_DATA_NAME = "Word Ole Data";

   /**
    * wraps the value in a simple word paragraph
    * 
    * @param attributeType
    * @param value
    */
   public WordAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      value = WordUtil.removeWordMarkupSmartTags(value);
      return super.subClassSetValue(value);
   }

   public boolean mergeMarkupPresent() throws OseeCoreException {
      String temp = getValue();
      return (temp.contains("<w:delText>") || temp.contains("w:type=\"Word.Insertion\"") || temp.contains("w:type=\"Word.Formatting\""));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() throws OseeCoreException {
      return WordUtil.textOnly(getValue());
   }
}