/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.internal.admin.FetchDatastoreMetadataCallable;
import org.eclipse.osee.orcs.core.internal.admin.MigrateDatastoreAdminCallable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class OrcsAdminImpl implements OrcsAdmin {
   private final OrcsApi orcsApi;
   private final Log logger;
   private final OrcsSession session;
   private final DataStoreAdmin dataStoreAdmin;
   private final EventAdmin eventAdmin;
   private final QueryBuilder fromCommon;

   public OrcsAdminImpl(OrcsApi orcsApi, Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.session = session;
      this.dataStoreAdmin = dataStoreAdmin;
      this.eventAdmin = eventAdmin;
      fromCommon = orcsApi.getQueryFactory().fromBranch(COMMON);
   }

   @Override
   public void createDatastore(String typeModel) {
      dataStoreAdmin.createDataStore();
      orcsApi.getOrcsTypes().loadTypes(typeModel);
   }

   @Override
   public void createSystemBranches(String typeModel) {
      new CreateSystemBranches(orcsApi, eventAdmin).create(typeModel);
      createSynonymsAndGrants();

   }

   private void createSynonymsAndGrants() {
      JdbcClient jdbcClient = dataStoreAdmin.getJdbcClient();
      for (String table : Arrays.asList("OSEE_ACCOUNT_SESSION", "OSEE_ACTIVITY", "OSEE_ACTIVITY_TYPE", "OSEE_ARTIFACT",
         "OSEE_ARTIFACT_ACL", "OSEE_ATTRIBUTE", "OSEE_BRANCH", "OSEE_BRANCH_ACL", "OSEE_CONFLICT",
         "OSEE_IMPORT_INDEX_MAP", "OSEE_IMPORT_MAP", "OSEE_IMPORT_SAVE_POINT", "OSEE_IMPORT_SOURCE", "OSEE_INFO",
         "OSEE_JOIN_ARTIFACT", "OSEE_JOIN_CHAR_ID", "OSEE_JOIN_CLEANUP", "OSEE_JOIN_EXPORT_IMPORT", "OSEE_JOIN_ID",
         "OSEE_JOIN_ID4", "OSEE_JOIN_TRANSACTION", "OSEE_KEY_VALUE", "OSEE_MERGE", "OSEE_OAUTH_AUTHORIZATION",
         "OSEE_OAUTH_CLIENT_CREDENTIAL", "OSEE_OAUTH_TOKEN", "OSEE_PERMISSION", "OSEE_RELATION_LINK",
         "OSEE_SCHEMA_VERSION", "OSEE_SEARCH_TAGS", "OSEE_SEQUENCE", "OSEE_SERVER_LOOKUP", "OSEE_SESSION",
         "OSEE_TAG_GAMMA_QUEUE", "OSEE_TTE_CLASSROOMS", "OSEE_TTE_COURSE", "OSEE_TTE_SESSION", "OSEE_TTE_USER",
         "OSEE_TTE_USERSESSIONS", "OSEE_TUPLE2", "OSEE_TUPLE3", "OSEE_TUPLE4", "OSEE_TXS", "OSEE_TXS_ARCHIVED",
         "OSEE_TX_DETAILS")) {
         try {
            jdbcClient.runCall("create public synonym " + table + " for " + table);
         } catch (Exception ex) {
            System.err.println("Error creating synonym for table " + table);
         }
         try {
            jdbcClient.runCall("grant insert, update, delete, select on " + table + " to osee_client_role");
         } catch (Exception ex) {
            System.err.println("Error granting permissions for table " + table);
         }
      }
   }

   @Override
   public void createDemoBranches() {
      new CreateDemoBranches(orcsApi).populate();
   }

   @Override
   public Callable<OrcsMetaData> migrateDatastore() {
      return new MigrateDatastoreAdminCallable(logger, session, dataStoreAdmin);
   }

   @Override
   public Callable<OrcsMetaData> createFetchOrcsMetaData() {
      return new FetchDatastoreMetadataCallable(logger, session, dataStoreAdmin);
   }

   @Override
   public boolean isDataStoreInitialized() {
      return dataStoreAdmin.isDataStoreInitialized();
   }

   @Override
   public void requireRole(UserId user, ArtifactId role) {
      if (!fromCommon.andId(role).andRelatedTo(CoreRelationTypes.Users_User, role).exists()) {
         throw new OseeAccessDeniedException("User [%s] not in user group [%s]", user, role);
      }
   }

   @Override
   public void createUsers(TransactionBuilder tx, Iterable<UserToken> users, QueryBuilder query) {
      if (tx.getAuthor().notEqual(SystemUser.OseeSystem)) {
         requireRole(tx.getAuthor(), CoreArtifactTokens.OseeAdmin);
      }
      List<? extends ArtifactId> defaultGroups =
         query.and(CoreAttributeTypes.DefaultGroup, "true").getResultsIds().getList();

      for (UserToken userToken : users) {
         ArtifactId user = tx.createArtifact(userToken);
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Active, userToken.isActive());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.UserId, userToken.getUserId());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Email, userToken.getEmail());

         if (userToken.isAdmin()) {
            tx.relate(CoreArtifactTokens.OseeAdmin, CoreRelationTypes.Users_User, user);
            tx.relate(CoreArtifactTokens.OseeAccessAdmin, CoreRelationTypes.Users_User, user);
         }

         for (ArtifactId userGroup : defaultGroups) {
            tx.relate(userGroup, CoreRelationTypes.Users_User, user);
         }
      }
   }
}