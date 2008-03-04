/*
 * Created on Mar 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class VersionMetrics {

   private final VersionArtifact verArt;
   private final VersionTeamMetrics verTeamMet;

   public VersionMetrics(VersionArtifact verArt, VersionTeamMetrics verTeamMet) {
      this.verArt = verArt;
      this.verTeamMet = verTeamMet;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer(verArt.getDescriptiveName() + "\n");
      try {
         sb.append("Workflows: " + verArt.getTargetedForTeamArtifacts().size());
         sb.append("Problem: " + getTeamWorkFlows(ChangeType.Problem).size() + " Improve: " + getTeamWorkFlows(ChangeType.Improvement) + " Support: " + getTeamWorkFlows(ChangeType.Support));
         sb.append("Release Date: " + verArt.getReleaseDate());
         VersionMetrics prevVerMet = getPreviousVerMetViaReleaseDate();
         if (prevVerMet == null) {
            sb.append("Prev Release Version: <not found>");
         } else {
            sb.append("Prev Release Version \"" + prevVerMet + "\"   Release Date: " + verArt.getReleaseDate());
         }
         sb.append("Start Date: " + getReleaseStartDate());
         sb.append("Num Days: " + getNumberDaysInRelease());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return sb.toString();
   }

   public Integer getNumberDaysInRelease() throws Exception {
      Date startDate = getReleaseStartDate();
      if (startDate == null) return null;
      if (verArt.getReleaseDate() == null) return null;
      return XDate.calculateDifference(startDate, verArt.getReleaseDate());
   }

   public Date getReleaseStartDate() throws Exception {
      VersionMetrics prevVerMet = getPreviousVerMetViaReleaseDate();
      if (prevVerMet == null) return null;
      return prevVerMet.getVerArt().getReleaseDate();
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlows(ChangeType... changeType) throws Exception {
      List<ChangeType> changeTypes = Arrays.asList(changeType);
      Set<TeamWorkFlowArtifact> teams = new HashSet<TeamWorkFlowArtifact>();
      for (TeamWorkFlowArtifact team : verArt.getTargetedForTeamArtifacts()) {
         if (changeTypes.contains(team.getChangeType())) teams.add(team);
      }
      return teams;
   }

   public VersionMetrics getPreviousVerMetViaReleaseDate() throws Exception {
      if (verArt.getReleaseDate() == null) return null;
      int index = verTeamMet.getReleasedOrderedVersions().indexOf(this);
      if (index > 0) return verTeamMet.getReleasedOrderedVersions().get(index - 1);
      return null;
   }

   /**
    * @return the verArt
    */
   public VersionArtifact getVerArt() {
      return verArt;
   }

   /**
    * @return the verTeamMet
    */
   public VersionTeamMetrics getVerTeamMet() {
      return verTeamMet;
   }

}
