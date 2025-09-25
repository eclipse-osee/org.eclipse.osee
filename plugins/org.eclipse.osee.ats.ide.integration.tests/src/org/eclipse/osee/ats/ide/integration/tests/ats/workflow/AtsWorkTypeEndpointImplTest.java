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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for AtsWorkTypeEndpointImpl
 *
 * @author Donald G. Dunne
 */
public class AtsWorkTypeEndpointImplTest {

   private AtsApi atsApi;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
   }

   @Test
   public void testGetWorkType() {
      Collection<WorkType> workTypes = atsApi.getServerEndpoints().getWorkTypeEp().get();
      Assert.assertNotNull(workTypes);
      Assert.assertTrue(workTypes.contains(WorkType.ChangeRequest));
      Assert.assertTrue(workTypes.size() > 25);
   }

}
