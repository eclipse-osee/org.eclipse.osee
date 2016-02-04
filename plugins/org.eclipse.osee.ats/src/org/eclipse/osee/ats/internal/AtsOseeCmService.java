/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDataFactory;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsOseeCmService implements IOseeCmService {

   @Override
   public boolean isCmAdmin() {
      return AtsUtilClient.isAtsAdmin();
   }

   @Override
   public void openArtifact(String guid, OseeCmEditor oseeCmEditor) {
      AtsUtil.openArtifact(guid, oseeCmEditor);
   }

   @Override
   public void openArtifact(Artifact artifact, OseeCmEditor oseeCmEditor) {
      AtsUtil.openATSArtifact(artifact);
   }

   @Override
   public void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor) {
      WorldEditor.open(new WorldEditorSimpleProvider(name, artifacts));
   }

   @Override
   public void openArtifactsByGuid(String name, List<String> guidOrAtsIds, OseeCmEditor oseeCmEditor) {
      try {
         List<Artifact> artifacts = AtsArtifactQuery.getArtifactListFromIds(guidOrAtsIds);
         openArtifacts(name, artifacts, oseeCmEditor);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error opening ATS artifacts by Id", ex);
      }
   }

   @Override
   public KeyedImage getOpenImage(OseeCmEditor oseeCmEditor) {
      if (oseeCmEditor == OseeCmEditor.CmPcrEditor) {
         return AtsImage.TEAM_WORKFLOW;
      } else if (oseeCmEditor == OseeCmEditor.CmMultiPcrEditor) {
         return AtsImage.GLOBE;
      }
      return FrameworkImage.LASER;
   }

   @Override
   public boolean isPcrArtifact(Artifact artifact) {
      return AtsUtil.isAtsArtifact(artifact);
   }

   @Override
   public boolean isCompleted(Artifact artifact) {
      boolean completed = false;
      if (isPcrArtifact(artifact) && artifact instanceof AbstractWorkflowArtifact) {
         completed = ((AbstractWorkflowArtifact) artifact).isCompletedOrCancelled();
      }
      return completed;
   }

   @Override
   public List<Artifact> getTaskArtifacts(Artifact pcrArtifact) {
      if (pcrArtifact instanceof TeamWorkFlowArtifact) {
         try {
            List<Artifact> arts = new ArrayList<>();
            arts.addAll(((TeamWorkFlowArtifact) pcrArtifact).getTaskArtifacts());
            return arts;
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return Collections.emptyList();
   }

   @Override
   public Artifact createWorkTask(String name, String parentPcrGuid) {
      try {
         Artifact artifact = AtsArtifactQuery.getArtifactFromId(parentPcrGuid);
         Date createdDate = new Date();
         NewTaskData newTaskData = NewTaskDataFactory.get(getClass().getSimpleName() + " - Create Work Task",
            AtsClientService.get().getUserService().getCurrentUser().getUserId(), artifact.getUuid());
         JaxAtsTaskFactory.get(newTaskData, name, AtsClientService.get().getUserService().getCurrentUser(),
            createdDate);
         AtsClientService.getTaskEp().create(new NewTaskDatas(newTaskData));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   @Override
   public Artifact createPcr(String title, String description, String changeType, String priority, Date needByDate, Collection<String> productNames) {
      try {
         ChangeType cType = ChangeType.getChangeType(changeType);
         if (cType == null) {
            cType = ChangeType.Improvement;
         }
         Set<IAtsActionableItem> aias =
            ActionableItems.getActionableItems(productNames, AtsClientService.get().getConfig());
         if (aias.isEmpty()) {
            throw new OseeArgumentException("Can not resolve productNames to Actionable Items");
         }
         NewActionJob job = new NewActionJob(title, description, cType, priority, needByDate, false, aias, null, null);
         job.schedule();
         job.join();
         return job.getActionArt();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public boolean isBranchesAllCommittedExcept(Artifact art, Branch branch) {
      boolean toReturn = false;
      if (art instanceof TeamWorkFlowArtifact) {
         try {
            toReturn = AtsClientService.get().getBranchService().isBranchesAllCommittedExcept(
               (TeamWorkFlowArtifact) art, branch);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
            toReturn = false;
         }
      }
      return toReturn;
   }

   @Override
   public KeyedImage getImage(ImageType imageType) {
      if (imageType == ImageType.Pcr) {
         return AtsImage.TEAM_WORKFLOW;
      } else if (imageType == ImageType.Task) {
         return AtsImage.TASK;
      }
      return AtsImage.ACTION;
   }

   @Override
   public IArtifactType getPcrArtifactType() {
      return AtsArtifactTypes.TeamWorkflow;
   }

   @Override
   public IArtifactType getPcrTaskArtifactType() {
      return AtsArtifactTypes.Task;
   }

   @Override
   public BranchId getCmBranchToken() {
      return AtsUtilCore.getAtsBranch();
   }

   @Override
   public boolean isWorkFlowBranch(BranchId branch) {
      boolean toReturn = false;
      Artifact art;
      try {
         art = BranchManager.getAssociatedArtifact(branch);
         if (art instanceof TeamWorkFlowArtifact) {
            toReturn = true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
         toReturn = false;
      }

      return toReturn;
   }

   @Override
   public void commitBranch(Artifact art, Branch branch, boolean isArchiveSource) {
      if (art instanceof TeamWorkFlowArtifact) {
         IOperation operation;
         try {
            operation =
               AtsBranchManager.commitWorkingBranch((TeamWorkFlowArtifact) art, false, false, branch, isArchiveSource);
            Operations.executeAsJob(operation, true);

         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
         }
      }
   }
}
