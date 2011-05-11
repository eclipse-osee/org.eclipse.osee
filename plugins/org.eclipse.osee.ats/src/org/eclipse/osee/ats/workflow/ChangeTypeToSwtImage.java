/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow;

import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeToSwtImage {

   public static Image getImage(ChangeType type) {
      if (type == ChangeType.Problem) {
         return ImageManager.getImage(FrameworkImage.PROBLEM);
      } else if (type == ChangeType.Improvement) {
         return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
      } else if (type == ChangeType.Support) {
         return ImageManager.getImage(FrameworkImage.SUPPORT);
      } else if (type == ChangeType.Refinement) {
         return ImageManager.getImage(FrameworkImage.REFINEMENT);
      }
      return null;
   }

}
