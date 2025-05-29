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

import static org.mockito.MockitoAnnotations.initMocks;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test unit for {@link AtsXComboBooleanValidator}
 *
 * @author Donald G. Dunne
 */
public class AtsXComboBooleanValidatorTest {

   private AtsXComboBooleanValidator validator;
   private AtsApi atsServices;

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @Before
   public void setUp() {
      initMocks(this);

      validator = new AtsXComboBooleanValidator();

   }

   @Test
   public void testValidateTransition() {
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

      widgetDef.setXWidgetName("XComboBooleanDam");

      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.RFT);

      // Not valid if widgetDef required and no values set
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());

      // Check for "true" value
      StringValueProvider valueProvider = new StringValueProvider(Arrays.asList("true"));
      result = validator.validateTransition(workItem, valueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());

      // Check for "false" value
      valueProvider = new StringValueProvider(Arrays.asList("false"));
      result = validator.validateTransition(workItem, valueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());

      // Check for "junk" value
      valueProvider = new StringValueProvider(Arrays.asList("junk"));
      result = validator.validateTransition(workItem, valueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Range, result.getStatus());
   }

}
