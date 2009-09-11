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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class ImageManagerTest {

   private final OseeImage[] oseeImages;
   private final String imageClassName;
   private static SevereLoggingMonitor monitorLog;

   public ImageManagerTest(String imageClassName, OseeImage[] oseeImages) {
      this.imageClassName = imageClassName;
      this.oseeImages = oseeImages;
   }

   @org.junit.BeforeClass
   public static void testSetup() throws Exception {
      monitorLog = new SevereLoggingMonitor();
      // Clear db image
      ImageManager.setArtifactTypeImageInDb(ArtifactTypeManager.getType("Folder"), null);
      OseeLog.registerLoggerListener(monitorLog);
   }

   @org.junit.AfterClass
   public static void testCleanup() throws Exception {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new CopyOnWriteArrayList<IHealthStatus>(stats)) {
         if (stat.getException() != null) {
            fail("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
      StringBuffer sb = new StringBuffer();
      for (IHealthStatus stat : new CopyOnWriteArrayList<IHealthStatus>(stats)) {
         if (stat.getMessage().contains("Unable to load the image for") && !stat.getMessage().contains("NOT_HERE")) {
            sb.append(stat.getMessage() + "\n");
         }
      }
      if (!sb.toString().equals("")) {
         fail(sb.toString());
      }
   }

   /**
    * Test that image that is not found, returns MISSING image
    * 
    * @throws Exception
    */
   @org.junit.Test
   public void testFrameworkImageMissing() throws Exception {
      // This will throw an OseeLog exception cause NOT_HERE can't be found; this is expected
      assertEquals(ImageManager.getImage(MissingImage.NOT_HERE), ImageManager.getImage(FrameworkImage.MISSING));
   }

   /**
    * Test that all image enums have associated immage files. (Non return missing image)
    * 
    * @throws Exception
    */
   @org.junit.Test
   public void testFrameworkImageEnums() throws Exception {
      StringBuffer sb = new StringBuffer();
      for (OseeImage oseeImage : oseeImages) {
         if (oseeImage == FrameworkImage.MISSING) {
            continue;
         }
         assertNotNull(String.format("[%s] Image not defined for [%s]", imageClassName, oseeImage),
               ImageManager.getImage(oseeImage));
         if (ImageManager.getImage(oseeImage).equals(ImageManager.getImage(FrameworkImage.MISSING))) {
            sb.append(String.format("\n[%s] Image not defined for [%s]", imageClassName, oseeImage));
         }
      }
      assertEquals("", sb.toString());
   }

   @org.junit.Test
   public void testGetImageByType() throws Exception {
      assertTrue("Image returned not a folder image.",
            ImageManager.getImage(ArtifactTypeManager.getType("Folder")).equals(
                  ImageManager.getImage(FrameworkImage.FOLDER)));

   }

   @org.junit.Test
   public void testGetImageByArtifact() throws Exception {
      Artifact folder =
            StaticIdManager.getOrCreateSingletonArtifact(Requirements.FOLDER, "user.groups",
                  BranchManager.getCommonBranch());
      assertTrue("Image returned not a folder image.", ImageManager.getImage(folder).equals(
            ImageManager.getImage(FrameworkImage.FOLDER)));
   }

   @org.junit.Test
   public void testSetArtifactTypeImageInDb() throws Exception {

      Artifact folder =
            StaticIdManager.getOrCreateSingletonArtifact(Requirements.FOLDER, "user.groups",
                  BranchManager.getCommonBranch());

      // Check folder image
      assertTrue("Image returned not a \"Folder\" image.", ImageManager.getImage(folder).equals(
            ImageManager.getImage(FrameworkImage.FOLDER)));

      // Set different image for folder
      ImageManager.setArtifactTypeImageInDb(ArtifactTypeManager.getType("Folder"),
            getByteArrayInputStream("heading.gif"));

      // Test that different image overrides folder image
      assertFalse("Image returned should be \"Heading\" image.", ImageManager.getImage(folder).equals(
            ImageManager.getImage(FrameworkImage.FOLDER)));

      // Clear db image
      ImageManager.setArtifactTypeImageInDb(ArtifactTypeManager.getType("Folder"), null);

      // Reload cache
      ImageManager.loadCache();
      TestUtil.sleep(2000);

      // Test that folder image is back
      assertTrue("Image returned not a \"Folder\" image.", ImageManager.getImage(folder).equals(
            ImageManager.getImage(FrameworkImage.FOLDER)));

      // Cleanup folder artifact
      folder.purgeFromBranch();
   }

   public static ByteArrayInputStream getByteArrayInputStream(String imageFilename) throws Exception {
      File imageFile = SkynetGuiPlugin.getInstance().getPluginFile("images/" + imageFilename);
      if (!imageFile.exists()) {
         throw new OseeArgumentException("Invalid image filename.");
      }
      return new ByteArrayInputStream(Lib.inputStreamToBytes(new FileInputStream(imageFile)));
   }

   public enum MissingImage implements OseeImage {
      NOT_HERE("nothere.gif");

      private final String fileName;

      private MissingImage(String fileName) {
         this.fileName = fileName;
      }

      @Override
      public ImageDescriptor createImageDescriptor() {
         return ImageManager.createImageDescriptor(SkynetGuiPlugin.PLUGIN_ID, "images", fileName);
      }

      @Override
      public String getImageKey() {
         return SkynetGuiPlugin.PLUGIN_ID + "." + fileName;
      }
   }

}
