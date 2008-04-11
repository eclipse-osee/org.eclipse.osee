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
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   public static final String CONTENT_NAME = "Word Formatted Content";
   public static final String OLE_DATA_NAME = "Word Ole Data";

   /**
    * wraps the value in a simple word paragraph
    * 
    * @param attributeType
    * @param value
    */
   public WordAttribute(DynamicAttributeDescriptor attributeType, String value) {
      super(attributeType, value);
      swagValue(value);
   }

   private void swagValue(String value) {
      StringBuilder strB = new StringBuilder(300);
      strB.append("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\">\n\t<w:r>\n\t\t<w:t>");
      if (value != null) {
         value = value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
         strB.append(value);
      }
      strB.append("</w:t>\n\t\t</w:r>\n\t</w:p>");
      setValue(strB.toString());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public String getValue() {
      return getValue(getRawContentStream(), getRawContent());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   public static String getValue(ByteArrayInputStream rawContentStream, byte[] bytes) {
      try {
         if (rawContentStream != null) {
            byte[] local_bytes = Lib.decompressBytes(rawContentStream);
            if (local_bytes.length == 0) {
               //assume decompression failed because the content was not compress originally (do this to be backwards compatible for now)
               local_bytes = bytes;
            }
            return WordUtil.reassignBinDataID(new String(local_bytes, "UTF-8"));
         }
      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(String value) {
      try {
         value = WordUtil.removeWordMarkupSmartTags(value);
         if(false){
         setRawContent(Lib.compressFile(new ByteArrayInputStream(value.getBytes("UTF-8")), getAttributeType().getName()));
         } else {
        	 setRawContent(value.getBytes("UTF-8"));
         }
      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      if(false){
      setRawContent(Lib.compressFile(value, getAttributeType().getName()));
      }
   }
   
   public static String convertStreamToString(ByteArrayInputStream stream, byte[] rawContent){
	      try {
	          if (stream != null) {
	             byte[] bytes = Lib.decompressBytes(stream);
	             if (bytes.length == 0) {
	                //assume decompression failed because the content was not compress originally (do this to be backwards compatible for now)
	                bytes = rawContent;
	             }
	             return WordUtil.reassignBinDataID(new String(bytes, "UTF-8"));
	          }
	       } catch (IOException ex) {
	          SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
	       }
	       return null;
	   
   }
   

   @Override
   public void setRawContent(byte[] value) {
	   super.setRawContent(value);
   }
}