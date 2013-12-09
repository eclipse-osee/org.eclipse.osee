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
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AtsXWidgetValidateManager}
 * 
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManagerTest {

   @Test
   public void testValidateTransition_emptyValidators() {
      List<WidgetResult> results = new LinkedList<WidgetResult>();

      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();
      manager.validateTransition(results, ValidatorTestUtil.emptyValueProvider, null, null, null);
      Assert.assertTrue(results.isEmpty());
   }

   @Test
   public void testValidateTransition_validValidators() {
      List<WidgetResult> results = new LinkedList<WidgetResult>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();

      manager.add(new TestValidatorProvider(new AtsValidator()));
      manager.validateTransition(results, ValidatorTestUtil.emptyValueProvider, null, null, null);
      Assert.assertTrue(results.isEmpty());
   }

   @Test
   public void testValidateTransition_inValidValidators() {
      List<WidgetResult> results = new LinkedList<WidgetResult>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();

      manager.add(new TestValidatorProvider(new AtsErrorValidator()));
      manager.validateTransition(results, ValidatorTestUtil.emptyValueProvider, null, null, null);
      Assert.assertFalse(results.isEmpty());
   }

   @Test
   public void testValidateTransition_exceptoinValidators() {
      List<WidgetResult> results = new LinkedList<WidgetResult>();
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();

      manager.add(new TestValidatorProvider(new AtsExceptoinValidator()));
      MockWidgetDefinition widgetDef = new MockWidgetDefinition("Widget Name");
      manager.validateTransition(results, ValidatorTestUtil.emptyValueProvider, widgetDef, null, null);
      Assert.assertFalse(results.isEmpty());
      Assert.assertEquals(results.iterator().next().getStatus(), WidgetStatus.Exception);
      Assert.assertEquals(
         "Exception - Widget Name - Exception retriving validation for widget [AtsExceptoinValidator] Exception [problem]",
         results.iterator().next().toString());
      Assert.assertTrue(results.iterator().next().getException() instanceof OseeStateException);
   }

   @Test
   public void testAdd() {
      AtsXWidgetValidateManager manager = new AtsXWidgetValidateManager();
      manager.add(new TestValidatorProvider(new AtsValidator()));
   }

   private class AtsValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
         return new WidgetResult(WidgetStatus.Valid, null, "Here it is");
      }

   }
   private class AtsErrorValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
         return new WidgetResult(WidgetStatus.Invalid_Incompleted, null, "Here it is");
      }

   }
   private class AtsExceptoinValidator implements IAtsXWidgetValidator {

      @Override
      public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) throws OseeStateException {
         throw new OseeStateException("problem");
      }

   }

   private class TestValidatorProvider implements AtsXWidgetValidatorProvider {

      private final LinkedList<IAtsXWidgetValidator> validators;

      public TestValidatorProvider(IAtsXWidgetValidator validator) {
         validators = new LinkedList<IAtsXWidgetValidator>();
         validators.add(validator);
      }

      @Override
      public Collection<IAtsXWidgetValidator> getValidators() {
         return validators;
      }

   }
}
