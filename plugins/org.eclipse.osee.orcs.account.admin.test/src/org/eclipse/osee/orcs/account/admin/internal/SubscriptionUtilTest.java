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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.orcs.account.admin.internal.SubscriptionUtil.ActiveDelegate;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test Case for {@link SubscriptionUtil}
 *
 * @author Roberto E. Escobar
 */
public class SubscriptionUtilTest {

   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(3242);
   private static final String ACCOUNT_NAME = "account-name";

   private static final SubscriptionGroupId GROUP_ID = new SubscriptionGroupId(97012L);
   private static final String SUBSCRIPTION_NAME = "subscription-name";

   @Test
   public void testEncodeDecodeSubscription() {
      String encodedUuid = SubscriptionUtil.toEncodedUuid(ACCOUNT_ID, ACCOUNT_NAME, GROUP_ID, SUBSCRIPTION_NAME);

      ActiveDelegate delegate = Mockito.mock(ActiveDelegate.class);

      Subscription actual = SubscriptionUtil.fromEncodedUuid(encodedUuid, delegate);
      assertEquals(encodedUuid, actual.getGuid());
      assertEquals(SUBSCRIPTION_NAME, actual.getName());
      assertEquals(ACCOUNT_ID, actual.getAccountId());
      assertEquals(ACCOUNT_NAME, actual.getAccountName());
      assertEquals(GROUP_ID, actual.getGroupId());

      when(delegate.isActive(ACCOUNT_ID, GROUP_ID)).thenReturn(true);
      boolean isActive = actual.isActive();

      assertEquals(true, isActive);
      verify(delegate).isActive(ACCOUNT_ID, GROUP_ID);
   }

   @Test
   public void testFromData() {
      String encodedUuid = SubscriptionUtil.toEncodedUuid(ACCOUNT_ID, ACCOUNT_NAME, GROUP_ID, SUBSCRIPTION_NAME);

      Subscription actual = SubscriptionUtil.fromData(ACCOUNT_ID, ACCOUNT_NAME, GROUP_ID, SUBSCRIPTION_NAME, true);

      assertEquals(encodedUuid, actual.getGuid());
      assertEquals(SUBSCRIPTION_NAME, actual.getName());
      assertEquals(ACCOUNT_ID, actual.getAccountId());
      assertEquals(ACCOUNT_NAME, actual.getAccountName());
      assertEquals(GROUP_ID, actual.getGroupId());
      assertEquals(true, actual.isActive());

      actual = SubscriptionUtil.fromData(ACCOUNT_ID, ACCOUNT_NAME, GROUP_ID, SUBSCRIPTION_NAME, false);

      assertEquals(encodedUuid, actual.getGuid());
      assertEquals(SUBSCRIPTION_NAME, actual.getName());
      assertEquals(ACCOUNT_ID, actual.getAccountId());
      assertEquals(ACCOUNT_NAME, actual.getAccountName());
      assertEquals(GROUP_ID, actual.getGroupId());
      assertEquals(false, actual.isActive());
   }

   public void testFromArtifactData() {
      String encodedUuid = SubscriptionUtil.toEncodedUuid(ACCOUNT_ID, ACCOUNT_NAME, GROUP_ID, SUBSCRIPTION_NAME);

      ArtifactReadable accountArt = Mockito.mock(ArtifactReadable.class);
      when(accountArt.getUuid()).thenReturn(ACCOUNT_ID.getUuid());
      when(accountArt.getName()).thenReturn(ACCOUNT_NAME);

      ArtifactReadable subscriptionArt = Mockito.mock(ArtifactReadable.class);
      when(subscriptionArt.getUuid()).thenReturn(GROUP_ID.getId());
      when(subscriptionArt.getName()).thenReturn(SUBSCRIPTION_NAME);

      Subscription actual = SubscriptionUtil.fromArtifactData(accountArt, subscriptionArt, true);

      assertEquals(encodedUuid, actual.getGuid());
      assertEquals(SUBSCRIPTION_NAME, actual.getName());
      assertEquals(ACCOUNT_ID, actual.getAccountId());
      assertEquals(ACCOUNT_NAME, actual.getAccountName());
      assertEquals(GROUP_ID, actual.getGroupId());
      assertEquals(true, actual.isActive());

      actual = SubscriptionUtil.fromArtifactData(accountArt, subscriptionArt, false);

      assertEquals(encodedUuid, actual.getGuid());
      assertEquals(SUBSCRIPTION_NAME, actual.getName());
      assertEquals(ACCOUNT_ID, actual.getAccountId());
      assertEquals(ACCOUNT_NAME, actual.getAccountName());
      assertEquals(GROUP_ID, actual.getGroupId());
      assertEquals(false, actual.isActive());
   }
}
