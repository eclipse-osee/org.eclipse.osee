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

   private static final String SUBSCRIPTION_UUID = "D_UhOLi6D7q_MbOiUYny75bUWxYdlHI9yCLyosilpDMYxRhasnqYvwCOlNEPgvrk";
   private static final boolean SUBSCRIPTION_IS_ACTIVE = true;

   private static final String ACCOUNT_UUID = "asdksa";
   private static final String ACCOUNT_NAME = "account-1";
   private static final long ACCOUNT_ID = 3129303L;
   private static final String ACCOUNT_USERNAME = "sadfaa";
   private static final String ACCOUNT_EMAIL = "hello@hello.com";
   private static final boolean ACCOUNT_IS_ACTIVE = true;

   private static final String GROUP_UUID = "sadjha322";
   private static final String GROUP_NAME = "group-1";
   private static final long GROUP_ID = 37219891L;

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

      when(subscription.getGuid()).thenReturn(GROUP_UUID);
      when(subscription.getName()).thenReturn(GROUP_NAME);
      when(subscription.getAccountName()).thenReturn(ACCOUNT_NAME);
      when(subscription.isActive()).thenReturn(SUBSCRIPTION_IS_ACTIVE);

      when(group.getGuid()).thenReturn(GROUP_UUID);
      when(group.getName()).thenReturn(GROUP_NAME);
      when(group.getId()).thenReturn(GROUP_ID);

      when(account.getGuid()).thenReturn(ACCOUNT_UUID);
      when(account.getName()).thenReturn(ACCOUNT_NAME);
      when(account.getId()).thenReturn(ACCOUNT_ID);
      when(account.getUserName()).thenReturn(ACCOUNT_USERNAME);
      when(account.getEmail()).thenReturn(ACCOUNT_EMAIL);
      when(account.isActive()).thenReturn(ACCOUNT_IS_ACTIVE);
   }

   @Test
   public void testGetSubscriptions() {
      ResultSet<Subscription> results = ResultSets.singleton(subscription);
      when(manager.getSubscriptionsByAccountUniqueField(ACCOUNT_UUID)).thenReturn(results);

      SubscriptionData[] actual = resource.getSubscriptions(ACCOUNT_UUID);

      assertEquals(1, actual.length);
      SubscriptionData data = actual[0];
      checkSubscription(data, GROUP_UUID, GROUP_NAME, ACCOUNT_NAME, SUBSCRIPTION_IS_ACTIVE);
      verify(manager).getSubscriptionsByAccountUniqueField(ACCOUNT_UUID);
   }

   @Test
   public void testGetSubscription() {
      when(manager.getSubscription(SUBSCRIPTION_UUID)).thenReturn(subscription);

      SubscriptionData actual = resource.getSubscription(SUBSCRIPTION_UUID);

      checkSubscription(actual, GROUP_UUID, GROUP_NAME, ACCOUNT_NAME, SUBSCRIPTION_IS_ACTIVE);
      verify(manager).getSubscription(SUBSCRIPTION_UUID);
   }

   @Test
   public void testSetSubscriptionActiveOk() {
      when(manager.setSubscriptionActive(SUBSCRIPTION_UUID, true)).thenReturn(true);

      Response response = resource.setSubscriptionActive(SUBSCRIPTION_UUID);
      assertEquals(Status.OK.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(SUBSCRIPTION_UUID, true);
   }

   @Test
   public void testSetSubscriptionActiveNotModified() {
      when(manager.setSubscriptionActive(SUBSCRIPTION_UUID, true)).thenReturn(false);

      Response response = resource.setSubscriptionActive(SUBSCRIPTION_UUID);
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(SUBSCRIPTION_UUID, true);
   }

   @Test
   public void testSetSubscriptionInactiveOk() {
      when(manager.setSubscriptionActive(SUBSCRIPTION_UUID, false)).thenReturn(true);

      Response response = resource.setSubscriptionInactive(SUBSCRIPTION_UUID);
      assertEquals(Status.OK.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(SUBSCRIPTION_UUID, false);
   }

   @Test
   public void testSetSubscriptionInactiveMotModified() {
      when(manager.setSubscriptionActive(SUBSCRIPTION_UUID, false)).thenReturn(false);

      Response response = resource.setSubscriptionInactive(SUBSCRIPTION_UUID);
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());

      verify(manager).setSubscriptionActive(SUBSCRIPTION_UUID, false);
   }

   @Test
   public void testGetSubscriptionGroups() {
      ResultSet<SubscriptionGroup> results = ResultSets.singleton(group);
      when(manager.getSubscriptionGroups()).thenReturn(results);

      SubscriptionGroupData[] actual = resource.getSubscriptionGroups();

      assertEquals(1, actual.length);
      SubscriptionGroupData data = actual[0];
      checkSubscriptionGroup(data, GROUP_UUID, GROUP_NAME, GROUP_ID);
      verify(manager).getSubscriptionGroups();
   }

   @Test
   public void testGetSubscriptionGroup() {
      ResultSet<SubscriptionGroup> results = ResultSets.singleton(group);
      when(manager.getSubscriptionGroupByUniqueField(GROUP_UUID)).thenReturn(results);

      SubscriptionGroupData actual = resource.getSubscriptionGroup(GROUP_UUID);

      checkSubscriptionGroup(actual, GROUP_UUID, GROUP_NAME, GROUP_ID);
      verify(manager).getSubscriptionGroupByUniqueField(GROUP_UUID);
   }

   @Test
   public void testCreateSubscriptionGroup() {
      when(manager.createSubscriptionGroup(GROUP_NAME)).thenReturn(group);

      SubscriptionGroupData actual = resource.createSubscriptionGroup(GROUP_NAME);

      checkSubscriptionGroup(actual, GROUP_UUID, GROUP_NAME, GROUP_ID);
      verify(manager).createSubscriptionGroup(GROUP_NAME);
   }

   @Test
   public void testDeleteSubscriptionGroup() {
      when(manager.deleteSubscriptionGroupByUniqueField(GROUP_UUID)).thenReturn(true);

      Response actual = resource.deleteSubscriptionGroup(GROUP_UUID);
      assertEquals(Status.OK.getStatusCode(), actual.getStatus());

      verify(manager).deleteSubscriptionGroupByUniqueField(GROUP_UUID);
   }

   @Test
   public void testDeleteSubscriptionGroupNotModified() {
      when(manager.deleteSubscriptionGroupByUniqueField(GROUP_UUID)).thenReturn(false);

      Response actual = resource.deleteSubscriptionGroup(GROUP_UUID);
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), actual.getStatus());

      verify(manager).deleteSubscriptionGroupByUniqueField(GROUP_UUID);
   }

   @Test
   public void testGetSubscriptionGroupMembers() {
      ResultSet<Account> results = ResultSets.singleton(account);
      when(manager.getSubscriptionGroupMembersByUniqueField(GROUP_UUID)).thenReturn(results);

      AccountInfoData[] actual = resource.getSubscriptionGroupMembers(GROUP_UUID);

      assertEquals(1, actual.length);
      AccountInfoData data = actual[0];
      checkAccount(data, ACCOUNT_UUID, ACCOUNT_NAME, ACCOUNT_ID, ACCOUNT_USERNAME, ACCOUNT_EMAIL, ACCOUNT_IS_ACTIVE);
      verify(manager).getSubscriptionGroupMembersByUniqueField(GROUP_UUID);
   }

   private static void checkSubscription(SubscriptionData actual, String guid, String name, String accountName, boolean active) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(accountName, actual.getAccountName());
      assertEquals(active, actual.isActive());
   }

   private static void checkSubscriptionGroup(SubscriptionGroupData actual, String guid, String name, long groupId) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(groupId, actual.getId());
   }

   private static void checkAccount(AccountInfoData actual, String guid, String name, long accountId, String username, String email, boolean active) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(accountId, actual.getAccountId());
      assertEquals(username, actual.getUserName());
      assertEquals(email, actual.getEmail());
      assertEquals(active, actual.isActive());
   }
}
