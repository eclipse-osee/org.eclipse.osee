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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow.review.defect;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.workflow.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.workflow.review.defect.ReviewDefectError;
import org.junit.Assert;
import org.mockito.Mock;

/**
 * Test unit for {@link AtsXDefectValidator}
 *
 * @author Donald G. Dunne
 */
public class AtsXDefectValidatorTest {
   private AtsApi atsServices;

   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @org.junit.Test
   public void testValidateTransition() {
      AtsXDefectValidator validator = new AtsXDefectValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      // Valid for anything not XDefectViewer
      WidgetResult result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef,
         fromStateDef, toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.setXWidgetName("XDefectViewer");

      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      ValidatorTestUtil.assertValidResult(result);

      widgetDef.getOptions().add(WidgetOption.REQUIRED_FOR_TRANSITION);

      // Reviews do not require Defects to be entered, even if required for transition
      result = validator.validateTransition(workItem, ValidatorTestUtil.emptyValueProvider, widgetDef, fromStateDef,
         toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition__Defect() {
      AtsXDefectValidator validator = new AtsXDefectValidator();

      MockWidgetDefinition widgetDef = new MockWidgetDefinition("test");
      widgetDef.setXWidgetName("XDefectViewer");

      MockStateDefinition fromStateDef = new MockStateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      MockStateDefinition toStateDef = new MockStateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      ReviewDefectItem item = getValidItem();
      MockDefectValueProvider itemValueProvider = new MockDefectValueProvider(Arrays.asList(item));

      // Valid defect
      WidgetResult result =
         validator.validateTransition(workItem, itemValueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());

      // Invalid Severity
      item = getValidItem();
      item.setSeverity(Severity.None);
      itemValueProvider = new MockDefectValueProvider(Arrays.asList(item));
      result =
         validator.validateTransition(workItem, itemValueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(ReviewDefectError.AllItemsMustBeMarkedAndClosed.getError(), result.getDetails());

      // Invalid Disposition
      item = getValidItem();
      item.setDisposition(Disposition.None);
      itemValueProvider = new MockDefectValueProvider(Arrays.asList(item));
      result =
         validator.validateTransition(workItem, itemValueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(ReviewDefectError.AllItemsMustBeMarkedAndClosed.getError(), result.getDetails());

      // Invalid closed
      item = getValidItem();
      item.setClosed(false);
      itemValueProvider = new MockDefectValueProvider(Arrays.asList(item));
      result =
         validator.validateTransition(workItem, itemValueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Invalid_Incompleted, result.getStatus());
      Assert.assertEquals(ReviewDefectError.AllItemsMustBeMarkedAndClosed.getError(), result.getDetails());

   }

   private ReviewDefectItem getValidItem() {
      Date date = new Date();
      String userId = "1234";
      ReviewDefectItem item =
         new ReviewDefectItem(userId, Severity.Issue, Disposition.Accept, InjectionActivity.Software_Design,
            "this is the description", "this is the resolution", "this is the location", date);
      item.setClosed(true);
      return item;
   }
}
