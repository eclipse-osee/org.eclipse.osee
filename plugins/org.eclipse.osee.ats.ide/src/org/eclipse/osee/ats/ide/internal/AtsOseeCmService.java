/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.internal;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
   public void openArtifact(ArtifactId artifact, OseeCmEditor oseeCmEditor) {
      AtsEditors.openATSArtifact(AtsApiService.get().getQueryService().getArtifact(artifact));
   }

   @Override
   public void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor) {
      WorldEditor.open(new WorldEditorSimpleProvider(name, artifacts));
   }

   @Override
   public boolean isPcrArtifact(Artifact artifact) {
      return AtsObjects.isAtsWorkItemOrAction(artifact);
   }

   @Override
   public boolean isBranchesAllCommittedExcept(Artifact art, BranchId branch) {
      boolean toReturn = false;
      if (art instanceof TeamWorkFlowArtifact) {
         try {
            toReturn =
               AtsApiService.get().getBranchService().isBranchesAllCommittedExcept((TeamWorkFlowArtifact) art, branch);
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
   public XResultData commitBranch(Artifact art, BranchToken branch, boolean isArchiveSource, XResultData rd) {
      if (art instanceof TeamWorkFlowArtifact) {
         AtsApiService.get().getBranchServiceIde().commitWorkingBranch((TeamWorkFlowArtifact) art, false, false, branch,
            isArchiveSource, rd);
      }
      return rd;
   }
}
