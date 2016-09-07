/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnToken {

   // ColumnId columns
   public static AtsColumnIdValueColumn AtsIdColumn = new AtsColumnIdValueColumn(AtsColumnId.AtsId,
      AtsColumnId.AtsId.name(), 75, ColumnAlign.Left.name(), false, ColumnType.String, false, "ATS ID", false, false);
   public static AtsColumnIdValueColumn AtsIdColumnShow = new AtsColumnIdValueColumn(AtsColumnId.AtsId,
      AtsColumnId.AtsId.name(), 75, ColumnAlign.Left.name(), true, ColumnType.String, false, "ATS ID", false, false);
   public static AtsColumnIdValueColumn AssigneeColumn = new AtsColumnIdValueColumn(AtsColumnId.Assignees,
      AtsColumnId.Assignees.name(), 100, ColumnAlign.Left.name(), true, ColumnType.String, true, "", true, false);
   public static AtsColumnIdValueColumn ActionableItemsColumn = new AtsColumnIdValueColumn(AtsColumnId.ActionableItem,
      AtsColumnId.ActionableItem.name(), 80, ColumnAlign.Left.name(), true, ColumnType.String, false, "", true, false);
   public static AtsColumnIdValueColumn CreatedDateColumn =
      new AtsColumnIdValueColumn(AtsColumnId.CreatedDate, AtsColumnId.CreatedDate.name(), 80, ColumnAlign.Left.name(),
         true, ColumnType.Date, false, "Date this workflow was created.", true, false);
   public static AtsColumnIdValueColumn StateColumn = new AtsColumnIdValueColumn(AtsColumnId.State, "State", 75,
      ColumnAlign.Left.name(), true, ColumnType.String, false, null, true, false);
   public static AtsColumnIdValueColumn TargtedVersionColumn =
      new AtsColumnIdValueColumn(AtsColumnId.TargetedVersion, "Targeted Version", 40, ColumnAlign.Left.name(), true,
         ColumnType.String, true, "Date this workflow transitioned to the Completed state.", true, true);
   public static AtsColumnIdValueColumn TypeColumn = new AtsColumnIdValueColumn(AtsColumnId.Type, "Type", 150,
      ColumnAlign.Left.name(), true, ColumnType.String, false, null, false, false);
   public static AtsColumnIdValueColumn TeamColumn =
      new AtsColumnIdValueColumn(AtsColumnId.Team, "Team", 50, ColumnAlign.Left.name(), true, ColumnType.String, false,
         "Team that has been assigned to work this Action.", true, true);
   public static AtsColumnIdValueColumn InsertionActivityColumn = new AtsColumnIdValueColumn(
      AtsColumnId.InsertionActivity, "Insertion Activity", 80, ColumnAlign.Left.name(), false, ColumnType.String, false,
      "Insertion Activity specified by related Work Package.  (I) if inherited from parent.", true, true);
   public static AtsColumnIdValueColumn InsertionColumn = new AtsColumnIdValueColumn(AtsColumnId.Insertion, "Insertion",
      80, ColumnAlign.Left.name(), false, ColumnType.String, false,
      "Insertion specified by related Work Package.  (I) if inherited from parent.", true, true);

   // AttributeValue columns
   public static AtsAttributeValueColumn NotesColumn = new AtsAttributeValueColumn(AtsAttributeTypes.SmaNote,
      AtsColumnId.Notes.getId(), "Notes", 80, ColumnAlign.Left.name(), true, ColumnType.String, true, "", false, false);
   public static AtsAttributeValueColumn ChangeTypeColumn = new AtsAttributeValueColumn(AtsAttributeTypes.ChangeType,
      AtsColumnId.ChangeType.getId(), AtsAttributeTypes.ChangeType.getUnqualifiedName(), 22, ColumnAlign.Center.name(),
      true, ColumnType.String, true, "", true, true);
   public static AtsAttributeValueColumn PriorityColumn = new AtsAttributeValueColumn(AtsAttributeTypes.PriorityType,
      AtsColumnId.Priority.getId(), AtsAttributeTypes.PriorityType.getUnqualifiedName(), 20, ColumnAlign.Left.name(),
      true, ColumnType.String, true, "", true, false);
   public static AtsAttributeValueColumn LegacyPcrIdColumn = new AtsAttributeValueColumn(AtsAttributeTypes.LegacyPcrId,
      AtsColumnId.LegacyPcrId.getId(), AtsAttributeTypes.LegacyPcrId.getUnqualifiedName(), 40, ColumnAlign.Left.name(),
      false, ColumnType.String, false, "", true, true);
   public static AtsAttributeValueColumn TitleColumn =
      new AtsAttributeValueColumn(CoreAttributeTypes.Name, "framework.artifact.name.Title", "Title", 150,
         ColumnAlign.Left.name(), true, ColumnType.String, true, "", true, false);
   public static AtsAttributeValueColumn PercentCompleteWorkflowColumn =
      new AtsAttributeValueColumn(AtsAttributeTypes.PercentComplete, AtsColumnId.PercentCompleteWorkflow.getId(),
         "Workflow Percent Complete", 40, ColumnAlign.Center.name(), false, ColumnType.Percent, false,
         "Percent Complete for full workflow (if work definition configured for single percent).\n\nAmount entered from user.",
         true, false);
}
