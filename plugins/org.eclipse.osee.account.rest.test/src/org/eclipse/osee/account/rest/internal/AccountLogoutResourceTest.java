/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.account.rest.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AccountLogoutResource}
 * 
 * @author Roberto E. Escobar
 */
public class AccountLogoutResourceTest {

   //@formatter:off
   @Mock private AccountOps ops;

   @Mock private AccountSessionData session;
   //@formatter:on

   private AccountLogoutResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      resource = new AccountLogoutResource(ops);
   }

   @Test
   public void testLogout() {
      String token = "dsfksa-kfsauiewa";

      when(session.getToken()).thenReturn(token);
      when(ops.doLogout(token)).thenReturn(true);

      Response response = resource.logout(session);

      assertEquals(Status.OK.getStatusCode(), response.getStatus());
      verify(ops).doLogout(token);
   }

   @Test
   public void testLogoutNotModified() {
      String token = "dsfksa-kfsauiewa";

      when(session.getToken()).thenReturn(token);
      when(ops.doLogout(token)).thenReturn(false);

      Response response = resource.logout(session);

      assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
      verify(ops).doLogout(token);
   }

}
