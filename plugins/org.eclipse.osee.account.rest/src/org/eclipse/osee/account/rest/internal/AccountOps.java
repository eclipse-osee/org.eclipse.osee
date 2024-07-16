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

package org.eclipse.osee.account.rest.internal;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountLoginRequest;
import org.eclipse.osee.account.admin.AccountLoginRequestBuilder;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.account.rest.model.AccountActiveData;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountLoginData;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountPreferencesInput;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class AccountOps {

   private final AccountAdmin accountAdmin;

   public AccountOps(AccountAdmin accountAdmin) {
      super();
      this.accountAdmin = accountAdmin;
   }

   public RequestInfo asRequestInfo(HttpServletRequest request) {
      final String userAgent = RequestUtil.getUserAgent(request);
      final String remoteAddress = RequestUtil.getClientIpAddrress(request);
      return new RequestInfo() {

         @Override
         public String getDetails() {
            return userAgent;
         }

         @Override
         public String getRemoteIpAddress() {
            return remoteAddress;
         }

      };
   }

   public List<AccountSessionDetailsData> getAccountSessionById(ArtifactId accountId) {
      ResultSet<AccountSession> result = accountAdmin.getAccountSessionById(accountId);
      List<AccountSessionDetailsData> toReturn = new ArrayList<>();
      for (AccountSession session : result) {
         toReturn.add(AccountDataUtil.asAccountAccessData(session));
      }
      return toReturn;
   }

   public AccountInfoData getAccountData(ArtifactId value) {
      ResultSet<Account> result = accountAdmin.getAccountById(value);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

   public AccountInfoData getAccountDataByName(String name) {
      ResultSet<Account> result = accountAdmin.getAccountByName(name);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

   public AccountSessionData doLogin(RequestInfo info, AccountLoginData input) {
      AccountLoginRequestBuilder builder = AccountLoginRequestBuilder.newBuilder();
      AccountLoginRequest request = builder//
         .userName(input.getUsername())//
         .password(input.getPassword())//
         .scheme(input.getScheme()) //
         .accessedBy(info.getDetails())//
         .remoteAddress(info.getRemoteIpAddress()) //
         .build();
      AccountSession session = accountAdmin.login(request);
      return AccountDataUtil.asSessionData(session);
   }

   public boolean doLogout(String token) {
      return accountAdmin.logout(token);
   }

   public AccountInfoData createAccount(String username, AccountInput input) {
      CreateAccountRequestBuilder builder = CreateAccountRequestBuilder.newBuilder();
      CreateAccountRequest request = builder//
         .userName(username)//
         .displayName(input.getName())//
         .email(input.getEmail())//
         .active(input.isActive()) //
         .prefs(input.getPreferences()) //
         .build();

      ArtifactId id = accountAdmin.createAccount(request);
      ResultSet<Account> result = accountAdmin.getAccountById(id);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

   public void deleteAccount(ArtifactId accountId) {
      accountAdmin.deleteAccount(accountId);
   }

   public boolean setAccountActive(ArtifactId accountId, boolean active) {
      return accountAdmin.setActive(accountId, active);
   }

   public AccountActiveData isActive(ArtifactId accountId) {
      ResultSet<Account> result = accountAdmin.getAccountById(accountId);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountActiveData(account);
   }

   public List<AccountInfoData> getAllAccounts() {
      List<AccountInfoData> toReturn = new ArrayList<>();
      for (Account account : accountAdmin.getAllAccounts()) {
         toReturn.add(AccountDataUtil.asAccountData(account));
      }
      return toReturn;
   }

   public AccountDetailsData getAccountDetailsData(ArtifactId accountId) {
      ResultSet<Account> result = accountAdmin.getAccountById(accountId);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountDetailsData(account);
   }

   public boolean editAccountWebPreferencesData(ArtifactId accountId, String key, String itemId, String newValue) {
      return accountAdmin.setAccountWebPreference(accountId, key, itemId, newValue);
   }

   public AccountWebPreferences getDefaultAccountWebPreferencesData(ArtifactId accountId) {
      ResultSet<Account> result = accountAdmin.getAccountById(accountId);
      Account account = result.getExactlyOne();
      AccountWebPreferences preferences = account.getWebPreferences();
      return preferences;
   }

   public AccountWebPreferences getAccountWebPreferencesData(ArtifactId id) {
      ResultSet<Account> result = accountAdmin.getAccountById(id);
      Account account = result.getExactlyOne();
      AccountWebPreferences preferences = account.getWebPreferences();
      return preferences;
   }

   public AccountPreferencesData getAccountPreferencesDataById(ArtifactId accountId) {
      ResultSet<Account> result = accountAdmin.getAccountById(accountId);
      Account account = result.getExactlyOne();
      AccountPreferences preferences = account.getPreferences();
      return AccountDataUtil.asAccountPreferencesData(preferences);
   }

   public AccountPreferencesData getAccountPreferencesDataByEmail(String email) {
      ResultSet<Account> result = accountAdmin.getAccountByEmail(email);
      Account account = result.getExactlyOne();
      AccountPreferences preferences = account.getPreferences();
      return AccountDataUtil.asAccountPreferencesData(preferences);
   }

   public boolean setAccountPreferences(ArtifactId accountId, AccountPreferencesInput input) {
      return accountAdmin.setAccountPreferences(accountId, input.getMap());
   }
}