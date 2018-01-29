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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.junit.Before;
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
   @Mock protected ArtifactToken workPkgArt;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(earnedValueServiceProvider.getEarnedValueService()).thenReturn(earnedValueService);
      when(earnedValueService.getWorkPackageId(workItem)).thenReturn(ArtifactId.valueOf(345));
      when(earnedValueService.getWorkPackage(workItem)).thenReturn(workPkg);
      when(workPkg.getStoreObject()).thenReturn(workPkgArt);
   }

   public abstract IAtsColumn getUtil();

}
