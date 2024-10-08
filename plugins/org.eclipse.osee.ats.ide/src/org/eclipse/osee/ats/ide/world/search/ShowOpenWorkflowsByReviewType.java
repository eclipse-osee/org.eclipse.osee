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

package org.eclipse.osee.ats.ide.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ShowOpenWorkflowsByReviewType extends WorldUISearchItem {

   private final boolean showFinished;
   private final boolean showWorkflow;
   private final WorkItemType workItemType;

   public ShowOpenWorkflowsByReviewType(String displayName, WorkItemType workItemType, boolean showFinished, boolean showWorkflow, AtsImage atsImage) {
      super(displayName, atsImage);
      Conditions.checkNotNull(workItemType, "workItemType");
      this.workItemType = workItemType;
      this.showFinished = showFinished;
      this.showWorkflow = showWorkflow;
      Conditions.assertTrue(
         Arrays.asList(WorkItemType.Review, WorkItemType.DecisionReview, WorkItemType.PeerReview).contains(
            workItemType),
         "WorkItemType must be a review type, not %s", workItemType.getDisplayName());
   }

   public ShowOpenWorkflowsByReviewType(ShowOpenWorkflowsByReviewType showOpenWorkflowsByArtifactType) {
      super(showOpenWorkflowsByArtifactType);
      this.showFinished = showOpenWorkflowsByArtifactType.showFinished;
      this.showWorkflow = showOpenWorkflowsByArtifactType.showWorkflow;
      this.workItemType = showOpenWorkflowsByArtifactType.workItemType;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      IAtsQuery query = AtsApiService.get().getQueryService().createQuery(workItemType);
      if (!showFinished) {
         query.andStateType(StateType.Working);
      }
      if (showWorkflow) {
         return Collections.castAll(query.createFilter().getTeamWorkflows());
      } else {
         return Collections.castAll(query.getResultArtifacts().getList());
      }
   }

   @Override
   public WorldUISearchItem copy() {
      return new ShowOpenWorkflowsByReviewType(this);
   }

}
