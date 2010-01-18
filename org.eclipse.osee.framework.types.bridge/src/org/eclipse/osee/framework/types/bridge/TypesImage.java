package org.eclipse.osee.framework.types.bridge;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public enum TypesImage implements KeyedImage {
   OSEE_TYPES_IMPORT("gears.gif"),
   OSEE_TYPES_LINK("link_obj.gif"),
   MISSING("missing");
   private final String fileName;

   private TypesImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      if (this == MISSING) {
         return ImageDescriptor.getMissingImageDescriptor();
      }
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + "." + fileName;
   }
}
