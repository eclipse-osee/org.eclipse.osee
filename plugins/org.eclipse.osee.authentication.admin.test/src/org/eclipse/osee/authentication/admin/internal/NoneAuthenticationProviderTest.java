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
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationConstants;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.authentication.admin.AuthenticationRequestBuilder;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link NoneAuthenticationProvider}
 * 
 * @author Roberto E. Escobar
 */
public class NoneAuthenticationProviderTest {

   private static final String NONE_SCHEME = "none";
   private static final String USERNAME = "my username";
   private static final String PASSWORD = "my password";

   // @formatter:off
   @Mock private Log logger;
   // @formatter:on

   private AuthenticationAdminImpl admin;

   @Before
   public void testSetup() {
      initMocks(this);

      admin = new AuthenticationAdminImpl();
      admin.setLogger(logger);

      NoneAuthenticationProvider provider = new NoneAuthenticationProvider();
      admin.addAuthenticationProvider(provider);

      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED,
         NoneAuthenticationProvider.AUTHENTICATION_TYPE);

      admin.start(properties);
   }

   @Test
   public void testAuthenticate() {
      Iterable<String> iterable = admin.getAllowedSchemes();
      assertEquals(NONE_SCHEME, iterable.iterator().next());

      AuthenticationRequest request = AuthenticationRequestBuilder.newBuilder()//
         .scheme(NONE_SCHEME) //
         .userName(USERNAME)//
         .password(PASSWORD)//
         .build();

      AuthenticatedUser actual = admin.authenticate(request);

      assertNotNull(actual);

      assertEquals(USERNAME, actual.getName());
      assertEquals(USERNAME, actual.getUserName());
      assertEquals(USERNAME, actual.getDisplayName());

      assertEquals("", actual.getEmailAddress());

      assertEquals(false, actual.getRoles().iterator().hasNext());
      assertEquals(true, actual.isActive());
      assertEquals(true, actual.isAuthenticated());
   }

}
