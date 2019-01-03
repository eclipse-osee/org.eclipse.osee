/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import java.util.List;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
      List<ProgramVersions> progVers = AtsClientService.getProgramEp().getVersions(null);
      Assert.assertNotNull(progVers);
   }

}
