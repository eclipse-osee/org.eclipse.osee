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
package org.eclipse.osee.authorization.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.authorization.admin.Authority;
import org.eclipse.osee.authorization.admin.Authorization;
import org.eclipse.osee.authorization.admin.AuthorizationConstants;
import org.eclipse.osee.authorization.admin.AuthorizationData;
import org.eclipse.osee.authorization.admin.AuthorizationOverride;
import org.eclipse.osee.authorization.admin.AuthorizationProvider;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.authorization.admin.AuthorizationRequestBuilder;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Test Case for {@link AuthorizationAdminImpl}
 * 
 * @author Roberto E. Escobar
 */
public class AuthorizationAdminImplTest {

   private static final String DEFAULT_SCHEME = "none";
   private static final String SCHEME_1 = "scheme1";
   private static final String SCHEME_2 = "scheme2";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   
   @Mock private Authorization authorization;
   @Mock private AuthorizationProvider provider1;
   @Mock private AuthorizationProvider provider2;
   // @formatter:on

   private AuthorizationAdminImpl admin;

   @Before
   public void testSetup() {
      initMocks(this);

      admin = new AuthorizationAdminImpl();
      admin.setLogger(logger);
      admin.start(Collections.<String, Object> emptyMap());

      when(provider1.getScheme()).thenReturn(SCHEME_1);
      when(provider2.getScheme()).thenReturn(SCHEME_2);

      admin.addAuthorizationProvider(new NoneAuthorizationProvider());
   }

   @Test
   public void testAddRemoveProvider() {
      Iterator<String> iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(false, iterator.hasNext());

      admin.addAuthorizationProvider(provider1);
      admin.addAuthorizationProvider(provider2);

      iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
      assertEquals(false, iterator.hasNext());

      admin.removeAuthorizationProvider(provider1);

      iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
      assertEquals(false, iterator.hasNext());

      admin.removeAuthorizationProvider(provider2);

      iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(false, iterator.hasNext());
   }

   @Test
   public void testAuthorizeSchemeAvailableButNotAllowed() {
      admin.addAuthorizationProvider(provider1);

      Iterator<String> iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

      boolean actual = admin.isSchemeAllowed(DEFAULT_SCHEME);
      assertEquals(false, actual);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(false, actual);

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .authorizationType(SCHEME_1) //
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authorization Error - scheme [" + SCHEME_1 + "] is not allowed. Schemes available [].");
      admin.authorize(request);
   }

   @Test
   public void testAuthorizeSchemeAvailableButNoneNotAllowed() {
      Iterator<String> iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(false, iterator.hasNext());

      boolean actual = admin.isSchemeAllowed(DEFAULT_SCHEME);
      assertEquals(false, actual);

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .authorizationType("None")//
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authorization Error - scheme [None] is not allowed. Schemes available [].");
      admin.authorize(request);
   }

   @Test
   public void testAuthorizeExceptionProviderReturnsNull() {
      admin.addAuthorizationProvider(provider1);

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_OVERRIDE, AuthorizationOverride.NONE);
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      assertEquals(true, admin.isSchemeAllowed(SCHEME_1));

      Date date = new Date();
      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .date(date)//
         .authorizationType(SCHEME_1) //
         .secure(true) //
         .build();

