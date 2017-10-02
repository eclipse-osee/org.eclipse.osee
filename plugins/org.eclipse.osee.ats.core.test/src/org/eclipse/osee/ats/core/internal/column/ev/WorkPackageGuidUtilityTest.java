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
package org.eclipse.osee.ats.core.internal.column.ev;

import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link WorkPackageGuidColumn}
 *
 * @author Donald G. Dunne
 */
public class WorkPackageGuidUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageGuidColumn util;

   @Override
   @Before
   public void setup() {
      super.setup();
      util = new WorkPackageGuidColumn(earnedValueServiceProvider, null);
   }

   @Test
   public void testGetColumnText() {
      when(workPkg.getGuid()).thenReturn("");
      Assert.assertEquals("", util.getColumnText(workItem));

      when(workPkg.getGuid()).thenReturn("AHSXGTSD34");
      Assert.assertEquals("AHSXGTSD34", util.getColumnText(workItem));
   }

   @Override
   public IAtsColumn getUtil() {
      return util;
   }

}
