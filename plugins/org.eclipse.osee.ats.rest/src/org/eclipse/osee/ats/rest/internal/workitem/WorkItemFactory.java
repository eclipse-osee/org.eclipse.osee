/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.review.DecisionReview;
import org.eclipse.osee.ats.core.review.PeerToPeerReview;
import org.eclipse.osee.ats.core.workflow.AbstractWorkItemFactory;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.workitem.model.Action;
import org.eclipse.osee.ats.rest.internal.workitem.model.Goal;
import org.eclipse.osee.ats.rest.internal.workitem.model.Task;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   private final IAtsServer atsServer;

   public WorkItemFactory(IAtsServer atsServer) {
      super(atsServer);
      this.atsServer = atsServer;
   }

   @Override
   public IAtsGoal getGoal(ArtifactId artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Goal)) {
            goal = new Goal(services.getLogger(), atsServer, (ArtifactReadable) artifact);
         }
      }
      return goal;
   }

   @Override
   public IAtsTask getTask(ArtifactId artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Task)) {
            task = new Task(services.getLogger(), atsServer, (ArtifactReadable) artifact);
         }
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactId artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
            review = new PeerToPeerReview(services.getLogger(), services, artRead);
         } else {
            review = new DecisionReview(services.getLogger(), services, artRead);
         }
      }
      return review;
   }

   @Override
   public IAtsAction getAction(ArtifactId artifact) {
      IAtsAction action = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Action)) {
            action = new Action(atsServer, artRead);
         }
      }
      return action;
   }

}
