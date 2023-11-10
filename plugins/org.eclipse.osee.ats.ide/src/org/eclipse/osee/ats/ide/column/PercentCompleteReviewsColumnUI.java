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

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
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
public class PercentCompleteReviewsColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteReviewsColumnUI instance = new PercentCompleteReviewsColumnUI();

   public static PercentCompleteReviewsColumnUI getInstance() {
      return instance;
   }

   private PercentCompleteReviewsColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".reviewPercentComplete", "Review Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the reviews.\n\nCalculation: total percent of all reviews / number of reviews");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteReviewsColumnUI copy() {
      PercentCompleteReviewsColumnUI newXCol = new PercentCompleteReviewsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) element;
            if (workItem.isTeamWorkflow()) {
               return String.valueOf(
                  getPercentCompleteReview(AtsApiService.get().getQueryServiceIde().getArtifact(element)));
            }
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on reviews. Total Percent / # Reviews
    */
   public static int getPercentCompleteReview(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double percent = 0;
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteReview(AtsApiService.get().getQueryServiceIde().getArtifact(team));
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / AtsApiService.get().getWorkItemService().getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         int spent = 0;
         Collection<IAtsAbstractReview> reviews =
            AtsApiService.get().getReviewService().getReviews((TeamWorkFlowArtifact) artifact);
         for (IAtsAbstractReview review : reviews) {
            spent += AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(review);
         }
         if (spent == 0) {
            return 0;
         }
         return spent / reviews.size();
      }
      return 0;
   }

}
