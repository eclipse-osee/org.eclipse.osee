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
package org.eclipse.osee.ats.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Donald G. Dunne
 */
public class GoalArtifact extends AbstractWorkflowArtifact {

   public static enum GoalState {
      InWork,
      Completed,
      Cancelled
   };

   public GoalArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerAtsWorldRelation(AtsRelationTypes.Goal_Member);
   }

   @Override
   public ActionArtifact getParentActionArtifact() {
      return null;
   }

   @Override
   public AbstractWorkflowArtifact getParentSMA() throws OseeCoreException {
      List<Artifact> parents = getRelatedArtifacts(AtsRelationTypes.Goal_Goal);
      if (parents.isEmpty()) {
         return null;
      }
      if (parents.size() == 1) {
         return (AbstractWorkflowArtifact) parents.iterator().next();
      }
      // TODO Two parent goals, what do here?
      return (AbstractWorkflowArtifact) parents.iterator().next();
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() {
      return null;
   }

   public List<Artifact> getMembers() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.Goal_Member, DeletionFlag.EXCLUDE_DELETED);
   }

   public void addMember(Artifact artifact) throws OseeCoreException {
      if (!getMembers().contains(artifact)) {
         addRelation(AtsRelationTypes.Goal_Member, artifact);
      }
   }

   public Collection<GoalArtifact> getInWorkGoals() throws OseeCoreException {
      GoalSearchItem searchItem = new GoalSearchItem("", new ArrayList<TeamDefinitionArtifact>(), false, null);
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(searchItem.performSearchGetResults());
   }
}
