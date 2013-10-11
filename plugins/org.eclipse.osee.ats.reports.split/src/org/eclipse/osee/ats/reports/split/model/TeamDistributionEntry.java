/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.split.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class to compute the work from work flow and fill the map
 * 
 * @author Chandan Bandemutt
 */
public class TeamDistributionEntry {

   private final Artifact versionArtifact;
   private final Map<String, Double> teamSplitMap;

   /**
    * Constructor to set the version artifact and to instantiate the map
    * 
    * @param verArtifact : sets the version artifact
    */
   public TeamDistributionEntry(final Artifact verArtifact) {
      this.versionArtifact = verArtifact;
      this.teamSplitMap = new HashMap<String, Double>();
   }

   /**
    * Method to compute the work from work flow and fill the map
    * 
    * @throws OseeCoreException :
    */
   public void computeTeamSplit() throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> teamWorkflows =
         this.versionArtifact.getRelatedArtifactsOfType(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
            TeamWorkFlowArtifact.class);
      for (TeamWorkFlowArtifact workflow : teamWorkflows) {
         double work = 0;
         String teamName = workflow.getTeamName();
         if (this.teamSplitMap.containsKey(teamName)) {
            work = this.teamSplitMap.get(teamName);
         }
         double hoursSpent = HoursSpentUtil.getHoursSpentTotal(workflow);
         this.teamSplitMap.put(teamName, work + hoursSpent);
      }
   }

   /**
    * @return the map
    */
   public Map<String, Double> getTeamSplitMap() {
      return this.teamSplitMap;
   }

}
