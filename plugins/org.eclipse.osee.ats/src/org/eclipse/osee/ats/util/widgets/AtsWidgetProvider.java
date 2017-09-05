/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.agile.XOpenSprintBurndownButton;
import org.eclipse.osee.ats.agile.XOpenSprintBurnupButton;
import org.eclipse.osee.ats.agile.XOpenSprintDataTableButton;
import org.eclipse.osee.ats.agile.XOpenSprintSummaryButton;
import org.eclipse.osee.ats.agile.XOpenStoredSprintReportsButton;
import org.eclipse.osee.ats.agile.XStoreSprintReportsButton;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.column.OperationalImpactWithWorkaroundXWidget;
import org.eclipse.osee.ats.column.OperationalImpactXWidget;
import org.eclipse.osee.ats.core.client.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.core.client.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.core.client.validator.AtsOperationalImpactValidator;
import org.eclipse.osee.ats.core.client.validator.AtsOperationalImpactWithWorkaroundValidator;
import org.eclipse.osee.ats.editor.widget.XAssigneesListWidget;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.XVersionList;
import org.eclipse.osee.ats.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.util.widgets.defect.XDefectViewer;
import org.eclipse.osee.ats.util.widgets.dialog.AtsObjectMultiChoiceSelect;
import org.eclipse.osee.ats.util.widgets.dialog.ClosureStateMultiChoiceSelect;
import org.eclipse.osee.ats.util.widgets.dialog.VersionMultiChoiceSelect;
import org.eclipse.osee.ats.util.widgets.role.XUserRoleViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
   public XWidget createXWidget(String widgetName, String name, XWidgetRendererItem widgetLayoutData) {
      XWidget toReturn = null;
      if (widgetName.equals(XHyperlabelTeamDefinitionSelection.WIDGET_ID)) {
         XHyperlabelTeamDefinitionSelection widget = new XHyperlabelTeamDefinitionSelection(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(XHyperlabelActionableItemSelection.WIDGET_ID)) {
         XHyperlabelActionableItemSelection widget = new XHyperlabelActionableItemSelection(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(XHyperlabelGroupSelection.WIDGET_ID)) {
         XHyperlabelGroupSelection widget = new XHyperlabelGroupSelection(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         toReturn = widget;
      } else if (widgetName.equals(AtsObjectMultiChoiceSelect.WIDGET_ID)) {
         toReturn = new AtsObjectMultiChoiceSelect();
      } else if (widgetName.equals(XReviewStateSearchCombo.WIDGET_ID)) {
         toReturn = new XReviewStateSearchCombo();
      } else if (widgetName.equals(XStateCombo.WIDGET_ID)) {
         toReturn = new XStateCombo();
      } else if (widgetName.equals(XStateSearchCombo.WIDGET_ID)) {
         toReturn = new XStateSearchCombo();
      } else if (widgetName.equals(XCommitManager.WIDGET_NAME)) {
         toReturn = new XCommitManager();
      } else if (widgetName.equals(XWorkingBranch.WIDGET_NAME)) {
         toReturn = new XWorkingBranch();
      } else if (widgetName.equals(XWorkingBranchLabel.WIDGET_NAME)) {
         toReturn = new XWorkingBranchLabel();
      } else if (widgetName.equals(XWorkingBranchButtonCreate.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonCreate();
      } else if (widgetName.equals(XWorkingBranchButtonArtifactExplorer.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonArtifactExplorer();
      } else if (widgetName.equals(XWorkingBranchButtonChangeReport.WIDGET_NAME)) {
         toReturn = new XWorkingBranchButtonChangeReport();
      } else if (widgetName.equals(XWorkingBranchUpdate.WIDGET_NAME)) {
         toReturn = new XWorkingBranchUpdate();
      } else if (widgetName.equals(XWorkingBranchDeleteMerge.WIDGET_NAME)) {
         toReturn = new XWorkingBranchDeleteMerge();
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
            for (IAtsProgram program : AtsClientService.get().getProgramService().getPrograms()) {
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
         if (widgetLayoutData.getXOptionHandler().contains(XOption.MULTI_SELECT)) {
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
      }

      return toReturn;
   }
}
