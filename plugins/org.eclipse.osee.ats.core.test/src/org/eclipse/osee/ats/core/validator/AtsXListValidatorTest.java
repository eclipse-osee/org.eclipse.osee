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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinitionListMinMaxSelectedConstraint;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.SimpleWidgetDefinitionListMinMaxSelectedConstraint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class AtsXListValidatorTest {
   private IAtsServices atsServices;

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXListValidator validator = new AtsXListValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef,
            atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XListDam");

      result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef,
            atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef,
            atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_MinMaxConstraint() throws OseeCoreException {
      AtsXListValidator validator = new AtsXListValidator();

      IAtsWidgetDefinitionListMinMaxSelectedConstraint constraint =
         new SimpleWidgetDefinitionListMinMaxSelectedConstraint("0", "0");

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("XListDam");
      widgetDef.getConstraints().add(constraint);

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid is nothing entered
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef,
            atsServices);
      ValidatorTestUtil.assertValidResult(result);

      //Invalid_Range if select more than supposed to
      constraint.set(0, 2);
      MockValueProvider provider = new MockValueProvider(Arrays.asList("this", "is", "selected"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Invalid_Range if less than supposed to
      constraint.set(2, 2);
      provider = new MockValueProvider(Arrays.asList("this"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Valid if less what supposed to
      constraint.set(2, 2);
      provider = new MockValueProvider(Arrays.asList("this", "is"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      // test nulls
      constraint = new SimpleWidgetDefinitionListMinMaxSelectedConstraint((String) null, null);
      Assert.assertEquals(null, constraint.getMinSelected());
      Assert.assertEquals(null, constraint.getMaxSelected());

   }

}
