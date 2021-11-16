/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.account.admin.internal.SubscriptionUtil.ActiveDelegate;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class OrcsSubscriptionStorage extends AbstractOrcsStorage implements SubscriptionStorage {

   // for ReviewOsgiXml public void setLogger(Log logger) {
   // for ReviewOsgiXml public void setOrcsApi(OrcsApi orcsApi) {
   // for ReviewOsgiXml public void setAccountFactory(AccountFactory factory) {

   @Override
   public ResultSet<Subscription> getSubscriptionsByAccountId(ArtifactId accountId) {
      ResultSet<ArtifactReadable> accountResults =
         newQuery().andTypeEquals(CoreArtifactTypes.User).andUuid(accountId.getUuid()).getResults();
      ArtifactReadable account = accountResults.getExactlyOne();

      ResultSet<ArtifactReadable> allGroups =
         newQuery().andTypeEquals(CoreArtifactTypes.SubscriptionGroup).getResults();
      List<Subscription> subscriptions = new ArrayList<>();
      for (ArtifactReadable group : allGroups) {
         boolean related = account.areRelated(CoreRelationTypes.Users_Artifact, group);
         subscriptions.add(SubscriptionUtil.fromArtifactData(account, group, related));
      }
      return ResultSets.newResultSet(subscriptions);
   }

   @Override
   public void updateSubscription(Subscription subscription, boolean activate) {
      Long intAccountId = subscription.getAccountId().getUuid();
      Long intGroupId = subscription.getGroupId().getId();

      ArtifactReadable account = newQuery().andUuid(intAccountId).getResults().getExactlyOne();
      ArtifactReadable group =
         newQuery().andUuid(intGroupId).andTypeEquals(CoreArtifactTypes.SubscriptionGroup).getResults().getExactlyOne();

      String txComment = String.format("%s user [%s] to [%s].", activate ? "Subscribe" : "Unsubscribe",
         account.getName(), group.getName());
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
   public Subscription getSubscriptionByEncodedId(String encodedId) {
      return SubscriptionUtil.fromEncodedUuid(encodedId, new LazyActiveDelegate());
   }

   private class LazyActiveDelegate implements ActiveDelegate {

      private final AtomicBoolean wasRun = new AtomicBoolean(false);
      private volatile boolean isActive;

      @Override
      public boolean isActive(ArtifactId accountId, SubscriptionGroupId groupId) {
         if (wasRun.compareAndSet(false, true)) {
            int intAccountId = Long.valueOf(accountId.getUuid()).intValue();
            int intGroupId = Long.valueOf(groupId.getId()).intValue();

            ArtifactReadable account = newQuery().andUuid(intAccountId).getResults().getExactlyOne();
            ArtifactReadable group = newQuery().andUuid(intGroupId).getResults().getExactlyOne();
            isActive = account.areRelated(CoreRelationTypes.Users_Artifact, group);
         }
         return isActive;
      }
   }

   @Override
   public ResultSet<SubscriptionGroup> getSubscriptionGroups() {
      ResultSet<ArtifactReadable> results = newQuery().andTypeEquals(CoreArtifactTypes.SubscriptionGroup).getResults();
      return getFactory().newAccountSubscriptionGroupResultSet(results);
   }

   @Override
   public SubscriptionGroupId createSubscriptionGroup(String name) {
      String comment = String.format("Create subscription group [%s]", name);

      TransactionBuilder tx = newTransaction(comment);
      ArtifactId artId = tx.createArtifact(CoreArtifactTypes.SubscriptionGroup, name);
      tx.commit();

      ArtifactReadable groupArt = newQuery().andId(artId).getResults().getExactlyOne();
      return new SubscriptionGroupId(groupArt.getUuid());
   }

   @Override
   public boolean deleteSubscriptionGroup(SubscriptionGroupId subscriptionId) {
      boolean toReturn = false;

      SubscriptionGroup subscriptionGroup = getSubscriptionGroupById(subscriptionId);

      if (subscriptionGroup != null) {
         ArtifactId subscriptionAsArtId = ArtifactId.create(subscriptionId);
         String comment = String.format("Delete subscription group [%s]", subscriptionGroup.getName());
         TransactionBuilder tx = newTransaction(comment);
         tx.deleteArtifact(subscriptionAsArtId);
         tx.commit();
         toReturn = true;
      }
      return toReturn;
   }

   @Override
   public SubscriptionGroup getSubscriptionGroupById(SubscriptionGroupId groupId) {
      ArtifactReadable artifact = newQuery().andId(ArtifactId.valueOf(groupId)).getArtifact();
      return getFactory().newAccountSubscriptionGroup(artifact);
   }

   @Override
   public boolean subscriptionGroupNameExists(String groupName) {
      return newQuery().andTypeEquals(CoreArtifactTypes.SubscriptionGroup).andNameEquals(groupName).exists();
   }

   @Override
   public ResultSet<Account> getSubscriptionMembersById(SubscriptionGroupId groupId) {
      ArtifactId groupArtId = ArtifactId.create(groupId);
      ResultSet<ArtifactReadable> results =
         newQuery().andTypeEquals(CoreArtifactTypes.User).andRelatedTo(CoreRelationTypes.Users_Artifact,
            groupArtId).getResults();
      return getFactory().newAccountResultSet(results);
   }

   @Override
   public ResultSet<Account> getMembersOfSubscriptionGroupById(SubscriptionGroupId subscriptionId) {
      ResultSet<ArtifactReadable> results =
         newQuery().andTypeEquals(CoreArtifactTypes.SubscriptionGroup).andUuid(subscriptionId.getId()).getResults();
      ArtifactReadable group = results.getOneOrDefault(ArtifactReadable.SENTINEL);
      return getMembers(group);
   }

   private ResultSet<Account> getMembers(ArtifactReadable group) {
      ResultSet<Account> toReturn;
      if (group.isValid()) {
         toReturn = getFactory().newAccountResultSet(group.getRelated(CoreRelationTypes.Users_User));
      } else {
         toReturn = ResultSets.emptyResultSet();
      }
      return toReturn;
   }
}
