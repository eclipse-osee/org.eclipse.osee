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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.RootArtifact, FrameworkImage.ROOT_HIERARCHY, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.HeadingMSWord, FrameworkImage.HEADING, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.Folder, PluginUiImage.FOLDER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.User, FrameworkImage.USER, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.GlobalPreferences, FrameworkImage.GEAR, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UserGroup, FrameworkImage.USERS, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.UniversalGroup, FrameworkImage.GROUP, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.ImplementationDetails,
         FrameworkImage.IMPLEMENTATION_DETAILS, this);

      ArtifactImageManager.registerOverrideImageProvider(this, CoreArtifactTypes.User);
   }

   @Override
   public String setupImage(IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (aArtifact.isDeleted()) {
         return null;
      } else if (((User) aArtifact).isSystemUser()) {
         return ImageManager.setupImage(FrameworkImage.USER_GREY);
      } else if (!((User) aArtifact).isActive()) {
         return ImageManager.setupImage(FrameworkImage.USER_YELLOW);
      } else if (((User) aArtifact).equals(UserManager.getUser())) {
         return ImageManager.setupImage(FrameworkImage.USER_RED);
      }

      return super.setupImage(artifact);
   }

   public static org.eclipse.swt.graphics.Image getUserImage(Collection<User> users) throws OseeCoreException {
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

   private static boolean containsCurrentUser(Collection<User> users) throws OseeCoreException {
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

   private static boolean containsInactiveUser(Collection<User> users) throws OseeCoreException {
      for (User user : users) {
         if (!user.isActive()) {
            return true;
         }
      }
      return false;
   }
}