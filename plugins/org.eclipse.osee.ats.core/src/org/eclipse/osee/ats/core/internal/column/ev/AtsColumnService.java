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

package org.eclipse.osee.ats.core.internal.column.ev;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumn;
import org.eclipse.osee.ats.api.column.AtsColumnToken;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.column.IAtsColumnProvider;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.core.column.ActionableItemsColumn;
import org.eclipse.osee.ats.core.column.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.core.column.AgileTeamPointsColumn;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.column.AtsAttributeValueColumnHandler;
import org.eclipse.osee.ats.core.column.AtsIdColumn;
import org.eclipse.osee.ats.core.column.AttributeColumn;
import org.eclipse.osee.ats.core.column.CancelReasonColumn;
import org.eclipse.osee.ats.core.column.CancelledByColumn;
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.core.column.CancelledReasonColumn;
import org.eclipse.osee.ats.core.column.CancelledReasonDetailsColumn;
import org.eclipse.osee.ats.core.column.CrIdColumn;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.column.CompletedByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.core.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.ats.core.column.DerivedFromAtsIdColumn;
import org.eclipse.osee.ats.core.column.DerivedFromTeamDefColumn;
import org.eclipse.osee.ats.core.column.FeatureImpactReferenceColumn;
import org.eclipse.osee.ats.core.column.FoundInVersionColumn;
import org.eclipse.osee.ats.core.column.IdColumn;
import org.eclipse.osee.ats.core.column.ImplementerColumn;
import org.eclipse.osee.ats.core.column.IncorporatedInColumn;
import org.eclipse.osee.ats.core.column.InsertionActivityColumn;
import org.eclipse.osee.ats.core.column.InsertionColumn;
import org.eclipse.osee.ats.core.column.ParentTitleColumn;
import org.eclipse.osee.ats.core.column.PercentCompleteTasksColumn;
import org.eclipse.osee.ats.core.column.PriorityColumn;
import org.eclipse.osee.ats.core.column.PrIdColumn;
import org.eclipse.osee.ats.core.column.SprintOrderColumn;
import org.eclipse.osee.ats.core.column.StateColumn;
import org.eclipse.osee.ats.core.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.column.TaskPointsColumn;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.core.column.TaskRiskFactorsColumn;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.eclipse.osee.ats.core.column.TitleColumn;
import org.eclipse.osee.ats.core.column.TypeColumn;
import org.eclipse.osee.ats.core.internal.column.WorkDefinitionColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;

