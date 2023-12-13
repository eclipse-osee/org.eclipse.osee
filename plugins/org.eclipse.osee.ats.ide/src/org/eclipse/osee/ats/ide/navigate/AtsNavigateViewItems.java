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

package org.eclipse.osee.ats.ide.navigate;

import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.BOT;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.MID_BOT;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.MID_TOP;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.OSEE_ADMIN;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.SUBCAT;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.TOP;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.DEFINE;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.EMAIL_NOTIFICATIONS;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.OTE;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.PLE;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.REPORTS;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.TOP_ADMIN;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.USER_MANAGEMENT;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.USER_MANAGEMENT_ADMIN;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.UTILITY;
import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem.UTILITY_EXAMPLES;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.agile.AgileUtil;
import org.eclipse.osee.ats.ide.actions.CreateEnumeratedArtifactAction;
import org.eclipse.osee.ats.ide.actions.EditEnumeratedArtifact;
import org.eclipse.osee.ats.ide.actions.NewGoal;
import org.eclipse.osee.ats.ide.actions.OpenArtifactEditorById;
import org.eclipse.osee.ats.ide.actions.OpenOrphanedTasks;
import org.eclipse.osee.ats.ide.actions.OpenWorkflowByIdAction;
import org.eclipse.osee.ats.ide.actions.RevertDuplicateAtsTransitionByIdAction;
import org.eclipse.osee.ats.ide.actions.RevertDuplicateAtsTransitionsAction;
import org.eclipse.osee.ats.ide.actions.ValidatePeerDefectsAction;
import org.eclipse.osee.ats.ide.branch.CreateAtsBaselineBranchBlam;
import org.eclipse.osee.ats.ide.column.ToggleXViewerColumnLoadingDebug;
import org.eclipse.osee.ats.ide.config.editor.AtsConfigResultsEditorNavigateItem;
import org.eclipse.osee.ats.ide.config.version.CreateNewVersionItem;
import org.eclipse.osee.ats.ide.config.version.GenerateFullVersionReportItem;
import org.eclipse.osee.ats.ide.config.version.GenerateVersionReportItem;
import org.eclipse.osee.ats.ide.config.version.MassEditTeamVersionItem;
import org.eclipse.osee.ats.ide.config.version.ParallelConfigurationView;
import org.eclipse.osee.ats.ide.config.version.ReleaseVersionItem;
import org.eclipse.osee.ats.ide.config.wizard.CreateAtsConfiguration;
import org.eclipse.osee.ats.ide.ev.OpenWorkPackageByIdAction;
import org.eclipse.osee.ats.ide.ev.WorkPackageConfigReport;
import org.eclipse.osee.ats.ide.ev.WorkPackageQBDReport;
import org.eclipse.osee.ats.ide.export.AtsExportAction;
import org.eclipse.osee.ats.ide.health.AtsHealthCheckNavigateItem;
import org.eclipse.osee.ats.ide.health.OseeProductionTestsNavItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.EmailTeamsItem.MemberType;
import org.eclipse.osee.ats.ide.notify.EmailActionsBlam;
import org.eclipse.osee.ats.ide.operation.ConvertWorkflowStatesBlam;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowAdvSearchItem;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.ide.search.navigate.SavedActionSearchNavigateItem;
import org.eclipse.osee.ats.ide.search.quick.AtsQuickSearchOperationFactory;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.util.CleanupOseeSystemAssignedWorkflows;
import org.eclipse.osee.ats.ide.workdef.ValidateWorkDefinitionNavigateItem;
import org.eclipse.osee.ats.ide.workdef.editor.WorkDefinitionViewer;
import org.eclipse.osee.ats.ide.workflow.review.GenerateReviewParticipationReport;
import org.eclipse.osee.ats.ide.workflow.review.NewPeerToPeerReviewItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchGoalSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchReviewSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTaskSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchWorkPackageSearchItem;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.ats.ide.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.ats.ide.world.search.MyReviewSearchItem;
import org.eclipse.osee.ats.ide.world.search.MySubscribedSearchItem;
import org.eclipse.osee.ats.ide.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.ide.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.ide.world.search.SearchReleaseArtifacts;
import org.eclipse.osee.ats.ide.world.search.SearchTeamWorkflowsByProgramSearchItem;
import org.eclipse.osee.ats.ide.world.search.ShowOpenWorkflowsByReviewType;
import org.eclipse.osee.ats.ide.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.ide.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorkingCompletePeerReviewReportSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.action.PurgeTransactionAction;
import org.eclipse.osee.framework.ui.skynet.action.XWidgetsDialogExampleAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.change.OpenChangeReportByTransactionIdAction;
import org.eclipse.osee.framework.ui.skynet.results.example.ResultsEditorExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XResultDataDialogExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XResultDataExample;
import org.eclipse.osee.framework.ui.skynet.results.example.XViewerExample;
import org.eclipse.osee.framework.ui.skynet.user.OpenUsersInMassEditor;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUserGroups;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * Main ATS Navigate View items for OSEE Navigator. Other XNavigateItems can be provided through similar providers via
 * extension points.
 *
 * @author Donald G. Dunne
 */
