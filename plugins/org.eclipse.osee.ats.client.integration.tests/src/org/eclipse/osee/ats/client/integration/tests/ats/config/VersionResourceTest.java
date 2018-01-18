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

import static org.eclipse.osee.ats.demo.api.DemoArtifactToken.SAW_Bld_1;
import org.eclipse.osee.ats.client.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link VersionResource}
 *
 * @author Donald G. Dunne
 */
public class VersionResourceTest extends AbstractRestTest {

   private void testVersionUrl(String url, int size, boolean hasReleased) {
      testUrl(url, size, SAW_Bld_1.getName(), "ats.Released", hasReleased);
   }

   @Test
   public void testAtsVersionsRestCall() {
      testVersionUrl("/ats/version", 6, false);
   }

   @Test
   public void testAtsVersionsDetailsRestCall() {
      testVersionUrl("/ats/version/details", 6, true);
   }

   @Test
   public void testAtsVersionRestCall() {
      testUrl("/ats/version/" + SAW_Bld_1.getIdString(), SAW_Bld_1.getName());

   }

   @Test
   public void testAtsVersionDetailsRestCall() {
      testVersionUrl("/ats/version/" + SAW_Bld_1.getIdString() + "/details", 1, true);
   }
}