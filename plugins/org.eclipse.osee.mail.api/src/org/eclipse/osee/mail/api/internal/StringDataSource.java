/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.mail.api.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Roberto E. Escobar
 */
public class StringDataSource implements javax.activation.DataSource {

   private final String name;
   private String data;
   private String charset;
   private String contentType;
   private ByteArrayOutputStream outputStream;

   public StringDataSource(String name, String data) {
      super();
      this.name = name;
      this.data = data;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setCharset(String charset) {
      this.charset = charset;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType.toLowerCase();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      if (data == null && outputStream == null) {
         throw new IOException("No data");
      }
      if (outputStream != null) {
         String encodedOut = outputStream.toString(charset);
         if (data == null) {
            data = encodedOut;
         } else {
            data = data.concat(encodedOut);
         }
         outputStream = null;
      }
      return new ByteArrayInputStream(data.getBytes(charset));
   }

   @Override
   public OutputStream getOutputStream() {
      if (outputStream == null) {
         outputStream = new ByteArrayOutputStream();
      }
      return outputStream;
   }

   @Override
   public String getContentType() {
      String toReturn;
      if (contentType != null && contentType.indexOf("charset") > 0 && contentType.startsWith("text/")) {
         toReturn = contentType;
      } else {
         toReturn = String.format("%s; charset=%s", contentType != null ? contentType : "text/plain", charset);
      }
      return toReturn;
   }
}