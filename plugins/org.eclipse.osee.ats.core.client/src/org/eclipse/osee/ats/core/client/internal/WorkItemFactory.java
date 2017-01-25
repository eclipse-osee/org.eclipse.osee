/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkItemFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   public WorkItemFactory(IAtsServices services) {
      super(services);
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof TeamWorkFlowArtifact) {
         team = (TeamWorkFlowArtifact) artifact;
      }
      return team;
   }

   @Override
   public IAtsTask getTask(ArtifactId artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactId artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      }
      return review;
   }

   @Override
   public IAtsGoal getGoal(ArtifactId artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      }
      return goal;
   }

   @Override
   public IAtsAction getAction(ArtifactId artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      }
      return action;
   }

}
