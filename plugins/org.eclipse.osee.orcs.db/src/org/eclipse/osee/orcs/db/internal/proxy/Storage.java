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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class Storage extends DataResource {

   private final DataHandler handler;
   private byte[] rawContent;
   private volatile boolean initialized;

   public Storage(DataHandler handler) {
      super();
      this.handler = handler;
      clear();
      setInitialized(false);
   }

   @Override
   public void setLocator(String locator) {
      super.setLocator(locator);
      setInitialized(false);
   }

   public boolean isDataValid() {
      return this.rawContent != null && this.rawContent.length > 0;
   }

   public InputStream getInputStream() throws OseeCoreException {
      return new ByteArrayInputStream(getContent());
   }

   public boolean isInitialized() {
      return initialized;
   }

   private void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public boolean isLoadingAllowed() {
      return !isInitialized() && isLocatorValid();
   }

   public byte[] getContent() throws OseeCoreException {
      if (isLoadingAllowed()) {
         rawContent = handler.acquire(this);
         setInitialized(true);
      }
      return this.rawContent;
   }

   public void persist(long storageId) throws OseeCoreException {
      if (isDataValid()) {
         handler.save(storageId, this, rawContent);
      }
   }

   public void purge() throws OseeCoreException {
      if (isLocatorValid()) {
         handler.delete(this);
      }
   }

   public void setContent(byte[] rawContent, String extension, String contentType, String encoding) {
      this.rawContent = rawContent;
      setContentType(contentType);
      setEncoding(encoding);
      setExtension(extension);
   }

   public void copyTo(Storage other) {
      if (this.rawContent != null) {
         other.rawContent = Arrays.copyOf(this.rawContent, this.rawContent.length);
      } else {
         other.rawContent = null;
      }
      other.setContentType(this.getContentType());
      other.setEncoding(this.getEncoding());
      other.setExtension(this.getExtension());
   }

   public void clear() {
      setContent(null, "txt", "txt/plain", "UTF-8");
      setLocator("");
   }
}
