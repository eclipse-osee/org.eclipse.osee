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
package org.eclipse.osee.orcs.db.internal.accessor;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.services.IdentityService;

/**
 * @author John R. Misinco
 */
public class ArtifactTypeDataAccessor<T extends AbstractOseeType<Long>> implements IOseeDataAccessor<Long, T> {

   public static interface TypeLoader {
      void load() throws OseeCoreException;
   }

   private final TypeLoader loader;
   private final IdentityService identityService;

   public ArtifactTypeDataAccessor(IdentityService identityService, TypeLoader loader) {
      this.identityService = identityService;
      this.loader = loader;
   }

   @Override
   public synchronized void load(IOseeCache<Long, T> cache) throws OseeCoreException {
      loader.load();
   }

   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      Collection<Long> remoteIds = new ArrayList<Long>();
      for (T type : types) {
         remoteIds.add(type.getGuid());
      }
      identityService.store(remoteIds);
      for (T type : types) {
         type.setId(identityService.getLocalId(type.getGuid()));
         type.clearDirty();
      }
   }
}
