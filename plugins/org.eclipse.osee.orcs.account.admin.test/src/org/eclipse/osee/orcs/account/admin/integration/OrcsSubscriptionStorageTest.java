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
package org.eclipse.osee.orcs.account.admin.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.account.admin.internal.OrcsSubscriptionStorage;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.Mock;

/**
 * Test Case for {@link OrcsSubscriptionStorage}
 * 
 * @author Roberto E. Escobar
 */
public class OrcsSubscriptionStorageTest {

   private static final String SUBSCRIPTION_GROUP = "Subscription-Group1";

   @Rule
   public TestRule osgi = OrcsIntegrationRule.integrationRule(this);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private SubscriptionStorage storage;

   @OsgiService
   private AccountStorage accountStorage;

   // @formatter:off
   @Mock private CreateAccountRequest request;
   @Mock private AccountPreferences preferences;
   // @formatter:on

   private String name;
   private String email;
   private String username;
   private boolean active;
   private Map<String, String> prefs;

   private Identifiable<String> newAccount;

   @Before
   public void testSetup() {
      initMocks(this);

      String methodName = testName.getMethodName();

      name = String.format("displayName-%s", methodName);
      email = String.format("%s@email.com", methodName);
      username = String.format("userName-%s", methodName);
      active = true;

      prefs = new HashMap<>();
      prefs.put("a", "1");
      prefs.put("b", "2");
      prefs.put("c", "true");

      when(request.getDisplayName()).thenReturn(name);
      when(request.getEmail()).thenReturn(email);
      when(request.getUserName()).thenReturn(username);
      when(request.getPreferences()).thenReturn(prefs);
      when(request.isActive()).thenReturn(active);

      newAccount = accountStorage.createAccount(request);
   }

   @Test
   public void testSubscriptionAPI() {
      Account account = accountStorage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();

      long accountId = account.getId();
      ResultSet<Subscription> results = storage.getSubscriptionsByAccountLocalId(accountId);
      assertEquals(true, results.isEmpty());

      assertEquals(false, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP));

      SubscriptionGroup group = storage.createSubscriptionGroup(SUBSCRIPTION_GROUP);
      assertNotNull(group);

      assertEquals(true, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP));

      SubscriptionGroup group1 = storage.getSubscriptionGroups().getExactlyOne();
      assertEquals(group, group1);

      ResultSet<Account> members = storage.getSubscriptionGroupMembersByLocalId(group.getId());
      assertEquals(true, members.isEmpty());

      Subscription subscription = storage.getSubscriptionsByAccountLocalId(account.getId()).getExactlyOne();
      assertEquals(group.getId(), subscription.getGroupId());
      assertEquals(group.getName(), subscription.getName());
      assertEquals(account.getId(), subscription.getAccountId());
      assertEquals(account.getName(), subscription.getAccountName());

      assertEquals(false, subscription.isActive());

      // Activate Subscription
      storage.updateSubscription(subscription.getAccountId(), subscription.getGroupId(), true);

      String subscriptionUuid = subscription.getGuid();
      subscription = storage.getSubscription(subscriptionUuid);
      assertEquals(true, subscription.isActive());

      members = storage.getSubscriptionGroupMembersByLocalId(group.getId());
      assertEquals(1, members.size());
      Account member = members.getExactlyOne();
      assertEquals(account, member);

      // De-Activate Subscription
      storage.updateSubscription(subscription.getAccountId(), subscription.getGroupId(), false);

      subscription = storage.getSubscription(subscriptionUuid);
      assertEquals(false, subscription.isActive());

      members = storage.getSubscriptionGroupMembersByLocalId(group.getId());
      assertEquals(true, members.isEmpty());

      storage.deleteSubscriptionGroup(group);
      assertEquals(false, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP));
   }

}
