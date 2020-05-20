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
package org.eclipse.osee.ats.ide.workflow.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractAtsArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifact extends AbstractAtsArtifact implements IAtsAction {

   public ActionArtifact(BranchId branch) {
      this(AtsArtifactTypes.Action);
   }

   public ActionArtifact(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType) {
      super(id, guid, branch, artifactType);
   }

   public ActionArtifact(ArtifactTypeToken artifactType) {
      super(Lib.generateId(), null, CoreBranches.COMMON, artifactType);
   }

   public Set<IAtsActionableItem> getActionableItems() {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (TeamWorkFlowArtifact team : getTeams()) {
         aias.addAll(AtsClientService.get().getActionableItemService().getActionableItems(team));
      }
      return aias;
   }

   public Collection<TeamWorkFlowArtifact> getTeams() {
      return getRelatedArtifactsUnSorted(AtsRelationTypes.ActionToWorkflow_TeamWorkflow, TeamWorkFlowArtifact.class);
   }

   public TeamWorkFlowArtifact getFirstTeam() {
      if (getRelatedArtifactsCount(AtsRelationTypes.ActionToWorkflow_TeamWorkflow) > 0) {
         return getTeams().iterator().next();
      }
      return null;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeamWorkflows() {
      return Collections.castAll(getTeams());
   }

   @Override
   public String getAtsId() {
      String toReturn = getIdString();
      try {
         toReturn = getSoleAttributeValueAsString(AtsAttributeTypes.AtsId, toReturn);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.WARNING, ex);
      }
      return toReturn;
   }

   @Override
   public void setAtsId(String atsId) {
      setSoleAttributeFromString(AtsAttributeTypes.AtsId, atsId);
   }

   @Override
   public ArtifactToken getStoreObject() {
      return this;
   }

   @Override
   public AtsApi getAtsApi() {
      return AtsClientService.get();
   }

   @Override
   public Collection<WorkType> getWorkTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isWorkType(WorkType workType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<String> getTags() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasTag(String tag) {
      throw new UnsupportedOperationException();
   }

}