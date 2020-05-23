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
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link AbstractValidator}
 * 
 * @author Roberto E. Escobar
 */
public abstract class AbstractValidatorTest<T extends FieldValidator> {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private final AccountField fieldType;
   private final String validValue;
   private final String invalidValue;

   public AbstractValidatorTest(AccountField fieldType, String validValue, String invalidValue) {
      super();
      this.fieldType = fieldType;
      this.validValue = validValue;
      this.invalidValue = invalidValue;
   }

   private T validator;

   @Before
   public void testSetup() {
      initMocks(this);

      validator = createValidator();
   }

   protected AccountField getFieldType() {
      return fieldType;
   }

   protected String getExpectedName() {
      return fieldType.name();
   }

   protected String getValidValue() {
      return validValue;
   }

   protected String getInvalidValue() {
      return invalidValue;
   }

   protected abstract T createValidator();

   protected T getValidator() {
      return validator;
   }

   @Test
   public void testGetPriority() {
      int actual = getValidator().getPriority();
      assertEquals(getFieldType().ordinal(), actual);
   }

   @Test
   public void testFieldType() {
      AccountField actual = getValidator().getFieldType();
      assertEquals(getFieldType(), actual);
   }

   @Test
   public void testName() {
      String actual = getValidator().getName();
      assertEquals(getExpectedName(), actual);
   }

   @Test
   public void testValidateWithNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(getExpectedName() + " cannot be null");
      getValidator().validate(null);
   }

   @Test
   public void testValidateWithEmpty() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(getExpectedName() + " cannot be empty");
      getValidator().validate("");
   }

   @Test
   public void testValidate() {
      getValidator().validate(getValidValue());
   }

   @Test
   public void testIsValid() {
      boolean status = getValidator().isValid(getValidValue());
      assertTrue(status);
   }

   @Test
   public void testIsNotValid() {
      boolean status = getValidator().isValid(getInvalidValue());
      assertFalse(status);
   }

}
