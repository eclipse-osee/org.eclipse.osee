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
package org.eclipse.osee.orcs.account.admin.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.account.admin.internal.SubscriptionUtil.ActiveDelegate;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.utility.OrcsUtil;

/**
 * @author Roberto E. Escobar
 */
public class OrcsSubscriptionStorage extends AbstractOrcsStorage implements SubscriptionStorage {

   @Override
   public ResultSet<Subscription> getSubscriptionsByAccountLocalId(long accountId) {
      int intAccountId = Long.valueOf(accountId).intValue();
      ResultSet<ArtifactReadable> accountResults =
         newQuery().andIsOfType(CoreArtifactTypes.User).andUuid(intAccountId).getResults();
      ArtifactReadable account = accountResults.getExactlyOne();

      ResultSet<ArtifactReadable> allGroups = newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).getResults();
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      for (ArtifactReadable group : allGroups) {
         boolean related = account.areRelated(CoreRelationTypes.Users_Artifact, group);
         subscriptions.add(SubscriptionUtil.fromArtifactData(account, group, related));
      }
      return ResultSets.newResultSet(subscriptions);
   }

   @Override
   public void updateSubscription(long accountId, long groupId, boolean activate) {
      int intAccountId = Long.valueOf(accountId).intValue();
      int intGroupId = Long.valueOf(groupId).intValue();

      ArtifactReadable account = newQuery().andUuid(intAccountId).getResults().getExactlyOne();
      ArtifactReadable group =
         newQuery().andUuid(intGroupId).andIsOfType(CoreArtifactTypes.SubscriptionGroup).getResults().getExactlyOne();

      String txComment =
         String.format("%s user [%s] to [%s].", activate ? "Subscribe" : "Unsubscribe", account.getName(),
            group.getName());
      TransactionBuilder tx = newTransaction(txComment);
      // relate/unrelate (Side_A Art) <- Users -> (Side_B Art)
      if (activate) {
         tx.relate(group, CoreRelationTypes.Users_Artifact, account);
      } else {
         tx.unrelate(group, CoreRelationTypes.Users_Artifact, account);
      }
      tx.commit();
   }

   @Override
   public Subscription getSubscription(String subscriptionUuid) {
      return SubscriptionUtil.fromEncodedUuid(subscriptionUuid, new LazyActiveDelegate());
   }

   private class LazyActiveDelegate implements ActiveDelegate {

      private final AtomicBoolean wasRun = new AtomicBoolean(false);
      private volatile boolean isActive;

      @Override
      public boolean isActive(long accountId, long groupId) {
         if (wasRun.compareAndSet(false, true)) {
            int intAccountId = Long.valueOf(accountId).intValue();
            int intGroupId = Long.valueOf(groupId).intValue();

            ArtifactReadable account = newQuery().andUuid(intAccountId).getResults().getExactlyOne();
            ArtifactReadable group = newQuery().andUuid(intGroupId).getResults().getExactlyOne();
            isActive = account.areRelated(CoreRelationTypes.Users_Artifact, group);
         }
         return isActive;
      }
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroups() {
      ResultSet<ArtifactReadable> results = newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).getResults();
      return getFactory().newAccountSubscriptionGroupResultSet(results);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByLocalId(long groupId) {
      int intGroupId = Long.valueOf(groupId).intValue();
      ResultSet<ArtifactReadable> results =
         newQuery().andUuid(intGroupId).andIsOfType(CoreArtifactTypes.SubscriptionGroup).getResults();
      return getFactory().newAccountSubscriptionGroupResultSet(results);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByName(String name) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).andNameEquals(name).getResults();
      return getFactory().newAccountSubscriptionGroupResultSet(results);
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroupByUuid(String uuid) {
      ResultSet<ArtifactReadable> results =
         newQuery().andGuid(uuid).andIsOfType(CoreArtifactTypes.SubscriptionGroup).getResults();
      return getFactory().newAccountSubscriptionGroupResultSet(results);
   }

   @SuppressWarnings("unchecked")
   @Override
   public SubscriptionGroup createSubscriptionGroup(String name) {
      String comment = String.format("Create subscription group [%s]", name);

      TransactionBuilder tx = newTransaction(comment);
      ArtifactId artId = tx.createArtifact(CoreArtifactTypes.SubscriptionGroup, name);
      tx.commit();

      ArtifactReadable groupArt = newQuery().andIds(artId).getResults().getExactlyOne();
      return getFactory().newAccountSubscriptionGroup(groupArt);
   }

   @Override
   public void deleteSubscriptionGroup(SubscriptionGroup group) {
      ArtifactId artId = OrcsUtil.newArtifactId(Lib.generateArtifactIdAsInt(), group.getGuid(), group.getName());

      String comment = String.format("Delete subscription group [%s]", group.getName());
      TransactionBuilder tx = newTransaction(comment);
      tx.deleteArtifact(artId);
      tx.commit();
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByLocalId(long groupId) {
      int intGroupId = Long.valueOf(groupId).intValue();
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.User).andRelatedToLocalIds(CoreRelationTypes.Users_Artifact,
            intGroupId).getResults();
      return getFactory().newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByName(String name) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).andNameEquals(name).getResults();
      ArtifactReadable group = results.getOneOrNull();
      return getMembers(group);
   }

   @Override
   public ResultSet<Account> getSubscriptionGroupMembersByUuid(String uuid) {
      ResultSet<ArtifactReadable> results =
         newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).andGuid(uuid).getResults();
      ArtifactReadable group = results.getOneOrNull();
      return getMembers(group);
   }

   private ResultSet<Account> getMembers(ArtifactReadable group) {
      ResultSet<Account> toReturn;
      if (group != null) {
         ResultSet<ArtifactReadable> related = group.getRelated(CoreRelationTypes.Users_User);
         toReturn = getFactory().newAccountResultSet(related);
      } else {
         toReturn = ResultSets.emptyResultSet();
      }
      return toReturn;
   }

   @Override
   public boolean subscriptionGroupNameExists(String groupName) {
      int count = newQuery().andIsOfType(CoreArtifactTypes.SubscriptionGroup).andNameEquals(groupName).getCount();
      return count > 0;
   }
}
