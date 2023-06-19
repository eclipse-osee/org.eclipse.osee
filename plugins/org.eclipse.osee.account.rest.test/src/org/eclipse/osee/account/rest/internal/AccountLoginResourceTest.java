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
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.account.rest.model.AccountLoginData;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AccountLoginResource}
 *
 * @author Roberto E. Escobar
 */
public class AccountLoginResourceTest {

   private static final long ACCOUNT_ID = 123413241244L;
   private static final String ACCOUNT_BASE = "accounts";

   //@formatter:off
   @Mock private AccountOps ops;
   @Mock private HttpServletRequest request;
   @Mock private UriInfo uriInfo;
   @Mock private AccountLoginData data;
   @Mock private RequestInfo reqInfo;
   @Mock private AccountSessionData session;
   @Captor private ArgumentCaptor<RequestInfo> reqInfoCaptor;
   //@formatter:on

   private AccountLoginResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      resource = new AccountLoginResource(ops);
   }

   @Test
   public void testLogin() throws URISyntaxException {
      when(session.getAccountId()).thenReturn(ACCOUNT_ID);

      when(ops.asRequestInfo(request)).thenReturn(reqInfo);
      when(ops.doLogin(any(RequestInfo.class), eq(data))).thenReturn(session);
      when(uriInfo.getBaseUri()).thenReturn(new URI(ACCOUNT_BASE));

      try (Response response = resource.login(request, uriInfo, data)) {
         assertEquals(Status.OK.getStatusCode(), response.getStatus());
         AccountSessionData actual = (AccountSessionData) response.getEntity();
         assertEquals(session, actual);
         URI location = (URI) response.getMetadata().getFirst(HttpHeaders.CONTENT_LOCATION);
         assertNotNull(location);
         assertEquals(ACCOUNT_BASE + "/" + ACCOUNT_ID, location.toASCIIString());
      }
      verify(ops).asRequestInfo(request);
      verify(ops).doLogin(reqInfoCaptor.capture(), eq(data));
      verify(uriInfo).getBaseUri();

      assertEquals(reqInfo, reqInfoCaptor.getValue());
   }

}
