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
package org.eclipse.osee.framework.ui.skynet;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Ryan D. Brooks
 */
public class BaseImage implements OseeImage {
   private static OseeImage overrideImageEnum;
   private final ArtifactType artifactType;

   private BaseImage(ArtifactType artifactType) {
      this.artifactType = artifactType;
   }

   public static OseeImage getBaseImageEnum(ArtifactType artifactType) {
      if (overrideImageEnum != null) {
         return overrideImageEnum;
      }
      if (artifactType.getImageData() == null) {
         return FrameworkImage.LASER;
      }
      return new BaseImage(artifactType);
   }

   public static OseeImage getBaseImageEnum(Artifact artifact) {
      if (overrideImageEnum != null) {
         return overrideImageEnum;
      }
      if (artifact instanceof NativeArtifact) {
         try {
            String extension = ((NativeArtifact) artifact).getFileExtension();
            return new ProgramImage(extension);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return getBaseImageEnum(artifact.getArtifactType());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#createImageDescriptor()
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageDescriptor.createFromImageData(new ImageData(new ByteArrayInputStream(artifactType.getImageData())));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageKey()
    */
   @Override
   public String getImageKey() {
      return SkynetGuiPlugin.PLUGIN_ID + ".artifact_type." + artifactType.getName();
   }

   /**
    * @param imageEnum representing the image that will be returned for all artifacts and artifact types
    */
   public static void setupOverrideImage(OseeImage imageEnum) {
      overrideImageEnum = imageEnum;
   }
}