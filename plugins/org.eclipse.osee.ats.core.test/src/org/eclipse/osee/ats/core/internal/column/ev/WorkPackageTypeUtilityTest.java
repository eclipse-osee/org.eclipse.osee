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
import org.eclipse.osee.ats.api.ev.AtsWorkPackageType;
import org.eclipse.osee.ats.core.column.IAtsColumnUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link WorkPackageTypeUtility}
 * 
 * @author Donald G. Dunne
 */
public class WorkPackageTypeUtilityTest extends AbstractWorkPackageUtilityTest {

   WorkPackageTypeUtility util;

   @Override
   @Before
   public void setup() throws OseeCoreException {
      super.setup();
      util = new WorkPackageTypeUtility(earnedValueServiceProvider);
   }

   @Test
   public void testGetColumnText() throws OseeCoreException {
      when(workPkg.getWorkPackageType()).thenReturn(AtsWorkPackageType.LOE);
      Assert.assertEquals(AtsWorkPackageType.LOE.name(), util.getColumnText(workItem));

      when(workPkg.getWorkPackageType()).thenReturn(AtsWorkPackageType.LOE);
      Assert.assertEquals(AtsWorkPackageType.LOE.name(), util.getColumnText(workItem));
   }

   @Override
   public IAtsColumnUtility getUtil() {
      return util;
   }

}
