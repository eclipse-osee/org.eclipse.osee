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
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.validator.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionFloatMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionListMinMaxSelectedConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
      public WidgetResult validateTransition(IValueProvider valueProvider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) {
         return null;
      }
   };

   @org.junit.Test
   public void testIsTransitionToComplete() {
      StateDefinition stateDef = new StateDefinition("test state");
      stateDef.setWorkPageType(WorkPageType.Working);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
      stateDef.setWorkPageType(WorkPageType.Completed);
      Assert.assertTrue(validator.isTransitionToComplete(stateDef));
      stateDef.setWorkPageType(WorkPageType.Cancelled);
      Assert.assertFalse(validator.isTransitionToComplete(stateDef));
   }

   @org.junit.Test
   public void testIsRequiredForTransition() {
      WidgetDefinition widgetDef = new WidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForTransition(widgetDef));

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);
      Assert.assertTrue(validator.isRequiredForTransition(widgetDef));
   }

   @org.junit.Test
   public void testIsRequiredForCompleted() {
      WidgetDefinition widgetDef = new WidgetDefinition("test widget");
      Assert.assertFalse(validator.isRequiredForCompletion(widgetDef));

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);
      Assert.assertTrue(validator.isRequiredForCompletion(widgetDef));
   }

   @org.junit.Test
   public void testIsEmptyValue() throws OseeCoreException {
      Assert.assertTrue(validator.isEmptyValue(ValidatorTestUtil.emptyValueProvider));

      Assert.assertFalse(validator.isEmptyValue(new MockValueProvider(Arrays.asList("hello"))));
   }

   @org.junit.Test
   public void testValidateWidgetIsRequired() throws OseeCoreException {
      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setWorkPageType(WorkPageType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setWorkPageType(WorkPageType.Working);

      // widget required_for_transition, state is working state returns incomplete state and details
      WidgetResult result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      toStateDef.setName("completed");
      toStateDef.setWorkPageType(WorkPageType.Completed);

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
      toStateDef.setWorkPageType(WorkPageType.Working);

      // widget required_for_completed, state is working state returns Valid state and no details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      toStateDef.setName("completed");
      toStateDef.setWorkPageType(WorkPageType.Completed);

      // widget required_for_completed, state is completed state, returns incomplete state and details
      result =
         validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));
   }

   @org.junit.Test
   public void testIsInteger() {
      Assert.assertFalse(validator.isInteger("asf"));

      Assert.assertFalse(validator.isInteger("4.5"));

      Assert.assertTrue(validator.isInteger("345"));
   }

   @org.junit.Test
   public void testGetInteger() {
      Assert.assertEquals(null, validator.getInteger("asf"));

      Assert.assertEquals(null, validator.getInteger("4.5"));

      Assert.assertEquals(new Integer(345), validator.getInteger("345"));
   }

   @org.junit.Test
   public void testGetConstraintOfType() {
      WidgetDefinition widgetDef = new WidgetDefinition("test");

      Assert.assertNull(validator.getConstraintOfType(widgetDef, WidgetDefinitionIntMinMaxConstraint.class));

      WidgetDefinitionIntMinMaxConstraint constraint = new WidgetDefinitionIntMinMaxConstraint(34, 45);
      widgetDef.getConstraints().add(constraint);

      Assert.assertEquals(constraint,
         validator.getConstraintOfType(widgetDef, WidgetDefinitionIntMinMaxConstraint.class));
   }

   @org.junit.Test
   public void testGetIntMinMaxValueSet() {

      WidgetDefinition widgetDef = new WidgetDefinition("test");

      Assert.assertNull(validator.getConstraintOfType(widgetDef, WidgetDefinitionIntMinMaxConstraint.class));
      Assert.assertEquals(null, validator.getIntMinValueSet(widgetDef));
      Assert.assertEquals(null, validator.getIntMaxValueSet(widgetDef));

      WidgetDefinitionIntMinMaxConstraint constraint = new WidgetDefinitionIntMinMaxConstraint(34, 45);
      widgetDef.getConstraints().add(constraint);

      Assert.assertEquals(new Integer(34), validator.getIntMinValueSet(widgetDef));
      Assert.assertEquals(new Integer(45), validator.getIntMaxValueSet(widgetDef));
   }

   @org.junit.Test
   public void testIsValidInteger() throws OseeCoreException {
      WidgetDefinition widgetDef = new WidgetDefinition("test");

      WidgetResult result = validator.isValidInteger(new MockValueProvider(Arrays.asList("asdf", "345")), widgetDef);
      Assert.assertEquals(WidgetStatus.Invalid_Type, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      result = validator.isValidInteger(new MockValueProvider(Arrays.asList("23", "345")), widgetDef);
      ValidatorTestUtil.assertValidResult(result);

      WidgetDefinitionIntMinMaxConstraint constraint = new WidgetDefinitionIntMinMaxConstraint(34, 45);
      widgetDef.getConstraints().add(constraint);
      result = validator.isValidInteger(new MockValueProvider(Arrays.asList("37", "42")), widgetDef);
      ValidatorTestUtil.assertValidResult(result);

      result = validator.isValidInteger(new MockValueProvider(Arrays.asList("12", "42")), widgetDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));
   }

   @org.junit.Test
   public void testIsFloat() {
      Assert.assertFalse(validator.isFloat("asf"));

      Assert.assertFalse(validator.isFloat("4a"));

      Assert.assertTrue(validator.isFloat("345.0"));
   }

   @org.junit.Test
   public void testGetFloat() {
      Assert.assertEquals(null, validator.getFloat("asf"));

      Assert.assertEquals(null, validator.getFloat("4a"));

      Assert.assertEquals(new Double(345.0), validator.getFloat("345.0"));
   }

   @org.junit.Test
   public void testGetFloatMinMaxValueSet() {

      WidgetDefinition widgetDef = new WidgetDefinition("test");

      Assert.assertNull(validator.getConstraintOfType(widgetDef, WidgetDefinitionFloatMinMaxConstraint.class));
      Assert.assertEquals(null, validator.getFloatMinValueSet(widgetDef));
      Assert.assertEquals(null, validator.getFloatMaxValueSet(widgetDef));

      WidgetDefinitionFloatMinMaxConstraint constraint = new WidgetDefinitionFloatMinMaxConstraint(34.3, 45.5);
      widgetDef.getConstraints().add(constraint);

      Assert.assertEquals(new Double(34.3), validator.getFloatMinValueSet(widgetDef));
      Assert.assertEquals(new Double(45.5), validator.getFloatMaxValueSet(widgetDef));
   }

   @org.junit.Test
   public void testIsValidFloat() throws OseeCoreException {
      WidgetDefinition widgetDef = new WidgetDefinition("test");

      WidgetResult result = validator.isValidFloat(new MockValueProvider(Arrays.asList("asdf", "345.0")), widgetDef);
      Assert.assertEquals(WidgetStatus.Invalid_Type, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      result = validator.isValidFloat(new MockValueProvider(Arrays.asList("23.2", "345.0")), widgetDef);
      ValidatorTestUtil.assertValidResult(result);

      WidgetDefinitionFloatMinMaxConstraint constraint = new WidgetDefinitionFloatMinMaxConstraint(34.3, 45.5);
      widgetDef.getConstraints().add(constraint);
      result = validator.isValidFloat(new MockValueProvider(Arrays.asList("37.3", "42.1")), widgetDef);
      ValidatorTestUtil.assertValidResult(result);

      result = validator.isValidFloat(new MockValueProvider(Arrays.asList("12.0", "42.1")), widgetDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));
   }

   @org.junit.Test
   public void testIsValidDate() throws OseeCoreException {
      WidgetDefinition widgetDef = new WidgetDefinition("test");

      WidgetResult result = validator.isValidDate(new MockDateValueProvider(Arrays.asList(new Date())), widgetDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

   }

   @org.junit.Test
   public void testGetListMinMaxValueSelected() {

      WidgetDefinition widgetDef = new WidgetDefinition("test");

      Assert.assertNull(validator.getConstraintOfType(widgetDef, WidgetDefinitionListMinMaxSelectedConstraint.class));
      Assert.assertEquals(null, validator.getListMinSelected(widgetDef));
      Assert.assertEquals(null, validator.getListMaxSelected(widgetDef));

      WidgetDefinitionListMinMaxSelectedConstraint constraint = new WidgetDefinitionListMinMaxSelectedConstraint(2, 4);
      widgetDef.getConstraints().add(constraint);

      Assert.assertEquals(new Integer(2), validator.getListMinSelected(widgetDef));
      Assert.assertEquals(new Integer(4), validator.getListMaxSelected(widgetDef));
   }

}
