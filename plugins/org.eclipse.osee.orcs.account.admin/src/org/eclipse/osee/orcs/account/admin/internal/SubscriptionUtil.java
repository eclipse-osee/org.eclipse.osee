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

import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.EncryptUtility;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public final class SubscriptionUtil {

   private static final String SUBSCRIPTION_SECRET = "lRL2uka3CwLL88Q1";

   public static interface ActiveDelegate {

      boolean isActive(long accountId, long groupId);
   }

   public static String toEncodedUuid(long accountId, String accountDisplayName, long groupId, String subscriptionName) {
      String rawData = String.format("%s:%s:%s:%s", accountId, accountDisplayName, groupId, subscriptionName);
      return EncryptUtility.encrypt(rawData, SUBSCRIPTION_SECRET);
   }

   public static Subscription fromEncodedUuid(String subscriptionUuid, ActiveDelegate delegate) {
      String decrypted = EncryptUtility.decrypt(subscriptionUuid, SUBSCRIPTION_SECRET);
      String[] data = decrypted.split(":");
      int index = 0;
      int accountId = Integer.parseInt(data[index++]);
      String accountDisplayName = data[index++];
      int groupId = Integer.parseInt(data[index++]);
      String subscriptionName = data[index++];
      return new DelegatingActiveSubscriptionImpl(subscriptionUuid, accountId, accountDisplayName, groupId,
         subscriptionName, delegate);
   }

   public static Subscription fromData(long accountId, String accountDisplayName, long groupId, String subscriptionName, boolean isActive) {
      String encodedUuid = toEncodedUuid(accountId, accountDisplayName, groupId, subscriptionName);
      return new SubscriptionImpl(encodedUuid, accountId, accountDisplayName, groupId, subscriptionName, isActive);
   }

   public static Subscription fromArtifactData(ArtifactReadable account, ArtifactReadable subscription, boolean isActive) {
      int accountId = account.getLocalId();
      String accountName = account.getName();
      int groupId = subscription.getLocalId();
      String subscriptionName = subscription.getName();
      return fromData(accountId, accountName, groupId, subscriptionName, isActive);
   }

   private static final class DelegatingActiveSubscriptionImpl extends SubscriptionImpl {

      private final ActiveDelegate provider;

      private DelegatingActiveSubscriptionImpl(String encodedUuid, long accountId, String accountDisplayName, long groupId, String subscriptionName, ActiveDelegate provider) {
         super(encodedUuid, accountId, accountDisplayName, groupId, subscriptionName, false);
         this.provider = provider;
      }

      @Override
      public boolean isActive() {
         return provider.isActive(getAccountId(), getGroupId());
      }
   }

   private static class SubscriptionImpl extends NamedIdentity<String> implements Subscription {

      private final long accountId;
      private final String accountDisplayName;
      private final long groupId;
      private final boolean active;

      private SubscriptionImpl(String encodedUuid, long accountId, String accountDisplayName, long groupId, String subscriptionName, boolean active) {
         super(encodedUuid, subscriptionName);
         this.accountId = accountId;
         this.accountDisplayName = accountDisplayName;
         this.groupId = groupId;
         this.active = active;
      }

      @Override
      public long getAccountId() {
         return accountId;
      }

      @Override
      public String getAccountName() {
         return accountDisplayName;
      }

      @Override
      public long getGroupId() {
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

   }
}