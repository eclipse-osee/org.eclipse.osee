/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect;

import java.util.Date;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;

/**
 * Test unit for {@link ReviewDefectItem}
 *
 * @author Donald G. Dunne
 */
public class ReviewDefectItemTest {

   @org.junit.Test
   public void testToXmlFromXml() {
      Date date = new Date();
      ReviewDefectItem item =
         new ReviewDefectItem("1234", Severity.Issue, Disposition.Duplicate, InjectionActivity.Software_Design,
            "this is the description", "this is the resolution", "this is the location", date, "this is the notes");

      ReviewDefectItem fromItem = new ReviewDefectItem(item.toXml(false), false, null);
      Assert.assertEquals("1234", fromItem.getUserId());
      Assert.assertEquals(item.getSeverity(), fromItem.getSeverity());
      Assert.assertEquals(item.getDisposition(), fromItem.getDisposition());
      Assert.assertEquals(item.getInjectionActivity(), fromItem.getInjectionActivity());
      Assert.assertEquals(item.getDescription(), fromItem.getDescription());
      Assert.assertEquals(item.getResolution(), fromItem.getResolution());
      Assert.assertEquals(item.getLocation(), fromItem.getLocation());
      Assert.assertEquals(item.getDate(), fromItem.getDate());
      Assert.assertEquals(item.getNotes(), fromItem.getNotes());
   }

   /**
    * See explanation in org.eclipse.osee.ats.ide.workflow.review.ReviewDefectItem.fromXml.
    */
   @org.junit.Test
   public void testFromGuidXmlToIdXml() {
      Date date = new Date();
      ReviewDefectItem item =
         new ReviewDefectItem("1234", Severity.Issue, Disposition.Duplicate, InjectionActivity.Software_Design,
            "this is the description", "this is the resolution", "this is the location", date, "this is the notes");
      String xmlStr = item.toXml(false);
      String guid = GUID.create();
      xmlStr = xmlStr.replaceFirst("id>.*</id", String.format("guid>%s</guid", guid));
      Assert.assertTrue(xmlStr.contains("guid"));

      ReviewDefectItem fromItem = new ReviewDefectItem(xmlStr, false, null);

      long hash = guid.hashCode();
      if (hash < 0) {
         hash = hash * -1;
      }
      Assert.assertEquals(hash, fromItem.getId().intValue());
   }

}
