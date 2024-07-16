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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.review.ReviewDefectError;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.defect.AtsXDefectValidator;
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

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("xList");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
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
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());
   }

   @org.junit.Test
   public void testValidateTransition__Defect() {
      AtsXDefectValidator validator = new AtsXDefectValidator();

      WidgetDefinition widgetDef = new WidgetDefinition("test");
      widgetDef.setXWidgetName("XDefectViewer");

      StateDefinition fromStateDef = new StateDefinition("from");
      fromStateDef.setStateType(StateType.Working);
      StateDefinition toStateDef = new StateDefinition("to");
      toStateDef.setStateType(StateType.Working);

      ReviewDefectItem item = getValidItem();
      MockDefectValueProvider itemValueProvider = new MockDefectValueProvider(Arrays.asList(item));

      // Valid defect
      WidgetResult result =
         validator.validateTransition(workItem, itemValueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
      Assert.assertEquals(WidgetStatus.Success, result.getStatus());

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
      item.setClosedUserId("");
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
            "this is the description", "this is the resolution", "this is the location", date, "this is the notes");
      item.setClosed(true);
      item.setClosedUserId(AtsApiService.get().getUserService().getCurrentUserId());
      return item;
   }
}
