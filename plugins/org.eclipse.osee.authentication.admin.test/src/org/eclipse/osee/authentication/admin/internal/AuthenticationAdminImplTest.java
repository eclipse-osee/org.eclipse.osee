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
package org.eclipse.osee.authentication.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationConstants;
import org.eclipse.osee.authentication.admin.AuthenticationProvider;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.admin.AuthenticationRequestBuilder;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link AuthenticationAdminImpl}
 * 
 * @author Roberto E. Escobar
 */
public class AuthenticationAdminImplTest {

   private static final String SCHEME_1 = "scheme1";
   private static final String SCHEME_2 = "scheme2";

   private static final String USERNAME = "my username";
   private static final String PASSWORD = "my password";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   
   @Mock private AuthenticationProvider provider1;
   @Mock private AuthenticationProvider provider2;
   
   @Mock private AuthenticatedUser authenticatedUser;
   // @formatter:on

   private AuthenticationAdminImpl admin;

   @Before
   public void testSetup() {
      initMocks(this);

      admin = new AuthenticationAdminImpl();
      admin.setLogger(logger);
      admin.start(Collections.<String, Object> emptyMap());

      when(provider1.getAuthenticationScheme()).thenReturn(SCHEME_1);
      when(provider2.getAuthenticationScheme()).thenReturn(SCHEME_2);
   }

   @Test
   public void testAddRemoveProvider() {
      Iterable<String> iterable = admin.getAvailableSchemes();
      assertEquals(false, iterable.iterator().hasNext());

      admin.addAuthenticationProvider(provider1);
      admin.addAuthenticationProvider(provider2);

      iterable = admin.getAvailableSchemes();
      Iterator<String> iterator = iterable.iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());

      admin.removeAuthenticationProvider(provider1);

      iterable = admin.getAvailableSchemes();
      iterator = iterable.iterator();
      assertEquals(SCHEME_2, iterator.next());

      admin.removeAuthenticationProvider(provider2);
      iterable = admin.getAvailableSchemes();
      assertEquals(false, iterable.iterator().hasNext());
   }

   @Test
   public void testAuthenticateExceptionNoSchemeAvailable() {
      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(SCHEME_1) //
         .userName(USERNAME)//
         .password(PASSWORD)//
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] is not allowed. Schemes available [].");
      admin.authenticate(request);
   }

   @Test
   public void testAuthenticateSchemeAvailableButNotAllowed() {
      admin.addAuthenticationProvider(provider1);

      Iterable<String> iterable = admin.getAvailableSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      iterable = admin.getAllowedSchemes();
      assertEquals(false, iterable.iterator().hasNext());

      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(SCHEME_1) //
         .userName(USERNAME)//
         .password(PASSWORD)//
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] is not allowed. Schemes available [].");
      admin.authenticate(request);
   }

   @Test
   public void testAuthenticateExceptionProviderReturnsNull() {
      admin.addAuthenticationProvider(provider1);

      Iterable<String> iterable = admin.getAvailableSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      iterable = admin.getAllowedSchemes();
      assertEquals(false, iterable.iterator().hasNext());

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      iterable = admin.getAllowedSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(SCHEME_1) //
         .userName(USERNAME)//
         .password(PASSWORD)//
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] returned null principal");
      admin.authenticate(request);
   }

   @Test
   public void testAuthenticate() {
      admin.addAuthenticationProvider(provider1);

      Iterable<String> iterable = admin.getAvailableSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      iterable = admin.getAllowedSchemes();
      assertEquals(false, iterable.iterator().hasNext());

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      iterable = admin.getAllowedSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(SCHEME_1) //
         .userName(USERNAME)//
         .password(PASSWORD)//
         .build();

      when(provider1.authenticate(request)).thenReturn(authenticatedUser);

      AuthenticatedUser actual = admin.authenticate(request);

      verify(provider1).authenticate(request);
      assertEquals(authenticatedUser, actual);
   }

   @Test
   public void testGetAllowedSchemes() {
      admin.addAuthenticationProvider(provider1);

      Iterable<String> iterable = admin.getAvailableSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());

      iterable = admin.getAllowedSchemes();
      assertEquals(false, iterable.iterator().hasNext());

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      iterable = admin.getAllowedSchemes();
      assertEquals(SCHEME_1, iterable.iterator().next());
   }

   @Test
   public void testIsSchemeAllowed() {
      admin.addAuthenticationProvider(provider1);
      admin.addAuthenticationProvider(provider2);

      Iterable<String> iterable = admin.getAvailableSchemes();
      Iterator<String> iterator = iterable.iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());

      boolean actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(false, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      admin.update(properties);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(true, actual);
   }

   @Test
   public void testConfigUpdate() {
      Iterator<String> iterator = admin.getAllowedSchemes().iterator();
      assertEquals(false, iterator.hasNext());

      Map<String, Object> properties = new HashMap<>();
      admin.update(properties);

      iterator = admin.getAllowedSchemes().iterator();
      assertEquals(false, iterator.hasNext());

      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      admin.update(properties);

      iterator = admin.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
   }

   @Test
   public void testDefaultSchemeAllowed() {
      admin.addAuthenticationProvider(provider1);
      admin.addAuthenticationProvider(provider2);

      Iterable<String> iterable = admin.getAvailableSchemes();
      Iterator<String> iterator = iterable.iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());

      assertEquals("", admin.getDefaultScheme());

      boolean actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(false, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      assertEquals(SCHEME_1, admin.getDefaultScheme());

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      admin.update(properties);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(true, actual);

      assertEquals(SCHEME_1, admin.getDefaultScheme());

      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED_DEFAULT, SCHEME_2);
      admin.update(properties);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(true, actual);

      assertEquals(SCHEME_2, admin.getDefaultScheme());
   }

}
