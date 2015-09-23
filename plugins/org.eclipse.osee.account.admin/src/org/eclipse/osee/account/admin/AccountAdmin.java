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
package org.eclipse.osee.account.admin;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountAdmin {

   ResultSet<Account> getAllAccounts();

   ResultSet<Account> getAccountByUniqueField(String uniqueField);

   ResultSet<Account> getAccountById(long id);

   ResultSet<Account> getAccountById(Identifiable<String> id);

   ResultSet<Account> getAccountByGuid(String guid);

   ResultSet<Account> getAccountByUserName(String username);

   ResultSet<Account> getAccountByEmail(String email);

   ResultSet<Account> getAccountByName(String name);

   ResultSet<AccountPreferences> getAccountPreferencesByUniqueField(String uniqueField);

   ResultSet<AccountPreferences> getAccountPreferencesById(long id);

   ResultSet<AccountPreferences> getAccountPreferencesById(Identifiable<String> id);

   ResultSet<AccountPreferences> getAccountPreferencesByGuid(String id);

   Identifiable<String> createAccount(CreateAccountRequest request);

   boolean deleteAccount(Identifiable<String> id);

   boolean deleteAccount(String uniqueField);

   boolean setActive(Identifiable<String> id, boolean active);

   boolean setActive(String uniqueField, boolean active);

   boolean setAccountPreferences(Identifiable<String> id, Map<String, String> preferences);

   boolean setAccountPreferences(String uniqueField, Map<String, String> preferences);

   boolean setAccountPreference(Identifiable<String> id, String key, String value);

   boolean setAccountPreference(String uniqueField, String key, String value);

   boolean deleteAccountPreference(Identifiable<String> id, String key);

   boolean deleteAccountPreference(String uniqueField, String key);

   AccountSession login(AccountLoginRequest request);

   boolean logout(String token);

   ResultSet<AccountSession> getAccountSessionBySessionToken(String token);

   ResultSet<AccountSession> getAccountSessionByUniqueField(String uniqueField);

   ResultSet<Account> getAnonymousAccount();

   boolean setAccountWebPreference(String accountGuid, String key, String itemId, String newValue);

}
