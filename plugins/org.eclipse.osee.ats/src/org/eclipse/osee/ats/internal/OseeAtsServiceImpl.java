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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public class OseeAtsServiceImpl implements IOseeCmService {

   @Override
   public boolean isCmAdmin() {
      return AtsUtil.isAtsAdmin();
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
      return artifact instanceof StateMachineArtifact;
   }

   @Override
   public boolean isCompleted(Artifact artifact) {
      if (isPcrArtifact(artifact)) {
         try {
            return ((StateMachineArtifact) artifact).isCancelledOrCompleted();
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return false;
   }

   @Override
   public List<Artifact> getTaskArtifacts(Artifact pcrArtifact) {
      if (pcrArtifact instanceof TaskableStateMachineArtifact) {
         try {
            List<Artifact> arts = new ArrayList<Artifact>();
            arts.addAll(((TaskableStateMachineArtifact) pcrArtifact).getTaskArtifacts());
            return arts;
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return Collections.emptyList();
   }

   @Override
   public Artifact createWorkTask(String name, String parentPcrGuid) {
      try {
         Artifact artifact = ArtifactQuery.getArtifactFromId(parentPcrGuid, AtsUtil.getAtsBranch());
         if (artifact instanceof TaskableStateMachineArtifact) {
            return ((TaskableStateMachineArtifact) artifact).createNewTask(name);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         PriorityType pType = PriorityType.getPriority(priority);
         if (pType == null) {
            pType = PriorityType.None;
         }
         Set<ActionableItemArtifact> aias = ActionableItemArtifact.getActionableItems(productNames);
         if (aias.size() == 0) {
            throw new OseeArgumentException("Can not resolve productNames to Actionable Items");
         }
         NewActionJob job = new NewActionJob(title, description, cType, pType, needByDate, false, aias, null);
         job.schedule();
         job.join();
         return job.getActionArt();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
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
}
