package org.eclipse.osee.ote.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteImage implements OseeImage {
   CHECKOUT("checkout.gif"),
   CONNECTED("connected_sm.gif"),
   OTE("welcome_item3.gif"),
   TEST_PROCEDURE("procedure.gif"),
   TEST_CONFIG("config.gif"),
   TEST_RUN("testrun.gif"),
   TEST_CASE("file.gif"),
   TEST_SUPPORT("function.gif");

   private final String fileName;

   private OteImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestCoreGuiPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return TestCoreGuiPlugin.PLUGIN_ID + ".images." + fileName;
   }
}