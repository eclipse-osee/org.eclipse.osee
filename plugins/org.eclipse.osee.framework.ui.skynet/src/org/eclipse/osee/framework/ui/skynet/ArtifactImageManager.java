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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactImageManager {
   private static final String EXTENSION_ELEMENT = "ArtifactImageProvider";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;

   private static final Map<IArtifactType, ArtifactImageProvider> providersOverrideImageMap =
      new ConcurrentHashMap<>();
   private static final Map<IArtifactType, KeyedImage> artifactTypeImageMap = new ConcurrentHashMap<>();
   private static final Map<IArtifactType, String> artifactTypeImageProviderMap =
      new ConcurrentHashMap<>();

   private static final String OSEE_DATABASE_PROVIDER = "OSEE Database Provider";

   private static KeyedImage overrideImageEnum;

   static {
      loadCache();
   }

   public synchronized static void loadCache() {

      Set<IArtifactType> imageKeys = new HashSet<>();
      imageKeys.addAll(providersOverrideImageMap.keySet());
      imageKeys.addAll(artifactTypeImageMap.keySet());
      imageKeys.addAll(artifactTypeImageProviderMap.keySet());

      for (IArtifactType imageKey : imageKeys) {
         ImageManager.removeFromRegistry(imageKey.getName());
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
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public static Image getConflictImage(Conflict conflict) {
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
      try {
         KeyedImage keyedImage = null;
         ModificationType modType = null;
         if (change.getItemKind().equals("Artifact")) {
            modType = change.getModificationType();
            if (ChangeImageType.CHANGE_TYPE == changeImageType) {
               keyedImage = BaseImage.getBaseImageEnum(change.getArtifactType());
            } else {
               ChangeItem changeItem = change.getChangeItem();
               if (changeItem != null && isArtDeletedOnDestination(changeItem.getIgnoreType())) {
                  keyedImage = FrameworkImage.DELETE;
                  modType = ModificationType.DELETED_ON_DESTINATION;
               } else {
                  keyedImage = BaseImage.getBaseImageEnum(change.getArtifactType());
               }
            }
         }
         if (change.getItemKind().equals("Attribute")) {
            modType = change.getModificationType();
            if (ChangeImageType.CHANGE_TYPE == changeImageType) {
               keyedImage = FrameworkImage.ATTRIBUTE_MOLECULE;
            } else {
               ChangeItem changeItem = change.getChangeItem();
               if (changeItem != null && isArtDeletedOnDestination(changeItem.getIgnoreType())) {
                  keyedImage = FrameworkImage.DELETE;
                  modType = ModificationType.DELETED_ON_DESTINATION;
               } else {
                  keyedImage = BaseImage.getBaseImageEnum(change.getArtifactType());
               }
            }
         }
         if (change.getItemKind().equals("Relation")) {
            modType = change.getModificationType();
            if (ChangeImageType.CHANGE_TYPE == changeImageType) {
               keyedImage = FrameworkImage.RELATION;
            } else {
               ChangeItem changeItem = change.getChangeItem();
               if (changeItem != null && isArtDeletedOnDestination(changeItem.getIgnoreType())) {
                  keyedImage = FrameworkImage.DELETE;
                  modType = ModificationType.DELETED_ON_DESTINATION;
               } else {
                  keyedImage = FrameworkImage.RELATION;
               }
            }
         }
         if (change.getItemKind().equals("Tuple")) {
            modType = change.getModificationType();
            if (ChangeImageType.CHANGE_TYPE == changeImageType) {
               keyedImage = FrameworkImage.TUPLE;
            } else {
               ChangeItem changeItem = change.getChangeItem();
               if (changeItem != null && isArtDeletedOnDestination(changeItem.getIgnoreType())) {
                  keyedImage = FrameworkImage.DELETE;
                  modType = ModificationType.DELETED_ON_DESTINATION;
               } else {
                  keyedImage = FrameworkImage.RELATION;
               }
            }
         }
         if (keyedImage != null && modType != null) {
            KeyedImage overlay = FrameworkImage.valueOf("OUTGOING_" + modType.toString().toUpperCase());
            toReturn =
               ImageManager.getImage(ImageManager.setupImageWithOverlay(keyedImage, overlay, Location.TOP_LEFT));
         }
      }
      // Do not want to stop UI just cause image isn't resolved correctly
      catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.WARNING, ex, "Error retrieving Aritfact Image for Change [%s]", change);
      }
      return toReturn == null ? ImageManager.getImage(FrameworkImage.QUESTION) : toReturn;
   }

   private static boolean isArtDeletedOnDestination(ChangeIgnoreType type) {
      return type.isDeletedOnDestAndNotResurrected() || type.isDeletedOnDestination();
   }

   public static Image getImage(IArtifactType artifactType) {
      return retrieveImage(artifactType);
   }

   public static Image retrieveImage(IArtifactType artifactType) {
      try {
         if (overrideImageEnum != null) {
            return ImageManager.getImage(overrideImageEnum);
         }

         // Check if image provider provides override for this image
         ArtifactImageProvider imageProvider = providersOverrideImageMap.get(artifactType);
         if (imageProvider != null) {
            String imageKey = imageProvider.setupImage(artifactType);
            if (imageKey != null) {
               return ImageManager.getImage(imageKey);
            }
         }

         // Return image for the base image
         return ImageManager.getImage(BaseImage.getBaseImageEnum(artifactType));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   public static Image getImage(Artifact artifact, KeyedImage overlay, Location location) {
      return ImageManager.getImage(
         ImageManager.setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay, location));
   }

   public synchronized static String setupImage(KeyedImage imageEnum) {
      return ImageManager.setupImage(imageEnum);
   }

   public static String setupImage(Artifact artifact, KeyedImage overlay, Location location) {
      return ImageManager.setupImageWithOverlay(BaseImage.getBaseImageEnum(artifact), overlay, location).getImageKey();
   }

   public static String setupImage(KeyedImage image, KeyedImage overlay, Location location) {
      return ImageManager.setupImageWithOverlay(image, overlay, location).getImageKey();
   }

   public static void registerOverrideImageProvider(ArtifactImageProvider imageProvider, IArtifactType artifactType) {
      providersOverrideImageMap.put(artifactType, imageProvider);
   }

   public synchronized static void registerBaseImage(IArtifactType artifactType, KeyedImage oseeImage, ArtifactImageProvider provider) {
      boolean alreadyProvided = artifactTypeImageMap.containsKey(artifactType);

      String providerId = artifactTypeImageProviderMap.get(artifactType);
      boolean providedByOseeDatabase = OSEE_DATABASE_PROVIDER.equals(providerId);

      // Database can override other providers, don't display error in that cases
      if (alreadyProvided && !providedByOseeDatabase) {
         OseeLog.logf(Activator.class, Level.SEVERE,
            "Two ArtifactImageProviders [%s][%s] specify image for same artifact type [%s]",
            provider.getClass().getSimpleName(), artifactTypeImageProviderMap.get(artifactType), artifactType);
      }
      // Regardless of error, register image if not already provided
      if (!alreadyProvided) {
         artifactTypeImageMap.put(artifactType, oseeImage);
         artifactTypeImageProviderMap.put(artifactType, provider.getClass().getSimpleName());
      }
   }

   public static Image getAnnotationImage(ArtifactAnnotation.Type annotationType) {
      if (annotationType == ArtifactAnnotation.Type.Warning) {
         return ImageManager.getImage(FrameworkImage.WARNING);
      }
      return ImageManager.getImage(FrameworkImage.INFO_LG);
   }

   public synchronized static Image getImage(Artifact artifact) {
      return ImageManager.getImage(setupImage(artifact));
   }

   public synchronized static ImageDescriptor getImageDescriptor(Artifact artifact) {
      return ImageManager.getImageDescriptor(setupImage(artifact));
   }

   private synchronized static String setupImage(Artifact artifact) {
      try {
         IArtifactType type = artifact.getArtifactType();
         ArtifactImageProvider imageProvider = providersOverrideImageMap.get(type);
         if (imageProvider != null) {
            return imageProvider.setupImage(artifact);
         } else {
            KeyedImage imageKey = artifactTypeImageMap.get(type);
            if (imageKey != null) {
               if (AccessControlManager.hasLock(artifact)) {
                  return getLockedImage(imageKey, artifact);
               }
               return ImageManager.setupImage(imageKey);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return setupImageNoProviders(artifact);
   }

   protected synchronized static String setupImageNoProviders(Artifact artifact) {
      KeyedImage baseImageEnum = null;
      try {
         baseImageEnum = BaseImage.getBaseImageEnum(artifact);

         if (AccessControlManager.hasLock(artifact)) {
            return getLockedImage(baseImageEnum, artifact);
         }

         AttributeAnnotationManager.get(artifact);
         if (AttributeAnnotationManager.isAnnotationWarning(artifact)) {
            return ImageManager.setupImageWithOverlay(baseImageEnum, FrameworkImage.WARNING_OVERLAY,
               Location.BOT_LEFT).getImageKey();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return ImageManager.setupImage(baseImageEnum);
   }

   private static String getLockedImage(KeyedImage baseImageEnum, Artifact artifact) {
      KeyedImage locked = FrameworkImage.LOCKED_NO_ACCESS;
      if (AccessControlManager.hasLockAccess(artifact)) {
         locked = FrameworkImage.LOCKED_WITH_ACCESS;
      }
      return ImageManager.setupImageWithOverlay(baseImageEnum, locked, Location.TOP_LEFT).getImageKey();
   }

   public static KeyedImage getArtifactTypeImage(IArtifactType artifactType) {
      return artifactTypeImageMap.get(artifactType);
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
