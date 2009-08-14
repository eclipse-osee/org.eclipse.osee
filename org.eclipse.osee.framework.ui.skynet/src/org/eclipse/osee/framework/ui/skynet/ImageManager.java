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

import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Ryan D. Brooks
 */
public class ImageManager {
   private static final String EXTENSION_ELEMENT = "ArtifactImageProvider";
   private static final String EXTENSION_ID = SkynetGuiPlugin.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static OseeImage overrideImageEnum;

   private static final String SELECT_ARTIFACT_TYPES_IMAGE_QUERY =
         "SELECT art_type_id, image FROM osee_artifact_type where image is not null";
   private static final ImageManager instance = new ImageManager();
   private final Map<String, ArtifactImageProvider> providersOverrideImageMap =
         Collections.synchronizedMap(new HashMap<String, ArtifactImageProvider>());
   private final Map<String, OseeImage> artifactTypeImageMap =
         Collections.synchronizedMap(new HashMap<String, OseeImage>());
   private boolean artifactTypeImagesLoaded = false;
   private final ImageRegistry imageRegistry = SkynetGuiPlugin.getInstance().getImageRegistry();

   static {
      loadCache();
   }

   public static void loadCache() {
      List<ArtifactImageProvider> providers =
            new ExtensionDefinedObjects<ArtifactImageProvider>(EXTENSION_ID, EXTENSION_ELEMENT, "class").getObjects();

      for (ArtifactImageProvider imageProvider : providers) {
         try {
            imageProvider.init();
         } catch (Exception ex) {
            // prevent an exception in one provider from affecting the initialization of other providers
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   private ImageManager() {
   }

   private static synchronized void ensureArtifactTypeImagesLoaded() throws OseeDataStoreException {
      if (!instance.artifactTypeImagesLoaded) {
         instance.artifactTypeImagesLoaded = true;
         // Load base images from database (which can override the ImageManager.registerImage() calls provided
         // through the ArtifactImageProviders
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES_IMAGE_QUERY);

            while (chStmt.next()) {
               try {
                  ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
                  instance.artifactTypeImageMap.put(artifactType.getName(), BaseImage.getBaseImageEnum(artifactType,
                        Lib.inputStreamToBytes(chStmt.getBinaryStream("image"))));
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         } finally {
            chStmt.close();
         }
      }
   }

   public static Image getConflictImage(Conflict conflict) throws OseeCoreException {
      if (conflict instanceof AttributeConflict) {
         return getImage(FrameworkImage.ATTRIBUTE_MOLECULE);
      }
      if (conflict instanceof ArtifactConflict) {
         return getImage(conflict.getArtifact());
      }
      throw new OseeArgumentException("Conflict not of supported type");
   }

   private static Image getChangeTypeImageInternal(OseeImage baseImage, ChangeType changeType, ModificationType modType) {
      if (changeType == CONFLICTING && modType == NEW) {
         modType = ModificationType.MODIFIED;
      }
      OseeImage overlay = FrameworkImage.valueOf(changeType + "_" + modType.toString());
      return instance.imageRegistry.get(setupImageWithOverlay(baseImage, overlay, Location.TOP_LEFT));
   }

   public static Image getChangeTypeImage(Change change) throws OseeArgumentException, OseeDataStoreException, OseeTypeDoesNotExist {
      if (change.getItemKind().equals("Attribute")) {
         return getAttributeChangeImage(change.getChangeType(), change.getModificationType());
      }
      if (change.getItemKind().equals("Artifact")) {
         return getArtifactChangeImage(change.getArtifactType(), change.getChangeType(), change.getModificationType());
      }
      if (change.getItemKind().equals("Relation")) {
         return getRelationChangeImage(change.getChangeType(), change.getModificationType());
      }
      throw new OseeArgumentException("Change not of supported type");
   }

   public static Image getChangeKindImage(Change change) throws OseeArgumentException, OseeDataStoreException, OseeTypeDoesNotExist {
      if (change.getItemKind().equals("Attribute")) {
         return getArtifactChangeImage(change.getArtifactType(), change.getChangeType(), change.getModificationType());
      }
      return getChangeTypeImage(change);
   }

   private static Image getArtifactChangeImage(ArtifactType artifactType, ChangeType changeType, ModificationType modType) {
      return getChangeTypeImageInternal(BaseImage.getBaseImageEnum(artifactType), changeType, modType);
   }

   private static Image getAttributeChangeImage(ChangeType changeType, ModificationType modType) {
      return getChangeTypeImageInternal(FrameworkImage.ATTRIBUTE_MOLECULE, changeType, modType);
   }

   private static Image getRelationChangeImage(ChangeType changeType, ModificationType modType) {
      return getChangeTypeImageInternal(FrameworkImage.RELATION, changeType, modType);
   }

   public static Image getImage(ArtifactType artifactType) {
      try {
         if (overrideImageEnum != null) {
            return ImageManager.getImage(overrideImageEnum);
         }

         // Check if image provider provides override for this image
         ArtifactImageProvider imageProvider = instance.providersOverrideImageMap.get(artifactType.getName());
         if (imageProvider != null) {
            String imageKey = imageProvider.setupImage(artifactType);
            if (imageKey != null) {
               return instance.imageRegistry.get(imageKey);
            }
         }

         // Return image for the base image
         return getImage(BaseImage.getBaseImageEnum(artifactType));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return getImage(FrameworkImage.MISSING);
   }

   public static Image getImage(Artifact artifact, OseeImage overlay, Location location) {
      return instance.imageRegistry.get(setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay, location));
   }

   public static String setupImage(Artifact artifact, OseeImage overlay, Location location) {
      return setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay, location);
   }

   public static void registerOverrideImageProvider(ArtifactImageProvider imageProvider, String artifactTypeName) {
      instance.providersOverrideImageMap.put(artifactTypeName, imageProvider);
   }

   public static void registerBaseImage(String artifactTypeName, OseeImage oseeImage) throws OseeDataStoreException {
      ensureArtifactTypeImagesLoaded();

      if (instance.artifactTypeImageMap.containsKey(artifactTypeName)) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,
               "Two ArtifactImageProviders specify image for same artifact type [" + artifactTypeName + "]");
      }
      instance.artifactTypeImageMap.put(artifactTypeName, oseeImage);
   }

