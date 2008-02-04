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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.xml.sax.SAXException;

/**
 * @author Jeff C. Phillips
 */
public abstract class WordAttribute extends Attribute {

   public static final String CONTENT_NAME = "Word Formatted Content";
   public static final String OLE_DATA_NAME = "Word Ole Data";
   public static final String TYPE_NAME = "Content";

   public WordAttribute(IMediaResolver resolver) {
      super(resolver);

   }

   public WordAttribute(IMediaResolver resolver, String name) {
      super(resolver, name);
   }

   @Override
   public String getTypeName() {
      return TYPE_NAME;
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }

   public byte[] getWordData() {
      return getDat();
   }

   public void setWordData(InputStream stream) {
      setDat(stream);
   }

   @Override
   public void setDat(InputStream data) {
      // MSWord seems to modify non-essential information in the first Element of the XML
      // so we must first do a comparison of the rest of the data to avoid unnintended
      // saving of word formatted content when no real changes were made.

      byte[] newData = Streams.getByteArray(data);
      byte[] curData = getDat();

      boolean dataDiffers = false;

      try {
         String newString = new String(newData, "UTF-8");
         String curString = new String(curData, "UTF-8");

         dataDiffers = !newString.equals(curString);

      } catch (UnsupportedEncodingException ex) {
         // If the data can't be decoded, then rely on the Resolver to detect similar data
         dataDiffers = true;
      }

      if (dataDiffers) super.setDat(new ByteArrayInputStream(newData));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.attribute.Attribute#swagValue(java.lang.String)
    */
   @Override
   public void swagValue(String value) {
      StringBuilder strB = new StringBuilder(300);
      strB.append("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\">\n\t<w:r>\n\t\t<w:t>");
      if (value != null) {
         value = value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
         strB.append(value);
      }
      strB.append("</w:t>\n\t\t</w:r>\n\t</w:p>");
      setStringData(strB.toString());
   }

   @Override
   public String getStringData() {
      return WordUtil.reassignBinDataID(super.getStringData());
   }
}
