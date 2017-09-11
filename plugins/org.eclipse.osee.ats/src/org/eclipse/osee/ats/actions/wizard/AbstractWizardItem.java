/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
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
import org.eclipse.osee.ats.core.client.branch.AtsBranchUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XAssigneesHyperlinkWidget;
import org.eclipse.osee.ats.util.widgets.XOriginatorHyperlinkWidget;
import org.eclipse.osee.ats.util.widgets.XWorkPackageHyperlinkWidget;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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
   private final IAtsServices services;
   private final WizardFields[] fields;
   private XComboViewer versionCombo;
   private XCheckBox createBranchCheck;

   public AbstractWizardItem(IAtsServices services, WizardFields... fields) {
      this.services = services;
      this.fields = fields;
   }

   @Override
   public void getWizardXWidgetExtensions(Collection<IAtsActionableItem> ais, Composite comp) {

      Collection<IAtsTeamDefinition> teamDefs = ActionableItems.getImpactedTeamDefs(ais);
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

            for (WizardFields field : fields) {
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
                  createTargetedVersionAndBranchWidgets(teamComp, teamDef);
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

   protected abstract boolean hasWizardXWidgetExtensions(IAtsTeamDefinition teamDef);

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

      XComboViewer pointsCombo = new XComboViewer("Points", SWT.NONE);
      Collection<Object> objects = new LinkedList<>();
      objects.addAll(AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.Points));
      pointsCombo.setInput(objects);
      pointsCombo.setFillHorizontally(false);
      pointsCombo.createWidgets(pointsComp, 2);
      teamDefFieldToWidget.put(teamDef, WizardFields.Points, pointsCombo);
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

   private void createTargetedVersionAndBranchWidgets(Composite parent, IAtsTeamDefinition teamDef) {
      IAtsTeamDefinition teamDefHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();
      if (teamDefHoldingVersions != null) {
         List<IAtsVersion> versions = new LinkedList<>();
         for (IAtsVersion version : services.getVersionService().getVersions(teamDefHoldingVersions)) {
            if (!version.isReleased()) {
               versions.add(version);
            }
         }
         if (!versions.isEmpty()) {
            Collections.sort(versions, new Comparator<IAtsVersion>() {

               @Override
               public int compare(IAtsVersion arg0, IAtsVersion arg1) {
                  return arg0.getName().compareTo(arg1.getName());
               }
            });

            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(ALayout.getZeroMarginLayout(2, false));
            comp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            versionCombo = new XComboViewer("Targeted Version", SWT.NONE);
            Collection<Object> objects = new LinkedList<>();
            objects.addAll(versions);
            versionCombo.setInput(objects);
            versionCombo.setFillHorizontally(false);
            versionCombo.createWidgets(comp, 2);
            teamDefFieldToWidget.put(teamDef, WizardFields.TargetedVersion, versionCombo);

            Composite comp2 = new Composite(parent, SWT.NONE);
            comp2.setLayout(ALayout.getZeroMarginLayout(2, false));
            comp2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            createBranchCheck = new XCheckBox("Create Branch Automatically");
            createBranchCheck.setToolTip(
               "Branch will be completed after Action Creation (Valid Targeted Version must be selected)");
            createBranchCheck.setEditable(false);
            createBranchCheck.setFillHorizontally(false);
            createBranchCheck.createWidgets(comp2, 2);
            teamDefFieldToWidget.put(teamDef, WizardFields.CreateBranch, createBranchCheck);

            versionCombo.addXModifiedListener(new XModifiedListener() {

               @Override
               public void widgetModified(XWidget widget) {
                  IAtsVersion version = (IAtsVersion) versionCombo.getSelected();
                  if (version == null) {
                     createBranchCheck.setEditable(false);
                  } else {
                     createBranchCheck.setEditable(true);
                  }
                  comp2.layout();
               }

            });
         }
      }
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
      ArtifactToken agileTeam =
         services.getRelationResolver().getRelatedOrNull(teamDef, AtsRelationTypes.AgileTeamToAtsTeam_AgileTeam);
      if (agileTeam != null) {
         List<IAgileSprint> sprints = new LinkedList<>();
         for (ArtifactToken sprintArt : services.getRelationResolver().getRelated(agileTeam,
            AtsRelationTypes.AgileTeamToSprint_Sprint)) {
            IAgileSprint sprint = services.getWorkItemFactory().getAgileSprint(sprintArt);
            if (sprint.isInWork()) {
               sprints.add(sprint);
            }
         }
         if (!sprints.isEmpty()) {
            Collections.sort(sprints, new Comparator<IAgileSprint>() {

               @Override
               public int compare(IAgileSprint arg0, IAgileSprint arg1) {
                  return arg0.getName().compareTo(arg1.getName());
               }
            });
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(ALayout.getZeroMarginLayout(2, false));
            comp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            XComboViewer sprintCombo = new XComboViewer("Sprint", SWT.NONE);
            Collection<Object> objects = new LinkedList<>();
            objects.addAll(sprints);
            sprintCombo.setInput(objects);
            sprintCombo.setFillHorizontally(false);
            sprintCombo.createWidgets(comp, 2);
            teamDefFieldToWidget.put(teamDef, WizardFields.Sprint, sprintCombo);

         }
      }
   }

   private void createFeatureGroupWidget(Composite parent, IAtsTeamDefinition teamDef) {
      ArtifactToken agileTeam =
         services.getRelationResolver().getRelatedOrNull(teamDef, AtsRelationTypes.AgileTeamToAtsTeam_AgileTeam);
      if (agileTeam != null) {
         List<IAgileFeatureGroup> featureGroups = new LinkedList<>();
         for (ArtifactToken featureGroupArt : services.getRelationResolver().getRelated(agileTeam,
            AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup)) {
            IAgileFeatureGroup featureGroup = services.getConfigItemFactory().getAgileFeatureGroup(featureGroupArt);
            featureGroups.add(featureGroup);
         }
         if (!featureGroups.isEmpty()) {
            Collections.sort(featureGroups, new Comparator<IAgileFeatureGroup>() {

               @Override
               public int compare(IAgileFeatureGroup arg0, IAgileFeatureGroup arg1) {
                  return arg0.getName().compareTo(arg1.getName());
               }
            });
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(ALayout.getZeroMarginLayout(2, false));
            comp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            XComboViewer featureGroupCombo = new XComboViewer("Feature Group", SWT.NONE);
            Collection<Object> objects = new LinkedList<>();
            objects.addAll(featureGroups);
            featureGroupCombo.setInput(objects);
            featureGroupCombo.setFillHorizontally(false);
            featureGroupCombo.createWidgets(comp, 2);
            teamDefFieldToWidget.put(teamDef, WizardFields.FeatureGroup, featureGroupCombo);
         }
      }
   }

   @Override
   public void wizardCompleted(ActionResult actionResult, NewActionWizard wizard, IAtsChangeSet changes) {
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
      XCheckBox unPlanedCheckBox = ((XCheckBox) teamDefFieldToWidget.get(teamDef, WizardFields.UnPlannedWork));
      if (unPlanedCheckBox != null) {
         boolean unplanned = unPlanedCheckBox.isChecked();
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.UnPlannedWork, unplanned);
      }
   }

   private void wizardCompletedFeatureGroup(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XComboViewer featureCombo = ((XComboViewer) teamDefFieldToWidget.get(teamDef, WizardFields.FeatureGroup));
      if (featureCombo != null) {
         IAgileFeatureGroup featureGroup = (IAgileFeatureGroup) featureCombo.getSelected();
         if (featureGroup != null) {
            changes.relate(featureGroup, AtsRelationTypes.AgileFeatureToItem_AtsItem, teamWf);
         }
      }
   }

   private void wizardCompletedTargetedVersion(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XComboViewer versionCombo = ((XComboViewer) teamDefFieldToWidget.get(teamDef, WizardFields.TargetedVersion));
      if (versionCombo != null) {
         IAtsVersion version = (IAtsVersion) versionCombo.getSelected();
         if (version != null) {
            services.getVersionService().setTargetedVersion(teamWf, version, changes);
         }
      }
   }

   private void wizardCompletedSprint(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XComboViewer sprintCombo = ((XComboViewer) teamDefFieldToWidget.get(teamDef, WizardFields.Sprint));
      if (sprintCombo != null) {
         IAgileSprint sprint = (IAgileSprint) sprintCombo.getSelected();
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
            services.getEarnedValueService().setWorkPackage(workPackage, teamWf, changes);
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
      ArtifactToken agileTeamArt = AtsClientService.get().getRelationResolver().getRelatedOrNull(teamDef,
         AtsRelationTypes.AgileTeamToAtsTeam_AgileTeam);
      if (agileTeamArt != null) {
         IAgileTeam agileTeam = AtsClientService.get().getConfigItemFactory().getAgileTeam(agileTeamArt);
         AttributeTypeId agileTeamPointsAttributeType =
            AtsClientService.get().getAgileService().getAgileTeamPointsAttributeType(agileTeam);
         XWidget widget = (XWidget) teamDefFieldToWidget.get(teamDef, agileTeamPointsAttributeType.equals(
            AtsAttributeTypes.Points) ? WizardFields.Points : WizardFields.PointsNumeric);
         if (widget != null) {
            if (widget instanceof XFloat) {
               XFloat xFloat = (XFloat) widget;
               String pointsStr = xFloat.get();
               if (Strings.isNumeric(pointsStr)) {
                  Double points = Double.valueOf(pointsStr);
                  changes.setSoleAttributeValue(teamWf, agileTeamPointsAttributeType, points);
               }
            } else if (widget instanceof XComboViewer) {
               XComboViewer pointsCombo = (XComboViewer) widget;
               String pointsStr = (String) pointsCombo.getSelected();
               if (Strings.isValid(pointsStr)) {
                  changes.setSoleAttributeValue(teamWf, agileTeamPointsAttributeType, pointsStr);
               }
            }
         }
      }
   }

   private void wizardCompletedAssignees(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef) {
      XAssigneesHyperlinkWidget assigneesWidget =
         ((XAssigneesHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.Assignees));
      if (assigneesWidget != null && !assigneesWidget.getSelected().isEmpty()) {
         teamWf.getStateMgr().setAssignees(assigneesWidget.getSelected());
      }
   }

   private void wizardCompletedOriginator(IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      XOriginatorHyperlinkWidget originatorWidget =
         ((XOriginatorHyperlinkWidget) teamDefFieldToWidget.get(teamDef, WizardFields.Originator));
      if (originatorWidget != null && originatorWidget.getSelected() != null) {
         ((AbstractWorkflowArtifact) teamWf.getStoreObject()).setCreatedBy(originatorWidget.getSelected(), true,
            new Date(), changes);
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
               Result result = AtsBranchUtil.createWorkingBranch_Validate(teamArt);
               if (result.isTrue()) {
                  AtsBranchUtil.createWorkingBranch_Create(teamArt);
               }
            }
         }
      }
   }

   @Override
   public String getName() {
      return "MSA Teams";
   }

}
