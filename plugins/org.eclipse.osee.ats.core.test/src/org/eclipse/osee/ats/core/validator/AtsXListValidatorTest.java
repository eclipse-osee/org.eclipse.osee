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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.ats.mocks.MockValueProvider;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
import org.junit.Assert;
import org.mockito.Mock;

/**
 * @author Donald G. Dunne
 */
public class AtsXListValidatorTest {
   private IAtsServices atsServices;
   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @org.junit.Test
   public void testValidateTransition()  {
      AtsXListValidator validator = new AtsXListValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XListDam");

      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_MinMaxConstraint()  {
      AtsXListValidator validator = new AtsXListValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("XListDam");
      widgetDef.setConstraint(0, 0);

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid is nothing entered
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      //Invalid_Range if select more than supposed to
      widgetDef.setConstraint(0, 2);
      MockValueProvider provider = new MockValueProvider(Arrays.asList("this", "is", "selected"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Invalid_Range if less than supposed to
      widgetDef.setConstraint(2, 2);
      provider = new MockValueProvider(Arrays.asList("this"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Valid if less what supposed to
      widgetDef.setConstraint(2, 2);
      provider = new MockValueProvider(Arrays.asList("this", "is"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);
   }
}
