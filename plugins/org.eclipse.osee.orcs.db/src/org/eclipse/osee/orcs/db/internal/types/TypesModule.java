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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.db.internal.callable.OrcsTypeLoader;
import org.eclipse.osee.orcs.db.internal.callable.PurgeArtifactTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributeTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeRelationTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class TypesModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IResourceManager resourceManager;

   public TypesModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.resourceManager = resourceManager;
   }

   public OrcsTypesDataStore createTypesDataStore() {
      return new OrcsTypesDataStore() {

         @Override
         public IResource getOrcsTypesLoader(OrcsSession session) {
            return new OrcsTypeLoader(jdbcClient, resourceManager).load();
         }

         @Override
         public Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends IArtifactType> typesToPurge) {
            return new PurgeArtifactTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }

         @Override
         public Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends AttributeTypeId> typesToPurge) {
            return new PurgeAttributeTypeDatabaseTxCallable(logger, session, jdbcClient, joinFactory, typesToPurge);
         }

         @Override
         public Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends IRelationType> typesToPurge) {
            return new PurgeRelationTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }

      };
   }

}
