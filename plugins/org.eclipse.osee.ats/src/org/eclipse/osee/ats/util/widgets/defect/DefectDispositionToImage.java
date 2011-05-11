/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.osee.ats.core.review.defect.DefectItem.Disposition;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DefectDispositionToImage {
   public static Image getImage(Disposition sev) {
      if (sev == Disposition.Accept) {
         return ImageManager.getImage(FrameworkImage.ACCEPT);
      } else if (sev == Disposition.Reject) {
         return ImageManager.getImage(FrameworkImage.REJECT);
      } else if (sev == Disposition.Duplicate) {
         return ImageManager.getImage(FrameworkImage.DUPLICATE);
      }
      return null;
   }

}
