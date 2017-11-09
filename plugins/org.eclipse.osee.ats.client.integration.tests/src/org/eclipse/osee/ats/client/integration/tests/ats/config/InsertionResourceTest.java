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
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.osee.ats.demo.api.DemoInsertion;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link InsertionResource}
 *
 * @author Donald G. Dunne
 */
public class InsertionResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsInsertionsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertion");
      Assert.assertEquals(12, array.size());
      JsonObject obj = getObjectNamed("COMM", array);
      Assert.assertNotNull("Did not find value COMM", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionsDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertion/details");
      Assert.assertEquals(12, array.size());
      JsonObject obj = getObjectNamed("COMM", array);
      Assert.assertNotNull("Did not find value COMM", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertion/" + DemoInsertion.sawComm.getId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("COMM", array);
      Assert.assertNotNull("Did not find value COMM", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertion/" + DemoInsertion.sawComm.getId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("COMM", array);
      Assert.assertNotNull("Did not find value COMM", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

}
