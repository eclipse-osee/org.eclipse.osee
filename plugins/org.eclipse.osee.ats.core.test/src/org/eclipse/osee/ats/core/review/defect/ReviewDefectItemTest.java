/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.defect;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectItem.Severity;

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
