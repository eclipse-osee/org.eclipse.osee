/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.coderev.packet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * THROWAWAY migration reference: the curated old-&gt;new XWidget name mapping for the XWidget -&gt; XXWidget migration
 * (commit {@code feature[TW29349]}), now organized by {@link Category}. One {@link WidgetMapping} per NEW widget, with
 * the old name(s) that merged into it (sometimes several old widgets collapsed into one new one, e.g. all the working
 * branch widgets). Deleted widgets are entries with a blank new name and {@link Category#DELETED}.
 * <p>
 * This is the single source of truth; the legacy {@link #getMapping()} / {@link #getNewName(String)} /
 * {@link #getDeletedWidgets()} accessors are derived from the list so existing callers keep working, and
 * {@link #getWidgetMappings()} exposes the categorized list for the review report.
 *
 * @author Donald G. Dunne
 */
public class ReportXWidgetMapping {

   /**
    * Widget groupings, ordered for the review report: framework/base first, then ATS domain widgets, then agile, then
    * deleted last. {@link #band} groups categories; {@link #label} is the section title.
    */
   public enum Category {
      // --- Framework band ---
      BASE(Band.FRAMEWORK, "Base Widgets"),
      SELECTION(Band.FRAMEWORK, "Selection Widgets"),
      TYPE(Band.FRAMEWORK, "Type Selection Widgets"),
      BRANCH(Band.FRAMEWORK, "Branch Widgets"),
      USER(Band.FRAMEWORK, "User Widgets"),
      FILE(Band.FRAMEWORK, "File / Dir Widgets"),
      // --- ATS band ---
      ACTIONABLE_ITEM(Band.ATS, "Actionable Item"),
      ASSIGNEES(Band.ATS, "Assignees"),
      VERSION(Band.ATS, "Version"),
      TEAM(Band.ATS, "Team Definition"),
      WORKING_BRANCH(Band.ATS, "Working Branch"),
      PROGRAM(Band.ATS, "Program"),
      REVIEW(Band.ATS, "Review"),
      WORKFLOW_STATE(Band.ATS, "Workflow / State"),
      PLE(Band.ATS, "PLE / Configuration"),
      ATS_MISC(Band.ATS, "ATS Misc"),
      // --- Agile band ---
      AGILE(Band.AGILE, "Agile"),
      // --- Deleted (always last) ---
      DELETED(Band.DELETED, "Deleted (no replacement)");

      public enum Band {
         FRAMEWORK,
         ATS,
         AGILE,
         DELETED;
      }

      private final Band band;
      private final String label;

      Category(Band band, String label) {
         this.band = band;
         this.label = label;
      }

      public Band getBand() {
         return band;
      }

      public String getLabel() {
         return label;
      }
   }

   /** One new widget, its category, and the old name(s) that map to it (empty old names allowed for brand-new). */
   public static final class WidgetMapping {
      public final String newName;
      public final Category category;
      public final List<String> oldNames;

      public WidgetMapping(String newName, Category category, String... oldNames) {
         this.newName = newName;
         this.category = category;
         this.oldNames = List.of(oldNames);
      }
   }

   private static final List<WidgetMapping> MAPPINGS = new ArrayList<>();
   static {
      // =========================================================================
      // Framework band
      // =========================================================================
      // Base (text / numeric / boolean / date / label / button / list / misc primitives)
      add("XXTextWidget", Category.BASE, "XText", "XTextDam");
      add("XXFlatAttrsWidget", Category.BASE, "XTextFlatDam", "XFlatDam");
      add("XXStackedWidget", Category.BASE, "XStackedDam");
      add("XXLongSTextWidget", Category.BASE, "XLongTextDam");
      add("XXIntegerSTextWidget", Category.BASE, "XInteger", "XIntegerDam");
      add("XXDoubleSTextWidget", Category.BASE, "XFloat", "XFloatDam");
      add("XXPercentSTextWidget", Category.BASE, "XPercent");
      add("XXCheckBoxWidget", Category.BASE, "XCheckBox", "XCheckBoxDam");
      add("XXBooleanWidget", Category.BASE, "XComboBooleanDam", "XHyperlinkTriStateBoolean",
         "XHyperlinkTriStateBooleanDam");
      add("XXDateSelWidget", Category.BASE, "XDate", "XDateDam", "XDateWithValidateDam",
         "XHyperlinkLabelDate", "XHyperlinkLabelDateDam");
      add("XXLabelWidget", Category.BASE, "XLabel");
      add("XXLabelValueWidget", Category.BASE, "XLabelDam");
      add("XXButtonHyperWidget", Category.BASE, "XHyperlinkLabel");
      add("XXButtonPushWidget", Category.BASE, "XButtonPush");
      add("XXStringsWidget", Category.BASE, "XCombo", "XComboDam", "XComboViewer",
         "XHyperlinkLabelValueSelectionDam", "XHyperlinkWfdForEnum", "XHyperlinkWfdForEnumAttr",
         "XHyperlinkWfdForEnumAttrDam", "ClosureStateMultiChoiceSelect");
      add("XXStringsEnumArtWidget", Category.BASE, "XHyperlinkLabelEnumeratedArt",
         "XHyperlinkLabelEnumeratedArtDam");
      add("XXListWidget", Category.BASE, "XListViewer");
      add("XXListArtifactDropWidget", Category.BASE, "XListDropViewer", "XListDropViewerDam");
      add("XXListRelationWidget", Category.BASE);
      add("XXMapEntryWidget", Category.BASE, "XMapEntry");
      add("XXStringSTextHEditWidget", Category.BASE);
      add("XXTextInternalWidget", Category.BASE);

      // Selection (artifact ref / list / select)
      add("XXArtifactRefWidget", Category.SELECTION, "XHyperlinkArtifactRefIdEntryWidget",
         "XArtifactReferencedAtsObjectAttributeWidget");
      add("XXArtifactListWidget", Category.SELECTION, "XArtifactList");
      add("XXArtifactSelWidget", Category.SELECTION, "XHyperlinkArtifactSel", "XArtifactSelectWidget",
         "XArtifactSelectWidgetWithSave");
      add("XXChildSelWidget", Category.SELECTION);
      add("XXArtEdAttrViewerWidget", Category.SELECTION);
      add("XXComputedCharacteristicWidget", Category.SELECTION);
      add("XXCopyIdHyperlinkWidget", Category.SELECTION);
      add("XXButtonViaActionWidget", Category.SELECTION);

      // Type selection
      add("XXArtifactTypeWidget", Category.TYPE, "XArtifactTypeComboViewer", "XArtifactTypeMultiChoiceSelect");
      add("XXAttributeTypeWidget", Category.TYPE, "XAttributeTypeComboViewer", "XAttributeTypeMultiChoiceSelect",
         "XAttributeTypeListViewer");

      // Branch
      add("XXBranchSelWidget", Category.BRANCH, "XBranchSelectWidget", "XHyperlinkBranchSel");
      add("XXBranchViewWidget", Category.BRANCH, "ArtifactExplorerViewApplicability");
      add("XXBranchAndViewSelWidget", Category.BRANCH, "XHyperlinkBranchViewSelect");

      // User
      add("XXUserTokenWidget", Category.USER, "XMembersCombo", "XMembersList", "XHyperlabelMemberSelectionDam",
         "XHyperlinkWfdForUser", "XHyperlinkWfdForUserAll");
      add("XXUserTypeWidget", Category.USER);

      // File / Dir
      add("XXFileSelWidget", Category.FILE, "XFileSelectionDialog", "XFileTextWithSelectionDialog",
         "XTextWithFileSelDialogWidget");
      add("XXDirSelWidget", Category.FILE, "XDirSelectionDialog");

      // =========================================================================
      // ATS band
      // =========================================================================
      add("XXActionableItemWidget", Category.ACTIONABLE_ITEM, "XActionableItemWidget", "XActionableItemCombo",
         "XActionableItemAllCombo", "XHyperlabelActionableItemSelection", "XHyperlinkWfdForActiveAis");

      add("XXAssigneesWidget", Category.ASSIGNEES, "XAssigneesHyperlinkWidget", "XAssigneesListWidget",
         "XAssigneesListWorkItemWidget", "AbstractXAssigneesListWidget");

      add("XXTargetedVersionWidget", Category.VERSION, "XTargetedVersionHyperlinkWidget",
         "XTargetedVersionWithPersistWidget", "VersionMultiChoiceSelect");
      add("XXVersionSelWidget", Category.VERSION, "XHyperlabelVersionSelection",
         "XHyperlabelVersionSelectionWithPersist");
      add("XXFoundInVersionWidget", Category.VERSION, "XFoundInVersionWithPersistWidget",
         "XHyperlabelFoundInVersionSelection");
      add("XXIntroducedInVersionWidget", Category.VERSION, "XIntroducedInVersionWithPersistWidget",
         "XHyperlabelIntroducedInVersionSelection");
      add("XXVersionListWidget", Category.VERSION, "XVersionList");

      add("XXTeamDefinitionsWidget", Category.TEAM, "XTeamDefinitionCombo", "XHyperlabelTeamDefinitionSelection");

      add("XXWorkingBranchWfeWidget", Category.WORKING_BRANCH, "XWorkingBranchWidgetAbstract", "XWorkingBranchLabel",
         "XWorkingBranchButtonAbstract", "XWorkingBranchButtonCreate", "XWorkingBranchButtonDelete",
         "XWorkingBranchButtonArtifactExplorer", "XWorkingBranchButtonChangeReport",
         "XWorkingBranchButtonWordChangeReport", "XWorkingBranchButtonContextChangeReport",
         "XWorkingBranchButtonFavorites", "XWorkingBranchButtonLock", "XWorkingBranchUpdate",
         "XWorkingBranchButtonDeleteMergeBranches");
      add("XXGitFetchButtonWfeWidget", Category.WORKING_BRANCH, "XGitFetchButton");
      add("XCommitManagerWfeWidget", Category.WORKING_BRANCH, "XCommitManager");

      add("XXProgramWidget", Category.PROGRAM, "XAtsProgramComboWidget", "XHyperlinkWfdForPrograms",
         "XProgramSelectionWidget", "XHyperlinkWfdForProgramClosure");
      add("XXProgramAiWidget", Category.PROGRAM, "XHyperlinkWfdForProgramAi");

      add("XXReviewFormalTypeWidget", Category.REVIEW, "XReviewTypeWidget");
      add("XXDefectViewerWidget", Category.REVIEW, "XDefectViewer");
      add("XUserRoleViewerWidget", Category.REVIEW, "XUserRoleViewer");
      add("XXDecisionRevWidget", Category.REVIEW);

      add("XXChangeTypeWidget", Category.WORKFLOW_STATE, "XHyperlinkChangeTypeSelection",
         "XHyperlinkChangeTypeSelectionDam");
      add("XXPriorityWidget", Category.WORKFLOW_STATE, "XHyperlinkPrioritySelection", "XHyperlinkPrioritySelectionDam",
         "XHyperlinkLabelValueStringSel");
      add("XXPointsEnumWidget", Category.WORKFLOW_STATE, "XEstimatedPointsWidget");
      add("XXPointsNumericWidget", Category.WORKFLOW_STATE, "XPointsNumericWidget");
      add("XXOriginatorWidget", Category.WORKFLOW_STATE, "XOriginatorHyperlinkWidget");
      add("XXRelatedStateWfeWidget", Category.WORKFLOW_STATE, "XHyperlinkWfdForRelatedState",
         "XHyperlinkWfdForRelatedStateDam");
      add("XXOperationalImpactWidget", Category.WORKFLOW_STATE, "OperationalImpactXWidget");
      add("XXOperImpactWithWorkArndWidget", Category.WORKFLOW_STATE, "OperationalImpactWithWorkaroundXWidget");
      add("XXWorkPackageEnumArtWidget", Category.WORKFLOW_STATE, "XHyperlinkWorkPackageDam");
      add("XXPercentWfeWidget", Category.WORKFLOW_STATE, "StatePercentCompleteXWidget");
      add("XXHoursSpentWfeWidget", Category.WORKFLOW_STATE, "XHoursSpentWfeWidget");
      add("XXRemainingHoursWfeWidget", Category.WORKFLOW_STATE, "XRemainingHoursWfeWidget");
      add("XXTotalHoursSpentWfeWidget", Category.WORKFLOW_STATE, "XTotalHoursSpentWfeWidget");
      add("XXTitleWfeWidget", Category.WORKFLOW_STATE, "XTitleWfeWidget");
      add("XXWorkflowNotesWfeWidget", Category.WORKFLOW_STATE, "XWorkflowNotesWfeWidget");
      add("XXTaskInfoWfeWidget", Category.WORKFLOW_STATE, "TaskInfoXWidget");
      add("XXSignByAndDateArtWidget", Category.WORKFLOW_STATE, "XSignByAndDateWidget");
      add("XXBlockWfeWidget", Category.WORKFLOW_STATE, "XBlockWfeWidget");
      add("XXHoldWfeWidget", Category.WORKFLOW_STATE, "XHoldWfeWidget");
      add("XXMeetingAttendeesWfeWidget", Category.WORKFLOW_STATE, "XHyperlinkMeetingAttendeesSelectionDam");
      add("XXDragDropAttachWfeWidget", Category.WORKFLOW_STATE, "XDragDropAttachWidget");
      add("XXValidateReqChangesButtonWfeWidget", Category.WORKFLOW_STATE, "XValidateReqChangesButton");
      add("XXRelatedWfeWidget", Category.WORKFLOW_STATE, "XRelatedWidget");
      add("XXCreateChangeReportTasksWidget", Category.WORKFLOW_STATE);
      add("XXWorkDefReferenceWidget", Category.WORKFLOW_STATE);

      add("XXWorkflowApplicWidget", Category.PLE, "XHyperlabelWorkflowApplicabilitySelection",
         "XHyperlinkApplicabilityWidget", "XHyperlinkApplicabilityWidgetDam");
      add("XXPleFeatureWidget", Category.PLE, "XHyperlinkFeatureDam");
      add("XXPleConfigurationWidget", Category.PLE, "XHyperlinkConfigurationWidget", "XHyperlinkWfdForConfiguration",
         "XHyperlinkWfdForConfigurationDam");
      add("XXBuildImpactNameWidget", Category.PLE, "XHyperlinkBuildImpactWidget");
      add("XXBuildImpactStateWidget", Category.PLE, "XHyperlinkBuildImpactStateWidget");
      add("XXPrBuildSelWidget", Category.PLE, "XHyperlinkPrBuildSelection");

      add("XXAttachmentDemoWidget", Category.ATS_MISC, "XAttachmentExampleWidget");
      add("XXHyperlinkOpenBitTabWidget", Category.ATS_MISC, "XHyperlinkOpenBitTab");
      add("XXHyperlinkJiraStatusWidget", Category.ATS_MISC, "XHyperlinkJiraStatus");
      add("XXFunctAreaDemoWidget", Category.ATS_MISC, "XFunctAreaDemoWidget");
      add("XXFundingGoalDemoWidget", Category.ATS_MISC, "XFundingGoalDemoWidget");
      add("XXCreateEscapeDemoWfButtonWidget", Category.ATS_MISC, "XCreateEscapeDemoWfXButton");
      add("XXHoldStateWidget", Category.ATS_MISC);
      add("XXStateNameWidget", Category.ATS_MISC);
      add("XXStateTypeWidget", Category.ATS_MISC);

      // =========================================================================
      // Agile band
      // =========================================================================
      add("XXAgileFeatureSelWidget", Category.AGILE, "XAgileFeatureHyperlinkWidget");
      add("XXAgileSprintSelWidget", Category.AGILE, "XSprintHyperlinkWidget", "XSprintHyperlinkWidgetDam");
      add("XXOpenSprintSummaryArtWidget", Category.AGILE, "XOpenSprintSummaryButton");
      add("XXOpenSprintDataTableArtWidget", Category.AGILE, "XOpenSprintDataTableButton");
      add("XXOpenSprintBurndownArtWidget", Category.AGILE, "XOpenSprintBurndownButton");
      add("XXOpenSprintBurnupArtWidget", Category.AGILE, "XOpenSprintBurnupButton", "AbstractXOpenSprintBurnupButton");
      add("XXOpenSprintReportsArtWidget", Category.AGILE, "XOpenSprintReportsButton");
      add("XXOpenStoredSprintReportsArtWidget", Category.AGILE, "XOpenStoredSprintReportsButton");
      add("XXStoreSprintReportsArtWidget", Category.AGILE, "XStoreSprintReportsButton");

      // =========================================================================
      // Deleted (removed entirely, no direct replacement) - blank new name, Category.DELETED
      // =========================================================================
      add("", Category.DELETED, "XAbstractHyperlinkTriStateNotifyDam", "XAtsUserListDam", "XGoalCombo",
         "XHyperlinkWorkDefDam", "XTransitionToStateComboWidget", "XDynamicAttrValuesWidget",
         "AtsObjectMultiChoiceSelect", "XCheckBoxesWithTaskGenExample", "XHyperlabelGroupSelection",
         "XHyperlinkWfdForObject");
   }

   private static void add(String newName, Category category, String... oldNames) {
      MAPPINGS.add(new WidgetMapping(newName, category, oldNames));
   }

   /** @return the full categorized list of widget mappings (source of truth for the review report). */
   public static List<WidgetMapping> getWidgetMappings() {
      return MAPPINGS;
   }

   /** @return count of NEW widgets (non-blank new name, excluding deleted entries). */
   public static int getNewWidgetCount() {
      int count = 0;
      for (WidgetMapping m : MAPPINGS) {
         if (m.category != Category.DELETED && !m.newName.isEmpty()) {
            count++;
         }
      }
      return count;
   }

   /** @return count of distinct OLD widget names across all entries (including deleted). */
   public static int getOldWidgetCount() {
      java.util.Set<String> old = new java.util.HashSet<>();
      for (WidgetMapping m : MAPPINGS) {
         old.addAll(m.oldNames);
      }
      return old.size();
   }

   /** @return count of old widgets deleted outright (no replacement). */
   public static int getDeletedWidgetCount() {
      return getDeletedWidgets().size();
   }

   /** @return derived old-&gt;new map (one entry per old name) for legacy callers; excludes deleted entries. */
   public static Map<String, String> getMapping() {
      Map<String, String> map = new LinkedHashMap<>();
      for (WidgetMapping m : MAPPINGS) {
         if (m.category == Category.DELETED) {
            continue;
         }
         for (String old : m.oldNames) {
            map.put(old, m.newName);
         }
      }
      return map;
   }

   /** @return the new XX widget name for the given old widget name, or null if not found. */
   public static String getNewName(String oldName) {
      return getMapping().get(oldName);
   }

   /** @return the list of old XWidgets that were deleted entirely (no replacement). */
   public static List<String> getDeletedWidgets() {
      List<String> deleted = new ArrayList<>();
      for (WidgetMapping m : MAPPINGS) {
         if (m.category == Category.DELETED) {
            deleted.addAll(m.oldNames);
         }
      }
      return deleted;
   }

}
