/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.Arrays;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.WorkPageType;
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
      public WidgetResult validateTransition(IValueProvider valueProvider, WidgetDefinition widgetDef, StateDefinition toStateDef) {
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
   public void testalidateWidgetIsRequired() throws OseeCoreException {
      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      StateDefinition toStateDef = new StateDefinition("working state");
      toStateDef.setWorkPageType(WorkPageType.Working);

      // widget required_for_transition, state is working state returns incomplete state and details
      WidgetResult result = validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      toStateDef.setName("completed");
      toStateDef.setWorkPageType(WorkPageType.Completed);

      // widget required_for_transition, state is completed state, returns incomplete state and details
      result = validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(widgetDef.getName(), result.getWidgetDef().getName());
      Assert.assertTrue(Strings.isValid(result.getDetails()));

      // change widget to required_for_completed, turn off required_for_transition
      widgetDef.getOptions().add(WidgetOption.NOT_REQUIRED_FOR_TRANSITION);
      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_COMPLETION);

      toStateDef.setName("working state");
      toStateDef.setWorkPageType(WorkPageType.Working);

      // widget required_for_completed, state is working state returns Valid state and no details
      result = validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      toStateDef.setName("completed");
      toStateDef.setWorkPageType(WorkPageType.Completed);

      // widget required_for_completed, state is completed state, returns incomplete state and details
      result = validator.validateWidgetIsRequired(ValidatorTestUtil.emptyValueProvider, widgetDef, toStateDef);
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

}
