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
 * Test Case for {@link DisplayNameValidator}
 * 
 * @author Roberto E. Escobar
 */
public class DisplayNameValidatorTest extends AbstractConfigurableValidatorTest<DisplayNameValidator> {

   private static final String VALID_DISPLAY_NAME = "my_name";
   private static final String INVALID_DISPLAY_NAME = "";
   private static final String INVALID_DISPLAY_NAME_FOR_PATTERN = "12313";

   // @formatter:off
   @Mock private AccountStorage storage;
   @Mock private AccountAdminConfiguration config;
   // @formatter:on

   public DisplayNameValidatorTest() {
      super(AccountField.DISPLAY_NAME, VALID_DISPLAY_NAME, INVALID_DISPLAY_NAME, INVALID_DISPLAY_NAME_FOR_PATTERN);
   }

   @Override
   public void testSetup() {
      super.testSetup();

      when(storage.displayNameExists(getValidValue())).thenReturn(false);
   }

   @Override
   protected DisplayNameValidator createValidator() {
      return new DisplayNameValidator(storage);
   }

   @Override
   protected Pattern createCustomPattern() {
      return Pattern.compile("[A-Za-z_]+");
   }

   @Override
   @Test
   public void testExists() {
      when(storage.displayNameExists(getValidValue())).thenReturn(true);

      boolean status = getValidator().exists(getValidValue());
      assertTrue(status);

      verify(storage).displayNameExists(getValidValue());
   }

   @Override
   @Test
   public void testNotExists() {
      when(storage.displayNameExists(getValidValue())).thenReturn(false);

      boolean status = getValidator().exists(getValidValue());
      assertFalse(status);

      verify(storage).displayNameExists(getValidValue());
   }

   @Override
   @Test
   public void testValidateFailNotUnique() {
      when(storage.displayNameExists(getValidValue())).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid [" + getExpectedName() + "] - [" + getValidValue() + "] is already in use");
      getValidator().validate(getValidValue());
   }

   @Override
   public void testGetPatternFromConfig() {
      String patternFromConfig = "adashdsahfafha";
      when(config.getDisplayNamePattern()).thenReturn(patternFromConfig);

      String actual = getValidator().getPatternFromConfig(config);
      assertEquals(patternFromConfig, actual);

      verify(config).getDisplayNamePattern();
   }
}
