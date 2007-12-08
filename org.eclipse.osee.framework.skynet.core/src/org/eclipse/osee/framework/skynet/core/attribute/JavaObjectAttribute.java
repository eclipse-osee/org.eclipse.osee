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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.xml.sax.SAXException;

public class JavaObjectAttribute extends Attribute {
   public static final String TYPE_NAME = "Object";

   public JavaObjectAttribute(String name) {
      super(new BlobMediaResolver(), name);
   }

   @Override
   public String getTypeName() {
      return TYPE_NAME;
   }

   @Override
   public void setDat(InputStream data) {
      this.getResolver().setBlobData(data);
      this.dirty = true;
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }

   public Object getObject() throws IOException, ClassNotFoundException {
      byte[] bytes = this.getResolver().getBlobData();

      if (bytes.length == 0) {
         return null;
      }

      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bis);
      Object obj = ois.readObject();
      ois.close();
      return obj;
   }

   public void setObject(Object obj) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.flush();
      oos.close();
      setDat(new ByteArrayInputStream(bos.toByteArray()));
   }
}
