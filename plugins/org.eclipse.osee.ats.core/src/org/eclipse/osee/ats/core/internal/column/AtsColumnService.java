/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.column;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsColumnUtil;
import org.eclipse.osee.ats.api.column.AtsCoreAttrTokColumnToken;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.api.column.IAtsColumnProvider;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.ats.core.column.ActionableItemsColumn;
import org.eclipse.osee.ats.core.column.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.core.column.AgileTeamPointsColumn;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.column.AtsIdColumn;
import org.eclipse.osee.ats.core.column.AttachmentsCountColumn;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.column.CheckColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.core.column.CrIdColumn;
import org.eclipse.osee.ats.core.column.DerivedFromAtsIdColumn;
import org.eclipse.osee.ats.core.column.DerivedFromTaskColumn;
import org.eclipse.osee.ats.core.column.DerivedFromTeamDefColumn;
import org.eclipse.osee.ats.core.column.DerivedWorkflowColumn;
import org.eclipse.osee.ats.core.column.FeatureImpactReferenceColumn;
import org.eclipse.osee.ats.core.column.FoundInVersionColumn;
import org.eclipse.osee.ats.core.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.core.column.IdColumn;
import org.eclipse.osee.ats.core.column.ImplementerColumn;
import org.eclipse.osee.ats.core.column.IncorporatedInColumn;
import org.eclipse.osee.ats.core.column.InsertionActivityColumn;
import org.eclipse.osee.ats.core.column.InsertionColumn;
import org.eclipse.osee.ats.core.column.ParentTitleColumn;
import org.eclipse.osee.ats.core.column.PercentCompleteTasksColumn;
import org.eclipse.osee.ats.core.column.PrIdColumn;
import org.eclipse.osee.ats.core.column.PriorityColumn;
import org.eclipse.osee.ats.core.column.SprintOrderColumn;
import org.eclipse.osee.ats.core.column.StateColumn;
import org.eclipse.osee.ats.core.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.column.TaskPointsColumn;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.core.column.TaskRiskFactorsColumn;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.eclipse.osee.ats.core.column.TitleColumn;
import org.eclipse.osee.ats.core.column.TypeColumn;
import org.eclipse.osee.ats.core.column.WorkDefinitionColumn;
import org.eclipse.osee.ats.core.column.model.AtsCoreAttrTokenColumn;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * See AtsColumnToken javadoc for architecture/design
 *
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   public static final String CELL_ERROR_PREFIX = "!Error";
   private Map<String, AtsCoreColumn> idToAtsColumn;
   private final AtsApi atsApi;
   private final Set<String> errorLogged = new HashSet<>();
   private final Set<AttributeTypeToken> attrTypeHintChecked = new HashSet<>();
   private XResultData rd = null;
   private Map<String, String> legacyIdToId;

   public AtsColumnService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public XResultData validateIdeColumns(List<XViewerColumn> ideColumns) {
      XResultData rd = new XResultData();
      ensureLoaded();
      for (XViewerColumn ideCol : ideColumns) {
         // will log error if not found
         atsApi.getColumnService().getColumn(ideCol.getId());
      }
      return rd;
   }

   public synchronized void ensureLoaded() {
      if (idToAtsColumn == null) {
         idToAtsColumn = new HashMap<>();
         rd = new XResultData();

         // These override what else is defined in Core
         loadCoreAtsConfigViewsColumns();

         loadCoreAtsColumns();

         loadCoreAtsProviderColumns();

         loadCoreAtsCodedColumns();

         loadRemainingAtsWorkflowAttributes();
      }
   }

   @Override
   public AtsCoreColumn getColumn(String id) {

      ensureLoaded();

      // Get from cache
      AtsCoreColumn column = idToAtsColumn.get(id);
      if (column == null) {
         // Only log error once
         if (!errorLogged.contains(id)) {
            errorLogged.add(id);
         }
      }

      return column;
   }

   private void loadRemainingAtsWorkflowAttributes() {
      loadRemainingAtsWorkflowAttributes("ats.");
   }

   @Override
   public void loadRemainingAtsWorkflowAttributes(String prefix) {
      loadRemainingAtsWorkflowAttributes(prefix, AtsArtifactTypes.AbstractWorkflowArtifact);
   }

   public void loadRemainingAtsWorkflowAttributes(String prefix, ArtifactTypeToken artType) {
      rd.logf("%s - Processing art type: %s\n", getClass().getSimpleName(), artType.toStringWithId());
      for (AttributeTypeToken attrType : artType.getValidAttributeTypes()) {
         if (attrTypeHintChecked.contains(attrType)) {
            continue;
         }
         try {
            if (attrType.hasDisplayHint(AtsDisplayHint.Read, AtsDisplayHint.Edit, AtsDisplayHint.ReadConfig)) {
               if (!idToAtsColumn.containsKey(attrType.getName())) {
                  AtsCoreAttrTokenColumn col = getAttributeColumn(attrType, "workflow attr types");
                  idToAtsColumn.put(col.getId(), col);
               }
            } else {
               rd.error(String.format("Workflow Attribute Type %s must specify AtsDisplayHint.Edit or Read",
                  attrType.toStringWithId()));
            }
            if (attrType.hasDisplayHint(AtsDisplayHint.Config)) {
               rd.error(String.format("Workflow Attribute Type %s should not specify DisplayHint.Config",
                  attrType.toStringWithId()));
            }
         } catch (OseeTypeDoesNotExist ex) {
            rd.logf("Attribute Type %s Does Not Exist %s", attrType.toStringWithId(), Lib.exceptionToString(ex));
         }
         attrTypeHintChecked.add(attrType);
      }
      for (ArtifactTypeToken childArtType : artType.getAllDescendantTypes()) {
         loadRemainingAtsWorkflowAttributes(prefix, childArtType);
      }
   }

   private void loadCoreAtsCodedColumns() {
      registerCol(new ActionableItemsColumn(atsApi));
      registerCol(new AgileFeatureGroupColumn(atsApi));
      registerCol(new AgileTeamPointsColumn(atsApi));
      registerCol(new AssigneeColumn(atsApi));
      registerCol(new AtsIdColumn(atsApi));
      registerCol(new AttachmentsCountColumn(atsApi));
      registerCol(new ChangeTypeColumn(atsApi));
      registerCol(new CheckColumn(atsApi));
      registerCol(new CompletedCancelledByColumn(atsApi));
      registerCol(new CompletedCancelledDateColumn(atsApi));
      registerCol(new CrIdColumn(atsApi));
      registerCol(new DerivedFromAtsIdColumn(atsApi));
      registerCol(new DerivedFromTeamDefColumn(atsApi));
      registerCol(new DerivedFromTaskColumn(atsApi));
      registerCol(new DerivedWorkflowColumn(atsApi));
      registerCol(new FeatureImpactReferenceColumn(atsApi));
      registerCol(new FoundInVersionColumn(atsApi));
      registerCol(new HoursSpentTotalColumn(atsApi));
      registerCol(new IdColumn(atsApi));
      registerCol(new ImplementerColumn(atsApi));
      registerCol(new IncorporatedInColumn(atsApi));
      registerCol(new InsertionActivityColumn(atsApi));
      registerCol(new InsertionColumn(atsApi));
      registerCol(new ParentTitleColumn(atsApi));
      registerCol(new PercentCompleteTasksColumn(atsApi));
      registerCol(new PrIdColumn(atsApi));
      registerCol(new PriorityColumn(atsApi));
      registerCol(new SprintOrderColumn(atsApi));
      registerCol(new StateColumn(atsApi));
      registerCol(new TargetedVersionColumn(atsApi));
      registerCol(new TaskPointsColumn(atsApi));
      registerCol(new TaskRelatedArtifactTypeColumn(atsApi));
      registerCol(new TaskRiskFactorsColumn(atsApi));
      registerCol(new TeamColumn(atsApi));
      registerCol(new TitleColumn(atsApi));
      registerCol(new TypeColumn(atsApi));
      registerCol(new WorkDefinitionColumn(atsApi));
   }

   private void registerCol(AtsCoreCodeColumn col) {
      if (!idToAtsColumn.containsKey(col.getId())) {
         idToAtsColumn.put(col.getId(), col);
      }
   }

   private void loadCoreAtsColumns() {
      for (AtsCoreAttrTokColumnToken atsAttrCol : AtsColumnTokensDefault.getIdToAttrValCol().values()) {
         AtsCoreAttrTokenColumn col = new AtsCoreAttrTokenColumn(atsAttrCol, "code", AtsApiService.get());
         if (!idToAtsColumn.containsKey(atsAttrCol.getId())) {
            idToAtsColumn.put(atsAttrCol.getId(), col);
         }
      }
   }

   // Add from database AtsConfig artifact views= attribute
   private void loadCoreAtsConfigViewsColumns() {
      try {
         for (AtsCoreAttrTokColumnToken atsAttrCol : atsApi.getConfigService().getConfigurations().getViews().getAttrColumns()) {
            String msg = "";
            if (Strings.isInvalid(atsAttrCol.getName())) {
               msg = "AtsConfig.views col name invalid for " + atsAttrCol;
            } else if (Strings.isInvalid(atsAttrCol.getNamespace())) {
               msg = "AtsConfig.views col namespace invalid for " + atsAttrCol;
            } else if (Strings.isInvalid(atsAttrCol.getColumnType())) {
               msg = "AtsConfig.views col columnType invalid for " + atsAttrCol;
            } else if (atsAttrCol.getAttrTypeId() <= 0) {
               msg = "AtsConfig.views col attrTypeId <=0 for " + atsAttrCol;
            } else if (atsApi.tokenService().getAttributeTypeOrSentinel(atsAttrCol.getAttrTypeId()).isInvalid()) {
               msg = "AtsConfig.views col attrTypeId invalid for " + atsAttrCol;
            } else if (atsApi.tokenService().getAttributeTypeOrSentinel(atsAttrCol.getAttrTypeId()).isInvalid()) {
               msg = "AtsConfig.views col attrTypeId invalid for " + atsAttrCol;
            } else {
               AtsCoreAttrTokenColumn col =
                  new AtsCoreAttrTokenColumn(atsAttrCol, "AtsConfig.views", AtsApiService.get());
               idToAtsColumn.put(atsAttrCol.getId(), col);
            }
            if (Strings.isValid(msg)) {
               rd.error(msg);
            }
         }
      } catch (Exception ex) {
         rd.errorf("Exception processing AtsConfig.views %s\n", Lib.exceptionToString(ex));
      }
   }

   private void loadCoreAtsProviderColumns() {
      // Add columns provided through OSGI services
      for (IAtsColumnProvider provider : AtsColumnProviderCollector.getColumnProviders()) {
         for (AtsCoreAttrTokColumnToken atsAttrCol : provider.getAttrValCols()) {
            if (!idToAtsColumn.containsKey(atsAttrCol.getId())) {
               AtsCoreAttrTokenColumn col = new AtsCoreAttrTokenColumn(atsAttrCol,
                  "code " + provider.getClass().getSimpleName(), AtsApiService.get());
               idToAtsColumn.put(atsAttrCol.getId(), col);
            }
         }
      }
   }

   @Override
   public AtsCoreAttrTokenColumn getAttributeColumn(AttributeTypeToken attrType, String source) {
      return new AtsCoreAttrTokenColumn(attrType, source, atsApi);
   }

   @Override
   public String getColumnText(AtsCoreColumnToken column, IAtsObject atsObject) {
      return getColumnText(column.getId(), atsObject);
   }

   @Override
   public String getColumnText(String id, IAtsObject atsObject) {
      String result = "";
      AtsCoreColumn column = getColumn(id);
      if (column == null) {
         result = "Unhandled Column";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, AtsCoreColumnToken column, IAtsObject atsObject) {
      return getColumnText(configurations, column.getId(), atsObject);
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject) {
      String result = "";
      AtsCoreColumn column = getColumn(id);
      if (column == null) {
         result = "Unhandled Column";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public AtsCoreColumn getColumn(AtsCoreColumnToken columnId) {
      return getColumn(columnId.getId());
   }

   @Override
   public Collection<IAtsColumnProvider> getColumProviders() {
      return AtsColumnProviderCollector.getColumnProviders();
   }

   @Override
   public Date getColumnDate(AtsCoreAttrTokColumnToken attrCol, IAtsWorkItem workItem) {
      AttributeTypeGeneric<?> attrType = atsApi.tokenService().getAttributeType(attrCol.getAttrTypeId());
      return getColumnDate(attrType, workItem);
   }

   @Override
   public Date getColumnDate(AttributeTypeToken attrType, IAtsWorkItem workItem) {
      if (attrType.isInvalid() || !attrType.isDate()) {
         return null;
      }
      Date date = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, attrType, null);
      return date;
   }

   @Override
   public Collection<AtsCoreColumn> getColumns() {
      ensureLoaded();
      return idToAtsColumn.values();
   }

   @Override
   public String getColumnsJson() {
      ensureLoaded();
      String json = JsonUtil.toJson(idToAtsColumn.values());
      return json;
   }

   @Override
   public ColumnType getColumnType(AttributeTypeToken attributeType) {
      return AtsColumnUtil.getColumnType(attributeType);
   }

   @Override
   public XResultData getLoadResults() {
      ensureLoaded();
      return rd;
   }

   @Override
   public synchronized String getIdFromLegacyId(String legacyId) {
      if (legacyIdToId == null) {
         // @formatter:off
         legacyIdToId = new HashMap<>();
         legacyIdToId.put("ats.Category", AtsAttributeTypes.Category1.getName());
         legacyIdToId.put("ats.Category2", AtsAttributeTypes.Category2.getName());
         legacyIdToId.put("ats.Category3", AtsAttributeTypes.Category3.getName());
         legacyIdToId.put("ats.Priority", AtsColumnTokensDefault.PriorityColumn.getId());
         legacyIdToId.put("ats.column.Priority", AtsColumnTokensDefault.PriorityColumn.getId());
         legacyIdToId.put("ats.column.activityId", AtsAttributeTypes.ActivityId.getName());
         legacyIdToId.put("ats.column.applicabletoprogram", AtsAttributeTypes.ApplicableToProgram.getName());
         legacyIdToId.put("ats.column.cancelledDate", AtsAttributeTypes.CancelledDate.getName());
         legacyIdToId.put("ats.column.category", AtsAttributeTypes.Category1.getName());
         legacyIdToId.put("ats.column.completedBy", AtsAttributeTypes.CompletedBy.getName());
         legacyIdToId.put("ats.column.completedDate", AtsAttributeTypes.CompletedDate.getName());
         legacyIdToId.put("ats.column.createdDate", AtsAttributeTypes.CreatedDate.getName());
         legacyIdToId.put("ats.column.description", AtsAttributeTypes.Description.getName());
         legacyIdToId.put("ats.column.duplicatedpcrid", AtsAttributeTypes.DuplicatedPcrId.getName());
         legacyIdToId.put("ats.column.estimatedCompletionDate", AtsAttributeTypes.EstimatedCompletionDate.getName());
         legacyIdToId.put("ats.column.estimatedHours", AtsAttributeTypes.EstimatedHours.getName());
         legacyIdToId.put("ats.column.legacyPcr", AtsAttributeTypes.LegacyPcrId.getName());
         legacyIdToId.put("ats.column.locReviewed", AtsAttributeTypes.LocReviewed.getName());
         legacyIdToId.put("ats.column.notes", AtsAttributeTypes.WorkflowNotes.getName());
         legacyIdToId.put("ats.column.numeric1", AtsAttributeTypes.Numeric1.getName());
         legacyIdToId.put("ats.column.originatingpcrid", AtsAttributeTypes.OriginatingPcrId.getName());
         legacyIdToId.put("ats.column.pagesReviewed", AtsAttributeTypes.PagesReviewed.getName());
         legacyIdToId.put("ats.column.pcrtoolid", AtsAttributeTypes.PcrToolId.getName());
         legacyIdToId.put("ats.column.points", AtsAttributeTypes.Points.getName());
         legacyIdToId.put("ats.column.rationale", AtsAttributeTypes.Rationale.getName());
         legacyIdToId.put("ats.column.resolution", AtsAttributeTypes.Resolution.getName());
         legacyIdToId.put("ats.column.reviewformaltype", AtsAttributeTypes.ReviewFormalType.getName());
         legacyIdToId.put("ats.column.workPackageId", "ats.column.workPackage");
         legacyIdToId.put("ats.column.workPackageName", "ats.column.workPackage");
         legacyIdToId.put("ats.column.workflowPercentComplete", AtsAttributeTypes.PercentComplete.getName());
         legacyIdToId.put("attribute.ats.Category3", AtsAttributeTypes.Category3.getName());
         legacyIdToId.put("attribute.ats.Completed Date", AtsAttributeTypes.CompletedDate.getName());
         legacyIdToId.put("attribute.ats.Created Date", AtsAttributeTypes.CreatedDate.getName());
         legacyIdToId.put("attribute.ats.Current State", AtsAttributeTypes.CurrentState.getName());
         legacyIdToId.put("attribute.ats.Description", AtsAttributeTypes.Description.getName());
         legacyIdToId.put("attribute.ats.End Date", AtsAttributeTypes.EndDate.getName());
         legacyIdToId.put("attribute.ats.Estimated Completion Date", AtsAttributeTypes.EstimatedCompletionDate.getName());
         legacyIdToId.put("attribute.ats.Id", "ats.id");
         legacyIdToId.put("attribute.ats.Id", AtsAttributeTypes.AtsId.getName());
         legacyIdToId.put("attribute.ats.Legacy PCR Id", AtsAttributeTypes.LegacyPcrId.getName());
         legacyIdToId.put("attribute.ats.Meeting Attendee", AtsAttributeTypes.MeetingAttendee.getName());
         legacyIdToId.put("attribute.ats.Meeting Date", AtsAttributeTypes.MeetingDate.getName());
         legacyIdToId.put("attribute.ats.Meeting Length", AtsAttributeTypes.MeetingLength.getName());
         legacyIdToId.put("attribute.ats.Meeting Location", AtsAttributeTypes.MeetingLocation.getName());
         legacyIdToId.put("attribute.ats.Need By", "ats.column.deadline");
         legacyIdToId.put("attribute.ats.Operational Impact Description", AtsAttributeTypes.OperationalImpactDescription.getName());
         legacyIdToId.put("attribute.ats.Operational Impact Workaround Description", AtsAttributeTypes.OperationalImpactDescription.getName());
         legacyIdToId.put("attribute.ats.Operational Impact Workaround", AtsAttributeTypes.OperationalImpactWorkaround.getName());
         legacyIdToId.put("attribute.ats.Operational Impact", AtsAttributeTypes.OperationalImpact.getName());
         legacyIdToId.put("attribute.ats.Percent Complete", AtsAttributeTypes.PercentComplete.getName());
         legacyIdToId.put("attribute.ats.Priority", AtsColumnTokensDefault.PriorityColumn.getId());
         legacyIdToId.put("attribute.ats.Review Blocks", AtsAttributeTypes.ReviewBlocks.getName());
         legacyIdToId.put("attribute.ats.Review Formal Type", AtsAttributeTypes.ReviewFormalType.getName());
         legacyIdToId.put("attribute.ats.Start Date", AtsAttributeTypes.StartDate.getName());
         legacyIdToId.put("attribute.ats.Workflow Definition", AtsAttributeTypes.WorkflowDefinition.getName());
         legacyIdToId.put("ats.column.TestProcedure", CoreArtifactTypes.TestProcedure.getName());
         legacyIdToId.put("ats.column.cancelledReasonDetails", AtsAttributeTypes.CancelledReasonDetails.getName());
         legacyIdToId.put("ats.column.cancelledReason", AtsAttributeTypes.CancelledReason.getName());
         legacyIdToId.put("ats.column.crash.or.blank.display", AtsAttributeTypes.CrashOrBlankDisplay.getName());
         legacyIdToId.put("ats.column.external.reference", AtsAttributeTypes.ExternalReference.getName());
         // @formatter:on

         for (IAtsColumnProvider provider : AtsColumnProviderCollector.getColumnProviders()) {
            provider.getLegacyIdToId(legacyIdToId);
         }
      }
      return legacyIdToId.get(legacyId);
   }

}
