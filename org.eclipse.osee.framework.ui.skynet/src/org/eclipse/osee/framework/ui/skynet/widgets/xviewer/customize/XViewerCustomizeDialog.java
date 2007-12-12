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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class XViewerCustomizeDialog extends MessageDialog {
   private String title = "Customize Table";
   private static String buttons[] = new String[] {"Ok", "Apply", "Cancel"};
   private final XViewer xViewer;
   private TableViewer custTable;
   private TableViewer hiddenColTable;
   private TableViewer visibleColTable;
   private CustomizeData currentCustomizeData;
   private Text sorterText;
   private Text filterText;
   // Select Customization Buttons
   Button setDefaultButton, deleteButton;
   // Config Customization Buttons - Moving items
   Button addItemButton, addAllItemButton, removeItemButton, removeAllItemButton, moveUpButton, moveDownButton;
   // Config Customization Buttons
   Button saveButton, renameButton;
   private static String SET_AS_DEFAULT = " Set as Default ";
   private static String REMOVE_DEFAULT = "Remove Default";
   private CustomizeData defaultTableCustData;

   public XViewerCustomizeDialog(CustomizeData currentCustomizeData, XViewer xViewer) {
      this(currentCustomizeData, xViewer, Display.getCurrent().getActiveShell(), buttons, 0);
   }

   private XViewerCustomizeDialog(CustomizeData currentCustomizeData, XViewer xViewer, Shell parentShell, String[] buttons, int defaultButton) {
      super(parentShell, "", null, "", MessageDialog.NONE, buttons, defaultButton);
      this.currentCustomizeData = currentCustomizeData;
      this.xViewer = xViewer;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   protected Control createDialogArea(Composite parent) {

      getShell().setText(title);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      SkynetGuiPlugin.getInstance().setHelp(parent, "table_customization");

      final Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      final GridLayout gridLayout_2 = new GridLayout();
      gridLayout_2.numColumns = 2;
      comp.setLayout(gridLayout_2);

      final Label selectCustomizationLabel = new Label(comp, SWT.NONE);
      selectCustomizationLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      selectCustomizationLabel.setText("Select Customization");

      // Column Configuration
      final Group configureColumnsGroup = new Group(comp, SWT.NONE);
      configureColumnsGroup.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 3));
      configureColumnsGroup.setText("Configure Customization");
      final GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 3;
      gridLayout.marginHeight = 3;
      gridLayout.numColumns = 3;
      configureColumnsGroup.setLayout(gridLayout);

      final Composite hiddenTableComp = new Composite(configureColumnsGroup, SWT.NONE);
      hiddenTableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      final GridLayout gridLayout_4 = new GridLayout();
      gridLayout_4.marginWidth = 0;
      gridLayout_4.marginHeight = 0;
      hiddenTableComp.setLayout(gridLayout_4);

      final Label hiddenColumnsLabel = new Label(hiddenTableComp, SWT.NONE);
      hiddenColumnsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      hiddenColumnsLabel.setText("Hidden Columns");

      // Hidden Column Table
      hiddenColTable = new TableViewer(hiddenTableComp, SWT.BORDER | SWT.MULTI);
      final Table table_1 = hiddenColTable.getTable();
      final GridData gd_table_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
      gd_table_1.widthHint = 150;
      table_1.setLayoutData(gd_table_1);
      hiddenColTable.setLabelProvider(new XViewerColumnLabelProvider());
      hiddenColTable.setContentProvider(new ArrayTreeContentProvider());
      hiddenColTable.setSorter(new ViewerSorter() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
          *      java.lang.Object, java.lang.Object)
          */
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((XViewerColumn) e1).getNameAlternate(),
                  ((XViewerColumn) e2).getNameAlternate());
         }
      });
      hiddenColTable.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            updateButtonEnablements();
         }
      });

      final Composite moveButtonComp = new Composite(configureColumnsGroup, SWT.NONE);
      moveButtonComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      final GridLayout gridLayout_5 = new GridLayout();
      gridLayout_5.marginWidth = 0;
      gridLayout_5.marginHeight = 0;
      moveButtonComp.setLayout(gridLayout_5);

      addItemButton = new Button(moveButtonComp, SWT.NONE);
      addItemButton.setText(">");
      addItemButton.setToolTipText("Add");
      addItemButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      addItemButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleAddItemButton();
         }
      });

      addAllItemButton = new Button(moveButtonComp, SWT.NONE);
      addAllItemButton.setText(">>");
      addAllItemButton.setToolTipText("Add All");
      addAllItemButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleAddAllItemButton();
         }
      });

      removeItemButton = new Button(moveButtonComp, SWT.NONE);
      removeItemButton.setText("<");
      removeItemButton.setToolTipText("Remove");
      removeItemButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      removeItemButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleRemoveItemButton();
         }
      });

      removeAllItemButton = new Button(moveButtonComp, SWT.NONE);
      removeAllItemButton.setText("<<");
      removeAllItemButton.setToolTipText("Remove All");
      removeAllItemButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleRemoveAllItemButton();
         }
      });

      moveUpButton = new Button(moveButtonComp, SWT.NONE);
      moveUpButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      moveUpButton.setText("^");
      moveUpButton.setToolTipText("Move Up");
      moveUpButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleMoveUpButton();
         }
      });

      moveDownButton = new Button(moveButtonComp, SWT.NONE);
      moveDownButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      moveDownButton.setText("v");
      moveDownButton.setToolTipText("Move Down");
      moveDownButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleMoveDownButton();
         }
      });

      final Composite visibleTableComp = new Composite(configureColumnsGroup, SWT.NONE);
      visibleTableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      final GridLayout gridLayout_6 = new GridLayout();
      gridLayout_6.marginWidth = 0;
      gridLayout_6.marginHeight = 0;
      visibleTableComp.setLayout(gridLayout_6);

      final Label visibleColumnsLabel = new Label(visibleTableComp, SWT.NONE);
      visibleColumnsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      visibleColumnsLabel.setText("Visible Columns");

      // Visible Column Table
      visibleColTable = new TableViewer(visibleTableComp, SWT.BORDER | SWT.MULTI);
      final Table table = visibleColTable.getTable();
      final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd_table.widthHint = 150;
      table.setLayoutData(gd_table);
      visibleColTable.setLabelProvider(new XViewerColumnLabelProvider());
      visibleColTable.setContentProvider(new ArrayTreeContentProvider());
      visibleColTable.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            updateButtonEnablements();
         }
      });
      gridLayout.numColumns = 3;
      gridLayout.numColumns = 3;

      // Sorter text block
      final Composite composite_2 = new Composite(configureColumnsGroup, SWT.NONE);
      composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
      final GridLayout gridLayout_3 = new GridLayout();
      gridLayout_3.numColumns = 3;
      composite_2.setLayout(gridLayout_3);

      final Label sorterLabel = new Label(composite_2, SWT.NONE);
      sorterLabel.setText("Sorter:");

      sorterText = new Text(composite_2, SWT.BORDER);
      sorterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      final Label clearSorterLabel = new Label(composite_2, SWT.PUSH);
      clearSorterLabel.setImage(SkynetGuiPlugin.getInstance().getImage("clear.gif"));
      clearSorterLabel.addMouseListener(new MouseListener() {
         public void mouseDown(MouseEvent e) {
         }

         public void mouseDoubleClick(MouseEvent e) {

         }

         public void mouseUp(MouseEvent e) {
            sorterText.setText("");
         }
      });

      // Filter text block
      final Composite composite_7 = new Composite(composite_2, SWT.NONE);
      composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
      final GridLayout gridLayout_13 = new GridLayout();
      gridLayout_13.numColumns = 3;
      composite_7.setLayout(gridLayout_13);

      final Label filterLabel = new Label(composite_7, SWT.NONE);
      filterLabel.setText("Filter Text:");

      filterText = new Text(composite_7, SWT.BORDER);
      filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      final Label clearFilterLabel = new Label(composite_7, SWT.PUSH);
      clearFilterLabel.setImage(SkynetGuiPlugin.getInstance().getImage("clear.gif"));
      clearFilterLabel.addMouseListener(new MouseListener() {
         public void mouseDown(MouseEvent e) {
         }

         public void mouseDoubleClick(MouseEvent e) {

         }

         public void mouseUp(MouseEvent e) {
            filterText.setText("");
         }
      });

      // Button block
      final Composite composite_1 = new Composite(composite_2, SWT.NONE);
      composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
      final GridLayout gridLayout_10 = new GridLayout();
      gridLayout_10.numColumns = 5;
      composite_1.setLayout(gridLayout_10);

      // Customization Buttons
      renameButton = new Button(composite_1, SWT.NONE);
      renameButton.setText("Rename");
      renameButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleRenameButton();
         }
      });

      saveButton = new Button(composite_1, SWT.NONE);
      saveButton.setText("Save");
      saveButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleSaveButton();
         }
      });

      // Customization Table and Buttons
      final Composite custComp = new Composite(comp, SWT.NONE);
      final GridData gd_composite_6 = new GridData(SWT.FILL, SWT.FILL, true, true);
      custComp.setLayoutData(gd_composite_6);
      final GridLayout gridLayout_1 = new GridLayout();
      gridLayout_1.marginWidth = 0;
      gridLayout_1.marginHeight = 0;
      custComp.setLayout(gridLayout_1);

      // Customization Table
      custTable = new TableViewer(custComp, SWT.BORDER);
      final Table table_2 = custTable.getTable();
      final GridData gd_table_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd_table_2.heightHint = 270;
      gd_table_2.widthHint = 200;
      table_2.setLayoutData(gd_table_2);
      custTable.setLabelProvider(new CustomizeDataLabelProvider(xViewer));
      custTable.setContentProvider(new ArrayTreeContentProvider());
      custTable.setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1.toString().startsWith("-"))
               return -1;
            else if (e2.toString().startsWith("-"))
               return 1;
            else
               return getComparator().compare(e1.toString(), e2.toString());
         }
      });
      custTable.addDoubleClickListener(new IDoubleClickListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
          */
         public void doubleClick(DoubleClickEvent event) {
            handleLoadSelCustButton();
            close();
         }
      });
      custTable.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            handleCustTableSelectionChanged();
            updateButtonEnablements();
         }
      });

      // Customization Table Buttons
      final Composite composite = new Composite(comp, SWT.NONE);
      composite.setLayoutData(new GridData());
      final GridLayout gridLayout_7 = new GridLayout();
      gridLayout_7.numColumns = 4;
      composite.setLayout(gridLayout_7);

      setDefaultButton = new Button(composite, SWT.NONE);
      setDefaultButton.setLayoutData(new GridData());
      setDefaultButton.setText(SET_AS_DEFAULT);
      setDefaultButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleSetDefaultButton();
            updateButtonEnablements();
         }
      });

      deleteButton = new Button(composite, SWT.NONE);
      deleteButton.setLayoutData(new GridData());
      deleteButton.setText("Delete");
      deleteButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            handleDeleteButton();
            updateButtonEnablements();
         }
      });

      loadCustomizeTable();
      updateButtonEnablements();

      return comp;
   }

   @SuppressWarnings("unchecked")
   private void handleAddItemButton() {
      // Remove from hidden
      List<XViewerColumn> hiddenSelCols = getHiddenTableSelection();
      if (hiddenSelCols == null) return;
      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getInput();
      hiddenCols.removeAll(hiddenSelCols);
      hiddenColTable.setInput(hiddenCols);

      // Add to visible
      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getInput();
      visibleCols.addAll(hiddenSelCols);
      visibleColTable.setInput(visibleCols);
   }

   @SuppressWarnings("unchecked")
   private void handleRemoveItemButton() {
      // Remove from visible
      List<XViewerColumn> visibleSelCols = getVisibleTableSelection();
      if (visibleSelCols == null) return;
      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getInput();
      visibleCols.removeAll(visibleSelCols);
      visibleColTable.setInput(visibleCols);

      // Add to hidden
      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getInput();
      hiddenCols.addAll(visibleSelCols);
      hiddenColTable.setInput(hiddenCols);
   }

   @SuppressWarnings("unchecked")
   private void handleAddAllItemButton() {

      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getInput();

      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getInput();

      visibleCols.addAll(hiddenCols);
      visibleColTable.setInput(visibleCols);

      hiddenCols.clear();
      hiddenColTable.setInput(hiddenCols);

   }

   @SuppressWarnings("unchecked")
   private void handleRemoveAllItemButton() {

      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getInput();

      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getInput();
      hiddenCols.addAll(visibleCols);
      hiddenColTable.setInput(hiddenCols);

      // Add to visible
      visibleCols.clear();
      visibleColTable.setInput(visibleCols);

   }

   @SuppressWarnings("unchecked")
   private void handleMoveUpButton() {
      List<XViewerColumn> selCols = getVisibleTableSelection();
      if (selCols == null) return;
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getInput();
      int index = orderCols.indexOf(selCols.iterator().next());
      if (index > 0) {
         orderCols.removeAll(selCols);
         orderCols.addAll(index - 1, selCols);
         visibleColTable.setInput(orderCols);
      } else
         return;
      ArrayList<XViewerColumn> selected = new ArrayList<XViewerColumn>();
      selected.addAll(selCols);
      visibleColTable.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      visibleColTable.getTable().setFocus();
      updateButtonEnablements();
   }

   @SuppressWarnings("unchecked")
   private void handleMoveDownButton() {
      List<XViewerColumn> selCols = getVisibleTableSelection();
      if (selCols == null) return;
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getInput();
      int index = orderCols.indexOf(selCols.iterator().next());
      if (index < (orderCols.size() - selCols.size())) {
         orderCols.removeAll(selCols);
         orderCols.addAll(index + 1, selCols);
         visibleColTable.setInput(orderCols);
      } else
         return;
      ArrayList<XViewerColumn> selected = new ArrayList<XViewerColumn>();
      selected.addAll(selCols);
      visibleColTable.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      visibleColTable.getTable().setFocus();
      updateButtonEnablements();
   }

   private void handleLoadSelCustButton() {
      CustomizeData custData = getCustTableSelection();
      if (custData != null) {
         xViewer.getCustomize().setCustomization(getCustTableSelection());
      }
   }

   /**
    * @return xColumns from hidden and visible customization lists
    */
   private List<XViewerColumn> getConfigCustXViewerColumns() {
      List<XViewerColumn> xCols = new ArrayList<XViewerColumn>();
      int x = 0;
      for (XViewerColumn xCol : getTableXViewerColumns(visibleColTable)) {
         xCol.setShow(true);
         xCol.setOrderNum(x);
         xCol.setColumnNum(x++);
         xCol.setTreeViewer(xViewer);
         xCols.add(xCol);
      }
      for (XViewerColumn xCol : getTableXViewerColumns(hiddenColTable)) {
         xCol.setShow(false);
         xCol.setOrderNum(x++);
         xCol.setTreeViewer(xViewer);
         xCols.add(xCol);
      }
      return xCols;
   }

   private void handleSaveButton() {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      for (CustomizeData custData : xViewer.getCustomize().getXViewerCustomizations().getCustDatas()) {
         if (custData.isPersonal())
            custDatas.add(custData);
         else if (OseeAts.isAtsAdmin()) custDatas.add(custData);
      }
      CustomizationDataSelectionDialog diag = new CustomizationDataSelectionDialog(xViewer, custDatas);
      if (diag.open() == 0) {
         String name = diag.getEnteredName();
         try {
            CustomizeData custXml = getConfigCustomizeCustData();
            custXml.setName(name);
            custXml.setPersonal(!diag.isSaveGlobal());
            xViewer.getCustomize().getXViewerCustomizations().saveCustomization(custXml);
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
      loadCustomizeTable();
   }

   private void handleRenameButton() {
      XViewerColumn xCol = getVisibleTableSelection().iterator().next();
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Rename Column", null, "Enter new name",
                  MessageDialog.QUESTION, new String[] {"OK", "Use Default", "Cancel"}, 0);
      int result = ed.open();
      if (result == 2) return;
      if (result == 0)
         xCol.setAlternateName(ed.getEntry());
      else if (result == 1) xCol.setAlternateName("");
      visibleColTable.update(xCol, null);
   }

   /**
    * @return CustomizeData represented by the configuration area
    */
   private CustomizeData getConfigCustomizeCustData() {
      CustomizeData custData = new CustomizeData(currentCustomizeData.getXml(), xViewer.getXViewerFactory());
      custData.resetGuid();
      custData.getColumnData().setColumns(getConfigCustXViewerColumns());
      custData.getSortingData().setFromXml(sorterText.getText());
      custData.getFilterData().setFilterText(filterText.getText());
      return custData;
   }

   private void handleLoadConfigCustButton() {
      xViewer.getCustomize().setCustomization(getConfigCustomizeCustData());
   }

   private void handleSetDefaultButton() {
      CustomizeData custData = getCustTableSelection();
      if (custData.getName().equals(XViewerCustomize.TABLE_DEFAULT_LABEL) || custData.getName().equals(
            XViewerCustomize.CURRENT_LABEL)) {
         AWorkbench.popup("ERROR", "Can't set table default or current as default");
         return;
      }
      if (xViewer.getCustomize().getXViewerCustomizations().isCustomizationUserDefault(custData)) {
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Remove Default",
               "Remove \"" + custData + "\" as default for this table?")) {
            xViewer.getCustomize().getXViewerCustomizations().setUserDefaultCustData(custData, false);
         }
      } else if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Set Default",
            "Set \"" + custData + "\" as default for this table?")) {
         xViewer.getCustomize().getXViewerCustomizations().setUserDefaultCustData(custData, true);
      }
      loadCustomizeTable();
   }

   private void handleDeleteButton() {
      try {
         CustomizeData custSel = getCustTableSelection();
         if (custSel == null) return;
         if (!custSel.isPersonal() && !OseeAts.isAtsAdmin()) {
            AWorkbench.popup("ERROR", "Global Customizations can only be deleted by admin");
            return;
         }
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete Customization",
               "Delete \"" + custSel + "\" customization?")) {
            xViewer.getCustomize().getXViewerCustomizations().deleteCustomization(custSel);
            loadCustomizeTable();
            updateButtonEnablements();
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   private void updateButtonEnablements() {
      CustomizeData custData = getCustTableSelection();
      setDefaultButton.setEnabled(xViewer.getXViewerFactory().getXViewerCustomizeDefaults().isSaveDefaultsEnabled() && custTable.getTable().isFocusControl() && custData != null && !custData.getName().equals(
            XViewerCustomize.TABLE_DEFAULT_LABEL) && !custData.getName().equals(XViewerCustomize.CURRENT_LABEL));
      if (custTable.getTable().isFocusControl() && custData != null) {
         setDefaultButton.setText(xViewer.getCustomize().getXViewerCustomizations().isCustomizationUserDefault(custData) ? REMOVE_DEFAULT : SET_AS_DEFAULT);
         setDefaultButton.getParent().layout();
      }
      deleteButton.setEnabled(xViewer.getXViewerFactory().getXViewerCustomizeDefaults().isSaveDefaultsEnabled() && custTable.getTable().isFocusControl() && custData != null);
      addItemButton.setEnabled(hiddenColTable.getTable().isFocusControl() && getHiddenTableSelection() != null);
      removeItemButton.setEnabled(visibleColTable.getTable().isFocusControl() && getVisibleTableSelection() != null);
      renameButton.setEnabled(visibleColTable.getTable().isFocusControl() && getVisibleTableSelection() != null && getVisibleTableSelection().size() == 1);
      moveDownButton.setEnabled(visibleColTable.getTable().isFocusControl() && getVisibleTableSelection() != null);
      moveUpButton.setEnabled(visibleColTable.getTable().isFocusControl() && getVisibleTableSelection() != null);
      saveButton.setEnabled(xViewer.getXViewerFactory().getXViewerCustomizations(xViewer).isCustomizationPersistAvailable());
   }

   private void loadCustomizeTable() {
      // Add stored customization data
      List<CustomizeData> custDatas = xViewer.getCustomize().getXViewerCustomizations().getCustDatas();

      // Add table default customization data
      defaultTableCustData = xViewer.getCustomize().getTableDefaultCustData();
      defaultTableCustData.setName(XViewerCustomize.TABLE_DEFAULT_LABEL);
      custDatas.add(defaultTableCustData);

      // Add current customization data
      CustomizeData currentCustData = xViewer.getCustomize().getCurrentCustData();
      currentCustData.setName(XViewerCustomize.CURRENT_LABEL);
      custDatas.add(currentCustData);

      custTable.setInput(custDatas);

      ArrayList<Object> sel = new ArrayList<Object>();
      sel.add(currentCustData);
      custTable.setSelection(new StructuredSelection(sel.toArray(new Object[sel.size()])));
      custTable.getTable().setFocus();

      updateButtonEnablements();
   }

   private CustomizeData getCustTableSelection() {
      IStructuredSelection selection = (IStructuredSelection) custTable.getSelection();
      if (selection.size() == 0) return null;
      Iterator<?> i = selection.iterator();
      CustomizeData custData = (CustomizeData) i.next();
      // Add columns that were added after this customization was saved
      custData.getColumnData().addMissingColumns(defaultTableCustData.getColumnData());
      return custData;
   }

   private List<XViewerColumn> getVisibleTableSelection() {
      return getTableSelection(visibleColTable);
   }

   private List<XViewerColumn> getHiddenTableSelection() {
      return getTableSelection(hiddenColTable);
   }

   private List<XViewerColumn> getTableSelection(TableViewer xColTableViewer) {
      List<XViewerColumn> xCols = new ArrayList<XViewerColumn>();
      IStructuredSelection selection = (IStructuredSelection) xColTableViewer.getSelection();
      if (selection.size() == 0) return null;
      Iterator<?> i = selection.iterator();
      while (i.hasNext())
         xCols.add((XViewerColumn) i.next());
      return xCols;
   }

   @SuppressWarnings("unchecked")
   private List<XViewerColumn> getTableXViewerColumns(TableViewer xColTableViewer) {
      return (List<XViewerColumn>) xColTableViewer.getInput();
   }

   private void handleCustTableSelectionChanged() {
      if (getCustTableSelection() == null) return;
      CustomizeData custData = getCustTableSelection();
      if (custData == null) {
         OSEELog.logException(SkynetGuiPlugin.class, new IllegalStateException("Can't obtain selection Xml"), true);
         return;
      }
      List<XViewerColumn> hideXCols = new ArrayList<XViewerColumn>();
      List<XViewerColumn> showXCols = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : custData.getColumnData().getColumns()) {
         if (xCol.isShow())
            showXCols.add(xCol);
         else
            hideXCols.add(xCol);
      }

      hiddenColTable.setInput(hideXCols);
      visibleColTable.setInput(showXCols);

      sorterText.setText(custData.getSortingData().getXml());
      sorterText.setData(custData);

      filterText.setText(custData.getFilterData().getFilterText());
      filterText.setData(custData);
   }

   protected void buttonPressed(int buttonId) {
      // Ok
      if (buttonId == 0) {
         handleLoadConfigCustButton();
         close();
      }
      // Apply
      else if (buttonId == 1) {
         handleLoadConfigCustButton();
      }
      // Cancel
      else
         close();
   }

   public String getTitle() {
      return title;
   }

}
