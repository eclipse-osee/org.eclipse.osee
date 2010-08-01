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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactImageManager {
   private static final String EXTENSION_ELEMENT = "ArtifactImageProvider";
   private static final String EXTENSION_ID = SkynetGuiPlugin.PLUGIN_ID + "." + EXTENSION_ELEMENT;

   private static final String SELECT_ARTIFACT_TYPES_IMAGE_QUERY =
      "SELECT art_type_id, image FROM osee_artifact_type where image is not null";
   private static final String UPDATE_ARTIFACT_TYPE_IMAGE =
      "UPDATE osee_artifact_type SET image = ? where art_type_id = ?";

   private static final Map<String, ArtifactImageProvider> providersOverrideImageMap =
      Collections.synchronizedMap(new HashMap<String, ArtifactImageProvider>());
   private static final Map<String, KeyedImage> artifactTypeImageMap =
      Collections.synchronizedMap(new HashMap<String, KeyedImage>());
   private static final Map<String, String> artifactTypeImageProviderMap =
      Collections.synchronizedMap(new HashMap<String, String>());

   private static final String OSEE_DATABASE_PROVIDER = "OSEE Database Provider";

   private static boolean artifactTypeImagesLoaded;
   private static KeyedImage overrideImageEnum;

   static {
      loadCache();
   }

   public synchronized static void loadCache() {
      artifactTypeImagesLoaded = false;

      Set<String> imageKeys = new HashSet<String>();
      imageKeys.addAll(providersOverrideImageMap.keySet());
      imageKeys.addAll(artifactTypeImageMap.keySet());
      imageKeys.addAll(artifactTypeImageProviderMap.keySet());

      for (String imageKey : imageKeys) {
         ImageManager.removeFromRegistry(imageKey);
      }

      providersOverrideImageMap.clear();
      artifactTypeImageMap.clear();
      artifactTypeImageProviderMap.clear();

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

   private synchronized static void ensureArtifactTypeImagesLoaded() throws OseeDataStoreException {
      if (!artifactTypeImagesLoaded) {
         artifactTypeImagesLoaded = true;
         // Load base images from database (which can override the ImageManager.registerImage() calls provided
         // through the ArtifactImageProviders
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES_IMAGE_QUERY);

            while (chStmt.next()) {
               try {
                  ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
                  artifactTypeImageMap.put(artifactType.getName(),
                     BaseImage.getBaseImageEnum(artifactType, Lib.inputStreamToBytes(chStmt.getBinaryStream("image"))));
                  artifactTypeImageProviderMap.put(artifactType.getName(), OSEE_DATABASE_PROVIDER);
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
         return ImageManager.getImage(FrameworkImage.ATTRIBUTE_MOLECULE);
      }
      if (conflict instanceof ArtifactConflict) {
         return getImage(conflict.getArtifact());
      }
      throw new OseeArgumentException("Conflict not of supported type");
   }

   public static Image getChangeTypeImage(Change change) {
      return getChangeImage(change, ChangeImageType.CHANGE_TYPE);
   }

   public static Image getChangeKindImage(Change change) {
      return getChangeImage(change, ChangeImageType.CHANGE_KIND);
   }

   private static enum ChangeImageType {
      CHANGE_KIND,
      CHANGE_TYPE;
   }

   private static Image getChangeImage(Change change, ChangeImageType changeImageType) {
      Image toReturn = null;
      KeyedImage keyedImage = null;
      ModificationType modType = null;
      if (change.getItemKind().equals("Artifact")) {
         modType = change.getModificationType();
         keyedImage = BaseImage.getBaseImageEnum(change.getArtifactType());
      }
      if (change.getItemKind().equals("Attribute")) {
         if (ChangeImageType.CHANGE_TYPE == changeImageType) {
            modType = change.getModificationType();
            keyedImage = FrameworkImage.ATTRIBUTE_MOLECULE;
         } else {
            modType = change.getModificationType();
            keyedImage = BaseImage.getBaseImageEnum(change.getArtifactType());
         }
      }
      if (change.getItemKind().equals("Relation")) {
         keyedImage = FrameworkImage.RELATION;
         modType = change.getModificationType();
      }
      if (keyedImage != null && modType != null) {
         KeyedImage overlay = FrameworkImage.valueOf("OUTGOING_" + modType.toString());
         toReturn = ImageManager.getImage(ImageManager.setupImageWithOverlay(keyedImage, overlay, Location.TOP_LEFT));
      }
      return toReturn;
   }

   public static Image getImage(IArtifactType artifactType) {
      try {
         return getImage(ArtifactTypeManager.getType(artifactType));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   public static Image getImage(ArtifactType artifactType) {
      try {
         if (overrideImageEnum != null) {
            return ImageManager.getImage(overrideImageEnum);
         }

         // Check if image provider provides override for this image
         ArtifactImageProvider imageProvider = providersOverrideImageMap.get(artifactType.getName());
         if (imageProvider != null) {
            String imageKey = imageProvider.setupImage(artifactType);
            if (imageKey != null) {
               return ImageManager.getImage(imageKey);
            }
         }

         // Return image for the base image
         return ImageManager.getImage(BaseImage.getBaseImageEnum(artifactType));
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   public static Image getImage(Artifact artifact, KeyedImage overlay, Location location) {
      return ImageManager.getImage(ImageManager.setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay,
         location));
   }

   public synchronized static String setupImage(KeyedImage imageEnum) {
      return ImageManager.setupImage(imageEnum);
   }

   public static String setupImage(Artifact artifact, KeyedImage overlay, Location location) {
      return ImageManager.setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay, location).getImageKey();
   }

   public static void registerOverrideImageProvider(ArtifactImageProvider imageProvider, String artifactTypeName) {
      providersOverrideImageMap.put(artifactTypeName, imageProvider);
   }

   public synchronized static void registerBaseImage(IArtifactType artifactType, KeyedImage oseeImage, ArtifactImageProvider provider) throws OseeDataStoreException {
      registerBaseImage(artifactType.getName(), oseeImage, provider);
   }

   public synchronized static void registerBaseImage(String artifactTypeName, KeyedImage oseeImage, ArtifactImageProvider provider) throws OseeDataStoreException {
      ensureArtifactTypeImagesLoaded();

      boolean alreadyProvided = artifactTypeImageMap.containsKey(artifactTypeName);
      boolean providedByOseeDatabase =
         artifactTypeImageProviderMap.get(artifactTypeName) != null && artifactTypeImageProviderMap.get(
            artifactTypeName).equals(OSEE_DATABASE_PROVIDER);

      // Database can override other providers, don't display error in that cases
      if (alreadyProvided && !providedByOseeDatabase) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
            "Two ArtifactImageProviders [%s][%s] specify image for same artifact type [%s]",
            provider.getClass().getSimpleName(), artifactTypeImageProviderMap.get(artifactTypeName), artifactTypeName));
      }
      // Regardless of error, register image if not already provided
      if (!alreadyProvided) {
         artifactTypeImageMap.put(artifactTypeName, oseeImage);
         artifactTypeImageProviderMap.put(artifactTypeName, provider.getClass().getSimpleName());
      }
   }

   public static Image getAnnotationImage(ArtifactAnnotation.Type annotationType) {
      if (annotationType == ArtifactAnnotation.Type.Warning) {
         return ImageManager.getImage(FrameworkImage.WARNING);
      }
      return ImageManager.getImage(FrameworkImage.INFO_LG);
   }

   public synchronized static Image getImage(IArtifact artifact) {
      return ImageManager.getImage(setupImage(artifact));
   }

   public synchronized static ImageDescriptor getImageDescriptor(IArtifact artifact) {
      return ImageManager.getImageDescriptor(setupImage(artifact));
   }

   private synchronized static String setupImage(IArtifact artifact) {
      try {
         Artifact castedArtifact = artifact.getFullArtifact();
         ArtifactImageProvider imageProvider = providersOverrideImageMap.get(artifact.getArtifactType().getName());
         if (imageProvider != null) {
            return imageProvider.setupImage(castedArtifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return setupImageNoProviders(artifact);
   }

   protected synchronized static String setupImageNoProviders(IArtifact artifact) {
      KeyedImage baseImageEnum = null;
      try {
         Artifact castedArtifact = artifact.getFullArtifact();
         baseImageEnum = BaseImage.getBaseImageEnum(castedArtifact);

         if (AccessControlManager.hasLock(castedArtifact)) {
            KeyedImage overlay =
               AccessControlManager.hasLockAccess(castedArtifact) ? FrameworkImage.LOCKED_WITH_ACCESS : FrameworkImage.LOCKED_NO_ACCESS;
            return ImageManager.setupImageWithOverlay(baseImageEnum, overlay, Location.TOP_LEFT).getImageKey();
         }

         if (castedArtifact.isAnnotationWarning()) {
            return ImageManager.setupImageWithOverlay(baseImageEnum, FrameworkImage.WARNING_OVERLAY, Location.BOT_LEFT).getImageKey();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return ImageManager.setupImage(baseImageEnum);
   }

   public static void setArtifactTypeImageInDb(IArtifactType artifactType, ByteArrayInputStream byteInput) throws OseeCoreException {
      setArtifactTypeImageInDb(ArtifactTypeManager.getType(artifactType), byteInput);
   }

   public static void setArtifactTypeImageInDb(ArtifactType artifactType, ByteArrayInputStream byteInput) throws OseeDataStoreException {
      Object imageData = byteInput != null ? byteInput : SQL3DataType.BLOB;
      ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_TYPE_IMAGE, imageData, artifactType.getId());
      loadCache();

      // TODO Need remote event kick to tell other clients of artifact type image changes
   }

   public static KeyedImage getArtifactTypeImage(String artifactTypeName) {
      return artifactTypeImageMap.get(artifactTypeName);
   }

   /**
    * @param overrideImageEnum the overrideImageEnum to set
    */
   public static void setOverrideImageEnum(KeyedImage overrideImageEnum) {
      ArtifactImageManager.overrideImageEnum = overrideImageEnum;
   }

   /**
    * @return the overrideImageEnum
    */
   public static KeyedImage getOverrideImageEnum() {
      return overrideImageEnum;
   }

}
