/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;

/**
 * @author Donald G. Dunne
 */
public class HoursSpentUtil {

   /**
    * @return hours spent working states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getStateMgr().getStateType().isCancelled()) {
               hours += getHoursSpentTotal(team, atsApi);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentTotal(workItem, atsApi.getWorkItemService().getCurrentState(workItem), atsApi);
      }
      return hours;
   }

   /**
    * @return hours spent working all states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(IAtsObject atsObject, IStateToken state, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentTotalSMAState(hours, workItem) + getHoursSpentFromTasks(atsObject,
            atsApi) + getHoursSpentReview(atsObject, atsApi);
      }
      return hours;
   }

   /**
    * @return hours for all states (not children SMAs)
    */
   public static double getHoursSpentTotalSMAState(double hours, IAtsWorkItem workItem) {
      for (String stateName : workItem.getStateMgr().getVisitedStateNames()) {
         SimpleTeamState teamState = new SimpleTeamState(stateName, StateType.Working);
         hours += getHoursSpentSMAState(workItem, teamState);
      }
      return hours;
   }

   /**
    * @return hours spent working SMA state, state tasks and state reviews (not children SMAs)
    */
   public static double getHoursSpentStateTotal(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getStateMgr().getStateType().isCancelled()) {
               hours += getHoursSpentStateTotal(team, atsApi);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentStateTotal(workItem, atsApi.getWorkItemService().getCurrentState(workItem), atsApi);
      }
      return hours;
   }

   /**
    * @return hours spent working SMA state, state tasks and state reviews (not children SMAs)
    */
   public static double getHoursSpentStateTotal(IAtsObject atsObject, IStateToken state, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentSMAState(workItem, state) + getHoursSpentFromStateTasks(workItem, state,
            atsApi) + getHoursSpentStateReview(workItem, state, atsApi);
      }
      return hours;
   }

   /**
    * @return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentStateReview(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getStateMgr().getStateType().isCancelled()) {
               hours += getHoursSpentStateReview(team, atsApi);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentStateReview(workItem, atsApi.getWorkItemService().getCurrentState(workItem), atsApi);
      }
      return hours;
   }

   /**
    * @return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentStateReview(IAtsObject atsObject, IStateToken state, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsAbstractReview review : atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) atsObject,
            state)) {
            hours += review.getStateMgr().getHoursSpent(state.getName());
         }
      }
      return hours;
   }

   /**
    * @return hours spent for all reviews
    */
   public static double getHoursSpentReview(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsAbstractReview review : atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) atsObject)) {
            hours += HoursSpentUtil.getHoursSpentTotal(review, atsApi);
         }
      }
      return hours;
   }

   /**
    * @return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getStateMgr().getStateType().isCancelled()) {
               hours += getHoursSpentSMAState(team, atsApi);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentSMAState(workItem, atsApi.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * @return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(IAtsObject atsObject, IStateToken state) {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = workItem.getStateMgr().getHoursSpent(state.getName());
      }
      return hours;
   }

   /**
    * @return hours spent working ONLY on tasks related to stateName
    */
   public static double getHoursSpentFromStateTasks(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            if (!team.getStateMgr().getStateType().isCancelled()) {
               hours += getHoursSpentFromStateTasks(team, atsApi);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours =
            getHoursSpentFromStateTasks(workItem, atsApi.getWorkItemService().getCurrentState(workItem), atsApi);
      }
      return hours;
   }

   /**
    * @return Hours Spent for Tasks of "Related to State" stateName
    * @param relatedToState state name of parent workflow's state
    */
   public static double getHoursSpentFromStateTasks(IAtsObject atsObject, IStateToken relatedToState, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsTask taskArt : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) atsObject, relatedToState)) {
            hours += HoursSpentUtil.getHoursSpentTotal(taskArt, atsApi);
         }
      }
      return hours;
   }

   /**
    * @return Hours Spent for all Tasks
    */
   public static double getHoursSpentFromTasks(IAtsObject atsObject, AtsApi atsApi) {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsTask taskArt : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) atsObject)) {
            hours += HoursSpentUtil.getHoursSpentTotal(taskArt, atsApi);
         }
      }
      return hours;
   }

}
