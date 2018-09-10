/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.swt;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.osee.framework.ui.swt.internal.Activator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Ryan D. Brooks
 */
public final class ImageManager {

   public static final KeyedImage MISSING = new DefaultImage();

   private static ImageRegistry getImageRegistry() {
      return Activator.getInstance().getImageRegistry();
   }

   private synchronized static void storeOnImageRegistry(String key, ImageDescriptor descriptor) {
      getImageRegistry().put(key, descriptor);
   }

   public synchronized static Image getImage(String imageKey) {
      return getImageRegistry().get(imageKey);
   }

   public synchronized static ImageDescriptor getImageDescriptor(String imageKey) {
      return getImageRegistry().getDescriptor(imageKey);
   }

   public synchronized static void removeFromRegistry(String imageKey) {
      getImageRegistry().remove(imageKey);
   }

   public synchronized static Image getProgramImage(String extension) {
      if (isInTest()) {
         return getImage(MISSING);
      }
      return getImage(new ProgramImage(extension));
   }

   public synchronized static ImageDescriptor getProgramImageDescriptor(String extension) {
      if (isInTest()) {
         return getImageDescriptor(MISSING);
      }
      return getImageDescriptor(new ProgramImage(extension));
   }

   private static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public synchronized static Image getImage(KeyedImage imageEnum) {
      return getImage(setupImage(imageEnum));
   }

   public synchronized static ImageDescriptor getImageDescriptor(KeyedImage imageEnum) {
      return getImageDescriptor(setupImage(imageEnum));
   }

   public synchronized static String setupImage(KeyedImage imageEnum) {
      String imageKey = imageEnum != null ? imageEnum.getImageKey() : MISSING.getImageKey();
      if (getImageRegistry().getDescriptor(imageKey) == null && imageEnum != null) {
         ImageDescriptor imageDescriptor = imageEnum.createImageDescriptor();
         if (imageDescriptor == null) {
            if (!imageKey.contains("nothere.gif") && !(imageEnum instanceof ProgramImage)) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Unable to load the image for [%s]",
                  imageEnum.getImageKey());
            }
            return setupImage(MISSING);
         }
         storeOnImageRegistry(imageKey, imageDescriptor);
      }
      return imageKey;
   }

   /**
    * @param baseImageName must refer to an image that is already mapped to this key in the image registry
    * @return the overlay keyed image
    */
   public synchronized static KeyedImage setupImageWithOverlay(KeyedImage baseImageEnum, KeyedImage overlay, Location location) {
      String baseImageName = setupImage(baseImageEnum);
      String overlayImageKey = baseImageName + "_" + overlay.getImageKey();

      ImageDescriptor overlayImageDescriptor = getImageDescriptor(overlayImageKey);
      if (overlayImageDescriptor == null) {
         Image baseImage = getImage(baseImageName);
         overlayImageDescriptor = new OverlayImage(baseImage, getImageDescriptor(setupImage(overlay)), location);
         storeOnImageRegistry(overlayImageKey, overlayImageDescriptor);
      }
      return new KeyedImagePair(overlayImageKey, overlayImageDescriptor);
   }

   public static ImageDescriptor createImageDescriptor(String symbolicBundleName, String imageFileName) {
      return AbstractUIPlugin.imageDescriptorFromPlugin(symbolicBundleName, "OSEE-INF/images" + "/" + imageFileName);
   }

   public static KeyedImage createKeyedImage(String imageKey, ImageDescriptor descriptor) {
      return new KeyedImagePair(imageKey, descriptor);
   }

   private static final class KeyedImagePair implements KeyedImage {
      private final ImageDescriptor descriptor;
      private final String imageKey;

      public KeyedImagePair(String imageKey, ImageDescriptor descriptor) {
         super();
         this.descriptor = descriptor;
         this.imageKey = imageKey;
      }

      @Override
      public ImageDescriptor createImageDescriptor() {
         return descriptor;
      }

      @Override
      public String getImageKey() {
         return imageKey;
      }
   }

   private static final class DefaultImage implements KeyedImage {

      @Override
      public ImageDescriptor createImageDescriptor() {
         return ImageDescriptor.getMissingImageDescriptor();
      }

      @Override
      public String getImageKey() {
         return Activator.PLUGIN_ID + ".missing";
      }
   }
}
