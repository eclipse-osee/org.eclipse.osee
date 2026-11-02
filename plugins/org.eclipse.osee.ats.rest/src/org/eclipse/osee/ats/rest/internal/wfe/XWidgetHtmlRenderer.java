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
package org.eclipse.osee.ats.rest.internal.wfe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Server-side renderer that walks a WorkDefinition's state layouts and produces HTML. Mirrors the logic of
 * XWidgetWorkDefRenderer.generateLayoutDatas() but outputs HTML instead of SWT widgets.
 * <p>
 * Does NOT depend on any SWT/UI classes.
 *
 * @author Donald G. Dunne
 */
public class XWidgetHtmlRenderer {

   private final AtsApi atsApi;
   private final IAtsWorkItem workItem;
   private final WorkDefinition workDef;
   private final Map<AttributeTypeToken, List<String>> attrValues;
   private final boolean editable;

   /**
    * @param attrValues pre-loaded attribute values for the work item (for performance)
    * @param editable whether to render editable form fields (false for v1)
    */
   public XWidgetHtmlRenderer(AtsApi atsApi, IAtsWorkItem workItem, Map<AttributeTypeToken, List<String>> attrValues, boolean editable) {
      this.atsApi = atsApi;
      this.workItem = workItem;
      this.workDef = workItem.getWorkDefinition();
      this.attrValues = attrValues;
      this.editable = editable;
   }

   /**
    * Renders all visited states as collapsible HTML sections.
    */
   public String renderStates() {
      StringBuilder html = new StringBuilder();
      Collection<String> visitedStates = workItem.getLog().getVisitedStateNames();
      List<StateDefinition> statesOrdered = atsApi.getWorkDefinitionService().getStatesOrderedByOrdinal(workDef);

      // Render in reverse ordinal (most recent state first, like WfeWorkflowSection)
      for (int i = statesOrdered.size() - 1; i >= 0; i--) {
         StateDefinition state = statesOrdered.get(i);
         if (visitedStates.contains(state.getName())) {
            html.append(renderState(state));
         }
      }
      return html.toString();
   }

   /**
    * Renders a single state section with collapsible details element.
    */
   private String renderState(StateDefinition state) {
      StringBuilder html = new StringBuilder();
      boolean isCurrent = state.getName().equals(atsApi.getWorkItemService().getCurrentStateName(workItem));

      String stateTitle = buildStateTitle(state, isCurrent);
      String stateClass = isCurrent ? "wfe-state wfe-state-current" : "wfe-state";

      html.append(String.format("<details class=\"%s\"%s>\n", stateClass, isCurrent ? " open" : ""));
      html.append(String.format("  <summary class=\"wfe-state-title\">%s</summary>\n", stateTitle));
      html.append("  <div class=\"wfe-state-body\">\n");

      // Render state description if present
      if (Strings.isValid(state.getDescription())) {
         html.append(String.format("    <div class=\"wfe-state-desc\">%s</div>\n", esc(state.getDescription())));
      }

      // Render layout items
      renderLayoutItems(html, state.getLayoutItems());

      html.append("  </div>\n");
      html.append("</details>\n");
      return html.toString();
   }

   private String buildStateTitle(StateDefinition state, boolean isCurrent) {
      String name = state.getName();
      if (isCurrent) {
         if (workItem.getCurrentStateType().isCompleted()) {
            return String.format("%s - State Completed %s by %s", name,
               DateUtil.getMMDDYYHHMM(workItem.getCompletedDate()), workItem.getCompletedBy().getName());
         } else if (workItem.getCurrentStateType().isCancelled()) {
            return String.format("%s - Cancelled %s by %s", name, DateUtil.getMMDDYYHHMM(workItem.getCancelledDate()),
               workItem.getCancelledBy().getName());
         }
         return name + " (Current)";
      }
      // Non-current visited state — show completion info from log
      return name + " - State Completed";
   }

   /**
    * Recursively renders layout items (widgets and composites).
    */
   private void renderLayoutItems(StringBuilder html, Collection<LayoutItem> items) {
      for (LayoutItem item : items) {
         if (item instanceof CompositeLayoutItem) {
            renderComposite(html, (CompositeLayoutItem) item);
         } else if (item instanceof WidgetDef) {
            renderWidgetDef(html, (WidgetDef) item);
         }
      }
   }

