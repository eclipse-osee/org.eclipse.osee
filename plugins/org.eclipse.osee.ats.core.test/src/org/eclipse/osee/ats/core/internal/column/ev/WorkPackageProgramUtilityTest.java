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
 * Test case for {@link WorkPackageProgramColumn}
 *
 * @author Donald G. Dunne
 */
public class WorkPackageProgramUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageProgramColumn util;

   @Override
   @Before
   public void setup()  {
      super.setup();
      util = new WorkPackageProgramColumn(earnedValueServiceProvider, null);
   }

   @Test
   public void testGetColumnText()  {
      when(workPkg.getWorkPackageProgram()).thenReturn("");
      Assert.assertEquals("", util.getColumnText(workItem));

      when(workPkg.getWorkPackageProgram()).thenReturn("USG");
      Assert.assertEquals("USG", util.getColumnText(workItem));
   }

   @Override
   public IAtsColumn getUtil() {
      return util;
   }

}
