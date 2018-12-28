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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public abstract class ImageManagerTest {

   private final KeyedImage[] oseeImages;
   private final String imageClassName;
   private static SevereLoggingMonitor monitorLog;
   private final static String PLUGIN_ID = "org.eclipse.osee.client.integration.tests";

   public ImageManagerTest(String imageClassName, KeyedImage[] oseeImages) {
      this.imageClassName = imageClassName;
      this.oseeImages = oseeImages;
   }

   @org.junit.BeforeClass
   public static void testSetup() throws Exception {
      monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
   }

   @org.junit.AfterClass
   public static void testCleanup() throws Exception {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new ArrayList<>(stats)) {
         if (stat.getException() != null) {
            fail("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
      StringBuffer sb = new StringBuffer();
      for (IHealthStatus stat : new ArrayList<>(stats)) {
         if (stat.getMessage().contains("Unable to load the image for") && !stat.getMessage().contains("nothere")) {
            sb.append(stat.getMessage() + "\n");
         }
      }
      if (!sb.toString().equals("")) {
         fail(sb.toString());
      }
   }

   /**
    * Test that all image enums have associated image files. (Non return missing image)
    */
   @org.junit.Test
   public void testFrameworkImageEnums() throws Exception {
      StringBuffer sb = new StringBuffer();
      for (KeyedImage oseeImage : oseeImages) {
         if (oseeImage == ImageManager.MISSING) {
            continue;
         }
         assertNotNull(String.format("[%s] Image not defined for [%s]", imageClassName, oseeImage),
            ImageManager.getImage(oseeImage));
         if (ImageManager.getImage(oseeImage).equals(ImageManager.getImage(ImageManager.MISSING))) {
            sb.append(String.format("\n[%s] Image not defined for [%s]", imageClassName, oseeImage));
         }
      }
      assertEquals("", sb.toString());
   }

   @org.junit.Test
   public void testGetImageByType() throws Exception {
      assertTrue("Image returned not a folder image.",
         ArtifactImageManager.getImage(CoreArtifactTypes.Folder).equals(ImageManager.getImage(PluginUiImage.FOLDER)));

   }

   public static ByteArrayInputStream getByteArrayInputStream(String imageFilename) throws Exception {
      URL imageFile = Platform.getBundle(PLUGIN_ID).getResource("images/" + imageFilename);
      if (imageFile == null) {
         throw new OseeArgumentException("Invalid image filename.");
      }
      return new ByteArrayInputStream(Lib.inputStreamToBytes(imageFile.openStream()));
   }

   public enum MissingImage implements KeyedImage {
      NOT_HERE("nothere.gif");

      private final String fileName;

      private MissingImage(String fileName) {
         this.fileName = fileName;
      }

      @Override
      public ImageDescriptor createImageDescriptor() {
         return ImageManager.createImageDescriptor(PLUGIN_ID, fileName);
      }

      @Override
      public String getImageKey() {
         return PLUGIN_ID + "." + fileName;
      }
   }

}
