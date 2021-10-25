/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.actions.wizard;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XAgileFeatureHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XAssigneesHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkLabelValuePointsSelection;
import org.eclipse.osee.ats.ide.util.widgets.XOriginatorHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XSprintHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XTargetedVersionHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XWorkPackageHyperlinkWidget;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWizardItem implements IAtsWizardItem, IDynamicWidgetLayoutListener {

   private final DoubleKeyHashMap<IAtsTeamDefinition, WizardFields, Object> teamDefFieldToWidget =
      new DoubleKeyHashMap<>();
   private final AtsApi atsApi;
   private final XComboViewer versionCombo = null;
   private final XCheckBox createBranchCheck = null;

   public AbstractWizardItem(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public Collection<WizardFields> getFields(IAtsTeamDefinition teamDef) {
      return Collections.emptyList();
   }

   @Override
   public void getWizardXWidgetExtensions(Collection<IAtsActionableItem> ais, Composite comp) {

      Collection<IAtsTeamDefinition> teamDefs = AtsApiService.get().getActionableItemService().getImpactedTeamDefs(ais);
      boolean first = true;
      for (IAtsTeamDefinition teamDef : teamDefs) {

         if (hasWizardXWidgetExtensions(teamDef)) {

            XLabel label = new XLabel((first ? "" : "\n") + "Extra fields for team \"" + teamDef.getName() + "\"");
            label.createWidgets(comp, 1);

            Composite teamComp = new Composite(comp, SWT.BORDER);
            teamComp.setLayout(new GridLayout(1, false));
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.widthHint = 500;
            teamComp.setLayoutData(gd);

            for (WizardFields field : getFields(teamDef)) {
               if (field.equals(WizardFields.Assignees)) {
                  createAssigneeWidget(teamDef, teamComp);
               } else if (field.equals(WizardFields.Originator)) {
                  createOriginatorWidget(teamDef, teamComp);
               } else if (field.equals(WizardFields.PointsNumeric)) {
                  createPointsNumericWidget(teamDef, teamComp);
               } else if (field.equals(WizardFields.Points)) {
                  createPointsWidget(teamDef, teamComp);
               } else if (field.equals(WizardFields.WorkPackage)) {
                  createWorkPackageWidget(teamDef, teamComp);
               } else if (field.equals(WizardFields.Sprint)) {
                  createSprintWidget(teamComp, teamDef);
               } else if (field.equals(WizardFields.TargetedVersion)) {
                  createTargetedVersionWidget(teamComp, teamDef);
               } else if (field.equals(WizardFields.UnPlannedWork)) {
                  createUnplannedWorkWidget(teamComp, teamDef);
               } else if (field.equals(WizardFields.FeatureGroup)) {
                  createFeatureGroupWidget(teamComp, teamDef);
               }
            }
            teamComp.layout();
         }
      }
   }

   protected boolean hasWizardXWidgetExtensions(IAtsTeamDefinition teamDef) {
      return false;
   }

   private void createWorkPackageWidget(IAtsTeamDefinition teamDef, Composite teamComp) {
      XWorkPackageHyperlinkWidget wpComp = new XWorkPackageHyperlinkWidget(teamDef);
      wpComp.setFillHorizontally(true);
      wpComp.createWidgets(teamComp, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.WorkPackage, wpComp);
   }

   private void createPointsNumericWidget(IAtsTeamDefinition teamDef, Composite teamComp) {
      Composite pointsComp = new Composite(teamComp, SWT.NONE);
      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      pointsComp.setLayout(layout);
      pointsComp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

      XFloat pointsNumeric = new XFloat(WizardFields.PointsNumeric.getDisplayName());
      pointsNumeric.setFillHorizontally(false);
      pointsNumeric.createWidgets(pointsComp, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.PointsNumeric, pointsNumeric);
   }

   private void createPointsWidget(IAtsTeamDefinition teamDef, Composite teamComp) {
      Composite pointsComp = new Composite(teamComp, SWT.NONE);
      GridLayout layout = ALayout.getZeroMarginLayout(1, false);
      pointsComp.setLayout(layout);
      pointsComp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

      XHyperlinkLabelValuePointsSelection widget = new XHyperlinkLabelValuePointsSelection(teamDef);
      widget.createWidgets(pointsComp, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.Points, widget);
   }

   private void createAssigneeWidget(IAtsTeamDefinition teamDef, Composite teamComp) {
      XAssigneesHyperlinkWidget assigneesWidget = new XAssigneesHyperlinkWidget(teamDef);
      assigneesWidget.setToolTip("If not set here, configured Lead(s) will be assigned.");
      assigneesWidget.setFillHorizontally(true);
      assigneesWidget.createWidgets(teamComp, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.Assignees, assigneesWidget);
   }

   private void createOriginatorWidget(IAtsTeamDefinition teamDef, Composite parent) {
      XOriginatorHyperlinkWidget originatorWidget = new XOriginatorHyperlinkWidget();
      originatorWidget.setFillHorizontally(true);
      originatorWidget.createWidgets(parent, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.Originator, originatorWidget);
   }

   private void createTargetedVersionWidget(Composite parent, IAtsTeamDefinition teamDef) {
      XTargetedVersionHyperlinkWidget widget = new XTargetedVersionHyperlinkWidget();
      widget.setTeamDef(teamDef);
      widget.createWidgets(parent, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.TargetedVersion, widget);
   }

   private void createUnplannedWorkWidget(Composite parent, IAtsTeamDefinition teamDef) {
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(2, false));
      comp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

      final XCheckBox createBranchCheck = new XCheckBox("Unplanned Work");
      createBranchCheck.setFillHorizontally(false);
      createBranchCheck.createWidgets(comp, 2);
      teamDefFieldToWidget.put(teamDef, WizardFields.UnPlannedWork, createBranchCheck);
   }

   private void createSprintWidget(Composite parent, IAtsTeamDefinition teamDef) {
      XSprintHyperlinkWidget widget = new XSprintHyperlinkWidget();
      widget.setTeamDef(teamDef);
      widget.createWidgets(parent, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.Sprint, widget);
   }

   private void createFeatureGroupWidget(Composite parent, IAtsTeamDefinition teamDef) {
      XAgileFeatureHyperlinkWidget widget = new XAgileFeatureHyperlinkWidget();
      widget.setTeamDef(teamDef);
      widget.createWidgets(parent, 1);
      teamDefFieldToWidget.put(teamDef, WizardFields.FeatureGroup, widget);
   }

   @Override
   public void wizardCompleted(ActionResult actionResult, IAtsChangeSet changes) {
      for (IAtsTeamWorkflow teamWf : actionResult.getTeams()) {
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();

         wizardCompletedAssignees(teamWf, teamDef);
         wizardCompletedOriginator(teamWf, teamDef, changes);
         wizardCompletedPointsNumeric(teamWf, teamDef, changes);
         wizardCompletedPoints(teamWf, teamDef, changes);
         wizardCompletedWorkPackage(teamWf, teamDef, changes);
         wizardCompletedSprint(teamWf, teamDef, changes);
         wizardCompletedTargetedVersion(teamWf, teamDef, changes);
         wizardCompletedFeatureGroup(teamWf, teamDef, changes);
         wizardCompletedUnPlanned(teamWf, teamDef, changes);

         if (versionCombo != null && versionCombo.getSelected() != null && createBranchCheck != null && createBranchCheck.isChecked()) {
            changes.addExecuteListener(new CreateBranch(teamDef));
         }
      }
   }

   private void wizardCompletedUnPlanned(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XCheckBox unPlanedCheckBox = (XCheckBox) teamDefFieldToWidget.get(teamDef, WizardFields.UnPlannedWork);
      if (unPlanedCheckBox != null) {
         boolean unplanned = unPlanedCheckBox.isChecked();
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.UnplannedWork, unplanned);
      }
   }

   private void wizardCompletedFeatureGroup(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XAgileFeatureHyperlinkWidget featureCombo =
         (XAgileFeatureHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.FeatureGroup);
      if (featureCombo != null) {
         for (IAgileFeatureGroup featureGroup : featureCombo.getFeatures()) {
            changes.relate(featureGroup, AtsRelationTypes.AgileFeatureToItem_AtsItem, teamWf);
         }
      }
   }

   private void wizardCompletedTargetedVersion(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XTargetedVersionHyperlinkWidget widget =
         (XTargetedVersionHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.TargetedVersion);
      if (widget != null) {
         IAtsVersion version = widget.getSelected();
         if (version != null) {
            atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);
         }
      }
   }

   private void wizardCompletedSprint(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XSprintHyperlinkWidget sprintCombo =
         (XSprintHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.Sprint);
      if (sprintCombo != null) {
         IAgileSprint sprint = sprintCombo.getSelected();
         if (sprint != null) {
            changes.relate(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, teamWf);
         }
      }
   }

   private void wizardCompletedWorkPackage(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XWorkPackageHyperlinkWidget workPackageWidget =
         (XWorkPackageHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.WorkPackage);
      if (workPackageWidget != null) {
         IAtsWorkPackage workPackage = workPackageWidget.getSelected();
         if (workPackage != null) {
            atsApi.getEarnedValueService().setWorkPackage(workPackage, teamWf, changes);
         }
      }
   }

   private void wizardCompletedPointsNumeric(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XFloat xFloat = (XFloat) teamDefFieldToWidget.get(teamDef, WizardFields.PointsNumeric);
      if (xFloat != null) {
         String pointsNumeric = xFloat.get();
         if (Strings.isValid(pointsNumeric)) {
            changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.PointsNumeric, pointsNumeric);
         }
      }
   }

   private void wizardCompletedPoints(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XHyperlinkLabelValuePointsSelection pointsWidget =
         (XHyperlinkLabelValuePointsSelection) teamDefFieldToWidget.get(teamDef, WizardFields.Points);
      if (pointsWidget != null) {
         AttributeTypeToken pointsAttrType = atsApi.getAgileService().getPointsAttrType(teamDef);
         String value = pointsWidget.getValue();
         if (Strings.isValid(value)) {
            if (pointsAttrType.equals(AtsAttributeTypes.Points)) {
               changes.setSoleAttributeValue(teamWf, pointsAttrType, value);
            } else {
               changes.setSoleAttributeValue(teamWf, pointsAttrType, Double.valueOf(value));
            }
         }
      }
   }

   private void wizardCompletedAssignees(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef) {
      XAssigneesHyperlinkWidget assigneesWidget =
         (XAssigneesHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.Assignees);
      if (assigneesWidget != null && !assigneesWidget.getSelected().isEmpty()) {
         teamWf.getStateMgr().setAssignees(assigneesWidget.getSelected());
      }
   }

   private void wizardCompletedOriginator(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XOriginatorHyperlinkWidget originatorWidget =
         (XOriginatorHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.Originator);
      if (originatorWidget != null && originatorWidget.getSelected() != null) {
         teamWf.getStateMgr().setCreatedBy(originatorWidget.getSelected(), true, new Date(), changes);
      }
   }

   private class CreateBranch implements IExecuteListener {
      private final IAtsTeamDefinition teamDef;

      public CreateBranch(IAtsTeamDefinition teamDef) {
         this.teamDef = teamDef;
      }

      @Override
      public void changesStored(IAtsChangeSet changes) {
         for (IAtsWorkItem workItem : changes.getWorkItemsCreated()) {
            if (workItem.isTeamWorkflow() && workItem.getParentTeamWorkflow().getTeamDefinition().equals(teamDef)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) workItem.getParentTeamWorkflow().getStoreObject();
               Result result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(teamArt);
               if (result.isTrue()) {
                  AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(teamArt);
               }
            }
         }
      }
   }

   @Override
   public abstract String getName();

}
