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

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDataProxy implements DataProxy {
   private Storage storage;
   private Log logger;
   private long gammaId;
   private boolean isNewGammaId;
   private ResourceNameResolver resolver;

   @Override
   public Object getRawValue() {
      return "";
   }

   @Override
   public String getUri() {
      return getStorage().getLocator();
   }

   @Override
   public void setGamma(long gammaId, boolean isNewGammaId) {
      this.gammaId = gammaId;
      this.isNewGammaId = isNewGammaId;
   }

   @Override
   public long getGammaId() {
      return gammaId;
   }

   @Override
   public void persist() {
      if (isNewGammaId) {
         storage.persist(gammaId);
      }
   }

   @Override
   public void rollBack() {
      if (isNewGammaId) {
         purge();
      }
   }

   protected Storage getStorage() {
      return storage;
   }

   protected void setStorage(Storage storage) {
      this.storage = storage;
   }

   protected Log getLogger() {
      return logger;
   }

   protected void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public void setResolver(ResourceNameResolver resolver) {
      this.resolver = resolver;
   }

   protected ResourceNameResolver getResolver() {
      return resolver;
   }

   @Override
   public boolean isInMemory() {
      return storage.isInitialized() && storage.isDataValid();
   }

   @Override
   public String toString() {
      return String.format("%s [value:[%s]]", getClass().getSimpleName(), getDisplayableString());
   }

   @Override
   public void purge() {
      storage.purge();
   }
}