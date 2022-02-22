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

package org.eclipse.osee.ats.core.internal.column.ev;

import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.column.AtsColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link WorkPackageIdColumn}
 *
 * @author Donald G. Dunne
 */
public class WorkPackageIdUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageIdColumn util;

   @Override
   @Before
   public void setup() {
      super.setup();
      util = new WorkPackageIdColumn(earnedValueServiceProvider, null);
   }

   @Test
   public void testGetColumnText() {
      when(workPkg.getWorkPackageId()).thenReturn("");
      Assert.assertEquals("", util.getColumnText(workItem));

      when(workPkg.getWorkPackageId()).thenReturn("WP ID");
      Assert.assertEquals("WP ID", util.getColumnText(workItem));
   }

   @Override
   public AtsColumn getUtil() {
      return util;
   }

}
