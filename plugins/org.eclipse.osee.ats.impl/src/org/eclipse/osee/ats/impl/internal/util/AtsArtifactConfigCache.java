/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactConfigCache implements IAtsConfig {

   private final IAtsConfigItemFactory configItemFactory;
   private final OrcsApi orcsApi;
   private final Cache<Long, IAtsConfigObject> uuidCache =
      CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

   public AtsArtifactConfigCache(IAtsConfigItemFactory configItemFactory, OrcsApi orcsApi) {
      this.configItemFactory = configItemFactory;
      this.orcsApi = orcsApi;
   }

   @Override
   public <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) throws OseeCoreException {
      throw new UnsupportedOperationException("AtsArtifactConfigCache.getByTag not supported on server");
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) throws OseeCoreException {
      throw new UnsupportedOperationException("AtsArtifactConfigCache.getSoleByTag not supported on server");
   }

   @Override
   public <A extends IAtsConfigObject> List<A> get(Class<A> clazz) throws OseeCoreException {
      throw new UnsupportedOperationException("AtsArtifactConfigCache.get not supported on server");
   }

   @Override
   public void getReport(XResultData rd) throws OseeCoreException {
      throw new OseeStateException("Not Implemented");
   }

   @Override
   public void invalidate(IAtsConfigObject configObject) throws OseeCoreException {
      uuidCache.invalidate(configObject.getUuid());
   }

   @Override
   public final <A extends IAtsConfigObject> List<A> getById(long id, Class<A> clazz) {
      throw new UnsupportedOperationException("AtsArtifactConfigCache.getById not supported on server");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <A extends IAtsConfigObject> A getSoleByUuid(final long uuid, Class<A> clazz) throws OseeCoreException {
      IAtsConfigObject atsConfigObject = null;
      try {
         atsConfigObject = uuidCache.get(uuid, new Callable<IAtsConfigObject>() {

            @Override
            public IAtsConfigObject call() throws Exception {
               ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
                  uuid).getResults().getOneOrNull();
               if (artifact == null) {
                  throw new OseeStateException("Artifact of %d of class %s is not found", uuid, clazz.getTypeName());
               }
               IAtsConfigObject atsConfigObject = configItemFactory.getConfigObject(artifact);
               if (atsConfigObject == null) {
                  throw new OseeStateException("Artifact of %d is not of class %s", uuid, clazz.getTypeName());
               }
               return atsConfigObject;
            }
         });
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return atsConfigObject != null ? (A) atsConfigObject : null;
   }

   @Override
   public IAtsConfigObject getSoleByUuid(long uuid) throws OseeCoreException {
      return getSoleByUuid(uuid, IAtsConfigObject.class);
   }

}
