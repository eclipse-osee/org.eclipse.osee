/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task.createtasks;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionManagerCore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.CreateTeamOption;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.version.VersionArtifact;
import org.eclipse.osee.ats.core.client.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Shawn F. Cook
 */
public class CreateTasksOperation extends AbstractOperation {
   //QUESTION: Do we need either of theses commit* objects?
   //   private final IAtsProgram commitProgram;
   //   private final VersionArtifact commitVersion;
   private final VersionArtifact destVersion;
   private final ActionableItemArtifact actionableItemArt;
   private final ChangeData changeData;
   private final TeamWorkFlowArtifact reqTeamWf;
   private final boolean reportOnly;
   private SkynetTransaction transaction;
   private final ITaskTitleProvider taskTitleProvider;

   public CreateTasksOperation(VersionArtifact destinationVersion, ActionableItemArtifact actionableItemArt, ChangeData changeData, TeamWorkFlowArtifact reqTeamWf, boolean reportOnly, SkynetTransaction transaction, OperationLogger logger, ITaskTitleProvider taskTitleProvider) {
      super("Create Tasks Operation for [" + reqTeamWf.getName() + "]", Activator.PLUGIN_ID, logger);
      this.destVersion = destinationVersion;
      this.actionableItemArt = actionableItemArt;
      this.changeData = changeData;
      this.reqTeamWf = reqTeamWf;
      this.reportOnly = reportOnly;
      this.transaction = transaction;
      this.taskTitleProvider = taskTitleProvider;
   }

   private Map<TaskEnum, ITaskOperation> createReportOnlyTaskOperationMap() {
      Map<TaskEnum, ITaskOperation> toReturn = new HashMap<TaskEnum, ITaskOperation>();

      toReturn.put(TaskEnum.CREATE, new TaskOpCreate(taskTitleProvider));
      toReturn.put(TaskEnum.MODIFY, new TaskOpModify());

      return toReturn;
   }

   private Map<TaskEnum, ITaskOperation> createTaskOperationMap() {
      Map<TaskEnum, ITaskOperation> toReturn = new HashMap<TaskEnum, ITaskOperation>();

      toReturn.put(TaskEnum.CREATE, new TaskOpCreate(taskTitleProvider));
      toReturn.put(TaskEnum.MODIFY, new TaskOpModify());

      return toReturn;
   }

   public static interface StatusCollector {

      void onStatus(IStatus status, TaskMetadata metadata);

   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

      //Initialize the TaskEnum->TaskOperation map
      Map<TaskEnum, ITaskOperation> ops = null;
      if (reportOnly) {
         ops = createReportOnlyTaskOperationMap();
      } else {
         ops = createTaskOperationMap();
      }

      // Ensure the destination workflow exists. - destTeamWf is only necessary if NOT reportOnly
      TeamWorkFlowArtifact destTeamWf =
         ensureDestTeamWfExists(reqTeamWf, actionableItemArt, destVersion, transaction, reportOnly);
      if (destTeamWf == null) {
         String msg = "CreateTasksOperation: Failed to create new team workflow for [" + reqTeamWf.getName() + "]";
         OseeLog.log(Activator.class, Level.SEVERE, msg);
      }

      // Generate list of metadatas
      GenerateTaskOpList generateTaskOpList = new GenerateTaskOpList();
      List<TaskMetadata> metadatas = generateTaskOpList.generate(changeData, destTeamWf);

      // Execute taskOps
      ExecuteTaskOpList executeTaskOpList = new ExecuteTaskOpList();
      Map<TaskMetadata, IStatus> statusMap = executeTaskOpList.execute(metadatas, ops);

      if (!reportOnly) {
         if (transaction == null) {
            transaction =
               TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Auto-create tasks for " + reqTeamWf);
         }
         transaction.execute();
      }

      //TODO: Need to provide a status report (somehow) back to calling code
   }

   private TeamWorkFlowArtifact findDestTeamWf(TeamWorkFlowArtifact reqTeamWf, ActionableItemArtifact actionableItemArt, VersionArtifact destVersion) throws OseeCoreException {
      TeamWorkFlowArtifact destTeamWf = null;
      TeamDefinitionArtifact teamDef =
         TeamDefinitionManagerCore.getImpactedTeamDefs(Collections.singleton(actionableItemArt)).iterator().next();
      List<Artifact> deriveToArts = reqTeamWf.getRelatedArtifacts(AtsRelationTypes.Derive_To);
      for (Artifact derivedArt : deriveToArts) {
         TeamWorkFlowArtifact derivedTeamWfArt = null;
         if (derivedArt instanceof TeamWorkFlowArtifact) {
            //TODO: WRONG! - Need to make use of destVersion
            derivedTeamWfArt = (TeamWorkFlowArtifact) derivedArt;
            TeamDefinitionArtifact teamDefFromArt = derivedTeamWfArt.getTeamDefinition();
            ActionableItemManagerCore actionableItemsDamFromArt = derivedTeamWfArt.getActionableItemsDam();
            boolean isTeamDef = teamDefFromArt.equals(teamDef);
            boolean isAia = actionableItemsDamFromArt.getActionableItems().contains(actionableItemArt);
            if (isTeamDef && isAia) {
               destTeamWf = derivedTeamWfArt;
               break;
            }
         } else {
            OseeLog.log(
               Activator.class,
               Level.WARNING,
               "GenerateTaskOperationMetaData.findDestTeamWf(): Artifact in Derive_To relationship found that is not of type TeamWorkFlowArtifact. Derive_From:" + reqTeamWf.getArtId() + " Derive_To:" + derivedArt.getArtId());
         }
      }

      return destTeamWf;
   }

   /**
    * Get the team workflow related to the teamWf parameter via the 'derived from' relationship that is also targeted
    * for the version parameter. If such a workflow does not exist then return null.
    *
    * @throws OseeCoreException
    */
   private TeamWorkFlowArtifact ensureDestTeamWfExists(TeamWorkFlowArtifact reqTeamWf, ActionableItemArtifact actionableItemArt, VersionArtifact destVersion, SkynetTransaction transaction, boolean reportOnly) throws OseeCoreException {
      Date createdDate = new Date();
      IAtsUser createdBy = AtsUsersClient.getUser();

      TeamWorkFlowArtifact destTeamWf = findDestTeamWf(reqTeamWf, actionableItemArt, destVersion);

      if (destTeamWf == null && !reportOnly) {
         Artifact actionArt = reqTeamWf.getParentActionArtifact();
         TeamDefinitionArtifact teamDef =
            TeamDefinitionManagerCore.getImpactedTeamDefs(Collections.singleton(actionableItemArt)).iterator().next();

         destTeamWf =
            ActionManager.createTeamWorkflow(actionArt, teamDef, Collections.singleton(actionableItemArt),
               Arrays.asList(AtsUsersClient.getUser()), transaction, createdDate, createdBy, null,
               CreateTeamOption.Duplicate_If_Exists);
         if (destTeamWf != null) {
            destTeamWf.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
               Collections.singleton(destVersion));
         }
      }
      return destTeamWf;
   }

}
