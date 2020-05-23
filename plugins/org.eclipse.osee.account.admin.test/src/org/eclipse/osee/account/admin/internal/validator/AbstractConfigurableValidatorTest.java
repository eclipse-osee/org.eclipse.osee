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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountField;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link AbstractConfigurableValidator}
 * 
 * @author Roberto E. Escobar
 */
public abstract class AbstractConfigurableValidatorTest<T extends AbstractConfigurableValidator> extends AbstractValidatorTest<T> {

   private Pattern pattern;
   private final String invalidCustomPatternValue;
   private final String configKey;

   // @formatter:off
   @Mock private Map<String, Object> config;
   // @formatter:on

   public AbstractConfigurableValidatorTest(AccountField fieldType, String validValue, String invalidValue, String invalidCustomPatternValue, String configKey) {
      super(fieldType, validValue, invalidValue);
      this.invalidCustomPatternValue = invalidCustomPatternValue;
      this.configKey = configKey;
   }

   @Override
   public void testSetup() {
      super.testSetup();

      pattern = createCustomPattern();
   }

   protected String getInvalidCustomPatternValue() {
      return invalidCustomPatternValue;
   }

   protected Pattern getPattern() {
      return pattern;
   }

   protected abstract Pattern createCustomPattern();

   @Test
   public void testGetSetCustomPattern() {
      T validator = getValidator();

      validator.setCustomPattern(null);
      boolean patternStatus = validator.hasCustomPattern();
      assertFalse(patternStatus);

      validator.setCustomPattern(getPattern());
      patternStatus = validator.hasCustomPattern();
      assertTrue(patternStatus);

      Pattern actualPattern = validator.getCustomPattern();
      assertEquals(getPattern(), actualPattern);
   }

   @Test
   public void testValidateWithCustomPattern() {
      T validator = getValidator();

      validator.setCustomPattern(getPattern());
      boolean patternStatus = validator.hasCustomPattern();
      assertTrue(patternStatus);

      getValidator().validate(getValidValue());
   }

   @Test
   public void testIsValidWithCustomPattern() {
      T validator = getValidator();

      validator.setCustomPattern(getPattern());
      boolean patternStatus = validator.hasCustomPattern();
      assertTrue(patternStatus);

      boolean status = getValidator().isValid(getValidValue());
      assertTrue(status);
   }

   @Test
   public void testIsNotValidWithCustomPattern() {
      T validator = getValidator();

      validator.setCustomPattern(getPattern());
      boolean patternStatus = validator.hasCustomPattern();
      assertTrue(patternStatus);

      boolean status = getValidator().isValid(getInvalidCustomPatternValue());
      assertFalse(status);
   }

   @Test
   public abstract void testExists();

   @Test
   public abstract void testNotExists();

   @Test
   public abstract void testValidateFailNotUnique();

   @Test
   public void testGetPatternFromConfig() {
      String patternFromConfig = "adashdsahfafha";
      when(config.get(configKey)).thenReturn(patternFromConfig);

      String actual = getValidator().getPatternFromConfig(config);
      assertEquals(patternFromConfig, actual);

      verify(config).get(configKey);
   }

}
