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
package org.eclipse.osee.ats.core.task;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

public class TaskManager {

   public static TaskArtifact cast(Artifact artifact) {
      if (artifact instanceof TaskArtifact) {
         return (TaskArtifact) artifact;
      }
      return null;
   }

   public static Result transitionToCompleted(TaskArtifact taskArt, double estimatedHours, double additionalHours, SkynetTransaction transaction) {
      if (taskArt.isInState(TeamState.Completed)) {
         return Result.TrueResult;
      }
      // Assign current user if unassigned
      try {
         if (taskArt.getStateMgr().isUnAssigned()) {
            taskArt.getStateMgr().setAssignee(UserManager.getUser());
         }
         taskArt.getStateMgr().updateMetrics(additionalHours, 100, true);
         taskArt.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, 100);
         if (estimatedHours > 0.0) {
            taskArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, estimatedHours);
         }
         TransitionHelper helper =
            new TransitionHelper("Transition to Completed", Arrays.asList(taskArt), TaskStates.Completed.getPageName(),
               null, null);
         TransitionManager transitionMgr = new TransitionManager(helper, transaction);
         TransitionResults results = transitionMgr.handleAll();
         if (results.isEmpty()) {
            return Result.TrueResult;
         }
         return new Result("Transition Error %s", results.toString());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result(
            "Exception occurred while transitioning to completed (see error log) " + ex.getLocalizedMessage());
      }
   }

   public static Result transitionToInWork(TaskArtifact taskArt, IBasicUser toUser, int percentComplete, double additionalHours, SkynetTransaction transaction) throws OseeCoreException {
      if (taskArt.isInState(TaskStates.InWork)) {
         return Result.TrueResult;
      }
      TransitionHelper helper =
         new TransitionHelper("Transition to InWork", Arrays.asList(taskArt), TaskStates.InWork.getPageName(),
            Arrays.asList(toUser), null, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transitionMgr.handleAll();
      if (!results.isEmpty()) {
         return new Result("Transition Error %s", results.toString());
      }
      if (taskArt.getStateMgr().getPercentComplete() != percentComplete || additionalHours > 0) {
         taskArt.getStateMgr().updateMetrics(additionalHours, percentComplete, true);
         taskArt.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (transaction != null) {
         taskArt.saveSMA(transaction);
      }
      return Result.TrueResult;
   }

   /**
    * Tasks must transition in/out of completed when percent changes between 100 and <100. This method will handle these
    * cases.
    */
   public static Result statusPercentChanged(TaskArtifact taskArt, double additionalHours, int percentComplete, SkynetTransaction transaction) throws OseeCoreException {
      if (percentComplete == 100 && !taskArt.isCompleted()) {
         Result result = transitionToCompleted(taskArt, 0.0, additionalHours, transaction);
         return result;
      } else if (percentComplete != 100 && taskArt.isCompleted()) {
         Result result =
            transitionToInWork(taskArt, UserManager.getUser(), percentComplete, additionalHours, transaction);
         return result;
      }
      // Case where already completed and statusing, just add additional hours to InWork state
      else if (percentComplete == 100 && taskArt.isCompleted()) {
         if (additionalHours > 0) {
            taskArt.getStateMgr().updateMetrics(TaskStates.InWork, additionalHours, percentComplete, true);
            taskArt.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
         }
      } else {
         taskArt.getStateMgr().updateMetrics(additionalHours, percentComplete, true);
         taskArt.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (transaction != null) {
         taskArt.persist(transaction);
      }
      return Result.TrueResult;
   }

}
