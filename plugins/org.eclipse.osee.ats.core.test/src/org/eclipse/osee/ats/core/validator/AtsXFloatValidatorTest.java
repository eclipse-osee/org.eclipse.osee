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
import org.eclipse.osee.ats.core.validator.AtsXFloatValidator;
import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.validator.WidgetStatus;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionFloatMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXFloatValidatorTest {

   @org.junit.Test
   public void testValidateTransition() throws OseeCoreException {
      AtsXFloatValidator validator = new AtsXFloatValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setWorkPageType(WorkPageType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setWorkPageType(WorkPageType.Working);

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

      WidgetDefinitionFloatMinMaxConstraint constraint = new WidgetDefinitionFloatMinMaxConstraint("0.0", "0.0");

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("XFloatDam");
      widgetDef.getConstraints().add(constraint);

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setWorkPageType(WorkPageType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setWorkPageType(WorkPageType.Working);

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
   }

}
