/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.branch;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.PeerReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.stateitem.AtsStateItemCoreManager;
import org.eclipse.osee.ats.core.client.workflow.stateitem.IAtsStateItemCore;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.CatchAndReleaseJob;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchUtil {

   /**
    * @return true if one or more reviews were created
    */
   public static boolean createNecessaryBranchEventReviews(StateEventType stateEventType, TeamWorkFlowArtifact teamArt, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes)  {
      Conditions.checkNotNull(teamArt, "Team Workflow");
      boolean created = false;
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType [%s]", stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (IAtsDecisionReviewDefinition decRevDef : teamArt.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(stateEventType)) {
            DecisionReviewArtifact decArt = DecisionReviewDefinitionManager.createNewDecisionReview(decRevDef, changes,
               teamArt, createdDate, createdBy);
            if (decArt != null) {
               created = true;
               changes.add(decArt);
            }
         }
      }
      for (IAtsPeerReviewDefinition peerRevDef : teamArt.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(stateEventType)) {
            PeerToPeerReviewArtifact peerArt = PeerReviewDefinitionManager.createNewPeerToPeerReview(peerRevDef,
               changes, teamArt, createdDate, createdBy);
            if (peerArt != null) {
               created = true;
               changes.add(peerArt);
            }
         }
      }
      return created;
   }

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    *
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   public static Result createWorkingBranch_Validate(TeamWorkFlowArtifact teamArt) {
      try {
         if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
            return new Result(
               "Committed branch already exists. Can not create another working branch once changes have been committed.");
         }
         BranchId parentBranch = AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(teamArt);
         if (parentBranch == null || parentBranch.isInvalid()) {
            return new Result(
               "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.");
         }
         Result result = AtsClientService.get().getBranchService().isCreateBranchAllowed(teamArt);
         if (result.isFalse()) {
            return result;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   /**
    * Create a working branch associated with this Team Workflow. Call createWorkingBranch_Validate first to validate
    * that branch can be created.
    */
   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt)  {
      return createWorkingBranch_Create(teamArt, false);
   }

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, boolean pend)  {
      final BranchId parentBranch = AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(teamArt);
      return createWorkingBranch_Create(teamArt, parentBranch, pend);
   }

   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, final BranchId parentBranch)  {
      return createWorkingBranch_Create(teamArt, parentBranch, false);
   }

   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, final BranchId parentBranch, boolean pend)  {
      Conditions.checkNotNull(teamArt, "Parent Team Workflow");
      Conditions.checkNotNull(parentBranch, "Parent Branch");
      TransactionToken parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(teamArt, parentTransactionId, pend);
   }

   public static Job createWorkingBranch(final TeamWorkFlowArtifact teamArt, final TransactionToken parentTransactionId, boolean pend)  {
      final String branchName = AtsClientService.get().getBranchService().getBranchName(teamArt);
      Conditions.checkNotNull(teamArt, "Parent Team Workflow");
      Conditions.checkNotNull(parentTransactionId, "Parent Branch");

      IExceptionableRunnable runnable = new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor)  {
            teamArt.setWorkingBranchCreationInProgress(true);
            BranchManager.createWorkingBranch(parentTransactionId, branchName, teamArt);
            teamArt.setWorkingBranchCreationInProgress(false);
            performPostBranchCreationTasks(teamArt);
            return Status.OK_STATUS;
         }

      };

      //            Jobs.runInJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID);
      Job job =
         Jobs.startJob(new CatchAndReleaseJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID), true);
      if (pend) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            throw new OseeWrappedException(ex);
         }
      }
      return job;
   }

   private static void performPostBranchCreationTasks(final TeamWorkFlowArtifact teamArt)  {
      // Create reviews as necessary
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create Reviews upon Transition");
      boolean created = createNecessaryBranchEventReviews(StateEventType.CreateBranch, teamArt, new Date(),
         AtsCoreUsers.SYSTEM_USER, changes);
      if (created) {
         changes.execute();
      }

      // Notify extensions of branch creation
      for (IAtsStateItemCore item : AtsStateItemCoreManager.getStateItems()) {
         item.workingBranchCreated(teamArt);
      }
   }

   public static Result deleteWorkingBranch(TeamWorkFlowArtifact teamArt, boolean pend)  {
      BranchId branch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
      if (branch != null) {
         IStatus status = null;
         if (pend) {
            status = BranchManager.deleteBranchAndPend(branch);
         } else {
            Job job = BranchManager.deleteBranch(branch);
            job.schedule();
            try {
               job.join();
            } catch (InterruptedException ex) {
               throw new OseeWrappedException(ex);
            }
            status = job.getResult();
         }
         if (status.isOK()) {
            return Result.TrueResult;
         }
         return new Result(status.getMessage());
      }
      return Result.TrueResult;
   }

}
