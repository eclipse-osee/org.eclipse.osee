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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.agile.AgileUtil;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.NewAction;
import org.eclipse.osee.ats.ide.actions.NewGoal;
import org.eclipse.osee.ats.ide.actions.OpenArtifactEditorById;
import org.eclipse.osee.ats.ide.actions.OpenOrphanedTasks;
import org.eclipse.osee.ats.ide.actions.RevertDuplicateAtsTransitionByIdAction;
import org.eclipse.osee.ats.ide.actions.RevertDuplicateAtsTransitionsAction;
import org.eclipse.osee.ats.ide.agile.navigate.CreateNewAgileBacklog;
import org.eclipse.osee.ats.ide.agile.navigate.CreateNewAgileFeatureGroup;
import org.eclipse.osee.ats.ide.agile.navigate.CreateNewAgileSprint;
import org.eclipse.osee.ats.ide.agile.navigate.CreateNewAgileTeam;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileBacklog;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprint;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprintReports;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileStoredSprintReports;
import org.eclipse.osee.ats.ide.config.AtsConfig2ExampleNavigateItem;
import org.eclipse.osee.ats.ide.config.ValidateAtsConfiguration;
import org.eclipse.osee.ats.ide.config.editor.AtsConfigResultsEditorNavigateItem;
import org.eclipse.osee.ats.ide.config.version.CreateNewVersionItem;
import org.eclipse.osee.ats.ide.config.version.GenerateFullVersionReportItem;
import org.eclipse.osee.ats.ide.config.version.GenerateVersionReportItem;
import org.eclipse.osee.ats.ide.config.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.ide.config.version.ParallelConfigurationView;
import org.eclipse.osee.ats.ide.config.version.ReleaseVersionItem;
import org.eclipse.osee.ats.ide.ev.EvNavigateItems;
import org.eclipse.osee.ats.ide.export.AtsExportAction;
import org.eclipse.osee.ats.ide.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.internal.OseePerspective;
import org.eclipse.osee.ats.ide.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.ide.notify.EmailActionsBlam;
import org.eclipse.osee.ats.ide.operation.ConvertWorkflowStatesBlam;
import org.eclipse.osee.ats.ide.operation.MoveTeamWorkflowsBlam;
import org.eclipse.osee.ats.ide.search.AtsQuickSearchOperationFactory;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.util.CleanupOseeSystemAssignedWorkflows;
import org.eclipse.osee.ats.ide.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.ats.ide.util.Import.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.ide.util.Import.ImportAgileActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.ide.workdef.ValidateWorkDefinitionNavigateItem;
import org.eclipse.osee.ats.ide.workdef.editor.WorkDefinitionViewer;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.ide.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.ide.world.search.ArtifactTypeWithInheritenceSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchGoalSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTaskSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.ats.ide.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.ide.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.ide.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.ide.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.ide.world.search.SearchTeamWorkflowsByProgramSearchItem;
import org.eclipse.osee.ats.ide.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.OpenPerspectiveNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateContributionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateExtensionPointData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.PurgeTransactionAction;
import org.eclipse.osee.framework.ui.skynet.change.OpenChangeReportByTransactionIdAction;
import org.eclipse.osee.framework.ui.skynet.results.example.ResultsEditorExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XResultDataExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XViewerExample;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUserGroups;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.osgi.framework.Bundle;

/**
 * Main Navigate View for OSEE
 *
 * @author Donald G. Dunne
 */
