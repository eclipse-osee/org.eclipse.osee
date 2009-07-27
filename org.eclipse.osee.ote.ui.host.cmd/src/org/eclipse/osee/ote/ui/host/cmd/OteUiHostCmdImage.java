/*
 * Created on Jul 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.host.cmd;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

public enum OteUiHostCmdImage implements OseeImage {
   CONSOLE("console.gif"), TEST_SERVER("test_server.gif"), USER("user.gif");

   private final String fileName;

   private OteUiHostCmdImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(UiPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return UiPlugin.PLUGIN_ID + ".images." + fileName;
   }

}
