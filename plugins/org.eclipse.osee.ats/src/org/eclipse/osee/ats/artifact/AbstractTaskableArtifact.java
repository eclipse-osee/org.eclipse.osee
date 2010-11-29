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
package org.eclipse.osee.ats.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractTaskableArtifact extends AbstractWorkflowArtifact {

   public AbstractTaskableArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerAtsWorldRelation(AtsRelationTypes.SmaToTask_Task);
   }

   @Override
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      super.getSmaArtifactsOneLevel(smaArtifact, artifacts);
      artifacts.addAll(getTaskArtifacts());
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         taskArt.atsDelete(deleteArts, allRelated);
      }
   }

   @Override
   public void transitioned(WorkPageDefinition fromPage, WorkPageDefinition toPage, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(fromPage, toPage, toAssignees, persist, transaction);
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         taskArt.parentWorkFlowTransitioned(fromPage, toPage, toAssignees, persist, transaction);
      }
   }

   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException {
      return AtsCacheManager.getTaskArtifacts(this);
   }

   public Collection<TaskArtifact> getTaskArtifactsSorted() throws OseeCoreException {
      return AtsCacheManager.getTaskArtifacts(this);
   }

   public Collection<TaskArtifact> getTaskArtifactsFromCurrentState() throws OseeCoreException {
      return getTaskArtifacts(getStateMgr().getCurrentState());
   }

   public Collection<TaskArtifact> getTaskArtifacts(IWorkPage state) throws OseeCoreException {
      List<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         if (taskArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "").equals(state.getPageName())) {
            arts.add(taskArt);
         }
      }
      return arts;
   }

   public boolean hasTaskArtifacts() {
      return getRelatedArtifactsCount(AtsRelationTypes.SmaToTask_Task) > 0;
   }

   public TaskArtifact createNewTask(String title, Date createdDate, User createdBy) throws OseeCoreException {
      return createNewTask(Arrays.asList(UserManager.getUser()), title, createdDate, createdBy);
   }

   public TaskArtifact createNewTask(Collection<User> assignees, String title, Date createdDate, User createdBy) throws OseeCoreException {
      TaskArtifact taskArt = null;
      taskArt = (TaskArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Task, AtsUtil.getAtsBranch(), title);

      addRelation(AtsRelationTypes.SmaToTask_Task, taskArt);
      taskArt.initializeNewStateMachine(assignees, new Date(), UserManager.getUser());

      // Set parent state task is related to
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, getStateMgr().getCurrentStateName());

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

   public Result areTasksComplete(IWorkPage state) {
      try {
         for (TaskArtifact taskArt : getTaskArtifacts(state)) {
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
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Estimated Hours
    */
   @Override
   public double getEstimatedHoursFromTasks(IWorkPage relatedToState) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToState)) {
         hours += taskArt.getEstimatedHoursTotal();
      }
      return hours;
   }

   /**
    * Return Estimated Hours for all tasks
    */
   @Override
   public double getEstimatedHoursFromTasks() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         hours += taskArt.getEstimatedHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Remain Task Hours of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Remain Hours
    */
   public double getRemainHoursFromTasks(IWorkPage relatedToState) throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToState)) {
         hours += taskArt.getRemainHoursFromArtifact();
      }
      return hours;
   }

   /**
    * Return Remain Hours for all tasks
    */
   public double getRemainHoursFromTasks() throws OseeCoreException {
      double hours = 0;
      for (TaskArtifact taskArt : getTaskArtifacts()) {
         hours += taskArt.getRemainHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public double getHoursSpentFromTasks(IWorkPage relatedToState) throws OseeCoreException {
      double spent = 0;
      for (TaskArtifact taskArt : getTaskArtifacts(relatedToState)) {
         spent += taskArt.getHoursSpentSMATotal();
      }
      return spent;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Percent Complete.
    */
   public int getPercentCompleteFromTasks(IWorkPage relatedToState) throws OseeCoreException {
      int spent = 0;
      Collection<TaskArtifact> taskArts = getTaskArtifacts(relatedToState);
      for (TaskArtifact taskArt : taskArts) {
         spent += taskArt.getPercentCompleteSMATotal();
      }
      if (spent == 0) {
         return 0;
      }
      return spent / taskArts.size();
   }

   public Collection<TaskArtifact> createTasks(List<String> titles, List<User> assignees, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      List<TaskArtifact> tasks = new ArrayList<TaskArtifact>();
      for (String title : titles) {
         TaskArtifact taskArt = createNewTask(title, createdDate, createdBy);
         if (assignees != null && !assignees.isEmpty()) {
            Set<User> users = new HashSet<User>(); // NOPMD by b0727536 on 9/29/10 8:51 AM
            for (User art : assignees) {
               users.add(art);
            }
            taskArt.getStateMgr().setAssignees(users);
         }
         tasks.add(taskArt);
         taskArt.persist(transaction);
      }
      return tasks;
   }

}