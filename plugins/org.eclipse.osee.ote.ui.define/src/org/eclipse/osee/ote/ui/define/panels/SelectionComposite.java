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
package org.eclipse.osee.ote.ui.define.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class SelectionComposite extends Composite {
   private static final Image CONFLICT_IMAGE = ImageManager.getImage(OteDefineImage.OBSTRUCTED);
   private static final Image CHECKED_IMAGE = ImageManager.getImage(OteDefineImage.CHECKBOX_ENABLED);
   private static final Image UNCHECKED_IMAGE = ImageManager.getImage(OteDefineImage.CHECKBOX_DISABLED);
   private static Image CHECK_OVERRIDEN_IMAGE = null;
   private static Image UNCHECKED_OVERRIDEN_IMAGE = null;

   private final ITableLabelProvider tableLabelProvider;
   private TableViewer tableViewer;
   private final Object[] resources;
   private final boolean isSelectAllByDefault;
   private final Map<Object, MutableBoolean> selectableMap;
   private final Set<Object> userSelectedResources = new HashSet<>();
   private final Set<Object> notSelectableResources = new HashSet<>();
   private final Set<Object> overridable = new HashSet<>();
   private final String[] columnNames;
   private Label lblSelectedResourcesNumber;
   private IOverrideHandler overrideHandler;
   private boolean areOverridesAllowed;

   public SelectionComposite(Composite parent, int style, String[] columnNames, ITableLabelProvider tableLabelProvider, Object[] resources, boolean isSelectAllByDefault, IOverrideHandler overrideHandler) {
      this(parent, style, columnNames, tableLabelProvider, resources, isSelectAllByDefault, null, null,
         overrideHandler);
   }

   public SelectionComposite(Composite parent, int style, String[] columnNames, ITableLabelProvider tableLabelProvider, Object[] resources, boolean isSelectAllByDefault, Object[] userSelectedResources, Object[] unSelectableResources, IOverrideHandler overrideHandler) {
      super(parent, style);
      this.columnNames = columnNames;
      this.tableLabelProvider = tableLabelProvider;
      this.selectableMap = new HashMap<>();
      this.resources = resources;
      this.isSelectAllByDefault = isSelectAllByDefault;
      if (userSelectedResources != null) {
         this.userSelectedResources.addAll(Arrays.asList(userSelectedResources));
      }
      if (unSelectableResources != null) {
         this.notSelectableResources.addAll(Arrays.asList(unSelectableResources));
      }
      this.overrideHandler = overrideHandler;
      if (overrideHandler != null) {
         try {
            this.overridable.addAll(overrideHandler.getOverridableFromUnselectable(notSelectableResources));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      this.areOverridesAllowed = false;
      createControls();
   }

   private void createControls() {
      GridLayout gridLayout = new GridLayout();
      gridLayout.marginHeight = 0;
      gridLayout.marginWidth = 0;
      this.setLayout(gridLayout);

      createTableArea(this);
      Composite buttonComposite = createButtonArea(this);
      createLabelArea(buttonComposite);
   }

   private void createTableArea(Composite parent) {
      int style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER;
      Table table = new Table(parent, style);
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      TableLayout layout = new TableLayout();
      table.setLayout(layout);

      this.tableViewer = new TableViewer(table);
      this.tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
      this.tableViewer.setLabelProvider(this.tableLabelProvider);
      this.tableViewer.setContentProvider(new TableContentProvider());
      this.tableViewer.setSorter(new ViewerSorter());

      TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
      viewerColumn.setEditingSupport(new CheckColumnEditingSupport(tableViewer));
      viewerColumn.setLabelProvider(new CheckCellLabelProvider());

      createColumns();

      initializeSelections();
      this.tableViewer.setInput(this.resources);
      packColumnData();
   }

   private void packColumnData() {
      TableColumn[] columns = tableViewer.getTable().getColumns();
      for (TableColumn column : columns) {
         column.pack();
      }
   }

   private void createColumns() {
      Table table = tableViewer.getTable();
      for (String item : columnNames) {
         TableColumn column = new TableColumn(table, SWT.NONE);
         column.setResizable(true);
         column.setText(item);
      }
   }

   private void createLabelArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.horizontalSpacing = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      this.lblSelectedResourcesNumber = new Label(composite, SWT.RIGHT);
      this.lblSelectedResourcesNumber.setText(selectItemsToString(getSelectedCount()));
      this.lblSelectedResourcesNumber.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
   }

   private Composite createButtonArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.RIGHT);
      GridLayout gLayout = new GridLayout();
      gLayout.numColumns = overrideHandler != null ? 4 : 3;
      gLayout.marginWidth = 0;
      composite.setLayout(gLayout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Button selectButton = new Button(composite, SWT.PUSH);
      selectButton.setText("Select All");
      selectButton.setLayoutData(new GridData());
      selectButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setAllItemsChecked(true);
         }
      });

      Button deselectButton = new Button(composite, SWT.PUSH);
      deselectButton.setText("Deselect All");
      deselectButton.setLayoutData(new GridData());
      deselectButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setAllItemsChecked(false);
         }
      });

      if (overrideHandler != null) {
         final Button overrideButton = new Button(composite, SWT.CHECK);
         overrideButton.setText(overrideHandler.getText());
         overrideButton.setToolTipText(overrideHandler.getToolTipText());
         overrideButton.setLayoutData(new GridData());
         overrideButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               areOverridesAllowed = overrideButton.getSelection();
               if (areOverridesAllowed != false) {
                  for (Object object : overridable) {
                     selectableMap.put(object, new MutableBoolean(false));
                  }
               } else {
                  for (Object object : overridable) {
                     selectableMap.remove(object);
                  }
               }
               refresh();
            }
         });
      }
      return composite;
   }

   private void setAllItemsChecked(boolean state) {
      for (Object key : selectableMap.keySet()) {
         MutableBoolean mutable = selectableMap.get(key);
         mutable.setValue(state);
         selectableMap.put(key, mutable);
      }
      refresh();
   }

   private void refresh() {
      lblSelectedResourcesNumber.setText(selectItemsToString(getSelectedCount()));
      tableViewer.refresh();
   }

   protected void initializeSelections() {
      for (Object object : resources) {
         if (notSelectableResources.contains(object) != true) {
            boolean initialValue = userSelectedResources.contains(object) || isSelectAllByDefault;
            selectableMap.put(object, new MutableBoolean(initialValue));
         }
      }
   }

   protected String selectItemsToString(int value) {
      return String.format("Selected: %s of %s", String.valueOf(value), String.valueOf(resources.length));
   }

   public void addOverrideHandler(IOverrideHandler overrideHandler) {
      this.overrideHandler = overrideHandler;
   }

   public Object[] getSelectedResources() {
      return getSelectedItems(true);
   }

   public Object[] getNotSelectedResources() {
      return getSelectedItems(false);
   }

   private Object[] getSelectedItems(boolean thatMatchTrue) {
      List<Object> toReturn = new ArrayList<>();
      for (Object key : selectableMap.keySet()) {
         if (selectableMap.get(key).getValue() == thatMatchTrue) {
            toReturn.add(key);
         }
      }
      return toReturn.toArray(new Object[toReturn.size()]);
   }

   public int getSelectedCount() {
      int count = 0;
      for (MutableBoolean value : selectableMap.values()) {
         if (value.getValue() == true) {
            count++;
         }
      }
      return count;
   }

   private boolean isOverridable(Object element) {
      return overridable.contains(element) != false && areOverridesAllowed != false;
   }

   private final class TableContentProvider implements IStructuredContentProvider {
      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
         // do nothing
      }

      @Override
      public Object[] getElements(Object inputElement) {
         return resources;
      }
   }

   private final class CheckCellLabelProvider extends CellLabelProvider {

      public CheckCellLabelProvider() {
         super();
      }

      @Override
      public void update(ViewerCell cell) {
         if (cell.getColumnIndex() == 0) {
            Object data = cell.getItem().getData();
            if (notSelectableResources.contains(data) && isOverridable(data) != true) {
               cell.setImage(CONFLICT_IMAGE);
            } else {
               MutableBoolean isSelectedObject = selectableMap.get(data);
               boolean isSelected = isSelectedObject != null && isSelectedObject.getValue() == true;
               boolean isOverriden = overridable.contains(data);
               cell.setImage(getImage(isSelected, isOverriden));
            }
         }
      }

      private Image getImage(boolean isChecked, boolean isOverriden) {
         Image toReturn = isChecked != false ? CHECKED_IMAGE : UNCHECKED_IMAGE;
         if (isOverriden != false) {
            if (CHECK_OVERRIDEN_IMAGE == null || UNCHECKED_OVERRIDEN_IMAGE == null) {
               DecorationOverlayIcon overlay = new DecorationOverlayIcon(toReturn,
                  ImageManager.getImageDescriptor(OteDefineImage.SWITCHED), IDecoration.BOTTOM_RIGHT);

               Image overlayedImage = overlay.createImage();
               if (isChecked != false) {
                  CHECK_OVERRIDEN_IMAGE = overlayedImage;
               } else {
                  UNCHECKED_OVERRIDEN_IMAGE = overlayedImage;
               }
            }
            toReturn = isChecked != false ? CHECK_OVERRIDEN_IMAGE : UNCHECKED_OVERRIDEN_IMAGE;
         }
         return toReturn;
      }
   }

   private final class CheckColumnEditingSupport extends EditingSupport {

      private final CheckboxCellEditor editor;

      public CheckColumnEditingSupport(ColumnViewer viewer) {
         super(viewer);
         this.editor = new CheckboxCellEditor((Composite) viewer.getControl());
         viewer.setCellEditors(new CellEditor[] {editor});
      }

      @Override
      protected boolean canEdit(Object element) {
         return notSelectableResources.contains(element) != true || overridable.contains(element);
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
         return editor;
      }

      @Override
      protected Object getValue(Object element) {
         MutableBoolean value = selectableMap.get(element);
         return value != null ? value.getValue() : false;
      }

      @Override
      protected void setValue(Object element, Object value) {
         MutableBoolean object = selectableMap.get(element);
         if (object != null) {
            object.setValue((Boolean) value);
         }
         refresh();
      }
   }
}
