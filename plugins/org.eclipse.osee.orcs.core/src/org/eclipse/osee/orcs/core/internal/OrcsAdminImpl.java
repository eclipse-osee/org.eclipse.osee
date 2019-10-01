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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
   private final JdbcClient jdbcClient;

   public OrcsAdminImpl(OrcsApi orcsApi, Log logger, OrcsSession session, DataStoreAdmin dataStoreAdmin, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.session = session;
      this.dataStoreAdmin = dataStoreAdmin;
      this.eventAdmin = eventAdmin;
      fromCommon = orcsApi.getQueryFactory().fromBranch(COMMON);
      jdbcClient = dataStoreAdmin.getJdbcClient();
   }

   @Override
   public void createDatastoreAndSystemBranches(String typeModel) {
      typeModel += OseeInf.getResourceContents("OseeTypes_Framework.osee", getClass());
      dataStoreAdmin.createDataStore();
      orcsApi.getOrcsTypes().loadTypes(typeModel);
      new CreateSystemBranches(orcsApi, eventAdmin).create(typeModel);
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
   public void createUsers(TransactionBuilder tx, Iterable<UserToken> users) {
      if (tx.getAuthor().notEqual(SystemUser.OseeSystem)) {
         requireRole(tx.getAuthor(), CoreUserGroups.OseeAdmin);
      }
      // Create UserGroups Header if not already created
      ArtifactToken userGroupHeader = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.UserGroups).getArtifactOrNull();
      if (userGroupHeader == null) {
         ArtifactToken oseeConfig = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            CoreArtifactTokens.OseeConfiguration).getArtifactOrNull();
         userGroupHeader = tx.createArtifact(oseeConfig, CoreArtifactTokens.UserGroups);
      }

      // Create users and relate to user groups
      Map<ArtifactToken, ArtifactToken> userGroupToArtifact = new HashMap<>();
      List<ArtifactReadable> defaultGroups =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andAttributeIs(CoreAttributeTypes.DefaultGroup,
            "true").getResults().getList();
      List<ArtifactReadable> existingUsers = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         CoreArtifactTypes.User).getResults().getList();
      for (UserToken userToken : users) {
         if (existingUsers.contains(userToken)) {
            throw new OseeStateException("User %s already exists", userToken);
         }
         ArtifactId user = tx.createArtifact(userToken);
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Active, userToken.isActive());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.UserId, userToken.getUserId());
         tx.setSoleAttributeValue(user, CoreAttributeTypes.Email, userToken.getEmail());

         Collection<ArtifactToken> roles = userToken.getRoles();
         for (ArtifactToken userGroup : roles) {
            ArtifactToken userGroupArt = getOrCreate(userGroup, userGroupToArtifact, tx, userGroupHeader);
            tx.relate(userGroupArt, CoreRelationTypes.Users_User, user);
         }
         for (ArtifactToken userGroup : defaultGroups) {
            ArtifactToken userGroupArt = getOrCreate(userGroup, userGroupToArtifact, tx, userGroupHeader);
            tx.relate(userGroupArt, CoreRelationTypes.Users_User, user);
         }
         setUserInfo(tx, userToken, defaultGroups);
      }
   }

   private ArtifactToken getOrCreate(ArtifactToken userGroup, Map<ArtifactToken, ArtifactToken> userGroupToArtifact, TransactionBuilder tx, ArtifactToken userGroupHeader) {
      ArtifactToken userGroupArt = userGroupToArtifact.get(userGroup);
      if (userGroupArt == null) {
         userGroupArt = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(userGroup).getArtifactOrNull();
      }
      if (userGroupArt == null) {
         userGroupArt = tx.createArtifact(userGroup);
         tx.addChild(userGroupHeader, userGroupArt);
      }
      userGroupToArtifact.put(userGroup, userGroupArt);
      return userGroupArt;
   }

   @Override
   public void createUser(TransactionBuilder tx, UserToken userToken) {
      if (tx.getAuthor().notEqual(SystemUser.OseeSystem)) {
         requireRole(tx.getAuthor(), CoreUserGroups.OseeAdmin);
      }
      List<ArtifactId> defaultGroups =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andAttributeIs(CoreAttributeTypes.DefaultGroup,
            "true").asArtifactIds();
      setUserInfo(tx, userToken, defaultGroups);
   }

   private void setUserInfo(TransactionBuilder tx, UserToken userToken, List<? extends ArtifactId> defaultGroups) {
      ArtifactId userId = tx.createArtifact(userToken);
      tx.setSoleAttributeValue(userId, CoreAttributeTypes.Active, userToken.isActive());
      tx.setSoleAttributeValue(userId, CoreAttributeTypes.UserId, userToken.getUserId());
      tx.setSoleAttributeValue(userId, CoreAttributeTypes.Email, userToken.getEmail());
      for (ArtifactToken userRole : userToken.getRoles()) {
         tx.relate(userRole, CoreRelationTypes.Users_User, userId);
      }
      for (ArtifactId userGroup : defaultGroups) {
         tx.relate(userGroup, CoreRelationTypes.Users_User, userId);
      }
   }

   @Override
   public void changeArtifactTypeOutsideofHistory(ArtifactTypeId artifactType, List<? extends ArtifactId> artifacts) {
      String sql = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?";
      OseePreparedStatement batchStatement = jdbcClient.getBatchStatement(sql);
      artifacts.forEach(art -> batchStatement.addToBatch(artifactType, art));
      batchStatement.execute();
   }
}