      when(provider1.authorize(request)).thenReturn(null);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] returned null authorization");
      admin.authorize(request);
   }

   @Test
   public void testAuthorizeExceptionProviderWasNull() {
      Date date = new Date();

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_OVERRIDE, AuthorizationOverride.NONE);
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      assertEquals(true, admin.isSchemeAllowed(SCHEME_1));

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .date(date)//
         .authorizationType(SCHEME_1) //
         .secure(true) //
         .build();

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] returned null provider");
      admin.authorize(request);
   }

   @Test
   public void testAuthorizeExceptionAuthorityWasNull() {
      admin.addAuthorizationProvider(provider1);

      Date date = new Date();

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_OVERRIDE, AuthorizationOverride.NONE);
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      assertEquals(true, admin.isSchemeAllowed(SCHEME_1));

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .date(date)//
         .authorizationType(SCHEME_1) //
         .secure(true) //
         .build();

      AuthorizationData data = Mockito.mock(AuthorizationData.class);

      when(provider1.authorize(request)).thenReturn(data);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Authentication Error - scheme [" + SCHEME_1 + "] returned null authority");
      admin.authorize(request);
   }

   @Test
   public void testGetAllowedSchemes() {
      Iterator<String> iterator = admin.getAllowedSchemes().iterator();
      assertEquals(false, iterator.hasNext());

      admin.addAuthorizationProvider(provider1);

      iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

      iterator = admin.getAllowedSchemes().iterator();
      assertEquals(false, iterator.hasNext());

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      iterator = admin.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

   }

   @Test
   public void testIsSchemeAllowed() {
      admin.addAuthorizationProvider(provider1);
      admin.addAuthorizationProvider(provider2);

      Iterator<String> iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
      assertEquals(false, iterator.hasNext());

      boolean actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(false, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      actual = admin.isSchemeAllowed(DEFAULT_SCHEME);
      assertEquals(false, actual);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);

      admin.update(properties);

      actual = admin.isSchemeAllowed(SCHEME_1);
      assertEquals(true, actual);

      actual = admin.isSchemeAllowed(SCHEME_2);
      assertEquals(false, actual);
   }

   @Test
   public void testConfigUpdate() {
      Iterator<String> iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(false, iterator.hasNext());

      Map<String, Object> properties = new HashMap<>();
      admin.update(properties);

      iterator = admin.getAvailableSchemes().iterator();
      assertEquals(DEFAULT_SCHEME, iterator.next());
      assertEquals(false, iterator.hasNext());

      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      admin.update(properties);

      iterator = admin.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
      assertEquals(false, iterator.hasNext());
   }

   @Test
   public void testAuthorizeWithDefault() {
      Date date = new Date();

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, DEFAULT_SCHEME);
      admin.update(properties);

      assertEquals(true, admin.isSchemeAllowed(DEFAULT_SCHEME));

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .date(date)//
         .authorizationType(SCHEME_1) //
         .secure(true) //
         .build();

      Authorization actual = admin.authorize(request);

      assertEquals(date, actual.getCreationDate());
      assertEquals(AuthorizationConstants.PERMIT_ALL_AUTHORIZER_SCHEME, actual.getScheme());
      assertEquals(true, actual.isSecure());
      assertEquals(null, actual.getPrincipal());
      assertEquals(true, actual.isInRole(null));
      assertEquals(true, actual.isInRole(""));
      assertEquals(true, actual.isInRole("*"));
      assertEquals(true, actual.isInRole("adssadadad"));
   }

   @Test
   public void testAuthorizeWithProvider() {
      admin.addAuthorizationProvider(provider1);

      Date date = new Date();

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_OVERRIDE, AuthorizationOverride.NONE);
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_1);
      admin.update(properties);

      assertEquals(true, admin.isSchemeAllowed(SCHEME_1));

      AuthorizationRequest request = AuthorizationRequestBuilder.newBuilder()//
         .date(date)//
         .authorizationType(SCHEME_1) //
         .secure(true) //
         .build();

      AuthorizationData data = Mockito.mock(AuthorizationData.class);
      Authority authority = Mockito.mock(Authority.class);
      Principal principal = Mockito.mock(Principal.class);

      when(provider1.authorize(request)).thenReturn(data);
      when(data.getAuthority()).thenReturn(authority);
      when(data.getPrincipal()).thenReturn(principal);

      when(authority.getScheme()).thenReturn("authority");
      when(authority.isInRole("okRole")).thenReturn(true);

      Authorization actual = admin.authorize(request);

      assertEquals(date, actual.getCreationDate());
      assertEquals("authority", actual.getScheme());
      assertEquals(true, actual.isSecure());
      assertEquals(principal, actual.getPrincipal());
      assertEquals(false, actual.isInRole(null));
      assertEquals(false, actual.isInRole(""));
      assertEquals(false, actual.isInRole("*"));
      assertEquals(false, actual.isInRole("adssadadad"));
      assertEquals(true, actual.isInRole("okRole"));
   }

}
