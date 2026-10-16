/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.signby.XXAbstractSignByAndDateButtonArtWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.fund.XXAbstractFundingGoalWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXTargetedVersionWidget;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;

/**
 * Debug action that fills all required widgets in the current state with dummy data to facilitate quick transition
 * during development.
 *
 * @author Donald G. Dunne
 */
public class WfeDebugFillAction {

   private WfeDebugFillAction() {
      // Utility class
   }

   public static void fillCurrentState(WorkflowEditor editor) {
      WfeWorkflowSection currentSection = editor.getWorkFlowTab().getCurrentStateSection();
      if (currentSection == null) {
         return;
      }
      Artifact artifact = editor.getWorkItem();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Debug Fill Required");
      ArtifactId artId = ArtifactId.valueOf(artifact.getId());
      IAtsTeamWorkflow teamWf = artifact instanceof IAtsTeamWorkflow ? (IAtsTeamWorkflow) artifact : null;
      boolean changed = false;

      // Fill required header widgets
      Collection<XWidget> headerWidgets = editor.getWorkFlowTab().getHeader().getXWidgets();
      for (XWidget widget : headerWidgets) {
         try {
            if (widget instanceof XXAbstractBaseXWidget) {
               boolean filled = fillWidget((XXAbstractBaseXWidget<?>) widget, changes, artId, teamWf);
               changed = changed || filled;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Debug fill failed for header widget: " + widget.getLabel(),
               ex);
         }
      }

      // Fill required current state widgets
      List<XWidget> widgets = currentSection.getXWidgets();
      for (XWidget widget : widgets) {
         try {
            if (widget instanceof XXAbstractBaseXWidget) {
               boolean filled = fillWidget((XXAbstractBaseXWidget<?>) widget, changes, artId, teamWf);
               changed = changed || filled;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Debug fill failed for widget: " + widget.getLabel(), ex);
         }
      }

      if (changed || !changes.isEmpty()) {
         changes.executeIfNeeded();
         editor.getWorkFlowTab().refresh();
      }
   }

   private static boolean fillWidget(XXAbstractBaseXWidget<?> widget, IAtsChangeSet changes, ArtifactId artId,
      IAtsTeamWorkflow teamWf) {
      // Only fill required widgets that don't already have a value
      if (widget.isSelected() || !isRequired(widget, teamWf)) {
         return false;
      }
      // Sign-off widgets own their signing action; let the widget apply signed-by user + date to the change set
      if (widget instanceof XXAbstractSignByAndDateButtonArtWidget) {
         ((XXAbstractSignByAndDateButtonArtWidget) widget).setSigned(changes, true);
         return true;
      }
      if (widget instanceof XXTargetedVersionWidget && teamWf != null) {
         return fillTargetedVersion(widget, teamWf, changes);
      }
      if (widget instanceof XXAbstractFundingGoalWidget) {
         return fillFundingGoal((XXAbstractFundingGoalWidget) widget, changes);
      }
      // Skip widgets that aren't backed by an attribute
      AttributeTypeToken attrType = widget.getAttributeType();
      if (!attrType.isValid()) {
         return fillFromSelectable(widget);
      }

      // Fill dummy data by ATTRIBUTE TYPE so the persisted value is valid for the type. Determine the display value
      // the widget holds (text widgets are String-backed; the checkbox is Boolean-backed) via the sentinel.
      Object sentinel = widget.getSentinel();
      if (attrType.isBoolean()) {
         changes.setSoleAttributeValue(artId, attrType, Boolean.TRUE);
         setWidgetSelected(widget, sentinel instanceof Boolean ? Boolean.TRUE : "true");
         return true;
      } else if (attrType.isDouble()) {
         changes.setSoleAttributeValue(artId, attrType, Double.valueOf(1.0));
         setWidgetSelected(widget, "1.0");
         return true;
      } else if (attrType.isInteger()) {
         changes.setSoleAttributeValue(artId, attrType, Integer.valueOf(1));
         setWidgetSelected(widget, "1");
         return true;
      } else if (attrType.isLong()) {
         changes.setSoleAttributeValue(artId, attrType, Long.valueOf(1L));
         setWidgetSelected(widget, "1");
         return true;
      } else if (attrType.isDate()) {
         Date date = new Date();
         changes.setSoleAttributeValue(artId, attrType, date);
         setWidgetSelected(widget, date);
         return true;
      } else if (widget.isValueSingleSelectFromSelectable()) {
         return fillFromSelectable(widget, changes, artId, attrType);
      } else if (sentinel instanceof String) {
         String dummyText = "My " + widget.getLabel();
         changes.setSoleAttributeValue(artId, attrType, dummyText);
         setWidgetSelected(widget, dummyText);
         return true;
      } else {
         return fillFromSelectable(widget);
      }
   }

   private static boolean isRequired(XXAbstractBaseXWidget<?> widget, IAtsTeamWorkflow teamWf) {
      return widget.isRequiredEntry() || widget instanceof XXTargetedVersionWidget && teamWf != null && teamWf.getWorkDefinition().hasOption(
         WorkDefOption.RequireTargetedVersion);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   private static void setWidgetSelected(XXAbstractBaseXWidget<?> widget, Object value) {
      ((XXAbstractBaseXWidget) widget).setSelected(value, false);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   private static boolean fillFromSelectable(XXAbstractBaseXWidget<?> widget) {
      Collection<?> selectable = widget.getSelectable();
      if (selectable != null && !selectable.isEmpty()) {
         Object firstItem = selectable.iterator().next();
         ((XXAbstractBaseXWidget) widget).setSelected(firstItem, false);
         return true;
      }
      return false;
   }

   private static boolean fillFromSelectable(XXAbstractBaseXWidget<?> widget, IAtsChangeSet changes, ArtifactId artId,
      AttributeTypeToken attrType) {
      Collection<?> selectable = widget.getSelectable();
      if (selectable == null || selectable.isEmpty()) {
         return false;
      }
      Object firstItem = selectable.iterator().next();
      if (!(firstItem instanceof String)) {
         return false;
      }
      changes.setSoleAttributeValue(artId, attrType, firstItem);
      setWidgetSelected(widget, firstItem);
      return true;
   }

   private static boolean fillTargetedVersion(XXAbstractBaseXWidget<?> widget, IAtsTeamWorkflow teamWf,
      IAtsChangeSet changes) {
      Collection<?> selectable = widget.getSelectable();
      if (selectable == null || selectable.isEmpty()) {
         return false;
      }
      Object firstItem = selectable.iterator().next();
      if (!(firstItem instanceof ArtifactToken)) {
         return false;
      }
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, (ArtifactToken) firstItem, changes);
      setWidgetSelected(widget, firstItem);
      return true;
   }

   private static boolean fillFundingGoal(XXAbstractFundingGoalWidget widget, IAtsChangeSet changes) {
      Collection<ArtifactToken> selectable = widget.getSelectable();
      if (selectable.isEmpty()) {
         return false;
      }
      ArtifactToken goal = selectable.iterator().next();
      widget.setSelected(goal, false);
      return true;
   }

}
