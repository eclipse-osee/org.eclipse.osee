/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;

/**
 * @author Donald G. Dunne
 */
public class NewDecisionReviewJob extends Job {
   private final IAtsTeamWorkflow teamWf;
   private final ReviewBlockType reviewBlockType;
   private final String reviewTitle;
   private final String againstState;
   private final List<IAtsDecisionReviewOption> options;
   private final Collection<AtsUser> assignees;
   private final String description;
   private final Date createdDate;
   private final AtsUser createdBy;

   public NewDecisionReviewJob(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, String reviewTitle, String againstState, String description, List<IAtsDecisionReviewOption> options, Collection<AtsUser> assignees, Date createdDate, AtsUser createdBy) {
      super("Creating New Decision Review");
      this.teamWf = teamWf;
      this.reviewTitle = reviewTitle;
      this.againstState = againstState;
      this.reviewBlockType = reviewBlockType;
      this.description = description;
      this.options = options;
      this.assignees = assignees;
      this.createdDate = createdDate;
      this.createdBy = createdBy;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
         DecisionReviewArtifact decArt =
            (DecisionReviewArtifact) AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
               reviewBlockType, reviewTitle, againstState, description, options, assignees, createdDate, createdBy,
               changes).getStoreObject();
         changes.execute();
         AtsEditors.openATSAction(decArt, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Error creating Decision Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

}
