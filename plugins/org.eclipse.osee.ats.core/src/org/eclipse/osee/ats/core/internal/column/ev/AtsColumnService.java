/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column.ev;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.column.IAtsColumnId;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.core.column.ActionableItemsColumn;
import org.eclipse.osee.ats.core.column.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.core.column.AgileTeamPointsColumn;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.column.AtsAttributeValueColumnHandler;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.column.AtsIdColumn;
import org.eclipse.osee.ats.core.column.AttributeColumn;
import org.eclipse.osee.ats.core.column.CancelReasonColumn;
import org.eclipse.osee.ats.core.column.CancelledByColumn;
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.core.column.CancelledReasonColumn;
import org.eclipse.osee.ats.core.column.CancelledReasonDetailsColumn;
import org.eclipse.osee.ats.core.column.CompletedByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.core.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.ats.core.column.FoundInVersionColumn;
import org.eclipse.osee.ats.core.column.IAtsColumnProvider;
import org.eclipse.osee.ats.core.column.IdColumn;
import org.eclipse.osee.ats.core.column.ImplementerColumn;
import org.eclipse.osee.ats.core.column.InsertionActivityColumn;
import org.eclipse.osee.ats.core.column.InsertionColumn;
import org.eclipse.osee.ats.core.column.ParentTitleColumn;
import org.eclipse.osee.ats.core.column.PercentCompleteTasksColumn;
import org.eclipse.osee.ats.core.column.SprintOrderColumn;
import org.eclipse.osee.ats.core.column.StateColumn;
import org.eclipse.osee.ats.core.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.eclipse.osee.ats.core.column.TitleColumn;
import org.eclipse.osee.ats.core.column.TypeColumn;
import org.eclipse.osee.ats.core.internal.column.WorkDefinitionColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   public static final String CELL_ERROR_PREFIX = "!Error";
   private Map<String, IAtsColumn> columnIdToAtsColumn;
   private final AtsApi atsApi;

   public AtsColumnService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsColumn getColumn(String id) {
      if (columnIdToAtsColumn == null) {
         columnIdToAtsColumn = new HashMap<>();
      }

      // Get from cache
      IAtsColumn column = columnIdToAtsColumn.get(id);
      if (column != null) {
         return column;
      }

      // Add from database configurations
      for (AtsAttributeValueColumn attrCol : atsApi.getConfigService().getConfigurations().getViews().getAttrColumns()) {
         if (id.equals(attrCol.getId())) {
            column = new AtsAttributeValueColumnHandler(attrCol, atsApi);
            break;
         }
      }

      // Add from coded columns; this will only happen once per column as they're cached after this check
      if (column == null) {
         if (id.equals(AtsColumnId.ActionableItem.getId())) {
            column = new ActionableItemsColumn(atsApi);
         } else if (id.equals(AtsColumnId.LegacyPcrId.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.LegacyPcrIdColumn, atsApi);
         } else if (id.equals(AtsColumnId.Team.getId())) {
            column = new TeamColumn(atsApi);
         } else if (id.equals(AtsColumnId.Assignees.getId())) {
            column = new AssigneeColumn(atsApi);
         } else if (id.equals(AtsColumnId.AtsId.getId())) {
            column = new AtsIdColumn(atsApi);
         } else if (id.equals(AtsColumnId.ActivityId.getId())) {
            column = new WorkPackageColumn(atsApi.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.Implementers.getId())) {
            column = new ImplementerColumn(atsApi);
         } else if (id.equals(AtsColumnId.ChangeType.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.ChangeTypeColumn, atsApi);
         } else if (id.equals(AtsColumnId.CreatedDate.getId())) {
            column = new CreatedDateColumn(atsApi);
         } else if (id.equals(AtsColumnId.CompletedDate.getId())) {
            column = new CompletedDateColumn(atsApi);
         } else if (id.equals(AtsColumnId.CancelledDate.getId())) {
            column = new CancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnId.CancelledBy.getId())) {
            column = new CancelledByColumn(atsApi);
         } else if (id.equals(AtsColumnId.CancelledReason.getId())) {
            column = new CancelledReasonColumn(atsApi);
         } else if (id.equals(AtsColumnId.CancelReason.getId())) {
            column = new CancelReasonColumn(atsApi);
         } else if (id.equals(AtsColumnId.CancelledReasonDetails.getId())) {
            column = new CancelledReasonDetailsColumn(atsApi);
         } else if (id.equals(AtsColumnId.CompletedBy.getId())) {
            column = new CompletedByColumn(atsApi);
         } else if (id.equals(AtsColumnId.CompletedCancelledBy.getId())) {
            column = new CompletedCancelledByColumn(atsApi);
         } else if (id.equals(AtsColumnId.CompletedCancelledDate.getId())) {
            column = new CompletedCancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnId.Notes.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.NotesColumn, atsApi);
         } else if (id.equals(AtsColumnId.State.getId())) {
            column = new StateColumn(atsApi);
         } else if (id.equals(AtsColumnId.Name.getId()) || id.equals(AtsColumnId.Title.getId())) {
            column = new TitleColumn(atsApi);
         } else if (id.equals(AtsColumnId.Type.getId())) {
            column = new TypeColumn(atsApi);
         } else if (id.equals(AtsColumnId.PercentCompleteWorkflow.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.PercentCompleteWorkflowColumn, atsApi);
         } else if (id.equals(AtsColumnId.PercentCompleteTasks.getId())) {
            column = new PercentCompleteTasksColumn(atsApi);
         } else if (id.equals(AtsColumnId.Id.getId())) {
            column = new IdColumn(atsApi);
         } else if (id.equals(AtsColumnId.Priority.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.PriorityColumn, atsApi);
         } else if (id.equals(AtsColumnId.WorkDefinition.getId())) {
            column = new WorkDefinitionColumn(atsApi);
         } else if (id.equals(AtsColumnId.WorkPackageName.getId())) {
            column = new WorkPackageNameColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnId.WorkPackageId.getId())) {
            column = new WorkPackageIdColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnId.WorkPackageType.getId())) {
            column = new WorkPackageTypeColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnId.WorkPackageProgram.getId())) {
            column = new WorkPackageProgramColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnId.State.getId())) {
            column = new StateColumn(atsApi);
         } else if (id.equals(AtsColumnId.Id.getId())) {
            column = new IdColumn(atsApi);
         } else if (id.equals(AtsColumnId.Insertion.getId())) {
            column = new InsertionColumn(atsApi);
         } else if (id.equals(AtsColumnId.InsertionActivity.getId())) {
            column = new InsertionActivityColumn(atsApi);
         } else if (id.equals(AtsColumnId.TargetedVersion.getId())) {
            column = new TargetedVersionColumn(atsApi);
         } else if (id.equals(AtsColumnId.FoundInVersion.getId())) {
            column = new FoundInVersionColumn(atsApi);
         } else if (id.equals(AtsColumnId.UnPlannedWork.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.UnPlannedWorkColumn, atsApi);
         } else if (id.equals(AtsColumnId.SprintOrder.getId())) {
            column = new SprintOrderColumn(atsApi);
         } else if (id.equals(AtsColumnId.AgileTeamPoints.getId())) {
            column = new AgileTeamPointsColumn(atsApi);
         } else if (id.equals(AtsColumnId.CompletedCancelledDate.getId())) {
            column = new CompletedCancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnId.AgileFeatureGroup.getId())) {
            column = new AgileFeatureGroupColumn(atsApi);
         } else if (id.equals(AtsColumnId.TaskToRelatedArtifactType.getId())) {
            column = new TaskRelatedArtifactTypeColumn(atsApi);
         } else if (id.equals(AtsColumnId.ParentTitle.getId())) {
            column = new ParentTitleColumn(atsApi);
         }
      }
      // Add columns provided through OSGI services
      if (column == null) {
         for (IAtsColumnProvider provider : AtsColumnProviderCollector.getColumnProviders()) {
            column = provider.getColumn(id, atsApi);
            if (column != null) {
               break;
            }
         }
      }
      // Add columns defined as attribute, if valid attribute
      if (column == null) {
         if (id.startsWith("attribute.")) {
            AttributeTypeToken attrType =
               atsApi.getStoreService().getAttributeType(id.replaceFirst("attribute\\.", ""));
            column = new AttributeColumn(atsApi, attrType);

         }
         if (id.startsWith("ats.")) {
            try {
               AttributeTypeToken attrType = atsApi.getStoreService().getAttributeType(id);
               if (attrType != null) {
                  column = new AttributeColumn(atsApi, attrType);
               }
            } catch (OseeTypeDoesNotExist ex) {
               // do nothing
            }
         }
      }
      // Add to cache even if not found so don't need to look again
      add(id, column);
      return column;
   }

   @Override
   public String getColumnText(IAtsColumnId column, IAtsObject atsObject) {
      return getColumnText(column.getId(), atsObject);
   }

   @Override
   public String getColumnText(String id, IAtsObject atsObject) {
      String result = "";
      IAtsColumn column = getColumn(id);
      if (column == null) {
         result = "column not supported";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, IAtsColumnId column, IAtsObject atsObject) {
      return getColumnText(configurations, column.getId(), atsObject);
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject) {
      String result = "";
      IAtsColumn column = getColumn(id);
      if (column == null) {
         result = "column not supported";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public void add(String id, IAtsColumn column) {
      columnIdToAtsColumn.put(id, column);
   }

   @Override
   public IAtsColumn getColumn(IAtsColumnId columnId) {
      return getColumn(columnId.getId());
   }

}