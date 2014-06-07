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
package org.eclipse.osee.orcs.account.admin.internal;

import java.util.Date;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSetTransform.Function;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AccountFactory {

   private final Function<String, ArtifactReadable, Account> function1 = new ArtifactToAccount();
   private final Function<String, ArtifactReadable, AccountPreferences> function2 = new ArtifactToAccountPreferences();
   private final Function<String, ArtifactReadable, SubscriptionGroup> function3 =
      new ArtifactToAccountSubscriptionGroup();

   public ResultSet<Account> newAccountResultSet(ResultSet<ArtifactReadable> results) {
      return ResultSets.transform(results, function1);
   }

   public ResultSet<AccountPreferences> newAccountPreferencesResultSet(ResultSet<ArtifactReadable> results) {
      return ResultSets.transform(results, function2);
   }

   public Account newAccount(ArtifactReadable artifact) {
      AccountPreferences preferences = newAccountPreferences(artifact);
      return new AccountArtifact(artifact.getGuid(), artifact, preferences);
   }

   public AccountPreferences newAccountPreferences(ArtifactReadable artifact) {
      String id = artifact.getGuid();
      return new AccountPreferencesArtifact(id, artifact);
   }

   private class ArtifactToAccount implements Function<String, ArtifactReadable, Account> {

      @Override
      public Account apply(ArtifactReadable source) {
         return newAccount(source);
      }
   }

   private class ArtifactToAccountPreferences implements Function<String, ArtifactReadable, AccountPreferences> {

      @Override
      public AccountPreferences apply(ArtifactReadable source) {
         return newAccountPreferences(source);
      }
   }

   public AccountSession newAccountSession(long accountId, String sessionToken, String accessedFrom, String accessDetails) {
      Date currentDate = new Date();
      return newAccountSession(accountId, sessionToken, currentDate, currentDate, accessedFrom, accessDetails);
   }

   public AccountSession newAccountSession(long accountId, String sessionToken, Date createdOn, Date lastAccessedOn, String accessedFrom, String accessDetails) {
      AccountSessionImpl session = new AccountSessionImpl();
      session.setAccountId(accountId);
      session.setSessionToken(sessionToken);
      session.setCreatedOn(createdOn);
      session.setLastAccessedOn(lastAccessedOn);
      session.setAccessDetails(accessDetails);
      session.setAccessedFrom(accessedFrom);
      return session;
   }

   public SubscriptionGroup newAccountSubscriptionGroup(ArtifactReadable source) {
      return new AccountSubscriptionGroupImpl(source.getGuid(), source);
   }

   public ResultSet<SubscriptionGroup> newAccountSubscriptionGroupResultSet(ResultSet<ArtifactReadable> results) {
      return ResultSets.transform(results, function3);
   }

   private class ArtifactToAccountSubscriptionGroup implements Function<String, ArtifactReadable, SubscriptionGroup> {

      @Override
      public SubscriptionGroup apply(ArtifactReadable source) {
         return newAccountSubscriptionGroup(source);
      }
   }

}
