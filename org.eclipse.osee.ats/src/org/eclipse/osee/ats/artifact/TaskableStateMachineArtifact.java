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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public abstract class TaskableStateMachineArtifact extends StateMachineArtifact {

   public TaskableStateMachineArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMAEditorRelation(AtsRelationTypes.SmaToTask_Task);
      registerAtsWorldRelation(AtsRelationTypes.SmaToTask_Task);
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = getParentTeamWorkflow();
      if (teamArt == null) return null;
      return teamArt.getWorldViewTargetedVersion();
   }

   @Override
   public boolean showTaskTab() throws OseeCoreException {
      return (isTaskable() || smaMgr.isCompleted() || smaMgr.isCancelled());
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.atsDelete(deleteArts, allRelated);
   }

   @Override
   public void transitioned(WorkPageDefinition fromPage, WorkPageDefinition toPage, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(fromPage, toPage, toAssignees, persist, transaction);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.parentWorkFlowTransitioned(fromPage, toPage, toAssignees, persist, transaction);
   }

   public String getWorldViewNumberOfTasksRemaining() throws OseeCoreException {
      if (smaMgr.getTaskMgr().getTaskArtifacts().size() == 0) return "";
      return String.valueOf(smaMgr.getTaskMgr().getNumTasksInWork());
   }

}