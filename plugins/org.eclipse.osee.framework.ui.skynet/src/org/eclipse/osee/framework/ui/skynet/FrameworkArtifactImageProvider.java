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

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.RootArtifact, FrameworkImage.ROOT_HIERARCHY, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Heading, FrameworkImage.HEADING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Folder, PluginUiImage.FOLDER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.User, FrameworkImage.USER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.GlobalPreferences, FrameworkImage.GEAR, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UserGroup, FrameworkImage.USERS, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.WorkFlowDefinition, FrameworkImage.WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.WorkPageDefinition, FrameworkImage.PAGE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.WorkRuleDefinition, FrameworkImage.RULE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.WorkWidgetDefinition, FrameworkImage.WIDGET, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UniversalGroup, FrameworkImage.GROUP, this);

      ArtifactImageManager.registerOverrideImageProvider(this, CoreArtifactTypes.User);
   }

   @Override
   public String setupImage(Artifact artifact) throws OseeCoreException {
      if (artifact.isDeleted()) {
         return null;
      } else if (((User) artifact).isSystemUser()) {
         return ImageManager.setupImage(FrameworkImage.USER_GREY);
      } else if (!((User) artifact).isActive()) {
         return ImageManager.setupImage(FrameworkImage.USER_YELLOW);
      } else if (((User) artifact).equals(UserManager.getUser())) {
         return ImageManager.setupImage(FrameworkImage.USER_RED);
      }

      return super.setupImage(artifact);
   }

   public static org.eclipse.swt.graphics.Image getUserImage(Collection<User> users) throws OseeCoreException {
      if (users.size() > 0) {
         if (UserManager.isUserSystem(users)) {
            return ImageManager.getImage(FrameworkImage.USER_GREY);
         } else if (UserManager.isUserInactive(users)) {
            return ImageManager.getImage(FrameworkImage.USER_YELLOW);
         } else if (UserManager.isUserCurrentUser(users)) {
            return ImageManager.getImage(FrameworkImage.USER_RED);
         } else {
            return ArtifactImageManager.getImage(CoreArtifactTypes.User);
         }
      }
      return null;
   }

}