public final class NavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<>();
   private boolean ensurePopulatedRanOnce = false;
   public static long ATS_SEARCH_NAVIGATE_VIEW_ITEM = 324265264L;

   private final static NavigateViewItems instance = new NavigateViewItems();

   public static NavigateViewItems getInstance() {
      return instance;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      ensurePopulated();
      return items;
   }

   @Override
   public void addUtilItems(XNavigateItem utilItems) {
      try {
         new ToggleAtsAdmin(utilItems);
         new XNavigateItemBlam(utilItems, new ImportActionsViaSpreadsheetBlam());
         new XNavigateItemBlam(utilItems, new ImportAgileActionsViaSpreadsheetBlam());
         new XNavigateItemAction(utilItems, new AtsExportAction(), FrameworkImage.EXPORT_DATA);
         new GenerateIdsAndArtId(utilItems);
         new CommaDelimitLines(utilItems);
         new ClearAtsConfigCache(utilItems);
         new XNavigateItemBlam(utilItems, new MoveTeamWorkflowsBlam(), AtsImage.TEAM_WORKFLOW);
         new AtsConfigResultsEditorNavigateItem(utilItems);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private synchronized void ensurePopulated() {
      if (!ensurePopulatedRanOnce) {
         if (DbConnectionUtility.areOSEEServicesAvailable().isFalse()) {
            return;
         }
         this.ensurePopulatedRanOnce = true;

         addAtsSectionChildren(null);
         XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
      }
   }

   public void addAtsSectionChildren(XNavigateItem item) {
      try {

         items.add(new SearchNavigateItem(item, new MyWorldSearchItem("My World", null)));
         items.add(new RecentlyVisitedNavigateItems(item));
         items.add(new SearchNavigateItem(item, new AtsSearchWorkflowSearchItem()));

         createMySearchesSection(item, items);

         createAdvancedSearchesSection(item, items, null);

         createOpenViewsSection(item, items);
         items.add(new XNavigateItemAction(item, new NewAction(), AtsImage.NEW_ACTION));

         if (AgileUtil.isAgileUser(AtsClientService.get())) {
            createAgileSection(item, items);
         }
         if (AgileUtil.isEarnedValueUser(AtsClientService.get())) {
            EvNavigateItems.createSection(item, items);
         }

         addExtensionPointItems(item, items);

         createVersionsSection(item, items);

         createEmailItems(item, items);

         createReportItems(item, items);

         createGoalItems(item, items);

         createAdminItems(item, items);

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createAdvancedSearchesSection(XNavigateItem parent, List<XNavigateItem> items, AtsUser user) {
      XNavigateItem advancedSearchesItems = new XNavigateItemFolder(parent, "Advanced Searches");
      new SearchNavigateItem(advancedSearchesItems, new MyFavoritesSearchItem("My Favorites", null));
      new SearchNavigateItem(advancedSearchesItems, new MySubscribedSearchItem("My Subscribed", null));
      new SearchNavigateItem(advancedSearchesItems, new AtsSearchTeamWorkflowSearchItem());
      new SearchNavigateItem(advancedSearchesItems, new AtsSearchTaskSearchItem());
      new SearchNavigateItem(advancedSearchesItems, new MyWorldSearchItem("User's World"));
      new ArtifactImpactToActionSearchItem(advancedSearchesItems);
      new SearchNavigateItem(advancedSearchesItems, new AtsSearchGoalSearchItem());

      new SearchNavigateItem(advancedSearchesItems,
         new SearchTeamWorkflowsByProgramSearchItem("Search Team Workflows by Program", null, false));

      // Search Items
      new XNavigateItemOperation(advancedSearchesItems, FrameworkImage.BRANCH_CHANGE, "Open Change Report(s) by ID(s)",
         new MultipleIdSearchOperationFactory("Open Change Report(s) by ID(s)", AtsEditor.ChangeReport));
      new XNavigateItemOperation(advancedSearchesItems, AtsImage.OPEN_BY_ID, "Search by ID(s) - Open World Editor",
         new MultipleIdSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor));
      new XNavigateItemOperation(advancedSearchesItems, AtsImage.OPEN_BY_ID,
         "Search by ID(s) - Multi-Line - Open World Editor",
         new MultipleIdMultiLineSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor));
      new XNavigateItemOperation(advancedSearchesItems, AtsImage.WORKFLOW_CONFIG,
         "Search by ID(s) - Open Workflow Editor",
         new MultipleIdSearchOperationFactory("Search by ID(s) - Open Workflow Editor", AtsEditor.WorkflowEditor));
      new XNavigateItemOperation(advancedSearchesItems, AtsImage.GLOBE, "Action Quick Search",
         new AtsQuickSearchOperationFactory());
      items.add(advancedSearchesItems);
   }

   private void createOpenViewsSection(XNavigateItem parent, List<XNavigateItem> items) {
      XNavigateItem openViewsItems = new XNavigateItemFolder(parent, "Open Views");
      new XNavigateItemAction(openViewsItems, new OpenArtifactExplorerViewAction(), FrameworkImage.ARTIFACT_EXPLORER);
      new XNavigateItemAction(openViewsItems, new OpenArtifactQuickSearchViewAction(), FrameworkImage.ARTIFACT_SEARCH);
      new XNavigateItemAction(openViewsItems, new OpenBranchManagerAction(), FrameworkImage.BRANCH);
      items.add(openViewsItems);
   }

   private void createAdminItems(XNavigateItem parent, List<XNavigateItem> items) {
      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         createWorkDefinitionsSection(parent, items);
         createExampleItems(parent, items);
         XNavigateItem adminItems = new XNavigateItem(parent, "Admin", PluginUiImage.ADMIN);

         XNavigateItem dbConvertItems = new XNavigateItem(adminItems, "Database Conversions", PluginUiImage.ADMIN);
         new XNavigateItemBlam(dbConvertItems, new ConvertWorkflowStatesBlam());

         new DisplayCurrentOseeEventListeners(adminItems);

         XNavigateItem demoItems = new XNavigateItemFolder(adminItems, "Demo");
         new AtsRemoteEventTestItem(demoItems);

         new SearchNavigateItem(demoItems, new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action));
         new SearchNavigateItem(demoItems,
            new ArtifactTypeSearchItem("Show all Decision Review", AtsArtifactTypes.DecisionReview));
         new SearchNavigateItem(demoItems,
            new ArtifactTypeSearchItem("Show all PeerToPeer Review", AtsArtifactTypes.PeerToPeerReview));
         new SearchNavigateItem(demoItems,
            new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows", AtsArtifactTypes.TeamWorkflow));
         new SearchNavigateItem(demoItems, new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task));
         new CreateActionUsingAllActionableItems(demoItems);

         new AtsConfig2ExampleNavigateItem(demoItems);
         new XNavigateItemAction(adminItems, new OpenChangeReportByTransactionIdAction(), FrameworkImage.BRANCH_CHANGE);
         new XNavigateItemAction(adminItems, new OpenArtifactEditorById(), FrameworkImage.ARTIFACT_EDITOR);
         new XNavigateItemAction(adminItems, new PurgeTransactionAction(), FrameworkImage.PURGE);
         XNavigateItem healthItems = new XNavigateItemFolder(adminItems, "Health");
         new ValidateAtsDatabase(healthItems);
         new ValidateAtsConfiguration(healthItems);
         new CleanupOseeSystemAssignedWorkflows(healthItems);
         new XNavigateItemAction(healthItems, new OpenOrphanedTasks(), AtsImage.TASK);
         new XNavigateItemAction(adminItems, new RevertDuplicateAtsTransitionByIdAction(), AtsImage.TASK);
         new XNavigateItemAction(adminItems, new RevertDuplicateAtsTransitionsAction(), AtsImage.TASK);

         Set<XNavigateExtensionPointData> extraItems =
            XNavigateContributionManager.getNavigateItems(NavigateView.VIEW_ID);

         if (!extraItems.isEmpty()) {
            XNavigateItem extra = new XNavigateItemFolder(adminItems, "Other");
            for (XNavigateExtensionPointData extraItem : extraItems) {
               for (XNavigateItem navigateItem : extraItem.getNavigateItems()) {
                  extra.addChild(navigateItem);
               }
            }
         }

         items.add(adminItems);
      }
   }

   private void createEmailItems(XNavigateItem parent, List<XNavigateItem> items) {
      XNavigateItem emailItems = new XNavigateItem(parent, "Email & Notifications", FrameworkImage.EMAIL);
      new EmailTeamsItem(emailItems, null, MemberType.Both);
      new EmailTeamsItem(emailItems, null, MemberType.Leads);
      new EmailTeamsItem(emailItems, null, MemberType.Members);
      new EmailUserGroups(emailItems);
      new SubscribeByActionableItem(emailItems);
      new SubscribeByTeamDefinition(emailItems);
      new XNavigateItemBlam(emailItems, new EmailActionsBlam(), FrameworkImage.EMAIL);
      items.add(emailItems);
   }

   private void createReportItems(XNavigateItem parent, List<XNavigateItem> items) {
      XNavigateItem reportItems = new XNavigateItem(parent, "Reports", AtsImage.REPORT);
      new FirstTimeQualityMetricReportItem(reportItems);
   }

   private void createGoalItems(XNavigateItem parent, List<XNavigateItem> items) {
      XNavigateItem goalItems = new XNavigateItem(parent, "Goals", AtsImage.REPORT);
      items.add(new XNavigateItemAction(goalItems, new NewGoal(), AtsImage.GOAL));
   }

   private void createExampleItems(XNavigateItem parent, List<XNavigateItem> items) {
      if (UserGroupService.getOseeAdmin().isCurrentUserMember() || AtsClientService.get().getUserGroupService().isInUserGroup(
         CoreUserGroups.Everyone)) {
         XNavigateItem exampleItems = new XNavigateItem(parent, "Examples", AtsImage.REPORT);

         new ResultsEditorExample(exampleItems);
         new CompareEditorExample(exampleItems);
         new XViewerExample(exampleItems);
         new XResultDataExample(exampleItems);
         new FilteredTreeDialogExample(exampleItems);
         new FilteredTreeDialogSingleExample(exampleItems);
         new FilteredTreeArtifactDialogExample(exampleItems);
         new FilteredCheckboxTreeDialogExample(exampleItems);
         new FilteredCheckboxTreeArtifactDialogExample(exampleItems);
         new FilteredCheckboxTreeDialogSelectAllExample(exampleItems);
         items.add(exampleItems);
      }
   }

   private void createWorkDefinitionsSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem workDefItems = new XNavigateItem(parent, "Work Definition", FrameworkImage.VERSION);
         new WorkDefinitionViewer(workDefItems);
         new ValidateWorkDefinitionNavigateItem(workDefItems);
         items.add(workDefItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   private void createVersionsSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem releaseItems = new XNavigateItem(parent, "Versions", FrameworkImage.VERSION);
         new ParallelConfigurationView(releaseItems);
         new SearchNavigateItem(releaseItems,
            new VersionTargetedForTeamSearchItem(null, null, false, LoadView.WorldEditor));
         new SearchNavigateItem(releaseItems, new NextVersionSearchItem(null, LoadView.WorldEditor));
         new GenerateVersionReportItem(releaseItems);
         new GenerateFullVersionReportItem(releaseItems);

         // Admin
         if (AtsClientService.get().getUserService().isAtsAdmin()) {
            new MassEditTeamVersionItem("Team Versions (Admin)", releaseItems, FrameworkImage.VERSION);
            new CreateNewVersionItem(releaseItems, null);
            new ReleaseVersionItem(releaseItems, null);
         }

         items.add(releaseItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   private void createAgileSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem agileItems = new XNavigateItem(parent, "Agile", FrameworkImage.VERSION);
         new OpenAgileBacklog(agileItems);
         new OpenAgileSprint(agileItems);

         XNavigateItem agileReports = new XNavigateItem(agileItems, "Reports", AtsImage.REPORT);
         new OpenAgileSprintReports(agileReports);
         new OpenAgileStoredSprintReports(agileReports);

         XNavigateItem agileConfigs = new XNavigateItem(agileItems, "Configuration", FrameworkImage.GEAR);
         new CreateNewAgileTeam(agileConfigs);
         new CreateNewAgileFeatureGroup(agileConfigs);
         new CreateNewAgileSprint(agileConfigs);
         new CreateNewAgileBacklog(agileConfigs);

         XNavigateItem conversionItems = new XNavigateItem(agileItems, "Conversions", FrameworkImage.VERSION);
         new ConvertVersionToAgileSprint(conversionItems);

         items.add(agileItems);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Agile section");
      }
   }

   private void createMySearchesSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem searches = new XNavigateItem(parent, "Saved Action Searches", AtsImage.SEARCH);
         searches.setId(ATS_SEARCH_NAVIGATE_VIEW_ITEM);
         populateSavedSearchesItem(searches);
         items.add(searches);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create section");
      }
   }

   private static void populateSavedSearchesItem(XNavigateItem searches) {
      for (IAtsWorldEditorItem worldEditorItem : AtsWorldEditorItems.getItems()) {
         for (AtsSearchWorkflowSearchItem item : worldEditorItem.getSearchWorkflowSearchItems()) {
            ArrayList<AtsSearchData> savedSearches = AtsClientService.get().getQueryService().getSavedSearches(
               AtsClientService.get().getUserService().getCurrentUser(), item.getNamespace());
            for (AtsSearchData data : savedSearches) {
               AtsSearchWorkflowSearchItem searchItem = item.copy();
               searchItem.setSavedData(data);
               SearchNavigateItem navItem = new SearchNavigateItem(searches, searchItem);
               navItem.setName(item.getShortNamePrefix() + ": " + data.getSearchName());
            }
         }
      }
   }

   private void addExtensionPointItems(XNavigateItem parentItem, List<XNavigateItem> items) {
      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.ide.AtsNavigateItem");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access AtsNavigateItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      Map<String, XNavigateItem> nameToNavItem = new TreeMap<>();
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
               if (task != null) {
                  for (XNavigateItem navItem : task.getNavigateItems(parentItem)) {
                     nameToNavItem.put(navItem.getName(), navItem);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error loading AtsNavigateItem extension", ex);
            }
         }
      }
      items.addAll(nameToNavItem.values());
   }

   private static final class MultipleIdSearchOperationFactory implements IOperationFactory {

      private final AtsEditor atsEditor;
      private final String operationName;

      public MultipleIdSearchOperationFactory(String operationName, AtsEditor atsEditor) {
         this.operationName = operationName;
         this.atsEditor = atsEditor;
      }

      @Override
      public IOperation createOperation() {
         return new MultipleIdSearchOperation(new MultipleIdSearchData(operationName, atsEditor));
      }
   }

   private static final class MultipleIdMultiLineSearchOperationFactory implements IOperationFactory {

      private final AtsEditor atsEditor;
      private final String operationName;

      public MultipleIdMultiLineSearchOperationFactory(String operationName, AtsEditor atsEditor) {
         this.operationName = operationName;
         this.atsEditor = atsEditor;
      }

      @Override
      public IOperation createOperation() {
         MultipleIdSearchOperation op =
            new MultipleIdSearchOperation(new MultipleIdSearchData(operationName, atsEditor));
         op.setMultiLine(true);
         return op;
      }
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      XNavigateItem reviewItem = new XNavigateItem(null, "OSEE ATS", AtsImage.ACTION);
      new OpenPerspectiveNavigateItem(reviewItem, "ATS", OseePerspective.ID, AtsImage.ACTION);
      addAtsSectionChildren(reviewItem);
      items.add(reviewItem);
   }

   @Override
   public String getSectionId() {
      return "ATS";
   }

   public void clearCaches() {
      ensurePopulatedRanOnce = false;
      items.clear();
   }

   public static void refreshTopAtsSearchItem() {
      XNavigateItem searchesItem = NavigateView.getNavigateView().getItem(ATS_SEARCH_NAVIGATE_VIEW_ITEM, true);
      if (searchesItem != null) {
         searchesItem.getChildren().clear();
         populateSavedSearchesItem(searchesItem);
         if (NavigateView.getNavigateView() != null && NavigateView.isAccessible()) {
            NavigateView.getNavigateView().refresh(searchesItem);
         }
      }
   }

}
