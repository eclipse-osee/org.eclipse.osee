/*
 * Created on Jun 12, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.test.manager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author b1528444
 *
 */
public enum OteTestManagerImage implements OseeImage {
   PROJECT_SET_IMAGE("import_wiz.gif"),
   TEST_BATCH_IMAGE("file.gif"),
   TEST_MANAGER("tm.gif");

   private final String fileName;

   private OteTestManagerImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestManagerPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return TestManagerPlugin.PLUGIN_ID + ".images." + fileName;
   }
}
