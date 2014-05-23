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
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.config.ActionableItemManager;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Shawn F. Cook
 */
public class CreateTasksOperation extends AbstractOperation {
   private final IAtsVersion destVersion;
   private final IAtsActionableItem actionableItemArt;
   private final ChangeData changeData;
   private final TeamWorkFlowArtifact reqTeamWf;
   private final boolean reportOnly;
   private final XResultData resultData;
   private final ITaskTitleProvider taskTitleProvider;
   private final IAtsChangeSet changes;

   public CreateTasksOperation(IAtsVersion destinationVersion, IAtsActionableItem actionableItemArt, ChangeData changeData, TeamWorkFlowArtifact reqTeamWf, boolean reportOnly, XResultData resultData, IAtsChangeSet changes, OperationLogger logger, ITaskTitleProvider taskTitleProvider) {
      super("Create Tasks Operation for [" + reqTeamWf.getName() + "]", Activator.PLUGIN_ID, logger);
      this.destVersion = destinationVersion;
      this.actionableItemArt = actionableItemArt;
      this.changeData = changeData;
      this.reqTeamWf = reqTeamWf;
      this.reportOnly = reportOnly;
      this.resultData = resultData;
      this.changes = changes;
      this.taskTitleProvider = taskTitleProvider;
   }

   private Map<TaskEnum, ITaskOperation> createTaskOperationMap() {
      Map<TaskEnum, ITaskOperation> toReturn = new HashMap<TaskEnum, ITaskOperation>();

      toReturn.put(TaskEnum.CREATE, new TaskOpCreate(taskTitleProvider));
      toReturn.put(TaskEnum.MODIFY, new TaskOpModify());

      return toReturn;
   }

   private Map<TaskEnum, ITaskOperation> createReportOnlyTaskOperationMap() {
      Map<TaskEnum, ITaskOperation> toReturn = new HashMap<TaskEnum, ITaskOperation>();

      toReturn.put(TaskEnum.CREATE, new TaskOpDoNothing(taskTitleProvider));
      toReturn.put(TaskEnum.MODIFY, new TaskOpDoNothing(taskTitleProvider));

      return toReturn;
   }

   public static interface StatusCollector {

      void onStatus(IStatus status, TaskMetadata metadata);

   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      //Initialize the TaskEnum->TaskOperation map
      Map<TaskEnum, ITaskOperation> ops = null;
      if (!reportOnly) {
         ops = createTaskOperationMap();
      } else {
         ops = createReportOnlyTaskOperationMap();
      }

      // Ensure the destination workflow exists. - destTeamWf is only necessary if NOT reportOnly
      TeamWorkFlowArtifact destTeamWf =
         ensureDestTeamWfExists(reqTeamWf, actionableItemArt, destVersion, changes, reportOnly);
      if (destTeamWf == null) {
         String msg = "CreateTasksOperation: Failed to create new team workflow for [" + reqTeamWf.getName() + "]";
         OseeLog.log(Activator.class, Level.SEVERE, msg);
      }

      // Generate list of metadatas
      GenerateTaskOpList generateTaskOpList = new GenerateTaskOpList();
      List<TaskMetadata> metadatas = generateTaskOpList.generate(changeData, destTeamWf);

      // Execute taskOps
      ExecuteTaskOpList executeTaskOpList = new ExecuteTaskOpList();
      Map<TaskMetadata, IStatus> statusMap = executeTaskOpList.execute(metadatas, ops, changes);

      // Populate status report
      resultData.addRaw("Status report for creating tasks for workflow:" + reqTeamWf.toStringWithId());
      resultData.addRaw(AHTML.beginMultiColumnTable(100, 1));
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {
         "Success?",
         "Create/Modify",
         "Dest TeamWF",
         "Task"}));
      Set<TaskMetadata> statusKeySet = statusMap.keySet();
      for (TaskMetadata metadata : statusKeySet) {
         IStatus status = statusMap.get(metadata);
         TaskEnum taskEnum = metadata.getTaskEnum();
         TeamWorkFlowArtifact parentTeamWf = metadata.getParentTeamWf();
         TaskArtifact taskArtifact = metadata.getTaskArtifact();

         String statusStr = status.isOK() ? "Success" : "Failed";
         String taskEnumStr = (taskEnum != null) ? taskEnum.name() : "[taskEnum null]";
         String parentTeamWfStr = (parentTeamWf != null) ? parentTeamWf.toStringWithId() : "[parentTeamWf null]";
         String taskArtifactStr = (taskArtifact != null) ? taskArtifact.toStringWithId() : "[taskEnum null]";

         resultData.addRaw(AHTML.addRowMultiColumnTable(statusStr, taskEnumStr, parentTeamWfStr, taskArtifactStr));
      }
      resultData.addRaw(AHTML.endMultiColumnTable());
   }

   private TeamWorkFlowArtifact findDestTeamWf(TeamWorkFlowArtifact reqTeamWf, IAtsActionableItem actionableItemArt, IAtsVersion destVersion) throws OseeCoreException {
      TeamWorkFlowArtifact destTeamWf = null;
      List<Artifact> deriveToArts = reqTeamWf.getRelatedArtifacts(AtsRelationTypes.Derive_To);
      for (Artifact derivedArt : deriveToArts) {
         TeamWorkFlowArtifact derivedTeamWfArt = null;
         if (derivedArt instanceof TeamWorkFlowArtifact) {
            derivedTeamWfArt = (TeamWorkFlowArtifact) derivedArt;

            IAtsVersion derivedArtVersion = AtsVersionService.get().getTargetedVersion(derivedTeamWfArt);
            boolean isDestVersion = destVersion.equals(derivedArtVersion);

            ActionableItemManager actionableItemsDamFromArt = derivedTeamWfArt.getActionableItemsDam();
            boolean isAia = actionableItemsDamFromArt.getActionableItems().contains(actionableItemArt);

            if (isDestVersion && isAia) {
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
    */
   private TeamWorkFlowArtifact ensureDestTeamWfExists(TeamWorkFlowArtifact reqTeamWf, IAtsActionableItem actionableItemArt, IAtsVersion destVersion, IAtsChangeSet changes, boolean reportOnly) throws OseeCoreException {
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();

      TeamWorkFlowArtifact destTeamWf = findDestTeamWf(reqTeamWf, actionableItemArt, destVersion);

      if (destTeamWf == null && !reportOnly) {
         Artifact actionArt = reqTeamWf.getParentActionArtifact();
         IAtsTeamDefinition teamDef =
            TeamDefinitions.getImpactedTeamDefs(Collections.singleton(actionableItemArt)).iterator().next();

         destTeamWf =
            ActionManager.createTeamWorkflow(actionArt, teamDef, Collections.singleton(actionableItemArt),
               Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), changes, createdDate, createdBy,
               null, CreateTeamOption.Duplicate_If_Exists);
         if (destTeamWf != null) {
            AtsVersionService.get().setTargetedVersionAndStore(destTeamWf, destVersion);
         }
      }
      return destTeamWf;
   }
}