   public static Image getAnnotationImage(ArtifactAnnotation.Type annotationType) {
      if (annotationType == ArtifactAnnotation.Type.Warning) {
         return getImage(FrameworkImage.WARNING);
      } else if (annotationType == ArtifactAnnotation.Type.Error) {
         return getImage(FrameworkImage.ERROR);
      }
      return getImage(FrameworkImage.INFO_LG);
   }

   public static synchronized Image getImage(Artifact artifact) {
      return instance.imageRegistry.get(instance.setupImage(artifact));
   }

   public static synchronized ImageDescriptor getImageDescriptor(Artifact artifact) {
      return instance.imageRegistry.getDescriptor(instance.setupImage(artifact));
   }

   public static synchronized Image getProgramImage(String extension) {
      return getImage(new ProgramImage(extension));
   }

   public static synchronized ImageDescriptor getProgramImageDescriptor(String extension) {
      return getImageDescriptor(new ProgramImage(extension));
   }

   public static synchronized Image getImage(OseeImage imageEnum) {
      return instance.imageRegistry.get(setupImage(imageEnum));
   }

   public static synchronized ImageDescriptor getImageDescriptor(OseeImage imageEnum) {
      return instance.imageRegistry.getDescriptor(setupImage(imageEnum));
   }

   public static ImageDescriptor createImageDescriptor(String symbolicBundleName, String imagePath, String imageFileName) {
      return AbstractUIPlugin.imageDescriptorFromPlugin(symbolicBundleName, imagePath + File.separator + imageFileName);
   }

