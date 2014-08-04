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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.config.AtsConfig2ExampleNavigateItem;
import org.eclipse.osee.ats.config.ValidateAtsConfiguration;
import org.eclipse.osee.ats.config.editor.AtsConfigResultsEditorNavigateItem;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.ev.EvNavigateItems;
import org.eclipse.osee.ats.goal.GoalSearchWorkflowSearchItem;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.ats.internal.ATSPerspective;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.notify.EmailActionsBlam;
import org.eclipse.osee.ats.operation.ConvertWorkflowStatesBlam;
import org.eclipse.osee.ats.operation.MoveTeamWorkflowsBlam;
import org.eclipse.osee.ats.search.AtsQuickSearchOperationFactory;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.util.ConvertAIsAndTeamDefinitions;
import org.eclipse.osee.ats.util.CreateActionUsingAllActionableItems;
import org.eclipse.osee.ats.util.DoesNotWorkItemAts;
import org.eclipse.osee.ats.util.Import.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.version.CreateNewVersionItem;
import org.eclipse.osee.ats.version.GenerateFullVersionReportItem;
import org.eclipse.osee.ats.version.GenerateVersionReportItem;
import org.eclipse.osee.ats.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.version.ReleaseVersionItem;
import org.eclipse.osee.ats.workdef.config.ImportAIsAndTeamDefinitionsItem;
import org.eclipse.osee.ats.workdef.config.ImportWorkDefinitionsItem;
import org.eclipse.osee.ats.workdef.config.ValidateWorkspaceToDatabaseWorkDefinitions;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeSearchItem;
import org.eclipse.osee.ats.world.search.ArtifactTypeWithInheritenceSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.ats.world.search.MyFavoritesGoalsSearchItem;
import org.eclipse.osee.ats.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.world.search.MyGoalWorkflowItem;
import org.eclipse.osee.ats.world.search.MyGoalWorkflowItem.GoalSearchState;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem;
import org.eclipse.osee.ats.world.search.MyReviewWorkflowItem.ReviewState;
import org.eclipse.osee.ats.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.TaskSearchWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.model.Branch;
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
 * @author Donald G. Dunne
 */
