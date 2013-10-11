/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.efficiency.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.reports.efficiency.internal.Activator;
import org.eclipse.osee.ats.reports.efficiency.internal.AtsClientService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Class to collect the list of artifacts and compute work done
 * 
 * @author Praveen Joseph
 */
public class VersionEfficiency {

   private final IAtsVersion version;
   private final Map<String, Double> efficiency;

   /**
    * Constructor to set the version
    * 
    * @param version :
    */
   public VersionEfficiency(final IAtsVersion version) {
      this.version = version;
      this.efficiency = new HashMap<String, Double>();
   }

   /**
    * Method to collect the list of artifacts and compute work done
    * 
    * @throws OseeCoreException :
    */
   public void compute() throws OseeCoreException {
      final Map<String, List<TeamWorkFlowArtifact>> teams = new HashMap<String, List<TeamWorkFlowArtifact>>();

      // 1. For each team, collect the list of artifacts.
      for (TeamWorkFlowArtifact teamWorkflow : AtsClientService.get().getAtsVersionService().getTargetedForTeamWorkflowArtifacts(
         this.version)) {
         if (teams.containsKey(teamWorkflow.getTeamName())) {
            teams.get(teamWorkflow.getTeamName()).add(teamWorkflow);
         } else {
            List<TeamWorkFlowArtifact> team = new ArrayList<TeamWorkFlowArtifact>();
            team.add(teamWorkflow);
            teams.put(teamWorkflow.getTeamName(), team);
         }
      }
      // 2. compute work done
      Iterator<String> teamNames = teams.keySet().iterator();
      while (teamNames.hasNext()) {
         String teamName = teamNames.next();
         double estimated = 0;
         double actual = 0;
         for (TeamWorkFlowArtifact twa : teams.get(teamName)) {
            estimated += twa.getEstimatedHoursTotal();
            actual += HoursSpentUtil.getHoursSpentTotal(twa);
         }
         if (actual != 0) {
            this.efficiency.put(teamName, estimated / actual);
         } else {
            Activator.getDefault().getLog().log(
               new Status(IStatus.INFO, Activator.PLUGIN_ID, "Actual hours spent by team : " + teamName + " was 0"));
         }
      }

   }

   /**
    * @return the map
    */
   public Map<String, Double> getEfficiency() {
      return this.efficiency;
   }

   /**
    * @return the version artifact
    */
   public IAtsVersion getVersion() {
      return this.version;
   }
}
