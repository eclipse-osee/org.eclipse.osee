package org.eclipse.osee.ote.ui.define;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteDefineImage implements OseeImage {
   TEST_RUN_VIEW("testRunView.gif");

   private final String fileName;

   private OteDefineImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(OteUiDefinePlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return OteUiDefinePlugin.PLUGIN_ID + ".images." + fileName;
   }
}