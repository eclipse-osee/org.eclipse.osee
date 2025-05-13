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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.junit.Assert;
import org.mockito.Mock;

/**
 * @author Donald G. Dunne
 */
public class AtsXDateValidatorTest {
   private AtsApi atsServices;

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @org.junit.Test
   public void testValidateTransition() {
      AtsXDateValidator validator = new AtsXDateValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XIntegerDam
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XDateDam");

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
   public void testValidateTransition_validDate() {
      AtsXDateValidator validator = new AtsXDateValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("XDateDam");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      MockDateValueProvider dateProvider = new MockDateValueProvider(Arrays.asList(new Date()));

      // Valid for valid date
      WidgetResult result =
         validator.validateTransition(workItem, dateProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_validRange() {
      AtsXDateValidator validator = new AtsXDateValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("XDateDam");
      widgetDef.getOptions().add(WidgetOption.FUTURE_DATE_REQUIRED);

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      Calendar cal = new GregorianCalendar(2010, 02, 05);
      Date pastDate = cal.getTime();
      MockDateValueProvider dateProvider = new MockDateValueProvider(Arrays.asList(pastDate, new Date()));

      // Not valid for pastDate
      WidgetResult result =
         validator.validateTransition(workItem, dateProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
   }
}