   private synchronized String setupImage(Artifact artifact) {
      try {
         ArtifactImageProvider imageProvider =
               instance.providersOverrideImageMap.get(artifact.getArtifactType().getName());
         if (imageProvider != null) {
            return imageProvider.setupImage(artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return setupImageNoProviders(artifact);
   }

   protected static synchronized String setupImageNoProviders(Artifact artifact) {
      OseeImage baseImageEnum = BaseImage.getBaseImageEnum(artifact);

      if (AccessControlManager.hasLock(artifact)) {
         OseeImage overlay =
               AccessControlManager.hasLockAccess(artifact) ? FrameworkImage.LOCKED_WITH_ACCESS : FrameworkImage.LOCKED_NO_ACCESS;
         return setupImageWithOverlay(baseImageEnum, overlay, Location.TOP_LEFT);
      }

      try {
         if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Error) {
            return setupImageWithOverlay(baseImageEnum, FrameworkImage.ERROR_OVERLAY, Location.BOT_LEFT);
         } else if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Warning) {
            return setupImageWithOverlay(baseImageEnum, FrameworkImage.WARNING_OVERLAY, Location.BOT_LEFT);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return setupImage(baseImageEnum);
   }

   /**
    * @param baseImageName must refer to an image that is already mapped to this key in the imageregistry
    * @param overlay
    * @param location
    * @return the image registry key to the resulting image descriptor
    * @throws OseeArgumentException
    */
   public static synchronized String setupImageWithOverlay(OseeImage baseImageEnum, OseeImage overlay, Location location) {
      String baseImageName = setupImage(baseImageEnum);
      String imageName = baseImageName + "_" + overlay.getImageKey();
      if (instance.imageRegistry.getDescriptor(imageName) == null) {
         Image baseImage = instance.imageRegistry.get(baseImageName);
         ImageDescriptor overlayDescriptor = instance.imageRegistry.getDescriptor(setupImage(overlay));
         instance.imageRegistry.put(imageName, new OverlayImage(baseImage, overlayDescriptor, location));
      }
      return imageName;
   }

   public static String setupImage(OseeImage imageEnum) {
      return instance.setupImage(imageEnum.getImageKey(), imageEnum);
   }

   private synchronized String setupImage(String imageKey, OseeImage imageEnum) {
      if (imageRegistry.getDescriptor(imageKey) == null) {
         ImageDescriptor imageDescriptor = imageEnum.createImageDescriptor();
         if (imageDescriptor == null) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format("Unable to load the image for [%s]",
                  imageEnum.toString()));
            return setupImage(FrameworkImage.MISSING);
         }
         imageRegistry.put(imageKey, imageDescriptor);
      }
      return imageKey;
   }

   public static void setArtifactTypeImageInDb(ArtifactType artifactType, ByteArrayInputStream byteInput) throws OseeDataStoreException, IOException {
      if (byteInput != null) {
         ConnectionHandler.runPreparedUpdate("UPDATE osee_artifact_type SET image = ? where art_type_id = ?",
               byteInput, artifactType.getArtTypeId());
      } else {
         ConnectionHandler.runPreparedUpdate("UPDATE osee_artifact_type SET image = ? where art_type_id = ?",
               SQL3DataType.NULL, artifactType.getArtTypeId());
      }
      loadCache();
      // TODO Need remote event kick to tell other clients of artifact type image changes
   }

   public static OseeImage getArtifactTypeImage(String artifactTypeName) {
      return instance.artifactTypeImageMap.get(artifactTypeName);
   }

   /**
    * @param overrideImageEnum the overrideImageEnum to set
    */
   public static void setOverrideImageEnum(OseeImage overrideImageEnum) {
      ImageManager.overrideImageEnum = overrideImageEnum;
   }

   /**
    * @return the overrideImageEnum
    */
   public static OseeImage getOverrideImageEnum() {
      return overrideImageEnum;
   }

}
