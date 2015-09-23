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
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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

   public List<AccountSessionDetailsData> getAccountSessionById(String accountId) {
      ResultSet<AccountSession> result = accountAdmin.getAccountSessionByUniqueField(accountId);
      List<AccountSessionDetailsData> toReturn = new ArrayList<>();
      for (AccountSession session : result) {
         toReturn.add(AccountDataUtil.asAccountAccessData(session));
      }
      return toReturn;
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

      Identifiable<String> id = accountAdmin.createAccount(request);
      ResultSet<Account> result = accountAdmin.getAccountByGuid(id.getGuid());
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

   public boolean deleteAccount(String accountId) {
      return accountAdmin.deleteAccount(accountId);
   }

   public boolean setAccountActive(String accountId, boolean active) {
      return accountAdmin.setActive(accountId, active);
   }

   public AccountActiveData isActive(String accountId) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(accountId);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountActiveData(account);
   }

   public List<AccountInfoData> getAllAccounts() {
      List<AccountInfoData> toReturn = new ArrayList<>();
      ResultSet<Account> result = accountAdmin.getAllAccounts();
      for (Account account : result) {
         toReturn.add(AccountDataUtil.asAccountData(account));
      }
      return toReturn;
   }

   public AccountDetailsData getAccountDetailsData(String accountId) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(accountId);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountDetailsData(account);
   }

   public AccountInfoData getAccountData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(value);
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

   public boolean editAccountWebPreferencesData(String accountGuid, String key, String itemId, String newValue) {
      return accountAdmin.setAccountWebPreference(accountGuid, key, itemId, newValue);
   }

   public AccountWebPreferences getDefaultAccountWebPreferencesData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByGuid(value);
      Account account = result.getExactlyOne();
      AccountWebPreferences preferences = account.getWebPreferences();
      return preferences;
   }

   public AccountWebPreferences getAccountWebPreferencesData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByGuid(value);
      Account account = result.getExactlyOne();
      AccountWebPreferences preferences = account.getWebPreferences();
      return preferences;
   }

   public AccountPreferencesData getAccountPreferencesData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(value);
      Account account = result.getExactlyOne();
      AccountPreferences preferences = account.getPreferences();
      return AccountDataUtil.asAccountPreferencesData(preferences);
   }

   public boolean setAccountPreferences(String accountId, AccountPreferencesInput input) {
      return accountAdmin.setAccountPreferences(accountId, input.getMap());
   }

   public AccountInfoData getAnonymousAccount() {
      ResultSet<Account> result = accountAdmin.getAnonymousAccount();
      Account account = result.getExactlyOne();
      return AccountDataUtil.asAccountData(account);
   }

}
