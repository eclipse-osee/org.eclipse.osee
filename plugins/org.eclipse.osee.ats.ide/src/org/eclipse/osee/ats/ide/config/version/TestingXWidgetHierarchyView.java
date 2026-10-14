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

package org.eclipse.osee.ats.ide.config.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetService;

/**
 * Displays the class hierarchy for XWidgets registered through OSGi.
 */
public class TestingXWidgetHierarchyView extends XNavigateItemAction {

   public static final String TITLE = "Testing - Open XWidget Hierarchy View";

   private enum Columns {
      Widget,
      AttributeTypeProviders,
      ValueType,
      DefaultXOptions,
      Package,
      Registered;
   }

   private final List<XViewerColumn> columns = Arrays.asList(
      new XViewerColumn(Columns.Widget.name(), "Widget / WidgetId", 280, XViewerAlign.Left, true, SortDataType.String,
         false, ""),
      new XViewerColumn(Columns.AttributeTypeProviders.name(), "Attribute Type Provider(s)", 100, XViewerAlign.Left,
         true, SortDataType.String, false, ""),
      new XViewerColumn(Columns.ValueType.name(), "Value Type", 73, XViewerAlign.Left, true, SortDataType.String, false,
         ""),
      new XViewerColumn(Columns.DefaultXOptions.name(), "Default XOptions", 100, XViewerAlign.Left, true,
         SortDataType.String, false, ""),
      new XViewerColumn(Columns.Package.name(), Columns.Package.name(), 110, XViewerAlign.Left, true,
         SortDataType.String, false, ""),
      new XViewerColumn(Columns.Registered.name(), Columns.Registered.name(), 90, XViewerAlign.Left, true,
         SortDataType.String, false, ""));
   private final Map<Class<? extends XWidget>, WidgetDetails> widgetDetailsByClass = new HashMap<>();

