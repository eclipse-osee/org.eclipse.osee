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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurndownButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintBurnupButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintDataTableButton;
import org.eclipse.osee.ats.ide.agile.XOpenSprintSummaryButton;
import org.eclipse.osee.ats.ide.agile.XOpenStoredSprintReportsButton;
import org.eclipse.osee.ats.ide.agile.XStoreSprintReportsButton;
import org.eclipse.osee.ats.ide.column.OperationalImpactWithWorkaroundXWidget;
import org.eclipse.osee.ats.ide.column.OperationalImpactXWidget;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XAssigneesListWidget;
import org.eclipse.osee.ats.ide.editor.tab.workflow.widget.XRequestedHoursApprovalWidget;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.XVersionList;
import org.eclipse.osee.ats.ide.util.validate.AtsOperationalImpactValidator;
import org.eclipse.osee.ats.ide.util.validate.AtsOperationalImpactWithWorkaroundValidator;
import org.eclipse.osee.ats.ide.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.ide.util.widgets.defect.XDefectViewer;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ClosureStateMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionMultiChoiceSelect;
import org.eclipse.osee.ats.ide.util.widgets.role.XUserRoleViewer;
import org.eclipse.osee.ats.ide.util.widgets.task.XCreateChangeReportTasksXButton;
import org.eclipse.osee.ats.ide.workflow.cr.XCreateEscapementDemoWfXButton;
import org.eclipse.osee.ats.ide.workflow.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.ide.workflow.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.ide.workflow.task.widgets.XCheckBoxesWithTaskGenExample;
import org.eclipse.osee.ats.ide.workflow.task.widgets.estimates.demo.XTaskEstManagerDemo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateWithValidateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class AtsWidgetProvider implements IXWidgetProvider {

   @Override
   public XWidget createXWidget(String widgetName, String name, XWidgetRendererItem widgetRendererItem) {
      XWidget toReturn = null;
      if (widgetName.equals(XHyperlabelTeamDefinitionSelection.WIDGET_ID)) {
         XHyperlabelTeamDefinitionSelection widget = new XHyperlabelTeamDefinitionSelection(name);
         widget.setToolTip(widgetRendererItem.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(XHyperlabelActionableItemSelection.WIDGET_ID)) {
         XHyperlabelActionableItemSelection widget = new XHyperlabelActionableItemSelection(name);
         widget.setToolTip(widgetRendererItem.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(XHyperlabelGroupSelection.WIDGET_ID)) {
         XHyperlabelGroupSelection widget = new XHyperlabelGroupSelection(name);
         widget.setToolTip(widgetRendererItem.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(XProductLineApprovalWidget.WIDGET_ID)) {
         toReturn = new XProductLineApprovalWidget();
      } else if (widgetName.equals(AtsObjectMultiChoiceSelect.WIDGET_ID)) {
         toReturn = new AtsObjectMultiChoiceSelect();
      } else if (widgetName.equals(XReviewStateSearchCombo.WIDGET_ID)) {
         toReturn = new XReviewStateSearchCombo();
      } else if (widgetName.equals(XStateCombo.WIDGET_ID)) {
         toReturn = new XStateCombo();
      } else if (widgetName.equals(XStateSearchCombo.WIDGET_ID)) {
         toReturn = new XStateSearchCombo();
      } else if (widgetName.equals(XFoundInVersionWidget.WIDGET_ID)) {
         toReturn = new XFoundInVersionWidget(name);
      } else if (widgetName.equals(XIntroducedInVersionWidget.WIDGET_ID)) {
         toReturn = new XIntroducedInVersionWidget(name);
      } else if (widgetName.equals(XCommitManager.WIDGET_NAME)) {
         toReturn = new XCommitManager();
      } else if (widgetName.equals(XTaskEstManagerDemo.WIDGET_ID)) {
         toReturn = new XTaskEstManagerDemo();
      } else if (widgetName.equals(XWorkingBranchLabel.WIDGET_NAME)) {
         toReturn = new XWorkingBranchLabel();
      } else if (widgetName.equals(XWorkingBranchButtonCreate.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonCreate();
      } else if (widgetName.equals(XWorkingBranchButtonArtifactExplorer.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonArtifactExplorer();
      } else if (widgetName.equals(XWorkingBranchButtonChangeReport.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonChangeReport();
      } else if (widgetName.equals(XWorkingBranchButtonWordChangeReport.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonWordChangeReport();
      } else if (widgetName.equals(XWorkingBranchButtonContextChangeReport.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonContextChangeReport();
      } else if (widgetName.equals(XWorkingBranchUpdate.WIDGET_NAME)) {
         toReturn = new XWorkingBranchUpdate();
      } else if (widgetName.equals(XWorkingBranchButtonDeleteMergeBranches.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonDeleteMergeBranches();
      } else if (widgetName.equals(XWorkingBranchButtonDelete.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonDelete();
      } else if (widgetName.equals(XWorkingBranchButtonFavorites.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonFavorites();
      } else if (widgetName.equals(XWorkingBranchButtonLock.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonLock();
      } else if (widgetName.equals(AtsOperationalImpactValidator.WIDGET_NAME)) {
         toReturn = new OperationalImpactXWidget();
      } else if (widgetName.equals(XTeamDefinitionCombo.WIDGET_ID)) {
         toReturn = new XTeamDefinitionCombo();
      } else if (widgetName.equals(XActionableItemCombo.WIDGET_ID)) {
         toReturn = new XActionableItemCombo();
      } else if (widgetName.equals(XActionableItemAllCombo.WIDGET_ID)) {
         toReturn = new XActionableItemAllCombo();
      } else if (widgetName.equals(AtsOperationalImpactWithWorkaroundValidator.WIDGET_NAME)) {
         toReturn = new OperationalImpactWithWorkaroundXWidget();
      } else if (widgetName.equals(AtsXDefectValidator.WIDGET_NAME)) {
         return new XDefectViewer();
      } else if (widgetName.equals(AtsXUserRoleValidator.WIDGET_NAME)) {
         return new XUserRoleViewer();
      } else if (widgetName.equals("XAtsProgramComboWidget")) {
         try {
            return new XAtsProgramComboWidget();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (widgetName.equals("XAtsProgramActiveComboWidget")) {
         try {
            List<IAtsProgram> activePrograms = new ArrayList<>();
            for (IAtsProgram program : AtsApiService.get().getProgramService().getPrograms()) {
               if (program.isActive()) {
                  activePrograms.add(program);
               }
            }
            return new XAtsProgramComboWidget(activePrograms);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (widgetName.equals(XVersionList.WIDGET_ID)) {
         XVersionList versionList = new XVersionList();
         if (widgetRendererItem.getXOptionHandler().contains(XOption.MULTI_SELECT)) {
            versionList.setMultiSelect(true);
         }
         return versionList;
      } else if (widgetName.equals(XGoalCombo.WIDGET_ID)) {
         return new XGoalCombo();
      } else if (widgetName.equals(XCpaOpenOriginatingPcrWidget.WIDGET_ID)) {
         return new XCpaOpenOriginatingPcrWidget();
      } else if (widgetName.equals(XCpaOpenDuplicatedPcrWidget.WIDGET_ID)) {
         return new XCpaOpenDuplicatedPcrWidget();
      } else if (widgetName.equals(VersionMultiChoiceSelect.WIDGET_ID)) {
         return new VersionMultiChoiceSelect();
      } else if (widgetName.equals(ClosureStateMultiChoiceSelect.WIDGET_ID)) {
         return new ClosureStateMultiChoiceSelect();
      } else if (widgetName.equals(XProgramSelectionWidget.WIDGET_ID)) {
         return new XProgramSelectionWidget();
      } else if (widgetName.equals(XOpenSprintSummaryButton.WIDGET_ID)) {
         return new XOpenSprintSummaryButton();
      } else if (widgetName.equals(XOpenSprintDataTableButton.WIDGET_ID)) {
         return new XOpenSprintDataTableButton();
      } else if (widgetName.equals(XOpenSprintBurndownButton.WIDGET_ID)) {
         return new XOpenSprintBurndownButton();
      } else if (widgetName.equals(XOpenSprintBurnupButton.WIDGET_ID)) {
         return new XOpenSprintBurnupButton();
      } else if (widgetName.equals(XOpenStoredSprintReportsButton.WIDGET_ID)) {
         return new XOpenStoredSprintReportsButton();
      } else if (widgetName.equals(XStoreSprintReportsButton.WIDGET_ID)) {
         return new XStoreSprintReportsButton();
      } else if (widgetName.equals(XWorkPackageWidget.WIDGET_ID)) {
         return new XWorkPackageWidget();
      } else if (widgetName.equals(XHyperlabelWorkflowApplicabilitySelection.WIDGET_ID)) {
         return new XHyperlabelWorkflowApplicabilitySelection();
      } else if (widgetName.equals(XAssigneesListWidget.WIDGET_ID)) {
         return new XAssigneesListWidget();
      } else if (widgetName.equals(XArtifactReferencedAtsObjectAttributeWidget.WIDGET_ID)) {
         return new XArtifactReferencedAtsObjectAttributeWidget(name);
      } else if (widgetName.equals(XRequestedHoursApprovalWidget.ID)) {
         if (widgetRendererItem.isRequired()) {
            return new XRequestedHoursApprovalWidget(true);
         } else {
            return new XRequestedHoursApprovalWidget();
         }
      } else if (widgetName.equals(XCreateChangeReportTasksXButton.WIDGET_ID)) {
         AtsTaskDefToken atsTaskDefToken = (AtsTaskDefToken) widgetRendererItem.getParameters().get(AtsTaskDefToken.ID);
         return new XCreateChangeReportTasksXButton(name, atsTaskDefToken);
      } else if (widgetName.equals(XAssigneesHyperlinkWidget.WIDGET_ID)) {
         return new XAssigneesHyperlinkWidget(null);
      } else if (widgetName.equals(XAttachmentExampleWidget.WIDGET_ID)) {
         return new XAttachmentExampleWidget();
      } else if (widgetName.equals(XAgileFeatureHyperlinkWidget.WIDGET_ID)) {
         return new XAgileFeatureHyperlinkWidget();
      } else if (widgetName.equals(XGitFetchButton.WIDGET_ID)) {
         return new XGitFetchButton();
      } else if (widgetName.equals(XTargetedVersionHyperlinkWidget.WIDGET_ID)) {
         return new XTargetedVersionHyperlinkWidget();
      } else if (widgetName.equals(XOriginatorHyperlinkWidget.WIDGET_ID)) {
         return new XOriginatorHyperlinkWidget();
      } else if (widgetName.equals(XSprintHyperlinkWidget.WIDGET_ID)) {
         return new XSprintHyperlinkWidget();
      } else if (widgetName.equals(XCheckBoxesWithTaskGenExample.WIDGET_ID)) {
         return new XCheckBoxesWithTaskGenExample();
      } else if (widgetName.equals(XCreateEscapementDemoWfXButton.WIDGET_ID)) {
         return new XCreateEscapementDemoWfXButton();
      } else if (widgetName.equals(XHyperlinkFeatureDam.WIDGET_ID)) {
         return new XHyperlinkFeatureDam(name);
      } else if (widgetName.equals(XDateWithValidateDam.WIDGET_ID)) {
         return new XDateWithValidateDam(name);
      } else if (widgetName.equals(XPointsWidget.WIDGET_ID)) {
         return new XPointsWidget();
      } else if (widgetName.equals(XTleReviewedWidget.WIDGET_ID)) {
         return new XTleReviewedWidget();
      }

      return toReturn;
   }
}
