/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.core.validator;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.util.StringValueProvider;
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
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider valueProvider,
         WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices) {
         return null;
      }
   };

   @org.junit.Test
   public void testIsTransitionToComplete() {
      StateDefinition stateDef = new StateDefinition("test state");
      stateDef.setStateType(StateType.Working);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
      stateDef.setStateType(StateType.Completed);
      Assert.assertTrue(validator.isTransitionToComplete(stateDef));
      stateDef.setStateType(StateType.Cancelled);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
   }

   @org.junit.Test
   public void testIsRequiredForTransition() {
      WidgetDefinition widgetDef = new WidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForTransition(widgetDef));

      widgetDef.getOptions().add(WidgetOption.RFT);
      Assert.assertTrue(validator.isRequiredForTransition(widgetDef));
   }

   @org.junit.Test
   public void testIsRequiredForCompleted() {
      WidgetDefinition widgetDef = new WidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForCompletion(widgetDef));

      widgetDef.getOptions().add(WidgetOption.RFC);
      Assert.assertTrue(validator.isRequiredForCompletion(widgetDef));
   }

   @org.junit.Test
   public void testIsEmptyValue() {
      Assert.assertTrue(validator.isEmptyValue(ValidatorTestUtil.emptyValueProvider));

      Assert.assertFalse(validator.isEmptyValue(new StringValueProvider(Arrays.asList("hello"))));
   }

   @org.junit.Test
   public void testValidateWidgetIsRequired() {
      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.getOptions().add(WidgetOption.RFT);

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // widget RFT, state is working state returns incomplete state and details
      WidgetResult result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      toStateDef.setName("completed");
      toStateDef.setStateType(StateType.Completed);

      // widget RFT, state is completed state, returns incomplete state and details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      // change widget to required_for_completed, turn off RFT
      widgetDef.getOptions().add(WidgetOption.NOT_RFT);
      widgetDef.getOptions().add(WidgetOption.RFC);

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
      Assert.assertTrue(Strings.isValid(result.getDetails()));
   }

   @org.junit.Test
   public void testIsValidDate() {
      WidgetDefinition widgetDef = new WidgetDefinition("test");

      WidgetResult result = validator.isValidDate(new MockDateValueProvider(Arrays.asList(new Date())), widgetDef);
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());

   }

}
