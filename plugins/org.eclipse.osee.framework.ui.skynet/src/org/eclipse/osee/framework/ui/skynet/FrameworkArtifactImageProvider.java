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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ArtifactImageManager.registerBaseImage("Root Artifact", FrameworkImage.ROOT_HIERARCHY, this);
      ArtifactImageManager.registerBaseImage("Heading", FrameworkImage.HEADING, this);
      ArtifactImageManager.registerBaseImage("Narrative", FrameworkImage.NARRITIVE, this);
      ArtifactImageManager.registerBaseImage("Blam Workflow", FrameworkImage.BLAM, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Folder, PluginUiImage.FOLDER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.User, FrameworkImage.USER, this);
      ArtifactImageManager.registerBaseImage("Global Preferences", FrameworkImage.GEAR, this);
      ArtifactImageManager.registerBaseImage("User Group", FrameworkImage.USERS, this);
      ArtifactImageManager.registerBaseImage("Work Flow Definition", FrameworkImage.WORKFLOW, this);
      ArtifactImageManager.registerBaseImage("Work Page Definition", FrameworkImage.PAGE, this);
      ArtifactImageManager.registerBaseImage("Work Rule Definition", FrameworkImage.RULE, this);
      ArtifactImageManager.registerBaseImage("Work Widget Definition", FrameworkImage.WIDGET, this);
      ArtifactImageManager.registerBaseImage("Universal Group", FrameworkImage.GROUP, this);

      ArtifactImageManager.registerOverrideImageProvider(this, "User");
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
            return ArtifactImageManager.getImage(ArtifactTypeManager.getType("User"));
         }
      }
      return null;
   }

}