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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountResource}
 * 
 * @author Roberto E. Escobar
 */
public class AccountResourceTest {

   private static final String ACCOUNT_ID = "hello@hello.com";

   //@formatter:off
   @Mock private AccountOps accountOps;
   
   @Mock private AccountInput accountInput;
   @Mock private AccountInfoData accountInfoData;
   @Mock private AccountDetailsData details;
   //@formatter:on

   private AccountResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountResource(accountOps, ACCOUNT_ID);
   }

   @Test
   public void testCreateAccount() {
      when(accountOps.createAccount(ACCOUNT_ID, accountInput)).thenReturn(accountInfoData);

      AccountInfoData actual = resource.createAccount(accountInput);
      assertEquals(accountInfoData, actual);

      verify(accountOps).createAccount(ACCOUNT_ID, accountInput);
   }

   @Test
   public void testDeleteAccount() {
      when(accountOps.deleteAccount(ACCOUNT_ID)).thenReturn(true);

      Response actual = resource.deleteAccount();
      assertEquals(Status.OK.getStatusCode(), actual.getStatus());

      verify(accountOps).deleteAccount(ACCOUNT_ID);
   }

   @Test
   public void testDeleteAccountNotModified() {
      when(accountOps.deleteAccount(ACCOUNT_ID)).thenReturn(false);

      Response actual = resource.deleteAccount();
      assertEquals(Status.NOT_MODIFIED.getStatusCode(), actual.getStatus());

      verify(accountOps).deleteAccount(ACCOUNT_ID);
   }

   @Test
   public void testGetAccountDetailsData() {
      when(accountOps.getAccountDetailsData(ACCOUNT_ID)).thenReturn(details);

      AccountDetailsData actual = resource.getAccountDetailsData();
      assertEquals(details, actual);

      verify(accountOps).getAccountDetailsData(ACCOUNT_ID);
   }

   @Test
   public void testAccountSettingsData() {
      AccountPreferencesResource actual = resource.getAccountSettingsData();
      assertNotNull(actual);

      // Ensure resource constructed correctly;
      actual.getAccountPreferences();
      verify(accountOps).getAccountPreferencesData(ACCOUNT_ID);
   }

   @Test
   public void testActive() {
      AccountActiveResource actual = resource.active();
      assertNotNull(actual);

      // Ensure resource constructed correctly;
      actual.isActive();
      verify(accountOps).isActive(ACCOUNT_ID);
   }

   @Test
   public void testGetSessions() {
      AccountSessionsResource actual = resource.sessions();
      assertNotNull(actual);

      // Ensure resource constructed correctly;
      actual.getAccountSessions();
      verify(accountOps).getAccountSessionById(ACCOUNT_ID);
   }

}
