/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkingCompletePeerReviewReportSearchItem extends VersionTargetedForTeamSearchItem {

   public WorkingCompletePeerReviewReportSearchItem() {
      super(null, null, false, LoadView.WorldEditor);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {

      ArrayList<Artifact> arts = new ArrayList<>();
      for (IAtsTeamWorkflow teamWf : AtsApiService.get().getVersionService().getTargetedForTeamWorkflows(
         getSearchVersionArtifact())) {
         for (IAtsAbstractReview review : AtsApiService.get().getReviewService().getReviews(teamWf)) {
            if (review.isPeerReview() && (review.isInWork() || review.isCompleted())) {
               arts.add((Artifact) review.getStoreObject());
            }
         }
      }
      if (isCancelled()) {
         return EMPTY_SET;
      }
      return arts;
   }

   @Override
   public WorldUISearchItem copy() {
      return new WorkingCompletePeerReviewReportSearchItem();
   }

   @Override
   public String getName() {
      return "Working/Completed Peer Review Search";
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return getName();
   }

}
