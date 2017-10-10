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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class XListViewer extends GenericXWidget {

   // XListViewer uses a table so images can be used.  SWT doesn't support images in ListViewer
   protected TableViewer listViewer;
   private Menu listMenu;
   private Composite parent;
   private Composite composite;
   private int requiredMinSelected = 0;
   private int requiredMaxSelected = 0;
   private boolean grabHorizontal = false;
   private boolean multiSelect = false;
   private Object defaultSelectedObject;

   protected SelectionListener listListener = new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
         handleSelection();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }
   };
   private Collection<Object> input;
   private IContentProvider contentProvider;
   private ILabelProvider labelProvider;
   private ViewerSorter sorter;
   private int widthHint;
   private int heightHint;

   private StructuredSelection preselected;

   public XListViewer(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return listViewer.getControl();
   }

   /**
    * @param listMenu The listMenu to set.
    */
   public void setListMenu(Menu listMenu) {
      this.listMenu = listMenu;
   }

   public void setContentProvider(IContentProvider contentProvider) {
      this.contentProvider = contentProvider;
   }

   @Override
   public void setLabelProvider(ILabelProvider labelProvider) {
      this.labelProvider = labelProvider;
      if (listViewer != null) {
         listViewer.setLabelProvider(labelProvider);
      }
   }

   public void setSorter(ViewerSorter sorter) {
      this.sorter = sorter;
   }

   public void setInputArtifacts(Collection<? extends Artifact> arts) {
      setInput(arts);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void setInput(Object input) {
      if (input instanceof Collection) {
         setInput((Collection) input);
      } else {
         setInput(Arrays.asList(input));
      }
   }

   public void setInput(Collection<Object> input) {
      this.input = input;
      if (listViewer != null) {
         listViewer.setInput(input);
      }
   }

   public Object getInput() {
      return listViewer.getInput();
   }

   public Collection<Object> getCollectionInput() {
      return input;
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      listViewer.addSelectionChangedListener(listener);
   }

   public void setHints(int widthHint, int heightHint) {
      this.widthHint = widthHint;
      this.heightHint = heightHint;
   }

   /**
    * Create List Widgets. Widgets Created: List: horizonatalSpan takes up 2 columns; horizontalSpan must be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      this.parent = parent;
      composite = null;

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (isDisplayLabel() && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         // composite.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN));
         composite.setLayout(new GridLayout(1, false));
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.horizontalSpan = 2;
         gd.grabExcessHorizontalSpace = true;
         gd.grabExcessVerticalSpace = true;
         composite.setLayoutData(gd);
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      createControlsAfterLabel(parent, horizontalSpan);

      listViewer =
         new TableViewer(composite, (multiSelect ? SWT.MULTI : SWT.SINGLE) | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      listViewer.setContentProvider(contentProvider);
      listViewer.setLabelProvider(labelProvider);
      if (sorter != null) {
         listViewer.setSorter(sorter);
      }
      listViewer.setInput(input);
      listViewer.getTable().setMenu(listMenu);
      listViewer.getTable().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSelection();
         }
      });

      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.verticalSpan = 10;
      if (grabHorizontal) {
         gd.grabExcessHorizontalSpace = true;
      }
      if (widthHint > 0) {
         gd.widthHint = widthHint;
      }
      if (heightHint > 0) {
         gd.heightHint = heightHint;
      }
      gd.grabExcessVerticalSpace = true;
      listViewer.getTable().setLayoutData(gd);
      listViewer.getTable().addSelectionListener(listListener);

      if (defaultSelectedObject != null) {
         setSelected(defaultSelectedObject);
      }
      if (preselected != null) {
         setSelected(preselected);
      }

      updateListWidget();
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      listViewer.getTable().dispose();
      if (composite != null && !composite.isDisposed()) {
         composite.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   private void handleSelection() {
      validate();
      notifyXModifiedListeners();
   }

   @Override
   public void refresh() {
      updateListWidget();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      listViewer.getTable().addSelectionListener(selectionListener);
   }

   public ArrayList<Object> getSelected() {
      ArrayList<Object> selected = new ArrayList<>();
      IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
      Iterator<?> i = selection.iterator();
      while (i.hasNext()) {
         selected.add(i.next());
      }
      return selected;
   }

   public Table getTable() {
      return listViewer.getTable();
   }

   public TableViewer getTableViewer() {
      return listViewer;
   }

   protected void updateListWidget() {
      if (listViewer == null || listViewer.getTable().isDisposed()) {
         return;
      }
      listViewer.refresh();
      validate();
   }

   public void add(Object object) {
      if (!input.contains(object)) {
         input.add(object);
      }
   }

   public void add(Object[] objects) {
      for (Object object : objects) {
         add(object);
      }
   }

   public void add(Collection<Object> objects) {
      input.removeAll(objects);
      input.addAll(objects);
   }

   public void setSelected(ArrayList<Object> selected) {
      preselected = new StructuredSelection(selected.toArray(new Object[selected.size()]));
      setSelected(preselected);
   }

   public void setSelected(Object selected) {
      preselected = new StructuredSelection(selected);
      setSelected(preselected);
   }

   private void setSelected(StructuredSelection selection) {
      if (listViewer != null) {
         listViewer.setSelection(selection);
         updateListWidget();
      }
   }

   @Override
   public IStatus isValid() {
      if (!isRequiredEntry()) {
         return Status.OK_STATUS;
      }
      int size = getSelected().size();
      if (requiredMaxSelected != 0) {
         if (size >= requiredMinSelected && size <= requiredMaxSelected) {
            return Status.OK_STATUS;
         } else if (size < requiredMinSelected) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
               getLabel() + " must have at least " + requiredMinSelected + " selected.");
         } else if (size < requiredMaxSelected) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
               getLabel() + " should only have " + requiredMaxSelected + " selected.");
         } else {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel());
         }
      }
      if (size == 0) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return Status.OK_STATUS;

   }

   @Override
   public boolean isEmpty() {
      return getSelected().isEmpty();
   }

   /**
    * Minimum number of selected items that makes this widget valid
    */
   public void setRequiredSelected(int minSelected, int maxSelected) {
      requiredMinSelected = minSelected;
      requiredMaxSelected = maxSelected;
      setRequiredEntry(true);
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      if (!requiredEntry) {
         requiredMinSelected = 1;
         requiredMaxSelected = 1;
      }
   }

   @Override
   public String getReportData() {
      String s = "\n";
      for (Object obj : getSelected()) {
         s = s + "       - " + obj + "\n";
      }
      s = s.replaceAll("\n+$", "");
      return s;
   }

   @Override
   public String toHTML(String labelFont) {
      String s = "<dl><dt>" + AHTML.getLabelStr(labelFont, getLabel() + ": ") + "<dt><ul type=\"disc\">";
      for (Object xItem : getSelected()) {
         s += "<li>" + xItem;
      }

      return s + "</ul></dl>";
   }

   /**
    * @param grabHorizontal The grabHorizontal to set.
    */
   public void setGrabHorizontal(boolean grabHorizontal) {
      this.grabHorizontal = grabHorizontal;
   }

   public boolean isMultiSelect() {
      return multiSelect;
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   @Override
   public Object getData() {
      return getSelected();
   }

   public void setDefaultSelected(Object defaultSelectedObject) {
      this.defaultSelectedObject = defaultSelectedObject;
   }
}