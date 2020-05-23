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

package org.eclipse.osee.account.rest.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Date;
import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test Case for {@link AccountDataUtil}
 *
 * @author Roberto E. Escobar
 */
public class AccountDataUtilTest {

   @Test
   public void testAsAccountAccessData() {
      Date d1 = newRandomDate();
      Date d2 = newRandomDate();

      AccountSession access = mockAccess(789L, "t3", d1, d2, "f3", "d3");

      AccountSessionDetailsData actual = AccountDataUtil.asAccountAccessData(access);

      assertAccess(actual, 789L, d1, d2, "f3", "d3");
   }

   @Test
   public void testAsSessionData() {
      Date d1 = newRandomDate();
      Date d2 = newRandomDate();
      AccountSession access = mockAccess(123L, "t1", d1, d2, "f1", "d1");

      AccountSessionData actual = AccountDataUtil.asSessionData(access);

      assertEquals((Long) 123L, actual.getAccountId());
      assertEquals("t1", actual.getToken());
   }

   @Test
   public void testAsSubscriptionData() {
      Subscription subscription = Mockito.mock(Subscription.class);
      when(subscription.getGuid()).thenReturn("ABCDE");
      when(subscription.getName()).thenReturn("group-1");
      when(subscription.getGroupId()).thenReturn(new SubscriptionGroupId(98765L));
      when(subscription.getAccountId()).thenReturn(ArtifactId.valueOf(123145));
      when(subscription.getAccountName()).thenReturn("account-1");
      when(subscription.isActive()).thenReturn(true);

      SubscriptionData actual = AccountDataUtil.asAccountSubscriptionData(subscription);

      assertEquals("ABCDE", actual.getGuid());
      assertEquals("group-1", actual.getName());
      assertEquals("account-1", actual.getAccountName());
      assertEquals(true, actual.isActive());

      assertEquals(true, actual.equals(subscription));
   }

   @Test
   public void testAsSubscriptionGroupData() {
      SubscriptionGroup group = Mockito.mock(SubscriptionGroup.class);
      when(group.getGuid()).thenReturn("ABCDE");
      when(group.getName()).thenReturn("group-1");
      when(group.getGroupId()).thenReturn(new SubscriptionGroupId(98765L));

      SubscriptionGroupData actual = AccountDataUtil.asSubscriptionGroupData(group);

      assertEquals("ABCDE", actual.getGuid());
      assertEquals("group-1", actual.getName());
      assertEquals((Long) 98765L, actual.getSubscriptionGroupId().getId());

      assertEquals(true, actual.matches(group));
      assertEquals(true, actual.equals(group));
   }

   private static Date newRandomDate() {
      return new Date(Math.abs(System.currentTimeMillis() - RandomUtils.nextLong()));
   }

   private static AccountSession mockAccess(long id, String token, Date created, Date accessed, String accessFrom, String accessDetails) {
      AccountSession access = Mockito.mock(AccountSession.class);
      when(access.getAccountId()).thenReturn(id);
      when(access.getSessionToken()).thenReturn(token);
      when(access.getCreatedOn()).thenReturn(created);
      when(access.getLastAccessedOn()).thenReturn(accessed);
      when(access.getAccessedFrom()).thenReturn(accessFrom);
      when(access.getAccessDetails()).thenReturn(accessDetails);
      return access;
   }

   private static void assertAccess(AccountSessionDetailsData actual, Long id, Date created, Date accessed, String accessFrom, String accessDetails) {
      assertEquals(accessDetails, actual.getAccessDetails());
      assertEquals(accessFrom, actual.getAccessedFrom());
      assertEquals(id, actual.getAccountId());
      assertEquals(created, actual.getCreatedOn());
      assertEquals(accessed, actual.getLastAccessedOn());
   }

}
