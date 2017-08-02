/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.internal;

import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.AccountActiveData;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupData;

/**
 * @author Roberto E. Escobar
 */
public final class AccountDataUtil {

   private AccountDataUtil() {
      // Utility class
   }

   public static AccountSessionDetailsData asAccountAccessData(AccountSession session) {
      AccountSessionDetailsData data = new AccountSessionDetailsData();
      data.setAccountId(session.getAccountId());
      data.setAccessDetails(session.getAccessDetails());
      data.setAccessedFrom(session.getAccessedFrom());
      data.setCreatedOn(session.getCreatedOn());
      data.setLastAccessedOn(session.getLastAccessedOn());
      return data;
   }

   public static AccountSessionData asSessionData(AccountSession session) {
      AccountSessionData data = new AccountSessionData();
      data.setAccountId(session.getAccountId());
      data.setToken(session.getSessionToken());
      return data;
   }

   public static AccountDetailsData asAccountDetailsData(Account account) {
      AccountDetailsData data = new AccountDetailsData();
      fillData(account, data);
      AccountPreferencesData preferences = asAccountPreferencesData(account.getPreferences());
      data.setPreferences(preferences);
      return data;
   }

   public static AccountInfoData asAccountData(Account account) {
      AccountInfoData data = new AccountInfoData();
      fillData(account, data);
      return data;
   }

   private static void fillData(Account account, AccountInfoData data) {
      data.setAccountId(account.getId());
      data.setName(account.getName());
      data.setEmail(account.getEmail());
      data.setUserName(account.getUserName());
      data.setActive(account.isActive());
   }

   public static AccountPreferencesData asAccountPreferencesData(AccountPreferences preferences) {
      AccountPreferencesData data = new AccountPreferencesData();
      data.setId(preferences.getId());
      data.setMap(preferences.asMap());
      return data;
   }

   public static AccountActiveData asAccountActiveData(Account account) {
      AccountActiveData data = new AccountActiveData();
      data.setAccountId(account.getId());
      data.setActive(account.isActive());
      return data;
   }

   public static SubscriptionData asAccountSubscriptionData(Subscription subscription) {
      SubscriptionData data = new SubscriptionData();
      data.setGuid(subscription.getGuid());
      data.setName(subscription.getName());
      data.setActive(subscription.isActive());
      data.setAccountName(subscription.getAccountName());
      return data;
   }

   public static SubscriptionGroupData asSubscriptionGroupData(SubscriptionGroup src) {
      SubscriptionGroupData data = new SubscriptionGroupData();
      data.setGuid(src.getGuid());
      data.setName(src.getName());
      data.setSubscriptionGroupId(src.getGroupId());
      return data;
   }

   public static AccountInfoData asAccountInfoData(OseePrincipal principal) {
      AccountInfoData toReturn = new AccountInfoData();
      toReturn.setAccountId(principal.getGuid());
      toReturn.setActive(principal.isActive());
      toReturn.setEmail(principal.getEmailAddress());
      toReturn.setName(principal.getName());
      toReturn.setUserName(principal.getUserName());
      toReturn.setRoles(principal.getRoles());
      return toReturn;
   }

}