public final class AtsNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<XNavigateItem>();
   private boolean ensurePopulatedRanOnce = false;

   private final static AtsNavigateViewItems instance = new AtsNavigateViewItems();

   public static AtsNavigateViewItems getInstance() {
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

         items.add(new SearchNavigateItem(item, new MyWorldSearchItem("My World", user)));
         items.add(new SearchNavigateItem(item, new MyFavoritesSearchItem("My Favorites", user)));
         items.add(new SearchNavigateItem(item, new MySubscribedSearchItem("My Subscribed", user)));
         items.add(new SearchNavigateItem(item, new MyGoalWorkflowItem("My Goals", user, GoalSearchState.InWork)));
         items.add(new SearchNavigateItem(item, new MyReviewWorkflowItem("My Reviews", user, ReviewState.InWork)));
         items.add(new VisitedItems(item));
         items.add(new XNavigateItemAction(item, new NewAction(), AtsImage.NEW_ACTION));
         items.add(new XNavigateItemAction(item, new NewGoal(), AtsImage.GOAL));
         items.add(new SearchNavigateItem(item, new MyWorldSearchItem("User's World")));

         items.add(new SearchNavigateItem(item, new UserSearchWorkflowSearchItem()));
         items.add(new SearchNavigateItem(item, new TaskSearchWorldSearchItem()));
         items.add(new SearchNavigateItem(item, new GroupWorldSearchItem((Branch) null)));
         items.add(new SearchNavigateItem(item, new TeamWorkflowSearchWorkflowSearchItem()));
         items.add(new SearchNavigateItem(item, new UserCommunitySearchItem()));
         items.add(new SearchNavigateItem(item, new ActionableItemWorldSearchItem("Actionable Item Search", null,
            false, false, false)));

         createGoalsSection(item, items);
         createVersionsSection(item, items);
         EvNavigateItems.createSection(item, items);
         addExtensionPointItems(item, items);

         // Search Items
         items.add(new XNavigateItemOperation(item, FrameworkImage.BRANCH_CHANGE, "Open Change Report(s) by ID(s)",
            new MultipleIdSearchOperationFactory("Open Change Report(s) by ID(s)", AtsEditor.ChangeReport)));
         items.add(new XNavigateItemOperation(item, AtsImage.OPEN_BY_ID, "Search by ID(s) - Open World Editor",
            new MultipleIdSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor)));
         items.add(new XNavigateItemOperation(item, AtsImage.WORKFLOW_CONFIG, "Search by ID(s) - Open Workflow Editor",
            new MultipleIdSearchOperationFactory("Search by ID(s) - Open Workflow Editor", AtsEditor.WorkflowEditor)));
         items.add(new XNavigateItemOperation(item, AtsImage.GLOBE, "Quick Search",
            new AtsQuickSearchOperationFactory()));

         items.add(new ArtifactImpactToActionSearchItem(null));

         createEmailItems(item, items);

         createReportItems(item, items);

         createUtilItems(item, items);

         createAdminItems(item, items);

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createAdminItems(XNavigateItem parent, List<XNavigateItem> items) throws OseeCoreException, OseeArgumentException {
      if (AtsUtilClient.isAtsAdmin()) {
         XNavigateItem adminItems = new XNavigateItem(parent, "Admin", PluginUiImage.ADMIN);

         XNavigateItem dbConvertItems = new XNavigateItem(adminItems, "Database Conversions", PluginUiImage.ADMIN);
         new ConvertAIsAndTeamDefinitions(dbConvertItems);
         new ImportAIsAndTeamDefinitionsItem(dbConvertItems);
         new ImportWorkDefinitionsItem(dbConvertItems);
         new XNavigateItemBlam(dbConvertItems, new ConvertWorkflowStatesBlam());

         new DisplayCurrentOseeEventListeners(adminItems);
         new AtsRemoteEventTestItem(adminItems);

         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Actions", AtsArtifactTypes.Action));
         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Decision Review",
            AtsArtifactTypes.DecisionReview));
         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all PeerToPeer Review",
            AtsArtifactTypes.PeerToPeerReview));
         new SearchNavigateItem(adminItems, new ArtifactTypeWithInheritenceSearchItem("Show all Team Workflows",
            AtsArtifactTypes.TeamWorkflow));
         new SearchNavigateItem(adminItems, new ArtifactTypeSearchItem("Show all Tasks", AtsArtifactTypes.Task));
         new CreateActionUsingAllActionableItems(adminItems);

         new AtsConfig2ExampleNavigateItem(adminItems);
         new DoesNotWorkItemAts(adminItems);
         new XNavigateItemAction(adminItems, new OpenChangeReportByTransactionIdAction(), FrameworkImage.BRANCH_CHANGE);
         new XNavigateItemAction(adminItems, new PurgeTransactionAction(), FrameworkImage.PURGE);

         XNavigateItem healthItems = new XNavigateItemFolder(adminItems, "Health");
         new ValidateAtsDatabase(healthItems);
         new ValidateAtsConfiguration(healthItems);
         new ValidateWorkspaceToDatabaseWorkDefinitions(healthItems);

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
      new XNavigateItemAction(utilItems, new CompareTwoStringsAction(), FrameworkImage.EDIT);
      new GenerateGuid(utilItems);
      new XNavigateItemOperation(utilItems, FrameworkImage.ARTIFACT_MASS_EDITOR, MassEditDirtyArtifactOperation.NAME,
         new MassEditDirtyArtifactOperation());
      new ClearAtsWorkDefinitionCache(utilItems);
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
      new BarChartExample(reportItems);
      new ResultsEditorExample(reportItems);
      new CompareEditorExample(reportItems);
      new XViewerExample(reportItems);
      new XResultDataExample(reportItems);
      //      new ExtendedStatusReportItem(atsReportItems, "ATS World Extended Status Report");
      items.add(reportItems);

   }

   private void createVersionsSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem releaseItems = new XNavigateItem(parent, "Versions", FrameworkImage.VERSION);
         new MassEditTeamVersionItem("Team Versions", releaseItems, (IAtsTeamDefinition) null, FrameworkImage.VERSION);
         new SearchNavigateItem(releaseItems, new VersionTargetedForTeamSearchItem(null, null, false,
            LoadView.WorldEditor));
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

   private void createGoalsSection(XNavigateItem parent, List<XNavigateItem> items) {
      try {
         XNavigateItem goalItem = new XNavigateItem(parent, "Goals", AtsImage.GOAL);
         new SearchNavigateItem(goalItem, new GoalSearchItem("InWork Goals", new ArrayList<IAtsTeamDefinition>(),
            false, null));
         new SearchNavigateItem(goalItem, new GoalSearchWorkflowSearchItem());
         new SearchNavigateItem(goalItem, new MyFavoritesGoalsSearchItem("Favorites",
            AtsClientService.get().getUserService().getCurrentUser()));
         items.add(goalItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Goals section");
      }
   }

   private void addExtensionPointItems(XNavigateItem parentItem, List<XNavigateItem> items) {
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsNavigateItem");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access AtsNavigateItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      Map<String, XNavigateItem> nameToNavItem = new TreeMap<String, XNavigateItem>();
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
               for (XNavigateItem navItem : task.getNavigateItems(parentItem)) {
                  nameToNavItem.put(navItem.getName(), navItem);
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

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      XNavigateItem reviewItem = new XNavigateItem(null, "OSEE ATS", AtsImage.ACTION);
      new OpenPerspectiveNavigateItem(reviewItem, "ATS", ATSPerspective.ID, AtsImage.ACTION);
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
}
