/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.osee.ats.core.review.defect.DefectItem.Severity;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DefectSeverityToImage {

   public static org.eclipse.swt.graphics.Image getImage(Severity sev) {
      if (sev == Severity.Major) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_MAJOR);
      } else if (sev == Severity.Minor) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_MINOR);
      } else if (sev == Severity.Issue) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_ISSUE);
      }
      return null;
   }

}
