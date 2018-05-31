/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.workflow.review.defect;

import java.util.Date;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Severity;
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
            "this is the description", "this is the resolution", "this is the location", date);

      ReviewDefectItem fromItem = new ReviewDefectItem(item.toXml());
      Assert.assertEquals("1234", fromItem.getUserId());
      Assert.assertEquals(item.getSeverity(), fromItem.getSeverity());
      Assert.assertEquals(item.getDisposition(), fromItem.getDisposition());
      Assert.assertEquals(item.getInjectionActivity(), fromItem.getInjectionActivity());
      Assert.assertEquals(item.getDescription(), fromItem.getDescription());
      Assert.assertEquals(item.getResolution(), fromItem.getResolution());
      Assert.assertEquals(item.getLocation(), fromItem.getLocation());
      Assert.assertEquals(item.getDate(), fromItem.getDate());
   }

   /**
    * See explanation in org.eclipse.osee.ats.workflow.review.ReviewDefectItem.fromXml.
    */
   @org.junit.Test
   public void testFromGuidXmlToIdXml() {
      Date date = new Date();
      ReviewDefectItem item =
         new ReviewDefectItem("1234", Severity.Issue, Disposition.Duplicate, InjectionActivity.Software_Design,
            "this is the description", "this is the resolution", "this is the location", date);
      String xmlStr = item.toXml();
      String guid = GUID.create();
      xmlStr = xmlStr.replaceFirst("id>.*</id", String.format("guid>%s</guid", guid));
      Assert.assertTrue(xmlStr.contains("guid"));

      ReviewDefectItem fromItem = new ReviewDefectItem(xmlStr);

      Assert.assertEquals(guid.hashCode(), fromItem.getId().intValue());
   }

}
