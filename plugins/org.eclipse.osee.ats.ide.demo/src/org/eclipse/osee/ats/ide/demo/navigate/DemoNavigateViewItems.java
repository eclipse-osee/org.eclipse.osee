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

package org.eclipse.osee.ats.ide.demo.navigate;

import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.MID_TOP;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.PROG;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.SUBCAT;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.config.AtsConfig2ExampleNavigateItem;
import org.eclipse.osee.ats.ide.config.version.CreateNewVersionItem;
import org.eclipse.osee.ats.ide.config.version.ReleaseVersionItem;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.ide.navigate.ToggleAtsAdmin;
import org.eclipse.osee.ats.ide.util.CreateActionUsingAllActionableItems;
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
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * Provides the ATS Navigator items for the sample XYZ company's teams
 *
 * @author Donald G. Dunne
 */
public class DemoNavigateViewItems implements XNavigateItemProvider {

   private static final XNavItemCat JOHN_HOPKINS_UNIV_JHU = new XNavItemCat("John Hopkins Univ (JHU)");
   private static final XNavItemCat JHU_ADMIN = new XNavItemCat(JOHN_HOPKINS_UNIV_JHU + ".Admin");
   private static final XNavItemCat JHU_HEALTH = new XNavItemCat(JOHN_HOPKINS_UNIV_JHU + ".Health");

   @Override
   public boolean isApplicable() {
      return getTeamDef(DemoArtifactToken.Process_Team) != null;
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {

      if (DemoUtil.isDbPopulatedWithDemoData().isTrue()) {

         items.add(new XNavigateItemFolder(XNavigateItem.DEMO.getName(), AtsImage.DEMO, MID_TOP));
         items.add(new XNavigateItemFolder(JOHN_HOPKINS_UNIV_JHU.getName(), JOHN_HOPKINS_UNIV_JHU, PROG));

         items.add(new ToggleAtsAdmin());

         addTeamItems(items);

         items.add(
            new SearchNavigateItem(new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action), JHU_ADMIN));
         items.add(new SearchNavigateItem(
            new ArtifactTypeSearchItem("Show all Decision Review", AtsArtifactTypes.DecisionReview), JHU_ADMIN));
         items.add(new SearchNavigateItem(
            new ArtifactTypeSearchItem("Show all PeerToPeer Review", AtsArtifactTypes.PeerToPeerReview), JHU_ADMIN));
         items.add(new SearchNavigateItem(
            new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows", AtsArtifactTypes.TeamWorkflow),
            JHU_ADMIN, XNavItemCat.OSEE_ADMIN));
         items.add(
            new SearchNavigateItem(new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task), JHU_ADMIN));

         items.add(new CreateGoalTestDemoArtifacts(JHU_HEALTH));
         items.add(new CreateActionUsingAllActionableItems(XNavigateItem.DEMO));
         items.add(new AtsConfig2ExampleNavigateItem());
      }

