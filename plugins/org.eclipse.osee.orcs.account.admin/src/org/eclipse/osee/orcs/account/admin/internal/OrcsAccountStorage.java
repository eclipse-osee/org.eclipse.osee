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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class OrcsAccountStorage extends AbstractOrcsStorage implements AccountStorage {
   private JdbcService jdbcService;
   private AccountSessionStorage sessionStore;
   private Account bootstrapAccount;
   private final Supplier<ResultSet<Account>> anonymousAccountSupplier = Suppliers.memoize(getAnonymousSupplier());

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   @Override
   public void start() {
      super.start();
      JdbcClient jdbcClient = jdbcService.getClient();
      sessionStore = new AccountSessionDatabaseStore(getLogger(), jdbcClient, getFactory());
   }

   @Override
   public void stop() {
      super.stop();
      sessionStore = null;
   }

   @Override
   public boolean userNameExists(String username) {
      return newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, username).exists();
   }

   @Override
   public boolean emailExists(String email) {
      return newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.Email, email).exists();
   }

   @Override
   public boolean displayNameExists(String displayName) {
      return newQuery().andIsOfType(CoreArtifactTypes.User).andNameEquals(displayName).exists();
   }

   @Override
   public ResultSet<Account> getAllAccounts() {
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.User).getResults();
      return getFactory().newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountById(ArtifactId accountId) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).andUuid(accountId.getUuid()).getResults();
      return getFactory().newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getAccountByEmail(String email) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.Email, email).getResults();
      return getFactory().newAccountResultSet(results);
   }

   @Override
   public AccountPreferences getAccountPreferencesById(ArtifactId accountId) {
      ArtifactReadable artifact = newQuery().andIsOfType(CoreArtifactTypes.User).andId(accountId).getArtifact();
      return getFactory().newAccountPreferences(artifact);
   }

   @Override
   public ArtifactId createAccount(CreateAccountRequest request) {
      TransactionBuilder tx = newTransaction("Create Account");
      ArtifactId artId = tx.createArtifact(CoreArtifactTypes.User, request.getDisplayName());
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.Email, request.getEmail());
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.UserId, request.getUserName());
      tx.setSoleAttributeValue(artId, CoreAttributeTypes.Active, request.isActive());

      Map<String, String> preferences = request.getPreferences();
      if (preferences != null && !preferences.isEmpty()) {
         String prefValue = asString(artId, preferences);
         tx.createAttribute(artId, CoreAttributeTypes.UserSettings, prefValue);
      }
      tx.commit();
      return artId;
   }

   @Override
   public void setActive(ArtifactId accountId, boolean active) {
      TransactionBuilder tx = newTransaction("Update Account Active");
      tx.setSoleAttributeValue(accountId, CoreAttributeTypes.Active, active);
      tx.commit();
   }

   private String asString(ArtifactId artId, Map<String, String> preferences) {
      PropertyStore settings = new PropertyStore(artId.getIdString());
      for (Entry<String, String> entry : preferences.entrySet()) {
         settings.put(entry.getKey(), entry.getValue());
      }

      StringWriter stringWriter = new StringWriter();
      try {
         settings.save(stringWriter);
         return stringWriter.toString();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public void setAccountPreferences(ArtifactId accountId, Map<String, String> preferences) {
      String prefValue = asString(accountId, preferences);

      TransactionBuilder tx = newTransaction("User - Save Settings");
      tx.setSoleAttributeFromString(accountId, CoreAttributeTypes.UserSettings, prefValue);
      tx.commit();
   }

   @Override
   public void deleteAccount(ArtifactId accountId) {
      TransactionBuilder tx = newTransaction("Delete User");
      tx.deleteArtifact(accountId);
      tx.commit();
   }

   @Override
   public ResultSet<AccountSession> getAccountSessionById(ArtifactId accountId) {
      try {
         return sessionStore.getAccountSessionByAccountId(accountId).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResultSet<AccountSession> getAccountSessionBySessionToken(String sessionToken) {
      try {
         return sessionStore.getAccountSessionBySessionToken(sessionToken).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public AccountSession createAccountSession(String sessionToken, Account account, String remoteAddress, String accessDetails) {
      ArtifactId artId = ArtifactId.valueOf(account.getId());
      AccountSession session = getFactory().newAccountSession(artId, sessionToken, remoteAddress, accessDetails);
      try {
         sessionStore.createAccountSession(Collections.singleton(session)).call();
         return session;
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public void deleteAccountSessionBySessionToken(String sessionToken) {
      try {
         sessionStore.deleteAccountSessionBySessionToken(sessionToken).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ResultSet<Account> getAnonymousAccount() {
      ResultSet<Account> toReturn;
      if (isInitialized()) {
         toReturn = anonymousAccountSupplier.get();
      } else {
         toReturn = ResultSets.singleton(bootstrapAccount);
      }
      return toReturn;
   }

   private Supplier<ResultSet<Account>> getAnonymousSupplier() {
      return new Supplier<ResultSet<Account>>() {
         @Override
         public ResultSet<Account> get() {
            ResultSet<ArtifactReadable> results =
               newQuery().andIsOfType(CoreArtifactTypes.User).andUuid(SystemUser.Anonymous.getUuid()).getResults();
            return getFactory().newAccountResultSet(results);
         }
      };
   }

   @Override
   public void setAccountWebPreferences(ArtifactId artifactId, String preferences) {
      TransactionBuilder tx = newTransaction("User - Save Web Preferences");
      tx.setSoleAttributeFromString(artifactId, CoreAttributeTypes.WebPreferences, preferences);
      tx.commit();
   }

   @Override
   public AccountWebPreferences getAccountWebPreferencesById(ArtifactId accountId) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).andUuid(accountId.getUuid()).getResults();
      return getFactory().newAccountWebPreferences(results.getExactlyOne());
   }

   @Override
   public ResultSet<Account> getAccountByName(String name) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, name).getResults();
      return getFactory().newAccountResultSet(results);
   }

}
