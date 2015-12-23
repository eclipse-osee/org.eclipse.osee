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
package org.eclipse.osee.ats.core.client.review.defect;

import java.util.Date;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Severity;
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
}
