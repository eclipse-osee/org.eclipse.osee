/*
 * Created on Jun 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.service.control;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author b1528444
 *
 */
public enum ServiceControlImage implements OseeImage {
   MONITOR("monitor.GIF");

   private final String fileName;

   private ServiceControlImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(ControlPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return ControlPlugin.PLUGIN_ID + ".images." + fileName;
   }
}
