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
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.actions.NewGoal;
import org.eclipse.osee.ats.actions.OpenArtifactEditorById;
import org.eclipse.osee.ats.actions.OpenOrphanedTasks;
import org.eclipse.osee.ats.actions.RevertDuplicateTransitionByIdAction;
import org.eclipse.osee.ats.actions.RevertDuplicateTransitionsAction;
import org.eclipse.osee.ats.agile.navigate.CreateNewAgileBacklog;
import org.eclipse.osee.ats.agile.navigate.CreateNewAgileFeatureGroup;
import org.eclipse.osee.ats.agile.navigate.CreateNewAgileSprint;
import org.eclipse.osee.ats.agile.navigate.CreateNewAgileTeam;
import org.eclipse.osee.ats.agile.navigate.OpenAgileBacklog;
import org.eclipse.osee.ats.agile.navigate.OpenAgileSprint;
import org.eclipse.osee.ats.agile.navigate.OpenAgileSprintReports;
import org.eclipse.osee.ats.agile.navigate.OpenAgileStoredSprintReports;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.config.AtsConfig2ExampleNavigateItem;
import org.eclipse.osee.ats.config.ValidateAtsConfiguration;
import org.eclipse.osee.ats.config.editor.AtsConfigResultsEditorNavigateItem;
import org.eclipse.osee.ats.ev.EvNavigateItems;
import org.eclipse.osee.ats.export.AtsExportAction;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.internal.OseePerspective;
import org.eclipse.osee.ats.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.notify.EmailActionsBlam;
import org.eclipse.osee.ats.operation.ConvertWorkflowStatesBlam;
import org.eclipse.osee.ats.operation.MoveTeamWorkflowsBlam;
import org.eclipse.osee.ats.search.AtsQuickSearchOperationFactory;
import org.eclipse.osee.ats.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.util.CleanupOseeSystemAssignedWorkflows;
import org.eclipse.osee.ats.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.ats.util.DoesNotWorkItemAts;
import org.eclipse.osee.ats.util.Import.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.util.Import.ImportAgileActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.version.CreateNewVersionItem;
import org.eclipse.osee.ats.version.GenerateFullVersionReportItem;
import org.eclipse.osee.ats.version.GenerateVersionReportItem;
import org.eclipse.osee.ats.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.version.ReleaseVersionItem;
import org.eclipse.osee.ats.workdef.config.ImportAIsAndTeamDefinitionsItem;
import org.eclipse.osee.ats.workdef.config.ImportWorkDefinitionsItem;
import org.eclipse.osee.ats.workdef.config.ValidateWorkspaceToDatabaseWorkDefinitions;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeWithInheritenceSearchItem;
import org.eclipse.osee.ats.world.search.AtsSearchGoalSearchItem;
import org.eclipse.osee.ats.world.search.AtsSearchTaskSearchItem;
import org.eclipse.osee.ats.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.ats.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.ats.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoArtifactIdListsAction;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.action.PurgeTransactionAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;
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
         IAtsUser user = AtsClientService.get().getUserService().getCurrentUser();
         SearchNavigateItem searchItem = new SearchNavigateItem(item, new AtsSearchWorkflowSearchItem());
         items.add(searchItem);

         createMySearchesSection(item, items);

         items.add(new SearchNavigateItem(item, new MyWorldSearchItem("My World", user)));
         items.add(new SearchNavigateItem(item, new MyFavoritesSearchItem("My Favorites", user)));
         items.add(new SearchNavigateItem(item, new MySubscribedSearchItem("My Subscribed", user)));
         items.add(
            new XNavigateItemAction(item, new OpenArtifactExplorerViewAction(), FrameworkImage.ARTIFACT_EXPLORER));
         items.add(
            new XNavigateItemAction(item, new OpenArtifactQuickSearchViewAction(), FrameworkImage.ARTIFACT_SEARCH));
         items.add(new XNavigateItemAction(item, new OpenBranchManagerAction(), FrameworkImage.BRANCH));
         items.add(new VisitedItems(item));
         items.add(new XNavigateItemAction(item, new NewAction(), AtsImage.NEW_ACTION));
         items.add(new SearchNavigateItem(item, new MyWorldSearchItem("User's World")));

         items.add(new SearchNavigateItem(item, new AtsSearchTaskSearchItem()));
         items.add(new SearchNavigateItem(item, new AtsSearchTeamWorkflowSearchItem()));

         createVersionsSection(item, items);
         createAgileSection(item, items);
         EvNavigateItems.createSection(item, items);
         addExtensionPointItems(item, items);

         // Search Items
         items.add(new XNavigateItemOperation(item, FrameworkImage.BRANCH_CHANGE, "Open Change Report(s) by ID(s)",
            new MultipleIdSearchOperationFactory("Open Change Report(s) by ID(s)", AtsEditor.ChangeReport)));
         items.add(new XNavigateItemOperation(item, AtsImage.OPEN_BY_ID, "Search by ID(s) - Open World Editor",
            new MultipleIdSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor)));
         items.add(new XNavigateItemOperation(item, AtsImage.OPEN_BY_ID,
            "Search by ID(s) - Multi-Line - Open World Editor", new MultipleIdMultiLineSearchOperationFactory(
               "Search by ID(s) - Open World Editor", AtsEditor.WorldEditor)));
         items.add(new XNavigateItemOperation(item, AtsImage.WORKFLOW_CONFIG, "Search by ID(s) - Open Workflow Editor",
            new MultipleIdSearchOperationFactory("Search by ID(s) - Open Workflow Editor", AtsEditor.WorkflowEditor)));
         items.add(new XNavigateItemOperation(item, AtsImage.GLOBE, "Action Quick Search",
            new AtsQuickSearchOperationFactory()));

         items.add(new ArtifactImpactToActionSearchItem(null));

         createEmailItems(item, items);

         createReportItems(item, items);

         createGoalItems(item, items);

         createExampleItems(item, items);

         createUtilItems(item, items);

         createAdminItems(item, items);

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createAdminItems(XNavigateItem parent, List<XNavigateItem> items) throws OseeArgumentException {
      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         XNavigateItem adminItems = new XNavigateItem(parent, "Admin", PluginUiImage.ADMIN);

         XNavigateItem dbConvertItems = new XNavigateItem(adminItems, "Database Conversions", PluginUiImage.ADMIN);
         new ImportAIsAndTeamDefinitionsItem(dbConvertItems);
         new ImportWorkDefinitionsItem(dbConvertItems);
         new XNavigateItemBlam(dbConvertItems, new ConvertWorkflowStatesBlam());

         new DisplayCurrentOseeEventListeners(adminItems);
         new AtsRemoteEventTestItem(adminItems);

         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action));
         new SearchNavigateItem(adminItems,
            new ArtifactTypeSearchItem("Show all Decision Review", AtsArtifactTypes.DecisionReview));
         new SearchNavigateItem(adminItems,
            new ArtifactTypeSearchItem("Show all PeerToPeer Review", AtsArtifactTypes.PeerToPeerReview));
         new SearchNavigateItem(adminItems,
            new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows", AtsArtifactTypes.TeamWorkflow));
         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task));
         new CreateActionUsingAllActionableItems(adminItems);

         new AtsConfig2ExampleNavigateItem(adminItems);
         new DoesNotWorkItemAts(adminItems);
         new XNavigateItemAction(adminItems, new OpenChangeReportByTransactionIdAction(), FrameworkImage.BRANCH_CHANGE);
         new XNavigateItemAction(adminItems, new OpenArtifactEditorById(), FrameworkImage.ARTIFACT_EDITOR);
         new XNavigateItemAction(adminItems, new PurgeTransactionAction(), FrameworkImage.PURGE);

         XNavigateItem healthItems = new XNavigateItemFolder(adminItems, "Health");
         new ValidateAtsDatabase(healthItems);
         new ValidateAtsConfiguration(healthItems);
         new ValidateWorkspaceToDatabaseWorkDefinitions(healthItems);
         new CleanupOseeSystemAssignedWorkflows(healthItems);
         new XNavigateItemAction(adminItems, new OpenOrphanedTasks(), AtsImage.TASK);
         new XNavigateItemAction(adminItems, new RevertDuplicateTransitionByIdAction(), AtsImage.TASK);
         new XNavigateItemAction(adminItems, new RevertDuplicateTransitionsAction(), AtsImage.TASK);

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
   }

   private void createUtilItems(XNavigateItem parent, List<XNavigateItem> items) {
      XNavigateItem utilItems = new XNavigateItem(parent, "Util", FrameworkImage.GEAR);
      new ToggleAtsAdmin(utilItems);
      new XNavigateItemBlam(utilItems, new ImportActionsViaSpreadsheetBlam());
      new XNavigateItemBlam(utilItems, new ImportAgileActionsViaSpreadsheetBlam());
      new XNavigateItemAction(utilItems, new CompareTwoStringsAction(), FrameworkImage.EDIT);
      new XNavigateItemAction(utilItems, new CompareTwoArtifactIdListsAction(), FrameworkImage.EDIT);
      new XNavigateItemAction(utilItems, new AtsExportAction(), FrameworkImage.EXPORT_DATA);
      new GenerateGuidIdArtId(utilItems);
      new XNavigateItemOperation(utilItems, FrameworkImage.ARTIFACT_MASS_EDITOR, MassEditDirtyArtifactOperation.NAME,
         new MassEditDirtyArtifactOperation());
      new ClearAtsConfigCache(utilItems);
      new XNavigateItemBlam(utilItems, new MoveTeamWorkflowsBlam(), AtsImage.TEAM_WORKFLOW);
      new AtsConfigResultsEditorNavigateItem(utilItems);

      items.add(utilItems);
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
      items.add(new SearchNavigateItem(goalItems, new AtsSearchGoalSearchItem()));
      items.add(new XNavigateItemAction(goalItems, new NewGoal(), AtsImage.GOAL));
   }

   private void createExampleItems(XNavigateItem parent, List<XNavigateItem> items) {
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

   private void createVersionsSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem releaseItems = new XNavigateItem(parent, "Versions", FrameworkImage.VERSION);
         new MassEditTeamVersionItem("Team Versions", releaseItems, FrameworkImage.VERSION);
         new SearchNavigateItem(releaseItems,
            new VersionTargetedForTeamSearchItem(null, null, false, LoadView.WorldEditor));
         new SearchNavigateItem(releaseItems, new NextVersionSearchItem(null, LoadView.WorldEditor));
         new ReleaseVersionItem(releaseItems, null);
         new CreateNewVersionItem(releaseItems, null);
         new GenerateVersionReportItem(releaseItems);
         new GenerateFullVersionReportItem(releaseItems);
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
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsNavigateItem");
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
         NavigateView.getNavigateView().refresh(searchesItem);
      }
   }

}
