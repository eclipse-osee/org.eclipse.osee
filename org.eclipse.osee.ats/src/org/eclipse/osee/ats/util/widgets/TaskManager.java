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
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class TaskManager {

   private static SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private final SMAManager smaMgr;

   public TaskManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws SQLException {
      return smaMgr.getSma().getArtifacts(RelationSide.SmaToTask_Task, TaskArtifact.class);
   }

   public Collection<TaskArtifact> getTaskArtifactsFromCurrentState() throws SQLException {
      return getTaskArtifacts(smaMgr.getSma().getCurrentStateName());
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws SQLException {
      List<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      for (TaskArtifact taskArt : smaMgr.getSma().getArtifacts(RelationSide.SmaToTask_Task, TaskArtifact.class)) {
         if (taskArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName()).equals(stateName)) arts.add(taskArt);
      }
      return arts;
   }

   public boolean hasTaskArtifacts() {
      return smaMgr.getSma().hasArtifacts(RelationSide.SmaToTask_Task);
   }

   public TaskArtifact createNewTask(String title, boolean persist) throws SQLException {
      return createNewTask(Arrays.asList(new User[] {skynetAuth.getAuthenticatedUser()}), title, persist);
   }

   public TaskArtifact createNewTask(User assignee, String title, boolean persist) throws SQLException {
      return createNewTask(Arrays.asList(new User[] {assignee}), title, persist);
   }

   public TaskArtifact createNewTask(Collection<User> assignees, String title, boolean persist) throws SQLException {
      TaskArtifact taskArt = null;
      taskArt =
            (TaskArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  TaskArtifact.ARTIFACT_NAME, BranchPersistenceManager.getInstance().getAtsBranch()).makeNewArtifact();
      taskArt.getLog().addLog(LogType.Originated, "", "");
      taskArt.setDescriptiveName(title);

      // Set current state and POCs
      taskArt.getCurrentStateDam().setState(new SMAState("InWork", assignees));
      taskArt.getLog().addLog(LogType.StateEntered, "InWork", "");

      // Set parent state task is related to
      taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            smaMgr.getSma().getCurrentStateName());

      if (persist) taskArt.persistAttributes();
      smaMgr.getSma().relate(RelationSide.SmaToTask_Task, taskArt, persist);

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

}
