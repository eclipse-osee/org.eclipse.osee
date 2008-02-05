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
package org.eclipse.osee.ats.config.demo.navigate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.health.ActionsHaveOneTeam;
import org.eclipse.osee.ats.health.AssignedActiveActions;
import org.eclipse.osee.ats.health.AttributeDuplication;
import org.eclipse.osee.ats.health.DuplicateUsersItem;
import org.eclipse.osee.ats.health.InvalidEstimatedHoursAttribute;
import org.eclipse.osee.ats.health.OrphanedTasks;
import org.eclipse.osee.ats.health.TeamWorkflowsHaveZeroOrOneVersion;
import org.eclipse.osee.ats.health.UnAssignedAssignedAtsObjects;
import org.eclipse.osee.ats.navigate.CreateNewVersionItem;
import org.eclipse.osee.ats.navigate.IAtsNavigateItem;
import org.eclipse.osee.ats.navigate.ReleaseVersionItem;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.search.CriteriaSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UnReleasedTeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateUrlItem;

/**
 * Provides the ATS Navigator items for the sample XYZ company's teams
 * 
 * @author Donald G. Dunne
 */
public class DemoNavigateViewItems implements IAtsNavigateItem {

   public DemoNavigateViewItems() {
      super();
   }

   public List<XNavigateItem> getNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (!ConnectionHandler.isConnected()) return items;

      // If Demo Teams not configured, ignore these navigate items
      try {
         if (DemoTeams.getInstance().getTeamDef(Team.Process_Team) == null) return items;
      } catch (Exception ex) {
         OSEELog.logWarning(OseeAtsConfigDemoPlugin.class, "Demo Teams Not Cofigured", ex, false);
         return items;
      }
      // If Demo Teams not configured, ignore these navigate items
      try {
         if (DemoTeams.getInstance().getTeamDef(Team.Process_Team) == null) return items;
      } catch (Exception ex) {
         OSEELog.logWarning(OseeAtsConfigDemoPlugin.class, "Demo Teams Not Cofigured", ex, false);
         return items;
      }
      XNavigateItem jhuItem = new XNavigateItem(null, "John Hopkins Univ (JHU)");
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Externally", "http://www.jhu.edu/", true);
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Internally", "http://www.jhu.edu/", false);

      items.add(jhuItem);

      for (Team team : Team.values()) {
         try {
            TeamDefinitionArtifact teamDef = DemoTeams.getInstance().getTeamDef(team);
            XNavigateItem teamItems = new XNavigateItem(jhuItem, "JHU " + team.name().replaceAll("_", " "));
            new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + teamDef + " Actions",
                  DemoTeams.getInstance().getTeamDef(team), false, true, true));
            new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + teamDef + " Workflows",
                  DemoTeams.getInstance().getTeamDef(team), false, false, true));
            // Handle all children teams
            for (TeamDefinitionArtifact childTeamDef : Artifacts.getChildrenOfTypeSet(
                  DemoTeams.getInstance().getTeamDef(team), TeamDefinitionArtifact.class, true)) {
               new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + childTeamDef + " Workflows",
                     childTeamDef, false, false, false));
            }
            if (teamDef.isTeamUsesVersions()) {
               if (team.name().contains("SAW"))
                  new XNavigateUrlItem(teamItems, "Open SAW Website", "http://www.cisst.org/cisst/saw/", false);
               else if (team.name().contains("CIS")) new XNavigateUrlItem(teamItems, "Open CIS Website",
                     "http://www.cisst.org/cisst/cis/", false);

               new SearchNavigateItem(teamItems, new NextVersionSearchItem(teamDef));
               new SearchNavigateItem(teamItems, new VersionTargetedForTeamSearchItem(teamDef, null, false));
               new SearchNavigateItem(teamItems, new UnReleasedTeamWorldSearchItem("Show Un-Released Team Workflows",
                     teamDef, true, false, true));
               new ReleaseVersionItem(teamItems, teamDef);
               new CreateNewVersionItem(teamItems, teamDef);
            }
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }

      XNavigateItem adminItems = new XNavigateItem(jhuItem, "JHU Admin");

      LinkedList<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(ActionArtifact.ARTIFACT_NAME, Operator.EQUAL));
      new SearchNavigateItem(adminItems, new CriteriaSearchItem("Show All Actions", criteria, true));

      criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(DecisionReviewArtifact.ARTIFACT_NAME, Operator.EQUAL));
      new SearchNavigateItem(adminItems, new CriteriaSearchItem("Show All Decision Review", criteria, true));

      criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(PeerToPeerReviewArtifact.ARTIFACT_NAME, Operator.EQUAL));
      new SearchNavigateItem(adminItems, new CriteriaSearchItem("Show All PeerToPeer Review", criteria, true));

      criteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtifactName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         criteria.add(new ArtifactTypeSearch(teamArtifactName, Operator.EQUAL));
      new SearchNavigateItem(adminItems, new CriteriaSearchItem("Show All Teams", criteria, false));

      criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(TaskArtifact.ARTIFACT_NAME, Operator.EQUAL));
      new SearchNavigateItem(adminItems, new CriteriaSearchItem("Show All Tasks", criteria, true));

      XNavigateItem healthItems = new XNavigateItem(adminItems, "Health");
      new AttributeDuplication(healthItems);
      new OrphanedTasks(healthItems);
      new InvalidEstimatedHoursAttribute(healthItems);
      new ActionsHaveOneTeam(healthItems);
      new AssignedActiveActions(healthItems);
      new DuplicateUsersItem(healthItems);
      new TeamWorkflowsHaveZeroOrOneVersion(healthItems);
      new UnAssignedAssignedAtsObjects(healthItems);

      XNavigateItem demoItems = new XNavigateItem(adminItems, "Demo Data");
      new PopulateDemoActions(demoItems);

      return items;
   }
}
