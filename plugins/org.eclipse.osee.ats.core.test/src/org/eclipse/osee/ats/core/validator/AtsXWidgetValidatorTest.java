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

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.ats.mocks.MockValueProvider;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Assert;

/**
 * Test unit for {@link AtsXWidgetValidator}
 *
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidatorTest {

   private static AtsXWidgetValidator validator = new AtsXWidgetValidator() {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider valueProvider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
         return null;
      }
   };

   @org.junit.Test
   public void testIsTransitionToComplete() {
      MockStateDefinition stateDef = new MockStateDefinition("test state");
      stateDef.setStateType(StateType.Working);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
      stateDef.setStateType(StateType.Completed);
      Assert.assertTrue(validator.isTransitionToComplete(stateDef));
      stateDef.setStateType(StateType.Cancelled);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
   }

   @org.junit.Test
   public void testIsRequiredForTransition() {
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForTransition(widgetDef));

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      Assert.assertTrue(validator.isRequiredForTransition(widgetDef));
   }

   @org.junit.Test
   public void testIsRequiredForCompleted() {
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForCompletion(widgetDef));

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);
      Assert.assertTrue(validator.isRequiredForCompletion(widgetDef));
   }

   @org.junit.Test
   public void testIsEmptyValue() {
      Assert.assertTrue(validator.isEmptyValue(ValidatorTestUtil.emptyValueProvider));

      Assert.assertFalse(validator.isEmptyValue(new MockValueProvider(Arrays.asList("hello"))));
   }

   @org.junit.Test
   public void testValidateWidgetIsRequired() {
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // widget required_for_transition, state is working state returns incomplete state and details
      WidgetResult result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      toStateDef.setName("completed");
      toStateDef.setStateType(StateType.Completed);

      // widget required_for_transition, state is completed state, returns incomplete state and details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      // change widget to required_for_completed, turn off required_for_transition
      widgetDef.getOptions().add(WidgetOption.NOT_REQUIRED_FOR_TRANSITION);
      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);

      toStateDef.setName("working state");
      toStateDef.setStateType(StateType.Working);

      // widget required_for_completed, state is working state returns Valid state and no details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      toStateDef.setName("completed");
      toStateDef.setStateType(StateType.Completed);

      // widget required_for_completed, state is completed state, returns incomplete state and details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));
   }

   @org.junit.Test
   public void testIsValidDate() {
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");

      WidgetResult result = validator.isValidDate(new MockDateValueProvider(Arrays.asList(new Date())), widgetDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

   }

}
