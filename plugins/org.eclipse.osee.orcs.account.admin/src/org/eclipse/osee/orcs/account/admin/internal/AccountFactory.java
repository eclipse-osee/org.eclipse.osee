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

package org.eclipse.osee.orcs.account.admin.internal;

import java.util.Date;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AccountFactory {

   public ResultSet<Account> newAccountResultSet(ResultSet<ArtifactReadable> results) {
      return ResultSets.transform(results, this::newAccount);
   }

   private Account newAccount(ArtifactReadable account) {
      AccountPreferences preferences = new AccountPreferencesArtifact(account.getGuid(), account);
      AccountWebPreferences webPreferences = newAccountWebPreferences(account);
      return new AccountArtifact(account, preferences, webPreferences);
   }

   public AccountPreferences newAccountPreferencesResultSet(ArtifactReadable account) {
      return new AccountPreferencesArtifact(account.getGuid(), account);
   }

   public AccountPreferences newAccountPreferences(ArtifactReadable artifact) {
      String id = artifact.getGuid();
      return new AccountPreferencesArtifact(id, artifact);
   }

   public AccountWebPreferences newAccountWebPreferences(ArtifactReadable artifact) {
      String webPreferencesJson = artifact.getSoleAttributeAsString(CoreAttributeTypes.WebPreferences, "{}");
      return new AccountWebPreferences(webPreferencesJson, artifact.getName());
   }

   public AccountSession newAccountSession(ArtifactId accountId, String sessionToken, String accessedFrom, String accessDetails) {
      Date currentDate = new Date();
      return newAccountSession(accountId, sessionToken, currentDate, currentDate, accessedFrom, accessDetails);
   }

   public AccountSession newAccountSession(ArtifactId accountId, String sessionToken, Date createdOn, Date lastAccessedOn, String accessedFrom, String accessDetails) {
      AccountSessionImpl session = new AccountSessionImpl();
      session.setAccountId(accountId.getId());
      session.setSessionToken(sessionToken);
      session.setCreatedOn(createdOn);
      session.setLastAccessedOn(lastAccessedOn);
      session.setAccessDetails(accessDetails);
      session.setAccessedFrom(accessedFrom);
      return session;
   }

   public SubscriptionGroup newAccountSubscriptionGroup(ArtifactReadable source) {
      return new AccountSubscriptionGroupImpl(source);
   }

   public ResultSet<SubscriptionGroup> newAccountSubscriptionGroupResultSet(ResultSet<ArtifactReadable> results) {
      return ResultSets.transform(results, this::newAccountSubscriptionGroup);
   }
}