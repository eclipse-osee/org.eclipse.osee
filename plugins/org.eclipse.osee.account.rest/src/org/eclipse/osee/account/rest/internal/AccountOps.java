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
import org.eclipse.osee.account.admin.AccountAccess;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountLoginRequest;
import org.eclipse.osee.account.admin.AccountLoginRequestBuilder;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.account.rest.model.AccountAccessData;
import org.eclipse.osee.account.rest.model.AccountActiveData;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountLoginData;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountPreferencesInput;
import org.eclipse.osee.account.rest.model.AccountSessionData;
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

   public List<AccountAccessData> getAccountAccessById(String accountId) {
      ResultSet<AccountAccess> result = accountAdmin.getAccountAccessByUniqueField(accountId);
      List<AccountAccessData> toReturn = new ArrayList<AccountAccessData>();
      for (AccountAccess access : result) {
         toReturn.add(asAccountAccessData(access));
      }
      return toReturn;
   }

   public AccountAccessData asAccountAccessData(AccountAccess access) {
      AccountAccessData data = new AccountAccessData();
      data.setAccountId(access.getAccountId());
      data.setAccessDetails(access.getAccessDetails());
      data.setAccessedFrom(access.getAccessedFrom());
      data.setCreatedOn(access.getCreatedOn());
      data.setLastAccessedOn(access.getLastAccessedOn());
      return data;
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
      AccountAccess session = accountAdmin.login(request);
      return asSessionData(session);
   }

   public boolean doLogout(String token) {
      return accountAdmin.logout(token);
   }

   public AccountSessionData asSessionData(AccountAccess session) {
      AccountSessionData data = new AccountSessionData();
      data.setAccountId(session.getAccountId());
      data.setToken(session.getAccessToken());
      return data;
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
      ResultSet<Account> result = accountAdmin.getAccountByUuid(id.getGuid());
      Account account = result.getExactlyOne();
      return asAccountData(account);
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
      return asAccountActiveData(account);
   }

   public List<AccountInfoData> getAllAccounts() {
      List<AccountInfoData> toReturn = new ArrayList<AccountInfoData>();
      ResultSet<Account> result = accountAdmin.getAllAccounts();
      for (Account account : result) {
         toReturn.add(asAccountData(account));
      }
      return toReturn;
   }

   public AccountDetailsData getAccountDetailsData(String accountId) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(accountId);
      Account account = result.getExactlyOne();
      return asAccountDetailsData(account);
   }

   public AccountInfoData getAccountData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(value);
      Account account = result.getExactlyOne();
      return asAccountData(account);
   }

   public AccountPreferencesData getAccountPreferencesData(String value) {
      ResultSet<Account> result = accountAdmin.getAccountByUniqueField(value);
      Account account = result.getExactlyOne();
      AccountPreferences preferences = account.getPreferences();
      return asAccountPreferencesData(preferences);
   }

   private AccountDetailsData asAccountDetailsData(Account account) {
      AccountDetailsData data = new AccountDetailsData();
      fillData(account, data);
      AccountPreferencesData preferences = asAccountPreferencesData(account.getPreferences());
      data.setPreferences(preferences);
      return data;
   }

   private AccountInfoData asAccountData(Account account) {
      AccountInfoData data = new AccountInfoData();
      fillData(account, data);
      return data;
   }

   private void fillData(Account account, AccountInfoData data) {
      data.setAccountId(account.getId());
      data.setGuid(account.getGuid());
      data.setName(account.getName());
      data.setEmail(account.getEmail());
      data.setUserName(account.getUserName());
      data.setActive(account.isActive());
   }

   private AccountPreferencesData asAccountPreferencesData(AccountPreferences preferences) {
      AccountPreferencesData data = new AccountPreferencesData();
      data.setId(preferences.getId());
      data.setMap(preferences.asMap());
      return data;
   }

   private AccountActiveData asAccountActiveData(Account account) {
      AccountActiveData data = new AccountActiveData();
      data.setAccountId(account.getId());
      data.setGuid(account.getGuid());
      data.setActive(account.isActive());
      return data;
   }

   public boolean setAccountPreferences(String accountId, AccountPreferencesInput input) {
      return accountAdmin.setAccountPreferences(accountId, input.getMap());
   }

}
