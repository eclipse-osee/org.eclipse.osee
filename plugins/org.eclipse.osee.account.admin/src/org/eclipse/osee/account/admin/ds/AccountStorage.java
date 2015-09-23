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
package org.eclipse.osee.account.admin.ds;

import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountStorage {

   ResultSet<Account> getAllAccounts();

   ResultSet<Account> getAccountByUserName(String username);

   ResultSet<Account> getAccountByGuid(String guid);

   ResultSet<Account> getAccountByUuid(String accountUuid);

   ResultSet<Account> getAccountByLocalId(long accountId);

   ResultSet<Account> getAccountByEmail(String email);

   ResultSet<Account> getAccountByName(String name);

   ResultSet<AccountPreferences> getAccountPreferencesById(long accountId);

   ResultSet<AccountPreferences> getAccountPreferencesByGuid(String guid);

   Identifiable<String> createAccount(CreateAccountRequest request);

   void setActive(Identifiable<String> account, boolean active);

   void setAccountPreferences(Identity<String> account, Map<String, String> preferences);

   void deleteAccount(Identifiable<String> account);

   boolean userNameExists(String username);

   boolean emailExists(String email);

   boolean displayNameExists(String displayName);

   ResultSet<AccountSession> getAccountSessionById(long accountId);

   ResultSet<AccountSession> getAccountSessionBySessionToken(String sessionToken);

   AccountSession createAccountSession(String sessionToken, Account account, String accessDetails, String remoteAddress);

   void deleteAccountSessionBySessionToken(String sessionToken);

   ResultSet<Account> getAnonymousAccount();

   AccountWebPreferences getAccountWebPreferencesByGuid(String accountGuid);

   AccountWebPreferences getAccountWebPreferencesById(int accountId);

   void setAccountWebPreferences(String accountGuid, String newPreferences);

}