   public TestingXWidgetHierarchyView() {
      super(TITLE, CoreImage.WIDGET, XNavigateItem.TOP_ADMIN_TESTING);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public boolean expandAll() {
            return true;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            return Arrays.<IResultsEditorTab> asList(createHierarchyTab(), createFlatTab());
         }
      });
   }

   private IResultsEditorTab createHierarchyTab() {
      return createDataTab("Hierarchy", createHierarchyRows());
   }

   private IResultsEditorTab createFlatTab() {
      Map<WidgetId, Class<? extends XWidget>> registeredWidgets = XWidgetService.get().getRegisteredWidgetClasses();
      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (Class<? extends XWidget> widgetClass : registeredWidgets.values()) {
         WidgetDetails details = getWidgetDetails(widgetClass);
         rows.add(new ResultsXViewerRow(new String[] {
            widgetClass.getSimpleName(),
            details.attributeTypeProviders,
            details.valueType,
            details.defaultXOptions,
            widgetClass.getPackageName(),
            Boolean.TRUE.toString()}, widgetClass));
      }
      rows.sort(Comparator.comparing(row -> row.getValue(0)));
      return createDataTab("Flat", rows);
   }

   private IResultsEditorTab createDataTab(String tabName, List<IResultsXViewerRow> rows) {
      return new ResultsEditorTableTab(tabName, columns, rows);
   }

   private List<IResultsXViewerRow> createHierarchyRows() {
      Map<WidgetId, Class<? extends XWidget>> registeredWidgets = XWidgetService.get().getRegisteredWidgetClasses();
      Map<Class<? extends XWidget>, Set<WidgetId>> widgetIdsByClass = new HashMap<>();
      for (Map.Entry<WidgetId, Class<? extends XWidget>> entry : registeredWidgets.entrySet()) {
         widgetIdsByClass.computeIfAbsent(entry.getValue(), key -> new LinkedHashSet<>()).add(entry.getKey());
      }

      Map<Class<? extends XWidget>, WidgetRow> rowsByClass = new HashMap<>();
      for (Class<? extends XWidget> widgetClass : widgetIdsByClass.keySet()) {
         addHierarchy(widgetClass, widgetIdsByClass, rowsByClass);
      }

      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (WidgetRow row : rowsByClass.values()) {
         if (row.getParent() == null) {
            rows.add(row);
         }
      }
      rows.sort(Comparator.comparing(row -> ((WidgetRow) row).getWidgetClass().getSimpleName()));
      return rows;
   }

   @SuppressWarnings("unchecked")
   private WidgetRow addHierarchy(Class<? extends XWidget> widgetClass,
      Map<Class<? extends XWidget>, Set<WidgetId>> widgetIdsByClass,
      Map<Class<? extends XWidget>, WidgetRow> rowsByClass) {
      WidgetRow row = rowsByClass.computeIfAbsent(widgetClass, key -> new WidgetRow(key, widgetIdsByClass.get(key)));
      Class<?> superclass = widgetClass.getSuperclass();
      if (superclass != null && XWidget.class.isAssignableFrom(superclass)) {
         WidgetRow parent = addHierarchy((Class<? extends XWidget>) superclass, widgetIdsByClass, rowsByClass);
         parent.addChild(row);
      }
      return row;
   }

   private WidgetDetails getWidgetDetails(Class<? extends XWidget> widgetClass) {
      return widgetDetailsByClass.computeIfAbsent(widgetClass, this::createWidgetDetails);
   }

   private WidgetDetails createWidgetDetails(Class<? extends XWidget> widgetClass) {
      XWidget widget = XWidgetService.get().instantiateXWidget(widgetClass);
      if (widget == null) {
         return WidgetDetails.EMPTY;
      }
      XWidgetData widgetData = widget.getWidData();
      List<String> providers = new ArrayList<>();
      for (AttributeTypeToken provider : widgetData.getProviderFor()) {
         providers.add(provider.getName());
      }
      List<String> valueTypes = new ArrayList<>();
      for (XOption option : XOption.getRequiredValOptions()) {
         if (widgetData.is(option)) {
            valueTypes.add(option.name());
         }
      }
      List<String> xOptions = new ArrayList<>();
      for (XOption option : widgetData.getXOptionHandler().getXOptions()) {
         xOptions.add(option.name());
      }
      xOptions.sort(String::compareTo);
      return new WidgetDetails(providers.isEmpty() ? "(none)" : String.join(", ", providers),
         String.join(", ", valueTypes), xOptions.toString());
   }

   private class WidgetRow extends ResultsXViewerRow {

      private final Class<? extends XWidget> widgetClass;
      private WidgetRow parent;

      WidgetRow(Class<? extends XWidget> widgetClass, Set<WidgetId> widgetIds) {
         this.widgetClass = widgetClass;
         setData(widgetClass);
         WidgetDetails details = widgetIds == null ? WidgetDetails.EMPTY : getWidgetDetails(widgetClass);
         values.add(widgetClass.getSimpleName());
         values.add(details.attributeTypeProviders);
         values.add(details.valueType);
         values.add(details.defaultXOptions);
         values.add(widgetClass.getPackageName());
         values.add(String.valueOf(widgetIds != null));
      }

      void addChild(WidgetRow child) {
         if (!children.contains(child)) {
            children.add(child);
            children.sort(Comparator.comparing(row -> ((WidgetRow) row).widgetClass.getSimpleName()));
            child.parent = this;
         }
      }

      Class<? extends XWidget> getWidgetClass() {
         return widgetClass;
      }

      @Override
      public Collection<IResultsXViewerRow> getChildren() {
         return children;
      }

      @Override
      public IResultsXViewerRow getParent() {
         return parent;
      }
   }

   private static class WidgetDetails {

      private static final WidgetDetails EMPTY = new WidgetDetails("", "", "");

      private final String attributeTypeProviders;
      private final String valueType;
      private final String defaultXOptions;

      WidgetDetails(String attributeTypeProviders, String valueType, String defaultXOptions) {
         this.attributeTypeProviders = attributeTypeProviders;
         this.valueType = valueType;
         this.defaultXOptions = defaultXOptions;
      }
   }
}
