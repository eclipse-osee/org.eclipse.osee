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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TaskManager {
   private final SMAManager smaMgr;

   public TaskManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws SQLException {
      return smaMgr.getSma().getArtifacts(CoreRelationEnumeration.SmaToTask_Task, TaskArtifact.class);
   }

   public Collection<TaskArtifact> getTaskArtifactsFromCurrentState() throws SQLException, MultipleAttributesExist {
      return getTaskArtifacts(smaMgr.getStateMgr().getCurrentStateName());
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws SQLException, MultipleAttributesExist {
      List<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      for (TaskArtifact taskArt : smaMgr.getSma().getArtifacts(CoreRelationEnumeration.SmaToTask_Task,
            TaskArtifact.class)) {
         if (taskArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "").equals(
               stateName)) arts.add(taskArt);
      }
      return arts;
   }

   public boolean hasTaskArtifacts() {
      try {
         return smaMgr.getSma().getRelatedArtifactsCount(CoreRelationEnumeration.SmaToTask_Task) > 0;
      } catch (OseeDataStoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
   }

   public TaskArtifact createNewTask(String title, boolean persist) throws Exception {
      return createNewTask(Arrays.asList(new User[] {SkynetAuthentication.getUser()}), title, persist);
   }

   public TaskArtifact createNewTask(User assignee, String title, boolean persist) throws Exception {
      return createNewTask(Arrays.asList(new User[] {assignee}), title, persist);
   }

   public TaskArtifact createNewTask(Collection<User> assignees, String title, boolean persist) throws Exception {
      TaskArtifact taskArt = null;
      taskArt =
            (TaskArtifact) ArtifactTypeManager.addArtifact(TaskArtifact.ARTIFACT_NAME,
                  BranchPersistenceManager.getAtsBranch(), title);
      taskArt.getLog().addLog(LogType.Originated, "", "");

      // Initialize state machine
      taskArt.getSmaMgr().getStateMgr().initializeStateMachine(TaskArtifact.INWORK_STATE, assignees);
      taskArt.getLog().addLog(LogType.StateEntered, "InWork", "");

      // Set parent state task is related to
      taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            smaMgr.getStateMgr().getCurrentStateName());

      smaMgr.getSma().addRelation(CoreRelationEnumeration.SmaToTask_Task, taskArt);
      if (persist) {
         taskArt.persistAttributesAndRelations();
      }

      return taskArt;
   }

   public Result areTasksComplete() {
      return areTasksComplete(true);
   }

   public Result areTasksComplete(boolean popup) {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts()) {
            if (!taskArt.isCompleted() && taskArt.isCancelled()) return new Result("Not Complete");
         }
         return Result.TrueResult;
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return Result.TrueResult;
   }

   /**
    * Return Estimated Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Estimated Hours
    */
   public double getEstimatedHours(String relatedToStateName) throws Exception {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         hours += taskArt.getEstimatedHoursTotal();
      return hours;
   }

   /**
    * Return Estimated Hours for all tasks
    * 
    * @return
    * @throws Exception
    */
   public double getEstimatedHours() throws Exception {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts())
         hours += taskArt.getEstimatedHoursFromArtifact();
      return hours;

   }

   /**
    * Return Remain Task Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Remain Hours
    */
   public double getRemainHours(String relatedToStateName) throws Exception {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         hours += taskArt.getRemainHoursFromArtifact();
      return hours;
   }

   /**
    * Return Remain Hours for all tasks
    * 
    * @return
    * @throws Exception
    */
   public double getRemainHours() throws Exception {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts())
         hours += taskArt.getRemainHoursFromArtifact();
      return hours;

   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public double getHoursSpent(String relatedToStateName) throws Exception {
      double spent = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToStateName))
         spent += taskArt.getHoursSpentSMATotal();
      return spent;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Percent Complete.
    */
   public int getPercentComplete(String relatedToStateName) throws Exception {
      int spent = 0;
      Collection<TaskArtifact> taskArts = getTaskArtifacts(relatedToStateName);
      for (TaskArtifact taskArt : taskArts)
         spent += taskArt.getPercentCompleteSMATotal();
      if (spent == 0) return 0;
      return spent / taskArts.size();
   }

}
