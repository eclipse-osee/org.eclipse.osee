/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
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
   IAtsServices atsServices;
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
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("Widget Name");
      widgetDef.setXWidgetName("XTestWidget");
      AtsXWidgetValidateManager.validateTransition(workItem, results, ValidatorTestUtil.emptyValueProvider, widgetDef,
         null, null, atsServices);
      Assert.assertFalse(results.isEmpty());
      Assert.assertEquals(results.iterator().next().getStatus(), WidgetStatus.Exception);
      Assert.assertEquals(
         "Exception - Widget Name - Exception retriving validation for widget [AtsExceptionValidator] Exception [problem]",
         results.iterator().next().toString());
      Assert.assertTrue(results.iterator().next().getException() instanceof OseeStateException);
      manager.removeWidgetValidatorProvider(provider);
   }

   private class AtsValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
         return new WidgetResult(WidgetStatus.Valid, null, "Here it is");
      }

   }
   private class AtsErrorValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, null, "Here it is");
      }

   }
   private class AtsExceptionValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
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
