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
package org.eclipse.osee.framework.server.admin.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbResource implements IResource {

   //   private String name = "unknown";
   private final InputStream inputStream;
   private boolean isCompressed = false;
   private URI uri;
   private final String hrid;
   private final String artName;
   private final String fileTypeExtension;

   /**
    * @param binaryStream
    */
   public DbResource(InputStream inputStream, String artName, String hrid, String fileTypeExtension) {
      this.hrid = hrid;
      this.artName = artName;
      this.fileTypeExtension = fileTypeExtension;
      this.inputStream = inputStream;
      try {
         uri = new URI("db://");
      } catch (URISyntaxException ex1) {
      }
      if (inputStream.markSupported()) {
         inputStream.mark(1000);
         ZipInputStream in = new ZipInputStream(inputStream);
         // find out if it was compressed
         try {
            ZipEntry entry = in.getNextEntry();
            if (entry == null) {
               isCompressed = false;
            } else {
               //               name = entry.getName();
               isCompressed = true;
            }
         } catch (IOException ex) {
            isCompressed = false;
         }
         try {
            inputStream.reset();
         } catch (IOException ex) {
         }
      }
   }

   @Override
   public InputStream getContent() throws OseeCoreException {
      return inputStream;
   }

   @Override
   public URI getLocation() {
      return uri;
   }

   @Override
   public String getName() {
      return generateFileName(artName, hrid, fileTypeExtension);
   }

   @Override
   public boolean isCompressed() {
      return isCompressed;
   }

   private String generateFileName(String artName, String hrid, String fileTypeExtension) {
      StringBuilder builder = new StringBuilder();
      try {
         builder.append(URLEncoder.encode(artName, "UTF-8"));
         builder.append(".");
      } catch (Exception ex) {
         // Do Nothing - this is not important
      }
      builder.append(hrid);

      if (fileTypeExtension != null && fileTypeExtension.length() > 0) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }
}
