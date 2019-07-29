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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link RuleDefinitionOption}
 *
 * @author Donald G. Dunne
 */
public class RuleDefinitionOptionTest {

   @Test
   public void testGetDescription() {
      Assert.assertEquals(
         "StateDefinition Option: Will auto-create a blocking decision review for this state requesting validation for this workflow.",
         RuleDefinitionOption.AddDecisionValidateBlockingReview.getDescription());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(9, RuleDefinitionOption.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(RuleDefinitionOption.AddDecisionValidateBlockingReview,
         RuleDefinitionOption.valueOf(RuleDefinitionOption.AddDecisionValidateBlockingReview.name()));
   }

}
