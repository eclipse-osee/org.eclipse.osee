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
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class LegacyPCRActionsWorldSearchItem extends WorldUISearchItem {
   private final boolean returnActions;
   private final Collection<String> pcrIds;
   private final Collection<IAtsTeamDefinition> teamDefs;

   public LegacyPCRActionsWorldSearchItem(Collection<String> pcrIds, Collection<IAtsTeamDefinition> teamDefs, boolean returnActions) {
      super("");
      this.pcrIds = pcrIds;
      this.teamDefs = teamDefs;
      this.returnActions = returnActions;
   }

   public LegacyPCRActionsWorldSearchItem(LegacyPCRActionsWorldSearchItem legacyPCRActionsWorldSearchItem) {
      super(legacyPCRActionsWorldSearchItem);
      this.returnActions = legacyPCRActionsWorldSearchItem.returnActions;
      this.pcrIds = legacyPCRActionsWorldSearchItem.pcrIds;
      this.teamDefs = legacyPCRActionsWorldSearchItem.teamDefs;
   }

   private boolean isPcrIdsSet() {
      return pcrIds != null && !pcrIds.isEmpty();
   }

   private boolean isTeamDefsSet() {
      return teamDefs != null && !teamDefs.isEmpty();
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {

      List<Artifact> pcrIdArts = new ArrayList<>();
      List<Artifact> teamDefArts = new ArrayList<>();
      List<Long> teamDefIds = new ArrayList<>();

      if (isPcrIdsSet()) {
         LegacyPcrIdQuickSearch srch = new LegacyPcrIdQuickSearch(pcrIds);
         pcrIdArts.addAll(srch.performSearch());
      }
      if (isTeamDefsSet()) {
         TeamDefinitionQuickSearch srch = new TeamDefinitionQuickSearch(teamDefs);
         teamDefArts.addAll(srch.performSearch());
         teamDefIds = AtsObjects.toIds(teamDefs);
      }

      // If both set, return intersection; else return just what was set
      List<Artifact> arts = new ArrayList<>();
      if (isPcrIdsSet() && isTeamDefsSet()) {
         arts = Collections.setIntersection(pcrIdArts, teamDefArts);
      } else if (isPcrIdsSet()) {
         arts = pcrIdArts;
      } else if (isTeamDefsSet()) {
         arts = teamDefArts;
      }

      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (Artifact art : arts) {
         workItems.add((IAtsWorkItem) art);
      }

      IAtsWorkItemFilter filter =
         AtsClientService.get().getQueryService().createFilter(workItems).withOrValue(AtsAttributeTypes.LegacyPcrId,
            pcrIds).withOrValue(AtsAttributeTypes.TeamDefinitionReference, teamDefIds).isOfType(
               AtsArtifactTypes.TeamWorkflow);

      List<Artifact> results = new ArrayList<>();
      if (returnActions) {
         for (IAtsWorkItem workItem : filter.getItems()) {
            results.add(((AbstractWorkflowArtifact) workItem.getStoreObject()).getParentActionArtifact());
         }
      } else {
         for (IAtsWorkItem workItem : filter.getItems()) {
            results.add((AbstractWorkflowArtifact) workItem.getStoreObject());
         }
      }
      return results;

   }

   @Override
   public WorldUISearchItem copy() {
      return new LegacyPCRActionsWorldSearchItem(this);
   }

}