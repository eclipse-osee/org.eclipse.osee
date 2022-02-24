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

import org.eclipse.osee.ats.api.config.ActionRollup;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.config.InheritParent;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColumnType;
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
public class AtsColumnTokens {

   public static final String WorldXViewerFactory_COLUMN_NAMESPACE = "ats.column";

   /**
    * ColumnId columns are basic columns providing simple or computed data. They are NOT backed directly by an
    * attribute, usually don't change, usually can't be changed by the user through edit or alt-left click.</br>
    * USE ATTRIBUTE VALUE COLUMNS below if possible!!
    */
   // @formatter:off

   public static AtsValColumn ActionableItemsColumn = new AtsValColumn("ats.column.actionableItems", "Actionable Item(s)", 80, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static AtsValColumn ActivityIdColumn = new AtsValColumn("ats.column.activityId", "Activy Id", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static AtsValColumn AgileFeatureGroupColumn = new AtsValColumn("ats.column.agileFeatureGroup", "Feature Group", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Agile Feature Group for this Item");
   public static AtsValColumn AgileTeamPointsColumn = new AtsValColumn("ats.agileTeam.Points", "Agile Points", 40, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "Points as defined by AgileTeam configuration.  Either ats.Points or ats.Points Numeric");
   public static AtsValColumn AssigneeColumn = new AtsValColumn("ats.column.assignees", "Assignees", 100, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsValColumn AtsIdColumn = new AtsValColumn("ats.id", "ATS Id", 75, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsValColumn AtsIdColumnShow = new AtsValColumn("ats.id", "ATS Id", 75, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsValColumn SiblingAtsIdsColumn = new AtsValColumn("ats.sibling.id", "Sibling ATS Ids", 75, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsValColumn CancelledByColumn = new AtsValColumn("ats.column.cancelledBy", "Cancelled By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to cancelled state.");
   public static AtsValColumn CancelledDateColumn = new AtsValColumn("ats.column.cancelledDate", "Cancelled Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsValColumn CancelledReason = new AtsValColumn("ats.column.cancelledReason", "Cancelled Reason", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Reason for cancelling action.");
   public static AtsValColumn CancelledReasonDetails = new AtsValColumn("ats.column.cancelledReasonDetails", "Cancelled Reason Details", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "More detailed explanation for cancelling action.");
   public static AtsValColumn CancelReason = new AtsValColumn("ats.column.cancelReason", "Cancelled Reason", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Reason for cancelling action.");
   public static AtsValColumn CompletedByColumn = new AtsValColumn("ats.column.completedBy", "Completed By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to completed state.");
   public static AtsValColumn CompletedCancelledByColumn = new AtsValColumn( "ats.column.cmpCnclBy", "Completed or Cancelled By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to completed or cancelled state.");
   public static AtsValColumn CompletedCancelledDateColumn = new AtsValColumn("ats.column.cmpCnclDate", "Completed or Cancelled Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Date action to completed or cancelled");
   public static AtsValColumn CompletedDateColumn = new AtsValColumn("ats.column.completedDate", "Completed Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsValColumn CreatedDateColumn = new AtsValColumn("ats.column.createdDate", "Created Date", 80, ColumnType.Date, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Date this workflow was created.");
   public static AtsValColumn FoundInVersionColumn = new AtsValColumn("ats.column.foundInVersion", "Found In Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Release version in which software issue occured.");
   public static AtsValColumn IdColumn = new AtsValColumn("framework.id", "Id", 30, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static AtsValColumn ImplementersColumn = new AtsValColumn("ats.column.implementer", "Implementer(s)", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static AtsValColumn IncorporatedInColumn = new AtsValColumn("ats.column.incorporated.in", "Incorporated In", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "");
   public static AtsValColumn InsertionActivityColumn = new AtsValColumn( "ats.column.insertionActivity", "Insertion Activity", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion Activity specified by related Work Package.  (I) if inherited from parent.");
   public static AtsValColumn InsertionColumn = new AtsValColumn("ats.column.insertion", "Insertion", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static AtsValColumn ParentTitleColumn = new AtsValColumn("ats.column.parentTitle", "Parent Title", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn PercentCompleteTasksColumn = new AtsValColumn("ats.column.taskPercentComplete", "Percent Complete Tasks", 80, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static AtsValColumn PointsColumn = new AtsValColumn("ats.column.points", "Points", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static AtsValColumn ReleaseDateColumn = new AtsValColumn("ats.column.releaseDate", "Release Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static AtsValColumn SprintOrderColumn = new AtsValColumn("ats.column.sprintOrder", "Sprint order", 45, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "Order of item within displayed sprint.  Editing this field changes order.");
   public static AtsValColumn StateColumn = new AtsValColumn("ats.column.state", "State", 75, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsValColumn TargetedVersionColumn = new AtsValColumn("ats.column.versionTarget", "Targeted Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Date this workflow transitioned to the Completed state.");
   public static AtsValColumn TaskToRelatedArtifactTypeColumnToken = new AtsValColumn("ats.column.taskToRelArtType", "Task To Related Artifact Type", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Shows the Artifact Type of the Related Artifact");
   public static AtsValColumn TeamColumn = new AtsValColumn("ats.column.team", "Team", 50, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Team that has been assigned to work this Action.");
   public static AtsValColumn TypeColumn = new AtsValColumn("ats.column.type", "Type", 150, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn WorkDefinitionColumn = new AtsValColumn("ats.column.workDefinition", "Work Definition", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn WorkPackageIdColumn = new AtsValColumn("ats.column.workPackageId", "Work Package Id", 50, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn WorkPackageNameColumn = new AtsValColumn("ats.column.workPackageName", "Work Package Name", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn WorkPackageProgramColumn = new AtsValColumn("ats.column.workPackageProgram", "Work Package Program", 50, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsValColumn WorkPackageTypeColumn = new AtsValColumn("ats.column.workPackageType", "Work Package Type", 50, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);

   // @formatter:on

   /**
    * Attribute Value columns. These are backed directly by an attribute with the possible ability to change with edit
    * or alt-left-click. USE THIS IF POSSIBLE!!!
    */
   // @formatter:off

   public static AtsAttrValCol ChangeTypeColumn = new AtsAttrValCol(AtsAttributeTypes.ChangeType, "ats.column.changetype", AtsAttributeTypes.ChangeType.getUnqualifiedName(), 22, ColumnType.String, ColumnAlign.Center, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "");
   public static AtsAttrValCol LegacyPcrIdColumn = new AtsAttrValCol(AtsAttributeTypes.LegacyPcrId, "ats.column.legacyPcr", AtsAttributeTypes.LegacyPcrId.getUnqualifiedName(), 40, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "");
   public static AtsAttrValCol NotesColumn = new AtsAttrValCol(AtsAttributeTypes.WorkflowNotes, "ats.column.notes", "Notes", 80, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol PercentCompleteWorkflowColumn = new AtsAttrValCol(AtsAttributeTypes.PercentComplete, "ats.column.workflowPercentComplete", "Workflow Percent Complete", 40, ColumnType.Percent, ColumnAlign.Center, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Percent Complete for full workflow (if work definition configured for single percent).\n\nAmount entered from user.");
   public static AtsAttrValCol PriorityColumn = new AtsAttrValCol(AtsAttributeTypes.Priority, "ats.column.priority", AtsAttributeTypes.Priority.getUnqualifiedName(), 20, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsAttrValCol TitleColumn = new AtsAttrValCol(CoreAttributeTypes.Name, "framework.artifact.name.Title", "Title", 150, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsAttrValCol NameColumn = new AtsAttrValCol(CoreAttributeTypes.Name, "framework.artifact.name", "Name", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsAttrValCol UnPlannedWorkColumn = new AtsAttrValCol(AtsAttributeTypes.UnplannedWork, "ats.Unplanned Work", AtsAttributeTypes.UnplannedWork.getUnqualifiedName(), 20, ColumnType.Boolean, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol CrashOrBlankDisplayColumn = new AtsAttrValCol(AtsAttributeTypes.CrashOrBlankDisplay, "ats.column.crash.or.blank.display", AtsAttributeTypes.CrashOrBlankDisplay.getUnqualifiedName(), 50, ColumnType.Boolean, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol ExternalReferenceColumn = new AtsAttrValCol(AtsAttributeTypes.ExternalReference, "ats.column.external.reference", AtsAttributeTypes.ExternalReference.getUnqualifiedName(), 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol GitChangeId = new AtsAttrValCol(CoreAttributeTypes.GitChangeId, "ats.column.git.change.id", CoreAttributeTypes.GitChangeId.getUnqualifiedName(), 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol RevisitDate = new AtsAttrValCol(AtsAttributeTypes.RevisitDate, "ats.column.revisit.date", AtsAttributeTypes.RevisitDate.getUnqualifiedName(), 75, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol NonFunctionalProblem = new AtsAttrValCol(AtsAttributeTypes.NonFunctionalProblem, "ats.column.non.functional.problem", AtsAttributeTypes.NonFunctionalProblem.getUnqualifiedName(), 55, ColumnType.Boolean, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");

   // @formatter:on
}
