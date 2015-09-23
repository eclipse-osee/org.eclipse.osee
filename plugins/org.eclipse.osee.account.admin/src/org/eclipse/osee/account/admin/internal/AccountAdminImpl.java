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
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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

   private AccountResolver resolver;
   private Validator validator;

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
      resolver = new AccountResolver(validator, this);
      update(props);
   }

   public void stop() {
      logger.trace("Stopping OrcsAccountAdminImpl...");
   }

   public void update(Map<String, Object> props) {
      validator.configure(props);
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
   public ResultSet<Account> getAccountById(long id) {
      return getStorage().getAccountByLocalId(id);
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesById(long id) {
      return getStorage().getAccountPreferencesById(id);
   }

   @Override
   public ResultSet<Account> getAccountById(Identifiable<String> id) {
      Conditions.checkNotNull(id, "id");
      return getAccountByGuid(id.getGuid());
   }

   @Override
   public ResultSet<Account> getAccountByGuid(String guid) {
      Conditions.checkNotNull(guid, "guid");
      return getStorage().getAccountByGuid(guid);
   }

   @Override
   public ResultSet<Account> getAccountByUserName(String username) {
      Conditions.checkNotNull(username, "username");
      return getStorage().getAccountByUserName(username);
   }

   @Override
   public ResultSet<Account> getAccountByEmail(String email) {
      Conditions.checkNotNull(email, "email");
      return getStorage().getAccountByEmail(email);
   }

   @Override
   public ResultSet<Account> getAccountByName(String name) {
      Conditions.checkNotNull(name, "name");
      return getStorage().getAccountByName(name);
   }

   @Override
   public ResultSet<Account> getAccountByUniqueField(String uniqueField) {
      return resolver.resolveAccount(uniqueField);
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesByGuid(String guid) {
      Conditions.checkNotNull(guid, "guid");
      return getStorage().getAccountPreferencesByGuid(guid);
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesById(Identifiable<String> id) {
      Conditions.checkNotNull(id, "id");
      return getAccountPreferencesByGuid(id.getGuid());
   }

   @Override
   public ResultSet<AccountPreferences> getAccountPreferencesByUniqueField(String uniqueField) {
      return resolver.resolveAccountPreferences(uniqueField);
   }

   @Override
   public Identifiable<String> createAccount(CreateAccountRequest request) {
      Conditions.checkNotNull(request, "create account request");

      Validator validator = getValidator();
      validator.validate(AccountField.EMAIL, request.getEmail());
      validator.validate(AccountField.USERNAME, request.getUserName());
      validator.validate(AccountField.DISPLAY_NAME, request.getDisplayName());

      return getStorage().createAccount(request);
   }

   @Override
   public boolean setActive(Identifiable<String> id, boolean active) {
      Conditions.checkNotNull(id, "account id");
      ResultSet<Account> result = getAccountById(id);
      return setActive(result, active);
   }

   @Override
   public boolean setActive(String uniqueField, boolean active) {
      ResultSet<Account> result = getAccountByUniqueField(uniqueField);
      return setActive(result, active);
   }

   private boolean setActive(ResultSet<Account> result, boolean active) {
      boolean modified = false;
      Account account = result.getExactlyOne();
      if (account.isActive() != active) {
         getStorage().setActive(account, active);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean deleteAccount(Identifiable<String> id) {
      Conditions.checkNotNull(id, "account id");
      ResultSet<Account> result = getAccountById(id);
      return deleteAccount(result);
   }

   @Override
   public boolean deleteAccount(String uniqueField) {
      ResultSet<Account> result = getAccountByUniqueField(uniqueField);
      return deleteAccount(result);
   }

   private boolean deleteAccount(ResultSet<Account> result) {
      boolean modified = false;
      Account account = result.getOneOrNull();
      if (account != null) {
         getStorage().deleteAccount(account);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean setAccountPreferences(Identifiable<String> id, Map<String, String> preferences) {
      Conditions.checkNotNull(id, "account preference id");
      Conditions.checkNotNull(preferences, "preferences");
      ResultSet<AccountPreferences> result = getAccountPreferencesById(id);
      return setAccountPreferences(result, preferences);
   }

   @Override
   public boolean setAccountPreferences(String uniqueField, Map<String, String> preferences) {
      Conditions.checkNotNull(preferences, "preferences");
      ResultSet<AccountPreferences> result = getAccountPreferencesByUniqueField(uniqueField);
      return setAccountPreferences(result, preferences);
   }

   private boolean setAccountPreferences(ResultSet<AccountPreferences> result, Map<String, String> preferences) {
      boolean modified = false;
      AccountPreferences prefs = result.getExactlyOne();
      Map<String, String> original = prefs.asMap();
      if (Compare.isDifferent(original, preferences)) {
         getStorage().setAccountPreferences(prefs, preferences);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean setAccountPreference(Identifiable<String> id, String key, String value) {
      Conditions.checkNotNull(id, "account preference id");
      ResultSet<AccountPreferences> result = getAccountPreferencesById(id);
      return setAccountPreference(result, key, value);
   }

   @Override
   public boolean setAccountPreference(String uniqueField, String key, String value) {
      ResultSet<AccountPreferences> result = getAccountPreferencesByUniqueField(uniqueField);
      return setAccountPreference(result, key, value);
   }

   @Override
   public boolean setAccountWebPreference(String accountGuid, String key, String itemId, String newValue) {
      Conditions.checkNotNull(key, "account preference key");
      Conditions.checkNotNull(newValue, "account preference value", "Use delete account preference instead");

      AccountWebPreferences allPreferences = getStorage().getAccountWebPreferencesByGuid(accountGuid);
      String newPreferences = AccountUtil.updateSinglePreference(allPreferences, key, itemId, newValue);

      boolean modified = false;
      if (Strings.isValid(newPreferences) && !newPreferences.equalsIgnoreCase(allPreferences.toString())) {
         getStorage().setAccountWebPreferences(accountGuid, newPreferences);
         modified = true;
      }
      return modified;
   }

   private boolean setAccountPreference(ResultSet<AccountPreferences> result, String key, String value) {
      Conditions.checkNotNull(key, "account preference key");
      Conditions.checkNotNull(value, "account preference value", "Use delete account preference instead");

      boolean modified = false;
      AccountPreferences prefs = result.getExactlyOne();
      Map<String, String> original = prefs.asMap();
      HashMap<String, String> newPrefs = new HashMap<>(original);
      newPrefs.put(key, value);
      if (Compare.isDifferent(original, newPrefs)) {
         getStorage().setAccountPreferences(prefs, newPrefs);
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean deleteAccountPreference(Identifiable<String> id, String key) {
      Conditions.checkNotNull(id, "account preference id");
      ResultSet<AccountPreferences> result = getAccountPreferencesById(id);
      return deleteAccountPreference(result, key);
   }

   @Override
   public boolean deleteAccountPreference(String uniqueField, String key) {
      ResultSet<AccountPreferences> result = getAccountPreferencesByUniqueField(uniqueField);
      return deleteAccountPreference(result, key);
   }

   private boolean deleteAccountPreference(ResultSet<AccountPreferences> result, String key) {
      Conditions.checkNotNull(key, "account preference key");
      boolean modified = false;
      AccountPreferences prefs = result.getExactlyOne();
      Map<String, String> original = prefs.asMap();
      HashMap<String, String> newPrefs = new HashMap<>(original);
      newPrefs.remove(key);
      if (Compare.isDifferent(original, newPrefs)) {
         getStorage().setAccountPreferences(prefs, newPrefs);
         modified = true;
      }
      return modified;
   }

   private String notAvailableWhenNullorEmpty(String value) {
      return Strings.isValid(value) ? value : AccountConstants.NOT_AVAILABLE;
   }

   @Override
   public AccountSession login(AccountLoginRequest request) {
      String id = authenticate(request);
      ResultSet<Account> result = getAccountByUniqueField(id);
      Account account = result.getAtMostOneOrNull();
      if (account == null) {
         throw new AccountLoginException(
            "Login Error - Unable to find account for username[%s] using authentication scheme[%s] and userId[%s]",
            request.getUserName(), request.getScheme(), id);
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
      return authenticate.getName();
   }

   @Override
   public ResultSet<AccountSession> getAccountSessionByUniqueField(String uniqueField) {
      ResultSet<Account> result = getAccountByUniqueField(uniqueField);
      Account account = result.getExactlyOne();
      return storage.getAccountSessionById(account.getId());
   }

   @Override
   public boolean logout(String sessionToken) {
      boolean modified = false;
      ResultSet<AccountSession> result = getAccountSessionBySessionToken(sessionToken);
      AccountSession account = result.getOneOrNull();
      if (account != null) {
         storage.deleteAccountSessionBySessionToken(sessionToken);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<Account> getAnonymousAccount() {
      return storage.getAnonymousAccount();
   }

}
