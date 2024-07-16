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
 * Test case for {@link WorkPackageArtIdColumn}
 *
 * @author Donald G. Dunne
 */
public class WorkPackageArtIdUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageArtIdColumn util;

   @Override
   @Before
   public void setup() {
      super.setup();
      util = new WorkPackageArtIdColumn(earnedValueServiceProvider, null);
   }

   @Test
   public void testGetColumnText() {
      when(workPkg.getIdString()).thenReturn("");
      Assert.assertEquals("", util.getColumnText(workItem));

      when(workPkg.getIdString()).thenReturn("334534");
      Assert.assertEquals("334534", util.getColumnText(workItem));
   }

   @Override
   public AtsColumn getUtil() {
      return util;
   }

}
