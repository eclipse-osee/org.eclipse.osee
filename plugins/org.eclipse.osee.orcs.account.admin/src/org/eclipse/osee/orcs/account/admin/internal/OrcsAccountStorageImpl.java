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
package org.eclipse.osee.orcs.account.admin.internal;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAccess;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.orcs.utility.OrcsUtil;

/**
 * @author Roberto E. Escobar
 */
public class OrcsAccountStorageImpl implements AccountStorage {

   private Log logger;
   private OrcsApi orcsApi;
   private IOseeDatabaseService dbService;

   private AccountFactory factory;
   private IOseeBranch storageBranch;
   private ApplicationContext context;
   private AccountAccessStorage accessStore;

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      logger.trace("Starting OrcsAccountStorageImpl...");
      factory = new AccountFactory();
      storageBranch = CoreBranches.COMMON;

      String sessionId = SystemUser.OseeSystem.getGuid();
      context = newApplicationContext(sessionId);

      accessStore = new AccountAccessDatabaseStore(logger, dbService, factory);
   }

   public void stop() {
      logger.trace("Stopping OrcsAccountStorageImpl...");
      storageBranch = null;
      factory = null;
   }

   private ApplicationContext newApplicationContext(final String sessionId) {
      return new ApplicationContext() {

         @Override
         public String getSessionId() {
            return sessionId;
         }
      };
   }

   private IOseeBranch getBranch() {
      return storageBranch;
   }

   private QueryBuilder newQuery() {
      QueryFactory queryFactory = orcsApi.getQueryFactory(context);
      return queryFactory.fromBranch(getBranch());
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getSystemUser() {
      return newQuery().andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   @Override
   public boolean userNameExists(String username) {
      int count = newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, username).getCount();
      return count > 0;
   }

   @Override
   public boolean emailExists(String email) {
      int count = newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.Email, email).getCount();
      return count > 0;
   }

   @Override
   public boolean displayNameExists(String displayName) {
      int count = newQuery().andIsOfType(CoreArtifactTypes.User).andNameEquals(displayName).getCount();
      return count > 0;
   }

   @Override
   public ResultSet<Account> getAllAccounts() {
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.User).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByUserName(String username) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, username).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByUuid(String accountUuid) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).andGuid(accountUuid).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByLocalId(long accountId) {
      int id = Long.valueOf(accountId).intValue();
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.User).andLocalId(id).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByEmail(String email) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.Email, Operator.EQUAL, email).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByName(String name) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).andNameEquals(name).getResults();
      return factory.newAccountResultSet(results);
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesById(long accountId) {
      int id = Long.valueOf(accountId).intValue();
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.User).andLocalId(id).getResults();
      return factory.newAccountPreferencesResultSet(results);
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesByUuid(String uuid) {
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.User).andGuid(uuid).getResults();
      return factory.newAccountPreferencesResultSet(results);
   }

   private TransactionBuilder newTransaction(String comment) {
      TransactionFactory transactionFactory = orcsApi.getTransactionFactory(context);
      return transactionFactory.createTransaction(getBranch(), getSystemUser(), comment);
   }

   @Override
   public Identifiable<String> createAccount(CreateAccountRequest request) {
      TransactionBuilder tx = newTransaction("Create Account");
      ArtifactId artId = tx.createArtifact(CoreArtifactTypes.User, request.getDisplayName());
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.Email, request.getEmail());
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.UserId, request.getUserName());
      tx.setSoleAttributeValue(artId, CoreAttributeTypes.Active, request.isActive());

      Map<String, String> preferences = request.getPreferences();
      if (preferences != null && !preferences.isEmpty()) {
         String prefValue = asString(artId.getGuid(), preferences);
         tx.createAttribute(artId, CoreAttributeTypes.UserSettings, prefValue);
      }
      tx.commit();
      return artId;
   }

   @Override
   public void setActive(Identifiable<String> account, boolean active) {
      ArtifactId artId = OrcsUtil.newArtifactId(account.getGuid(), account.getName());

      TransactionBuilder tx = newTransaction("Update Account Active");
      tx.setSoleAttributeValue(artId, CoreAttributeTypes.Active, active);
      tx.commit();
   }

   private String asString(String uuid, Map<String, String> preferences) {
      PropertyStore settings = new PropertyStore(uuid);
      for (Entry<String, String> entry : preferences.entrySet()) {
         settings.put(entry.getKey(), entry.getValue());
      }

      StringWriter stringWriter = new StringWriter();
      try {
         settings.save(stringWriter);
         return stringWriter.toString();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void setAccountPreferences(Identity<String> account, Map<String, String> preferences) {
      String prefValue = asString(account.getGuid(), preferences);

      ArtifactId artId = OrcsUtil.newArtifactId(account.getGuid(), "N/A");
      TransactionBuilder tx = newTransaction("User - Save Settings");
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.UserSettings, prefValue);
      tx.commit();
   }

   @Override
   public void deleteAccount(Identifiable<String> account) {
      ArtifactId artId = OrcsUtil.newArtifactId(account.getGuid(), account.getName());

      TransactionBuilder tx = newTransaction("Delete User");
      tx.deleteArtifact(artId);
      tx.commit();
   }

   @Override
   public ResultSet<AccountAccess> getAccountAccessById(long accountId) {
      try {
         return accessStore.getAccountAccessByAccountId(accountId).call();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public ResultSet<AccountAccess> getAccountAccessByAccessToken(String accessToken) {
      try {
         return accessStore.getAccountAccessByAccessToken(accessToken).call();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public AccountAccess createAccountAccess(String accessToken, Account account, String remoteAddress, String accessDetails) {
      AccountAccess access = factory.newAccountAccess(account.getId(), accessToken, remoteAddress, accessDetails);
      try {
         accessStore.createAccountAccess(Collections.singleton(access)).call();
         return access;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void deleteAccountAccessByAccessToken(String accessToken) {
      try {
         accessStore.deleteAccountAccessByAccessToken(accessToken).call();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

}
