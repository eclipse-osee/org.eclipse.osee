/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.column;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreAttrTokColTokenDefault;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.config.ActionRollup;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.InheritParent;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * NOTE: column ids can NOT be changed without affecting the stored customizations<br/>
 * <br/>
 * Architecture/Design<br/>
 * <br/>
 * - First, declare a column token for each column id. This either specifies an attr column (preferred) or value
 * column.<br/>
 * - If attr column, this is what brings the id together with column data and the attr type token<br/>
 * <br/>
 * - Second, add to AtsColumnService or an AtsColumnProvider to resolve and instantiate columns<br/>
 * <br/>
 * <br/>
 * - Third, add to Register column in WorldXViewerFactory(ATS bundle) or an IAtsWorldEditorItem (other bundles)<br/>
 * <br/>
 *
 * @author Donald G. Dunne
 */
public class AtsColumnTokensDefault {

   private static Map<String, AtsCoreAttrTokColumnToken> idToAttrValCol = new HashMap<>(500);
   private static boolean hintsSet = false;

   /**
    * Attribute Value columns. These are backed directly by an attribute with the possible ability to change with edit
    * or alt-left-click. Only create a column token if need to reference in code (eg: XViewerFactory default visible
    * columns). All other attrs will be auto created. USE THIS IF POSSIBLE!!! These are automatically added to world
    * views.
    */
   // @formatter:off

