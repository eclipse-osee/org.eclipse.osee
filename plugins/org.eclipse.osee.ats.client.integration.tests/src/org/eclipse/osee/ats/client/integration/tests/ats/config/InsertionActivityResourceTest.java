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
import org.eclipse.osee.ats.demo.api.DemoInsertionActivity;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link InsertionResource}
 *
 * @author Donald G. Dunne
 */
public class InsertionActivityResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsInsertionActivitiesRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertionactivity");
      Assert.assertEquals(10, array.size());
      JsonObject obj = getObjectNamed("COMM Page", array);
      Assert.assertNotNull("Did not find value COMM Page", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionActivitiesDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertionactivity/details");
      Assert.assertEquals(10, array.size());
      JsonObject obj = getObjectNamed("COMM Page", array);
      Assert.assertNotNull("Did not find value COMM Page", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionActivityRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("COMM Page", array);
      Assert.assertNotNull("Did not find value COMM Page", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsInsertionActivityDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("COMM Page", array);
      Assert.assertNotNull("Did not find value COMM Page", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

}
