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
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimeMultipart;

/**
 * @author Roberto E. Escobar
 */
public class MultiPartDataSource implements MultipartDataSource {
   private final String name;
   private final MimeMultipart content;

   public MultiPartDataSource(String name, MimeMultipart content) {
      this.name = name;
      this.content = content;
   }

   @Override
   public String getContentType() {
      return content.getContentType();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try {
         content.writeTo(os);
      } catch (MessagingException ex) {
         throw new IOException(ex);
      }
      return new ByteArrayInputStream(os.toByteArray());
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public OutputStream getOutputStream() {
      throw new UnsupportedOperationException("OutputStream is not available for this source");
   }

   @Override
   public int getCount() {
      try {
         return content.getCount();
      } catch (MessagingException ex) {
         return 0;
      }
   }

   @Override
   public BodyPart getBodyPart(int index) throws MessagingException {
      return content.getBodyPart(index);
   }
};