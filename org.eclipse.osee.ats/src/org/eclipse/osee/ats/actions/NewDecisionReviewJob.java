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

package org.eclipse.osee.ats.actions;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class NewDecisionReviewJob extends Job {
   private final TeamWorkFlowArtifact teamParent;
   private DecisionReviewArtifact decisionReviewArtifact;
   private final ReviewBlockType reviewBlockType;
   private final String reviewTitle;
   private final String againstState;
   private final String options;
   private final Collection<User> assignees;
   private final String description;

   public NewDecisionReviewJob(TeamWorkFlowArtifact teamParent, ReviewBlockType reviewBlockType, String reviewTitle, String againstState, String description, String options, Collection<User> assignees) {
      super("Creating New Decision Review");
      this.teamParent = teamParent;
      this.reviewTitle = reviewTitle;
      this.againstState = againstState;
      this.reviewBlockType = reviewBlockType;
      this.description = description;
      this.options = options;
      this.assignees = assignees;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         decisionReviewArtifact =
               ReviewManager.createNewDecisionReview(teamParent, reviewBlockType, reviewTitle, againstState,
                     description, options, assignees);
         decisionReviewArtifact.persist();
         AtsUtil.openAtsAction(decisionReviewArtifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Error creating Decision Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

}
