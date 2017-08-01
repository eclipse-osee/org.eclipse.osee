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
package org.eclipse.osee.account.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSetList;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountResolver}
 *
 * @author Roberto E. Escobar
 */
public class AccountResolverTest {

   private static final String TEST_VALUE = "atest";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Validator validator;
   @Mock private AccountAdmin accountAdmin;
   @Mock private Account account;
   @Mock private AccountPreferences prefs;
   // @formatter:on

   private ResultSet<Account> accountResult;
   private AccountResolver resolver;

   @Before
   public void testSetup() {
      initMocks(this);

      resolver = new AccountResolver(validator, accountAdmin);

      String uuid = GUID.create();
      when(account.getGuid()).thenReturn(uuid);
      when(prefs.getGuid()).thenReturn(uuid);
      when(account.getPreferences()).thenReturn(prefs);
      accountResult = new ResultSetList<>(Arrays.asList(account));
   }

   @Test
   public void testResolveAccountWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account unique field value cannot be null");
      resolver.resolveAccount(null);
   }

   @Test
   public void testResolveAccountPreferencesWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account unique field value cannot be null");
      resolver.resolveAccountPreferences(null);
   }

   @Test
   public void testResolveAccountWithEmpty() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account unique field value cannot be empty");
      resolver.resolveAccount("");
   }

   @Test
   public void testResolveAccountPreferencesWithEmpty() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("account unique field value cannot be empty");
      resolver.resolveAccountPreferences("");
   }

   @Test
   public void testResolveAccountAsEmail() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.EMAIL);
      when(accountAdmin.getAccountByEmail(TEST_VALUE)).thenReturn(accountResult);

      ResultSet<Account> actual = resolver.resolveAccount(TEST_VALUE);
      assertEquals(accountResult, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(accountAdmin).getAccountByEmail(TEST_VALUE);
   }

   @Test
   public void testResolveAccountPrefsUnknown() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.UNKNOWN);

      ResultSet<AccountPreferences> actual = resolver.resolveAccountPreferences(TEST_VALUE);
      assertEquals(0, actual.size());

      verify(accountAdmin, times(0)).getAccountByEmail(anyString());
      verify(accountAdmin, times(0)).getAccountById(Matchers.any(ArtifactId.class));
   }

   @Test
   public void testResolveAccountPreferences() {
      when(validator.guessFormatType(TEST_VALUE)).thenReturn(AccountField.EMAIL);
      when(accountAdmin.getAccountByEmail(TEST_VALUE)).thenReturn(accountResult);

      ResultSet<AccountPreferences> result = resolver.resolveAccountPreferences(TEST_VALUE);
      AccountPreferences actual = result.getExactlyOne();
      assertEquals(prefs, actual);

      verify(validator).guessFormatType(TEST_VALUE);
      verify(accountAdmin).getAccountByEmail(TEST_VALUE);
   }
}
