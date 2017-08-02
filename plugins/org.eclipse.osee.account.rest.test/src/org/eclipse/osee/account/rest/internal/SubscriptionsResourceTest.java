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
package org.eclipse.osee.account.rest.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupData;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link SubscriptionsResource}
 *
 * @author Roberto E. Escobar
 */
public class SubscriptionsResourceTest {

   private static final String SUBSCRIPTION_ENCODED =
      "D_UhOLi6D7q_MbOiUYny75bUWxYdlHI9yCLyosilpDMYxRhasnqYvwCOlNEPgvrk";
   private static final boolean SUBSCRIPTION_IS_ACTIVE = true;

   private static final String ACCOUNT_NAME = "account-1";
   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(3129303);
   private static final String ACCOUNT_USERNAME = "sadfaa";
   private static final String ACCOUNT_EMAIL = "hello@hello.com";
   private static final boolean ACCOUNT_IS_ACTIVE = true;

   private static final String GROUP_UUID = "sadjha322";
   private static final String GROUP_NAME = "group-1";
   private static final SubscriptionGroupId GROUP_ID = new SubscriptionGroupId(37219891L);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private SubscriptionAdmin manager;

   @Mock private Subscription subscription;
   @Mock private SubscriptionGroup group;
   @Mock private Account account;
   //@formatter:on

   private SubscriptionsResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new SubscriptionsResource(manager);

      when(subscription.getGroupId()).thenReturn(GROUP_ID);
      when(subscription.getGuid()).thenReturn(GROUP_UUID);
      when(subscription.getName()).thenReturn(GROUP_NAME);
      when(subscription.getAccountName()).thenReturn(ACCOUNT_NAME);
      when(subscription.isActive()).thenReturn(SUBSCRIPTION_IS_ACTIVE);

      when(group.getGuid()).thenReturn(GROUP_UUID);
      when(group.getName()).thenReturn(GROUP_NAME);
      when(group.getGroupId()).thenReturn(GROUP_ID);

