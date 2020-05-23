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
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link WorkPackageNameColumn}
 *
 * @author Donald G. Dunne
 */
public class WorkPackageNameUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageNameColumn util;

   @Override
   @Before
   public void setup() {
      super.setup();
      util = new WorkPackageNameColumn(earnedValueServiceProvider, null);
   }

   @Test
   public void testGetColumnText() {
      when(workPkg.getName()).thenReturn("");
      Assert.assertEquals("", util.getColumnText(workItem));

      when(workPkg.getName()).thenReturn("WP CONTROL");
      Assert.assertEquals("WP CONTROL", util.getColumnText(workItem));
   }

   @Override
   public IAtsColumn getUtil() {
      return util;
   }

}
