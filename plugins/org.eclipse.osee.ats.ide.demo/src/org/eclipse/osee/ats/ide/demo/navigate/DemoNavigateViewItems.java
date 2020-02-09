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
package org.eclipse.osee.ats.ide.demo.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.config.ValidateAtsConfiguration;
import org.eclipse.osee.ats.ide.config.version.CreateNewVersionItem;
import org.eclipse.osee.ats.ide.config.version.ReleaseVersionItem;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
import org.eclipse.osee.ats.ide.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.ide.navigate.IAtsNavigateItem;
import org.eclipse.osee.ats.ide.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.ide.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.ide.world.search.ArtifactTypeWithInheritenceSearchItem;
import org.eclipse.osee.ats.ide.world.search.ILazyTeamDefinitionProvider;
import org.eclipse.osee.ats.ide.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.ide.world.search.OpenWorkflowsByTeamDefSearchItem;
import org.eclipse.osee.ats.ide.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * Provides the ATS Navigator items for the sample XYZ company's teams
 *
 * @author Donald G. Dunne
 */
public class DemoNavigateViewItems implements IAtsNavigateItem {

   public DemoNavigateViewItems() {
      super();
   }

   private static IAtsTeamDefinition getTeamDef(ArtifactToken team) {
      IAtsTeamDefinition results = null;
      // Add check to keep exception from occurring for OSEE developers running against production
      if (!ClientSessionManager.isProductionDataStore()) {
         try {
            results = AtsClientService.get().getTeamDefinitionService().getTeamDefinitionById(team);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return results;
   }

   @Override
   public List<XNavigateItem> getNavigateItems(XNavigateItem parentItem) {
      ArtifactToken[] teams = new ArtifactToken[] {
         DemoArtifactToken.Process_Team,
         DemoArtifactToken.Tools_Team,
         DemoArtifactToken.SAW_HW,
         DemoArtifactToken.SAW_Code,
         DemoArtifactToken.SAW_Test,
         DemoArtifactToken.SAW_SW_Design,
         DemoArtifactToken.SAW_Requirements,
         DemoArtifactToken.SAW_SW,
         DemoArtifactToken.CIS_SW,
         DemoArtifactToken.CIS_Code,
         DemoArtifactToken.CIS_Test,
         DemoArtifactToken.Facilities_Team};

      List<XNavigateItem> items = new ArrayList<>();

      if (DbConnectionUtility.areOSEEServicesAvailable().isFalse()) {
         return items;
      }

      // If Demo Teams not configured, ignore these navigate items
      try {
         if (getTeamDef(DemoArtifactToken.Process_Team) == null) {
            return items;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.INFO, "Demo Teams Not Cofigured", ex);
         return items;
      }
      XNavigateItem jhuItem = new XNavigateItemFolder(parentItem, "John Hopkins Univ (JHU)");
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Externally", "http://www.jhu.edu/", true);
      new XNavigateUrlItem(jhuItem, "Open JHU Website - Internally", "http://www.jhu.edu/", false);

      items.add(jhuItem);

      for (ArtifactToken team : teams) {
         try {
            IAtsTeamDefinition teamDef = getTeamDef(team);
            XNavigateItem teamItems = new XNavigateItemFolder(jhuItem, "JHU " + team.getName().replaceAll("_", " "));
            new SearchNavigateItem(teamItems, new OpenWorkflowsByTeamDefSearchItem(
               "Show Open " + teamDef + " Workflows", new SimpleTeamDefinitionProvider(Arrays.asList(teamDef))));
            // Handle all children teams
            for (IAtsTeamDefinition childTeamDef : AtsClientService.get().getTeamDefinitionService().getChildren(
               teamDef, true)) {
               new SearchNavigateItem(teamItems,
                  new OpenWorkflowsByTeamDefSearchItem("Show Open " + childTeamDef + " Workflows",
                     new SimpleTeamDefinitionProvider(Arrays.asList(childTeamDef))));
            }
            if (AtsClientService.get().getVersionService().isTeamUsesVersions(teamDef)) {
               if (team.getName().contains("SAW")) {
                  new XNavigateUrlItem(teamItems, "Open SAW Website", "http://www.cisst.org/cisst/saw/", false);
               } else if (team.getName().contains("CIS")) {
                  new XNavigateUrlItem(teamItems, "Open CIS Website", "http://www.cisst.org/cisst/cis/", false);
               }

               new SearchNavigateItem(teamItems, new NextVersionSearchItem(teamDef, LoadView.WorldEditor));
               new SearchNavigateItem(teamItems,
                  new VersionTargetedForTeamSearchItem(teamDef, null, false, LoadView.WorldEditor));
               new SearchNavigateItem(teamItems, new OpenWorkflowsByTeamDefSearchItem("Show Un-Released Team Workflows",
                  new SimpleTeamDefinitionProvider(Arrays.asList(teamDef)), true, ReleasedOption.UnReleased));
               new ReleaseVersionItem(teamItems, teamDef);
               new CreateNewVersionItem(teamItems, teamDef);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      XNavigateItem adminItems = new XNavigateItem(jhuItem, "JHU Admin", FrameworkImage.LASER);

      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action));
      new SearchNavigateItem(adminItems,
         new ArtifactTypeSearchItem("Show all Decision Review", AtsArtifactTypes.DecisionReview));
      new SearchNavigateItem(adminItems,
         new ArtifactTypeSearchItem("Show all PeerToPeer Review", AtsArtifactTypes.PeerToPeerReview));
      new SearchNavigateItem(adminItems,
         new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows", AtsArtifactTypes.TeamWorkflow));
      new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task));

      XNavigateItem healthItems = new XNavigateItem(adminItems, "Health", FrameworkImage.LASER);
      new ValidateAtsDatabase(healthItems);
      new ValidateAtsConfiguration(healthItems);
      new CreateGoalTestDemoArtifacts(healthItems);

      return items;
   }

   private class SimpleTeamDefinitionProvider implements ILazyTeamDefinitionProvider {

      private final Collection<IAtsTeamDefinition> teamDefs;

      public SimpleTeamDefinitionProvider(Collection<IAtsTeamDefinition> teamDefs) {
         this.teamDefs = teamDefs;
      }

      @Override
      public Collection<IAtsTeamDefinition> getTeamDefs() {
         return teamDefs;
      }

   }

}
