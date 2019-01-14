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

import org.eclipse.osee.ats.api.demo.DemoInsertionActivity;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link InsertionResource}
 *
 * @author Donald G. Dunne
 */
public class InsertionActivityResourceTest extends AbstractRestTest {

   private void testActivitiesUrl(String url, int size, boolean hasDescription) {
      testUrl(url, size, "COMM Page", "ats.Description", hasDescription);
   }

   @Test
   public void testAtsInsertionActivitiesRestCall() {
      testActivitiesUrl("/ats/insertionactivity", 10, false);
   }

   @Test
   public void testAtsInsertionActivitiesDetailsRestCall() {
      testActivitiesUrl("/ats/insertionactivity/details", 10, true);
   }

   @Test
   public void testAtsInsertionActivityRestCall() {
      testUrl("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getId(), "COMM Page");
   }

   @Test
   public void testAtsInsertionActivityDetailsRestCall() {
      testActivitiesUrl("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getId() + "/details", 1, true);
   }
}