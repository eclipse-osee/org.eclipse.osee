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
package org.eclipse.osee.account.rest.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountsResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountsResourceTest {

   private static final String NEW_USERNAME = "helloWorld";

   //@formatter:off
   @Mock private AccountOps accountOps;
   @Mock private AccountInfoData account;
   @Mock private AccountInput input;
   //@formatter:on

   private AccountsResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountsResource(accountOps, null);
   }

   @Test
   public void testCreateAccount() {
      when(accountOps.createAccount(NEW_USERNAME, input)).thenReturn(account);

      AccountInfoData actual = resource.createAccount(NEW_USERNAME, input);
      assertEquals(account, actual);

      verify(accountOps).createAccount(NEW_USERNAME, input);
   }

   @Test
   public void testGetAccounts() {
      List<AccountInfoData> accesses = new ArrayList<>();
      accesses.add(account);
      AccountInfoData[] expected = accesses.toArray(new AccountInfoData[] {});
      when(accountOps.getAllAccounts()).thenReturn(accesses);

      AccountInfoData[] actual = resource.getAccounts();

      assertArrayEquals(expected, actual);
      verify(accountOps).getAllAccounts();
   }
}
