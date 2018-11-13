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
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.UniversalCellEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public class AttributesComposite extends Composite implements IArtifactEventListener {
   private TableViewer tableViewer;
   private Table table;
   private Text helpText;
   private static final String[] columnNames = new String[] {"name", "value", "attrId", "gammaId"};
   private static final Integer[] columnWidths = new Integer[] {200, 400, 70, 70};
   private Artifact artifact;
   private final IDirtiableEditor editor;
   private Label warningLabel;
   private final ArrayList<ModifyAttributesListener> modifyAttrListeners = new ArrayList<>();
   private MenuItem deleteItem;
   private final ToolBar toolBar;

   public static final int NAME_COLUMN_INDEX = 0;
   public static final int VALUE_COLUMN_INDEX = 1;

   public AttributesComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact) {
      this(editor, parent, style, artifact, null);
   }

   public AttributesComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact, ToolBar toolBar) {
      super(parent, style);
      this.artifact = artifact;
      this.editor = editor;

      create(this);
      Menu popupMenu = new Menu(parent);
      createAddMenuItem(popupMenu);
      createDeleteMenuItem(popupMenu);
      popupMenu.addMenuListener(new AttributeMenuListener());
      tableViewer.getTable().setMenu(popupMenu);

      OseeEventManager.addListener(this);

      this.toolBar = toolBar;
   }

   public void updateLabel(String msg) {
      warningLabel.setText(msg);
      layout();
   }

   private void create(Composite parent) {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      SashForm mainSash = new SashForm(this, SWT.NONE);
      mainSash.setLayout(new GridLayout());
      mainSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainSash.setOrientation(SWT.VERTICAL);

      createTableArea(mainSash);

      SashForm sashForm = new SashForm(mainSash, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sashForm.setOrientation(SWT.HORIZONTAL);

      createWarningArea(sashForm);
      createHelpArea(sashForm);

      mainSash.setWeights(new int[] {8, 2});
      sashForm.setWeights(new int[] {5, 5});

      HelpUtil.setHelp(tableViewer.getControl(), OseeHelpContext.ARTIFACT_EDITOR__ATTRIBUTES);
   }

   private void createTableArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
      composite.setText("Attributes");

      createTable(composite);
      createColumns();
      createTableViewer(composite);

      tableViewer.refresh();
      attachTableListeners();
   }

   private void createTable(Composite parent) {
      table = new Table(parent,
         SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      table.setLinesVisible(true);
      table.setHeaderVisible(true);
   }

   private void attachTableListeners() {
      tableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object selected = selection.getFirstElement();

            if (selected instanceof Attribute<?>) {
               Attribute<?> attribute = (Attribute<?>) selected;
               String tipText = attribute.getAttributeType().getDescription();
               if (tipText != null && !tipText.equals("null")) {
                  helpText.setText(tipText);
               } else {
                  helpText.setText("");
               }
            }
         }
      });
   }

   public TableViewer getTableViewer() {
      return tableViewer;
   }

   private void createTableViewer(Composite parent) {
      tableViewer = new TableViewer(table);

      TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
         ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
      tableViewer.setUseHashlookup(true);
      tableViewer.setColumnProperties(columnNames);

      if (!artifact.isReadOnly()) {
         CellEditor[] editors = new CellEditor[columnNames.length];
         editors[VALUE_COLUMN_INDEX] = new UniversalCellEditor(table, SWT.NONE);

         tableViewer.setCellEditors(editors);
         tableViewer.setCellModifier(new AttributeCellModifier(editor, tableViewer, this));
      }
      tableViewer.setContentProvider(new AttributeContentProvider());
      tableViewer.setLabelProvider(new AttributeLabelProvider());
      tableViewer.setComparator(new AttributeNameSorter());
      load();
   }

   public void load() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (tableViewer != null && Widgets.isAccessible(tableViewer.getTable())) {
               tableViewer.setInput(artifact);
            }
         }
      });
   }

   public class AttributeNameSorter extends ViewerComparator {

      public AttributeNameSorter() {
         super();
      }

      @Override
      public int compare(Viewer viewer, Object o1, Object o2) {
         if (o1 instanceof Attribute && o2 instanceof Attribute) {
            return getComparator().compare(((Attribute<?>) o1).getAttributeType().getName(),
               ((Attribute<?>) o2).getAttributeType().getName());
         } else if (o1 instanceof String && o2 instanceof String) {
            return getComparator().compare((String) o1, (String) o2);
         }
         return super.compare(viewer, o1, o2);
      }

   }

   private void createColumns() {
      for (int index = 0; index < columnNames.length; index++) {
         TableColumn column = new TableColumn(table, SWT.LEFT, index);
         column.setText(columnNames[index]);
         column.setWidth(columnWidths[index]);
      }
   }

   private void createHelpArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Tips");

      helpText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
      helpText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   private void createWarningArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Warnings");

      warningLabel = new Label(composite, SWT.NONE);
      warningLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      warningLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      updateLabel("");
   }

   private void createAddMenuItem(Menu parentMenu) {
      MenuItem addItem = new MenuItem(parentMenu, SWT.PUSH);
      addItem.setText("Add");
      addItem.setEnabled(true && !artifact.isReadOnly());
      addItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Collection<AttributeTypeToken> selectableTypes = new ArrayList<>();
            try {
               for (AttributeTypeToken attrType : artifact.getAttributeTypes()) {
                  if (artifact.getRemainingAttributeCount(attrType) > 0) {
                     selectableTypes.add(attrType);
                  }
               }
               FilteredCheckboxAttributeTypeDialog dialog = new FilteredCheckboxAttributeTypeDialog(
                  "Select Attribute Types", "Select attribute types to display.");
               dialog.setSelectable(selectableTypes);
               if (dialog.open() == 0) {
                  for (Object obj : dialog.getResult()) {
                     getArtifact().addAttribute((AttributeTypeId) obj);
                  }
                  tableViewer.refresh();
                  layout();
                  getParent().layout();
                  editor.onDirtied();
                  notifyModifyAttribuesListeners();
               }

            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }

   private void createDeleteMenuItem(Menu parentMenu) {
      deleteItem = new MenuItem(parentMenu, SWT.PUSH);
      deleteItem.setImage(null);
      deleteItem.setText("Delete");
      deleteItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               Attribute<?> attribute = getSelectedAttribute();
               attribute.delete();
               editor.onDirtied();
               notifyModifyAttribuesListeners();
               tableViewer.refresh();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   private Attribute<?> getSelectedAttribute() {
      TableItem[] items = tableViewer.getTable().getSelection();
      if (items.length > 0) {
         return (Attribute<?>) tableViewer.getTable().getSelection()[0].getData();
      } else {
         return null;
      }
   }

   public class AttributeMenuListener implements MenuListener {
      @Override
      public void menuHidden(MenuEvent e) {
         // do nothing
      }

      @Override
      public void menuShown(MenuEvent e) {
         Attribute<?> attribute = getSelectedAttribute();

         if (attribute == null) {
            deleteItem.setText("Delete - No Attribute Selected");
            deleteItem.setEnabled(false);
         } else if (!attribute.canDelete()) {
            deleteItem.setText("Delete - Lower Limit Met");
            deleteItem.setEnabled(false);
         } else {
            deleteItem.setText("Delete");
            deleteItem.setEnabled(!artifact.isReadOnly());
         }
      }
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void refreshArtifact(Artifact artifact) {
      this.artifact = artifact;

      if (tableViewer.getContentProvider() != null) {
         tableViewer.setInput(artifact);
         tableViewer.refresh();
      }
   }

   public void addModifyAttributesListener(ModifyAttributesListener listener) {
      if (!modifyAttrListeners.contains(listener)) {
         modifyAttrListeners.add(listener);
      }
   }

   public void removeModifyAttributesListener(ModifyAttributesListener listener) {
      modifyAttrListeners.remove(listener);
   }

   public void notifyModifyAttribuesListeners() {
      for (ModifyAttributesListener listener : modifyAttrListeners) {
         listener.handleEvent();
      }
   }

   public ToolBar getToolBar() {
      return toolBar;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Collections.singletonList(new ArtifactEventFilter(artifact));
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      load();
   }
}
