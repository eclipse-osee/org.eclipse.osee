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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.util.StringValueProvider;
import org.junit.Assert;
import org.mockito.Mock;

/**
 * @author Donald G. Dunne
 */
public class AtsXListValidatorTest {
   private AtsApi atsServices;
   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @org.junit.Test
   public void testValidateTransition() {
      AtsXListValidator validator = new AtsXListValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("xLabel");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XListDam");

      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.RFT);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_MinMaxConstraint() {
      AtsXListValidator validator = new AtsXListValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("XListDam");
      widgetDef.setConstraint(0, 0);

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid is nothing entered
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      //Invalid_Range if select more than supposed to
      widgetDef.setConstraint(0, 2);
      StringValueProvider provider = new StringValueProvider(Arrays.asList("this", "is", "selected"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Invalid_Range if less than supposed to
      widgetDef.setConstraint(2, 2);
      provider = new StringValueProvider(Arrays.asList("this"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());

      //Valid if less what supposed to
      widgetDef.setConstraint(2, 2);
      provider = new StringValueProvider(Arrays.asList("this", "is"));
      result = validator.validateTransition(workItem, provider, widgetDef, fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);
   }
}
