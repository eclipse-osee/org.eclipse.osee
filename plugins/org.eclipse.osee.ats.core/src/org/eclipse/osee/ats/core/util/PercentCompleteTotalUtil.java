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
package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTotalUtil {

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/canceled)
    */
   public static int getPercentCompleteTotal(IAtsObject atsObject, IAtsServices services)  {
      int percent = 0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         if (workItem.getStateMgr().getStateType().isCompletedOrCancelled()) {
            percent = 100;
         } else {
            if (services.getWorkDefinitionService().isStateWeightingEnabled(workItem.getWorkDefinition())) {
               // Calculate total percent using configured weighting
               for (IAtsStateDefinition stateDef : workItem.getWorkDefinition().getStates()) {
                  if (!stateDef.getStateType().isCompletedState() && !stateDef.getStateType().isCancelledState()) {
                     percent = addStatePercentWithWeight(services, percent, workItem, stateDef);
                  }
               }
            } else {
               percent = getPercentCompleteSMASinglePercent(workItem, services);
               if (percent == 0) {
                  if (isAnyStateHavePercentEntered(workItem)) {
                     int numStates = 0;
                     for (IAtsStateDefinition state : workItem.getWorkDefinition().getStates()) {
                        if (!state.getStateType().isCompletedState() && !state.getStateType().isCancelledState()) {
                           percent += getPercentCompleteSMAStateTotal(workItem, state, services);
                           numStates++;
                        }
                     }
                     if (numStates > 0) {
                        percent = percent / numStates;
                     }
                  }
               }
            }
         }
      }
      return percent;
   }

   private static int addStatePercentWithWeight(IAtsServices services, int percent, IAtsWorkItem workItem, IAtsStateDefinition stateDef) {
      double stateWeightInt = stateDef.getStateWeight();
      double weight = stateWeightInt / 100;
      int percentCompleteForState = getPercentCompleteSMAStateTotal(workItem, stateDef, services);
      percent += weight * percentCompleteForState;
      return percent;
   }

   private static boolean isAnyStateHavePercentEntered(IAtsWorkItem workItem)  {
      for (String stateName : workItem.getStateMgr().getVisitedStateNames()) {
         if (workItem.getStateMgr().getPercentComplete(stateName) != 0) {
            return true;
         }
      }
      return false;
   }

   /**
    * Add percent represented by percent attribute, percent for reviews and tasks divided by number of objects.
    */
   private static int getPercentCompleteSMASinglePercent(IAtsObject atsObject, IAtsServices services)  {
      int percent = 0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         int numObjects = 1;
         percent =
            services.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, 0);
         if (workItem instanceof IAtsTeamWorkflow) {
            for (IAtsAbstractReview revArt : services.getWorkItemService().getReviews((IAtsTeamWorkflow) workItem)) {
               percent += getPercentCompleteTotal(revArt, services);
               numObjects++;
            }
         }
         if (workItem instanceof IAtsTeamWorkflow) {
            for (IAtsTask taskArt : services.getTaskService().getTasks((IAtsTeamWorkflow) workItem)) {
               percent += getPercentCompleteTotal(taskArt, services);
               numObjects++;
            }
         }
         if (percent > 0) {
            if (numObjects > 0) {
               percent = percent / numObjects;
            }
         }
      }
      return percent;
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    *
    * @param services JavaTip
    */
   public static int getPercentCompleteSMAStateTotal(IAtsObject atsObject, IStateToken state, IAtsServices services)  {
      return getStateMetricsData(atsObject, state, services).getResultingPercent();
   }

   private static StateMetricsData getStateMetricsData(IAtsObject atsObject, IStateToken teamState, IAtsServices services)  {
      if (!(atsObject instanceof IAtsWorkItem)) {
         return null;
      }
      IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
      // Add percent and bump objects 1 for state percent
      int percent = getPercentCompleteSMAState(workItem, teamState);
      int numObjects = 1; // the state itself is one object

      // Add percent for each task and bump objects for each task
      if (workItem instanceof IAtsTeamWorkflow) {
         Collection<IAtsTask> tasks = services.getTaskService().getTasks((IAtsTeamWorkflow) workItem, teamState);
         for (IAtsTask taskArt : tasks) {
            percent += getPercentCompleteTotal(taskArt, services);
         }
         numObjects += tasks.size();
      }

      // Add percent for each review and bump objects for each review
      if (workItem instanceof IAtsTeamWorkflow) {
         Collection<IAtsAbstractReview> reviews =
            services.getWorkItemService().getReviews((IAtsTeamWorkflow) workItem, teamState);
         for (IAtsAbstractReview reviewArt : reviews) {
            percent += getPercentCompleteTotal(reviewArt, services);
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
   public static int getPercentCompleteSMAState(IAtsObject atsObject, IAtsServices services)  {
      int percent = 0;
      if (atsObject instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) atsObject;
         if (((IAtsAction) atsObject).getTeamWorkflows().size() == 1) {
            return getPercentCompleteSMAState(services.getWorkItemService().getFirstTeam(action), services);
         } else {
            int items = 0;
            for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
               if (!team.getStateMgr().getStateType().isCancelled()) {
                  percent += getPercentCompleteSMAState(team, services);
                  items++;
               }
            }
            if (items > 0) {
               Double rollPercent = new Double(percent) / items;
               percent = rollPercent.intValue();
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         return getPercentCompleteSMAState(atsObject,
            services.getWorkItemService().getCurrentState((IAtsWorkItem) atsObject));
      }
      return percent;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    */
   public static int getPercentCompleteSMAState(IAtsObject atsObject, IStateToken state)  {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         return workItem.getStateMgr().getPercentComplete(state.getName());
      }
      return 0;
   }
}
