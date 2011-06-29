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
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.WorkflowManagerCore;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTotalUtil {

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
      int percent = getPercentCompleteSMAState(awa, teamState);
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

   /**
    * Return Percent Complete working ONLY the current state (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         if (ActionManagerCore.getTeams(artifact).size() == 1) {
            return getPercentCompleteSMAState(ActionManagerCore.getFirstTeam(artifact));
         } else {
            double percent = 0;
            int items = 0;
            for (TeamWorkFlowArtifact team : ActionManagerCore.getTeams(artifact)) {
               if (!team.isCancelled()) {
                  percent += getPercentCompleteSMAState(team);
                  items++;
               }
            }
            if (items > 0) {
               Double rollPercent = percent / items;
               return rollPercent.intValue();
            }
         }
         return 0;
      }
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getPercentCompleteSMAState(artifact, WorkflowManagerCore.getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    */
   public static int getPercentCompleteSMAState(Artifact artifact, IWorkPage state) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return WorkflowManagerCore.getStateManager(artifact).getPercentComplete(state);
      }
      return 0;
   }

}