      return items;
   }

   private void addTeamItems(List<XNavigateItem> items) {
      for (ArtifactToken team : teams) {
         String teamCatName = getTeamCategoryName(team);
         XNavItemCat teamCat = new XNavItemCat(teamCatName);
         items.add(new XNavigateItemFolder(team.getName(), teamCat, SUBCAT));
      }
      for (ArtifactToken prog : Arrays.asList(DemoArtifactToken.SAW_SW, DemoArtifactToken.CIS_SW)) {
         List<ArtifactToken> progTeams = null;
         if (prog.equals(DemoArtifactToken.SAW_SW)) {
            progTeams = swTeams;
         } else {
            progTeams = cisTeams;
         }
         for (ArtifactToken team : progTeams) {
            String teamCatName = getProgTeamCategoryName(prog, team);
            XNavItemCat teamCat = new XNavItemCat(teamCatName);
            items.add(new XNavigateItemFolder(team.getName(), teamCat, SUBCAT));
         }
      }

      items.add(new XNavigateItemFolder("JHU Admin", FrameworkImage.LASER, JHU_ADMIN, SUBCAT));
      items.add(new XNavigateItemFolder("Health", FrameworkImage.HEALTH, JHU_HEALTH, SUBCAT));

      items.add(
         new XNavigateUrlItem("Open JHU Website - Externally", "http://www.jhu.edu/", true, JOHN_HOPKINS_UNIV_JHU));
      items.add(
         new XNavigateUrlItem("Open JHU Website - Internally", "http://www.jhu.edu/", false, JOHN_HOPKINS_UNIV_JHU));

      for (ArtifactToken team : teams) {
         try {
            IAtsTeamDefinition teamDef = getTeamDef(team);
            String teamCatName = getTeamCategoryName(team);
            XNavItemCat teamCat = new XNavItemCat(teamCatName);

            if (teamDef != null) {
               items.add(
                  new SearchNavigateItem(new OpenWorkflowsByTeamDefSearchItem("Show Open " + teamDef + " Workflows",
                     new SimpleTeamDefinitionProvider(Arrays.asList(teamDef))), teamCat));

               if (AtsApiService.get().getVersionService().isTeamUsesVersions(teamDef)) {
                  if (team.getName().contains("SAW")) {
                     items.add(
                        new XNavigateUrlItem("Open SAW Website", "http://www.cisst.org/cisst/saw/", false, teamCat));
                  } else if (team.getName().contains("CIS")) {
                     items.add(
                        new XNavigateUrlItem("Open CIS Website", "http://www.cisst.org/cisst/cis/", false, teamCat));
                  }

                  items.add(new SearchNavigateItem(new NextVersionSearchItem(teamDef, LoadView.WorldEditor), teamCat));
                  items.add(new SearchNavigateItem(
                     new VersionTargetedForTeamSearchItem(teamDef, null, false, LoadView.WorldEditor), teamCat));
                  items.add(new SearchNavigateItem(
                     new OpenWorkflowsByTeamDefSearchItem("Show Un-Released Team Workflows",
                        new SimpleTeamDefinitionProvider(Arrays.asList(teamDef)), true, ReleasedOption.UnReleased),
                     teamCat));
                  items.add(new ReleaseVersionItem(teamDef, teamCat));
                  items.add(new CreateNewVersionItem(teamDef, teamCat));
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      // Handle programs
      for (ArtifactToken prog : Arrays.asList(DemoArtifactToken.SAW_SW, DemoArtifactToken.CIS_SW)) {
         List<ArtifactToken> progTeams = null;
         if (prog.equals(DemoArtifactToken.SAW_SW)) {
            progTeams = swTeams;
         } else {
            progTeams = cisTeams;
         }
         for (ArtifactToken team : progTeams) {
            try {
               IAtsTeamDefinition teamDef = getTeamDef(team);
               String teamCatName = getProgTeamCategoryName(prog, team);
               XNavItemCat teamCat = new XNavItemCat(teamCatName);

               items.add(
                  new SearchNavigateItem(new OpenWorkflowsByTeamDefSearchItem("Show Open " + teamDef + " Workflows",
                     new SimpleTeamDefinitionProvider(Arrays.asList(teamDef))), teamCat));

               if (teamDef != null) {
                  if (AtsApiService.get().getVersionService().isTeamUsesVersions(teamDef)) {
                     items.add(new SearchNavigateItem(
                        new OpenWorkflowsByTeamDefSearchItem("Show Un-Released Team Workflows",
                           new SimpleTeamDefinitionProvider(Arrays.asList(teamDef)), true, ReleasedOption.UnReleased),
                        teamCat));
                     items.add(
                        new SearchNavigateItem(new NextVersionSearchItem(teamDef, LoadView.WorldEditor), teamCat));
                     items.add(new SearchNavigateItem(
                        new VersionTargetedForTeamSearchItem(teamDef, null, false, LoadView.WorldEditor), teamCat));
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private String getTeamCategoryName(ArtifactToken team) {
      return JOHN_HOPKINS_UNIV_JHU + "." + team.getName().replaceAll("_", " ");
   }

   private String getProgTeamCategoryName(ArtifactToken prog, ArtifactToken team) {
      return JOHN_HOPKINS_UNIV_JHU + "." + prog.getName().replaceAll("_", "") + "." + team.getName().replaceAll("_",
         " ");
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

   private final List<ArtifactToken> teams = Arrays.asList(DemoArtifactToken.Process_Team, DemoArtifactToken.Tools_Team,
      DemoArtifactToken.SAW_SW, DemoArtifactToken.CIS_SW);

   private final List<ArtifactToken> swTeams = Arrays.asList(DemoArtifactToken.SAW_HW, DemoArtifactToken.SAW_Code,
      DemoArtifactToken.SAW_Test, DemoArtifactToken.SAW_SW_Design, DemoArtifactToken.SAW_Requirements);

   private final List<ArtifactToken> cisTeams =
      Arrays.asList(DemoArtifactToken.CIS_Code, DemoArtifactToken.CIS_Test, DemoArtifactToken.Facilities_Team);

   private static IAtsTeamDefinition getTeamDef(ArtifactToken team) {
      IAtsTeamDefinition results = null;
      // Add check to keep exception from occurring for OSEE developers running against production
      if (!ClientSessionManager.isProductionDataStore()) {
         results = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(team);
      }
      return results;
   }

}
