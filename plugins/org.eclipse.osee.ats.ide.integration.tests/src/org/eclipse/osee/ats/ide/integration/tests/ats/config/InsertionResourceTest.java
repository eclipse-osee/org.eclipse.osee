/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.api.demo.DemoInsertion;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link InsertionResource}
 *
 * @author Donald G. Dunne
 */
public class InsertionResourceTest extends AbstractRestTest {

   private void testInsertionUrl(String url, int size, boolean hasDescription) {
      testUrl(url, size, "COMM", "ats.Description", hasDescription);
   }

   @Test
   public void testAtsInsertionsRestCall() {
      testInsertionUrl("/ats/insertion", 12, false);
   }

   @Test
   public void testAtsInsertionsDetailsRestCall() {
      testInsertionUrl("/ats/insertion/details", 12, true);
   }

   @Test
   public void testAtsInsertionRestCall() {
      testUrl("/ats/insertion/" + DemoInsertion.sawComm.getIdString(), "COMM");
   }

   @Test
   public void testAtsInsertionDetailsRestCall() {
      testInsertionUrl("/ats/insertion/" + DemoInsertion.sawComm.getIdString() + "/details", 1, true);
   }
}