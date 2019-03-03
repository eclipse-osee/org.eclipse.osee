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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.ProgramImage;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Ryan D. Brooks
 */
public class BaseImage implements KeyedImage {
   private final ArtifactTypeToken artifactType;
   private final byte[] imageData;

   private BaseImage(ArtifactTypeToken artifactType, byte[] imageData) {
      this.artifactType = artifactType;
      this.imageData = imageData;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageDescriptor.createFromImageData(new ImageData(new ByteArrayInputStream(imageData)));
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".artifact_type." + artifactType.getName();
   }

   public static KeyedImage getBaseImageEnum(ArtifactTypeToken artifactType, byte[] imageData) {
      if (ArtifactImageManager.getOverrideImageEnum() != null) {
         return ArtifactImageManager.getOverrideImageEnum();
      }
      return new BaseImage(artifactType, imageData);
   }

   public static KeyedImage getBaseImageEnum(ArtifactTypeToken artifactType) {
      if (ArtifactImageManager.getOverrideImageEnum() != null) {
         return ArtifactImageManager.getOverrideImageEnum();
      }
      // Check extensions
      KeyedImage oseeImage = ArtifactImageManager.getArtifactTypeImage(artifactType);
      if (oseeImage != null) {
         return oseeImage;
      }

      return FrameworkImage.LASER;
   }

   public static KeyedImage getBaseImageEnum(Artifact artifact) {
      if (ArtifactImageManager.getOverrideImageEnum() != null) {
         return ArtifactImageManager.getOverrideImageEnum();
      }
      try {
         String extension = artifact.getSoleAttributeValue(CoreAttributeTypes.Extension, "");
         if (Strings.isValid(extension)) {
            return new ProgramImage(extension);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return getBaseImageEnum(artifact.getArtifactType());
   }

}