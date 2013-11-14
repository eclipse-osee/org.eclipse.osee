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
package org.eclipse.osee.ats.rest.internal.util;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.rest.internal.AtsServerImpl;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactConfigCache implements IAtsConfig {

   private final OrcsApi orcsApi;
   private final AtsConfigCache cache;

   public AtsArtifactConfigCache(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      cache = new AtsConfigCache();
   }

   @Override
   public <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) throws OseeCoreException {
      return cache.getByTag(tag, clazz);
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) throws OseeCoreException {
      return cache.getSoleByTag(tag, clazz);
   }

   @Override
   public <A extends IAtsConfigObject> List<A> get(Class<A> clazz) throws OseeCoreException {
      return cache.get(clazz);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <A extends IAtsConfigObject> A getSoleByGuid(String guid, Class<A> clazz) throws OseeCoreException {
      A result = cache.getSoleByGuid(guid, clazz);
      if (result == null) {
         ArtifactReadable artifact =
            orcsApi.getQueryFactory(null).fromBranch(AtsUtilRest.getAtsBranch()).andGuid(guid).getResults().getOneOrNull();
         if (artifact != null) {
            result = (A) AtsServerImpl.get().getWorkItemFactory().getConfigObject(artifact);
            if (result != null) {
               cache.cache(result);
            }
         }
      }
      return result;
   }

   @Override
   public IAtsConfigObject getSoleByGuid(String guid) throws OseeCoreException {
      IAtsConfigObject result = cache.getSoleByGuid(guid);
      if (result == null) {
         ArtifactReadable artifact =
            orcsApi.getQueryFactory(null).fromBranch(AtsUtilRest.getAtsBranch()).andGuid(guid).getResults().getOneOrNull();
         if (artifact != null) {
            result = AtsServerImpl.get().getWorkItemFactory().getConfigObject(artifact);
            if (result != null) {
               cache.cache(result);
            }
         }
      }
      return result;
   }

   @Override
   public void getReport(XResultData rd) throws OseeCoreException {
      throw new OseeStateException("Not Implemented");
   }

   @Override
   public void invalidate(IAtsConfigObject configObject) throws OseeCoreException {
      cache.invalidate(configObject);
   }

}
