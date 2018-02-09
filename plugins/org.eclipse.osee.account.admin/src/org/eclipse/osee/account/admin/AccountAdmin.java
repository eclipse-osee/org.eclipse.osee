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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountAdmin {

   ResultSet<Account> getAllAccounts();

   ResultSet<Account> getAccountById(ArtifactId accountId);

   ResultSet<Account> getAccountByEmail(String email);

   ResultSet<Account> getAccountByName(String name);

   AccountPreferences getAccountPreferencesById(ArtifactId accountId);

   ArtifactId createAccount(CreateAccountRequest request);

   boolean setActive(ArtifactId accountId, boolean active);

   boolean setAccountPreferences(ArtifactId accountId, Map<String, String> preferences);

   boolean setAccountPreference(ArtifactId accountId, String key, String value);

   boolean deleteAccountPreference(ArtifactId accountId, String key);

   AccountSession login(AccountLoginRequest request);

   boolean logout(String token);

   ResultSet<AccountSession> getAccountSessionBySessionToken(String token);

   ResultSet<AccountSession> getAccountSessionById(ArtifactId accountId);

   ResultSet<Account> getAnonymousAccount();

   boolean setAccountWebPreference(ArtifactId accountId, String key, String itemId, String newValue);

   void deleteAccount(ArtifactId accountId);

   AccountConfiguration getConfig();

}
