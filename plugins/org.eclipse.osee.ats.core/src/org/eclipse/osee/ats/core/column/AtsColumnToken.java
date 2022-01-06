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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
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
 * See {@link AtsColumnId} javadoc for architecture/design
 *
 * @author Donald G. Dunne
 */
public class AtsColumnToken {

   public static final String WorldXViewerFactory_COLUMN_NAMESPACE = "ats.column";

   /**
    * ColumnId columns are basic columns providing simple or computed data. They are NOT backed directly by an
    * attribute, usually don't change, usually can't be changed by the user through edit or alt-left click.</br>
    * USE ATTRIBUTE VALUE COLUMNS below if possible!!
    */
   // @formatter:off

   public static AtsColumnIdValueColumn ActionableItemsColumn = new AtsColumnIdValueColumn(AtsColumnId.ActionableItem, AtsColumnId.ActionableItem.name(), 80, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "");
   public static AtsColumnIdValueColumn AgileFeatureGroupColumn = new AtsColumnIdValueColumn(AtsColumnId.AgileFeatureGroup, "Feature Group", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Agile Feature Group for this Item");
   public static AtsColumnIdValueColumn AgileTeamPointsColumn = new AtsColumnIdValueColumn(AtsColumnId.AgileTeamPoints, "Agile Points", 40, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "Points as defined by AgileTeam configuration.  Either ats.Points or ats.Points Numeric");
   public static AtsColumnIdValueColumn AssigneeColumn = new AtsColumnIdValueColumn(AtsColumnId.Assignees, AtsColumnId.Assignees.name(), 100, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsColumnIdValueColumn AtsIdColumn = new AtsColumnIdValueColumn(AtsColumnId.AtsId, "ATS Id", 75, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsColumnIdValueColumn AtsIdColumnShow = new AtsColumnIdValueColumn(AtsColumnId.AtsId, "ATS Id", 75, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsColumnIdValueColumn SiblingAtsIdsColumn = new AtsColumnIdValueColumn(AtsColumnId.SiblingAtsIds, "Sibling ATS Ids", 75, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "ATS ID");
   public static AtsColumnIdValueColumn CancelledByColumn = new AtsColumnIdValueColumn(AtsColumnId.CancelledBy, "Cancelled By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to cancelled state.");
   public static AtsColumnIdValueColumn CancelledDateColumn = new AtsColumnIdValueColumn(AtsColumnId.CancelledDate, "Cancelled Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsColumnIdValueColumn CancelledReason = new AtsColumnIdValueColumn(AtsColumnId.CancelledReason, "Cancelled Reason", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Reason for cancelling action.");
   public static AtsColumnIdValueColumn CancelledReasonDetails = new AtsColumnIdValueColumn(AtsColumnId.CancelledReasonDetails, "Cancelled Reason Details", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "More detailed explanation for cancelling action.");
   public static AtsColumnIdValueColumn CancelReason = new AtsColumnIdValueColumn(AtsColumnId.CancelReason, "Cancelled Reason", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Reason for cancelling action.");
   public static AtsColumnIdValueColumn CompletedByColumn = new AtsColumnIdValueColumn(AtsColumnId.CompletedBy, "Completed By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to completed state.");
   public static AtsColumnIdValueColumn CompletedCancelledByColumn = new AtsColumnIdValueColumn( AtsColumnId.CompletedCancelledBy, "Completed or Cancelled By", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "User transitioning action to completed or cancelled state.");
   public static AtsColumnIdValueColumn CompletedCancelledDateColumn = new AtsColumnIdValueColumn(AtsColumnId.CompletedCancelledDate, "Completed or Cancelled Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Date action to completed or cancelled");
   public static AtsColumnIdValueColumn CompletedDateColumn = new AtsColumnIdValueColumn(AtsColumnId.CompletedDate, "Completed Date", 80, ColumnType.Date, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsColumnIdValueColumn CreatedDateColumn = new AtsColumnIdValueColumn(AtsColumnId.CreatedDate, AtsColumnId.CreatedDate.name(), 80, ColumnType.Date, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Date this workflow was created.");
   public static AtsColumnIdValueColumn InsertionActivityColumn = new AtsColumnIdValueColumn( AtsColumnId.InsertionActivity, "Insertion Activity", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion Activity specified by related Work Package.  (I) if inherited from parent.");
   public static AtsColumnIdValueColumn InsertionColumn = new AtsColumnIdValueColumn(AtsColumnId.Insertion, "Insertion", 80, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Insertion specified by related Work Package.  (I) if inherited from parent.");
   public static AtsColumnIdValueColumn SprintOrderColumn = new AtsColumnIdValueColumn(AtsColumnId.SprintOrder, "Sprint order", 45, ColumnType.Integer, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "Order of item within displayed sprint.  Editing this field changes order.");
   public static AtsColumnIdValueColumn StateColumn = new AtsColumnIdValueColumn(AtsColumnId.State, "State", 75, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.No, null);
   public static AtsColumnIdValueColumn TargetedVersionColumn = new AtsColumnIdValueColumn(AtsColumnId.TargetedVersion, "Targeted Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Date this workflow transitioned to the Completed state.");
   public static AtsColumnIdValueColumn FoundInVersionColumn = new AtsColumnIdValueColumn(AtsColumnId.FoundInVersion, "Found In Version", 40, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "Release version in which software issue occured.");
   public static AtsColumnIdValueColumn TaskToRelatedArtifactTypeColumnToken = new AtsColumnIdValueColumn( AtsColumnId.TaskToRelatedArtifactType, "Task To Related Artifact Type", 110, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, "Shows the Artifact Type of the Related Artifact");
   public static AtsColumnIdValueColumn TeamColumn = new AtsColumnIdValueColumn(AtsColumnId.Team, "Team", 50, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "Team that has been assigned to work this Action.");
   public static AtsColumnIdValueColumn TypeColumn = new AtsColumnIdValueColumn(AtsColumnId.Type, "Type", 150, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsColumnIdValueColumn WorkDefinitionColumn = new AtsColumnIdValueColumn(AtsColumnId.WorkDefinition, "Work Definition", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);
   public static AtsColumnIdValueColumn ParentTitleColumn = new AtsColumnIdValueColumn(AtsColumnId.ParentTitle, "Parent Title", 150, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.No, InheritParent.No, null);

   // @formatter:on

   /**
    * Attribute Value columns. These are backed directly by an attribute with the possible ability to change with edit
    * or alt-left-click. USE THIS IF POSSIBLE!!!
    */
   // @formatter:off

   public static AtsAttrValCol ChangeTypeColumn = new AtsAttrValCol(AtsAttributeTypes.ChangeType, AtsColumnId.ChangeType.getId(), AtsAttributeTypes.ChangeType.getUnqualifiedName(), 22, ColumnType.String, ColumnAlign.Center, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.Yes, "");
   public static AtsAttrValCol LegacyPcrIdColumn = new AtsAttrValCol(AtsAttributeTypes.LegacyPcrId, AtsColumnId.LegacyPcrId.getId(), AtsAttributeTypes.LegacyPcrId.getUnqualifiedName(), 40, ColumnType.String, ColumnAlign.Left, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.Yes, "");
   public static AtsAttrValCol NotesColumn = new AtsAttrValCol(AtsAttributeTypes.WorkflowNotes, AtsColumnId.Notes.getId(), "Notes", 80, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol PercentCompleteWorkflowColumn = new AtsAttrValCol(AtsAttributeTypes.PercentComplete, AtsColumnId.PercentCompleteWorkflow.getId(), "Workflow Percent Complete", 40, ColumnType.Percent, ColumnAlign.Center, Show.No, MultiEdit.No, ActionRollup.Yes, InheritParent.No, "Percent Complete for full workflow (if work definition configured for single percent).\n\nAmount entered from user.");
   public static AtsAttrValCol PriorityColumn = new AtsAttrValCol(AtsAttributeTypes.Priority, AtsColumnId.Priority.getId(), AtsAttributeTypes.Priority.getUnqualifiedName(), 20, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsAttrValCol TitleColumn = new AtsAttrValCol(CoreAttributeTypes.Name, "framework.artifact.name.Title", "Title", 150, ColumnType.String, ColumnAlign.Left, Show.Yes, MultiEdit.Yes, ActionRollup.Yes, InheritParent.No, "");
   public static AtsAttrValCol UnPlannedWorkColumn = new AtsAttrValCol(AtsAttributeTypes.UnplannedWork, AtsColumnId.UnPlannedWork.getId(), AtsAttributeTypes.UnplannedWork.getUnqualifiedName(), 20, ColumnType.Boolean, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");
   public static AtsAttrValCol CrashOrBlankDisplay = new AtsAttrValCol(AtsAttributeTypes.CrashOrBlankDisplay, AtsColumnId.CrashOrBlankDisplay.getId(), AtsAttributeTypes.CrashOrBlankDisplay.getUnqualifiedName(), 50, ColumnType.Boolean, ColumnAlign.Left, Show.No, MultiEdit.Yes, ActionRollup.No, InheritParent.No, "");

   // @formatter:on
}
