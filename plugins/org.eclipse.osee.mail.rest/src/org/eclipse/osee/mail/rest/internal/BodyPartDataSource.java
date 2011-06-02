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
package org.eclipse.osee.mail.rest.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.BodyPartEntity;

/**
 * @author Roberto E. Escobar
 */
public class BodyPartDataSource implements DataSource {

   private final BodyPart part;

   public BodyPartDataSource(BodyPart part) {
      this.part = part;
   }

   @Override
   public String getContentType() {
      return part.getMediaType().getType();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      Object entity = part.getEntity();
      if (entity instanceof BodyPartEntity) {
         BodyPartEntity partEntity = (BodyPartEntity) entity;
         return partEntity.getInputStream();
      }
      throw new IOException("Entity was not a BodyPartEntity");
   }

   @Override
   public String getName() {
      return "none";
   }

   @Override
   public OutputStream getOutputStream() {
      return null;
   }
}