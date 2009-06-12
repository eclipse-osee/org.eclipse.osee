/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class MyOrigSearchItem extends UserSearchItem {

   private final OriginatedState originatedState;

   public enum OriginatedState {
      InWork, All
   };

   public MyOrigSearchItem(String name, User user, OriginatedState originatedState) {
      super(name, user, FrameworkImage.USER);
      this.originatedState = originatedState;
   }

   public MyOrigSearchItem(MyOrigSearchItem myOrigSearchItem) {
      super(myOrigSearchItem, null);
      this.originatedState = myOrigSearchItem.originatedState;
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {

      Collection<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromAttribute(ATSAttributes.LOG_ATTRIBUTE.getStoreName(),
                  "%type=\"Originated\" userId=\"" + user.getUserId() + "\"%", AtsPlugin.getAtsBranch());

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact instanceof StateMachineArtifact) {
            if (originatedState == OriginatedState.All || (originatedState == OriginatedState.InWork && !((StateMachineArtifact) artifact).getSmaMgr().isCancelledOrCompleted())) {
               artifactsToReturn.add(artifact);
            }
         }
      }
      return artifactsToReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new MyOrigSearchItem(this);
   }

}
