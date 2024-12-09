/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.parsers.MsoApplicationExtractor;
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

   @SuppressWarnings("deprecation")
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
            // Case 1: Handle artifact with an "xml" extension
            if (extension.equals("xml")) {
               MicrosoftOfficeApplicationEnum msoApplication = MicrosoftOfficeApplicationEnum.SENTINEL;

               // Sub-case 1.1: The artifact explicitly specifies a Microsoft Office application
               if (artifact.hasAttributeWithNonNullValues(CoreAttributeTypes.MicrosoftOfficeApplication)) {
                  // Retrieve the specified Microsoft Office application and return a ProgramImage
                  msoApplication = MicrosoftOfficeApplicationEnum.fromApplicationName(
                     artifact.getSoleAttributeValue(CoreAttributeTypes.MicrosoftOfficeApplication));
                  return new ProgramImage(extension, msoApplication);
               }
               // Sub-case 1.2: The artifact does not specify a Microsoft Office application
               else {
                  // Extract the application information from the XML content
                  InputStream xmlStream = artifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent);
                  InputStreamReader inputStreamReader = new InputStreamReader(xmlStream);
                  try {
                     msoApplication =
                        MsoApplicationExtractor.findMsoApplicationValue(new BufferedReader(inputStreamReader));
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
                  return new ProgramImage(extension, msoApplication);
               }
            }
            // Case 2: Handle artifact with a non-XML extension
            else {
               return new ProgramImage(extension);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return getBaseImageEnum(artifact.getArtifactType());
   }

}