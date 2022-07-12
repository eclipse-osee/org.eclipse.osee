/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.config.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VersionMetrics {

   private final IAtsVersion verArt;
   private final VersionTeamMetrics verTeamMet;

   public VersionMetrics(IAtsVersion verArt, VersionTeamMetrics verTeamMet) {
      this.verArt = verArt;
      this.verTeamMet = verTeamMet;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(verArt.getName());
      sb.append("\n");
      try {
         sb.append("Workflows: ");
         sb.append(AtsApiService.get().getVersionService().getTargetedForTeamWorkflows(verArt).size());
         sb.append(" Problem: ");
         sb.append(getTeamWorkFlows(ChangeTypes.Problem).size());
         sb.append(" Improve: ");
         sb.append(getTeamWorkFlows(ChangeTypes.Improvement).size());
         sb.append(" Support: ");
         sb.append(getTeamWorkFlows(ChangeTypes.Support).size());
         sb.append(" Release Date: ");
         sb.append(AtsApiService.get().getVersionService().getReleaseDate(verArt));
         VersionMetrics prevVerMet = getPreviousVerMetViaReleaseDate();
         if (prevVerMet == null) {
            sb.append(" Prev Release Version: <not found>");
         } else {
            sb.append(" Prev Release Version \"");
            sb.append(prevVerMet);
            sb.append("\"   Release Date: ");
            sb.append(AtsApiService.get().getVersionService().getReleaseDate(verArt));
         }
         sb.append(" Start Date: ");
         sb.append(getReleaseStartDate());
         sb.append(" Num Days: ");
         sb.append(getNumberDaysInRelease());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return sb.toString();
   }

   public Integer getNumberDaysInRelease() {
      Date startDate = getReleaseStartDate();
      if (startDate == null) {
         return null;
      }
      Date relDate = AtsApiService.get().getVersionService().getReleaseDate(verArt);
      if (relDate == null) {
         return null;
      }
      return DateUtil.getDifference(startDate, relDate);
   }

   public Date getReleaseStartDate() {
      VersionMetrics prevVerMet = getPreviousVerMetViaReleaseDate();
      if (prevVerMet == null) {
         return null;
      }
      Date relDate = AtsApiService.get().getVersionService().getReleaseDate(verArt);
      return relDate;
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlows(ChangeTypes... changeType) {
      List<ChangeTypes> changeTypes = Arrays.asList(changeType);
      Set<TeamWorkFlowArtifact> teams = new HashSet<>();
      for (IAtsTeamWorkflow team : AtsApiService.get().getVersionService().getTargetedForTeamWorkflows(verArt)) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) team.getStoreObject();
         if (changeTypes.contains(ChangeTypeColumn.getChangeType(teamArt, AtsApiService.get()))) {
            teams.add(teamArt);
         }
      }
      return teams;
   }

   public VersionMetrics getPreviousVerMetViaReleaseDate() {
      if (AtsApiService.get().getVersionService().getReleaseDate(verArt) == null) {
         return null;
      }
      int index = verTeamMet.getReleasedOrderedVersions().indexOf(this);
      if (index > 0) {
         return verTeamMet.getReleasedOrderedVersions().get(index - 1);
      }
      return null;
   }

   public VersionMetrics getNextVerMetViaReleaseDate() {
      if (AtsApiService.get().getVersionService().getReleaseDate(verArt) == null) {
         return null;
      }
      int index = verTeamMet.getReleasedOrderedVersions().indexOf(this);
      if (index < verTeamMet.getReleasedOrderedVersions().size() - 1) {
         return verTeamMet.getReleasedOrderedVersions().get(index + 1);
      }
      return null;
   }

   public IAtsVersion getVerArt() {
      return verArt;
   }

}
