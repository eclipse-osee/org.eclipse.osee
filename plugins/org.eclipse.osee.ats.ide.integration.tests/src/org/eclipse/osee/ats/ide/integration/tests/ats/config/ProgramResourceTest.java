/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link ProgramResource}
 *
 * @author Donald G. Dunne
 */
public class ProgramResourceTest extends AbstractRestTest {

   private void testProgramUrl(String url, int size, boolean hasDescription) {
      testUrl(url, size, DemoArtifactToken.SAW_Program.getName(), "ats.Description", hasDescription);
   }

   @Test
   public void testAtsProgramsRestCall() {
      testProgramUrl("/ats/program", 6, false);
   }

   @Test
   public void testAtsProgramsDetailsRestCall() {
      testProgramUrl("/ats/program/details", 6, true);
   }

   @Test
   public void testAtsProgramRestCall() {
      testUrl("/ats/program/" + DemoArtifactToken.SAW_Program.getIdString(), DemoArtifactToken.SAW_Program.getName());
   }

   @Test
   public void testAtsProgramDetailsRestCall() {
      testProgramUrl("/ats/program/" + DemoArtifactToken.SAW_Program.getIdString() + "/details", 1, true);
   }
}