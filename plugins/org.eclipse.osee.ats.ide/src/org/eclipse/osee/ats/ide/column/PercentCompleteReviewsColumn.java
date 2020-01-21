/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteReviewsColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteReviewsColumn instance = new PercentCompleteReviewsColumn();

   public static PercentCompleteReviewsColumn getInstance() {
      return instance;
   }

   private PercentCompleteReviewsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".reviewPercentComplete", "Review Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the reviews.\n\nCalculation: total percent of all reviews / number of reviews");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteReviewsColumn copy() {
      PercentCompleteReviewsColumn newXCol = new PercentCompleteReviewsColumn();
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
                  getPercentCompleteReview(AtsClientService.get().getQueryServiceClient().getArtifact(element)));
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
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteReview(AtsClientService.get().getQueryServiceClient().getArtifact(team));
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / AtsClientService.get().getWorkItemService().getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         int spent = 0;
         Collection<IAtsAbstractReview> reviews =
            AtsClientService.get().getReviewService().getReviews((TeamWorkFlowArtifact) artifact);
         for (IAtsAbstractReview review : reviews) {
            spent += PercentCompleteTotalUtil.getPercentCompleteTotal(review, AtsClientService.get().getServices());
         }
         if (spent == 0) {
            return 0;
         }
         return spent / reviews.size();
      }
      return 0;
   }

}
