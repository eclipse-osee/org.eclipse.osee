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
public class UserArtifactImageProvider extends ArtifactImageProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#init()
    */
   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerProvider(this, ArtifactTypeManager.getType("User"));
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
      return null;
   }
}