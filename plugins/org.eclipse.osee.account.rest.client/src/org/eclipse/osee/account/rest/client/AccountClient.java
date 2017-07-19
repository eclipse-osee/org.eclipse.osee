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
package org.eclipse.osee.account.rest.client;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * AccountId parameter can be any field that uniquely identifies an account such as:
 * <ol>
 * <li>ID</li>
 * <li>UUID</li>
 * <li>Email</li>
 * <li>User name</li>
 * <li>Display name</li>
 * </ol>
 *
 * @author Roberto E. Escobar
 */
public interface AccountClient {

   public static interface UnsubscribeInfo {
      String getName();

      URI getUnsubscribeUri();
   }

   AccountSessionData login(String scheme, String username, String password);

   boolean logout(AccountSessionData session);

   AccountInfoData createAccount(String userName, AccountInput input);

   boolean deleteAccount(Long accountId);

   ResultSet<AccountSessionDetailsData> getAccountSessionDataByUniqueField(String accountId);

   ResultSet<AccountInfoData> getAllAccounts();

   AccountDetailsData getAccountDetailsById(Long accountId);

   AccountPreferencesData getAccountPreferencesById(Long accountId);

   boolean setAccountActive(Long accountId, boolean active);

   boolean isAccountActive(Long accountId);

   boolean setAccountPreferences(Long accountId, Map<String, String> preferences);

   ResultSet<UnsubscribeInfo> getUnsubscribeUris(Long accountId, Collection<String> groupNames);

   AccountWebPreferences getAccountWebPreferencesByUniqueField(ArtifactId accountId);

   boolean isLocalHost();

   String getBaseUri();

}
