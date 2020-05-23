/*********************************************************************
 * Copyright (c) 2015 Boeing
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
      testUrl("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getIdString(), "COMM Page");
   }

   @Test
   public void testAtsInsertionActivityDetailsRestCall() {
      testActivitiesUrl("/ats/insertionactivity/" + DemoInsertionActivity.commPage.getIdString() + "/details", 1, true);
   }
}