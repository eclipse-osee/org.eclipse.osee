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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerBaseImage("Root Artifact", FrameworkImage.ROOT_HIERARCHY, this);
      ImageManager.registerBaseImage("Heading", FrameworkImage.HEADING, this);
      ImageManager.registerBaseImage("Narrative", FrameworkImage.NARRITIVE, this);
      ImageManager.registerBaseImage("Blam Workflow", FrameworkImage.BLAM, this);
      ImageManager.registerBaseImage("Folder", FrameworkImage.FOLDER, this);
      ImageManager.registerBaseImage("User", FrameworkImage.USER, this);
      ImageManager.registerBaseImage("Global Preferences", FrameworkImage.GEAR, this);
      ImageManager.registerBaseImage("User Group", FrameworkImage.USERS, this);
      ImageManager.registerBaseImage("Work Flow Definition", FrameworkImage.WORKFLOW, this);
      ImageManager.registerBaseImage("Work Page Definition", FrameworkImage.PAGE, this);
      ImageManager.registerBaseImage("Work Rule Definition", FrameworkImage.RULE, this);
      ImageManager.registerBaseImage("Work Widget Definition", FrameworkImage.WIDGET, this);
      ImageManager.registerBaseImage("Universal Group", FrameworkImage.GROUP, this);

      ImageManager.registerOverrideImageProvider(this, "User");
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
            return ImageManager.getImage(ArtifactTypeManager.getType("User"));
         }
      }
      return null;
   }

}