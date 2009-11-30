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
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TaskManager {
   private final SMAManager smaMgr;

   public TaskManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   public String getStatus(String stateName) throws OseeCoreException {
      int completed = 0, cancelled = 0, inWork = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(stateName)) {
         if (taskArt.isCompleted()) {
            completed++;
         } else if (taskArt.isCancelled()) {
            cancelled++;
         } else {
            inWork++;
         }
      }
      return String.format("Total: %d - InWork: %d - Completed: %d - Cancelled: %d",
            getTaskArtifacts(stateName).size(), inWork, completed, cancelled);
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException {
      return smaMgr.getSma().getRelatedArtifactsUnSorted(AtsRelationTypes.SmaToTask_Task, TaskArtifact.class);
   }

   public Collection<TaskArtifact> getTaskArtifactsSorted() throws OseeCoreException {
      return smaMgr.getSma().getRelatedArtifacts(AtsRelationTypes.SmaToTask_Task, TaskArtifact.class);
   }

   public Collection<TaskArtifact> getTaskArtifactsFromCurrentState() throws OseeCoreException {
      return getTaskArtifacts(smaMgr.getStateMgr().getCurrentStateName());
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      List<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         if (taskArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "").equals(
               stateName)) {
            arts.add(taskArt);
         }
      }
      return arts;
   }

   public boolean hasTaskArtifacts() {
      try {
         return smaMgr.getSma().getRelatedArtifactsCount(AtsRelationTypes.SmaToTask_Task) > 0;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   public TaskArtifact createNewTask(String title) throws OseeCoreException {
      return createNewTask(Arrays.asList(UserManager.getUser()), title);
   }

   public TaskArtifact createNewTask(User assignee, String title) throws OseeCoreException {
      return createNewTask(Arrays.asList(assignee), title);
   }

   public TaskArtifact createNewTask(Collection<User> assignees, String title) throws OseeCoreException {
      TaskArtifact taskArt = null;
      taskArt =
            (TaskArtifact) ArtifactTypeManager.addArtifact(TaskArtifact.ARTIFACT_NAME, AtsUtil.getAtsBranch(), title);
      taskArt.getSmaMgr().getLog().addLog(LogType.Originated, "", "");

      // Initialize state machine
      taskArt.getSmaMgr().getStateMgr().initializeStateMachine(TaskStates.InWork.name(), assignees);
      taskArt.getSmaMgr().getLog().addLog(LogType.StateEntered, "InWork", "");

      // Set parent state task is related to
      taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            smaMgr.getStateMgr().getCurrentStateName());

      smaMgr.getSma().addRelation(AtsRelationTypes.SmaToTask_Task, taskArt);

      return taskArt;
   }

   public Result areTasksComplete() {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts()) {
            if (taskArt.isInWork()) {
               return new Result(false, "Task " + taskArt.getGuid() + " Not Complete");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result(false, "Exception " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public Result areTasksComplete(String stateName) {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts(stateName)) {
            if (taskArt.isInWork()) {
               return new Result(false, "Task " + taskArt.getGuid() + " Not Complete");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result(false, "Exception " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public int getNumTasksInWork() {
      int num = 0;
      try {
         for (TaskArtifact taskArt : getTaskArtifacts()) {
            if (taskArt.isInWork()) {
               num++;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return num;
   }

   /**
    * Return Estimated Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Estimated Hours
    */
   public double getEstimatedHours(String relatedToStateName) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName)) {
         hours += taskArt.getEstimatedHoursTotal();
      }
      return hours;
   }

   /**
    * Return Estimated Hours for all tasks
    * 
    * @return hours
    * @throws Exception
    */
   public double getEstimatedHours() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         hours += taskArt.getEstimatedHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Remain Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Remain Hours
    */
   public double getRemainHours(String relatedToStateName) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName)) {
         hours += taskArt.getRemainHoursFromArtifact();
      }
      return hours;
   }

   /**
    * Return Remain Hours for all tasks
    * 
    * @return hours
    * @throws Exception
    */
   public double getRemainHours() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         hours += taskArt.getRemainHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public double getHoursSpent(String relatedToStateName) throws OseeCoreException {
      double spent = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName)) {
         spent += taskArt.getHoursSpentSMATotal();
      }
      return spent;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Percent Complete.
    */
   public int getPercentComplete(String relatedToStateName) throws OseeCoreException {
      int spent = 0;
      Collection<TaskArtifact> taskArts = getTaskArtifacts(relatedToStateName);
      for (TaskArtifact taskArt : taskArts) {
         spent += taskArt.getPercentCompleteSMATotal();
      }
      if (spent == 0) {
         return 0;
      }
      return spent / taskArts.size();
   }

   public static Collection<TaskArtifact> createTasks(TeamWorkFlowArtifact teamArt, List<String> titles, List<User> assignees, SkynetTransaction transaction) throws OseeCoreException {
      List<TaskArtifact> tasks = new ArrayList<TaskArtifact>();
      for (String title : titles) {
         TaskArtifact taskArt = teamArt.getSmaMgr().getTaskMgr().createNewTask(title);
         if (assignees != null && assignees.size() > 0) {
            Set<User> users = new HashSet<User>();
            for (User art : assignees) {
               users.add(art);
            }
            taskArt.getSmaMgr().getStateMgr().setAssignees(users);
         }
         tasks.add(taskArt);
         taskArt.persist(transaction);
      }
      return tasks;
   }

}
