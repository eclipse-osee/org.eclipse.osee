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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.clone.CloneData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXAssigneesWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXChangeTypeWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXOriginatorWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPriorityWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXAgileFeatureSelWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXAgileSprintSelWidget;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XWidgetsDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXDoubleSTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
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
   public List<XWidgetData> getXWidgetItems() {
      wb = new XWidgetBuilderAts();
      XWidgetBuilderAts wba = (XWidgetBuilderAts) wb;

      // Title
      String defaulTitle = "(Cloned) " + teamWf.getName();
      data.setTitle(defaulTitle);
      wb.andWidget("Title", WidgetId.XXTextWidget).andDefault(defaulTitle);

      String desc = teamWf.getDescription();
      data.setDesc(desc);
      wb.andWidget("Description", WidgetId.XXTextWidget).fillVert().andDefault(desc);

      wb.andCheckBoxWidget("Create New Action with Workflow").andDefault(true) //
         .andToolTip("Un-Check to add Workflow to this Action, otherwise new Action will be created.");

      wb.andSpaceWidget();
      wb.andLabelWidget("Set or clear items for new workflow:");

      AtsUser createdBy = teamWf.getCreatedBy();
      data.setOriginator(createdBy);
      wba.andOriginatorWidget().andDefault(createdBy);

      List<AtsUser> assignees = teamWf.getAssignees();
      data.setAssignees(assignees);
      wba.andAssigneesWidget().andDefault(assignees);
      wba.andOseeImage(AtsImage.USER_SM);

      // Change Type / Priority combos (dynamic lists based on teamWf).
      wba.andWidget("Change Type", WidgetIdAts.XXChangeTypeWidget);

      wba.andWidget("Priority", WidgetIdAts.XXPriorityWidget);

      // Agile Points (float)
      wb.andWidget("Agile Points", WidgetId.XXDoubleSTextWidget);

      // Targeted Version
      wba.andTargetedVersionWidget();

      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
      if (agileTeam != null) {
         // Features
         wba.andAgileFeatureWidget();
         // Sprint
         wba.andSprintWidget();
      }

      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals("Title")) {
         XXTextWidget widget = (XXTextWidget) xWidget;
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setTitle(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Description")) {
         XXTextWidget widget = (XXTextWidget) xWidget;
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setDesc(widget.get());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Assignees")) {
         XXAssigneesWidget widget = (XXAssigneesWidget) xWidget;
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget22) {
               data.setAssignees(widget.getAssignees());
               handleModified();
            }
         });
      } else if (xWidget.isWidget(WidgetIdAts.XXOriginatorWidget)) {
         XXOriginatorWidget widget = (XXOriginatorWidget) xWidget;
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               ArtifactId origArt = ((XXOriginatorWidget) widget2).getSelectedFirst().getArtifactId();
               AtsUser orig = atsApi.getUserService().getUserById(origArt);
               data.setOriginator(orig);
               handleModified();
            }
         });
      } else if (xWidget.isWidget(WidgetIdAts.XXChangeTypeWidget)) {
         XXChangeTypeWidget widget = (XXChangeTypeWidget) xWidget;
         String changeType =
            atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, "");
         widget.setSelected(changeType);
         widget.setSelectable(OseeEnum.toStrings(atsApi.getWorkItemService().getChangeTypeOptions(teamWf)));
         data.setChangeType(changeType);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setChangeType(widget.getSelectedFirst());
               handleModified();
            }
         });
      }
      // TBD dont' think we need this (or any of these) now
      else if (xWidget.isWidget(WidgetIdAts.XXPriorityWidget)) {
         XXPriorityWidget widget = (XXPriorityWidget) xWidget;
         String priority = atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, "");
         widget.setSelected(priority);
         widget.setSelectable(OseeEnum.toStrings(atsApi.getWorkItemService().getPrioritiesOptions(teamWf)));
         data.setPriority(priority);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setPriority(widget.getSelectedFirst());
               handleModified();
            }
         });
      } else if (xWidget.getLabel().equals("Agile Points")) {
         XXDoubleSTextWidget widget = (XXDoubleSTextWidget) xWidget;
         String pointsStr = atsApi.getAgileService().getPointsStr(teamWf);
         widget.setSelected(pointsStr);
         widget.refresh();
         data.setPoints(pointsStr);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setPoints(widget.get());
               handleModified();
            }
         });
      } else if (xWidget instanceof XXAgileFeatureSelWidget) {
         XXAgileFeatureSelWidget widget = (XXAgileFeatureSelWidget) xWidget;
         Collection<IAgileFeatureGroup> features = atsApi.getAgileService().getFeatureGroups(teamWf);
         widget.setSelected(features);
         for (IAgileFeatureGroup feature : atsApi.getAgileService().getFeatureGroups(teamWf)) {
            widget.getSelected().add(feature);
         }
         widget.refresh();
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.getFeatures().clear();
               for (IAgileFeatureGroup feature : widget.getSelected()) {
                  data.getFeatures().add(feature.getName());
               }
               handleModified();
            }
         });
      } else if (xWidget instanceof XXAgileSprintSelWidget) {
         XXAgileSprintSelWidget widget = (XXAgileSprintSelWidget) xWidget;
         IAgileSprint sprint = atsApi.getAgileService().getSprint(teamWf);
         widget.setSelected(sprint);
         widget.refresh();
         data.setSprint(sprint);
         widget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget2) {
               data.setSprint(widget.getSelectedFirst());
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

   @Override
   protected Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

}
