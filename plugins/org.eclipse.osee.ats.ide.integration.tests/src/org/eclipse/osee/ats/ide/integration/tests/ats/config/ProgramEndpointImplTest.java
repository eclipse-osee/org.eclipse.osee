/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import java.util.List;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for
 *
 * @author Donald G. Dunne
 */
public class ProgramEndpointImplTest {

   @Test
   public void testGetProgramVersions() {
      List<ProgramVersions> progVers = AtsApiService.get().getServerEndpoints().getProgramEp().getVersions(null);
      Assert.assertNotNull(progVers);
   }

}
