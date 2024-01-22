/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.column.PriorityColumn;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurndownButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurnupButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintDataTableButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintSummaryButton;
import org.eclipse.osee.ats.ide.agile.XOpenStoredSprintReportsButton;
import org.eclipse.osee.ats.ide.agile.XStoreSprintReportsButton;
import org.eclipse.osee.ats.ide.editor.tab.bit.XHyperlinkOpenBitTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XAssigneesListWidget;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.XDynamicAttrValuesWidget;
import org.eclipse.osee.ats.ide.util.XVersionList;
import org.eclipse.osee.ats.ide.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.ide.util.widgets.defect.XDefectViewer;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ClosureStateMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.role.XUserRoleViewer;
import org.eclipse.osee.ats.ide.util.widgets.task.XCreateChangeReportTasksXButton;
import org.eclipse.osee.ats.ide.workflow.cr.XCreateEscapeDemoWfXButton;
import org.eclipse.osee.ats.ide.workflow.cr.demo.XTaskEstDemoWidget;
import org.eclipse.osee.ats.ide.workflow.cr.demo.XTaskEstSiblingWorldDemoWidget;
import org.eclipse.osee.ats.ide.workflow.task.widgets.XCheckBoxesWithTaskGenExample;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateWithValidateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetProvider extends BaseXWidgetProvider {

   public AtsXWidgetProvider() {
      register(AtsObjectMultiChoiceSelect.class);
      register(ClosureStateMultiChoiceSelect.class);
      register(OperationalImpactXWidget.class);
      register(OperationalImpactWithWorkaroundXWidget.class);
      register(VersionMultiChoiceSelect.class);
      register(XActionableItemAllCombo.class);
      register(XActionableItemCombo.class);
      register(XActionableItemWidget.class);
      register(XAgileFeatureHyperlinkWidget.class);
      register(XArtifactReferencedAtsObjectAttributeWidget.class);
      register(XAssigneesHyperlinkWidget.class);
      register(XAssigneesListWidget.class);
      register(XAtsProgramComboWidget.class);
      register(XAttachmentExampleWidget.class);
      register(XCheckBoxesWithTaskGenExample.class);
      register(XCommitManager.class);
      register(XCreateEscapeDemoWfXButton.class);
      register(XDateWithValidateDam.class);
      register(XDefectViewer.class);
      register(XDynamicAttrValuesWidget.class);
      register(XEstimatedPointsWidget.class);
      register(XFoundInVersionWithPersistWidget.class);
      register(XGitFetchButton.class);
      register(XGoalCombo.class);
      register(XHyperlabelActionableItemSelection.class);
      register(XHyperlabelFoundInVersionSelection.class);
      register(XHyperlabelGroupSelection.class);
      register(XHyperlabelTeamDefinitionSelection.class);
      register(XHyperlabelVersionSelection.class);
      register(XHyperlabelWorkflowApplicabilitySelection.class);
      register(XHyperlinkFeatureDam.class);
      register(XHyperlinkOpenBitTab.class);
      register(XHyperlinkWfdForActiveAis.class);
      register(XHyperlinkWfdForConfiguration.class);
      register(XHyperlinkWfdForConfigurationDam.class);
      register(XHyperlinkWfdForEnum.class);
      register(XHyperlinkWfdForProgramAi.class);
      register(XHyperlinkWfdForRelatedState.class);
      register(XHyperlinkWfdForRelatedStateDam.class);
      register(XHyperlinkWorkDefDam.class);
      register(XIntroducedInVersionWithPersistWidget.class);
      register(XOpenSprintBurndownButton.class);
      register(XOpenSprintBurnupButton.class);
      register(XOpenSprintDataTableButton.class);
      register(XOpenSprintSummaryButton.class);
      register(XOpenStoredSprintReportsButton.class);
      register(XOriginatorHyperlinkWidget.class);
      register(XProductLineApprovalWidget.class);
      register(XProgramSelectionWidget.class);
      register(XReviewStateSearchCombo.class);
      register(XReviewedWidget.class);
      register(XSignbyWidget.class);
      register(XSprintHyperlinkWidget.class);
      register(XStateCombo.class);
      register(XStateSearchCombo.class);
      register(XStoreSprintReportsButton.class);
      register(XTargetedVersionHyperlinkWidget.class);
      register(XTargetedVersionWithPersistWidget.class);
      register(XTaskEstDemoWidget.class);
      register(XTaskEstSiblingWorldDemoWidget.class);
      register(XTeamDefinitionCombo.class);
      register(XUserRoleViewer.class);
      register(XValidateReqChangesButton.class);
      register(XVersionList.class);
      register(XWorkPackageHyperlinkWidget.class);
      register(XWorkingBranchButtonArtifactExplorer.class);
      register(XWorkingBranchButtonChangeReport.class);
      register(XWorkingBranchButtonContextChangeReport.class);
      register(XWorkingBranchButtonCreate.class);
      register(XWorkingBranchButtonDelete.class);
      register(XWorkingBranchButtonDeleteMergeBranches.class);
      register(XWorkingBranchButtonFavorites.class);
      register(XWorkingBranchButtonLock.class);
      register(XWorkingBranchButtonWordChangeReport.class);
      register(XWorkingBranchLabel.class);
      register(XWorkingBranchUpdate.class);
   }

   @Override
   public XWidget createXWidget(String widgetName, String name, XWidgetRendererItem item) {
      XWidget widget = super.createXWidget(widgetName, name, item);
      if (widget != null) {
         return widget;
      } else if (widgetName.equals(XCreateChangeReportTasksXButton.WIDGET_ID)) {
         AtsTaskDefToken atsTaskDefToken = (AtsTaskDefToken) item.getParameters().get(AtsTaskDefToken.ID);
         widget = new XCreateChangeReportTasksXButton(name, XCreateChangeReportTasksXButton.class.getSimpleName(),
            atsTaskDefToken);
      } else if (widgetName.equals(XHyperlinkChangeTypeSelection.WIDGET_ID) || widgetName.equals(
         XHyperlinkChangeTypeSelectionDam.WIDGET_ID)) {
         String changeTypesStr = (String) item.getParameters().get(ChangeTypes.CHANGE_TYPE_PARAM_KEY);
         List<ChangeTypes> types = new ArrayList<>();
         if (Strings.isValid(changeTypesStr)) {
            for (String cTypeStr : changeTypesStr.split(";")) {
               ChangeTypes cType = ChangeTypeColumn.getChangeType(cTypeStr, AtsApiService.get());
               types.add(cType);
            }
         }
         if (widgetName.equals(XHyperlinkChangeTypeSelectionDam.WIDGET_ID)) {
            widget = new XHyperlinkChangeTypeSelectionDam(name, types.toArray(new ChangeTypes[types.size()]));
         } else if (widgetName.equals(XHyperlinkChangeTypeSelection.WIDGET_ID)) {
            widget = new XHyperlinkChangeTypeSelection(name, types.toArray(new ChangeTypes[types.size()]));
         }
      } else if (widgetName.equals(XHyperlinkPrioritySelection.WIDGET_ID) || widgetName.equals(
         XHyperlinkPrioritySelectionDam.WIDGET_ID)) {
         String priorityTypesStr = (String) item.getParameters().get(Priorities.PRIORITY_PARAM_KEY);
         List<Priorities> types = new ArrayList<>();
         if (Strings.isValid(priorityTypesStr)) {
            for (String priStr : priorityTypesStr.split(";")) {
               Priorities cType = PriorityColumn.getPriority(priStr, AtsApiService.get());
               types.add(cType);
            }
         }
         if (widgetName.equals(XHyperlinkPrioritySelectionDam.WIDGET_ID)) {
            widget = new XHyperlinkPrioritySelectionDam(name, types.toArray(new Priorities[types.size()]));
         } else if (widgetName.equals(XHyperlinkPrioritySelection.WIDGET_ID)) {
            widget = new XHyperlinkPrioritySelection(name, types.toArray(new Priorities[types.size()]));
         }
      }
      return widget;
   }

}