public final class AtsNavigateViewItems implements XNavigateItemProvider {

   public static final XNavItemCat GOALS = new XNavItemCat("Goals");

   public static final XNavItemCat ATS = new XNavItemCat("ATS");
   public static final XNavItemCat ATS_IMPORT = new XNavItemCat("ATS.Import");
   public static final XNavItemCat ATS_UTIL = new XNavItemCat("ATS.Utility");
   public static final XNavItemCat ATS_ADMIN = new XNavItemCat("ATS.Admin");
   public static final XNavItemCat ATS_HEALTH = new XNavItemCat("ATS.Health");

   public static final XNavItemCat ATS_VERSIONS = new XNavItemCat("ATS.Versions");
   public static final XNavItemCat ATS_VERSIONS_ADMIN = new XNavItemCat("ATS.Versions.Admin");

   public static final XNavItemCat ATS_RELEASES = new XNavItemCat("ATS.Releases");

   public static final XNavItemCat ATS_WORK_DEFINITION = new XNavItemCat("ATS.Work Definition");
   public static final XNavItemCat ATS_WORK_DEFINITION_ADMIN = new XNavItemCat("ATS.Work Definition.Admin");

   public static final XNavItemCat ATS_EARNED_VALUE = new XNavItemCat("ATS.Earned Value");

   public static final XNavItemCat ATS_ADVANCED_SEARCHES = new XNavItemCat("Advanced Searches");

   public static final XNavItemCat REVIEW = new XNavItemCat("Review");

   public static boolean debug = false;
   private List<XNavigateItem> items;

