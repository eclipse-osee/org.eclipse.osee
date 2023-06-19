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

import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.EncryptUtility;

/**
 * @author Roberto E. Escobar
 */
public final class SubscriptionUtil {

   private static final String SUBSCRIPTION_SECRET = "lRL2uka3CwLL88Q1";

   public static interface ActiveDelegate {

      boolean isActive(ArtifactId accountId, SubscriptionGroupId groupId);
   }

   public static String toEncodedUuid(ArtifactId accountId, String accountDisplayName, SubscriptionGroupId groupId,
      String subscriptionName) {
      String rawData =
         String.format("%s:%s:%s:%s", accountId.getIdString(), accountDisplayName, groupId, subscriptionName);
      return EncryptUtility.encrypt(rawData, SUBSCRIPTION_SECRET);
   }

   public static Subscription fromEncodedUuid(String subscriptionUuid, ActiveDelegate delegate) {
      String decrypted = EncryptUtility.decrypt(subscriptionUuid, SUBSCRIPTION_SECRET);
      String[] data = decrypted.split(":");
      int index = 0;
      int accountId = Integer.parseInt(data[index++]);
      ArtifactId artId = ArtifactId.valueOf(accountId);

      String accountDisplayName = data[index++];
      int groupId = Integer.parseInt(data[index++]);
      SubscriptionGroupId subscriptionId = new SubscriptionGroupId((long) groupId);
      String subscriptionName = data[index++];
      return new DelegatingActiveSubscriptionImpl(subscriptionUuid, artId, accountDisplayName, subscriptionId,
         subscriptionName, delegate);
   }

   public static Subscription fromData(ArtifactId accountId, String accountDisplayName, SubscriptionGroupId groupId,
      String subscriptionName, boolean isActive) {
      String encodedUuid = toEncodedUuid(accountId, accountDisplayName, groupId, subscriptionName);
      return new SubscriptionImpl(encodedUuid, accountId, accountDisplayName, groupId, subscriptionName, isActive);
   }

   public static Subscription fromArtifactData(ArtifactReadable account, ArtifactReadable subscription,
      boolean isActive) {
      String accountName = account.getName();
      SubscriptionGroupId subcriptionId = new SubscriptionGroupId(subscription.getId());
      String subscriptionName = subscription.getName();
      return fromData(account, accountName, subcriptionId, subscriptionName, isActive);
   }

   private static final class DelegatingActiveSubscriptionImpl extends SubscriptionImpl {

      private final ActiveDelegate provider;

      private DelegatingActiveSubscriptionImpl(String encodedUuid, ArtifactId accountId, String accountDisplayName, SubscriptionGroupId groupId, String subscriptionName, ActiveDelegate provider) {
         super(encodedUuid, accountId, accountDisplayName, groupId, subscriptionName, false);
         this.provider = provider;
      }

      @Override
      public boolean isActive() {
         return provider.isActive(getAccountId(), getGroupId());
      }
   }

   private static class SubscriptionImpl extends NamedIdentity<String> implements Subscription {

      private final ArtifactId accountId;
      private final String accountDisplayName;
      private final SubscriptionGroupId groupId;
      private final boolean active;

      private SubscriptionImpl(String encodedUuid, ArtifactId accountId, String accountDisplayName, SubscriptionGroupId groupId, String subscriptionName, boolean active) {
         super(encodedUuid, subscriptionName);
         this.accountId = accountId;
         this.accountDisplayName = accountDisplayName;
         this.groupId = groupId;
         this.active = active;
      }

      @Override
      public ArtifactId getAccountId() {
         return accountId;
      }

      @Override
      public String getAccountName() {
         return accountDisplayName;
      }

      @Override
      public SubscriptionGroupId getGroupId() {
         return groupId;
      }

      @Override
      public boolean isActive() {
         return active;
      }

      @Override
      public String toString() {
         return "SubscriptionImpl [accountId=" + accountId + ", accountDisplayName=" + accountDisplayName + ", groupId=" + groupId + ", active=" + isActive() + "]";
      }

      @Override
      public Long getId() {
         return null;
      }
   }
}