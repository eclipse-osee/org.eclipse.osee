package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class HoursSpentUtil {

   /**
    * Return hours spent working states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(IAtsObject atsObject) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : AtsCore.getWorkItemService().getTeams((IAtsAction) atsObject)) {
            if (!AtsCore.getWorkItemService().getWorkData(team).isCancelled()) {
               hours += getHoursSpentTotal(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentTotal(workItem, AtsCore.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * Return hours spent working all states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(IAtsObject atsObject, IStateToken state) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         for (String stateName : workItem.getStateMgr().getVisitedStateNames()) {
            hours += getHoursSpentStateTotal(workItem, new SimpleTeamState(stateName, StateType.Working));
         }
      }
      return hours;
   }

   /**
    * Return hours spent working SMA state, state tasks and state reviews (not children SMAs)
    */
   public static double getHoursSpentStateTotal(IAtsObject atsObject) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : AtsCore.getWorkItemService().getTeams((IAtsAction) atsObject)) {
            if (!AtsCore.getWorkItemService().getWorkData(team).isCancelled()) {
               hours += getHoursSpentStateTotal(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentStateTotal(workItem, AtsCore.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * Return hours spent working SMA state, state tasks and state reviews (not children SMAs)
    */
   public static double getHoursSpentStateTotal(IAtsObject atsObject, IStateToken state) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours =
            getHoursSpentSMAState(workItem, state) + getHoursSpentFromStateTasks(workItem, state) + getHoursSpentStateReview(
               workItem, state);
      }
      return hours;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentStateReview(IAtsObject atsObject) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : AtsCore.getWorkItemService().getTeams((IAtsAction) atsObject)) {
            if (!AtsCore.getWorkItemService().getWorkData(team).isCancelled()) {
               hours += getHoursSpentStateReview(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentStateReview(workItem, AtsCore.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentStateReview(IAtsObject atsObject, IStateToken state) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsAbstractReview review : AtsCore.getWorkItemService().getReviews((IAtsTeamWorkflow) atsObject, state)) {
            hours += review.getStateMgr().getHoursSpent(state.getName());
         }
      }
      return hours;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(IAtsObject atsObject) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : AtsCore.getWorkItemService().getTeams((IAtsAction) atsObject)) {
            if (!AtsCore.getWorkItemService().getWorkData(team).isCancelled()) {
               hours += getHoursSpentSMAState(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentSMAState(workItem, AtsCore.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(IAtsObject atsObject, IStateToken state) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = workItem.getStateMgr().getHoursSpent(state.getName());
      }
      return hours;
   }

   /**
    * Return hours spent working ONLY on tasks related to stateName
    */
   public static double getHoursSpentFromStateTasks(IAtsObject atsObject) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsAction) {
         for (IAtsTeamWorkflow team : AtsCore.getWorkItemService().getTeams((IAtsAction) atsObject)) {
            if (!AtsCore.getWorkItemService().getWorkData(team).isCancelled()) {
               hours += getHoursSpentFromStateTasks(team);
            }
         }
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         hours = getHoursSpentFromStateTasks(workItem, AtsCore.getWorkItemService().getCurrentState(workItem));
      }
      return hours;
   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public static double getHoursSpentFromStateTasks(IAtsObject atsObject, IStateToken relatedToState) throws OseeCoreException {
      double hours = 0.0;
      if (atsObject instanceof IAtsTeamWorkflow) {
         for (IAtsTask taskArt : AtsCore.getWorkItemService().getTasks((IAtsTeamWorkflow) atsObject,
            relatedToState)) {
            hours += HoursSpentUtil.getHoursSpentTotal(taskArt);
         }
      }
      return hours;
   }

}
