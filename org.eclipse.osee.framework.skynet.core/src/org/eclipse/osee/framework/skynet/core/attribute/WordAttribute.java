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

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public abstract class WordAttribute extends StringAttribute implements IStreamSetableAttribute {
   public static final String CONTENT_NAME = "Word Formatted Content";
   public static final String OLE_DATA_NAME = "Word Ole Data";

   /**
    * wraps the value in a simple word paragraph
    * 
    * @param attributeType
    * @param value
    */
   public WordAttribute(AttributeType attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType, dataProvider);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return super.getDisplayableString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(String value) {
      value = WordUtil.removeWordMarkupSmartTags(value);
      super.setValue(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IStreamableAttribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      setValue(Lib.inputStreamToString(value));
   }
}