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
import org.junit.Assert;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.core.column.IAtsColumnUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkPackageUtilityTest {

   // @formatter:off
   @Mock protected IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   @Mock protected IAtsEarnedValueService earnedValueService;
   @Mock protected IAtsWorkItem workItem;
   @Mock protected IAtsWorkPackage workPkg;
   // @formatter:on

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      when(earnedValueServiceProvider.getEarnedValueService()).thenReturn(earnedValueService);
      when(earnedValueService.getWorkPackage(workItem)).thenReturn(workPkg);
   }

   public abstract IAtsColumnUtility getUtil();

   @Test
   public void testGetColumnText_notWorkItem() {
      Assert.assertEquals("", getUtil().getColumnText(new Boolean(false)));
   }

   @Test
   public void testGetDescription() {
      Assert.assertTrue(Strings.isValid(getUtil().getDescription()));
   }

}
