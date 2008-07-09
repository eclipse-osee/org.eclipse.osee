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
package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class VersionTeamMetrics {

   private final TeamDefinitionArtifact verTeamDef;
   private List<VersionMetrics> releasedOrderedVersions = new ArrayList<VersionMetrics>();
   private Set<VersionMetrics> verMets = new HashSet<VersionMetrics>();
   Map<Date, VersionMetrics> relDateToVerMet = new HashMap<Date, VersionMetrics>();

   public VersionTeamMetrics(TeamDefinitionArtifact verTeamDef) throws OseeCoreException, SQLException {
      this.verTeamDef = verTeamDef;
      loadMetrics();
   }

   private void loadMetrics() throws OseeCoreException, SQLException {
      orderReleasedVersions();
   }

   public Collection<TeamWorkFlowArtifact> getWorkflowsOriginatedBetween(Date startDate, Date endDate) throws OseeCoreException, SQLException {
      Set<TeamWorkFlowArtifact> teams = new HashSet<TeamWorkFlowArtifact>();
      for (VersionArtifact verArt : verTeamDef.getVersionsArtifacts()) {
         for (TeamWorkFlowArtifact team : verArt.getTargetedForTeamArtifacts()) {
            Date origDate = team.getSmaMgr().getLog().getCreationDate();
            if (origDate != null) {
               if (origDate.after(startDate) && origDate.before(endDate)) teams.add(team);
            }
         }
      }
      return teams;
   }

   private void orderReleasedVersions() throws OseeCoreException, SQLException {
      for (VersionArtifact ver : verTeamDef.getVersionsArtifacts()) {
         VersionMetrics verMet = new VersionMetrics(ver, this);
         if (ver.getReleaseDate() != null) relDateToVerMet.put(ver.getReleaseDate(), verMet);
         verMets.add(verMet);
      }
      Date[] releases = relDateToVerMet.keySet().toArray(new Date[relDateToVerMet.keySet().size()]);
      Arrays.sort(releases);
      for (Date date : releases) {
         releasedOrderedVersions.add(relDateToVerMet.get(date));
      }
   }

   /**
    * @return the verTeamDef
    */
   public TeamDefinitionArtifact getVerTeamDef() {
      return verTeamDef;
   }

   /**
    * @return the releasedOrderedVersions
    */
   public List<VersionMetrics> getReleasedOrderedVersions() {
      return releasedOrderedVersions;
   }

   /**
    * @return the verMets
    */
   public Set<VersionMetrics> getVerMets() {
      return verMets;
   }

   /**
    * @return the relDateToVerMet
    */
   public Map<Date, VersionMetrics> getRelDateToVerMet() {
      return relDateToVerMet;
   }
}
