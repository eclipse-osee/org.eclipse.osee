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
import org.eclipse.osee.ats.workdef.api.WidgetOption;
import org.eclipse.osee.ats.workdef.api.WidgetResult;
import org.eclipse.osee.ats.workdef.api.WidgetStatus;
import org.eclipse.osee.ats.workdef.api.StateType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Test unit for {@link AtsXComboBooleanValidator}
 * 
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidatorTest {

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXComboBooleanValidator validator = new AtsXComboBooleanValidator();

      IAtsWidgetDefinition widgetDef = AtsWorkDefinitionService.getService().createWidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      IAtsStateDefinition fromStateDef = AtsWorkDefinitionService.getService().createStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      IAtsStateDefinition toStateDef = AtsWorkDefinitionService.getService().createStateDefinition("to");
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

      // Check for "yes" value
      MockValueProvider valueProvider = new MockValueProvider(Arrays.asList("yes"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "no" value
      valueProvider = new MockValueProvider(Arrays.asList("no"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Check for "junk" value
      valueProvider = new MockValueProvider(Arrays.asList("junk"));
      result = validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

   }
}
