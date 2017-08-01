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
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.account.admin.CreateAccountRequest;
import org.eclipse.osee.account.admin.CreateAccountRequestBuilder;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
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

   private String name;
   private String email;
   private String username;
   private boolean active;
   private Map<String, String> prefs;

   private ArtifactId newAccountId;

   @Before
   public void testSetup() {
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
      newAccountId = storage.createAccount(request);
   }

   @Test
   public void testGetAllAccounts() {
      assertEquals(24, storage.getAllAccounts().size());
   }

   @Test
   public void testGetById() {
      Account account1 = storage.getAccountById(newAccountId);
      assertAccount(account1, newAccountId, name, email, username, active, prefs);

      ArtifactId artId = ArtifactId.valueOf(account1.getId());
      Account account2 = storage.getAccountById(artId);
      assertEquals(account1, account2);
      assertAccount(account2, newAccountId, name, email, username, active, prefs);
   }

   @Test
   public void testGetByEmail() {
      ResultSet<Account> result = storage.getAccountByEmail(email);
      Account account = result.getExactlyOne();
      assertAccount(account, newAccountId, name, email, username, active, prefs);
   }

   @Test
   public void testSetActive() {
      Account account = storage.getAccountById(newAccountId);
      assertAccount(account, newAccountId, name, email, username, active, prefs);

      storage.setActive(newAccountId, false);

      account = storage.getAccountById(newAccountId);
      assertFalse(account.isActive());

      storage.setActive(newAccountId, true);

      account = storage.getAccountById(newAccountId);
      assertTrue(account.isActive());
   }

   @Test
   public void testDeleteAccount() {
      thrown.expect(ItemDoesNotExist.class);
      assertNotNull(storage.getAccountById(newAccountId));
      storage.deleteAccount(newAccountId);
      storage.getAccountById(newAccountId);
   }

   @Test
   public void testSetAccountPreferences() {
      Map<String, String> expected = new HashMap<>();
      expected.put("a", "x");
      expected.put("b", "y");
      expected.put("c", "z");
      expected.put("d", "false");

      storage.setAccountPreferences(newAccountId, expected);

      Account account = storage.getAccountById(newAccountId);
      AccountPreferences actual = account.getPreferences();
      assertPrefs(actual, newAccountId, account.getId(), expected);
   }

   @Test
   public void testAccountAccess() {
      String token = "myAccess";
      String address = "myAddress";
      String details = "myDetails";

      Account account = storage.getAccountById(newAccountId);

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

   private static void assertPrefs(AccountPreferences expected, ArtifactId artId, Long accountId, Map<String, String> prefs) {
      assertEquals(accountId, expected.getId());
      assertPrefs(expected, artId, prefs);
   }

   private static void assertPrefs(AccountPreferences expected, ArtifactId artId, Map<String, String> prefs) {
      assertTrue(expected.getId() > 0L);

      assertEquals(artId.getUuid(), expected.getId());

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

   private static void assertAccount(Account account, ArtifactId artId, String name, String email, String username, boolean isActive, Map<String, String> prefs) {
      assertTrue(account.getId() > 0L);
      assertEquals(artId.getUuid(), account.getId());

      assertEquals(name, account.getName());
      assertEquals(email, account.getEmail());
      assertEquals(username, account.getUserName());
      assertEquals(isActive, account.isActive());

      assertPrefs(account.getPreferences(), artId, prefs);
   }
}
