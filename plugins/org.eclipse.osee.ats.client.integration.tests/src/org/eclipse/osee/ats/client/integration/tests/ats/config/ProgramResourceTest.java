/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import static org.eclipse.osee.ats.demo.api.DemoArtifactToken.SAW_Program;
import org.eclipse.osee.ats.client.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link ProgramResource}
 *
 * @author Donald G. Dunne
 */
public class ProgramResourceTest extends AbstractRestTest {

   private void testProgramUrl(String url, int size, boolean hasDescription) {
      testUrl(url, size, "SAW Program", "ats.Description", hasDescription);
   }

   @Test
   public void testAtsProgramsRestCall() {
      testProgramUrl("/ats/program", 5, false);
   }

   @Test
   public void testAtsProgramsDetailsRestCall() {
      testProgramUrl("/ats/program/details", 5, true);
   }

   @Test
   public void testAtsProgramRestCall() {
      testProgramUrl("/ats/program/" + SAW_Program.getIdString(), 1, false);
   }

   @Test
   public void testAtsProgramDetailsRestCall() {
      testProgramUrl("/ats/program/" + SAW_Program.getIdString() + "/details", 1, true);
   }
}