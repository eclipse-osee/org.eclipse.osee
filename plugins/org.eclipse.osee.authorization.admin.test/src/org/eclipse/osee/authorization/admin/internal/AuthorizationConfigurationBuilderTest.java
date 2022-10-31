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

package org.eclipse.osee.authorization.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.authorization.admin.AuthorizationConfiguration;
import org.eclipse.osee.authorization.admin.AuthorizationConfigurationBuilder;
import org.eclipse.osee.authorization.admin.AuthorizationConstants;
import org.eclipse.osee.authorization.admin.AuthorizationOverride;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link AuthorizationConfigurationBuilder}
 *
 * @author Roberto E. Escobar
 */
public class AuthorizationConfigurationBuilderTest {

   private static final String SCHEME_1 = "scheme1";
   private static final String SCHEME_2 = "scheme2";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private AuthorizationConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = AuthorizationConfigurationBuilder.newBuilder();
   }

   @Test
   public void testOverride() {
      builder.override(AuthorizationOverride.DENY_ALL);

      AuthorizationConfiguration actual = builder.build();

      assertEquals(AuthorizationOverride.DENY_ALL, actual.getOverride());
   }

   @Test
   public void testAddSchemes() {
      builder.scheme(SCHEME_1);
      builder.scheme(SCHEME_2);

      AuthorizationConfiguration actual = builder.build();

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthorizationConstants.AUTHORIZATION_OVERRIDE, AuthorizationOverride.DENY_ALL);
      properties.put(AuthorizationConstants.AUTHORIZATION_SCHEME_ALLOWED, SCHEME_2);
      builder.properties(properties);

      AuthorizationConfiguration actual = builder.build();

      assertEquals(AuthorizationOverride.DENY_ALL, actual.getOverride());

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_2, iterator.next());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.override(AuthorizationOverride.DENY_ALL);
      builder.scheme(SCHEME_1);

      AuthorizationConfiguration actual = builder.build();

      assertEquals(AuthorizationOverride.DENY_ALL, actual.getOverride());

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

      builder.override(AuthorizationOverride.PERMIT_ALL);
      builder.scheme(SCHEME_2);

      assertEquals(AuthorizationOverride.DENY_ALL, actual.getOverride());
      iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());
   }
}
