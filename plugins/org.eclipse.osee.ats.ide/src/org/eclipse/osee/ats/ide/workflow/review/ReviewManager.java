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
package org.eclipse.osee.ats.ide.workflow.review;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ReviewManager {

   public static Collection<AbstractReviewArtifact> getReviews(IAtsTeamWorkflow teamWf) {
      return ((TeamWorkFlowArtifact) teamWf.getStoreObject()).getRelatedArtifacts(
         AtsRelationTypes.TeamWorkflowToReview_Review, AbstractReviewArtifact.class);
   }

   public static Collection<IAtsAbstractReview> getReviewsFromCurrentState(TeamWorkFlowArtifact teamArt) {
      return Collections.castAll(getReviews(teamArt, teamArt.getStateMgr().getCurrentState()));
   }

   public static Collection<AbstractReviewArtifact> getReviews(TeamWorkFlowArtifact teamArt, IStateToken state) {
      Set<AbstractReviewArtifact> arts = new HashSet<>();
      for (AbstractReviewArtifact revArt : getReviews(teamArt)) {
         if (revArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "").equals(state.getName())) {
            arts.add(revArt);
         }
      }
      return arts;
   }

   public static AbstractReviewArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractReviewArtifact) {
         return (AbstractReviewArtifact) artifact;
      }
      return null;
   }

}
