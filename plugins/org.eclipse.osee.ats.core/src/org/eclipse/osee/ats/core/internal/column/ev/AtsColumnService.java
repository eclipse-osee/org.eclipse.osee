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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
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
import org.eclipse.osee.ats.core.column.CancelledByColumn;
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.core.column.CompletedByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.core.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.ats.core.column.IAtsColumnProvider;
import org.eclipse.osee.ats.core.column.ImplementerColumn;
import org.eclipse.osee.ats.core.column.InsertionActivityColumn;
import org.eclipse.osee.ats.core.column.InsertionColumn;
import org.eclipse.osee.ats.core.column.PercentCompleteTasksColumn;
import org.eclipse.osee.ats.core.column.SprintOrderColumn;
import org.eclipse.osee.ats.core.column.StateColumn;
import org.eclipse.osee.ats.core.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.eclipse.osee.ats.core.column.TitleColumn;
import org.eclipse.osee.ats.core.column.TypeColumn;
import org.eclipse.osee.ats.core.column.UuidColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   public static final String CELL_ERROR_PREFIX = "!Error";
   private Map<String, IAtsColumn> columnIdToAtsColumn;
   private final IAtsServices services;

   public AtsColumnService(IAtsServices services) {
      this.services = services;
   }

   @Override
   public IAtsColumn getColumn(String id) {
      if (columnIdToAtsColumn == null) {
         columnIdToAtsColumn = new HashMap<String, IAtsColumn>();
      }

      // Get from cache
      IAtsColumn column = columnIdToAtsColumn.get(id);
      if (column != null) {
         return column;
      }

      // Add from database configurations
      for (AtsAttributeValueColumn attrCol : services.getConfigurations().getViews().getAttrColumns()) {
         if (id.equals(attrCol.getId())) {
            column = new AtsAttributeValueColumnHandler(attrCol, services);
            break;
         }
      }

      // Add from coded columns; this will only happen once per column as they're cached after this check
      if (column == null) {
         if (id.equals(AtsColumnId.ActionableItem.getId())) {
            column = new ActionableItemsColumn(services);
         } else if (id.equals(AtsColumnId.LegacyPcrId.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.LegacyPcrIdColumn, services);
         } else if (id.equals(AtsColumnId.Team.getId())) {
            column = new TeamColumn(services);
         } else if (id.equals(AtsColumnId.Assignees.getId())) {
            column = new AssigneeColumn(services);
         } else if (id.equals(AtsColumnId.AtsId.getId())) {
            column = new AtsIdColumn(services);
         } else if (id.equals(AtsColumnId.ActivityId.getId())) {
            column = new WorkPackageColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.Implementers.getId())) {
            column = new ImplementerColumn(services);
         } else if (id.equals(AtsColumnId.ChangeType.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.ChangeTypeColumn, services);
         } else if (id.equals(AtsColumnId.CreatedDate.getId())) {
            column = new CreatedDateColumn(services);
         } else if (id.equals(AtsColumnId.CompletedDate.getId())) {
            column = new CompletedDateColumn(services);
         } else if (id.equals(AtsColumnId.CancelledDate.getId())) {
            column = new CancelledDateColumn(services);
         } else if (id.equals(AtsColumnId.CancelledBy.getId())) {
            column = new CancelledByColumn(services);
         } else if (id.equals(AtsColumnId.CompletedBy.getId())) {
            column = new CompletedByColumn(services);
         } else if (id.equals(AtsColumnId.CompletedCancelledBy.getId())) {
            column = new CompletedCancelledByColumn(services);
         } else if (id.equals(AtsColumnId.CompletedCancelledDate.getId())) {
            column = new CompletedCancelledDateColumn(services);
         } else if (id.equals(AtsColumnId.Notes.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.NotesColumn, services);
         } else if (id.equals(AtsColumnId.State.getId())) {
            column = new StateColumn(services);
         } else if (id.equals(AtsColumnId.Name.getId()) || id.equals(AtsColumnId.Title.getId())) {
            column = new TitleColumn(services);
         } else if (id.equals(AtsColumnId.Type.getId())) {
            column = new TypeColumn(services);
         } else if (id.equals(AtsColumnId.PercentCompleteWorkflow.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.PercentCompleteWorkflowColumn, services);
         } else if (id.equals(AtsColumnId.PercentCompleteTasks.getId())) {
            column = new PercentCompleteTasksColumn(services);
         } else if (id.equals(AtsColumnId.Uuid.getId())) {
            column = new UuidColumn(services);
         } else if (id.equals(AtsColumnId.Priority.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.PriorityColumn, services);
         } else if (id.equals(AtsColumnId.WorkPackageName.getId())) {
            column = new WorkPackageNameColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageId.getId())) {
            column = new WorkPackageIdColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageType.getId())) {
            column = new WorkPackageTypeColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageProgram.getId())) {
            column = new WorkPackageProgramColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageGuid.getId())) {
            column = new WorkPackageGuidColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.State.getId())) {
            column = new StateColumn(services);
         } else if (id.equals(AtsColumnId.Uuid.getId())) {
            column = new UuidColumn(services);
         } else if (id.equals(AtsColumnId.Insertion.getId())) {
            column = new InsertionColumn(services);
         } else if (id.equals(AtsColumnId.InsertionActivity.getId())) {
            column = new InsertionActivityColumn(services);
         } else if (id.equals(AtsColumnId.TargetedVersion.getId())) {
            column = new TargetedVersionColumn(services);
         } else if (id.equals(AtsColumnId.UnPlannedWork.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnToken.UnPlannedWorkColumn, services);
         } else if (id.equals(AtsColumnId.SprintOrder.getId())) {
            column = new SprintOrderColumn(services);
         } else if (id.equals(AtsColumnId.AgileTeamPoints.getId())) {
            column = new AgileTeamPointsColumn(services);
         } else if (id.equals(AtsColumnId.CompletedCancelledDate.getId())) {
            column = new CompletedCancelledDateColumn(services);
         } else if (id.equals(AtsColumnId.AgileFeatureGroup.getId())) {
            column = new AgileFeatureGroupColumn(services);
         } else if (id.equals(AtsColumnId.TaskToRelatedArtifactType.getId())) {
            column = new TaskRelatedArtifactTypeColumn(services);
         }
      }
      // Add columns provided through OSGI services
      if (column == null) {
         for (IAtsColumnProvider provider : AtsColumnProviderCollector.getColumnProviders()) {
            column = provider.getColumn(id, services);
            if (column != null) {
               break;
            }
         }
      }
      // Add columns defined as attribute, if valid attribute
      if (column == null) {
         if (id.startsWith("attribute.")) {
            AttributeTypeId attrType = services.getStoreService().getAttributeType(id.replaceFirst("attribute\\.", ""));
            if (attrType != null) {
               column = new AttributeColumn(services, attrType);
            }
         }
         if (id.startsWith("ats.")) {
            AttributeTypeId attrType = services.getStoreService().getAttributeType(id);
            if (attrType != null) {
               column = new AttributeColumn(services, attrType);
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