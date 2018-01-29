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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsOseeCmService implements IOseeCmService {

   @Override
   public void openArtifact(String id, OseeCmEditor oseeCmEditor) {
      AtsUtil.openArtifact(id, oseeCmEditor);
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
   public boolean isPcrArtifact(Artifact artifact) {
      return AtsUtil.isAtsArtifact(artifact);
   }

   @Override
   public boolean isBranchesAllCommittedExcept(Artifact art, BranchId branch) {
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
   public void commitBranch(Artifact art, IOseeBranch branch, boolean isArchiveSource) {
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
