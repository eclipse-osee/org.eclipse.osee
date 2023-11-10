/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.column;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksReviewsColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteTasksReviewsColumnUI instance = new PercentCompleteTasksReviewsColumnUI();

   public static PercentCompleteTasksReviewsColumnUI getInstance() {
      return instance;
   }

   private PercentCompleteTasksReviewsColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".taskReviewPercentComplete", "Task and Review Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the tasks and reviews related to the workflow.\n\nCalculation: total percent of all tasks and reviews / number of tasks and reviews");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteTasksReviewsColumnUI copy() {
      PercentCompleteTasksReviewsColumnUI newXCol = new PercentCompleteTasksReviewsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsObject) {
            if (((IAtsObject) element).isOfType(TeamWorkflow, Action)) {
               return String.valueOf(
                  getPercentCompleteFromTasksAndReviews(AtsApiService.get().getQueryServiceIde().getArtifact(element)));
            }
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromTasksAndReviews(Artifact artifact) {
      if (artifact.isOfType(Action)) {
         double percent = 0;
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent +=
                  getPercentCompleteFromTasksAndReviews(AtsApiService.get().getQueryServiceIde().getArtifact(team));
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / AtsApiService.get().getWorkItemService().getTeams(artifact).size();
         return rollPercent.intValue();
      }
      int spent = 0;
      int size = 0;
      if (artifact.isOfType(TeamWorkflow)) {
         Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks((TeamWorkFlowArtifact) artifact);
         for (IAtsTask task : tasks) {
            spent += AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(task);
         }
         size = tasks.size();

         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) artifact;
         Collection<IAtsAbstractReview> reviewArts = AtsApiService.get().getReviewService().getReviews(teamWf);
         for (IAtsAbstractReview reviewArt : reviewArts) {
            spent += AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(reviewArt);
         }
         size += reviewArts.size();
      }
      if (size == 0) {
         return 0;
      }
      return spent / size;
   }

}