   public static CoreAttrTokColTokenDefault AssumptionsColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.Assumptions);
   public static CoreAttrTokColTokenDefault CreatedDateColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.CreatedDate);
   public static CoreAttrTokColTokenDefault DescriptionColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.Description);
   public static CoreAttrTokColTokenDefault EstimatedHoursColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.EstimatedHours);
   public static CoreAttrTokColTokenDefault HoursSpent = new CoreAttrTokColTokenDefault(AtsAttributeTypes.HoursSpent);
   public static CoreAttrTokColTokenDefault LegacyPcrIdColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.LegacyPcrId);
   public static CoreAttrTokColTokenDefault NameColumn = new CoreAttrTokColTokenDefault(CoreAttributeTypes.Name);
   public static CoreAttrTokColTokenDefault NotesColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.WorkflowNotes);
   public static CoreAttrTokColTokenDefault PercentCompleteWorkflowColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.PercentComplete);
   public static CoreAttrTokColTokenDefault ResolutionColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.Resolution);
   public static CoreAttrTokColTokenDefault RiskFactorColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.RiskFactor);
   public static CoreAttrTokColTokenDefault UnPlannedWorkColumn = new CoreAttrTokColTokenDefault(AtsAttributeTypes.UnplannedWork);

   // @formatter:on

   /**
    * ColumnId columns are basic columns providing simple or computed data. They are NOT backed directly by an
    * attribute, usually don't change, usually can't be changed by the user through edit or alt-left click.</br>
    * USE ATTRIBUTE VALUE COLUMNS above if possible!!
    */
   // @formatter:off

   public static CoreCodeColumnTokenDefault ActionableItemsColumn = new CoreCodeColumnTokenDefault("ats.column.actionableItems", "Actionable Item(s)", 80, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault AgileFeatureGroupColumn = new CoreCodeColumnTokenDefault("ats.column.agileFeatureGroup", "Feature Group", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Agile Feature Group for this Item");
   public static CoreCodeColumnTokenDefault AgileTeamPointsColumn = new CoreCodeColumnTokenDefault("ats.agileTeam.Points", AtsAttributeTypes.PointsNumeric);
   public static CoreCodeColumnTokenDefault AssigneeColumn = new CoreCodeColumnTokenDefault("ats.column.assignees", "Assignees", 100, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault AtsIdColumn = new CoreCodeColumnTokenDefault("ats.id", "ATS Id", 75, AtsAttributeTypes.AtsId);
   public static CoreCodeColumnTokenDefault AttachmentsCountColumn = new CoreCodeColumnTokenDefault("ats.column.attachment.count", "Attachment Count", 20, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, "Shows number of attachments.  Double-click to open task.");
   public static CoreCodeColumnTokenDefault BacklogOrderColumn = new CoreCodeColumnTokenDefault("ats.column.backlogOrder", "Backlog Order", 45, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault CrIdColumn = new CoreCodeColumnTokenDefault("ats.column.crId", "CR ID", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault ChangeTypeColumn = new CoreCodeColumnTokenDefault("ats.column.changetype", AtsAttributeTypes.ChangeType);
   public static CoreCodeColumnTokenDefault CheckColumn = new CoreCodeColumnTokenDefault("ats.column.check", "Select", 53, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, "Check and plus to create canned tasks.  Add task to create manual tasks.");
   public static CoreCodeColumnTokenDefault CompletedCancelledByColumn = new CoreCodeColumnTokenDefault( "ats.column.cmpCnclBy", "Completed or Cancelled By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to completed or cancelled state.");
   public static CoreCodeColumnTokenDefault CompletedCancelledDateColumn = new CoreCodeColumnTokenDefault("ats.column.cmpCnclDate", "Completed or Cancelled Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Date action to completed or cancelled");
   public static CoreCodeColumnTokenDefault DerivedFromAtsIdColumn = new CoreCodeColumnTokenDefault("ats.column.derivedFromAtsId", "Derived From ATS Id", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault DerivedFromTeamDefColumn = new CoreCodeColumnTokenDefault("ats.column.derivedFromTeam", "Derived From Team Def", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault FeatureImpactReferenceColumn = new CoreCodeColumnTokenDefault("ats.column.featureImpactReference", "Feature Impacted", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault FoundInVersionColumn = new CoreCodeColumnTokenDefault("ats.column.foundInVersion", "Found In Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Release version in which software issue occured.");
   public static CoreCodeColumnTokenDefault GoalOrderColumn = new CoreCodeColumnTokenDefault("ats.column.goalOrder", "Goal Order", 45, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault HoursSpentTotalColumn = new CoreCodeColumnTokenDefault("ats.column.totalHoursSpent", "Total Hours Spent", 40, ColumnType.Float, ColumnAlign.Center, Show.No, MultiEdit.No,ActionRollup.No, InheritParent.No, "Hours spent for all work related to all states.");
   public static CoreCodeColumnTokenDefault IdColumn = new CoreCodeColumnTokenDefault("framework.id", "Id", 30, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault ImplementersColumn = new CoreCodeColumnTokenDefault("ats.column.implementer", "Implementer(s)", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault IncorporatedInColumn = new CoreCodeColumnTokenDefault("ats.column.incorporated.in", "Incorporated In", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "");
   public static CoreCodeColumnTokenDefault InsertionActivityColumn = new CoreCodeColumnTokenDefault( "ats.column.insertionActivity", "Insertion Activity", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion Activity specified by related Work Package.  (I) if inherited from parent.");
   public static CoreCodeColumnTokenDefault InsertionColumn = new CoreCodeColumnTokenDefault("ats.column.insertion", "Insertion", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static CoreCodeColumnTokenDefault ParentTitleColumn = new CoreCodeColumnTokenDefault("ats.column.parentTitle", "Parent Title", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static CoreCodeColumnTokenDefault PercentCompleteTasksColumn = new CoreCodeColumnTokenDefault("ats.column.taskPercentComplete", "Percent Complete Tasks", 80, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static CoreCodeColumnTokenDefault PointsColumn = new CoreCodeColumnTokenDefault("ats.column.points", "Points", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static CoreCodeColumnTokenDefault PriorityColumn = new CoreCodeColumnTokenDefault("ats.column.priority", AtsAttributeTypes.Priority);
   public static CoreCodeColumnTokenDefault PrIdColumn = new CoreCodeColumnTokenDefault("ats.column.prId", "PR ID", 20, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault DerivedFromTaskColumn = new CoreCodeColumnTokenDefault("ats.column.derived.from.task", "Derived From Task", 200, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, "Show related task workflows was created from");
   public static CoreCodeColumnTokenDefault DerivedWorkflowColumn = new CoreCodeColumnTokenDefault("ats.column.derived.workflow", "Derived Workflow", 200, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, "Show related Team Workflow, if created");
   public static CoreCodeColumnTokenDefault RelatedToStateColumn = new CoreCodeColumnTokenDefault("ats.column.relatedToState", AtsAttributeTypes.RelatedToState);
   public static CoreCodeColumnTokenDefault ReviewedByAndDateColumn = new CoreCodeColumnTokenDefault(AtsColumnUtil.COLUMN_NAMESPACE + "." + AtsAttributeTypes.ReviewedBy.getId() + "." + AtsAttributeTypes.ReviewedByDate.getId(), "Reviewed By and Date", 60, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, "");
   public static CoreCodeColumnTokenDefault SprintColumn = new CoreCodeColumnTokenDefault("ats.column.sprint", "Sprint", 100, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault SprintOrderColumn = new CoreCodeColumnTokenDefault("ats.column.sprintOrder", "Sprint Order", 45, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "Order of item within displayed sprint.  Editing this field changes order.");
   public static CoreCodeColumnTokenDefault StateColumn = new CoreCodeColumnTokenDefault("ats.column.state", "State", AtsAttributeTypes.CurrentStateName);
   public static CoreCodeColumnTokenDefault TargetedVersionColumn = new CoreCodeColumnTokenDefault("ats.column.versionTarget", "Targeted Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Date this workflow transitioned to the Completed state.");
   public static CoreCodeColumnTokenDefault TaskPointsColumn = new CoreCodeColumnTokenDefault("ats.column.taskPoints", "Task Points", 50, ColumnType.Float, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault TaskRiskFactorsColumn = new CoreCodeColumnTokenDefault("ats.column.taskRiskFactors", "Task Risk Factors", 100, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "");
   public static CoreCodeColumnTokenDefault TaskToRelatedArtifactTypeColumnToken = new CoreCodeColumnTokenDefault("ats.column.taskToRelArtType", "Task To Related Artifact Type", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Shows the Artifact Type of the Related Artifact");
   public static CoreCodeColumnTokenDefault TeamColumn = new CoreCodeColumnTokenDefault("ats.column.team", "Team", 50, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Team that has been assigned to work this Action.");
   public static CoreCodeColumnTokenDefault TitleColumn = new CoreCodeColumnTokenDefault("framework.artifact.name.Title", CoreAttributeTypes.Name);
   public static CoreCodeColumnTokenDefault TypeColumn = new CoreCodeColumnTokenDefault("ats.column.type", "Type", 150, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static CoreCodeColumnTokenDefault WorkDefinitionColumn = new CoreCodeColumnTokenDefault("ats.column.workDefinition", "Work Definition", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);

   // @formatter:on

   public static class CoreAttrTokColTokenDefault extends AtsCoreAttrTokColumnToken {

      public CoreAttrTokColTokenDefault(AttributeTypeToken attrType) {
         super(attrType);
         if (!hintsSet) {
            // Mark Core Attr Types with correct AtsDisplayHints; Can't do at type definition due to dependency order
            CoreAttributeTypes.AccessContextId.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.Annotation.addDisplayHint(AtsDisplayHint.Edit);
            CoreAttributeTypes.BranchDiffData.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.ContentUrl.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.Description.addDisplayHint(AtsDisplayHint.Edit);
            CoreAttributeTypes.GitBranchName.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitBuildId.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitChangeId.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitCommitAuthorDate.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitCommitMessage.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitCommitSha.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.GitRepoName.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.Name.addDisplayHint(AtsDisplayHint.Edit);
            CoreAttributeTypes.Notes.addDisplayHint(AtsDisplayHint.Edit);
            CoreAttributeTypes.Partition.addDisplayHint(AtsDisplayHint.Edit);
            CoreAttributeTypes.RelationOrder.addDisplayHint(AtsDisplayHint.Read);
            CoreAttributeTypes.StaticId.addDisplayHint(AtsDisplayHint.Read);
            hintsSet = true;
         }
      }

   }

   public static class CoreCodeColumnTokenDefault extends AtsCoreCodeColumnToken {

      public CoreCodeColumnTokenDefault(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
         MultiEdit multi, String description) {
         super(columnId, name, width, columnType, align, show, multi, ActionRollup.No, InheritParent.No, description);
      }

      public CoreCodeColumnTokenDefault(String columnId, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
         MultiEdit multi, ActionRollup actionRollup, InheritParent inheritParent, String description) {
         super(columnId, name, width, columnType, align, show, multi, description);
      }

      public CoreCodeColumnTokenDefault(String columnId, AttributeTypeToken attrType) {
         super(columnId, attrType);
      }

      public CoreCodeColumnTokenDefault(String columnId, String name, AttributeTypeString attrType) {
         super(columnId, name, attrType);
      }

      public CoreCodeColumnTokenDefault(String columnId, String name, int width, AttributeTypeString attrType) {
         super(columnId, name, width, attrType);
      }

   }

   public static Map<String, AtsCoreAttrTokColumnToken> getIdToAttrValCol() {
      return idToAttrValCol;
   }

}
