/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractImageManagerTest {

   private static final String pluginId = "org.eclipse.osee.ats.ide";

   private final Collection<AtsImage> oseeImages;
   private final String imageClassName;

   private static SevereLoggingMonitor monitorLog;

   public AbstractImageManagerTest(String imageClassName, Collection<AtsImage> oseeImages) {
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
      for (OseeImage oseeImage : oseeImages) {
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

   @org.junit.Test
   public void testGetImageByArtifact() throws Exception {
      ArtifactId folder = ArtifactQuery.checkArtifactFromTypeAndName(CoreArtifactTypes.Folder, "User Groups",
         AtsApiService.get().getAtsBranch());
      assertTrue("Image returned not a folder image.",
         ArtifactImageManager.getImage(AtsApiService.get().getQueryServiceIde().getArtifact(folder)).equals(
            ImageManager.getImage(PluginUiImage.FOLDER)));
   }

   public ByteArrayInputStream getByteArrayInputStream(String imageFilename) throws Exception {
      InputStream image = OsgiUtil.getResourceAsStream(AtsImage.class, "images/" + imageFilename);
      return new ByteArrayInputStream(Lib.inputStreamToBytes(image));
   }

   public enum MissingImage implements KeyedImage {
      NOT_HERE("nothere.gif");

      private final String fileName;

      private MissingImage(String fileName) {
         this.fileName = fileName;
      }

      @Override
      public ImageDescriptor createImageDescriptor() {
         return ImageManager.createImageDescriptor(pluginId, fileName);
      }

      @Override
      public String getImageKey() {
         return pluginId + "." + fileName;
      }
   }

}
