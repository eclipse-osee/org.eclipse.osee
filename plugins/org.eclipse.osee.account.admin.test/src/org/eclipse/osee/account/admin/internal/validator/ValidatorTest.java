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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.logger.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * Test Case for {@link Validator}
 * 
 * @author Roberto E. Escobar
 */
public class ValidatorTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private FieldValidator delegate1;
   @Mock private FieldValidator delegate2;
   @Mock private AbstractConfigurableValidator configurable;
   @Mock private Map<String, Object> config;
   // @formatter:on

   private static final AccountField FIELD_1 = AccountField.EMAIL;
   private static final AccountField FIELD_2 = AccountField.USERNAME;
   private static final AccountField FIELD_3 = AccountField.DISPLAY_NAME;
   private static final AccountField OTHER = AccountField.UNKNOWN;

   private static final String TEST_VALUE = "asdadsas";

   private Validator validator;;

   @Before
   public void testSetup() {
      initMocks(this);

      Map<AccountField, FieldValidator> delegates = new HashMap<>();

      delegates.put(FIELD_1, delegate1);
      delegates.put(FIELD_2, delegate2);
      delegates.put(FIELD_3, configurable);

      validator = new Validator(logger, delegates);

      when(delegate1.getFieldType()).thenReturn(FIELD_1);
      when(delegate1.isValid(TEST_VALUE)).thenReturn(true);
      when(delegate1.getPriority()).thenReturn(4);

      when(delegate2.getFieldType()).thenReturn(FIELD_2);
      when(delegate2.isValid(TEST_VALUE)).thenReturn(false);
      when(delegate2.getPriority()).thenReturn(2);

      when(configurable.getFieldType()).thenReturn(FIELD_3);
      when(configurable.getPriority()).thenReturn(3);
   }

   @Test
   public void testGetValidator() {
      FieldValidator actual = validator.getValidator(FIELD_1);
      assertEquals(delegate1, actual);

      actual = validator.getValidator(FIELD_2);
      assertEquals(delegate2, actual);

      actual = validator.getValidator(OTHER);
      assertEquals(Validator.DEFAULT_VALIDATOR, actual);
   }

   @Test
   public void testIsValid() {
      boolean actual = validator.isValid(FIELD_1, TEST_VALUE);
      assertEquals(true, actual);

      verify(delegate1).isValid(TEST_VALUE);
      verify(delegate2, times(0)).isValid(TEST_VALUE);
   }

   @Test
   public void testIsNotValid() {
      boolean actual = validator.isValid(FIELD_2, TEST_VALUE);
      assertEquals(false, actual);

      verify(delegate1, times(0)).isValid(TEST_VALUE);
      verify(delegate2).isValid(TEST_VALUE);
   }

   @Test
   public void testValidate() {
      validator.validate(FIELD_1, TEST_VALUE);

      verify(delegate1).validate(TEST_VALUE);
      verify(delegate2, times(0)).validate(TEST_VALUE);
   }

   @Test
   public void testConfigure() {
      String configurableName = "MyName";
      String customPattern = "\\w+";
      when(configurable.getPatternFromConfig(config)).thenReturn(customPattern);
      when(configurable.getName()).thenReturn(configurableName);

      InOrder inOrder = inOrder(logger, configurable);

      validator.configure(config);

      inOrder.verify(logger).info("Start Validator Config Update...");
      inOrder.verify(configurable).setCustomPattern(any(Pattern.class));
      inOrder.verify(logger).info("Configured validator [%s] with [%s]", configurableName, customPattern);
      inOrder.verify(logger).info("Completed Validator Config Update");
   }

   @Test
   public void testFailConfigure() {
      String configurableName = "MyName";
      String customPattern = "[\\w+";
      when(configurable.getPatternFromConfig(config)).thenReturn(customPattern);
      when(configurable.getName()).thenReturn(configurableName);

      InOrder inOrder = inOrder(logger, configurable);

      validator.configure(config);

      inOrder.verify(logger).info("Start Validator Config Update...");
      inOrder.verify(logger).error(any(Throwable.class),
         eq("Error configuring validator [%s] - custom pattern[%s] was invalid."), eq(configurableName),
         eq(customPattern));
      inOrder.verify(logger).info("Completed Validator Config Update");

      verify(configurable, times(0)).setCustomPattern(Matchers.<Pattern> any());
   }

   @Test
   public void testOrdered() {
      Iterable<FieldValidator> ordered = validator.getOrdered();
      Iterator<FieldValidator> it = ordered.iterator();

      assertEquals(delegate2, it.next());
      assertEquals(configurable, it.next());
      assertEquals(delegate1, it.next());
      assertEquals(Validator.DEFAULT_VALIDATOR, it.next());
   }

   @Test
   public void testGuessFomatTypeNoMatch() {
      when(delegate1.isValid(TEST_VALUE)).thenReturn(false);

      AccountField actual = validator.guessFormatType(TEST_VALUE);
      assertEquals(Validator.DEFAULT_VALIDATOR.getFieldType(), actual);
   }

   @Test
   public void testGuessFomatTypeOneMatch() {
      AccountField actual = validator.guessFormatType(TEST_VALUE);
      assertEquals(delegate1.getFieldType(), actual);
   }

   @Test
   public void testGuessFomatTypeTwoMatchPriorityWins() {
      when(configurable.isValid(TEST_VALUE)).thenReturn(true);

      AccountField actual = validator.guessFormatType(TEST_VALUE);
      assertEquals(configurable.getFieldType(), actual);
   }
}
