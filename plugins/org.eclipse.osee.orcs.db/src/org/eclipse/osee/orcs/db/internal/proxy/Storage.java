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

package org.eclipse.osee.orcs.db.internal.proxy;

import java.util.Arrays;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Roberto E. Escobar
 */
public class Storage extends DataResource {

   private final IResourceManager resourceManager;
   private final AbstractDataProxy dataProxy;
   private byte[] rawContent;
   private volatile boolean initialized;

   public Storage(IResourceManager resourceManager, AbstractDataProxy dataProxy) {
      this.resourceManager = resourceManager;
      this.dataProxy = dataProxy;
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

   public boolean isInitialized() {
      return initialized;
   }

   private void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public boolean isLoadingAllowed() {
      return !isInitialized() && isLocatorValid();
   }

   public byte[] getContent() {
      if (isLoadingAllowed()) {
         rawContent = resourceManager.acquire(this);
         setInitialized(true);
      }
      return this.rawContent;
   }

   public void persist(long storageId) {
      if (isDataValid()) {
         resourceManager.save(storageId, dataProxy.getResolver().getStorageName(), this, rawContent);
      }
   }

   public void purge() {
      if (isLocatorValid()) {
         resourceManager.purge(this);
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
