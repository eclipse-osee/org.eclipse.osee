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
package org.eclipse.osee.account.admin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.eclipse.osee.account.admin.AccessDetails;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountConfiguration;
import org.eclipse.osee.account.admin.AccountConstants;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.AccountLoginException;
import org.eclipse.osee.account.admin.AccountLoginRequest;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.account.admin.internal.validator.Validators;
import org.eclipse.osee.account.rest.model.AccountUtil;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.admin.AuthenticationRequestBuilder;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AccountAdminImpl implements AccountAdmin {
   private Log logger;
   private AccountStorage storage;
   private AuthenticationAdmin authenticationAdmin;

   private Validator validator;
   private volatile AccountConfiguration config;

   public void setConfig(AccountConfiguration config) {
      this.config = config;
   }

   @Override
   public AccountConfiguration getConfig() {
      return config;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAccountStorage(AccountStorage storage) {
      this.storage = storage;
   }

   public void setAuthenticationAdmin(AuthenticationAdmin authenticationAdmin) {
      this.authenticationAdmin = authenticationAdmin;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting OrcsAccountAdminImpl...");

      validator = Validators.newAccountValidator(logger, storage);
      update(props);
   }

   public void stop() {
      logger.trace("Stopping OrcsAccountAdminImpl...");
   }

   public void update(Map<String, Object> props) {
      validator.configure(props);
      setConfig(AccountConfiguration.newConfig(props));
   }

   private AccountStorage getStorage() {
      return storage;
   }

   protected Validator getValidator() {
      return validator;
   }

   @Override
   public ResultSet<Account> getAllAccounts() {
      return getStorage().getAllAccounts();
   }

   @Override
   public ResultSet<Account> getAccountById(ArtifactId accountId) {
      return getStorage().getAccountById(accountId);
   }

   @Override
   public AccountPreferences getAccountPreferencesById(ArtifactId accountId) {
      return getStorage().getAccountPreferencesById(accountId);
   }

   @Override
   public ArtifactId createAccount(CreateAccountRequest request) {
      Conditions.checkNotNull(request, "create account request");

      Validator validator = getValidator();
      validator.validate(AccountField.EMAIL, request.getEmail());
      validator.validate(AccountField.USERNAME, request.getUserName());
      validator.validate(AccountField.DISPLAY_NAME, request.getDisplayName());

      return getStorage().createAccount(request);
   }

   @Override
   public boolean setActive(ArtifactId accountId, boolean active) {
      boolean modified = false;
      Account account = getAccountById(accountId).getOneOrDefault(Account.SENTINEL);
      if (account.isValid() && account.isActive() != active) {
         getStorage().setActive(accountId, active);
         modified = true;
      }
      return modified;
   }

   @Override
   public void deleteAccount(ArtifactId accountId) {
      getStorage().deleteAccount(accountId);
   }

   @Override
   public boolean setAccountPreferences(ArtifactId accountId, Map<String, String> preferences) {
      boolean modified = false;
      Conditions.checkNotNull(preferences, "preferences");
      AccountPreferences prefs = getAccountPreferencesById(accountId);
      Map<String, String> original = prefs.asMap();
      if (Compare.isDifferent(original, preferences)) {
         getStorage().setAccountPreferences(accountId, preferences);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean setAccountPreference(ArtifactId accountId, String key, String value) {
      boolean modified = false;
      Conditions.checkNotNull(key, "account preference key");
      Conditions.checkNotNull(value, "account preference value", "Use delete account preference instead");
      AccountPreferences prefs = getAccountPreferencesById(accountId);
      Map<String, String> original = prefs.asMap();
      HashMap<String, String> newPrefs = new HashMap<>(original);
      newPrefs.put(key, value);
      if (Compare.isDifferent(original, newPrefs)) {
         getStorage().setAccountPreferences(accountId, newPrefs);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean setAccountWebPreference(ArtifactId accountId, String key, String itemId, String newValue) {
      Conditions.checkNotNull(key, "account preference key");
      Conditions.checkNotNull(newValue, "account preference value", "Use delete account preference instead");

      AccountWebPreferences allPreferences = getStorage().getAccountWebPreferencesById(accountId);
      String newPreferences = AccountUtil.updateSinglePreference(allPreferences, key, itemId, newValue);

      boolean modified = false;
      if (Strings.isValid(newPreferences) && !newPreferences.equalsIgnoreCase(allPreferences.toString())) {
         getStorage().setAccountWebPreferences(accountId, newPreferences);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean deleteAccountPreference(ArtifactId accountId, String key) {
      boolean modified = false;
      Conditions.checkNotNull(key, "account preference key");
      AccountPreferences prefs = getAccountPreferencesById(accountId);
      Map<String, String> original = prefs.asMap();
      HashMap<String, String> newPrefs = new HashMap<>(original);
      newPrefs.remove(key);
      if (Compare.isDifferent(original, newPrefs)) {
         getStorage().setAccountPreferences(accountId, newPrefs);
         modified = true;
      }
      return modified;
   }

   private String notAvailableWhenNullorEmpty(String value) {
      return Strings.isValid(value) ? value : AccountConstants.NOT_AVAILABLE;
   }

   @Override
   public AccountSession login(AccountLoginRequest request) {
      String email = authenticate(request);
      ResultSet<Account> result = getAccountByEmail(email);
      Account account = result.getAtMostOneOrDefault(Account.SENTINEL);
      if (account.isInvalid()) {
         throw new AccountLoginException(
            "Login Error - Unable to find account for username[%s] using authentication scheme[%s]",
            request.getUserName(), request.getScheme());
      }
      String sessionToken = UUID.randomUUID().toString();

      AccessDetails details = request.getDetails();
      String remoteAddress = notAvailableWhenNullorEmpty(details.getRemoteAddress());
      String accessDetails = notAvailableWhenNullorEmpty(details.getAccessDetails());
      return storage.createAccountSession(sessionToken, account, remoteAddress, accessDetails);
   }

   @Override
   public ResultSet<AccountSession> getAccountSessionBySessionToken(String sessionToken) {
      Conditions.checkNotNull(sessionToken, "account session token");
      return storage.getAccountSessionBySessionToken(sessionToken);
   }

   private String authenticate(AccountLoginRequest login) {
      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(login.getScheme())//
         .userName(login.getUserName())//
         .password(login.getPassword())//
         .build();

      AuthenticatedUser authenticate = authenticationAdmin.authenticate(request);
      return authenticate.getEmailAddress();
   }

   @Override
   public boolean logout(String sessionToken) {
      boolean modified = false;
      ResultSet<AccountSession> result = getAccountSessionBySessionToken(sessionToken);
      AccountSession account = result.getOneOrDefault(AccountSession.SENTINEL);
      if (!account.equals(AccountSession.SENTINEL)) {
         storage.deleteAccountSessionBySessionToken(sessionToken);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<Account> getAnonymousAccount() {
      return storage.getAnonymousAccount();
   }

   @Override
   public ResultSet<Account> getAccountByEmail(String email) {
      Conditions.checkNotNull(email, "email");
      return getStorage().getAccountByEmail(email);
   }

   @Override
   public ResultSet<AccountSession> getAccountSessionById(ArtifactId accountId) {
      return getStorage().getAccountSessionById(accountId);
   }

   @Override
   public ResultSet<Account> getAccountByName(String name) {
      Conditions.checkNotNull(name, "name");
      return getStorage().getAccountByName(name);
   }

}
