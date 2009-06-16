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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.navigate.CreateNewVersionItem;
import org.eclipse.osee.ats.navigate.IAtsNavigateItem;
import org.eclipse.osee.ats.navigate.ReleaseVersionItem;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypesSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemFolder;
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

   public List<XNavigateItem> getNavigateItems() throws OseeCoreException {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (AtsPlugin.areOSEEServicesAvailable().isFalse()) return items;

      // If Demo Teams not configured, ignore these navigate items
      try {
         if (DemoTeams.getInstance().getTeamDef(Team.Process_Team) == null) return items;
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.WARNING, "Demo Teams Not Cofigured", ex);
         return items;
      }
      // If Demo Teams not configured, ignore these navigate items
      try {
         if (DemoTeams.getInstance().getTeamDef(Team.Process_Team) == null) return items;
      } catch (Exception ex) {
         OseeLog.log(OseeAtsConfigDemoActivator.class, Level.WARNING, "Demo Teams Not Cofigured", ex);
         return items;
      }
      XNavigateItem jhuItem = new XNavigateItemFolder(null, "John Hopkins Univ (JHU)");
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Externally", "http://www.jhu.edu/", true);
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Internally", "http://www.jhu.edu/", false);

      items.add(jhuItem);

      for (Team team : Team.values()) {
         try {
            TeamDefinitionArtifact teamDef = DemoTeams.getInstance().getTeamDef(team);
            XNavigateItem teamItems = new XNavigateItemFolder(jhuItem, "JHU " + team.name().replaceAll("_", " "));
            new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + teamDef + " Actions",
                  Arrays.asList(DemoTeams.getInstance().getTeamDef(team)), false, true, true, null, null,
                  ReleasedOption.Both));
            new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + teamDef + " Workflows",
                  Arrays.asList(DemoTeams.getInstance().getTeamDef(team)), false, false, true, null, null,
                  ReleasedOption.Both));
            // Handle all children teams
            for (TeamDefinitionArtifact childTeamDef : Artifacts.getChildrenOfTypeSet(
                  DemoTeams.getInstance().getTeamDef(team), TeamDefinitionArtifact.class, true)) {
               new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Open " + childTeamDef + " Workflows",
                     Arrays.asList(childTeamDef), false, false, false, null, null, ReleasedOption.Both));
            }
            if (teamDef.isTeamUsesVersions()) {
               if (team.name().contains("SAW"))
                  new XNavigateUrlItem(teamItems, "Open SAW Website", "http://www.cisst.org/cisst/saw/", false);
               else if (team.name().contains("CIS")) new XNavigateUrlItem(teamItems, "Open CIS Website",
                     "http://www.cisst.org/cisst/cis/", false);

               new SearchNavigateItem(teamItems, new NextVersionSearchItem(teamDef, LoadView.WorldEditor));
               new SearchNavigateItem(teamItems, new VersionTargetedForTeamSearchItem(teamDef, null, false,
                     LoadView.WorldEditor));
               new SearchNavigateItem(teamItems, new TeamWorldSearchItem("Show Un-Released Team Workflows",
                     Arrays.asList(teamDef), true, false, true, null, null, ReleasedOption.UnReleased));
               new ReleaseVersionItem(teamItems, teamDef);
               new CreateNewVersionItem(teamItems, teamDef);
            }
         } catch (Exception ex) {
            OseeLog.log(OseeAtsConfigDemoActivator.class, Level.SEVERE, ex);
         }
      }

      XNavigateItem adminItems = new XNavigateItem(jhuItem, "JHU Admin", FrameworkImage.LASER);

      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Actions", "Actions"));
      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Decision Review", "Decision Review"));
      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all PeerToPeer Review", "PeerToPeer Review"));
      new SearchNavigateItem(adminItems, new ArtifactTypesSearchItem("Show all Team Workflows",
            TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()));
      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Tasks", "Task"));

      XNavigateItem healthItems = new XNavigateItem(adminItems, "Health", FrameworkImage.LASER);
      new ValidateAtsDatabase(healthItems);

      XNavigateItem demoItems = new XNavigateItem(adminItems, "Demo Data", FrameworkImage.ADMIN);
      new PopulateDemoActions(demoItems);

      return items;
   }
}
