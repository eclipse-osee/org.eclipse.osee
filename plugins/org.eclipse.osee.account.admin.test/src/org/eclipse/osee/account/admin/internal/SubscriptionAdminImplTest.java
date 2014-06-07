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
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Test Case for {@link SubscriptionAdminImpl}
 * 
 * @author Roberto E. Escobar
 */
public class SubscriptionAdminImplTest {

   private static final long ID = 123121412L;
   private static final String UUID = GUID.create();
   private static final String GROUP_NAME = "group-name";
   private static final String EMAIL = "atest@email.com";
   private static final long ACCOUNT_ID = 21231L;
   private static final long GROUP_ID = 7885741L;

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
      manager.setAccountAdmin(accountManager);
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
   public void testGetSubscriptionWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription uuid cannot be null");
      manager.getSubscription(null);
   }

   @Test
   public void testGetSubscription() {
      manager.getSubscription(UUID);

      verify(storage).getSubscription(UUID);
   }

   @Test
   public void testGetSubscriptionsByAccountUniqueField() {
      when(account.getId()).thenReturn(ID);
      ResultSet<Account> resultSet = ResultSets.singleton(account);
      ResultSet<Subscription> subcriptionResultSet = ResultSets.singleton(subscription);

      when(accountManager.getAccountByUniqueField(EMAIL)).thenReturn(resultSet);
      when(storage.getSubscriptionsByAccountLocalId(ID)).thenReturn(subcriptionResultSet);

      ResultSet<Subscription> result = manager.getSubscriptionsByAccountUniqueField(EMAIL);
      assertEquals(subscription, result.getExactlyOne());

      verify(account).getId();
      verify(storage).getSubscriptionsByAccountLocalId(ID);
   }

   @Test
   public void testGetSubscriptionGroupByUuidWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("group uuid cannot be null");
      manager.getSubscriptionGroupByUuid(null);
   }

   @Test
   public void testGetSubscriptionGroupByUuiId() {
      manager.getSubscriptionGroupByUuid(UUID);

      verify(storage).getSubscriptionGroupByUuid(UUID);
   }

   @Test
   public void testGetSubscriptionGroupById() {
      manager.getSubscriptionGroupByLocalId(ID);

      verify(storage).getSubscriptionGroupByLocalId(ID);
   }

   @Test
   public void testGetSubscriptionGroupByNameWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("group name cannot be null");
      manager.getSubscriptionGroupByName(null);
   }

   @Test
   public void testGetSubscriptionGroupByName() {
      manager.getSubscriptionGroupByName(GROUP_NAME);

      verify(storage).getSubscriptionGroupByName(GROUP_NAME);
   }

   @Test
   public void testGetSubscriptionGroupByUniqueField() {
      ResultSet<SubscriptionGroup> resultSet = ResultSets.singleton(group);

      when(storage.getSubscriptionGroupByName(GROUP_NAME)).thenReturn(resultSet);

      ResultSet<SubscriptionGroup> result = manager.getSubscriptionGroupByUniqueField(GROUP_NAME);
      assertEquals(group, result.getExactlyOne());

      verify(storage).getSubscriptionGroupByName(GROUP_NAME);
   }

   @Test
   public void testCreateSubscriptionGroup() {
      manager.createSubscriptionGroup(GROUP_NAME);

      verify(storage).createSubscriptionGroup(GROUP_NAME);
   }

   @Test
   public void testDeleteSubscriptionGroupWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription group unique field value cannot be null");
      manager.deleteSubscriptionGroupByUniqueField(null);
   }

   @Test
   public void testDeleteSubscriptionGroupIdModified() {
      ResultSet<SubscriptionGroup> resultSet = ResultSets.singleton(group);

      when(storage.getSubscriptionGroupByName(GROUP_NAME)).thenReturn(resultSet);

      boolean modified = manager.deleteSubscriptionGroupByUniqueField(GROUP_NAME);
      assertTrue(modified);

      verify(storage).getSubscriptionGroupByName(GROUP_NAME);
      verify(storage).deleteSubscriptionGroup(group);
   }

   @Test
   public void testDeleteSubscriptionGroupIdNotModified() {
      @SuppressWarnings("unchecked")
      ResultSet<SubscriptionGroup> resultSet = Mockito.mock(ResultSet.class);

      when(storage.getSubscriptionGroupByName(GROUP_NAME)).thenReturn(resultSet);
      when(resultSet.getOneOrNull()).thenReturn(null);

      boolean modified = manager.deleteSubscriptionGroupByUniqueField(GROUP_NAME);
      assertFalse(modified);

      verify(storage, times(0)).deleteSubscriptionGroup(group);
   }

   @Test
   public void testGetMembersByUuidWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("uuid cannot be null");
      manager.getSubscriptionGroupMembersByUuid(null);
   }

   @Test
   public void testGetMembersByUuiId() {
      manager.getSubscriptionGroupMembersByUuid(UUID);

      verify(storage).getSubscriptionGroupMembersByUuid(UUID);
   }

   @Test
   public void testGetMembersById() {
      manager.getSubscriptionGroupMembersByLocalId(ID);

      verify(storage).getSubscriptionGroupMembersByLocalId(ID);
   }

   @Test
   public void testGetMembersByNameWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("group name cannot be null");
      manager.getSubscriptionGroupMembersByName(null);
   }

   @Test
   public void testGetMembersByName() {
      manager.getSubscriptionGroupMembersByName(GROUP_NAME);

      verify(storage).getSubscriptionGroupMembersByName(GROUP_NAME);
   }

   @Test
   public void testGetMembersByUniqueField() {
      ResultSet<Account> resultSet = ResultSets.singleton(account);

      when(storage.getSubscriptionGroupMembersByName(GROUP_NAME)).thenReturn(resultSet);

      ResultSet<Account> result = manager.getSubscriptionGroupMembersByUniqueField(GROUP_NAME);
      assertEquals(account, result.getExactlyOne());

      verify(storage).getSubscriptionGroupMembersByName(GROUP_NAME);
   }

   @Test
   public void testSetSubscriptionActiveModified() {
      when(storage.getSubscription(UUID)).thenReturn(subscription);
      when(subscription.isActive()).thenReturn(true);

      boolean modified = manager.setSubscriptionActive(UUID, false);
      assertTrue(modified);

      verify(storage).getSubscription(UUID);
      verify(storage).updateSubscription(ACCOUNT_ID, GROUP_ID, false);
   }

   @Test
   public void testSetSubscriptionActiveNotModified() {
      when(storage.getSubscription(UUID)).thenReturn(subscription);
      when(subscription.isActive()).thenReturn(true);

      boolean modified = manager.setSubscriptionActive(UUID, true);
      assertFalse(modified);

      verify(storage).getSubscription(UUID);
      verify(storage, times(0)).updateSubscription(ACCOUNT_ID, GROUP_ID, true);
   }

   @Test
   public void testUpdateConfig() {
      String namePattern = "group-\\d+";

      Validator validator = manager.getValidator();
      assertEquals(true, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-12313"));

      Map<String, Object> props = new HashMap<String, Object>();
      props.put(SUBSCRIPTION_GROUP_NAME_VALIDATION_PATTERN, namePattern);

      manager.update(props);

      assertEquals(false, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-ABCsd"));

      assertEquals(true, validator.isValid(AccountField.SUBSCRIPTION_GROUP_NAME, "group-45"));
   }

}