/**
 * See AtsColumnToken javadoc for architecture/design
 *
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   public static final String CELL_ERROR_PREFIX = "!Error";
   private Map<String, AtsColumn> columnIdToAtsColumn;
   private final AtsApi atsApi;

   public AtsColumnService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public AtsColumn getColumn(String id) {
      if (columnIdToAtsColumn == null) {
         columnIdToAtsColumn = new HashMap<>();
      }

      // Get from cache
      AtsColumn column = columnIdToAtsColumn.get(id);
      if (column != null) {
         return column;
      }

      // Add from database configurations
      for (AtsAttrValCol attrCol : atsApi.getConfigService().getConfigurations().getViews().getAttrColumns()) {
         if (id.equals(attrCol.getId())) {
            column = new AtsAttributeValueColumnHandler(attrCol, atsApi);
            break;
         }
      }

      // Add from coded columns; this will only happen once per column as they're cached after this check
      if (column == null) {

         /**
          * If possible, this is the preferred method of providing column handling if just showing attr value(s). This
          * method uses the token values to perform all the ways of displaying, rollup and edit funcionality.
          */
         if (id.equals(AtsColumnTokens.LegacyPcrIdColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.LegacyPcrIdColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.QuantityUnderReviewColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.QuantityUnderReviewColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.NotesColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.NotesColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.PercentCompleteWorkflowColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.PercentCompleteWorkflowColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.UnPlannedWorkColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.UnPlannedWorkColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.CrashOrBlankDisplayColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.CrashOrBlankDisplayColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.ExternalReferenceColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.ExternalReferenceColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.HowToReproduceProblemColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.HowToReproduceProblemColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.ProblemFirstObservedColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.ProblemFirstObservedColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.RiskAnalysisColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.RiskAnalysisColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.RevisitDateColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.RevisitDateColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.RootCauseColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.RootCauseColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.ProposedResolutionColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.ProposedResolutionColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.ImpactToMissionOrCrewColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.ImpactToMissionOrCrewColumn, atsApi);
         } else if (id.equals(AtsColumnTokens.WorkaroundColumn.getId())) {
            column = new AtsAttributeValueColumnHandler(AtsColumnTokens.WorkaroundColumn, atsApi);
         }

         /**
          * Only create a new column class when you can't use the default AtsAttributeValueColumnHandler above. A good
          * example is Assignees where portions of attr values need to be extracted and then the user resolved to
          * display.
          */
         else if (id.equals(AtsColumnTokens.ActionableItemsColumn.getId())) {
            column = new ActionableItemsColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TeamColumn.getId())) {
            column = new TeamColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.AssigneeColumn.getId())) {
            column = new AssigneeColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.AtsIdColumn.getId())) {
            column = new AtsIdColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.ActivityIdColumn.getId())) {
            column = new WorkPackageColumn(atsApi.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnTokens.ImplementersColumn.getId())) {
            column = new ImplementerColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CreatedDateColumn.getId())) {
            column = new CreatedDateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CompletedDateColumn.getId())) {
            column = new CompletedDateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CancelledDateColumn.getId())) {
            column = new CancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CancelledByColumn.getId())) {
            column = new CancelledByColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CancelledReason.getId())) {
            column = new CancelledReasonColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CancelReason.getId())) {
            column = new CancelReasonColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CrIdColumn.getId())) {
            column = new CrIdColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.PrIdColumn.getId())) {
            column = new PrIdColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CancelledReasonDetails.getId())) {
            column = new CancelledReasonDetailsColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.ChangeTypeColumn.getId())) {
            column = new ChangeTypeColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.PriorityColumn.getId())) {
            column = new PriorityColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CompletedByColumn.getId())) {
            column = new CompletedByColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CompletedCancelledByColumn.getId())) {
            column = new CompletedCancelledByColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CompletedCancelledDateColumn.getId())) {
            column = new CompletedCancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.StateColumn.getId())) {
            column = new StateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.NameColumn.getId()) || id.equals(AtsColumnTokens.TitleColumn.getId())) {
            column = new TitleColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TypeColumn.getId())) {
            column = new TypeColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.PercentCompleteTasksColumn.getId())) {
            column = new PercentCompleteTasksColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.IdColumn.getId())) {
            column = new IdColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.WorkDefinitionColumn.getId())) {
            column = new WorkDefinitionColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.WorkPackageNameColumn.getId())) {
            column = new WorkPackageNameColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnTokens.WorkPackageIdColumn.getId())) {
            column = new WorkPackageIdColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnTokens.WorkPackageTypeColumn.getId())) {
            column = new WorkPackageTypeColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnTokens.WorkPackageProgramColumn.getId())) {
            column = new WorkPackageProgramColumn(atsApi.getEarnedValueServiceProvider(), atsApi);
         } else if (id.equals(AtsColumnTokens.StateColumn.getId())) {
            column = new StateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.InsertionColumn.getId())) {
            column = new InsertionColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.InsertionActivityColumn.getId())) {
            column = new InsertionActivityColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TargetedVersionColumn.getId())) {
            column = new TargetedVersionColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.FoundInVersionColumn.getId())) {
            column = new FoundInVersionColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.SprintOrderColumn.getId())) {
            column = new SprintOrderColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.AgileTeamPointsColumn.getId())) {
            column = new AgileTeamPointsColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.CompletedCancelledDateColumn.getId())) {
            column = new CompletedCancelledDateColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.AgileFeatureGroupColumn.getId())) {
            column = new AgileFeatureGroupColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TaskToRelatedArtifactTypeColumnToken.getId())) {
            column = new TaskRelatedArtifactTypeColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.ParentTitleColumn.getId())) {
            column = new ParentTitleColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.IncorporatedInColumn.getId())) {
            column = new IncorporatedInColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.FeatureImpactReferenceColumn.getId())) {
            column = new FeatureImpactReferenceColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TaskPointsColumn.getId())) {
            column = new TaskPointsColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.TaskRiskFactorsColumn.getId())) {
            column = new TaskRiskFactorsColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.DerivedFromAtsIdColumn.getId())) {
            column = new DerivedFromAtsIdColumn(atsApi);
         } else if (id.equals(AtsColumnTokens.DerivedFromTeamDefColumn.getId())) {
            column = new DerivedFromTeamDefColumn(atsApi);
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
            AttributeTypeToken attrType = atsApi.tokenService().getAttributeType(id.replaceFirst("attribute\\.", ""));
            column = new AttributeColumn(atsApi, attrType);

         }
         if (id.startsWith("ats.")) {
            try {
               AttributeTypeToken attrType = atsApi.tokenService().getAttributeType(id);
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
   public String getColumnText(AtsColumnToken column, IAtsObject atsObject) {
      return getColumnText(column.getId(), atsObject);
   }

   @Override
   public String getColumnText(String id, IAtsObject atsObject) {
      String result = "";
      AtsColumn column = getColumn(id);
      if (column == null) {
         result = "column not supported";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, AtsColumnToken column, IAtsObject atsObject) {
      return getColumnText(configurations, column.getId(), atsObject);
   }

   @Override
   public String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject) {
      String result = "";
      AtsColumn column = getColumn(id);
      if (column == null) {
         result = "column not supported";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public void add(String id, AtsColumn column) {
      columnIdToAtsColumn.put(id, column);
   }

   @Override
   public AtsColumn getColumn(AtsColumnToken columnId) {
      return getColumn(columnId.getId());
   }

   @Override
   public Collection<IAtsColumnProvider> getColumProviders() {
      return AtsColumnProviderCollector.getColumnProviders();
   }

}