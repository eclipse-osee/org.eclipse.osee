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

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurndownButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurnupButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintDataTableButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintSummaryButton;
import org.eclipse.osee.ats.ide.agile.XOpenStoredSprintReportsButton;
import org.eclipse.osee.ats.ide.agile.XStoreSprintReportsButton;
import org.eclipse.osee.ats.ide.column.OperationalImpactWithWorkaroundXWidget;
import org.eclipse.osee.ats.ide.column.OperationalImpactXWidget;
import org.eclipse.osee.ats.ide.editor.tab.bit.XHyperlinkOpenBitTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XAssigneesListWidget;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XRequestedHoursApprovalWidget;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XDateWithValidateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class AtsWidgetProvider extends BaseXWidgetProvider {

   public AtsWidgetProvider() {
      register(XHyperlinkWfdForEnum.class);
      register(XHyperlinkWfdForProgramAi.class);
      register(XHyperlinkWfdForConfiguration.class);
      register(XHyperlinkWfdForConfigurationDam.class);
      register(XHyperlinkWfdForRelatedState.class);
      register(XHyperlinkWfdForRelatedStateDam.class);
      register(XHyperlinkWfdForActiveAis.class);
      register(XHyperlinkOpenBitTab.class);
      register(XReviewedWidget.class);
      register(XHyperlabelTeamDefinitionSelection.class);
      register(XHyperlabelActionableItemSelection.class);
      register(XHyperlabelGroupSelection.class);
      register(AtsObjectMultiChoiceSelect.class);
      register(OperationalImpactXWidget.class);
      register(OperationalImpactWithWorkaroundXWidget.class);
      register(XDefectViewer.class);
      register(XUserRoleViewer.class);
      register(XActionableItemAllCombo.class);
      register(XActionableItemCombo.class);
      register(XCommitManager.class);
      register(XFoundInVersionWithPersistWidget.class);
      register(XIntroducedInVersionWithPersistWidget.class);
      register(XProductLineApprovalWidget.class);
      register(XReviewStateSearchCombo.class);
      register(XStateCombo.class);
      register(XStateSearchCombo.class);
      register(XTargetedVersionWithPersistWidget.class);
      register(XTeamDefinitionCombo.class);
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
      register(XGoalCombo.class);
      register(ClosureStateMultiChoiceSelect.class);
      register(VersionMultiChoiceSelect.class);
      register(XArtifactReferencedAtsObjectAttributeWidget.class);
      register(XAssigneesListWidget.class);
      register(XCpaOpenDuplicatedPcrWidget.class);
      register(XCpaOpenOriginatingPcrWidget.class);
      register(XHyperlabelWorkflowApplicabilitySelection.class);
      register(XOpenSprintBurndownButton.class);
      register(XOpenSprintBurnupButton.class);
      register(XOpenSprintDataTableButton.class);
      register(XOpenSprintSummaryButton.class);
      register(XOpenStoredSprintReportsButton.class);
      register(XProgramSelectionWidget.class);
      register(XStoreSprintReportsButton.class);
      register(XWorkPackageWidget.class);
      register(XAssigneesHyperlinkWidget.class);
      register(XActionableItemWidget.class);
      register(XAgileFeatureHyperlinkWidget.class);
      register(XAttachmentExampleWidget.class);
      register(XCheckBoxesWithTaskGenExample.class);
      register(XCreateEscapeDemoWfXButton.class);
      register(XDateWithValidateDam.class);
      register(XEstimatedPointsWidget.class);
      register(XGitFetchButton.class);
      register(XHyperlabelVersionSelection.class);
      register(XHyperlabelFoundInVersionSelection.class);
      register(XHyperlinkFeatureDam.class);
      register(XOriginatorHyperlinkWidget.class);
      register(XSprintHyperlinkWidget.class);
      register(XTargetedVersionHyperlinkWidget.class);
      register(XTaskEstDemoWidget.class);
      register(XTaskEstSiblingWorldDemoWidget.class);
      register(XAtsProgramComboWidget.class);
      register(XVersionList.class);
      register(XRequestedHoursApprovalWidget.class);
      register(XHyperlinkWorkDefDam.class);
   }

   @Override
   public XWidget createXWidget(String widgetName, String name, XWidgetRendererItem rItem) {
      XWidget toReturn = super.createXWidget(widgetName, name, rItem);
      if (toReturn != null) {
         return toReturn;
      } else if (widgetName.equals(XCreateChangeReportTasksXButton.WIDGET_ID)) {
         AtsTaskDefToken atsTaskDefToken = (AtsTaskDefToken) rItem.getParameters().get(AtsTaskDefToken.ID);
         return new XCreateChangeReportTasksXButton(name, atsTaskDefToken);
      }
      return toReturn;
   }
}
