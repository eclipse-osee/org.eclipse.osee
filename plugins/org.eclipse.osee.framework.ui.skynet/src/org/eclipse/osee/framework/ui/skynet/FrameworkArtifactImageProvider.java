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

package org.eclipse.osee.framework.ui.skynet;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.DynamicImage;
import org.eclipse.osee.framework.ui.skynet.util.DynamicImages;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   private static List<ArtifactTypeToken> laserArtifactTypes = Arrays.asList(CoreArtifactTypes.SystemDesignMsWord,
      CoreArtifactTypes.SupportingContent, CoreArtifactTypes.IndirectSoftwareRequirementMsWord,
      CoreArtifactTypes.TestProcedureWholeWord, CoreArtifactTypes.InterfaceRequirementMsWord,
      CoreArtifactTypes.SystemFunctionMsWord, CoreArtifactTypes.SubsystemFunctionMsWord);

   @Override
   public void init() {
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.RootArtifact, FrameworkImage.ROOT_HIERARCHY, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.HeadingMsWord, FrameworkImage.HEADING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Folder, PluginUiImage.FOLDER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.User, FrameworkImage.USER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.GlobalPreferences, FrameworkImage.GEAR, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UserGroup, FrameworkImage.USERS, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SubscriptionGroup, FrameworkImage.EMAIL, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UniversalGroup, FrameworkImage.GROUP, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.BranchView, FrameworkImage.BRANCH_VIEW, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.FeatureDefinition, FrameworkImage.FEATURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Feature, FrameworkImage.FEATURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetailsMsWord,
         FrameworkImage.IMPLEMENTATION_DETAILS, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetailsProcedureMsWord,
         FrameworkImage.IMPLEMENTATION_DETAILS_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetailsFunctionMsWord,
         FrameworkImage.IMPLEMENTATION_DETAILS_FUNCTION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetailsDrawingMsWord,
         FrameworkImage.IMPLEMENTATION_DETAILS_DRAWING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetailsDataDefinitionMsWord,
         FrameworkImage.IMPLEMENTATION_DETAILS_DATA_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.HardwareRequirementMsWord,
         FrameworkImage.hardware_requirement, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareDesignMsWord, FrameworkImage.software_design,
         this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementDataDefinitionMsWord,
         FrameworkImage.SOFTWARE_REQUIREMENT_DATA_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementDrawingMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_DRAWING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementFunctionMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_FUNCTION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementProcedureMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementMsWord,
         FrameworkImage.software_requirement, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SubsystemDesignMsWord, FrameworkImage.subsystem_design,
         this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SubsystemRequirementMsWord,
         FrameworkImage.subsystem_requirement, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SystemRequirementMsWord,
         FrameworkImage.system_requirement, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.PlainText, new ProgramImage("txt"), this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Url, new ProgramImage("html"), this);

      for (ArtifactTypeToken artifactType : laserArtifactTypes) {
         ArtifactImageManager.registerBaseImage(artifactType, FrameworkImage.LASER, this);
      }

      readDynamicImagesFromUrl();

      ArtifactImageManager.registerOverrideImageProvider(this, CoreArtifactTypes.User);
   }

   private void readDynamicImagesFromUrl() {
      try {
         // first, retrieve the images.json file to see if there are any images
         WebTarget target = ServiceUtil.getOseeClient().jaxRsApi().newTarget("images.json");
         String imagesJson = null;
         try {
            imagesJson = target.request(MediaType.TEXT_PLAIN).get(String.class);
         } catch (Exception ex) {
            return;
         }
         if (Strings.isValid(imagesJson)) {

            // read images.xml
            DynamicImages images = JsonUtil.readValue(imagesJson, DynamicImages.class);

            // for each image
            for (DynamicImage dynamicImage : images.getImages()) {

               try {
                  // get image from url
                  if (Strings.isValid(dynamicImage.getImageUrl())) {

                     URL url = new URL(dynamicImage.getImageUrl());
                     Image image = ImageDescriptor.createFromURL(url).createImage();
                     if (image != null) {
                        // get artifact type
                        ArtifactTypeToken artifactType = null;
                        if (Strings.isNumeric(dynamicImage.getArtifactTypeUuid())) {
                           artifactType = ServiceUtil.getTokenService().getArtifactType(
                              Long.valueOf(dynamicImage.getArtifactTypeUuid()));
                        }
                        if (artifactType == null && Strings.isValid(dynamicImage.getArtifactTypeName())) {
                           artifactType =
                              ServiceUtil.getTokenService().getArtifactType(dynamicImage.getArtifactTypeName());
                        }
                        if (artifactType != null) {
                           // register image for artifact type
                           ArtifactImageManager.registerBaseImage(artifactType, ImageManager.createKeyedImage(
                              artifactType.getIdString(), ImageDescriptor.createFromImage(image)), this);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.logf(FrameworkArtifactImageProvider.class, Level.SEVERE, ex, "Error processing image [%s]",
                     dynamicImage);
               }
            }
         }

      } catch (Exception ex) {
         if (!(ex instanceof NotFoundException)) {
            OseeLog.logf(FrameworkArtifactImageProvider.class, Level.SEVERE, ex,
               "Error processing dynamic artifact images.");
         }
      }
   }

   @Override
   public String setupImage(Artifact artifact) {
      if (artifact.isDeleted()) {
         return null;
      }
      if (artifact.isOfType(CoreArtifactTypes.User)) {
         if (OseeApiService.userSvc().isSystemUser(artifact)) {
            return ImageManager.setupImage(FrameworkImage.USER_GREY);
         } else if (!OseeApiService.userSvc().isActive(artifact)) {
            return ImageManager.setupImage(FrameworkImage.USER_YELLOW);
         } else if (OseeApiService.userSvc().isCurrentUser(artifact)) {
            return ImageManager.setupImage(FrameworkImage.USER_RED);
         }
      }
      return super.setupImage(artifact);
   }

   public static org.eclipse.swt.graphics.Image getUserImage(Collection<UserToken> users) {
      if (users.size() > 0) {
         if (containsSystemUser(users)) {
            return ImageManager.getImage(FrameworkImage.USER_GREY);
         } else if (containsInactiveUser(users)) {
            return ImageManager.getImage(FrameworkImage.USER_YELLOW);
         } else if (containsCurrentUser(users)) {
            return ImageManager.getImage(FrameworkImage.USER_RED);
         } else {
            return ArtifactImageManager.getImage(CoreArtifactTypes.User);
         }
      }
      return null;
   }

   private static boolean containsCurrentUser(Collection<UserToken> users) {
      for (UserToken user : users) {
         if (OseeApiService.user().equals(user)) {
            return true;
         }
      }
      return false;
   }

   private static boolean containsSystemUser(Collection<UserToken> users) {
      for (UserToken user : users) {
         if (user.isSystemUser()) {
            return true;
         }
      }
      return false;
   }

   private static boolean containsInactiveUser(Collection<UserToken> users) {
      for (UserToken user : users) {
         if (!user.isActive()) {
            return true;
         }
      }
      return false;
   }
}