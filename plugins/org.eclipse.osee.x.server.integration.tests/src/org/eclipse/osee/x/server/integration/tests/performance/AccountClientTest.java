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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.x.server.integration.tests.util.IntegrationUtil;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

/**
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountClientTest {

   @Rule
   public MethodRule performanceRule = IntegrationUtil.createPerformanceRule();

   @Rule
   public TestName testName = new TestName();

   private final AtomicBoolean isFirst = new AtomicBoolean(true);

   private AccountClient client;
   private AccountInfoData newAccount;
   private String username;
   private String name;
   private String email;
   private boolean active;
   private long accountId;
   private String guid;
   private Map<String, String> prefs;

   @Before
   public void setUp() {
      client = IntegrationUtil.createAccountClient();

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
         guid = newAccount.getGuid();

         assertTrue(newAccount.getAccountId() > 0L);
         assertNotNull(newAccount.getGuid());

         assertEquals(email, newAccount.getEmail());
         assertEquals(name, newAccount.getName());
         assertEquals(username, newAccount.getUserName());
         assertEquals(active, newAccount.isActive());
      }
   }

   @Test
   public void test_A_LogInOut() {
      AccountSessionData session1 = client.login("none", email, "dummy");

      assertEquals(accountId, session1.getAccountId());
      assertNotNull(session1.getToken());

      AccountSessionDetailsData access = client.getAccountSessionDataByUniqueField(email).getExactlyOne();

      assertEquals(accountId, access.getAccountId());
      assertNotNull(access.getAccessDetails());
      assertNotNull(access.getAccessedFrom());
      assertNotNull(access.getCreatedOn());
      assertNotNull(access.getLastAccessedOn());

      AccountSessionData session2 = client.login("none", email, "dummy");
      assertEquals(accountId, session2.getAccountId());
      assertNotNull(session2.getToken());

      assertEquals(false, session1.getToken().equals(session2.getToken()));

      ResultSet<AccountSessionDetailsData> result = client.getAccountSessionDataByUniqueField(email);
      assertEquals(2, result.size());
      Iterator<AccountSessionDetailsData> iterator = result.iterator();
      AccountSessionDetailsData access1 = iterator.next();
      AccountSessionDetailsData access2 = iterator.next();
      assertEquals(accountId, access1.getAccountId());
      assertEquals(accountId, access2.getAccountId());

      assertEquals(true, client.logout(session1));
      assertEquals(true, client.logout(session2));

      ResultSet<AccountSessionDetailsData> result2 = client.getAccountSessionDataByUniqueField(email);
      assertEquals(true, result2.isEmpty());
   }

   @Test
   public void test_B_GetAccountDetails() {
      AccountDetailsData actual = client.getAccountDetailsByUniqueField(email);

      assertEquals(accountId, actual.getAccountId());
      assertEquals(guid, actual.getGuid());
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
      AccountPreferencesData actual = client.getAccountPreferencesByUniqueField(email);

      assertEquals(accountId, actual.getId());
      Map<String, String> actualMap = actual.getMap();
      assertEquals(3, actualMap.size());
      assertEquals("1", actualMap.get("a"));
      assertEquals("2", actualMap.get("b"));
      assertEquals("3", actualMap.get("c"));
   }

   @Test
   public void test_E_Active() {
      boolean actual = client.isAccountActive(email);
      assertEquals(active, actual);

      boolean modified = client.setAccountActive(email, active);
      assertEquals(false, modified);

      modified = client.setAccountActive(email, !active);
      assertEquals(true, modified);

      actual = client.isAccountActive(email);
      assertEquals(!active, actual);

      modified = client.setAccountActive(email, active);
      assertEquals(true, modified);

      actual = client.isAccountActive(email);
      assertEquals(active, actual);
   }

   @Test
   public void test_F_SetAccountPreferences() {
      Map<String, String> newPrefs = new HashMap<>();
      newPrefs.put("r", "7");
      newPrefs.put("s", "8");
      newPrefs.put("t", "9");
      newPrefs.put("u", "10");

      boolean modified = client.setAccountPreferences(email, newPrefs);
      assertEquals(true, modified);

      modified = client.setAccountPreferences(email, newPrefs);
      assertEquals(false, modified);

      AccountPreferencesData actual = client.getAccountPreferencesByUniqueField(email);

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

      boolean modified = client.deleteAccount(email);
      assertEquals(true, modified);

      modified = client.deleteAccount(email);
      assertEquals(false, modified);

      int afterDelete = client.getAllAccounts().size();
      assertEquals(beforeDelete - 1, afterDelete);
   }
}
