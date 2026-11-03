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
package org.eclipse.osee.framework.core.widget;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps WidgetId names to their default labels as declared in the XX*Widget constructors. Used server-side to resolve
 * widget labels when not explicitly specified in a WidgetDef.
 * <p>
 * Auto-populated from scanning XX*Widget class constructors.
 *
 * @author Donald G. Dunne
 */
public final class WidgetIdDefaultLabels {

   private static final Map<String, String> widgetIdToLabel = new HashMap<>(100);

   static {
      // Framework widgets
      widgetIdToLabel.put("XXArtifactListWidget", "Artifacts");
      widgetIdToLabel.put("XXArtifactRefWidget", "Artifact Ref");
      widgetIdToLabel.put("XXArtifactSelWidget", "Artifact");
      widgetIdToLabel.put("XXAttributeTypeWidget", "Attribute Type");
      widgetIdToLabel.put("XXArtifactTypeWidget", "Artifact Type");
      widgetIdToLabel.put("XXBranchSelWidget", "Branch");
      widgetIdToLabel.put("XXBranchAndViewSelWidget", "Branch / View Selection");
      widgetIdToLabel.put("XXBranchViewWidget", "Branch View");
      widgetIdToLabel.put("XXDirSelWidget", "Select Folder");
      widgetIdToLabel.put("XXFileSelWidget", "Select File");
      widgetIdToLabel.put("XXListDropWidget", "Artifacts");
      widgetIdToLabel.put("XXUserTokenWidget", "User");
      widgetIdToLabel.put("XXArtEdAttrViewerWidget", "Attributes");

      // ATS widgets
      widgetIdToLabel.put("XXAssigneesWidget", "Assignee(s)");
      widgetIdToLabel.put("XXProgramWidget", "Program");
      widgetIdToLabel.put("XXVersionListWidget", "Versions");
      widgetIdToLabel.put("XXHyperlinkJiraStatusWidget", "JIRA Status");
      widgetIdToLabel.put("XXRelatedStateWfeWidget", "Related State");
      widgetIdToLabel.put("XXOpenSprintBurndownArtWidget", "Open Sprint Burn-Down");
      widgetIdToLabel.put("XXOpenSprintBurnupArtWidget", "Open Sprint Burn-Up");
      widgetIdToLabel.put("XXOpenSprintDataTableArtWidget", "Open Sprint Data Table");
      widgetIdToLabel.put("XXOpenSprintReportsArtWidget", "Open Current Sprint Reports");
      widgetIdToLabel.put("XXOpenSprintSummaryArtWidget", "Open Sprint Summary");
      widgetIdToLabel.put("XXOpenStoredSprintReportsArtWidget", "Open Snapshots of Stored Sprint Charts");
      widgetIdToLabel.put("XXStoreSprintReportsArtWidget", "Store Snapshot of Sprint Reports");
      widgetIdToLabel.put("XXUserTypeWidget", "User Type");
      widgetIdToLabel.put("XXBuildImpactNameWidget", "Build Impact");
      widgetIdToLabel.put("XXCreateEscapeDemoWfButtonWidget", "Create/Open Great Escape Workflow");
      widgetIdToLabel.put("XXBuildImpactStateWidget", "Build Impact State");
      widgetIdToLabel.put("XXHoldStateWidget", "Hold State");
      widgetIdToLabel.put("XXStateNameWidget", "State Name(s)");
      widgetIdToLabel.put("XXStateTypeWidget", "State Type");
      widgetIdToLabel.put("XXHyperlinkOpenBitTabWidget", "Open Build Impact Table");
      widgetIdToLabel.put("XXDefectViewerWidget", "Defects");
      widgetIdToLabel.put("XXCreateChangeReportTasksWidget", "Create Code/Test Tasks");
      widgetIdToLabel.put("XXActionableItemWidget", "Actionable Item(s)");
      widgetIdToLabel.put("XXAgileFeatureSelWidget", "Feature");
      widgetIdToLabel.put("XXAgileSprintSelWidget", "Sprint");
      widgetIdToLabel.put("XXChangeTypeWidget", "Change Type");
      widgetIdToLabel.put("XXDragDropAttachWfeWidget", "Drag/Drop Related Here");
      widgetIdToLabel.put("XXGitFetchButtonWfeWidget", "Import Code Files from Branch");
      widgetIdToLabel.put("XXHoursSpentWfeWidget", "Hours Spent");
      widgetIdToLabel.put("XXOperationalImpactWidget", "Operational Impact");
      widgetIdToLabel.put("XXOperImpactWithWorkArndWidget", "Operational Impact");
      widgetIdToLabel.put("XXPercentWfeWidget", "Percent Complete");
      widgetIdToLabel.put("XXPointsEnumWidget", "Points");
      widgetIdToLabel.put("XXPointsNumericWidget", "Points");
      widgetIdToLabel.put("XXPriorityWidget", "Priority");
      widgetIdToLabel.put("XXProgramAiWidget", "Program");
      widgetIdToLabel.put("XXRemainingHoursWfeWidget", "Remaining Hours");
      widgetIdToLabel.put("XXReviewFormalTypeWidget", "Review Type");
      widgetIdToLabel.put("XXTargetedVersionWidget", "Targeted Version");
      widgetIdToLabel.put("XXTaskInfoWfeWidget", "Tasks");
      widgetIdToLabel.put("XXTeamDefinitionWidget", "Team Definition(s)");
      widgetIdToLabel.put("XXOriginatorWidget", "Originator");
      widgetIdToLabel.put("XXTitleWfeWidget", "Title");
      widgetIdToLabel.put("XXTotalHoursSpentWfeWidget", "Total Hours Spent");
      widgetIdToLabel.put("XXValidateReqChangesButtonWfeWidget", "Validate Requirement Changes");
      widgetIdToLabel.put("XXVersionSelWidget", "Version");
      widgetIdToLabel.put("XXWorkflowNotesWfeWidget", "Workflow Notes");
      widgetIdToLabel.put("XXWorkflowApplicWidget", "Applicability");
      widgetIdToLabel.put("XXWorkingBranchWfeWidget", "Working Branch");
      widgetIdToLabel.put("XXWorkPackageEnumArtWidget", "Work Package");
      widgetIdToLabel.put("XXFoundInVersionWidget", "Found In Version");
      widgetIdToLabel.put("XXIntroducedInVersionWidget", "Introduced In Version");
      widgetIdToLabel.put("XXFunctAreaDemoWidget", "Functional Area");
      widgetIdToLabel.put("XXFundingGoalDemoWidget", "Funding Goal");
      widgetIdToLabel.put("XXPleConfigurationWidget", "PLE Configuration");
      widgetIdToLabel.put("XXPleFeatureWidget", "PLE Feature");
      widgetIdToLabel.put("XXSignByAndDateArtWidget", "Sign By and Date");
      widgetIdToLabel.put("XXMeetingAttendeesWfeWidget", "Meeting Attendees");
      widgetIdToLabel.put("XXAttachmentExampleWidget", "Attachment Example");
      widgetIdToLabel.put("XXAtsArtifactRefWidget", "ATS Artifact Ref");
      widgetIdToLabel.put("XCommitManagerWfeWidget", "Commit Manager");
      widgetIdToLabel.put("XXPrBuildSelWidget", "PR Build");
   }

   private WidgetIdDefaultLabels() {
      // Constants only
   }

   /**
    * Returns the default label for the given WidgetId name, or empty string if not found.
    */
   public static String getLabel(String widgetIdName) {
      String label = widgetIdToLabel.get(widgetIdName);
      return label != null ? label : "";
   }

   /**
    * Returns the default label for the given WidgetId, or empty string if not found.
    */
   public static String getLabel(WidgetId widgetId) {
      if (widgetId == null) {
         return "";
      }
      return getLabel(widgetId.getName());
   }

}
