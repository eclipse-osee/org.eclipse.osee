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
package org.eclipse.osee.orcs.db.internal.types;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.OrcsTypeLoaderCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeArtifactTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributeTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeRelationTypeDatabaseTxCallable;

/**
 * @author Roberto E. Escobar
 */
public class TypesModule {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityManager identityService;
   private final IResourceManager resourceManager;

   public TypesModule(Log logger, IOseeDatabaseService dbService, IdentityManager identityService, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.resourceManager = resourceManager;
   }

   public OrcsTypesDataStore createTypesDataStore() {
      return new OrcsTypesDataStore() {

         @Override
         public Callable<IResource> getOrcsTypesLoader(OrcsSession session) {
            return new OrcsTypeLoaderCallable(logger, session, dbService, resourceManager);
         }

         @Override
         public Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends IArtifactType> typesToPurge) {
            return new PurgeArtifactTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
         }

         @Override
         public Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends IAttributeType> typesToPurge) {
            return new PurgeAttributeTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
         }

         @Override
         public Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends IRelationType> typesToPurge) {
            return new PurgeRelationTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
         }

      };
   }

}
