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
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.SimpleWidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class AtsXIntegerValidatorTest {

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXIntegerValidator validator = new AtsXIntegerValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XIntegerDam");

      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_MinMaxConstraint() throws OseeCoreException {
      AtsXIntegerValidator validator = new AtsXIntegerValidator();

      IAtsWidgetDefinitionIntMinMaxConstraint constraint = new SimpleWidgetDefinitionIntMinMaxConstraint("0", "0");

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("XIntegerDam");
      widgetDef.getConstraints().add(constraint);

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid is nothing entered
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      //Invalid_Range if > than what should be
      constraint.set(0, 2);
      MockValueProvider provider = new MockValueProvider(Arrays.asList("0", "2", "3"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Invalid_Range if less than supposed to
      constraint.set(1, 2);
      provider = new MockValueProvider(Arrays.asList("0"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Valid if == what supposed to be
      constraint.set(2, 2);
      provider = new MockValueProvider(Arrays.asList("2", "2"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      // test nulls
      constraint = new SimpleWidgetDefinitionIntMinMaxConstraint((String) null, null);
      Assert.assertEquals(null, constraint.getMinValue());
      Assert.assertEquals(null, constraint.getMaxValue());

   }

}
