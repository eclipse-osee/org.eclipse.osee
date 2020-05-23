/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link RestrictionHandler classes}
 * 
 * @author Roberto E. Escobar
 */
public abstract class BaseRestrictionHandlerTest<T extends ObjectRestriction> {

   private final RestrictionHandler<T> restrictionHandler;
   private final ObjectRestriction validRestriction;
   private final ObjectRestriction invalidRestriction;

   protected BaseRestrictionHandlerTest(RestrictionHandler<T> restrictionHandler, ObjectRestriction validRestriction, ObjectRestriction invalidRestriction) {
      this.restrictionHandler = restrictionHandler;
      this.validRestriction = validRestriction;
      this.invalidRestriction = invalidRestriction;
   }

   protected RestrictionHandler<T> getRestrictionHandler() {
      return restrictionHandler;
   }

   @Test
   public void testAsCastedObject() {
      T actualObject = restrictionHandler.asCastedObject(validRestriction);
      Assert.assertNotNull(actualObject);
      Assert.assertEquals(validRestriction, actualObject);
   }

   @Test
   public void testAsCastedObjectReturnsNull() {
      ObjectRestriction objectRestriction = MockModel.createObjectRestriction();
      T actualObject = restrictionHandler.asCastedObject(objectRestriction);
      Assert.assertNull(actualObject);
   }

   @Test
   public void testProcessNullObjectRestriction() {
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(restrictionHandler, null, null, expectedScope);
   }

   @Test
   public void testProcessInvalidObjectRestriction() {
      Scope expectedScope = new Scope().add("fail");
      DslAsserts.assertNullAccessDetail(restrictionHandler, invalidRestriction, null, expectedScope);
   }
}
