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

package org.eclipse.osee.authentication.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.authentication.admin.AuthenticationConfiguration;
import org.eclipse.osee.authentication.admin.AuthenticationConfigurationBuilder;
import org.eclipse.osee.authentication.admin.AuthenticationConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link AuthenticationConfigurationBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class AuthenticationConfigurationBuilderTest {

   private static final String SCHEME_1 = "scheme1";
   private static final String SCHEME_2 = "scheme2";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private AuthenticationConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = AuthenticationConfigurationBuilder.newBuilder();
   }

   @Test
   public void testAddSchemes() {
      builder.scheme(SCHEME_1);
      builder.scheme(SCHEME_2);

      AuthenticationConfiguration actual = builder.build();

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());
   }

   @Test
   public void testDefaultScheme() {
      builder.defaultScheme(SCHEME_2);

      AuthenticationConfiguration actual = builder.build();

      assertEquals(SCHEME_2, actual.getDefaultScheme());
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> properties = new HashMap<>();
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED, SCHEME_1 + "," + SCHEME_2);
      properties.put(AuthenticationConstants.AUTHENTICATION_SCHEME_ALLOWED_DEFAULT, SCHEME_2);
      builder.properties(properties);

      AuthenticationConfiguration actual = builder.build();

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(SCHEME_2, iterator.next());

      assertEquals(SCHEME_2, actual.getDefaultScheme());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.scheme(SCHEME_1);
      builder.defaultScheme(SCHEME_1);

      AuthenticationConfiguration actual = builder.build();

      Iterator<String> iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

      assertEquals(SCHEME_1, actual.getDefaultScheme());

      builder.scheme(SCHEME_2);
      builder.defaultScheme(SCHEME_2);

      iterator = actual.getAllowedSchemes().iterator();
      assertEquals(SCHEME_1, iterator.next());
      assertEquals(false, iterator.hasNext());

      assertEquals(SCHEME_1, actual.getDefaultScheme());
   }
}
