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
package org.eclipse.osee.ats;

import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactImageProvider extends ArtifactImageProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#init()
    */
   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerProvider(this, ArtifactTypeManager.getType("Version"));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider#getImage(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType("Version")) {
         if (artifact.getSoleAttributeValue("ats.Next Version", false)) {
            return ImageManager.getImage(artifact, AtsImage.NEXT, Location.BOT_RIGHT);
         }
         if (artifact.getSoleAttributeValue("ats.Released", false)) {
            return ImageManager.getImage(artifact, AtsImage.RELEASED, Location.TOP_RIGHT);
         }
      }

      if (artifact instanceof StateMachineArtifact) {
         StateMachineArtifact stateMachine = (StateMachineArtifact) artifact;
         if (stateMachine.isSubscribed(UserManager.getUser())) {
            // was 8,6
            return ImageManager.getImage(artifact, AtsImage.SUBSCRIBED, Location.BOT_RIGHT);
         }
         if (stateMachine.isFavorite(UserManager.getUser())) {
            // was 7,0
            return ImageManager.getImage(artifact, AtsImage.FAVORITE, Location.TOP_RIGHT);
         }
      }

      return ImageManager.getImage(artifact);
   }
}