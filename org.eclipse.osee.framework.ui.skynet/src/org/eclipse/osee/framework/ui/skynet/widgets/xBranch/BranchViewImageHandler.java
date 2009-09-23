/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class BranchViewImageHandler {
   private static Image favoriteBranchImage = null;
   private static Image favoriteChangeManagedBranchImage = null;
   private static Image inCreationBranchImage = null;
   private static int X_LOCATION = 0;
   private static int Y_LOCATION = 7;

   public static Image getImage(Object element, int columnIndex) {
      Image returnImage = null;

      //lazy loading of images
      checkImages();
      // Seek down through aggregation lists to the lowest level to get an actual element
      while (element instanceof List<?> && !((List<?>) element).isEmpty()) {
         element = ((List<?>) element).get(0);
      }

      if (element instanceof Branch && columnIndex == 0) {
         try {
            checkImages();
            Branch branch = (Branch) element;
            boolean favorite = UserManager.getUser().isFavoriteBranch(branch);
            boolean isChangeManaged = BranchManager.isChangeManaged(branch);
            boolean isSystemBranch = branch.getBranchType().isSystemRootBranch();

            if (isSystemBranch) {
               return ImageManager.getImage(FrameworkImage.BRANCH_SYSTEM_ROOT);
            } else {
               if (branch.getBranchState().isCreationInProgress()) {
                  returnImage = inCreationBranchImage;
               } else if (favorite && isChangeManaged) {
                  returnImage = favoriteChangeManagedBranchImage;
               } else if (favorite) {
                  returnImage = favoriteBranchImage;
               } else if (isChangeManaged) {
                  return ImageManager.getImage(FrameworkImage.BRANCH_CHANGE_MANAGED);
               } else {
                  returnImage = ImageManager.getImage(FrameworkImage.BRANCH);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }

      } else if (element instanceof TransactionId && columnIndex == 0) {
         returnImage = ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
      }
      return returnImage;
   }

   private static synchronized void checkImages() {
      if (favoriteBranchImage == null) {
         favoriteBranchImage =
               new OverlayImage(ImageManager.getImage(FrameworkImage.BRANCH),
                     ImageManager.getImageDescriptor(FrameworkImage.BRANCH_FAVORITE_OVERLAY), X_LOCATION, Y_LOCATION).createImage();
         favoriteChangeManagedBranchImage =
               new OverlayImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE_MANAGED),
                     ImageManager.getImageDescriptor(FrameworkImage.BRANCH_FAVORITE_OVERLAY), X_LOCATION, Y_LOCATION).createImage();
         inCreationBranchImage =
               new OverlayImage(ImageManager.getImage(FrameworkImage.BRANCH),
                     ImageManager.getImageDescriptor(FrameworkImage.BRANCH_IN_CREATION_OVERLAY), -1, 8).createImage();
      }
   }
}
