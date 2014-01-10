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
import org.eclipse.osee.account.admin.AccountAccess;
import org.eclipse.osee.account.admin.AccountPreferences;
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

   public AccountAccess newAccountAccess(long accountId, String accessToken, String accessedFrom, String accessDetails) {
      Date currentDate = new Date();
      return newAccountAccess(accountId, accessToken, currentDate, currentDate, accessedFrom, accessDetails);
   }

   public AccountAccess newAccountAccess(long accountId, String accessToken, Date createdOn, Date lastAccessedOn, String accessedFrom, String accessDetails) {
      AccountAccessImpl session = new AccountAccessImpl();
      session.setAccountId(accountId);
      session.setAccessToken(accessToken);
      session.setCreatedOn(createdOn);
      session.setLastAccessedOn(lastAccessedOn);
      session.setAccessDetails(accessDetails);
      session.setAccessedFrom(accessedFrom);
      return session;
   }
}
