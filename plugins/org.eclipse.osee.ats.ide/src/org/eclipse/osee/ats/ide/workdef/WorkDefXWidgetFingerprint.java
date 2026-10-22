/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/

package org.eclipse.osee.ats.ide.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * Produces a stable, runtime-object-free representation of resolved WorkDef widget data for regression snapshots.
 */
public final class WorkDefXWidgetFingerprint {

   private WorkDefXWidgetFingerprint() {
      // Utility class
   }

   public static String create(AbstractWorkflowArtifact workflow) {
      WorkDefinition workDefinition = workflow.getWorkDefinition();
      StringBuilder builder = new StringBuilder();
      builder.append("workDef=").append(workDefinition.toStringWithId()).append("\n");
      Collection<LayoutItem> headerItems =
         workDefinition.hasHeaderDefinitionItems() ? workDefinition.getHeaderDef().getLayoutItems() :
            workDefinition.getDefaultHeaderDef().getLayoutItems();
      append(builder, "header", resolve(workflow, workDefinition, null, headerItems));
      for (StateDefinition stateDefinition : workDefinition.getStates()) {
         append(builder, "state=" + stateDefinition.getName(),
            resolve(workflow, workDefinition, stateDefinition, stateDefinition.getLayoutItems()));
      }
      return builder.toString();
   }

   public static Map<String, Object> createData(AbstractWorkflowArtifact workflow) {
      return createData(workflow.getWorkDefinition(), workflow.getArtifactType());
   }

   public static Map<String, Object> createData(WorkDefinition workDefinition, ArtifactTypeToken artifactType) {
      Map<String, Object> workDefData = new LinkedHashMap<>();
      workDefData.put("id", workDefinition.getId());
      workDefData.put("name", workDefinition.getName());
      List<Map<String, Object>> sections = new ArrayList<>();
      Collection<LayoutItem> headerItems =
         workDefinition.hasHeaderDefinitionItems() ? workDefinition.getHeaderDef().getLayoutItems() :
            workDefinition.getDefaultHeaderDef().getLayoutItems();
      sections.add(section("header", resolve(workDefinition, artifactType, null, headerItems)));
      for (StateDefinition stateDefinition : workDefinition.getStates()) {
         sections.add(section("state", stateDefinition.getName(),
            resolve(workDefinition, artifactType, stateDefinition, stateDefinition.getLayoutItems())));
      }
      workDefData.put("sections", sections);
      return workDefData;
   }

   public static String create(Collection<XWidgetData> widDatas) {
      StringBuilder builder = new StringBuilder();
      int index = 0;
      for (XWidgetData widData : widDatas) {
         builder.append(String.format("[%d] label=%s%n", index++, widData.getLabel()));
         append(builder, "widgetId", widData.getWidgetId());
         append(builder, "attributeType", widData.getAttributeType());
         append(builder, "attributeType2", widData.getAttributeType2());
         append(builder, "relation", widData.getRelationTypeSide());
         append(builder, "enumArtifact", widData.getEnumeratedArt());
         append(builder, "options", widData.getXOptionHandler().getXOptions().stream().map(XOption::name).sorted().collect(
            Collectors.joining(",")));
         append(builder, "beginComposite", widData.getBeginComposite());
         append(builder, "endComposite", widData.isEndComposite());
         append(builder, "beginGroupComposite", widData.getBeginGroupComposite());
         append(builder, "endGroupComposite", widData.isEndGroupComposite());
         append(builder, "height", widData.getHeight());
         append(builder, "defaultValue", stableValue(widData.getDefaultValue()));
         append(builder, "conditions", widData.getConditions().stream().map(condition -> condition.getClass().getName()).sorted().collect(
            Collectors.joining(",")));
      }
      return builder.toString();
   }

   private static Collection<XWidgetData> resolve(AbstractWorkflowArtifact workflow, WorkDefinition workDefinition,
      StateDefinition stateDefinition, Collection<LayoutItem> layoutItems) {
      XWidgetWorkDefRenderer renderer =
         new XWidgetWorkDefRenderer(workflow, workDefinition, stateDefinition, layoutItems);
      return renderer.getResolvedXWidgetDatas();
   }

   private static Collection<XWidgetData> resolve(WorkDefinition workDefinition, ArtifactTypeToken artifactType,
      StateDefinition stateDefinition, Collection<LayoutItem> layoutItems) {
      return new XWidgetWorkDefRenderer(artifactType, workDefinition, stateDefinition, layoutItems).getResolvedXWidgetDatas();
   }

   private static void append(StringBuilder builder, String section, Collection<XWidgetData> widDatas) {
      builder.append("\n[").append(section).append("]\n");
      builder.append(create(widDatas));
   }

   private static Map<String, Object> section(String type, Collection<XWidgetData> widDatas) {
      return section(type, null, widDatas);
   }

   private static Map<String, Object> section(String type, String name, Collection<XWidgetData> widDatas) {
      Map<String, Object> section = new LinkedHashMap<>();
      section.put("type", type);
      if (name != null) {
         section.put("name", name);
      }
      List<Map<String, Object>> widgets = new ArrayList<>();
      for (XWidgetData widData : widDatas) {
         widgets.add(widget(widData));
      }
      section.put("widgets", widgets);
      return section;
   }

   private static Map<String, Object> widget(XWidgetData widData) {
      Map<String, Object> widget = new LinkedHashMap<>();
      widget.put("label", widData.getLabel());
      widget.put("widgetId", widData.getWidgetId().getName());
      widget.put("attributeType", stringValue(widData.getAttributeType()));
      widget.put("attributeType2", stringValue(widData.getAttributeType2()));
      widget.put("relation", stringValue(widData.getRelationTypeSide()));
      widget.put("enumArtifact", stringValue(widData.getEnumeratedArt()));
      widget.put("options", widData.getXOptionHandler().getXOptions().stream().map(XOption::name).sorted().collect(
         Collectors.toList()));
      widget.put("beginComposite", widData.getBeginComposite());
      widget.put("endComposite", widData.isEndComposite());
      widget.put("beginGroupComposite", widData.getBeginGroupComposite());
      widget.put("endGroupComposite", widData.isEndGroupComposite());
      widget.put("height", widData.getHeight());
      widget.put("defaultValue", stableValue(widData.getDefaultValue()));
      widget.put("conditions", widData.getConditions().stream().map(condition -> condition.getClass().getName()).sorted().collect(
         Collectors.toList()));
      return widget;
   }

   private static void append(StringBuilder builder, String name, Object value) {
      builder.append("  ").append(name).append("=").append(value == null ? "" : value).append("\n");
   }

   private static Object stableValue(Object value) {
      if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean) {
         return value;
      }
      return value instanceof java.util.Date ? value.getClass().getName() : value.toString();
   }

   private static String stringValue(Object value) {
      return value == null ? "" : value.toString();
   }
}
