/*
 * Created on Jun 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public class FrameworkArtifactImageProvider extends ArtifactImageProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#init()
    */
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#getImage(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
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

}