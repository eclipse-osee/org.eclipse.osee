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
package org.eclipse.osee.ats.config.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
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
         sb.append(AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(verArt).size());
         sb.append(" Problem: ");
         sb.append(getTeamWorkFlows(ChangeType.Problem).size());
         sb.append(" Improve: ");
         sb.append(getTeamWorkFlows(ChangeType.Improvement).size());
         sb.append(" Support: ");
         sb.append(getTeamWorkFlows(ChangeType.Support).size());
         sb.append(" Release Date: ");
         sb.append(verArt.getReleaseDate());
         VersionMetrics prevVerMet = getPreviousVerMetViaReleaseDate();
         if (prevVerMet == null) {
            sb.append(" Prev Release Version: <not found>");
         } else {
            sb.append(" Prev Release Version \"");
            sb.append(prevVerMet);
            sb.append("\"   Release Date: ");
            sb.append(verArt.getReleaseDate());
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
      Date relDate = verArt.getReleaseDate();
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
      Date relDate = prevVerMet.getVerArt().getReleaseDate();
      return relDate;
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlows(ChangeType... changeType) {
      List<ChangeType> changeTypes = Arrays.asList(changeType);
      Set<TeamWorkFlowArtifact> teams = new HashSet<>();
      for (IAtsTeamWorkflow team : AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(verArt)) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) team.getStoreObject();
         if (changeTypes.contains(ChangeTypeUtil.getChangeType(teamArt))) {
            teams.add(teamArt);
         }
      }
      return teams;
   }

   public VersionMetrics getPreviousVerMetViaReleaseDate() {
      if (verArt.getReleaseDate() == null) {
         return null;
      }
      int index = verTeamMet.getReleasedOrderedVersions().indexOf(this);
      if (index > 0) {
         return verTeamMet.getReleasedOrderedVersions().get(index - 1);
      }
      return null;
   }

   public VersionMetrics getNextVerMetViaReleaseDate() {
      if (verArt.getReleaseDate() == null) {
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
