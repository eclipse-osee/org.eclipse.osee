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
