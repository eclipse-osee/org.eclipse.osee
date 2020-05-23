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

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_USERNAME_VALIDATION_PATTERN;
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
 * Test Case for {@link UserNameValidator}
 * 
 * @author Roberto E. Escobar
 */
public class UserNameValidatorTest extends AbstractConfigurableValidatorTest<UserNameValidator> {

   private static final String VALID_USERNAME = "1231244112123";
   private static final String INVALID_USERNAME = "";
   private static final String INVALID_USERNAME_FOR_PATTERN = "asdasda";

   // @formatter:off
   @Mock private AccountStorage storage;
   // @formatter:on

   public UserNameValidatorTest() {
      super(AccountField.USERNAME, VALID_USERNAME, INVALID_USERNAME, INVALID_USERNAME_FOR_PATTERN,
         ACCOUNT_USERNAME_VALIDATION_PATTERN);
   }

   @Override
   public void testSetup() {
      super.testSetup();

      when(storage.userNameExists(getValidValue())).thenReturn(false);
   }

   @Override
   protected UserNameValidator createValidator() {
      return new UserNameValidator(storage);
   }

   @Override
   protected Pattern createCustomPattern() {
      return Pattern.compile("\\d+");
   }

   @Override
   @Test
   public void testExists() {
      when(storage.userNameExists(getValidValue())).thenReturn(true);

      boolean status = getValidator().exists(getValidValue());
      assertTrue(status);

      verify(storage).userNameExists(getValidValue());
   }

   @Override
   @Test
   public void testNotExists() {
      when(storage.userNameExists(getValidValue())).thenReturn(false);

      boolean status = getValidator().exists(getValidValue());
      assertFalse(status);

      verify(storage).userNameExists(getValidValue());
   }

   @Override
   @Test
   public void testValidateFailNotUnique() {
      when(storage.userNameExists(getValidValue())).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid [" + getExpectedName() + "] - [" + getValidValue() + "] is already in use");
      getValidator().validate(getValidValue());
   }

}
