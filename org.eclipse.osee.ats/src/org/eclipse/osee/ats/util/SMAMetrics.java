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
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class SMAMetrics {

   private static int MILLISECS_PER_DAY = (1000 * 60 * 60 * 24);

   public static String getEstRemainMetrics(Collection<StateMachineArtifact> smas) {
      if (smas.size() == 0) return "";
      try {
         int numTeamWfs = 0;
         int numTasks = 0;
         for (Artifact art : smas) {
            if (art instanceof TeamWorkFlowArtifact) {
               smas.add((TeamWorkFlowArtifact) art);
               numTeamWfs++;
            } else if ((art instanceof ActionArtifact) && ((ActionArtifact) art).getTeamWorkFlowArtifacts().size() > 0) {
               smas.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
               numTeamWfs += ((ActionArtifact) art).getTeamWorkFlowArtifacts().size();
            } else if (art instanceof TaskArtifact) {
               smas.add((TaskArtifact) art);
               numTasks++;
            }
         }
         int numObjects = numTasks + numTeamWfs;
         double estHours = 0;
         double hrsRemain = 0;
         double hrsSpent = 0;
         double manDaysNeeded = 0;
         double totalPercentComplete = 0;
         for (StateMachineArtifact team : smas) {
            hrsRemain += team.getWorldViewRemainHours();
            estHours += team.getWorldViewEstimatedHours();
            hrsSpent += team.getWorldViewTotalHoursSpent();
            manDaysNeeded += team.getWorldViewManDaysNeeded();
            totalPercentComplete += team.getWorldViewTotalPercentComplete();
         }
         double percentComplete = 0;
         if (numObjects > 0 && totalPercentComplete > 0) percentComplete = totalPercentComplete / numObjects;
         return String.format("  Selected %s  %s  EstHrs: %5.2f  %sCmp: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  %s",
               (numTeamWfs > 0 ? "TeamWFs: " + numTeamWfs : ""), (numTasks > 0 ? "Tasks: " + numTasks : ""), estHours,
               "%", percentComplete, hrsRemain, hrsSpent, (manDaysNeeded > 0 ? String.format("ManDaysNeeded: %5.2f ",
                     manDaysNeeded) : ""));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return "Exception occurred - see log";
      }
   }

   private static double manDayHrs = 7.2;

   public static String getReleaseEstRemainMetrics(Collection<StateMachineArtifact> smas) {
      if (smas.size() == 0) return "";
      try {
         Artifact art = smas.iterator().next();
         TeamWorkFlowArtifact selTeam = null;
         if (art instanceof TeamWorkFlowArtifact)
            selTeam = (TeamWorkFlowArtifact) art;
         else if ((art instanceof ActionArtifact) && ((ActionArtifact) art).getTeamWorkFlowArtifacts().size() == 1) selTeam =
               ((ActionArtifact) art).getTeamWorkFlowArtifacts().iterator().next();
         if (selTeam != null) {
            if (selTeam.getTargetedForVersion() != null) {
               VersionArtifact verArt = selTeam.getTargetedForVersion();
               Collection<TeamWorkFlowArtifact> teams = verArt.getTargetedForTeamArtifacts();
               double estHours = 0;
               double hrsRemain = 0;
               double hrsSpent = 0;
               for (TeamWorkFlowArtifact team : teams) {
                  hrsRemain += team.getWorldViewRemainHours();
                  estHours += team.getWorldViewEstimatedHours();
                  hrsSpent += team.getWorldViewTotalHoursSpent();
               }
               Date estRelDate = verArt.getEstimatedReleaseDate();
               double manDaysNeeded = 0;
               if (hrsRemain != 0) manDaysNeeded = hrsRemain / manDayHrs;
               String daysLeft = "";
               Date today = new Date();
               long daysTillRel = 0;
               if (estRelDate != null && estRelDate.after(today)) {
                  daysTillRel = (estRelDate.getTime() - today.getTime()) / MILLISECS_PER_DAY;
                  daysLeft = String.format(" DaysLft: %d", daysTillRel);
               }
               return String.format(
                     "  Target Version: %s  EstRelDate: %s  %s TeamWFs: %s  EstHrs: %5.2f  RmnHrs: %5.2f  HrsSpnt: %5.2f  ManDaysNeeded: %5.2f  ",
                     verArt.getDescriptiveName(), XDate.getDateStr(estRelDate, XDate.MMDDYY), daysLeft, teams.size(),
                     estHours, hrsRemain, hrsSpent, manDaysNeeded);

            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return "Exception occurred - see log";
      }
      return "";
   }
}
