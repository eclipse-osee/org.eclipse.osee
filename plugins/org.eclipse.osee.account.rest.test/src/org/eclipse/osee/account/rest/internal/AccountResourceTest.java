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
import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AccountResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountResourceTest {

   private static final ArtifactId ACCOUNT_ID = ArtifactId.valueOf(93253);

   //@formatter:off
   @Mock private AccountOps accountOps;
   @Mock private AccountInput accountInput;
   @Mock private AccountInfoData accountInfoData;
   @Mock private AccountDetailsData details;

   @Mock private UriInfo uriInfo;
   //@formatter:on

   private AccountResource resource;

   @Before
   public void setUp() {
      initMocks(this);

      resource = new AccountResource(accountOps, ACCOUNT_ID);
   }

   @Test
   public void testDeleteAccount() {
      Response actual = resource.deleteAccount();
      assertEquals(Status.OK.getStatusCode(), actual.getStatus());

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
      verify(accountOps).getAccountPreferencesDataById(ACCOUNT_ID);
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

   @Test
   public void testGetSubscriptions() {
      URI uri = UriBuilder.fromPath("http://localhost:8089/oseex/accounts/{account-id}/subscriptions").build(
         ACCOUNT_ID.getUuid());
      when(uriInfo.getRequestUri()).thenReturn(uri);

      Response response = resource.getSubscriptions(uriInfo);

      int status = response.getStatus();
      assertEquals(Status.SEE_OTHER.getStatusCode(), status);

      URI location = (URI) response.getMetadata().getFirst(HttpHeaders.LOCATION);
      URI expectedLocation =
         UriBuilder.fromUri(uri).path("..").path("..").path("..").path("subscriptions").path("for-account").path(
            "{account-id}").build(ACCOUNT_ID.getUuid());
      assertEquals(expectedLocation, location);
   }

}
