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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyWorldSearchItem extends UserSearchItem {

   public MyWorldSearchItem(String name) {
      this(name, null);
   }

   public MyWorldSearchItem() {
      this("My World", null);
   }

   public MyWorldSearchItem(String name, User user) {
      super(name, user, AtsImage.GLOBE);
   }

   public MyWorldSearchItem(MyWorldSearchItem myWorldSearchItem) {
      super(myWorldSearchItem);
   }

   @Override
   public Collection<Artifact> searchIt(User user) throws OseeCoreException {
      Set<Artifact> assigned =
            RelationManager.getRelatedArtifacts(Arrays.asList(user), 1, CoreRelationTypes.Users_Artifact);

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(assigned.size());
      for (Artifact artifact : assigned) {
         if ((artifact instanceof TeamWorkFlowArtifact) || (artifact instanceof ReviewSMArtifact)) {
            artifactsToReturn.add(artifact);
         }
         if (artifact instanceof TaskArtifact) {
            artifactsToReturn.add(((TaskArtifact) artifact).getParentTeamWorkflow());
         }
      }
      return artifactsToReturn;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyWorldSearchItem(this);
   }

}