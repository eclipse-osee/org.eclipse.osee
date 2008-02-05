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

package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.health.ActionsHaveOneTeam;
import org.eclipse.osee.ats.health.AssignedActiveActions;
import org.eclipse.osee.ats.health.AttributeDuplication;
import org.eclipse.osee.ats.health.DuplicateUsersItem;
import org.eclipse.osee.ats.health.InvalidEstimatedHoursAttribute;
import org.eclipse.osee.ats.health.OrphanedTasks;
import org.eclipse.osee.ats.health.TeamWorkflowsHaveZeroOrOneVersion;
import org.eclipse.osee.ats.health.UnAssignedAssignedAtsObjects;
import org.eclipse.osee.ats.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.report.ExtendedStatusReportItem;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.AtsAttributeSearchItem;
import org.eclipse.osee.ats.world.search.CriteriaSearchItem;
import org.eclipse.osee.ats.world.search.EditTasksByTeamVersionSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.ats.world.search.MyCompletedSearchItem;
import org.eclipse.osee.ats.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.world.search.MyOrigSearchItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.world.search.MyTaskSearchItem;
import org.eclipse.osee.ats.world.search.MyTeamWFSearchItem;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.ats.world.search.StateWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamVersionWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UnReleasedTeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.BlamOperations;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateViewItems extends XNavigateViewItems {
   private static AtsNavigateViewItems navigateItems = new AtsNavigateViewItems();
   private SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();

   public AtsNavigateViewItems() {
      super();
   }

   public static AtsNavigateViewItems getInstance() {
      return navigateItems;
   }

   public WorldSearchItem getMyWorldSearchItem() {
      return new MyWorldSearchItem("My World", skynetAuth.getAuthenticatedUser());
   }

   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (!ConnectionHandler.isConnected()) return items;

      items.add(new XNavigateItemAction(null, new NewAction()));

      LinkedList<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(ActionArtifact.ARTIFACT_NAME, Operator.EQUAL));
      items.add(new SearchNavigateItem(null, new MyWorldSearchItem("My World", skynetAuth.getAuthenticatedUser())));
      items.add(new SearchNavigateItem(null, new MyFavoritesSearchItem("My Favorites",
            skynetAuth.getAuthenticatedUser())));
      items.add(new SearchNavigateItem(null, new MyReviewWorkflowItem("My Reviews", skynetAuth.getAuthenticatedUser())));

      items.add(new VisitedItems(null));

      XNavigateItem otherItems = new XNavigateItem(null, "Other My Searches");
      new SearchNavigateItem(otherItems, new MyTeamWFSearchItem("My Team Workflows", skynetAuth.getAuthenticatedUser()));
      new SearchNavigateItem(otherItems, new MyTaskSearchItem("My Task (WorldView)", skynetAuth.getAuthenticatedUser(),
            LoadView.WorldView));
      new SearchNavigateItem(otherItems, new MyTaskSearchItem("My Task (Editor)", skynetAuth.getAuthenticatedUser(),
            LoadView.TaskEditor));
      new SearchNavigateItem(otherItems, new MySubscribedSearchItem("My Subscribed", skynetAuth.getAuthenticatedUser()));
      new SearchNavigateItem(otherItems, new MyOrigSearchItem("My Originator - InWork",
            skynetAuth.getAuthenticatedUser(), true));
      new SearchNavigateItem(otherItems, new MyOrigSearchItem("My Originator - All", skynetAuth.getAuthenticatedUser(),
            false));
      new SearchNavigateItem(otherItems, new MyCompletedSearchItem("My Completed", skynetAuth.getAuthenticatedUser()));
      items.add(otherItems);

      otherItems = new XNavigateItem(null, "Other User Searches");
      new SearchNavigateItem(otherItems, new MyWorldSearchItem("User's World"));
      new SearchNavigateItem(otherItems, new MyOrigSearchItem("User's Originator - InWork", null, true));
      new SearchNavigateItem(otherItems, new MyOrigSearchItem("User's Originator - All", null, false));
      new SearchNavigateItem(otherItems, new MyTeamWFSearchItem("User's Team Workflows"));
      new SearchNavigateItem(otherItems, new MyTaskSearchItem("User's Tasks (WorldView)", LoadView.WorldView));
      new SearchNavigateItem(otherItems, new MyTaskSearchItem("User's Tasks (Editor)", LoadView.TaskEditor));
      new SearchNavigateItem(otherItems, new MyCompletedSearchItem("User's Completed"));
      new SearchNavigateItem(otherItems, new MyFavoritesSearchItem("User's Favorites"));
      new SearchNavigateItem(otherItems, new MySubscribedSearchItem("User's Subscribed"));
      new SearchNavigateItem(otherItems, new MyReviewWorkflowItem("User's Reviews"));
      items.add(otherItems);

      items.add(new SearchNavigateItem(null, new GroupWorldSearchItem()));
      items.add(new SearchNavigateItem(null, new UserCommunitySearchItem()));

      XNavigateItem aiTeam = new XNavigateItem(null, "Actionable Items");
      new SearchNavigateItem(aiTeam, new ActionableItemWorldSearchItem("Actionable Item Actions", (String[]) null,
            false, true, false));
      items.add(aiTeam);

      XNavigateItem teamItem = new XNavigateItem(null, "Teams");
      new SearchNavigateItem(teamItem, new TeamWorldSearchItem("Team Actions", (String[]) null, false, true, false,
            null));
      new SearchNavigateItem(teamItem, new TeamVersionWorldSearchItem("Team Actions by Version", (String[]) null,
            false, false, false, null));
      new SearchNavigateItem(teamItem, new UnReleasedTeamWorldSearchItem("Un-Released Team Actions", (String[]) null,
            true, true, false));
      new MassEditTeamVersionItem("Show Team Versions", teamItem, "");
      items.add(teamItem);

      XNavigateItem taskItem = new XNavigateItem(null, "Tasks");
      new SearchNavigateItem(taskItem, new EditTasksByTeamVersionSearchItem(null, true));
      new EditTasksBySelectedWorkflows(taskItem);
      new EditTasksByGroup(taskItem);
      new SearchNavigateItem(taskItem, new MyTaskSearchItem("Edit Tasks by User", LoadView.TaskEditor));
      items.add(taskItem);

      XNavigateItem releaseItems = new XNavigateItem(null, "Versions");
      new MassEditTeamVersionItem("Edit Versions", releaseItems, (TeamDefinitionArtifact) null);
      new SearchNavigateItem(releaseItems, new VersionTargetedForTeamSearchItem(null, null, false));
      new SearchNavigateItem(releaseItems, new NextVersionSearchItem(null));
      new ReleaseVersionItem(releaseItems, null);
      new CreateNewVersionItem(releaseItems, null);
      new GenerateVersionReportItem(releaseItems);
      new GenerateFullVersionReportItem(releaseItems);
      items.add(releaseItems);

      addExtensionPointItems(items);

      XNavigateItem reviewItem = new XNavigateItem(null, "Reviews");
      new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Open " + DecisionReviewArtifact.ARTIFACT_NAME + "s",
            Arrays.asList(new String[] {DecisionReviewArtifact.ARTIFACT_NAME}), false, false));
      new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Workflows Waiting " + DecisionReviewArtifact.ARTIFACT_NAME + "s",
            Arrays.asList(new String[] {DecisionReviewArtifact.ARTIFACT_NAME}), false, true));
      new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Open " + PeerToPeerReviewArtifact.ARTIFACT_NAME + "s",
            Arrays.asList(new String[] {PeerToPeerReviewArtifact.ARTIFACT_NAME}), false, false));
      new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Workflows Waiting " + PeerToPeerReviewArtifact.ARTIFACT_NAME + "s",
            Arrays.asList(new String[] {PeerToPeerReviewArtifact.ARTIFACT_NAME}), false, true));
      new NewPeerToPeerReviewItem(reviewItem);
      items.add(reviewItem);

      XNavigateItem stateItems = new XNavigateItem(null, "States");
      new SearchNavigateItem(stateItems, new StateWorldSearchItem());
      new SearchNavigateItem(stateItems, new StateWorldSearchItem("Search for Authorize Actions", "Authorize"));
      items.add(stateItems);

      // Search Items
      items.add(new SearchNavigateItem(null, new MultipleHridSearchItem()));
      items.add(new SearchNavigateItem(null, new AtsAttributeSearchItem()));
      items.add(new SearchNavigateItem(null, new AtsAttributeSearchItem("Search ATS Titles", "Name", null)));
      items.add(new ArtifactImpactToActionSearchItem(null));

      XNavigateItem reportItems = new XNavigateItem(null, "Reports");
      XNavigateItem atsReportItems =
            new XNavigateItem(reportItems, "ATS World Reports - Input from Actions in ATS World");
      new ExtendedStatusReportItem(atsReportItems, "ATS World Extended Status Report");

      XNavigateItem emailItems = new XNavigateItem(null, "Email");
      new EmailTeamsItem(emailItems, null, MemberType.Both);
      new EmailTeamsItem(emailItems, null, MemberType.Leads);
      new EmailTeamsItem(emailItems, null, MemberType.Members);
      items.add(emailItems);

      items.add(reportItems);

      XNavigateItem importItems = new XNavigateItem(null, "Import");
      new ImportActionsViaSpreadsheet(importItems);
      items.add(importItems);

      XNavigateItem blamOperationItems = new XNavigateItem(null, "Blam Operations");
      for (BlamOperation blamOperation : BlamOperations.getBlamOperations()) {
         new XNavigateItemBlam(blamOperationItems, blamOperation);
      }
      items.add(blamOperationItems);

      if (AtsPlugin.isAtsAdmin()) {
         XNavigateItem adminItems = new XNavigateItem(null, "Admin");

         criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch(ActionArtifact.ARTIFACT_NAME, Operator.EQUAL));
         new SearchNavigateItem(adminItems, new CriteriaSearchItem("Admin - Actions", criteria, true));

         criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch(DecisionReviewArtifact.ARTIFACT_NAME, Operator.EQUAL));
         new SearchNavigateItem(adminItems, new CriteriaSearchItem("Admin - Decision Review", criteria, true));

         criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch(PeerToPeerReviewArtifact.ARTIFACT_NAME, Operator.EQUAL));
         new SearchNavigateItem(adminItems, new CriteriaSearchItem("Admin - PeerToPeer Review", criteria, true));

         criteria = new LinkedList<ISearchPrimitive>();
         for (String teamArtifactName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
            criteria.add(new ArtifactTypeSearch(teamArtifactName, Operator.EQUAL));
         new SearchNavigateItem(adminItems, new CriteriaSearchItem("Admin - Teams", criteria, false));

         criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch(TaskArtifact.ARTIFACT_NAME, Operator.EQUAL));
         new SearchNavigateItem(adminItems, new CriteriaSearchItem("Admin - Tasks", criteria, true));

         XNavigateItem healthItems = new XNavigateItem(adminItems, "Health");
         new AttributeDuplication(healthItems);
         new OrphanedTasks(healthItems);
         new InvalidEstimatedHoursAttribute(healthItems);
         new ActionsHaveOneTeam(healthItems);
         new AssignedActiveActions(healthItems);
         new DuplicateUsersItem(healthItems);
         new TeamWorkflowsHaveZeroOrOneVersion(healthItems);
         new UnAssignedAssignedAtsObjects(healthItems);

         // new ActionNavigateItem(adminItems, new XViewerViewAction());
         // new ActionNavigateItem(adminItems, new OpenEditorAction());
         // new CreateBugFixesItem(adminItems);

         items.add(adminItems);
      }

      return items;
   }

   @SuppressWarnings("deprecation")
   public void addExtensionPointItems(List<XNavigateItem> items) {
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsNavigateItem");
      if (point == null) OSEELog.logSevere(AtsPlugin.class, "Can't access AtsNavigateItem extension point", true);
      IExtension[] extensions = point.getExtensions();
      Map<String, XNavigateItem> nameToNavItem = new HashMap<String, XNavigateItem>();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsNavigateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Object obj = bundle.loadClass(classname).newInstance();
               IAtsNavigateItem task = (IAtsNavigateItem) obj;
               for (XNavigateItem navItem : task.getNavigateItems()) {
                  nameToNavItem.put(navItem.getName(), navItem);
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, "Error loading AtsNavigateItem extension", ex, false);
            }
         }
      }
      // Put in alpha order
      String[] names = nameToNavItem.keySet().toArray(new String[nameToNavItem.size()]);
      Arrays.sort(names);
      for (String name : names) {
         items.add(nameToNavItem.get(name));
      }
   }

}
