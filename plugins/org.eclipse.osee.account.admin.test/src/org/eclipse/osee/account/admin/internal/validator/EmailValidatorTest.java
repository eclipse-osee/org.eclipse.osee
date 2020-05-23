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

package org.eclipse.osee.account.admin.internal.validator;

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_EMAIL_VALIDATION_PATTERN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.regex.Pattern;
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
   // @formatter:on

   public EmailValidatorTest() {
      super(AccountField.EMAIL, VALID_EMAIL, INVALID_EMAIL, INVALID_EMAIL_ENDING, ACCOUNT_EMAIL_VALIDATION_PATTERN);
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

}
