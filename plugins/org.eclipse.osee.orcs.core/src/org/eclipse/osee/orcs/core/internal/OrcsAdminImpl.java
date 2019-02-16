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