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
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.account.admin.internal.OrcsSubscriptionStorage;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * Test Case for {@link OrcsSubscriptionStorage}
 *
 * @author Roberto E. Escobar
 */
public class OrcsSubscriptionStorageTest {

   private static final String SUBSCRIPTION_GROUP_NAME = "Subscription-Group1";

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

   private String name;
   private String email;
   private String username;
   private boolean active;
   private Map<String, String> prefs;

   private ArtifactId newAccountId;

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

      CreateAccountRequest request =
         new CreateAccountRequestBuilder.CreateAccountRequestImpl(active, username, email, name, prefs);
      newAccountId = accountStorage.createAccount(request);
   }

   @Test
   public void testSubscriptionAPI() {
      Account account = accountStorage.getAccountById(newAccountId).getExactlyOne();

      ResultSet<Subscription> results = storage.getSubscriptionsByAccountId(newAccountId);
      assertEquals(true, results.isEmpty());

      assertEquals(false, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP_NAME));

      SubscriptionGroupId groupId = storage.createSubscriptionGroup(SUBSCRIPTION_GROUP_NAME);
      assertEquals(true, groupId.getId() > 0);

      assertEquals(true, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP_NAME));

      SubscriptionGroup group1Id = storage.getSubscriptionGroups().getExactlyOne();
      assertEquals(groupId, group1Id);

      ResultSet<Account> members = storage.getMembersOfSubscriptionGroupById(groupId);
      assertEquals(true, members.isEmpty());

      ArtifactId artId = ArtifactId.valueOf(account.getId());
      Subscription subscription = storage.getSubscriptionsByAccountId(artId).getExactlyOne();
      assertEquals(groupId, subscription.getGroupId());
      assertEquals(account.getId(), subscription.getAccountId().getUuid());
      assertEquals(account.getName(), subscription.getAccountName());

      assertEquals(false, subscription.isActive());

      // Activate Subscription
      storage.updateSubscription(subscription, true);

      subscription = storage.getSubscriptionsByAccountId(artId).getExactlyOne();
      assertEquals(true, subscription.isActive());

      members = storage.getMembersOfSubscriptionGroupById(groupId);
      assertEquals(1, members.size());
      Account member = members.getExactlyOne();
      assertEquals(account, member);

      // De-Activate Subscription
      storage.updateSubscription(subscription, false);

      subscription = storage.getSubscriptionsByAccountId(artId).getExactlyOne();
      assertEquals(false, subscription.isActive());

      members = storage.getMembersOfSubscriptionGroupById(groupId);
      assertEquals(true, members.isEmpty());

      storage.deleteSubscriptionGroup(groupId);
      assertEquals(false, storage.subscriptionGroupNameExists(SUBSCRIPTION_GROUP_NAME));
   }

}
