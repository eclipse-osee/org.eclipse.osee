/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.workflow.duplicate;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.clone.CloneData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XAgileFeatureHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XAssigneesHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XOriginatorHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XSprintHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XTargetedVersionHyperlinkWidget;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloat;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XWidgetsDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CloneDialog extends XWidgetsDialog {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private final CloneData data;

   public CloneDialog(String dialogTitle, String dialogMessage, IAtsTeamWorkflow teamWf, CloneData data) {
      super(dialogTitle, dialogMessage);
      this.teamWf = teamWf;
      this.data = data;
      atsApi = AtsApiService.get();
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      // Title
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Title\" id=\"title\"/>");
      // Description
      builder.append(
         "<XWidget xwidgetType=\"XText\" displayName=\"Description\" height=\"60\" id=\"desc\" fill=\"Vertically\" />");

      // New Action
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Create New Action with Workflow\" "//
         + "toolTip=\"Un-Check to add Workflow to this Action, otherwise new Workflow will belong to this Action.\" " //
         + " id=\"newAction\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      // Space
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"   \" />");

      // Label, Originator, Assignees
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Set or clear items for new workflow:\" />");
      builder.append("<XWidget xwidgetType=\"XOriginatorHyperlinkWidget\" displayName=\"Originator\" id=\"orig\" />");
      builder.append("<XWidget xwidgetType=\"XAssigneesHyperlinkWidget\" displayName=\"Assignees\" id=\"assign\" />");

      // Change Type, Priority, Points
      builder.append(
         "<XWidget beginComposite=\"6\" xwidgetType=\"XCombo(" + getChangeTypeOptions() + ")\" horizontalLabel=\"true\" displayName=\"Change Type\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCombo(" + getPriorityOptions() + ")\" horizontalLabel=\"true\" displayName=\"Priority\" />");
      builder.append("<XWidget endComposite=\"true\" xwidgetType=\"XFloat\" displayName=\"Agile Points\" />");

      // Target Version
      builder.append("<XWidget xwidgetType=\"XTargetedVersionHyperlinkWidget\" displayName=\"Targeted Version\" />");

      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);

      if (agileTeam != null) {
         // Features
         builder.append("<XWidget xwidgetType=\"XAgileFeatureHyperlinkWidget\" displayName=\"Agile Feature(s)\" />");
         // Agile Sprint
         builder.append("<XWidget xwidgetType=\"XSprintHyperlinkWidget\" displayName=\"Agile Sprint\" />");
      }

      builder.append("</XWidgets>");
      return builder.toString();
   }

   private String getPriorityOptions() {
      return Collections.toString(",", AtsAttributeTypes.Priority.getEnumStrValues());
   }

   private String getChangeTypeOptions() {
      String cTypeStr = Collections.toString(",", atsApi.getWorkItemService().getChangeTypeOptions(teamWf));
      return cTypeStr;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals("Title")) {
         XText widget = (XText) xWidget;
         String title = "(Cloned) " + teamWf.getName();
         widget.set(title);
         widget.refresh();
         data.setTitle(title);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setTitle(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Description")) {
         XText widget = (XText) xWidget;
         widget.setText(teamWf.getDescription());
         widget.refresh();
         data.setDesc(teamWf.getDescription());
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setDesc(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Assignees")) {
         XAssigneesHyperlinkWidget widget = (XAssigneesHyperlinkWidget) xWidget;
         widget.getSelected().addAll(teamWf.getAssignees());
         widget.refresh();
         data.setAssignees(teamWf.getAssignees());
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setAssignees(widget.getAssignees());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Originator")) {
         XOriginatorHyperlinkWidget widget = (XOriginatorHyperlinkWidget) xWidget;
         widget.setSelected(teamWf.getCreatedBy());
         widget.refresh();
         data.setOriginator(teamWf.getCreatedBy());
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setOriginator(widget.getSelected());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Change Type")) {
         XCombo widget = (XCombo) xWidget;
         String changeType =
            atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, "");
         widget.set(changeType);
         widget.refresh();
         data.setChangeType(changeType);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setChangeType(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Priority")) {
         XCombo widget = (XCombo) xWidget;
         String priority = atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, "");
         widget.set(priority);
         widget.refresh();
         data.setPriority(priority);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setPriority(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Agile Points")) {
         XFloat widget = (XFloat) xWidget;
         String pointsStr = atsApi.getAgileService().getAgileTeamPointsStr(teamWf);
         widget.set(pointsStr);
         widget.refresh();
         data.setPoints(pointsStr);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setPoints(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Targeted Version")) {
         XTargetedVersionHyperlinkWidget widget = (XTargetedVersionHyperlinkWidget) xWidget;
         widget.getSelectable().addAll(atsApi.getVersionService().getVersions(
            atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamWf.getTeamDefinition()),
            VersionReleaseType.UnReleased, VersionLockedType.UnLocked));
         IAtsVersion version = atsApi.getVersionService().getTargetedVersion(teamWf);
         widget.setVersion(version);
         widget.refresh();
         data.setTargetedVersion(version);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setTargetedVersion(widget.getSelected());
               handleModified();
            }
         });
      } else if (xWidget instanceof XAgileFeatureHyperlinkWidget) {
         XAgileFeatureHyperlinkWidget widget = (XAgileFeatureHyperlinkWidget) xWidget;
         widget.setTeamWf(teamWf);
         for (IAgileFeatureGroup feature : atsApi.getAgileService().getFeatureGroups(teamWf)) {
            widget.getFeatures().add(feature);
            data.getFeatures().add(feature.getName());
         }
         widget.refresh();
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.getFeatures().clear();
               for (IAgileFeatureGroup feature : widget.getFeatures()) {
                  data.getFeatures().add(feature.getName());
               }
               handleModified();
            }
         });
      } else if (xWidget instanceof XSprintHyperlinkWidget) {
         XSprintHyperlinkWidget widget = (XSprintHyperlinkWidget) xWidget;
         widget.setTeamWf(teamWf);
         IAgileSprint sprint = atsApi.getAgileService().getSprint(teamWf);
         widget.setSprint(sprint);
         widget.refresh();
         data.setSprint(sprint);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setSprint(widget.getSelected());
               handleModified();
            }
         });
      }
   }

   @Override
   public boolean isEntryValid() {
      if (Strings.isInValid(data.getTitle())) {
         logError("Title must be entered");
         return false;
      }
      if (Strings.isInValid(data.getDesc())) {
         logError("Description must be entered");
         return false;
      }
      if (Strings.isInValid(data.getPriority())) {
         logError("Priority must be selected");
         return false;
      }
      if (Strings.isInValid(data.getChangeType())) {
         logError("Change Type must be selected");
         return false;
      }
      if (data.getOriginator() == null) {
         logError("Originator must be selected");
         return false;
      }
      logError("");
      return true;
   }

}
