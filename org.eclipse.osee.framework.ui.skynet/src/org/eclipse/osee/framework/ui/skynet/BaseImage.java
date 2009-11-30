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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Ryan D. Brooks
 */
public class BaseImage implements OseeImage {
   private final ArtifactType artifactType;
   private final byte[] imageData;

   private BaseImage(ArtifactType artifactType, byte[] imageData) {
      this.artifactType = artifactType;
      this.imageData = imageData;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageDescriptor.createFromImageData(new ImageData(new ByteArrayInputStream(imageData)));
   }

   @Override
   public String getImageKey() {
      return SkynetGuiPlugin.PLUGIN_ID + ".artifact_type." + artifactType.getName();
   }

   public static OseeImage getBaseImageEnum(ArtifactType artifactType, byte[] imageData) {
      if (ImageManager.getOverrideImageEnum() != null) {
         return ImageManager.getOverrideImageEnum();
      }
      return new BaseImage(artifactType, imageData);
   }

   public static OseeImage getBaseImageEnum(ArtifactType artifactType) {
      if (ImageManager.getOverrideImageEnum() != null) {
         return ImageManager.getOverrideImageEnum();
      }
      // Check extensions
      OseeImage oseeImage = ImageManager.getArtifactTypeImage(artifactType.getName());
      if (oseeImage != null) {
         return oseeImage;
      }

      return FrameworkImage.LASER;
   }

   public static OseeImage getBaseImageEnum(Artifact artifact) {
      if (ImageManager.getOverrideImageEnum() != null) {
         return ImageManager.getOverrideImageEnum();
      }
      try {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_EXTENSION.getName())) {
            String extension = artifact.getSoleAttributeValue(CoreAttributeTypes.NATIVE_EXTENSION.getName(), "");
            if (Strings.isValid(extension)) {
               return new ProgramImage(extension);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return getBaseImageEnum(artifact.getArtifactType());
   }

}