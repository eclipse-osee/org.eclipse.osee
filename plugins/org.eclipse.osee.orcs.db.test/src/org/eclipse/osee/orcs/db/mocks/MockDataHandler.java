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
package org.eclipse.osee.orcs.db.mocks;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.db.internal.proxy.DataHandler;
import org.eclipse.osee.orcs.db.internal.proxy.DataResource;

/**
 * @author Roberto E. Escobar
 */
public class MockDataHandler implements DataHandler {

   private DataResource resource;
   private long storageId;
   private byte[] content;
   private boolean acquire;
   private boolean save;
   private boolean delete;

   public MockDataHandler() {
      super();
      reset();
   }

   @Override
   public byte[] acquire(DataResource resource) throws OseeCoreException {
      setAcquire(true);
      this.resource = resource;
      return content;
   }

   @Override
   public void save(long storageId, DataResource resource, byte[] rawContent) throws OseeCoreException {
      setSave(true);
      this.storageId = storageId;
      this.resource = resource;
      this.content = rawContent;
   }

   @Override
   public void delete(DataResource resource) throws OseeCoreException {
      setDelete(true);
      this.resource = resource;
   }

   public DataResource getResource() {
      return resource;
   }

   public long getStorageId() {
      return storageId;
   }

   public byte[] getContent() {
      return content;
   }

   public void setContent(byte[] content) {
      this.content = content;
   }

   public void setResource(DataResource resource) {
      this.resource = resource;
   }

   public void setStorageId(int storageId) {
      this.storageId = storageId;
   }

   public boolean isAcquire() {
      return acquire;
   }

   public boolean isSave() {
      return save;
   }

   public boolean isDelete() {
      return delete;
   }

   public void setAcquire(boolean acquire) {
      this.acquire = acquire;
   }

   public void setSave(boolean save) {
      this.save = save;
   }

   public void setDelete(boolean delete) {
      this.delete = delete;
   }

   public void reset() {
      setAcquire(false);
      setSave(false);
      setDelete(false);
      resource = null;
      content = null;
      storageId = -1;
   }
}
