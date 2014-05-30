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
package org.eclipse.osee.jaxrs.server.internal.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.osee.authorization.admin.Authorization;
import org.eclipse.osee.authorization.admin.AuthorizationAdmin;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jaxrs.server.internal.filters.SecurityContextProviderImpl;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import com.sun.jersey.api.core.HttpRequestContext;

/**
 * Unit Test for {@link SecurityContextProviderImpl}
 * 
 * @author Roberto E. Escobar
 */
public class SecurityContextProviderImplTest {

   private static final String AUTHORIZATION = "custom auth";
   private static final String PATH_1 = "path 1";
   private static final String METHOD_1 = "mymethod";
   private static final String DATE_1 = "Tue, 15 Nov 1994 08:12:31 GMT";
   private static final String OK_ROLE = "okRole";
   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private AuthorizationAdmin authorizationAdmin;
   
   @Mock private HttpRequestContext request;
   @Mock private Authorization authorization;
   @Mock private Principal principal;
   @Captor private ArgumentCaptor<AuthorizationRequest> captor;
   // @formatter:on

   private SecurityContextProviderImpl provider;
   private SimpleDateFormat dateFormat;

   @Before
   public void setup() {
      initMocks(this);

      provider = new SecurityContextProviderImpl(logger, authorizationAdmin);

      dateFormat = new SimpleDateFormat(SecurityContextProviderImpl.HTTP_DATE_FORMAT);
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   @Test
   public void testGetSecurityContext() {
      when(request.isSecure()).thenReturn(true);
      when(request.getHeaderValue(SecurityContextProviderImpl.AUTHORIZATION_HEADER)).thenReturn(AUTHORIZATION);
      when(request.getHeaderValue(SecurityContextProviderImpl.DATE_HEADER)).thenReturn(DATE_1);
      when(request.getPath()).thenReturn(PATH_1);
      when(request.getMethod()).thenReturn(METHOD_1);

      when(authorizationAdmin.authorize(any(AuthorizationRequest.class))).thenReturn(authorization);
      when(authorization.getPrincipal()).thenReturn(principal);
      when(authorization.isInRole(OK_ROLE)).thenReturn(true);
      when(authorization.isSecure()).thenReturn(true);
      when(authorization.getScheme()).thenReturn(AUTHORIZATION);

      SecurityContext context = provider.getSecurityContext(request);

      verify(authorizationAdmin).authorize(captor.capture());

      AuthorizationRequest authRequest = captor.getValue();
      assertEquals(true, authRequest.isSecure());
      assertEquals(AUTHORIZATION, authRequest.getAuthorizationType());
      assertEquals(METHOD_1, authRequest.getMethod());
      assertEquals(PATH_1, authRequest.getPath());
      Date actualDate = authRequest.getRequestDate();
      assertNotNull(actualDate);
      assertEquals(DATE_1, dateFormat.format(actualDate));

      assertEquals(AUTHORIZATION, context.getAuthenticationScheme());
      assertEquals(principal, context.getUserPrincipal());
      assertEquals(true, context.isSecure());
      assertEquals(false, context.isUserInRole(null));
      assertEquals(true, context.isUserInRole(OK_ROLE));
   }

   @Test
   public void testGetSecurityContextException() {
      when(request.getHeaderValue(SecurityContextProviderImpl.AUTHORIZATION_HEADER)).thenReturn(AUTHORIZATION);
      when(request.getHeaderValue(SecurityContextProviderImpl.DATE_HEADER)).thenReturn(DATE_1);
      when(request.getPath()).thenReturn(PATH_1);
      when(request.getMethod()).thenReturn(METHOD_1);

      when(authorizationAdmin.authorize(any(AuthorizationRequest.class))).thenReturn(null);

      SecurityContext context = provider.getSecurityContext(request);

      thrown.expect(OseeWebApplicationException.class);
      context.getAuthenticationScheme();

      thrown.expect(OseeWebApplicationException.class);
      context.getUserPrincipal();

      thrown.expect(OseeWebApplicationException.class);
      context.isUserInRole(null);

      thrown.expect(OseeWebApplicationException.class);
      context.isSecure();
   }
}