   /**
    * Renders a composite (multi-column layout) as a CSS grid row.
    */
   private void renderComposite(StringBuilder html, CompositeLayoutItem composite) {
      int cols = composite.getNumColumns();
      html.append(String.format(
         "    <div class=\"wfe-composite\" style=\"display:grid; grid-template-columns:repeat(%d, auto); gap:8px; align-items:center;\">\n",
         cols));
      renderLayoutItems(html, composite.getLayoutItems());
      html.append("    </div>\n");
   }

   /**
    * Renders a single WidgetDef by resolving its WidgetId, label, and current value, then delegating to
    * XWidgetHtmlWidgetFactory. Uses AttributeXWidgetProviderCore to determine widget type from attribute type when
    * WidgetId is not explicitly specified (same as client-side XWidgetWorkDefRenderer).
    */
   private void renderWidgetDef(StringBuilder html, WidgetDef widgetDef) {
      XWidgetData widData = widgetDef.getWidData();
      WidgetId widgetId = widData.getWidgetId();
      String label = widgetDef.getName();
      AttributeTypeToken attrType = widgetDef.getAttributeType();

      // If WidgetId not specified but attribute type is, resolve via AttributeXWidgetProviderCore
      // (mirrors XWidgetWorkDefRenderer.processAttrWidgetDefinition on client)
      if ((widgetId == null || !widgetId.isValid()) && attrType != null && attrType.isValid()) {
         XWidgetData resolved =
            org.eclipse.osee.framework.core.widget.AttributeXWidgetProviderCore.get().getDefaultXWidgetData(
               workItem.getArtifactType(), attrType);
         widgetId = resolved.getWidgetId();
         if (Strings.isValid(resolved.getLabel())) {
            label = resolved.getLabel();
         }
         // Merge resolved options into widData for rendering decisions
         widData = resolved;
      }

      // Override label from widData if specified
      if (Strings.isValid(widgetDef.getWidData().getLabel())) {
         label = widgetDef.getWidData().getLabel();
      }

      // Fallback: if label is still empty, use default from WidgetIdDefaultLabels
      if (!Strings.isValid(label) && widgetId != null) {
         label = org.eclipse.osee.framework.core.widget.WidgetIdDefaultLabels.getLabel(widgetId);
      }

      // Resolve current value from pre-loaded attributes
      Collection<String> values = resolveValues(widgetDef);

      html.append("    ");
      html.append(XWidgetHtmlWidgetFactory.render(widgetId, label, values, widData, editable));
      html.append("\n");
   }

   /**
    * Resolves the current value(s) for a widget from the pre-loaded attribute map.
    */
   private Collection<String> resolveValues(WidgetDef widgetDef) {
      AttributeTypeToken attrType = widgetDef.getAttributeType();
      if (attrType != null && attrType.isValid()) {
         // Try direct map lookup by token
         List<String> vals = attrValues.get(attrType);
         if (vals != null && !vals.isEmpty()) {
            return vals;
         }
         // Try by ID match (handles different token instances for same type)
         for (Map.Entry<AttributeTypeToken, List<String>> entry : attrValues.entrySet()) {
            if (entry.getKey().getId().equals(attrType.getId())) {
               return entry.getValue();
            }
         }
         // Fallback: query directly
         Collection<String> resolved = atsApi.getAttributeResolver().getAttributesToStringList(workItem, attrType);
         if (!resolved.isEmpty()) {
            return resolved;
         }
      }
      // Try matching by widget label name against attribute type unqualified names in the map
      String label = widgetDef.getName();
      if (Strings.isValid(label)) {
         for (Map.Entry<AttributeTypeToken, List<String>> entry : attrValues.entrySet()) {
            if (entry.getKey().getUnqualifiedName().equals(label)) {
               return entry.getValue();
            }
         }
      }
      return Collections.emptyList();
   }

   /**
    * Renders the header definition widgets (extra widgets defined in WorkDef header).
    */
   public String renderHeaderWidgets() {
      if (!workDef.hasHeaderDefinitionItems()) {
         return "";
      }
      StringBuilder html = new StringBuilder();
      renderLayoutItems(html, workDef.getHeaderDef().getLayoutItems());
      return html.toString();
   }

   private static String esc(String text) {
      if (text == null) {
         return "";
      }
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }

}
