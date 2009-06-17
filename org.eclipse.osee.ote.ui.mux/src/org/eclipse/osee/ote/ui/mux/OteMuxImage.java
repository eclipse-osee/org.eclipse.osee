/*
 * Created on Jun 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.mux;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author b1528444
 *
 */
public enum OteMuxImage implements OseeImage {
   MUX("1553.gif");

   private final String fileName;

   private OteMuxImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(MuxToolPlugin.PLUGIN_ID, "icons", fileName);
   }

   @Override
   public String getImageKey() {
      return MuxToolPlugin.PLUGIN_ID + ".icons." + fileName;
   }
}
