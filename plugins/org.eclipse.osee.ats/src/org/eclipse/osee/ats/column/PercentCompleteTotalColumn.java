/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.ReviewManager;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.swt.SWT;

public class PercentCompleteTotalColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteTotalColumn instance = new PercentCompleteTotalColumn();

   public static PercentCompleteTotalColumn getInstance() {
      return instance;
   }

   private PercentCompleteTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".totalPercentComplete", "Total Percent Complete", 40, SWT.CENTER,
         false, SortDataType.Percent, false, "Percent Complete for the reviews related to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteTotalColumn copy() {
      PercentCompleteTotalColumn newXCol = new PercentCompleteTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(getPercentCompleteTotal((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/canceled)
    */
   public static int getPercentCompleteTotal(Artifact artifact) throws OseeCoreException {
      if (!(artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact))) {
         return 0;
      }
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
      if (awa.isCompletedOrCancelled()) {
         return 100;
      }
      if (awa.getWorkDefinition().isStateWeightingEnabled()) {
         // Calculate total percent using configured weighting
         int percent = 0;
         for (StateDefinition stateDef : awa.getWorkDefinition().getStates()) {
            if (!stateDef.isCompletedPage() && !stateDef.isCancelledPage()) {
               double stateWeightInt = stateDef.getStateWeight();
               double weight = stateWeightInt / 100;
               int percentCompleteForState = getPercentCompleteSMAStateTotal(awa, stateDef);
               percent += weight * percentCompleteForState;
            }
         }
         return percent;
      } else {
         int percent = getPercentCompleteSMASinglePercent(awa);
         if (percent > 0) {
            return percent;
         }
         if (awa.isCompletedOrCancelled()) {
            return 100;
         }
         if (awa.getStateMgr().isAnyStateHavePercentEntered()) {
            int numStates = 0;
            for (StateDefinition state : awa.getWorkDefinition().getStates()) {
               if (!state.isCompletedPage() && !state.isCancelledPage()) {
                  percent += getPercentCompleteSMAStateTotal(awa, state);
                  numStates++;
               }
            }
            if (numStates == 0) {
               return 0;
            }
            return percent / numStates;
         }

      }
      return 0;
   }

   /**
    * Add percent represented by percent attribute, percent for reviews and tasks divided by number of objects.
    */
   private static int getPercentCompleteSMASinglePercent(Artifact artifact) throws OseeCoreException {
      if (!(artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact))) {
         return 0;
      }
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
      int numObjects = 1;
      int percent = awa.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
      if (awa.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         for (AbstractReviewArtifact revArt : ReviewManager.getReviews((TeamWorkFlowArtifact) awa)) {
            percent += getPercentCompleteTotal(revArt);
            numObjects++;
         }
      }
      if (awa instanceof AbstractTaskableArtifact) {
         for (TaskArtifact taskArt : ((AbstractTaskableArtifact) awa).getTaskArtifacts()) {
            percent += getPercentCompleteTotal(taskArt);
            numObjects++;
         }
      }
      if (percent > 0) {
         if (numObjects == 0) {
            return 0;
         }
         return percent / numObjects;
      }
      return percent;
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    */
   public static int getPercentCompleteSMAStateTotal(Artifact artifact, IWorkPage state) throws OseeCoreException {
      return getStateMetricsData(artifact, state).getResultingPercent();
   }

   private static StateMetricsData getStateMetricsData(Artifact artifact, IWorkPage teamState) throws OseeCoreException {
      if (!(artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact))) {
         return null;
      }
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
      // Add percent and bump objects 1 for state percent
      int percent = PercentCompleteSMAStateColumn.getPercentCompleteSMAState(awa, teamState);
      int numObjects = 1; // the state itself is one object

      // Add percent for each task and bump objects for each task
      if (awa instanceof AbstractTaskableArtifact) {
         Collection<TaskArtifact> tasks = ((AbstractTaskableArtifact) awa).getTaskArtifacts(teamState);
         for (TaskArtifact taskArt : tasks) {
            percent += getPercentCompleteTotal(taskArt);
         }
         numObjects += tasks.size();
      }

      // Add percent for each review and bump objects for each review
      if (awa.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         Collection<AbstractReviewArtifact> reviews = ReviewManager.getReviews((TeamWorkFlowArtifact) awa, teamState);
         for (AbstractReviewArtifact reviewArt : reviews) {
            percent += getPercentCompleteTotal(reviewArt);
         }
         numObjects += reviews.size();
      }

      return new StateMetricsData(percent, numObjects);
   }
   private static class StateMetricsData {
      public int numObjects = 0;
      public int percent = 0;

      public StateMetricsData(int percent, int numObjects) {
         this.numObjects = numObjects;
         this.percent = percent;
      }

      public int getResultingPercent() {
         return percent / numObjects;
      }

      @Override
      public String toString() {
         return "Percent: " + getResultingPercent() + "  NumObjs: " + numObjects + "  Total Percent: " + percent;
      }
   }

}
