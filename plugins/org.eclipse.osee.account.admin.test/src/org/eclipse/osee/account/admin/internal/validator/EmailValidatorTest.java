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
package org.eclipse.osee.account.admin.internal.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountAdminConfiguration;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link EmailValidator}
 * 
 * @author Roberto E. Escobar
 */
public class EmailValidatorTest extends AbstractConfigurableValidatorTest<EmailValidator> {

   private static final String VALID_EMAIL = "atest@email.com";
   private static final String INVALID_EMAIL = "at est@ema il.com";
   private static final String INVALID_EMAIL_ENDING = "atest@something.com";

   // @formatter:off
   @Mock private AccountStorage storage;
   @Mock private AccountAdminConfiguration config;
   // @formatter:on

   public EmailValidatorTest() {
      super(AccountField.EMAIL, VALID_EMAIL, INVALID_EMAIL, INVALID_EMAIL_ENDING);
   }

   @Override
   public void testSetup() {
      super.testSetup();

      when(storage.emailExists(VALID_EMAIL)).thenReturn(false);
   }

   @Override
   protected EmailValidator createValidator() {
      return new EmailValidator(storage);
   }

   @Override
   protected Pattern createCustomPattern() {
      return Pattern.compile("(.*?)@email\\.com");
   }

   @Override
   @Test
   public void testExists() {
      when(storage.emailExists(VALID_EMAIL)).thenReturn(true);

      boolean status = getValidator().exists(VALID_EMAIL);
      assertTrue(status);

      verify(storage).emailExists(VALID_EMAIL);
   }

   @Override
   @Test
   public void testNotExists() {
      when(storage.emailExists(VALID_EMAIL)).thenReturn(false);

      boolean status = getValidator().exists(VALID_EMAIL);
      assertFalse(status);

      verify(storage).emailExists(VALID_EMAIL);
   }

   @Override
   @Test
   public void testValidateFailNotUnique() {
      when(storage.emailExists(VALID_EMAIL)).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid [" + getExpectedName() + "] - [" + getValidValue() + "] is already in use");
      getValidator().validate(getValidValue());
   }

   @Override
   public void testGetPatternFromConfig() {
      String patternFromConfig = "adashdsahfafha";
      when(config.getEmailPattern()).thenReturn(patternFromConfig);

      String actual = getValidator().getPatternFromConfig(config);
      assertEquals(patternFromConfig, actual);

      verify(config).getEmailPattern();
   }
}
