/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.agile;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FeatureGroupSumTest {

   @Test
   public void testSimplePage() throws Exception {
      FeatureGroupSum sum = new FeatureGroupSum("feature 1", "desc");
      sum.addToSum(2);
      sum.addToSum(3.3);
      Assert.assertTrue(sum.getSum() == 5.3);
   }
}
