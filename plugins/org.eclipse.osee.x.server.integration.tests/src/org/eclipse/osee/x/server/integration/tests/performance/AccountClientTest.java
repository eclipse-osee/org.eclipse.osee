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
package org.eclipse.osee.x.server.integration.tests.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.x.server.integration.tests.util.IntegrationUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

/**
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountClientTest {

   @Rule
   public TestName testName = new TestName();

   private final AtomicBoolean isFirst = new AtomicBoolean(true);

   private AccountClient client;
   private AccountInfoData newAccount;
   private String username;
   private String name;
   private String email;
   private boolean active;
   private Long accountId;
   private Map<String, String> prefs;

   @After
   public void cleanUp() {
      client = IntegrationUtil.createAccountClient();
      client.deleteAccount(accountId);
   }

   @Before
   public void setUp() {
      client = IntegrationUtil.createAccountClient();
      if (!client.isLocalHost()) {
         throw new OseeStateException("This test should be run with local test server, not %s", client.getBaseUri());
      }

      String methodName = testName.getMethodName();

      if (isFirst.compareAndSet(true, false)) {
         double value = RandomUtils.nextDouble();

         username = String.format("userName_%s_%s", methodName, value);
         name = String.format("name__%s_%s", methodName, value);
         email = String.format("email_%s_%s@hello.com", methodName, value);
         active = true;

         prefs = new HashMap<>();
         prefs.put("a", "1");
         prefs.put("b", "2");
         prefs.put("c", "3");

         AccountInput input = new AccountInput();
         input.setActive(active);
         input.setEmail(email);
         input.setName(name);
         input.setPreferences(prefs);

         newAccount = client.createAccount(username, input);
         accountId = newAccount.getAccountId();

         assertTrue(newAccount.getAccountId() > 0L);

         assertEquals(email, newAccount.getEmail());
         assertEquals(name, newAccount.getName());
         assertEquals(username, newAccount.getUserName());
         assertEquals(active, newAccount.isActive());
      }
   }

   @Test
   public void test_B_GetAccountDetails() {
      AccountDetailsData actual = client.getAccountDetailsById(accountId);

      assertEquals(accountId, actual.getAccountId());
      assertEquals(email, actual.getEmail());
      assertEquals(name, actual.getName());
      assertEquals(username, actual.getUserName());
      assertEquals(active, actual.isActive());
   }

   @Test
   public void test_C_GetAccounts() {
      ResultSet<AccountInfoData> result = client.getAllAccounts();
      assertEquals(false, result.isEmpty());
   }

   @Test
   public void test_D_GetAccountPreferences() {
      AccountPreferencesData actual = client.getAccountPreferencesById(accountId);

      assertEquals(accountId, actual.getId());
      Map<String, String> actualMap = actual.getMap();
      assertEquals(3, actualMap.size());
      assertEquals("1", actualMap.get("a"));
      assertEquals("2", actualMap.get("b"));
      assertEquals("3", actualMap.get("c"));
   }

   @Test
   public void test_E_Active() {
      boolean actual = client.isAccountActive(accountId);
      assertEquals(active, actual);

      boolean modified = client.setAccountActive(accountId, active);
      assertEquals(false, modified);

      modified = client.setAccountActive(accountId, !active);
      assertEquals(true, modified);

      actual = client.isAccountActive(accountId);
      assertEquals(!active, actual);

      modified = client.setAccountActive(accountId, active);
      assertEquals(true, modified);

      actual = client.isAccountActive(accountId);
      assertEquals(active, actual);
   }

   @Test
   public void test_F_SetAccountPreferences() {
      Map<String, String> newPrefs = new HashMap<>();
      newPrefs.put("r", "7");
      newPrefs.put("s", "8");
      newPrefs.put("t", "9");
      newPrefs.put("u", "10");

      boolean modified = client.setAccountPreferences(accountId, newPrefs);
      assertEquals(true, modified);

      modified = client.setAccountPreferences(accountId, newPrefs);
      assertEquals(false, modified);

      AccountPreferencesData actual = client.getAccountPreferencesById(accountId);

      assertEquals(accountId, actual.getId());
      Map<String, String> actualMap = actual.getMap();
      assertEquals(4, actualMap.size());
      assertEquals("7", actualMap.get("r"));
      assertEquals("8", actualMap.get("s"));
      assertEquals("9", actualMap.get("t"));
      assertEquals("10", actualMap.get("u"));
   }

   @Test
   public void test_G_DeleteAccount() {
      int beforeDelete = client.getAllAccounts().size();

      boolean modified = client.deleteAccount(accountId);
      assertEquals(true, modified);

      modified = client.deleteAccount(accountId);
      assertEquals(false, modified);

      int afterDelete = client.getAllAccounts().size();
      assertEquals(beforeDelete - 1, afterDelete);
   }
}