   @Override
   public boolean isApplicable() {
      return true;
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      this.items = (items == null ? new ArrayList<XNavigateItem>() : items);
      ElapsedTime time = new ElapsedTime("NVI - addAtsSectionChildren", debug);
      try {

         addTopSearchItems();

         addAdvancedSearchesItems();

         addEmailItems();

         addReportItems();

         addTraceItems();

         addGoalItems();

         addDefineItems();

         addAtsItems();

         addUtilItems();

         addUserItems();

         addReviewItems();

         addEvNavigateItems();

         addAdminItems();

         addExampleItems();

         addOteItems();

         addPleItems();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
      return items;
   }

   private void addTopSearchItems() {
      ElapsedTime time2 = new ElapsedTime("NVI - addAtsSectionChildren - My World", debug);
      items.add(new SearchNavigateItem(new MyWorldSearchItem("My World", true), TOP));
      time2.end();

      time2.start("NVI - addAtsSectionChildren - Recently Visited");
      items.add(new RecentlyVisitedNavigateItems(TOP));
      time2.end();

      time2.start("NVI - addAtsSectionChildren - Search");
      items.add(new SearchNavigateItem(new AtsSearchWorkflowSearchItem(), TOP));
      time2.end();

      time2.start("NVI - addAtsSectionChildren - Saved Srch");
      items.add(new SavedActionSearchNavigateItem(TOP));
      time2.end();

   }

   private void addUserItems() {
      ElapsedTime time = new ElapsedTime("NVI - addUserItems", debug);

      List<IUserGroupArtifactToken> adminOrUserMgmt =
         Arrays.asList(CoreUserGroups.UserMgmtAdmin, CoreUserGroups.OseeAdmin);

      items.add(new XNavigateItemFolder(USER_MANAGEMENT.getName(), FrameworkImage.USER, adminOrUserMgmt, TOP));
      items.add(new XNavigateItemFolder("Admin", FrameworkImage.USER, adminOrUserMgmt, USER_MANAGEMENT_ADMIN, SUBCAT));

      items.add(new XNavigateItemAction(new OpenUsersInMassEditor("Open Active Users", Active.Active),
         FrameworkImage.ARTIFACT_SEARCH, adminOrUserMgmt, XNavigateItem.USER_MANAGEMENT, OSEE_ADMIN));
      items.add(new XNavigateItemAction(new OpenUsersInMassEditor("Open All Users", Active.Both),
         FrameworkImage.ARTIFACT_SEARCH, adminOrUserMgmt, XNavigateItem.USER_MANAGEMENT, OSEE_ADMIN));

      items.add(new CreateNewUsersByNameItem());

      items.add(new SearchNavigateItem(
         new UserRelatedToAtsObjectSearch("Admin - Show User Related Objects", null, false, LoadView.WorldEditor),
         USER_MANAGEMENT_ADMIN, OSEE_ADMIN));
      items.add(new SearchNavigateItem(
         new UserRelatedToAtsObjectSearch("Show Active User Related Objects", null, true, LoadView.WorldEditor),
         USER_MANAGEMENT_ADMIN, OSEE_ADMIN));

      time.end();
   }

   private void addDefineItems() {
      ElapsedTime time = new ElapsedTime("NVI - addDefineItems", debug);
      try {
         items.add(new XNavigateItemFolder(DEFINE.getName(), FrameworkImage.LASER, TOP));
         items.add(new XNavigateItemFolder("Health", FrameworkImage.HEALTH, XNavigateItem.DEFINE_HEALTH, SUBCAT));
         items.add(
            new XNavigateItemFolder("Admin", FrameworkImage.LASER, XNavigateItem.DEFINE_ADMIN, OSEE_ADMIN, SUBCAT));
         if (AtsApiService.get().getUserService().isAtsAdmin()) {
            items.add(new CreateEnumeratedArtifactAction());
            items.add(new EditEnumeratedArtifact());
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
   }

   private void addOteItems() {
      ElapsedTime time = new ElapsedTime("NVI - addOteItems", debug);
      try {
         items.add(new XNavigateItemFolder(OTE.getName(), FrameworkImage.TEST_PROCEDURE, XNavItemCat.MID_BOT));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
   }

   private void addAtsItems() {
      ElapsedTime time = new ElapsedTime("NVI - addAtsItems", debug);
      items.add(new XNavigateItemFolder(ATS.getName(), AtsImage.ATS, TOP));
      items.add(new XNavigateItemFolder("Utility", FrameworkImage.GEAR, ATS_UTIL, SUBCAT));
      items.add(new XNavigateItemFolder("Import", FrameworkImage.IMPORT, ATS_IMPORT, SUBCAT));
      items.add(new XNavigateItemFolder("Admin", PluginUiImage.ADMIN, ATS_ADMIN, SUBCAT, OSEE_ADMIN));

      items.add(new AtsConfigResultsEditorNavigateItem());
      items.add(new XNavigateItemAction(new AtsExportAction(), FrameworkImage.EXPORT, ATS_UTIL));
      // Admin
      items.add(new ClearAtsConfigCache());
      items.add(new ClearAtsConfigCacheAllServers());
      items.add(new UpdateWorkDefValidStateNameConfig());
      items.add(new XNavigateItemAction(new OpenOrphanedTasks(), AtsImage.TASK, ATS_ADMIN, OSEE_ADMIN));
      items.add(
         new XNavigateItemAction(new RevertDuplicateAtsTransitionByIdAction(), AtsImage.TASK, ATS_ADMIN, OSEE_ADMIN));
      items.add(
         new XNavigateItemAction(new RevertDuplicateAtsTransitionsAction(), AtsImage.TASK, ATS_ADMIN, OSEE_ADMIN));
      items.add(new DuplicateArtifactReport());

      addReleasesItems();
      addVersionsItems();
      addWorkDefinitionsItems();
      addHealthItems();

      time.end();
   }

   private void addHealthItems() {
      ElapsedTime time = new ElapsedTime("NVI - addAtsItems", debug);
      try {
         items.add(new XNavigateItemFolder("Health", FrameworkImage.HEALTH, ATS_HEALTH, SUBCAT));

         items.add(new AtsHealthCheckNavigateItem());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
   }

   private void addUtilItems() {
      ElapsedTime time = new ElapsedTime("NVI - addUtilItems", debug);
      try {
         items.add(new XNavigateItemFolder(UTILITY.getName(), FrameworkImage.GEAR, XNavItemCat.BOT));

         items.add(new GenerateIdsAndArtId());
         items.add(new ValidateOseeTypes());
         items.add(new CommaDelimitLines());
         items.add(new ToggleAccessControlDebug());
         items.add(new ToggleXViewerColumnLoadingDebug());
         items.add(new XNavigateItemAction(new CompareTwoStringsAction(), FrameworkImage.EDIT, UTILITY));
         items.add(
            new XNavigateItemAction(new org.eclipse.osee.framework.ui.skynet.action.CompareTwoArtifactIdListsAction(),
               FrameworkImage.EDIT, UTILITY));
         items.add(new XNavigateItemOperation(FrameworkImage.ARTIFACT_MASS_EDITOR, MassEditDirtyArtifactOperation.NAME,
            new MassEditDirtyArtifactOperation(), UTILITY));
         items.add(new XNavigateUrlItem("Disciplined Engineering and OSEE",
            "https://git.eclipse.org/c/gerrit/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.admin/presentations_publications/Disciplined_Engineering_with_OSEE.pptx",
            true, FrameworkImage.PPTX, UTILITY));

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
   }

   private void addAdvancedSearchesItems() {
      ElapsedTime time = new ElapsedTime("NVI - advSearch", debug);

      items.add(new XNavigateItemFolder(ATS_ADVANCED_SEARCHES.getName(), AtsImage.SEARCH, TOP));

      items.add(new SearchNavigateItem(new AtsSearchWorkflowAdvSearchItem(), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(new MyFavoritesSearchItem("My Favorites", null), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(new MySubscribedSearchItem("My Subscribed", null), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(new AtsSearchTeamWorkflowSearchItem(), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(new AtsSearchTaskSearchItem(), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(new MyWorldSearchItem("User's World", false), ATS_ADVANCED_SEARCHES));
      items.add(new ArtifactImpactToActionSearchItem());
      items.add(new SearchNavigateItem(new AtsSearchGoalSearchItem(), ATS_ADVANCED_SEARCHES));
      items.add(new SearchNavigateItem(
         new SearchTeamWorkflowsByProgramSearchItem("Search Team Workflows by Program", null, false),
         ATS_ADVANCED_SEARCHES));
      // Search Items
      items.add(new XNavigateItemOperation(FrameworkImage.BRANCH_CHANGE, "Open Change Report(s) by ID(s)",
         new MultipleIdSearchOperationFactory("Open Change Report(s) by ID(s)", AtsEditor.ChangeReport),
         ATS_ADVANCED_SEARCHES));
      items.add(new XNavigateItemOperation(AtsImage.OPEN_BY_ID, "Search by ID(s) - Open World Editor",
         new MultipleIdSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor),
         ATS_ADVANCED_SEARCHES));
      items.add(new XNavigateItemOperation(AtsImage.OPEN_BY_ID, "Search by ID(s) - Multi-Line - Open World Editor",
         new MultipleIdMultiLineSearchOperationFactory("Search by ID(s) - Open World Editor", AtsEditor.WorldEditor),
         ATS_ADVANCED_SEARCHES));
      items.add(new XNavigateItemOperation(AtsImage.WORKFLOW, "Search by ID(s) - Open Workflow Editor",
         new MultipleIdSearchOperationFactory("Search by ID(s) - Open Workflow Editor", AtsEditor.WorkflowEditor),
         ATS_ADVANCED_SEARCHES));
      items.add(new XNavigateItemOperation(AtsImage.GLOBE, "Action Quick Search", new AtsQuickSearchOperationFactory(),
         ATS_ADVANCED_SEARCHES));

      time.end();
   }

   private void addAdminItems() {
      ElapsedTime time = new ElapsedTime("NVI - admin", debug);
      if (AtsApiService.get().getUserService().isAtsAdmin()) {

         items.add(new XNavigateItemFolder("Admin", PluginUiImage.ADMIN, MID_TOP, OSEE_ADMIN));

         items.add(new CreateAtsConfiguration());
         items.add(new XNavigateItemBlam(new ConvertWorkflowStatesBlam(), TOP_ADMIN));
         items.add(new DisplayCurrentOseeEventListeners());
         items.add(new XNavigateItemBlam(new CreateAtsBaselineBranchBlam(), TOP_ADMIN));
         items.add(new XNavigateItemAction(new OpenChangeReportByTransactionIdAction(), FrameworkImage.BRANCH_CHANGE,
            TOP_ADMIN));
         items.add(new XNavigateItemAction(new OpenArtifactEditorById(), FrameworkImage.ARTIFACT_EDITOR, TOP_ADMIN));
         items.add(new XNavigateItemAction(new PurgeTransactionAction(), FrameworkImage.PURGE, TOP_ADMIN));
         items.add(new AtsRemoteEventTestItem());
         items.add(new CleanupOseeSystemAssignedWorkflows());
         items.add(new OseeProductionTestsNavItem());
      }
      time.end();
   }

   private void addEmailItems() {
      ElapsedTime time = new ElapsedTime("NVI - email", debug);
      items.add(new XNavigateItemFolder(EMAIL_NOTIFICATIONS.getName(), FrameworkImage.EMAIL, BOT));

      items.add(new TestEmailSend());
      items.add(new EmailTeamsItem(null, MemberType.Both));
      items.add(new EmailTeamsItem(null, MemberType.Leads));
      items.add(new EmailTeamsItem(null, MemberType.Members));
      items.add(new EmailUserGroups());
      items.add(new SubscribeByActionableItem());
      items.add(new SubscribeByTeamDefinition());
      items.add(new XNavigateItemBlam(new EmailActionsBlam(), FrameworkImage.EMAIL, EMAIL_NOTIFICATIONS));
      time.end();
   }

   private void addTraceItems() {
      items.add(new XNavigateItemFolder(XNavigateItem.TRACE.getName(), FrameworkImage.TRACE, MID_BOT));
   }

   private void addReportItems() {
      ElapsedTime time = new ElapsedTime("NVI - report", debug);
      items.add(new XNavigateItemFolder(REPORTS.getName(), AtsImage.REPORT, MID_BOT));

      items.add(new FirstTimeQualityMetricReportItem());
      time.end();
   }

   private void addGoalItems() {
      ElapsedTime time = new ElapsedTime("NVI - goal", debug);
      items.add(new XNavigateItemFolder(GOALS.getName(), AtsImage.GOAL, MID_BOT));

      items.add(new XNavigateItemAction(new NewGoal(), AtsImage.GOAL, GOALS));
      time.end();
   }

   private void addExampleItems() {
      ElapsedTime time = new ElapsedTime("NVI - example", debug);

      items.add(new XNavigateItemFolder("Examples", FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES, SUBCAT));

      items.add(new ResultsEditorExample());
      items.add(new CompareEditorExample());
      items.add(new XViewerExample());
      items.add(new XResultDataExample());
      items.add(new XResultDataDialogExample());
      items.add(new XResultDataTableExample());
      items.add(new FilteredTreeDialogExample());
      items.add(new FilteredTreeDialogSingleExample());
      items.add(new FilteredTreeArtifactDialogExample());
      items.add(new FilteredCheckboxTreeDialogExample());
      items.add(new FilteredCheckboxTreeArtifactDialogExample());
      items.add(new FilteredCheckboxTreeDialogSelectAllExample());
      items.add(new XNavigateItemAction(new XWidgetsDialogExampleAction(), FrameworkImage.EXAMPLE, UTILITY_EXAMPLES));

      time.end();
   }

   private void addWorkDefinitionsItems() {
      ElapsedTime time = new ElapsedTime("NVI - workDef", debug);

      items.add(new XNavigateItemFolder("Work Definition", AtsImage.WORKFLOW_DEFINITION, ATS_WORK_DEFINITION, SUBCAT));
      items.add(
         new XNavigateItemFolder("Admin", AtsImage.WORKFLOW_DEFINITION, ATS_WORK_DEFINITION_ADMIN, SUBCAT, OSEE_ADMIN));

      try {
         items.add(new WorkDefinitionViewer(AtsNavigateViewItems.ATS_WORK_DEFINITION));
         items.add(new ValidateWorkDefinitionNavigateItem(ATS_WORK_DEFINITION_ADMIN));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Goals section");
      }
      time.end();
   }

   private void addVersionsItems() {
      ElapsedTime time = new ElapsedTime("NVI - version", debug);
      try {

         items.add(new XNavigateItemFolder("Versions", AtsImage.VERSION, ATS_VERSIONS, SUBCAT));
         items.add(new XNavigateItemFolder("Admin", AtsImage.VERSION, ATS_VERSIONS_ADMIN, SUBCAT, OSEE_ADMIN));

         items.add(new ParallelConfigurationView());
         items.add(new SearchNavigateItem(new VersionTargetedForTeamSearchItem(null, null, false, LoadView.WorldEditor),
            ATS_VERSIONS));
         items.add(new SearchNavigateItem(new NextVersionSearchItem(null, LoadView.WorldEditor), ATS_VERSIONS));
         items.add(new GenerateVersionReportItem());
         items.add(new GenerateFullVersionReportItem());
         items.add(new MassEditTeamVersionItem("Team Versions", FrameworkImage.VERSION));

         if (AtsApiService.get().getUserService().isAtsAdmin()) {
            items.add(new CreateNewVersionItem(null));
            items.add(new ReleaseVersionItem(null));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Versions section");
      }
      time.end();
   }

   private void addReleasesItems() {
      ElapsedTime time = new ElapsedTime("NVI - release", debug);
      try {

         items.add(new XNavigateItemFolder("Releases", AtsImage.RELEASED, ATS_RELEASES, SUBCAT));
         items.add(new SearchReleaseArtifacts());

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Releases section");
      }
      time.end();
   }

   private void addReviewItems() {

      items.add(new XNavigateItemFolder(REVIEW.getName(), AtsImage.REVIEW, XNavItemCat.MID_BOT));

      items.add(new SearchNavigateItem(new MyReviewSearchItem(), REVIEW));
      items.add(new SearchNavigateItem(new AtsSearchReviewSearchItem(), REVIEW));
      items.add(new XNavigateItemAction(new OpenWorkflowByIdAction("Open Review by ID"), AtsImage.REVIEW, REVIEW));
      items.add(new SearchNavigateItem(
         new ShowOpenWorkflowsByReviewType("Show Open " + WorkItemType.DecisionReview.name() + "s",
            WorkItemType.DecisionReview, false, false, AtsImage.DECISION_REVIEW),
         REVIEW));
      items.add(new SearchNavigateItem(
         new ShowOpenWorkflowsByReviewType("Show Workflows Waiting " + WorkItemType.DecisionReview.name() + "s",
            WorkItemType.DecisionReview, false, true, AtsImage.DECISION_REVIEW),
         REVIEW));
      items.add(
         new SearchNavigateItem(new ShowOpenWorkflowsByReviewType("Show Open " + WorkItemType.PeerReview.name() + "s",
            WorkItemType.PeerReview, false, false, AtsImage.PEER_REVIEW), REVIEW));
      items.add(new SearchNavigateItem(
         new ShowOpenWorkflowsByReviewType("Show Workflows Waiting " + WorkItemType.PeerReview.name() + "s",
            WorkItemType.PeerReview, false, true, AtsImage.PEER_REVIEW),
         REVIEW));
      items.add(new NewPeerToPeerReviewItem());
      items.add(new GenerateReviewParticipationReport());
      items.add(new SearchNavigateItem(new WorkingCompletePeerReviewReportSearchItem(), REVIEW));
      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         items.add(new XNavigateItemAction(new ValidatePeerDefectsAction(), AtsImage.PEER_REVIEW, REVIEW));
      }
   }

   private void addPleItems() {
      ElapsedTime time = new ElapsedTime("NVI - addPleItems", debug);
      try {
         String applicationServer = System.getProperty("osee.web.url", "null");

         if (applicationServer.equals("null")) {
            applicationServer = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER, "null");

            if (applicationServer.equals("null")) {
               OseeLog.log(Activator.class, Level.SEVERE, "osee.application.server property not set!");
            } else {
               OseeLog.log(Activator.class, Level.INFO,
                  "osee.web.url property not set, using default osee.application.server url: " + applicationServer);
            }
         }

         items.add(new XNavigateItemFolder(PLE.getName(), FrameworkImage.PLE, XNavItemCat.TOP, PLE));
         items.add(new XNavigateUrlItem("Product Line (PL) Dashboard", applicationServer + "/osee/ple", true,
            FrameworkImage.PLE, PLE));
         items.add(new XNavigateUrlItem("Product Line Configuration (PLConfig)",
            applicationServer + "/osee/ple/plconfig", true, FrameworkImage.PLE, PLE));
         items.add(new XNavigateUrlItem("Message Interface Modeling (MIM)", applicationServer + "/osee/ple/messaging",
            true, FrameworkImage.PLE, PLE));
         items.add(new XNavigateUrlItem("PLE - Getting Started",
            "https://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.admin/presentations_publications/Disciplined_Engineering_with_OSEE.pptx?h=dev",
            true, FrameworkImage.PPTX, PLE));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create PLE section");
      }
      time.end();
   }

   private void addEvNavigateItems() {
      ElapsedTime time = new ElapsedTime("NVI - addUserItems", debug);
      try {
         if (AgileUtil.isEarnedValueUser(AtsApiService.get())) {

            items.add(new XNavigateItemFolder("Earned Value", AtsImage.REPORT, ATS_EARNED_VALUE, SUBCAT));

            items.add(
               new XNavigateItemAction(new OpenWorkPackageByIdAction(), AtsImage.WORK_PACKAGE, ATS_EARNED_VALUE));
            items.add(new WorkPackageConfigReport());
            items.add(new WorkPackageQBDReport());
            items.add(new SearchNavigateItem(new AtsSearchWorkPackageSearchItem(), ATS_EARNED_VALUE));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      time.end();
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

   public static boolean isDebug() {
      return false;
   }

}
