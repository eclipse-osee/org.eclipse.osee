/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.core.validator.AtsXComboBooleanValidator.IAllowedBooleanValueProvider;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test unit for {@link AtsXComboBooleanValidator}
 * 
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidatorTest {

   private static final List<String> TRUE_FALSE_VALUES = Arrays.asList("true", "false");
   private static final List<String> YES_NO_VALUES = Arrays.asList("yes", "no");

   //@formatter:off
   @Mock private IAllowedBooleanValueProvider valueProvider;
   //@formatter:on

   private AtsXComboBooleanValidator validator;

   @Before
   public void setUp() {
      initMocks(this);

      validator = new AtsXComboBooleanValidator(valueProvider);
   }

   @Test
   public void testValidateTransition() throws OseeCoreException {
      when(valueProvider.getValues()).thenReturn(TRUE_FALSE_VALUES);

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XComboBooleanDam");

      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());

      // Check for "true" value
      MockValueProvider valueProvider = new MockValueProvider(Arrays.asList("true"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "false" value
      valueProvider = new MockValueProvider(Arrays.asList("false"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "junk" value
      valueProvider = new MockValueProvider(Arrays.asList("junk"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
   }

   @Test
   public void testValidateTransitionYesNo() throws OseeCoreException {
      when(valueProvider.getValues()).thenReturn(YES_NO_VALUES);

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XComboBooleanDam");

      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());

      // Check for "true" value
      MockValueProvider valueProvider = new MockValueProvider(Arrays.asList("yes"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "false" value
      valueProvider = new MockValueProvider(Arrays.asList("no"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "junk" value
      valueProvider = new MockValueProvider(Arrays.asList("junk"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
   }
}
