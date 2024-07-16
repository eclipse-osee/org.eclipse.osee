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

package org.eclipse.osee.account.admin.ds;

import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public interface AccountStorage {

   ResultSet<Account> getAllAccounts();

   ResultSet<Account> getAccountById(ArtifactId accountUuid);

   ResultSet<Account> getAccountByEmail(String email);

   ResultSet<Account> getAccountByName(String name);

   AccountPreferences getAccountPreferencesById(ArtifactId accountId);

   ArtifactId createAccount(CreateAccountRequest request);

   void setActive(ArtifactId accountId, boolean active);

   boolean userNameExists(String username);

   boolean emailExists(String email);

   boolean displayNameExists(String displayName);

   ResultSet<AccountSession> getAccountSessionById(ArtifactId accountId);

   ResultSet<AccountSession> getAccountSessionBySessionToken(String sessionToken);

   AccountSession createAccountSession(String sessionToken, Account account, String accessDetails, String remoteAddress);

   void deleteAccountSessionBySessionToken(String sessionToken);

   AccountWebPreferences getAccountWebPreferencesById(ArtifactId accountId);

   void setAccountWebPreferences(ArtifactId artifactId, String preferences);

   void setAccountPreferences(ArtifactId accountId, Map<String, String> preferences);

   void deleteAccount(ArtifactId accountId);

}