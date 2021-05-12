/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.OrcsTypeJoin;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.OseePreparedStatement;
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

/**
 * @author Roberto E. Escobar
 */
public class OrcsAdminImpl implements OrcsAdmin {
   private final OrcsApi orcsApi;
   private final Log logger;
   private final OrcsSession session;
   private final DataStoreAdmin dataStoreAdmin;
   private final QueryBuilder fromCommon;
   private final JdbcClient jdbcClient;

   public OrcsAdminImpl(OrcsApi orcsApi, Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.session = session;
      this.dataStoreAdmin = dataStoreAdmin;
      fromCommon = orcsApi.getQueryFactory().fromBranch(COMMON);
      jdbcClient = dataStoreAdmin.getJdbcClient();
   }

   @Override
   public TransactionId createDatastoreAndSystemBranches() {
      ActivityLog activityLog = orcsApi.getActivityLog();
      try {
         activityLog.setEnabled(false);

         dataStoreAdmin.createDataStore();
         return new CreateSystemBranches(orcsApi).create();
      } finally {
         activityLog.setEnabled(true);
      }
   }

   @Override
   public void registerMissingOrcsTypeJoins() {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, SystemUser.OseeSystem,
         "Add missing orcs type joins.");

      addMissingJoins(tx, CoreTupleTypes.ArtifactTypeJoin, orcsApi.tokenService().getArtifactTypeJoins());
      addMissingJoins(tx, CoreTupleTypes.AttributeTypeJoin, orcsApi.tokenService().getAttributeTypeJoins());
      addMissingJoins(tx, CoreTupleTypes.RelationTypeJoin, orcsApi.tokenService().getRelationTypeJoins());

      tx.commit();
   }

   private <J extends OrcsTypeJoin<J, T>, T extends NamedId> void addMissingJoins(TransactionBuilder tx, Tuple2Type<J, T> tupleType, Collection<J> registeredJoins) {
      List<J> joinsInDb = new ArrayList<>();
      orcsApi.getQueryFactory().tupleQuery().getTuple2UniqueE1(tupleType, COMMON, joinsInDb::add);

      for (J join : registeredJoins) {
         if (!joinsInDb.contains(join)) {
            tx.addOrcsTypeJoin(join);
         }
      }
   }

   @Override
   public void createSynonymsAndGrants() {
      for (String table : Arrays.asList("OSEE_ACCOUNT_SESSION", "OSEE_ACTIVITY", "OSEE_ACTIVITY_TYPE", "OSEE_ARTIFACT",
         "OSEE_ARTIFACT_ACL", "OSEE_ATTRIBUTE", "OSEE_BRANCH", "OSEE_BRANCH_ACL", "OSEE_CONFLICT",
         "OSEE_IMPORT_INDEX_MAP", "OSEE_IMPORT_MAP", "OSEE_IMPORT_SAVE_POINT", "OSEE_IMPORT_SOURCE", "OSEE_INFO",
         "OSEE_JOIN_ARTIFACT", "OSEE_JOIN_CHAR_ID", "OSEE_JOIN_CLEANUP", "OSEE_JOIN_EXPORT_IMPORT", "OSEE_JOIN_ID",
         "OSEE_JOIN_ID4", "OSEE_JOIN_TRANSACTION", "OSEE_KEY_VALUE", "OSEE_MERGE", "OSEE_OAUTH_AUTHORIZATION",
         "OSEE_OAUTH_CLIENT_CREDENTIAL", "OSEE_OAUTH_TOKEN", "OSEE_PERMISSION", "OSEE_RELATION_LINK",
         "OSEE_SCHEMA_VERSION", "OSEE_SEARCH_TAGS", "OSEE_SEQUENCE", "OSEE_SERVER_LOOKUP", "OSEE_SESSION",
         "OSEE_TAG_GAMMA_QUEUE", "OSEE_TUPLE2", "OSEE_TUPLE3", "OSEE_TUPLE4", "OSEE_TXS", "OSEE_TXS_ARCHIVED",
         "OSEE_TX_DETAILS")) {
         try {
            jdbcClient.runCall("create public synonym " + table + " for " + table);
         } catch (Exception ex) {
            XConsoleLogger.err("Error creating synonym for table " + table);
         }
         try {
            jdbcClient.runCall("grant insert, update, delete, select on " + table + " to osee_client_role");
         } catch (Exception ex) {
            XConsoleLogger.err("Error granting permissions for table " + table);
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
      try {
         orcsApi.getQueryFactory().fromBranch(COMMON).andIds(CoreArtifactTokens.DefaultHierarchyRoot);
         return true;
      } catch (Exception ex) {
         return false;
      }
   }

   @Override
   public void requireRole(UserId user, ArtifactId role) {
      if (!fromCommon.andId(role).andRelatedTo(CoreRelationTypes.Users_User, role).exists()) {
         throw new OseeAccessDeniedException("User [%s] not in user group [%s]", user, role);
      }
   }

   @Override
   public void changeArtifactTypeOutsideofHistory(ArtifactTypeId artifactType, List<? extends ArtifactId> artifacts) {
      String sql = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?";
      OseePreparedStatement batchStatement = jdbcClient.getBatchStatement(sql);
      artifacts.forEach(art -> batchStatement.addToBatch(artifactType, art));
      batchStatement.execute();
   }

   @Override
   public void updateBootstrapUser(UserId accountId) {
      dataStoreAdmin.updateBootstrapUser(accountId);
   }
}