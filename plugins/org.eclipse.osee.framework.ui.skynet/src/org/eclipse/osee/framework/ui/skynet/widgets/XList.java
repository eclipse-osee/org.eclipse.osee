/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public class XList extends GenericXWidget {

   public class XListItem {
      private String name = "";
      private String xmlValue = null;
      private Object data = null;
      private boolean selected = false;

      public XListItem(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return name;
      }

      public Object getData() {
         return data;
      }

      public void setData(Object data) {
         this.data = data;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public boolean isSelected() {
         return selected;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }

      public String getXmlValue() {
         return xmlValue;
      }

      public void setXmlValue(String xmlValue) {
         this.xmlValue = xmlValue;
      }
   }

   private List listList;
   private Menu listMenu;
   private Composite parent;
   private Composite composite;
   private final Map<String, XListItem> items = new TreeMap<>();
   private int requiredMinSelected = 0;
   private int requiredMaxSelected = 0;
   private boolean grabHorizontal = false;

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

   public XList(String displayLabel) {
      super(displayLabel);
      listMenu = null;
   }

   @Override
   public Control getControl() {
      return listList;
   }

   /**
    * @param listMenu The listMenu to set.
    */
   public void setListMenu(Menu listMenu) {
      this.listMenu = listMenu;
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
         int numColumns = 1;
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = numColumns;
         composite.setLayout(gridLayout);
         GridData gd = new GridData(
            GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
         gd.horizontalSpan = horizontalSpan;
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

      listList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      String array[] = items.keySet().toArray(new String[items.size()]);
      java.util.Arrays.sort(array);
      listList.setMenu(listMenu);
      listList.setItems(array);
      GridData gridData5 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
      gridData5.verticalSpan = 10;
      if (grabHorizontal) {
         gridData5.grabExcessHorizontalSpace = true;
      }
      int listHeight = listList.getItemHeight() * 6;
      Rectangle trim = listList.computeTrim(0, 0, 0, listHeight);
      gridData5.heightHint = trim.height;
      gridData5.grabExcessVerticalSpace = true;
      listList.setLayoutData(gridData5);
      listList.addSelectionListener(listListener);
      updateListWidget();
      listList.setEnabled(isEditable());

   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      listList.dispose();
      if (composite != parent) {
         composite.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   private void handleSelection() {
      String sels[] = listList.getSelection();
      for (XListItem xItem : items.values()) {
         xItem.setSelected(false);
      }
      for (String sel : sels) {
         XListItem xItem = items.get(sel);
         if (xItem != null) {
            xItem.setSelected(true);
         }
      }
      validate();

      notifyXModifiedListeners();
   }

   @Override
   public void refresh() {
      updateListWidget();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      listList.addSelectionListener(selectionListener);
   }

   public Set<XListItem> getSelected() {
      Set<XListItem> sel = new HashSet<>();
      for (XListItem xItem : items.values()) {
         if (xItem.isSelected()) {
            sel.add(xItem);
         }
      }
      return sel;
   }

   public List getList() {
      return listList;
   }

   public XListItem get(String name) {
      return items.get(name);
   }

   public String getSelectedStr() {
      StringBuffer sb = new StringBuffer();
      for (XListItem item : getSelected()) {
         sb.append(item.getName());
         sb.append(", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }

   public Collection<String> getSelectedStrs() {
      Set<String> selected = new HashSet<>();
      for (XListItem item : getSelected()) {
         selected.add(item.getName());
      }
      return selected;
   }

   public XListItem getByXmlName(String xmlName) {
      for (XListItem xItem : items.values()) {
         if (xItem.xmlValue.equals(xmlName)) {
            return xItem;
         }
      }
      return null;
   }

   protected void updateListWidget() {
      if (listList == null || listList.isDisposed()) {
         return;
      }
      String listItems[] = listList.getItems();
      Set<String> listItemsSet = new HashSet<>();
      Set<String> selected = new HashSet<>();
      for (String listItem : listItems) {
         listItemsSet.add(listItem);
      }
      if (listList != null) {
         listList.deselectAll();
         for (XListItem xItem : items.values()) {
            if (!listItemsSet.contains(xItem.getName())) {
               listList.add(xItem.getName());
               if (xItem.isSelected()) {
                  selected.add(xItem.getName());
               }
            } else if (xItem.isSelected()) {
               selected.add(xItem.getName());
            }
         }
         listList.setSelection(selected.toArray(new String[selected.size()]));
         validate();
      }
   }

   public XListItem add(String name) {
      XListItem xItem = new XListItem(name);
      items.put(name, xItem);
      return xItem;
   }

   public void add(String[] names) {
      for (String name : names) {
         add(name);
      }
   }

   public void removeAll() {
      items.clear();
      listList.removeAll();
   }

   public void add(Collection<String> names) {
      add(names.toArray(new String[0]));
   }

   public void addSelected(String name) {
      XListItem xItem = get(name);
      if (xItem != null) {
         xItem.setSelected(true);
      } else {
         xItem = add(name);
         xItem.setSelected(true);
      }
      updateListWidget();
   }

   public void setSelected(String name) {
      setSelected(Arrays.asList(name));
      updateListWidget();
   }

   public void setSelected(Collection<String> names) {
      ArrayList<String> handledNames = new ArrayList<>();
      for (Entry<String, XListItem> entry : items.entrySet()) {
         if (names.contains(entry.getKey())) {
            entry.getValue().selected = true;
            handledNames.add(entry.getKey());
         } else {
            entry.getValue().selected = false;
         }
      }
      for (String name : names) {
         if (!handledNames.contains(name)) {
            XListItem item = new XListItem(name);
            item.selected = true;
            items.put(name, item);
         }
      }
      updateListWidget();
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
      StringBuilder builder = new StringBuilder();
      builder.append("\n");
      for (XListItem xItem : getSelected()) {
         builder.append(String.format("       - %s\n", xItem));
      }
      return builder.toString().trim();
   }

   @Override
   public String toHTML(String labelFont) {
      StringBuilder builder = new StringBuilder();
      builder.append("<dl><dt>" + AHTML.getLabelStr(labelFont, getLabel() + ": ") + "<dt><ul type=\"disc\">");
      for (XListItem xItem : getSelected()) {
         builder.append("<li>" + xItem);
      }
      builder.append("</ul></dl>");
      return builder.toString();
   }

   /**
    * @param grabHorizontal The grabHorizontal to set.
    */
   public void setGrabHorizontal(boolean grabHorizontal) {
      this.grabHorizontal = grabHorizontal;
   }

   protected void clearAll() {
      if (listList != null) {
         listList.removeAll();
      }
   }

   @Override
   public Object getData() {
      return getSelected();
   }
}