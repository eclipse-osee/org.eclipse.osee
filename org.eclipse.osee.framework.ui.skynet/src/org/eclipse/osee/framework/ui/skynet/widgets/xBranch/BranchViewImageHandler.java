/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 *
 */
public class BranchViewImageHandler {
   private static Image branchImage = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static Image changeManagedBranchImage = SkynetGuiPlugin.getInstance().getImage("change_managed_branch.gif");
   private static Image favoriteBranchImage = null;
   private static Image defaultBranchImage = null;
   private static Image favoriteChangeManagedBranchImage = null;
   
   public static Image getImage(Object element, int columnIndex){
      Image returnImage = null;
      
      //lazy loading of images
      checkImages();
      // Seek down through aggregation lists to the lowest level to get an actual element
      while (element instanceof List && !((List<?>) element).isEmpty()) {
         element = ((List<?>) element).get(0);
      }

      if (element instanceof Branch && columnIndex == 0) {
         try {
            checkImages();
            Branch branch = (Branch) element;
            boolean favorite = UserManager.getUser().isFavoriteBranch(branch);
            boolean action = branch.isChangeManaged();

            if (favorite && action) {
               returnImage = favoriteChangeManagedBranchImage;
            } else if (favorite) {
               returnImage = favoriteBranchImage;
            } else if (action) {
               returnImage = changeManagedBranchImage;
            } else {
               returnImage = branchImage;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }

      } else if (element instanceof TransactionId && columnIndex == 0) {
         returnImage=  SkynetGuiPlugin.getInstance().getImage("DBiconBlue.GIF");

      }
      return returnImage;
   }
   
   
   private static synchronized void checkImages() {
      if (defaultBranchImage == null) {
         favoriteBranchImage =
               new OverlayImage(branchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("star_9_9.gif"), 0, 7).createImage();
         favoriteChangeManagedBranchImage =
               new OverlayImage(changeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "star_9_9.gif"), 0, 7).createImage();
      }
   }

}
