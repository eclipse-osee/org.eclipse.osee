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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyWorldSearchItemOld extends UserSearchItem {

   public MyWorldSearchItemOld(String name) {
      this(name, null);
   }

   public MyWorldSearchItemOld() {
      super("My World - Old", null);
   }

   public MyWorldSearchItemOld(String name, User user) {
      super(name, user);
   }

   public Collection<Artifact> searchIt(User user) throws Exception {
      List<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromAttribute(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
                  "%<" + user.getUserId() + ">%", BranchPersistenceManager.getAtsBranch());
      artifacts.addAll(RelationManager.getRelatedArtifacts(artifacts, 4, AtsRelation.SmaToTask_Sma,
            AtsRelation.TeamWorkflowToReview_Team, AtsRelation.ActionToWorkflow_Action));

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());

      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(ActionArtifact.ARTIFACT_NAME)) {
            artifactsToReturn.add(artifact);
         }
      }
      return artifactsToReturn;
   }
}
