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
package org.eclipse.osee.ats.ide.config.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class VersionTeamMetrics {

   private final IAtsTeamDefinition verTeamDef;
   private final List<VersionMetrics> releasedOrderedVersions = new ArrayList<>();
   private final Set<VersionMetrics> verMets = new HashSet<>();
   Map<Date, VersionMetrics> relDateToVerMet = new HashMap<>();

   public VersionTeamMetrics(IAtsTeamDefinition verTeamDef) {
      this.verTeamDef = verTeamDef;
      loadMetrics();
   }

   private void loadMetrics() {
      bulkLoadArtifacts();
      orderReleasedVersions();
   }

   private void bulkLoadArtifacts() {
      RelationManager.getRelatedArtifacts(
         Arrays.asList(AtsClientService.get().getQueryServiceClient().getArtifact(this.verTeamDef)), 6,
         CoreRelationTypes.DefaultHierarchical_Child, AtsRelationTypes.TeamDefinitionToVersion_Version,
         AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow, AtsRelationTypes.TeamWfToTask_Task,
         AtsRelationTypes.ActionToWorkflow_Action);
   }

   private Map<TeamWorkFlowArtifact, Date> teamWorkflowToOrigDate = null;

   public Collection<TeamWorkFlowArtifact> getWorkflowsOriginatedBetween(Date startDate, Date endDate) {
      if (teamWorkflowToOrigDate == null) {
         teamWorkflowToOrigDate = new HashMap<>();
         for (IAtsVersion verArt : AtsClientService.get().getVersionService().getVersions(verTeamDef)) {
            for (IAtsTeamWorkflow team : AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(
               verArt)) {
               Date origDate = team.getCreatedDate();
               teamWorkflowToOrigDate.put((TeamWorkFlowArtifact) team.getStoreObject(), origDate);
            }
         }
      }
      Set<TeamWorkFlowArtifact> teams = new HashSet<>();
      for (Entry<TeamWorkFlowArtifact, Date> entry : teamWorkflowToOrigDate.entrySet()) {
         if (entry.getValue() != null && entry.getValue().after(startDate) && entry.getValue().before(endDate)) {
            teams.add(entry.getKey());
         }
      }
      return teams;
   }

   private void orderReleasedVersions() {
      for (IAtsVersion verArt : AtsClientService.get().getVersionService().getVersions(verTeamDef)) {
         VersionMetrics verMet = new VersionMetrics(verArt, this);
         Date relDate = AtsClientService.get().getVersionService().getReleaseDate(verArt);
         if (relDate != null) {
            relDateToVerMet.put(relDate, verMet);
         }
         verMets.add(verMet);
      }
      Date[] releases = relDateToVerMet.keySet().toArray(new Date[relDateToVerMet.keySet().size()]);
      Arrays.sort(releases);
      for (Date date : releases) {
         releasedOrderedVersions.add(relDateToVerMet.get(date));
      }
   }

   public List<VersionMetrics> getReleasedOrderedVersions() {
      return releasedOrderedVersions;
   }

}
