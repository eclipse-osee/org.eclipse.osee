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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class ShowOpenWorkflowsByReviewType extends WorldUISearchItem {

   private final boolean showFinished;
   private final boolean showWorkflow;
   private final WorkItemType workItemType;

   public ShowOpenWorkflowsByReviewType(String displayName, WorkItemType workItemType, boolean showFinished, boolean showWorkflow, KeyedImage oseeImage) {
      super(displayName, oseeImage);
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
   public Collection<Artifact> performSearch(SearchType searchType)  {
      IAtsQuery query = AtsClientService.get().getQueryService().createQuery(workItemType);
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
