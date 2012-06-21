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
import junit.framework.Assert;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionService;
import org.eclipse.osee.ats.workdef.api.IAtsStateDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWidgetDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWidgetDefinitionFloatMinMaxConstraint;
import org.eclipse.osee.ats.workdef.api.WidgetOption;
import org.eclipse.osee.ats.workdef.api.WidgetResult;
import org.eclipse.osee.ats.workdef.api.WidgetStatus;
import org.eclipse.osee.ats.workdef.api.StateType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXFloatValidatorTest {

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXFloatValidator validator = new AtsXFloatValidator();

      IAtsWidgetDefinition widgetDef = AtsWorkDefinitionService.getService().createWidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      IAtsStateDefinition fromStateDef = AtsWorkDefinitionService.getService().createStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      IAtsStateDefinition toStateDef = AtsWorkDefinitionService.getService().createStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XFloatDam");

      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_MinMaxConstraint() throws OseeCoreException {
      AtsXFloatValidator validator = new AtsXFloatValidator();

      IAtsWidgetDefinitionFloatMinMaxConstraint constraint =
         AtsWorkDefinitionService.getService().createWidgetDefinitionFloatMinMaxConstraint("0.0", "0.0");

      IAtsWidgetDefinition widgetDef = AtsWorkDefinitionService.getService().createWidgetDefinition("test");
      widgetDef.setXWidgetName("XFloatDam");
      widgetDef.getConstraints().add(constraint);

      IAtsStateDefinition fromStateDef = AtsWorkDefinitionService.getService().createStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      IAtsStateDefinition toStateDef = AtsWorkDefinitionService.getService().createStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid is nothing entered
      WidgetResult result =
         validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      //Invalid_Range if > than what should be
      constraint.set(0.0, 2.0);
      MockValueProvider provider = new MockValueProvider(Arrays.asList("0.0", "2.0", "3.0"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Invalid_Range if less than supposed to
      constraint.set(1.0, 2.0);
      provider = new MockValueProvider(Arrays.asList("0.0"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Valid if == what supposed to be
      constraint.set(2.0, 2.0);
      provider = new MockValueProvider(Arrays.asList("2.0", "2.0"));
      result = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      // test nulls
      constraint = AtsWorkDefinitionService.getService().createWidgetDefinitionFloatMinMaxConstraint((String) null, null);
      Assert.assertEquals(null, constraint.getMinValue());
      Assert.assertEquals(null, constraint.getMaxValue());

   }

}
