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