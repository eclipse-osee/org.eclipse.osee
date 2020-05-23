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

package org.eclipse.osee.account.admin.internal;

import static org.eclipse.osee.account.admin.AccountConstants.SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.Subscription;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.ds.SubscriptionStorage;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.account.rest.model.SubscriptionGroupId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link SubscriptionAdminImpl}
 *
 * @author Roberto E. Escobar
 */
public class SubscriptionAdminImplTest {

   private static final String GROUP_NAME = "group-name";
   private static final String ENCODED_UUID = "D1jfajgjoiasdajv32";
   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(21231);
   private static final SubscriptionGroupId GROUP_ID = new SubscriptionGroupId(7885741L);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private SubscriptionStorage storage;
   @Mock private AccountAdmin accountManager;

   @Mock private SubscriptionGroup group;
   @Mock private Account account;
   @Mock private Subscription subscription;
   // @formatter:on

   private SubscriptionAdminImpl manager;

   @Before
   public void testSetup() {
      initMocks(this);

      manager = new SubscriptionAdminImpl();
      manager.setLogger(logger);
      manager.setSubscriptionStorage(storage);
      manager.start(Collections.<String, Object> emptyMap());

      when(subscription.getAccountId()).thenReturn(ACCOUNT_ID);
      when(subscription.getGroupId()).thenReturn(GROUP_ID);
   }

   @Test
   public void testGetAllSubscriptionGroups() {
      manager.getSubscriptionGroups();
      verify(storage).getSubscriptionGroups();
   }

   @Test
   public void testGetSubscription() {
      manager.getSubscriptionGroupById(GROUP_ID);

      verify(storage).getSubscriptionGroupById(GROUP_ID);
   }

   @Test
   public void testGetSubscriptionById() {
      manager.getSubscriptionGroupById(GROUP_ID);

      verify(storage).getSubscriptionGroupById(GROUP_ID);
   }

   @Test
   public void testCreateSubscriptionGroup() {
      manager.createSubscriptionGroup(GROUP_NAME);

      verify(storage).createSubscriptionGroup(GROUP_NAME);
   }

   @Test
   public void testDeleteSubscription() {
      manager.deleteSubscriptionById(GROUP_ID);

      verify(storage).deleteSubscriptionGroup(GROUP_ID);
   }

   @Test
   public void testGetMembersById() {
      manager.getSubscriptionMembersOfSubscriptionById(GROUP_ID);

      verify(storage).getMembersOfSubscriptionGroupById(GROUP_ID);
   }

   @Test
   public void testSetSubscriptionActiveModified() {
      when(storage.getSubscriptionByEncodedId(ENCODED_UUID)).thenReturn(subscription);
      when(subscription.isActive()).thenReturn(true);

      boolean modified = manager.setSubscriptionActive(subscription, false);
      assertTrue(modified);

      verify(storage).updateSubscription(subscription, false);
   }

   @Test
   public void testSetSubscriptionActiveNotModified() {
      when(storage.getSubscriptionByEncodedId(ENCODED_UUID)).thenReturn(subscription);
      when(subscription.isActive()).thenReturn(true);

      boolean modified = manager.setSubscriptionActive(subscription, true);
      assertFalse(modified);

      verify(storage, times(0)).updateSubscription(subscription, true);
   }

   @Test
   public void testUpdateConfig() {
      String namePattern = "group-\\d+";

      Validator validator = manager.getValidator();
      assertEquals(true, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-12313"));

      Map<String, Object> props = new HashMap<>();
      props.put(SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN, namePattern);

      manager.update(props);

      assertEquals(false, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-ABCsd"));

      assertEquals(true, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-45"));
   }

}