      when(account.getName()).thenReturn(ACCOUNT_NAME);
      when(account.getId()).thenReturn(ACCOUNT_ID.getUuid());
      when(account.getUserName()).thenReturn(ACCOUNT_USERNAME);
      when(account.getEmail()).thenReturn(ACCOUNT_EMAIL);
      when(account.isActive()).thenReturn(ACCOUNT_IS_ACTIVE);
   }

   @Test
   public void testGetSubscriptions() {
      ResultSet<Subscription> results = ResultSets.singleton(subscription);
      when(manager.getSubscriptionsByAccountId(ACCOUNT_ID)).thenReturn(results);

      SubscriptionData[] actual = resource.getSubscriptions(ACCOUNT_ID.getUuid());

      assertEquals(1, actual.length);
      SubscriptionData data = actual[0];
      checkSubscription(data, GROUP_UUID, GROUP_NAME, ACCOUNT_NAME, SUBSCRIPTION_IS_ACTIVE);
      verify(manager).getSubscriptionsByAccountId(ACCOUNT_ID);
   }

   @Test
   public void testGetSubscriptionEncoded() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);

      SubscriptionData actual = resource.getSubscriptionByEncoded(SUBSCRIPTION_ENCODED);

      checkSubscription(actual, GROUP_UUID, GROUP_NAME, ACCOUNT_NAME, SUBSCRIPTION_IS_ACTIVE);
      verify(manager).getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED);
   }

   @Test
   public void testGetSubscription() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);

      SubscriptionData actual = resource.getSubscriptionByEncoded(SUBSCRIPTION_ENCODED);

      checkSubscription(actual, GROUP_UUID, GROUP_NAME, ACCOUNT_NAME, SUBSCRIPTION_IS_ACTIVE);
      verify(manager).getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED);
   }

   @Test
   public void testSetSubscriptionActiveOk() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);
      when(manager.setSubscriptionActive(subscription, true)).thenReturn(true);

      Response response = resource.setSubscriptionActive(SUBSCRIPTION_ENCODED);
      assertEquals(Status.OK.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(subscription, true);
   }

   @Test
   public void testSetSubscriptionActiveNotModified() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);
      when(manager.setSubscriptionActive(subscription, true)).thenReturn(false);

      Response response = resource.setSubscriptionActive(SUBSCRIPTION_ENCODED);
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(subscription, true);
   }

   @Test
   public void testSetSubscriptionInactiveOk() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);
      when(manager.setSubscriptionActive(subscription, false)).thenReturn(true);

      Response response = resource.setSubscriptionInactive(SUBSCRIPTION_ENCODED);
      assertEquals(Status.OK.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(subscription, false);
   }

   @Test
   public void testSetSubscriptionInactiveMotModified() {
      when(manager.getSubscriptionsByEncodedId(SUBSCRIPTION_ENCODED)).thenReturn(subscription);
      when(manager.setSubscriptionActive(subscription, false)).thenReturn(false);

      Response response = resource.setSubscriptionInactive(SUBSCRIPTION_ENCODED);
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(subscription, false);
   }

   @Test
   public void testGetSubscriptionGroups() {
      ResultSet<SubscriptionGroup> results = ResultSets.singleton(group);
      when(manager.getSubscriptionGroups()).thenReturn(results);
      when(manager.getSubscriptionGroupById(GROUP_ID)).thenReturn(group);

      SubscriptionGroupData[] actual = resource.getSubscriptionGroups();

      assertEquals(1, actual.length);
      SubscriptionGroupData data = actual[0];
      checkSubscriptionGroup(data, GROUP_UUID, GROUP_NAME, GROUP_ID);
      verify(manager).getSubscriptionGroups();
   }

   @Test
   public void testCreateSubscriptionGroup() {
      when(manager.createSubscriptionGroup(GROUP_NAME)).thenReturn(GROUP_ID);
      when(manager.getSubscriptionGroupById(GROUP_ID)).thenReturn(group);

      SubscriptionGroupData actual = resource.createSubscriptionGroup(GROUP_NAME);

      checkSubscriptionGroup(actual, GROUP_UUID, GROUP_NAME, GROUP_ID);
      verify(manager).createSubscriptionGroup(GROUP_NAME);
   }

   @Test
   public void testDeleteSubscriptionGroup() {
      when(manager.deleteSubscriptionById(GROUP_ID)).thenReturn(true);

      Response actual = resource.deleteSubscriptionGroup(GROUP_ID.getId());
      assertEquals(Status.OK.getStatusCode(), actual.getStatus());

      verify(manager).deleteSubscriptionById(GROUP_ID);
   }

   @Test
   public void testGetSubscriptionGroupMembers() {
      ResultSet<Account> results = ResultSets.singleton(account);
      when(manager.getSubscriptionMembersOfSubscriptionById(GROUP_ID)).thenReturn(results);

      AccountInfoData[] actual = resource.getSubscriptionGroupMembers(GROUP_ID.getId());

      assertEquals(1, actual.length);
      AccountInfoData data = actual[0];
      checkAccount(data, ACCOUNT_NAME, ACCOUNT_ID, ACCOUNT_USERNAME, ACCOUNT_EMAIL, ACCOUNT_IS_ACTIVE);
      verify(manager).getSubscriptionMembersOfSubscriptionById(GROUP_ID);
   }

   private static void checkSubscription(SubscriptionData actual, String guid, String name, String accountName, boolean active) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(accountName, actual.getAccountName());
      assertEquals(active, actual.isActive());
   }

   private static void checkSubscriptionGroup(SubscriptionGroupData actual, String guid, String name, SubscriptionGroupId groupId) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(groupId, actual.getSubscriptionGroupId());
   }

   private static void checkAccount(AccountInfoData actual, String name, ArtifactId accountId, String username, String email, boolean active) {
      assertEquals(name, actual.getName());
      assertEquals(accountId.getUuid(), actual.getAccountId());
      assertEquals(username, actual.getUserName());
      assertEquals(email, actual.getEmail());
      assertEquals(active, actual.isActive());
   }
}
