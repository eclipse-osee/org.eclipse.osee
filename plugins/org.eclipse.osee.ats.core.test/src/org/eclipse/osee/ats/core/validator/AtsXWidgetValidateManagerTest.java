/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test case for {@link AtsXWidgetValidateManager}
 *
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManagerTest {
   AtsApi atsServices;
   // @formatter:off
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @Test
   public void testValidateTransition_emptyValidators() {
      List<WidgetResult> results = new LinkedList<>();
      AtsXWidgetValidateManager.getProviders().clear();

      AtsXWidgetValidateManager.validateTransition(workItem, results, ValidatorTestUtil.emptyValueProvider, null, null,
         null, atsServices);
      Assert.assertTrue(results.isEmpty());
   }

   @Test
   public void testValidateTransition_validValidators() {
      List<WidgetResult> results = new LinkedList<>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();
      AtsXWidgetValidateManager.getProviders().clear();

      TestValidatorProvider provider = new TestValidatorProvider(new AtsValidator());
      manager.addWidgetValidatorProvider(provider);
      AtsXWidgetValidateManager.validateTransition(workItem, results, ValidatorTestUtil.emptyValueProvider, null, null,
         null, atsServices);
      Assert.assertTrue(results.isEmpty());
      manager.removeWidgetValidatorProvider(provider);
   }

   @Test
   public void testValidateTransition_inValidValidators() {
      List<WidgetResult> results = new LinkedList<>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();

      TestValidatorProvider provider = new TestValidatorProvider(new AtsErrorValidator());
      manager.addWidgetValidatorProvider(provider);
      AtsXWidgetValidateManager.validateTransition(workItem, results, ValidatorTestUtil.emptyValueProvider, null, null,
         null, atsServices);
      Assert.assertFalse(results.isEmpty());
      manager.removeWidgetValidatorProvider(provider);
   }

   @Test
   public void testValidateTransition_exceptionValidators() {
      List<WidgetResult> results = new LinkedList<>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();

      TestValidatorProvider provider = new TestValidatorProvider(new AtsExceptionValidator());
      manager.addWidgetValidatorProvider(provider);
      WidgetDefinition widgetDef = new WidgetDefinition("Widget Name");
      widgetDef.setXWidgetName("XTestWidget");
      AtsXWidgetValidateManager.validateTransition(workItem, results, ValidatorTestUtil.emptyValueProvider, widgetDef,
         null, null, atsServices);
      Assert.assertFalse(results.isEmpty());
      Assert.assertEquals(results.iterator().next().getStatus(), WidgetStatus.Exception);
      Assert.assertEquals(
         "Exception - Exception retrieving validation for widget [AtsExceptionValidator] Exception [problem]",
         results.iterator().next().toString());
      Assert.assertTrue(results.iterator().next().getException().contains("OseeStateException"));
      manager.removeWidgetValidatorProvider(provider);
   }

   private class AtsValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices) {
         return new WidgetResult(WidgetStatus.Success, "Here it is");
      }

   }
   private class AtsErrorValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, "Here it is");
      }

   }
   private class AtsExceptionValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices) {
         throw new OseeStateException("problem");
      }

   }

   private class TestValidatorProvider implements IAtsXWidgetValidatorProvider {

      private final LinkedList<IAtsXWidgetValidator> validators;

      public TestValidatorProvider(IAtsXWidgetValidator validator) {
         validators = new LinkedList<>();
         validators.add(validator);
      }

      @Override
      public Collection<IAtsXWidgetValidator> getValidators() {
         return validators;
      }

   }
}
