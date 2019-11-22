/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
 ******************************/
package org.eclipse.osee.framework.ui.skynet;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.util.DynamicImage;
import org.eclipse.osee.framework.ui.skynet.util.DynamicImages;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramImage;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   private static List<ArtifactTypeToken> laserArtifactTypes =
      Arrays.asList(CoreArtifactTypes.SystemDesignMsWord, CoreArtifactTypes.SupportingContent,
         CoreArtifactTypes.IndirectSoftwareRequirementMsWord, CoreArtifactTypes.TestProcedureWholeWord,
         CoreArtifactTypes.InterfaceRequirementMsWord, CoreArtifactTypes.SystemFunctionMsWord, CoreArtifactTypes.SubsystemFunctionMsWord);

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
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.HardwareRequirementMsWord, FrameworkImage.hardware_requirement,
         this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareDesignMsWord, FrameworkImage.software_design, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementDataDefinitionMsWord,
         FrameworkImage.SOFTWARE_REQUIREMENT_DATA_DEFINITION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementDrawingMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_DRAWING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementFunctionMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_FUNCTION, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementProcedureMsWord,
         FrameworkImage.SOFTWARE_REQUIERMENT_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SoftwareRequirementMsWord, FrameworkImage.software_requirement,
         this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.SubsystemDesignMsWord, FrameworkImage.subsystem_design, this);
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
         String appServer = OseeClientProperties.getOseeApplicationServer();
         URI uri = UriBuilder.fromUri(appServer).path("images.json").build();

         // first, retrieve the images.json file to see if there are any images
         String imagesJson = null;
         try {
            imagesJson = JaxRsClient.newClient().target(uri).request(MediaType.TEXT_PLAIN).get(String.class);
         } catch (Exception ex) {
            // do nothing if does't exist
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
                           artifactType = ArtifactTypeManager.getType(Long.valueOf(dynamicImage.getArtifactTypeUuid()));
                        }
                        if (artifactType == null && Strings.isValid(dynamicImage.getArtifactTypeName())) {
                           artifactType = ArtifactTypeManager.getType(dynamicImage.getArtifactTypeName());
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
         if (((User) artifact).isSystemUser()) {
            return ImageManager.setupImage(FrameworkImage.USER_GREY);
         } else if (!((User) artifact).isActive()) {
            return ImageManager.setupImage(FrameworkImage.USER_YELLOW);
         } else if (((User) artifact).equals(UserManager.getUser())) {
            return ImageManager.setupImage(FrameworkImage.USER_RED);
         }
      }
      return super.setupImage(artifact);
   }

   public static org.eclipse.swt.graphics.Image getUserImage(Collection<User> users) {
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

   private static boolean containsCurrentUser(Collection<User> users) {
      for (User user : users) {
         if (UserManager.getUser().equals(user)) {
            return true;
         }
      }
      return false;
   }

   private static boolean containsSystemUser(Collection<User> users) {
      for (User user : users) {
         if (user.isSystemUser()) {
            return true;
         }
      }
      return false;
   }

   private static boolean containsInactiveUser(Collection<User> users) {
      for (User user : users) {
         if (!user.isActive()) {
            return true;
         }
      }
      return false;
   }
}