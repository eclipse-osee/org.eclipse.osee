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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.SubscriptionAdmin;
import org.eclipse.osee.account.admin.SubscriptionGroup;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link SubscriptionResolver}
 *
 * @author Roberto E. Escobar
 */
public class SubscriptionResolverTest {

   private static final String TEST_VALUE = "atest";
   private static final String TEST_LOCAID_VALUE = "12334";
   private static final long LOCAID_VALUE = 12334L;

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Validator validator;
   @Mock private SubscriptionAdmin manager;

   @Mock private SubscriptionGroup group;
   @Mock private ResultSet<SubscriptionGroup> groups;

   @Mock private Account account;
   @Mock private ResultSet<Account> members;
   // @formatter:on

   private SubscriptionResolver resolver;

   @Before
   public void testSetup() {
      initMocks(this);

      resolver = new SubscriptionResolver(validator, manager);

      String uuid1 = GUID.create();
      when(group.getGuid()).thenReturn(uuid1);

      String uuid2 = GUID.create();
      when(account.getGuid()).thenReturn(uuid2);

      when(groups.getExactlyOne()).thenReturn(group);
   }

   @Test
   public void testResolveSubscriptionGroupWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription group unique field value cannot be null");
      resolver.resolveSubscriptionGroup(null);
   }

   @Test
   public void testResolveSubscriptionGroupWithEmpty() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription group unique field value cannot be empty");
      resolver.resolveSubscriptionGroup("");
   }

   @Test
   public void testResolveSubscriptionGroupAsUnknown() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.UNKNOWN);

      ResultSet<SubscriptionGroup> actual = resolver.resolveSubscriptionGroup(TEST_VALUE);
      assertEquals(ResultSets.emptyResultSet(), actual);

      verify(validator).guessFormatType(TEST_VALUE);

      verify(manager, times(0)).getSubscriptionGroupByLocalId(anyLong());
      verify(manager, times(0)).getSubscriptionGroupByGuid(anyString());
      verify(manager, times(0)).getSubscriptionGroupByName(anyString());
   }

   @Test
   public void testResolveSubscriptionGroupAsLocalId() {
      when(validator.guessFormatType(TEST_LOCAID_VALUE)).thenReturn(AccountField.LOCAL_ID);
      when(manager.getSubscriptionGroupByLocalId(LOCAID_VALUE)).thenReturn(groups);

      ResultSet<SubscriptionGroup> actual = resolver.resolveSubscriptionGroup(TEST_LOCAID_VALUE);
      assertEquals(groups, actual);

      verify(validator).guessFormatType(TEST_LOCAID_VALUE);
      verify(manager).getSubscriptionGroupByLocalId(LOCAID_VALUE);
   }

   @Test
   public void testResolveSubscriptionGroupAsUuid() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.GUID);
      when(manager.getSubscriptionGroupByGuid(TEST_VALUE)).thenReturn(groups);

      ResultSet<SubscriptionGroup> actual = resolver.resolveSubscriptionGroup(TEST_VALUE);
      assertEquals(groups, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(manager).getSubscriptionGroupByGuid(TEST_VALUE);
   }

   @Test
   public void testResolveSubscriptionGroupAsName() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.SUBSCRIPTION_GROUP_NAME);
      when(manager.getSubscriptionGroupByName(TEST_VALUE)).thenReturn(groups);

      ResultSet<SubscriptionGroup> actual = resolver.resolveSubscriptionGroup(TEST_VALUE);
      assertEquals(groups, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(manager).getSubscriptionGroupByName(TEST_VALUE);
   }

   ////////////////////////////////////////
   @Test
   public void testResolveSubscriptionGroupMembersWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription group unique field value cannot be null");
      resolver.resolveSubscriptionGroupMembersByGroupUniqueField(null);
   }

   @Test
   public void testResolveSubscriptionGroupMembersWithEmpty() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("subscription group unique field value cannot be empty");
      resolver.resolveSubscriptionGroupMembersByGroupUniqueField("");
   }

   @Test
   public void testResolveMembersAsUnknown() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.UNKNOWN);

      ResultSet<Account> actual = resolver.resolveSubscriptionGroupMembersByGroupUniqueField(TEST_VALUE);
      assertEquals(ResultSets.emptyResultSet(), actual);

      verify(validator).guessFormatType(TEST_VALUE);

      verify(manager, times(0)).getSubscriptionGroupMembersByLocalId(anyLong());
      verify(manager, times(0)).getSubscriptionGroupMembersByGuid(anyString());
      verify(manager, times(0)).getSubscriptionGroupMembersByName(anyString());
   }

   @Test
   public void testResolveMembersAsLocalId() {
      when(validator.guessFormatType(TEST_LOCAID_VALUE)).thenReturn(AccountField.LOCAL_ID);
      when(manager.getSubscriptionGroupMembersByLocalId(LOCAID_VALUE)).thenReturn(members);

      ResultSet<Account> actual = resolver.resolveSubscriptionGroupMembersByGroupUniqueField(TEST_LOCAID_VALUE);
      assertEquals(members, actual);

      verify(validator).guessFormatType(TEST_LOCAID_VALUE);
      verify(manager).getSubscriptionGroupMembersByLocalId(LOCAID_VALUE);
   }

   @Test
   public void testResolveMembersAsGuid() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.GUID);
      when(manager.getSubscriptionGroupMembersByGuid(TEST_VALUE)).thenReturn(members);

      ResultSet<Account> actual = resolver.resolveSubscriptionGroupMembersByGroupUniqueField(TEST_VALUE);
      assertEquals(members, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(manager).getSubscriptionGroupMembersByGuid(TEST_VALUE);
   }

   @Test
   public void testResolveMembersAsName() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.SUBSCRIPTION_GROUP_NAME);
      when(manager.getSubscriptionGroupMembersByName(TEST_VALUE)).thenReturn(members);

      ResultSet<Account> actual = resolver.resolveSubscriptionGroupMembersByGroupUniqueField(TEST_VALUE);
      assertEquals(members, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(manager).getSubscriptionGroupMembersByName(TEST_VALUE);
   }
}
