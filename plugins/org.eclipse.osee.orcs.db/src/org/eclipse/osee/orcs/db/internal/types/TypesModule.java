/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.types;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
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

   public TypesModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
   }

   public OrcsTypesDataStore createTypesDataStore() {
      return new OrcsTypesDataStore() {

         @Override
         public Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends ArtifactTypeToken> typesToPurge) {
            return new PurgeArtifactTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }

         @Override
         public Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends AttributeTypeId> typesToPurge) {
            return new PurgeAttributeTypeDatabaseTxCallable(logger, session, jdbcClient, joinFactory, typesToPurge);
         }

         @Override
         public Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends RelationTypeToken> typesToPurge) {
            return new PurgeRelationTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }
      };
   }
}