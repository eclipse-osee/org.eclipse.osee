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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.health.ValidateChangeReportByHrid;
import org.eclipse.osee.ats.health.ValidateChangeReports;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.notify.AtsNotificationNavigateItem;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DoesNotWorkItemAts;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeWithInheritenceSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.MultipleHridSearchData;
import org.eclipse.osee.ats.world.search.MultipleHridSearchOperation;
import org.eclipse.osee.ats.world.search.MyFavoritesGoalsSearchItem;
import org.eclipse.osee.ats.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.world.search.MyGoalWorkflowItem;
import org.eclipse.osee.ats.world.search.MyGoalWorkflowItem.GoalSearchState;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.ats.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.ats.world.search.StateWorldSearchItem;
import org.eclipse.osee.ats.world.search.TaskSearchWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateContributionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateExtensionPointData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;
import org.eclipse.osee.framework.ui.skynet.results.example.ResultsEditorExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XResultDataExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XViewerExample;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUserGroups;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateViewItems extends XNavigateViewItems {
   private static AtsNavigateViewItems navigateItems = new AtsNavigateViewItems();
   private List<XNavigateItem> items;

   public AtsNavigateViewItems() {
      super();
   }

   public static AtsNavigateViewItems getInstance() {
      return navigateItems;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      if (items == null) {
         items = getItems();
      }
      return items;
   }

   private List<XNavigateItem> getItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (OseeUiActivator.areOSEEServicesAvailable().isFalse()) {
         return items;
      }

      try {
         User user = UserManager.getUser();

         items.add(new SearchNavigateItem(null, new MyWorldSearchItem("My World", user)));
         items.add(new SearchNavigateItem(null, new MyFavoritesSearchItem("My Favorites", user)));
         items.add(new SearchNavigateItem(null, new MySubscribedSearchItem("My Subscribed", user)));
         items.add(new SearchNavigateItem(null, new MyGoalWorkflowItem("My Goals", user, GoalSearchState.InWork)));
         items.add(new SearchNavigateItem(null, new MyReviewWorkflowItem("My Reviews", user, ReviewState.InWork)));
         items.add(new VisitedItems(null));
         items.add(new XNavigateItemAction(null, new NewAction(), AtsImage.NEW_ACTION));
         items.add(new SearchNavigateItem(null, new MyWorldSearchItem("User's World")));

         items.add(new SearchNavigateItem(null, new UserSearchWorkflowSearchItem()));
         items.add(new SearchNavigateItem(null, new TaskSearchWorldSearchItem()));
         items.add(new SearchNavigateItem(null, new GroupWorldSearchItem((Branch) null)));
         items.add(new SearchNavigateItem(null, new TeamWorkflowSearchWorkflowSearchItem()));
         items.add(new SearchNavigateItem(null, new UserCommunitySearchItem()));
         items.add(new SearchNavigateItem(null, new ActionableItemWorldSearchItem(null, "Actionable Item Search",
            false, false, false)));

         createGoalsSection(items);
         createVersionsSection(items);
         addExtensionPointItems(items);
         createReviewsSection(items);

         XNavigateItem stateItems = new XNavigateItem(null, "States", AtsImage.STATE);
         new SearchNavigateItem(stateItems, new StateWorldSearchItem());
         new SearchNavigateItem(stateItems, new StateWorldSearchItem("Search for Authorize Actions", "Authorize"));
         items.add(stateItems);

         // Search Items
         items.add(new XNavigateItemOperation(null, FrameworkImage.BRANCH_CHANGE, "Open Change Report(s) by ID(s)",
            new MultipleHridSearchOperationFactory("Open Change Report(s) by ID(s)", AtsEditor.ChangeReport)));
         items.add(new XNavigateItemOperation(null, AtsImage.OPEN_BY_ID, "Search by ID(s) - Open World Editor",
            new MultipleHridSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor)));
         items.add(new XNavigateItemOperation(null, AtsImage.WORKFLOW_CONFIG, "Search by ID(s) - Open Workflow Editor",
            new MultipleHridSearchOperationFactory("Search by ID(s) - Open Workflow Editor", AtsEditor.WorkflowEditor)));
         items.add(new XNavigateItemOperation(null, AtsImage.GLOBE, "Quick Search", new IOperationFactory() {

            @Override
            public IOperation createOperation() {
               EntryCheckDialog dialog =
                  new EntryCheckDialog("Search by Strings", "Enter search strings",
                     "Include Completed/Cancelled Workflows");
               if (dialog.open() == 0) {
                  return new AtsQuickSearchOperation(new AtsQuickSearchData("Search by Strings", dialog.getEntry(),
                     dialog.isChecked()));
               }
               return null;
            }
         }));

         items.add(new ArtifactImpactToActionSearchItem(null));

         XNavigateItem reportItems = new XNavigateItem(null, "Reports", AtsImage.REPORT);
         new FirstTimeQualityMetricReportItem(reportItems);
         new XNavigateItem(reportItems, "ATS World Reports - Input from Actions in ATS World", AtsImage.REPORT);
         new BarChartExample(reportItems);
         new ResultsEditorExample(reportItems);
         new CompareEditorExample(reportItems);
         new XViewerExample(reportItems);
         new XResultDataExample(reportItems);
         //      new ExtendedStatusReportItem(atsReportItems, "ATS World Extended Status Report");

         XNavigateItem emailItems = new XNavigateItem(null, "Email & Notifications", FrameworkImage.EMAIL);
         new EmailTeamsItem(emailItems, null, MemberType.Both);
         new EmailTeamsItem(emailItems, null, MemberType.Leads);
         new EmailTeamsItem(emailItems, null, MemberType.Members);
         new EmailUserGroups(emailItems);
         new SubscribeByActionableItem(emailItems);
         new SubscribeByTeamDefinition(emailItems);
         items.add(emailItems);

         items.add(reportItems);

         XNavigateItem utilItems = new XNavigateItem(null, "Util", FrameworkImage.GEAR);
         new ImportActionsViaSpreadsheet(utilItems);
         new XNavigateItemAction(utilItems, new CompareTwoStringsAction(), FrameworkImage.EDIT);
         new GenerateGuid(utilItems);
         new XNavigateItemOperation(utilItems, FrameworkImage.GEAR, MassEditDirtyArtifactOperation.NAME,
            new MassEditDirtyArtifactOperation());

         items.add(utilItems);

         BlamContributionManager.addBlamOperationsToNavigator(items);

         if (AtsUtil.isAtsAdmin()) {
            XNavigateItem adminItems = new XNavigateItem(null, "Admin", PluginUiImage.ADMIN);

            new AtsNotificationNavigateItem(adminItems);
            new AtsNotificationNavigateItem(adminItems, true);
            new UpdateAtsWorkItemDefinitions(adminItems);
            new DisplayCurrentOseeEventListeners(adminItems);
            new AtsRemoteEventTestItem(adminItems);

            new SearchNavigateItem(adminItems, new UserRelatedToAtsObjectSearch(
               "User's All Related Objects - Admin Only", null, false, LoadView.WorldEditor));
            new SearchNavigateItem(adminItems, new UserRelatedToAtsObjectSearch(
               "User's All Active Related Objects - Admin Only", null, true, LoadView.WorldEditor));

            new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action));
            new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Decision Review",
               AtsArtifactTypes.DecisionReview));
            new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all PeerToPeer Review",
               AtsArtifactTypes.PeerToPeerReview));
            new SearchNavigateItem(adminItems, new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows",
               AtsArtifactTypes.TeamWorkflow));
            new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task));
            new CreateGoalTestArtifacts(adminItems);

            new DoesNotWorkItemAts(adminItems);

            XNavigateItem healthItems = new XNavigateItemFolder(adminItems, "Health");
            new ValidateAtsDatabase(healthItems);
            new ValidateChangeReports(healthItems);
            new ValidateChangeReportByHrid(healthItems);

            // new ActionNavigateItem(adminItems, new XViewerViewAction());
            // new ActionNavigateItem(adminItems, new OpenEditorAction());
            // new CreateBugFixesItem(adminItems);

            XNavigateItem extra = new XNavigateItemFolder(adminItems, "Other");
            Set<XNavigateExtensionPointData> extraItems =
               XNavigateContributionManager.getNavigateItems(NavigateView.VIEW_ID);
            for (XNavigateExtensionPointData extraItem : extraItems) {
               for (XNavigateItem navigateItem : extraItem.getNavigateItems()) {
                  extra.addChild(navigateItem);
               }
            }

            items.add(adminItems);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return items;
      }

      return items;
   }

   public void createReviewsSection(List<XNavigateItem> items) {
      try {
         XNavigateItem reviewItem = new XNavigateItem(null, "Reviews", AtsImage.REVIEW);
         new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Open " + AtsArtifactTypes.DecisionReview.getName() + "s", AtsArtifactTypes.DecisionReview, false,
            false, AtsImage.REVIEW));
         new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Workflows Waiting " + AtsArtifactTypes.DecisionReview.getName() + "s",
            AtsArtifactTypes.DecisionReview, false, true, AtsImage.REVIEW));
         new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Open " + AtsArtifactTypes.PeerToPeerReview.getName() + "s", AtsArtifactTypes.PeerToPeerReview, false,
            false, AtsImage.REVIEW));
         new SearchNavigateItem(reviewItem, new ShowOpenWorkflowsByArtifactType(
            "Show Workflows Waiting " + AtsArtifactTypes.PeerToPeerReview.getName() + "s",
            AtsArtifactTypes.PeerToPeerReview, false, true, AtsImage.REVIEW));
         new NewPeerToPeerReviewItem(reviewItem);
         new GenerateReviewParticipationReport(reviewItem);
         items.add(reviewItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   public void createVersionsSection(List<XNavigateItem> items) {
      try {
         XNavigateItem releaseItems = new XNavigateItem(null, "Versions", FrameworkImage.VERSION);
         new MassEditTeamVersionItem("Team Versions", releaseItems, (TeamDefinitionArtifact) null,
            FrameworkImage.VERSION);
         new SearchNavigateItem(releaseItems, new VersionTargetedForTeamSearchItem(null, null, false,
            LoadView.WorldEditor));
         new SearchNavigateItem(releaseItems, new NextVersionSearchItem(null, LoadView.WorldEditor));
         new ReleaseVersionItem(releaseItems, null);
         new CreateNewVersionItem(releaseItems, null);
         new GenerateVersionReportItem(releaseItems);
         new GenerateFullVersionReportItem(releaseItems);
         items.add(releaseItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   public void createGoalsSection(List<XNavigateItem> items) {
      try {
         XNavigateItem goalItem = new XNavigateItem(null, "Goals", AtsImage.GOAL);
         new SearchNavigateItem(goalItem, new GoalSearchItem("InWork Goals", new ArrayList<TeamDefinitionArtifact>(),
            false, null));
         new SearchNavigateItem(goalItem, new GoalSearchWorkflowSearchItem());
         new SearchNavigateItem(goalItem, new MyFavoritesGoalsSearchItem("Favorites", UserManager.getUser()));
         items.add(goalItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   public void addExtensionPointItems(List<XNavigateItem> items) {
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsNavigateItem");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsNavigateItem extension point");
         return;
      }
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
               OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error loading AtsNavigateItem extension", ex);
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

   private static final class MultipleHridSearchOperationFactory implements IOperationFactory {

      private final AtsEditor atsEditor;
      private final String operationName;

      public MultipleHridSearchOperationFactory(String operationName, AtsEditor atsEditor) {
         this.operationName = operationName;
         this.atsEditor = atsEditor;
      }

      @Override
      public IOperation createOperation() {
         return new MultipleHridSearchOperation(new MultipleHridSearchData(operationName, atsEditor));
      }
   }
}
