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

import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;

/**
 * @author Jeff C. Phillips
 */
public class BlobMediaResolver implements IMediaResolver {

   private byte[] data;

   //   private Logger logger = Logger.getLogger(BlobMediaResolver.class.getCanonicalName());

   public BlobMediaResolver() {
      super();
      this.data = new byte[0];
   }

   public byte[] getValue() {
      return getBlobData();
   }

   public boolean setValue(InputStream stream) {
      byte[] newData = Streams.getByteArray(stream);

      if (Arrays.equals(data, newData)) return false;

      this.data = newData;
      return true;
   }

   public void setBlobData(InputStream stream) {
      this.data = Streams.getByteArray(stream);
   }

   public byte[] getBlobData() {
      return data;
   }

   public void setVarchar(String varchar) {
   }

   public String getvarchar() {
      return null;
   }
}
