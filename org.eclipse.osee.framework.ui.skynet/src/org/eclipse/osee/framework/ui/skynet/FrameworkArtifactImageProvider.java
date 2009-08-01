/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
      ImageManager.registerBaseImage("Heading", FrameworkImage.HEADING);
      ImageManager.registerBaseImage("Narrative", FrameworkImage.NARRITIVE);
      ImageManager.registerBaseImage("Blam Workflow", FrameworkImage.BLAM);
      ImageManager.registerBaseImage("Folder", FrameworkImage.FOLDER);
      ImageManager.registerBaseImage("User", FrameworkImage.USER);
      ImageManager.registerBaseImage("Global Preferences", FrameworkImage.GEAR);
      ImageManager.registerBaseImage("User Group", FrameworkImage.USERS);
      ImageManager.registerBaseImage("Work Flow Definition", FrameworkImage.WORKFLOW);
      ImageManager.registerBaseImage("Work Page Definition", FrameworkImage.PAGE);
      ImageManager.registerBaseImage("Work Rule Definition", FrameworkImage.RULE);
      ImageManager.registerBaseImage("Work Widget Definition", FrameworkImage.WIDGET);
      ImageManager.registerBaseImage("Universal Group", FrameworkImage.GROUP);

      ImageManager.registerOverrideImageProvider(this, ArtifactTypeManager.getType("User"));
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
         } else
            return ImageManager.getImage(ArtifactTypeManager.getType("User"));
      }
      return null;
   }

}