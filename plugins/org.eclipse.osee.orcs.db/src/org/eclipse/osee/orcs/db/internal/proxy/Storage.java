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
package org.eclipse.osee.orcs.db.internal.proxy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class Storage {

   private final DataResource resource;
   private final DataHandler handler;

   private byte[] rawContent;
   private boolean needToReadFromRemote;

   public Storage(DataResource resource, DataHandler handler) {
      super();
      this.resource = resource;
      this.handler = handler;
      clear();
      this.needToReadFromRemote = true;
   }

   public String getLocator() {
      return resource.getLocator();
   }

   public void setLocator(String locator) {
      resource.setLocator(locator);
      needToReadFromRemote = true;
   }

   public boolean isLocatorValid() {
      return resource.isLocatorValid();
   }

   public String getContentType() {
      return resource.getContentType();
   }

   public String getEncoding() {
      return resource.getEncoding();
   }

   public String getExtension() {
      return resource.getExtension();
   }

   public String getFileName() {
      return resource.getName();
   }

   //////// START OF DATA ////////////
   public boolean isDataValid() {
      return this.rawContent != null && this.rawContent.length > 0;
   }

   public InputStream getInputStream() throws OseeCoreException {
      return new ByteArrayInputStream(getContent());
   }

   public byte[] getContent() throws OseeCoreException {
      if (isLocatorValid() != false && needToReadFromRemote) {
         rawContent = handler.acquire(resource);
         needToReadFromRemote = false;
      }
      return this.rawContent;
   }

   public void persist(int storageId) throws OseeCoreException {
      if (this.rawContent != null && this.rawContent.length > 0) {
         handler.save(storageId, resource, rawContent);
      }
   }

   public void purge() throws OseeCoreException {
      if (isLocatorValid()) {
         handler.purge(resource);
      }
   }

   public void setContent(byte[] rawContent, String extension, String contentType, String encoding) {
      this.rawContent = rawContent;
      this.resource.setContentType(contentType);
      this.resource.setEncoding(encoding);
   }

   public void copyTo(Storage other) {
      other.rawContent = Arrays.copyOf(this.rawContent, this.rawContent.length);

      other.resource.setContentType(this.resource.getContentType());
      other.resource.setEncoding(this.resource.getEncoding());
      other.resource.setExtension(this.resource.getExtension());
   }

   public void clear() {
      setContent(null, "txt", "txt/plain", "UTF-8");
      setLocator(null);
   }
}
