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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.AttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.revision.RevisionChange;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Ryan D. Brooks
 */
public class ImageManager {
   private static final String EXTENSION_ELEMENT = "ArtifactImageProvider";
   private static final String EXTENSION_ID = SkynetGuiPlugin.PLUGIN_ID + "." + EXTENSION_ELEMENT;

   private static final ImageManager instance = new ImageManager();

   private final Map<ArtifactType, ArtifactImageProvider> providersMap =
         Collections.synchronizedMap(new HashMap<ArtifactType, ArtifactImageProvider>());
   private final ImageRegistry imageRegistry = SkynetGuiPlugin.getInstance().getImageRegistry();

   private ImageManager() {
      List<ArtifactImageProvider> providers =
            new ExtensionDefinedObjects<ArtifactImageProvider>(EXTENSION_ID, EXTENSION_ELEMENT, "class").getObjects();

      for (ArtifactImageProvider imageProvider : providers) {
         try {
            imageProvider.init();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public static Image getConflictImage(Conflict conflict) throws OseeCoreException {
      if (conflict instanceof AttributeConflict) {
         return getImage(FrameworkImage.ATTRIBUTE);
      }
      if (conflict instanceof ArtifactConflict) {
         return getImage(conflict.getArtifact());
      }
      throw new OseeArgumentException("Conflict not of supported type");
   }

   private static Image getChangeTypeImageInternal(String baseImageName, ChangeType changeType, ModificationType modType) throws OseeArgumentException {
      if (changeType == CONFLICTING && modType == NEW) {
         modType = ModificationType.CHANGE;
      }
      OseeImage overlay = FrameworkImage.valueOf(changeType + "_" + modType.getDisplayName());
      return instance.imageRegistry.get(instance.setupImageWithOverlay(baseImageName, overlay, Location.TOP_LEFT));
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

   public static Image getChangeImage(RevisionChange change) throws OseeArgumentException, OseeDataStoreException, OseeTypeDoesNotExist {
      if (change instanceof AttributeChange) {
         return getAttributeChangeImage(change.getChangeType(), change.getModificationType());
      }
      if (change instanceof ArtifactChange) {

         return getArtifactChangeImage(((ArtifactChange) change).getArtifact().getArtifactType(),
               change.getChangeType(), change.getModificationType());
      }
      if (change instanceof RelationLinkChange) {
         return getRelationChangeImage(change.getChangeType(), change.getModificationType());
      }
      throw new OseeArgumentException("Change not of supported type");
   }

   private static Image getArtifactChangeImage(ArtifactType artifactType, ChangeType changeType, ModificationType modType) throws OseeArgumentException {
      return getChangeTypeImageInternal(instance.setupBaseImage(artifactType), changeType, modType);
   }

   private static Image getAttributeChangeImage(ChangeType changeType, ModificationType modType) throws OseeArgumentException {
      return getChangeTypeImageInternal(instance.setupImage(FrameworkImage.ATTRIBUTE), changeType, modType);
   }

   private static Image getRelationChangeImage(ChangeType changeType, ModificationType modType) throws OseeArgumentException {
      return getChangeTypeImageInternal(instance.setupImage(FrameworkImage.RELATION), changeType, modType);
   }

   public static Image getImage(ArtifactType artifactType) {
      try {
         ArtifactImageProvider imageProvider = instance.providersMap.get(artifactType);
         if (imageProvider == null) {
            return getBaseImage(artifactType);
         }
         return imageProvider.getImage(artifactType);
      } catch (OseeCoreException ex) {
         return getMissingImage();
      }
   }

   public static synchronized Image getMissingImage() {
      Image missingImage = instance.imageRegistry.get("missing");
      if (missingImage == null) {
         instance.imageRegistry.put("missing", ImageDescriptor.getMissingImageDescriptor());
         missingImage = instance.imageRegistry.get("missing");
      }
      return missingImage;
   }

   public static Image getImage(Artifact artifact, OseeImage overlay, Location location) {
      return instance.imageRegistry.get(instance.setupImageWithOverlay(instance.setupBaseImage(artifact), overlay,
            location));
   }

   public static void registerProvider(ArtifactImageProvider imageProvider, ArtifactType artifactType) {
      instance.providersMap.put(artifactType, imageProvider);
   }

   public static Image getImage(ArtifactAnnotation.Type annotationType) {
      if (annotationType == ArtifactAnnotation.Type.Warning) {
         return getImage(FrameworkImage.WARNING);
      } else if (annotationType == ArtifactAnnotation.Type.Error) {
         return getImage(FrameworkImage.ERROR);
      }
      return getMissingImage();
   }

   public static synchronized Image getImage(Artifact artifact) {/*
                                                                                                                                                                     if (instance.imageRegistry.get("overrideImage") != null) {
                                                                                                                                                                        return instance.imageRegistry.get("overrideImage");
                                                                                                                                                                     }

                                                                                                                                                                     String baseImageName = artifact.getArtifactTypeName();
                                                                                                                                                                     Image baseImage;
                                                                                                                                                                     if (artifact instanceof NativeArtifact) {
                                                                                                                                                                        try {
                                                                                                                                                                           baseImage = getImageForProgram(((NativeArtifact) artifact).getFileExtension());
                                                                                                                                                                        } catch (Exception ex) {
                                                                                                                                                                           baseImage = getBaseImage(artifact.getArtifactType());
                                                                                                                                                                        }
                                                                                                                                                                     } else {
                                                                                                                                                                        baseImage = getBaseImage(artifact.getArtifactType());
                                                                                                                                                                     }

                                                                                                                                                                     if (AccessControlManager.hasLock(artifact)) {
                                                                                                                                                                        OseeImage overlay =
                                                                                                                                                                              AccessControlManager.getInstance().hasLockAccess(artifact) ? FrameworkImage.LOCKED_WITH_ACCESS : FrameworkImage.LOCKED_NO_ACCESS;
                                                                                                                                                                        return instance.getImageWithOverlay(baseImageName, baseImage, overlay.getFileName(), Location.TOP_LEFT);
                                                                                                                                                                     }

                                                                                                                                                                     try {

                                                                                                                                                                        if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Error) {
                                                                                                                                                                           return instance.getImageWithOverlay(baseImageName, baseImage, FrameworkImage.ERROR_OVERLAY.getFileName(),
                                                                                                                                                                                 Location.BOT_LEFT);
                                                                                                                                                                        } else if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Warning) {
                                                                                                                                                                           return instance.getImageWithOverlay(baseImageName, baseImage, FrameworkImage.WARNING_OVERLAY.getFileName(),
                                                                                                                                                                                 Location.BOT_LEFT);
                                                                                                                                                                        }
                                                                                                                                                                     } catch (OseeCoreException ex) {
                                                                                                                                                                        OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                                                                                                                                                                     }
                                                                                                                                                                     return baseImage;*/
      return instance.imageRegistry.get(instance.setupImage(artifact));
   }

   public static synchronized ImageDescriptor getImageDescriptor(Artifact artifact) {
      /*
       if (instance.imageRegistry.getDescriptor("overrideImage") != null) {
          return instance.imageRegistry.getDescriptor("overrideImage");
       }

       String baseImageName = artifact.getArtifactTypeName();
       ImageDescriptor baseImageDescriptor;
       Image baseImage;
       if (artifact instanceof NativeArtifact) {
          try {
             String extension = ((NativeArtifact) artifact).getFileExtension();
             baseImageDescriptor = getImageDescriptorForProgram(extension);
             baseImage = getImageForProgram(extension);
          } catch (Exception ex) {
             baseImageDescriptor = getBaseImageDescriptor(artifact.getArtifactType());
             baseImage = getBaseImage(artifact.getArtifactType());
          }
       } else {
          baseImageDescriptor = getBaseImageDescriptor(artifact.getArtifactType());
          baseImage = getBaseImage(artifact.getArtifactType());
       }

       if (AccessControlManager.hasLock(artifact)) {
          OseeImage overlay =
                AccessControlManager.getInstance().hasLockAccess(artifact) ? FrameworkImage.LOCKED_WITH_ACCESS : FrameworkImage.LOCKED_NO_ACCESS;
          return instance.getImageDescriptorWithOverlay(baseImageName, baseImage, overlay.getFileName(),
                Location.TOP_LEFT);
       }

       try {

          if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Error) {
             return instance.getImageDescriptorWithOverlay(baseImageName, baseImage,
                   FrameworkImage.ERROR_OVERLAY.getFileName(), Location.BOT_LEFT);
          } else if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Warning) {
             return instance.getImageDescriptorWithOverlay(baseImageName, baseImage,
                   FrameworkImage.WARNING_OVERLAY.getFileName(), Location.BOT_LEFT);
          }
       } catch (OseeCoreException ex) {
          OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
       }
       return baseImageDescriptor;
       */
      return instance.imageRegistry.getDescriptor(instance.setupImage(artifact));
   }

   public static synchronized Image getProgramImage(String extenstion) throws OseeArgumentException {
      return instance.imageRegistry.get(instance.setupProgramImage(extenstion));
   }

   public static synchronized ImageDescriptor getProgramImageDescriptor(String extenstion) throws OseeArgumentException {
      return instance.imageRegistry.getDescriptor(instance.setupProgramImage(extenstion));
   }

   /**
    * This method is intended to only be called by ArtifactImageProviders
    * 
    * @param artifactType
    * @return
    */
   public static synchronized Image getBaseImage(ArtifactType artifactType) {
      return instance.imageRegistry.get(instance.setupBaseImage(artifactType));
   }

   public static synchronized Image getImage(OseeImage imageEnum) {
      return instance.imageRegistry.get(instance.setupImage(imageEnum));
   }

   public static synchronized ImageDescriptor getImageDescriptor(OseeImage imageEnum) {
      return instance.imageRegistry.getDescriptor(instance.setupImage(imageEnum));
   }

   /**
    * @param imageEnum representing the image that will be returned for all artifacts and artifact types
    */
   public static synchronized void setupOverrideImage(OseeImage imageEnum) {
      instance.imageRegistry.remove("overrideImage");
      // causes the image descriptor to be put in the registry
      instance.setupImage(imageEnum.getSymbolicBundleName(), "overrideImage", imageEnum.getPath(),
            imageEnum.getFileName());
   }

   private synchronized String setupBaseImage(Artifact artifact) {
      if (imageRegistry.getDescriptor("overrideImage") != null) {
         return "overrideImage";
      }

      if (artifact instanceof NativeArtifact) {
         try {
            String extension = ((NativeArtifact) artifact).getFileExtension();
            return setupProgramImage(extension);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return setupBaseImage(artifact.getArtifactType());
   }

   private synchronized String setupImage(Artifact artifact) {
      String baseImageName = setupBaseImage(artifact);

      if (AccessControlManager.hasLock(artifact)) {
         OseeImage overlay =
               AccessControlManager.getInstance().hasLockAccess(artifact) ? FrameworkImage.LOCKED_WITH_ACCESS : FrameworkImage.LOCKED_NO_ACCESS;
         return setupImageWithOverlay(baseImageName, overlay, Location.TOP_LEFT);
      }

      try {
         if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Error) {
            return setupImageWithOverlay(baseImageName, FrameworkImage.ERROR_OVERLAY, Location.BOT_LEFT);
         } else if (artifact.getMainAnnotationType() == ArtifactAnnotation.Type.Warning) {
            return setupImageWithOverlay(baseImageName, FrameworkImage.WARNING_OVERLAY, Location.BOT_LEFT);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return baseImageName;
   }

   /**
    * @param baseImageName must refer to an image that is already mapped to this key in the imageregistry
    * @param overlay
    * @param location
    * @return
    */
   private synchronized String setupImageWithOverlay(String baseImageName, OseeImage overlay, Location location) {
      String imageName = baseImageName + "_" + overlay.toString();
      ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(imageName);
      if (imageDescriptor == null) {
         Image baseImage = imageRegistry.get(baseImageName);
         ImageDescriptor overlayDescriptor = imageRegistry.getDescriptor(setupImage(overlay));
         imageRegistry.put(imageName, new OverlayImage(baseImage, overlayDescriptor, location));
      }
      return imageName;
   }

   private synchronized String setupBaseImage(ArtifactType artifactType) {
      String imageName = artifactType.getName();
      if (imageRegistry.getDescriptor(imageName) == null) {
         if (artifactType.getImageData() == null) {
            imageName = setupImage(FrameworkImage.LASER);
         } else {
            ImageDescriptor baseDescriptor =
                  ImageDescriptor.createFromImageData(new ImageData(new ByteArrayInputStream(
                        artifactType.getImageData())));
            imageRegistry.put(imageName, baseDescriptor);
         }
      }
      return imageName;
   }

   private String setupImage(OseeImage imageEnum) {
      return setupImage(imageEnum.getSymbolicBundleName(), imageEnum.toString(), imageEnum.getPath(),
            imageEnum.getFileName());
   }

   private synchronized String setupImage(String symbolicBundleName, String imageName, String imagePath, String imageFileName) {
      if (imageRegistry.getDescriptor(imageName) == null) {
         ImageDescriptor imageDescriptor =
               AbstractUIPlugin.imageDescriptorFromPlugin(symbolicBundleName,
                     imagePath + File.separator + imageFileName);
         imageRegistry.put(imageName, imageDescriptor);
      }
      return imageName;
   }

   private synchronized String setupProgramImage(String extenstion) throws OseeArgumentException {
      if (extenstion == null) {
         throw new OseeArgumentException("extenstion can not be null");
      }
      String imageName = "program" + extenstion;

      if (imageRegistry.getDescriptor(imageName) == null) {
         Program program = Program.findProgram(extenstion);
         if (program == null || program.getImageData() == null) {
            throw new OseeArgumentException("no program exists for this extenstion");
         } else {
            ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageData(program.getImageData());
            imageRegistry.put(imageName, imageDescriptor);
         }
      }
      return imageName;
   }
}