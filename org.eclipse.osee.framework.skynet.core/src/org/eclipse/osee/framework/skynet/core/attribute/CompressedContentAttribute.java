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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.xml.sax.SAXException;

public class CompressedContentAttribute extends Attribute {

   public CompressedContentAttribute(String name) {
      super(new BlobMediaResolver(), name);
   }

   @Override
   public String getTypeName() {
      return "CompressedContent";
   }

   public void setInputStream(InputStream in) {
      try {
         this.getResolver().setBlobData(new ByteArrayInputStream(Lib.compressFile(in)));
         this.dirty = true;
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   @Override
   public void setDat(InputStream data) {
      setInputStream(data);
   }

   public InputStream getUncompressedStream() throws IOException {
      byte[] bytes = this.getResolver().getBlobData();
      return new ByteArrayInputStream(Lib.decompressBytes(bytes));
   }

   public byte[] getValue() {
      try {
         byte[] byteArray = this.getResolver().getBlobData();
         return Lib.decompressBytes(byteArray);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return new byte[0];
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }
}
