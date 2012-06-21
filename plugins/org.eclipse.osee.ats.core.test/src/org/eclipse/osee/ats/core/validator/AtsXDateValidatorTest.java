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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * @author Donald G. Dunne
 */
public class AtsXDateValidatorTest {

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXDateValidator validator = new AtsXDateValidator();

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

      widgetDef.setXWidgetName("XDateDam");

      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_validDate() throws OseeCoreException {
      AtsXDateValidator validator = new AtsXDateValidator();

      IAtsWidgetDefinition widgetDef = AtsWorkDefinitionService.getService().createWidgetDefinition("test");
      widgetDef.setXWidgetName("XDateDam");

      IAtsStateDefinition fromStateDef = AtsWorkDefinitionService.getService().createStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      IAtsStateDefinition toStateDef = AtsWorkDefinitionService.getService().createStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      MockDateValueProvider dateProvider = new MockDateValueProvider(Arrays.asList(new Date()));

      // Valid for valid date
      WidgetResult result = validator.validateTransition(dateProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition_validRange() throws OseeCoreException {
      AtsXDateValidator validator = new AtsXDateValidator();

      IAtsWidgetDefinition widgetDef = AtsWorkDefinitionService.getService().createWidgetDefinition("test");
      widgetDef.setXWidgetName("XDateDam");
      widgetDef.getOptions().add(WidgetOption.FUTURE_DATE_REQUIRED);

      IAtsStateDefinition fromStateDef = AtsWorkDefinitionService.getService().createStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      IAtsStateDefinition toStateDef = AtsWorkDefinitionService.getService().createStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      Calendar cal = new GregorianCalendar(2010, 02, 05);
      Date pastDate = cal.getTime();
      MockDateValueProvider dateProvider = new MockDateValueProvider(Arrays.asList(pastDate, new Date()));

      // Not valid for pastDate
      WidgetResult result = validator.validateTransition(dateProvider, widgetDef, fromStateDef, toStateDef);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
   }
}
