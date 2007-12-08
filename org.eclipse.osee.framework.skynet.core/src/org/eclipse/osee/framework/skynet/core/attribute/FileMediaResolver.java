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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Jeff C. Phillips
 */
public class FileMediaResolver implements IMediaResolver {
   private Logger logger = Logger.getLogger(FileMediaResolver.class.getCanonicalName());
   private String varchar;

   public byte[] getValue() {
      try {
         return Lib.inputStreamToBytes(new FileInputStream(getvarchar()));
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
         throw new RuntimeException(ex);
      }
   }

   public boolean setValue(InputStream stream) {
      try {
         OutputStream outputStream = new FileOutputStream(getvarchar());
         byte[] data = new byte[stream.available()];
         stream.read(data);
         outputStream.write(data);
         outputStream.close();

      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
         throw new RuntimeException(ex);
      }

      // Assume the file was always modified
      return true;
   }

   public void setBlobData(InputStream stream) {
      try {
         Lib.inputStreamToFile(stream, new File(varchar));
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
         throw new RuntimeException(ex);
      }
   }

   public byte[] getBlobData() {
      return getValue();
   }

   public void setVarchar(String varchar) {
      this.varchar = varchar;
   }

   public String getvarchar() {
      return varchar;
   }

   public void setFileLocation(String location) {
      setVarchar(location);
   }
}