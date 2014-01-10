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
package org.eclipse.osee.account.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.account.admin.AccountAdminConfiguration;
import org.eclipse.osee.account.admin.AccountAdminConfigurationBuilder;
import org.eclipse.osee.account.admin.AccountConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link AccountAdminConfigurationBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class AccountAdminConfigurationBuilderTest {

   private static final String DISPLAY_PATTERN = "pattern1";
   private static final String EMAIL_PATTERN = "pattern2";
   private static final String USERNAME_PATTERN = "pattern3";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private AccountAdminConfigurationBuilder builder;

   @Before
   public void testSetup() {
      initMocks(this);

      builder = AccountAdminConfigurationBuilder.newBuilder();
   }

   @Test
   public void testFields() {
      builder.displayNamePattern(DISPLAY_PATTERN);
      builder.emailPattern(EMAIL_PATTERN);
      builder.userNamePattern(USERNAME_PATTERN);

      AccountAdminConfiguration actual = builder.build();

      assertEquals(DISPLAY_PATTERN, actual.getDisplayNamePattern());
      assertEquals(EMAIL_PATTERN, actual.getEmailPattern());
      assertEquals(USERNAME_PATTERN, actual.getUserNamePattern());
   }

   @Test
   public void testConfigProperties() {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(AccountConstants.ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN, DISPLAY_PATTERN);
      properties.put(AccountConstants.ACCOUNT_EMAIL_VALIDATION_PATTERN, EMAIL_PATTERN);
      properties.put(AccountConstants.ACCOUNT_USERNAME_VALIDATION_PATTERN, USERNAME_PATTERN);
      builder.properties(properties);

      AccountAdminConfiguration actual = builder.build();

      assertEquals(DISPLAY_PATTERN, actual.getDisplayNamePattern());
      assertEquals(EMAIL_PATTERN, actual.getEmailPattern());
      assertEquals(USERNAME_PATTERN, actual.getUserNamePattern());
   }

   @Test
   public void testNoChangeAfterBuild() {
      builder.displayNamePattern(DISPLAY_PATTERN);
      builder.emailPattern(EMAIL_PATTERN);
      builder.userNamePattern(USERNAME_PATTERN);

      AccountAdminConfiguration actual = builder.build();

      assertEquals(DISPLAY_PATTERN, actual.getDisplayNamePattern());
      assertEquals(EMAIL_PATTERN, actual.getEmailPattern());
      assertEquals(USERNAME_PATTERN, actual.getUserNamePattern());

      builder.displayNamePattern("a");
      builder.emailPattern("b");
      builder.userNamePattern("c");

      assertEquals(DISPLAY_PATTERN, actual.getDisplayNamePattern());
      assertEquals(EMAIL_PATTERN, actual.getEmailPattern());
      assertEquals(USERNAME_PATTERN, actual.getUserNamePattern());
   }

}
