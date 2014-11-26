/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.orcs.account.admin.internal.OrcsAccountStorage;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.mockito.Mock;

/**
 * Test Case for {@link OrcsAccountStorage}
 * 
 * @author Roberto E. Escobar
 */
public class OrcsAccountStorageTest {

   @Rule
   public TestRule osgi = OrcsIntegrationRule.integrationRule(this);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private AccountStorage storage;

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

      prefs = new HashMap<String, String>();
      prefs.put("a", "1");
      prefs.put("b", "2");
      prefs.put("c", "true");

      when(request.getDisplayName()).thenReturn(name);
      when(request.getEmail()).thenReturn(email);
      when(request.getUserName()).thenReturn(username);
      when(request.getPreferences()).thenReturn(prefs);
      when(request.isActive()).thenReturn(active);

      newAccount = storage.createAccount(request);
   }

   @Test
   public void testGetAllAccounts() {
      ResultSet<Account> result = storage.getAllAccounts();
      assertEquals(10, result.size());
   }

   @Test
   public void testGetById() {
      ResultSet<Account> result = storage.getAccountByUuid(newAccount.getGuid());
      Account account1 = result.getExactlyOne();
      assertAccount(account1, newAccount.getGuid(), name, email, username, active, prefs);
   }

   @Test
   public void testGetByUuiId() {
      ResultSet<Account> result = storage.getAccountByUuid(newAccount.getGuid());
      Account account1 = result.getExactlyOne();
      assertAccount(account1, newAccount.getGuid(), name, email, username, active, prefs);

      ResultSet<Account> result2 = storage.getAccountByLocalId(account1.getId());
      Account account2 = result2.getExactlyOne();
      assertEquals(account1, account2);
      assertAccount(account2, newAccount.getGuid(), name, email, username, active, prefs);
   }

   @Test
   public void testGetByName() {
      ResultSet<Account> result = storage.getAccountByName(name);
      Account account = result.getExactlyOne();
      assertAccount(account, newAccount.getGuid(), name, email, username, active, prefs);
   }

   @Test
   public void testGetByEmail() {
      ResultSet<Account> result = storage.getAccountByEmail(email);
      Account account = result.getExactlyOne();
      assertAccount(account, newAccount.getGuid(), name, email, username, active, prefs);
   }

   @Test
   public void testGetByUserName() {
      ResultSet<Account> result = storage.getAccountByUserName(username);
      Account account = result.getExactlyOne();
      assertAccount(account, newAccount.getGuid(), name, email, username, active, prefs);
   }

   @Test
   public void testGetAccountPrefsByUuid() {
      AccountPreferences actual = storage.getAccountPreferencesByUuid(newAccount.getGuid()).getExactlyOne();
      assertPrefs(actual, newAccount.getGuid(), prefs);

      AccountPreferences actual2 = storage.getAccountPreferencesById(actual.getId()).getExactlyOne();
      assertPrefs(actual2, newAccount.getGuid(), prefs);
   }

   @Test
   public void testGetAccountPrefsByLocalId() {
      Account account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();

      AccountPreferences actual = storage.getAccountPreferencesById(account.getId()).getExactlyOne();
      assertPrefs(actual, account.getGuid(), account.getId(), prefs);
   }

   @Test
   public void testSetActive() {
      Account account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();
      assertAccount(account, newAccount.getGuid(), name, email, username, active, prefs);

      storage.setActive(newAccount, false);

      account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();
      assertFalse(account.isActive());

      storage.setActive(newAccount, true);

      account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();
      assertTrue(account.isActive());
   }

   @Test
   public void testDeleteAccount() {
      ResultSet<Account> result = storage.getAccountByUuid(newAccount.getGuid());
      assertEquals(1, result.size());

      storage.deleteAccount(newAccount);

      result = storage.getAccountByUuid(newAccount.getGuid());
      assertTrue(result.isEmpty());
   }

   @Test
   public void testSetAccountPreferences() {
      Map<String, String> expected = new HashMap<String, String>();
      expected.put("a", "x");
      expected.put("b", "y");
      expected.put("c", "z");
      expected.put("d", "false");

      storage.setAccountPreferences(newAccount, expected);

      Account account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();
      AccountPreferences actual = account.getPreferences();
      assertPrefs(actual, newAccount.getGuid(), account.getId(), expected);
   }

   @Test
   public void testAccountAccess() {
      String token = "myAccess";
      String address = "myAddress";
      String details = "myDetails";

      Account account = storage.getAccountByUuid(newAccount.getGuid()).getExactlyOne();

      AccountSession actual = storage.createAccountSession(token, account, address, details);
      assertEquals(details, actual.getAccessDetails());
      assertEquals(address, actual.getAccessedFrom());
      assertEquals(token, actual.getSessionToken());
      assertEquals(account.getId(), actual.getAccountId());
      assertNotNull(actual.getCreatedOn());
      assertNotNull(actual.getLastAccessedOn());

      ResultSet<AccountSession> result = storage.getAccountSessionBySessionToken(token);
      AccountSession actualAccess = result.getExactlyOne();
      assertEquals(actual, actualAccess);

      storage.deleteAccountSessionBySessionToken(token);
      assertEquals(true, storage.getAccountSessionBySessionToken(token).isEmpty());
   }

   private static void assertPrefs(AccountPreferences expected, String uuid, long accountId, Map<String, String> prefs) {
      assertEquals(accountId, expected.getId());
      assertPrefs(expected, uuid, prefs);
   }

   private static void assertPrefs(AccountPreferences expected, String uuid, Map<String, String> prefs) {
      assertTrue(expected.getId() > 0L);

      assertEquals(uuid, expected.getGuid());

      Map<String, String> actual = expected.asMap();
      if (prefs != null) {
         assertNotNull(actual);
         assertEquals(prefs.size(), actual.size());
         assertPrefs(prefs, actual);
      } else {
         Assert.assertNull(actual);
      }
   }

   private static void assertPrefs(Map<String, String> expected, Map<String, String> actual) {
      assertEquals(false, Compare.isDifferent(expected, actual));
   }

   private static void assertAccount(Account account, String uuid, String name, String email, String username, boolean isActive, Map<String, String> prefs) {
      assertTrue(account.getId() > 0L);
      assertEquals(uuid, account.getGuid());

      assertEquals(name, account.getName());
      assertEquals(email, account.getEmail());
      assertEquals(username, account.getUserName());
      assertEquals(isActive, account.isActive());

      assertPrefs(account.getPreferences(), uuid, prefs);
   }
}
