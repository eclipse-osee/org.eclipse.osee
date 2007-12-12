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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
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
public class XList extends XWidget {

   public class XListItem {
      private String name = "";
      private String xmlValue = null;
      private Object data = null;
      private boolean selected = false;

      public XListItem(String name) {
         this.name = name;
      }

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
   private Map<String, XListItem> items = new HashMap<String, XListItem>();
   private int requiredMinSelected = 0;
   private int requiredMaxSelected = 0;
   private boolean grabHorizontal = false;

   protected SelectionListener listListener = new SelectionListener() {

      public void widgetSelected(SelectionEvent e) {
         handleSelection();
      }

      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }
   };

   public XList(String displayLabel) {
      this(displayLabel, "list", "");
   }

   public XList() {
      this("List", "list", "");
   }

   public XList(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
      setReportType(XWidget.RPT_SINGLE_LINE);
      listMenu = null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
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
   public void createWidgets(Composite parent, int horizontalSpan) {

      this.parent = parent;
      composite = null;

      if (!verticalLabel && (horizontalSpan < 2)) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (displayLabel && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         int numColumns = 1;
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = numColumns;
         composite.setLayout(gridLayout);
         GridData gd =
               new GridData(
                     GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (displayLabel) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      listList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      String array[] = (String[]) items.keySet().toArray(new String[items.size()]);
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
      if (composite != parent) composite.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public void setFocus() {
   }

   /**
    * Don't need this since overriding toReport and toXml
    */
   public String getXmlData() {
      return "";
   }

   /**
    * Don't need this since overriding setFromXml
    */
   public void setXmlData(String str) {
      return;
   }

   private void handleSelection() {
      String sels[] = listList.getSelection();
      for (XListItem xItem : items.values())
         xItem.setSelected(false);
      for (String sel : sels) {
         XListItem xItem = items.get(sel);
         if (xItem != null) xItem.setSelected(true);
      }
      setLabelError();

      notifyXModifiedListeners();
   }

   public void refresh() {
      updateListWidget();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      listList.addSelectionListener(selectionListener);
   }

   public Set<XListItem> getSelected() {
      Set<XListItem> sel = new HashSet<XListItem>();
      for (XListItem xItem : items.values()) {
         if (xItem.isSelected()) sel.add(xItem);
      }
      return sel;
   }

   public void setFromXml(String xml) {
      Matcher inner, outter;
      String outterXml;
      items.clear();
      outter =
            Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      while (outter.find()) {
         outterXml = outter.group(1);
         inner = Pattern.compile("<" + xmlSubRoot + ">(.*?)</" + xmlSubRoot + ">").matcher(outterXml);
         while (inner.find()) {
            String str = inner.group(1);
            XListItem xItem = getByXmlName(str);
            if (xItem != null)
               xItem.setSelected(true);
            else {
               xItem = add(str);
               xItem.setSelected(true);
            }
         }
      }
      refresh();
   }

   public List getList() {
      return listList;
   }

   public XListItem get(String name) {
      return items.get(name);
   }

   public String getSelectedStr() {
      StringBuffer sb = new StringBuffer();
      for (XListItem item : getSelected())
         sb.append(item.getName() + ", ");
      return sb.toString().replaceFirst(", $", "");
   }

   public XListItem getByXmlName(String xmlName) {
      for (XListItem xItem : items.values()) {
         if (xItem.xmlValue.equals(xmlName)) return xItem;
      }
      return null;
   }

   protected void updateListWidget() {
      String listItems[] = listList.getItems();
      Set<String> listItemsSet = new HashSet<String>();
      Set<String> selected = new HashSet<String>();
      for (String listItem : listItems)
         listItemsSet.add(listItem);
      if (listList != null) {
         listList.deselectAll();
         for (XListItem xItem : items.values()) {
            if (!listItemsSet.contains(xItem.getName())) {
               listList.add(xItem.getName());
               if (xItem.isSelected()) selected.add(xItem.getName());
            } else if (xItem.isSelected()) selected.add(xItem.getName());
         }
         listList.setSelection(selected.toArray(new String[selected.size()]));
         setLabelError();
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

   public void add(Collection<String> names) {
      for (String name : names) {
         add(name);
      }
   }

   public void addSelected(String name) {
      XListItem xItem = get(name);
      if (xItem != null)
         xItem.setSelected(true);
      else {
         xItem = add(name);
         xItem.setSelected(true);
      }
      updateListWidget();
   }

   public void setSelected(String name) {
      setSelected(Arrays.asList(new String[] {name}));
      updateListWidget();
   }

   public void setSelected(Collection<String> names) {
      ArrayList<String> handledNames = new ArrayList<String>();
      for (Entry<String, XListItem> entry : items.entrySet()) {
         if (names.contains(entry.getKey())) {
            entry.getValue().selected = true;
            handledNames.add(entry.getKey());
         } else
            entry.getValue().selected = false;
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

   public boolean isValid() {
      if (!requiredEntry) return true;
      int size = getSelected().size();
      if (requiredMaxSelected != 0) return ((size >= requiredMinSelected) && (size <= requiredMaxSelected));
      return size > 0;
   }

   /**
    * Minimum number of selected items that makes this widget valid
    * 
    * @param minSelected -
    * @param maxSelected -
    */
   public void setRequiredSelected(int minSelected, int maxSelected) {
      this.requiredMinSelected = minSelected;
      this.requiredMaxSelected = maxSelected;
      setRequiredEntry(true);
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      if (!requiredEntry) {
         this.requiredMinSelected = 1;
         this.requiredMaxSelected = 1;
      }
   }

   public String getReportData() {
      String s = "\n";
      for (XListItem xItem : getSelected()) {
         s = s + "       - " + xItem + "\n";
      }
      s = s.replaceAll("\n+$", "");
      return s;
   }

   public String toXml() {
      return toXml(xmlRoot, xmlSubRoot);
   }

   public String toXml(String xmlRoot, String xmlSubRoot) {
      String s = "<" + xmlRoot + ">\n";
      for (XListItem xItem : getSelected()) {
         String dataStr;
         if (xItem.getXmlValue() != null) {
            dataStr = xItem.getXmlValue();
         } else {
            dataStr = xItem.getName();
         }
         s = s + "      <" + xmlSubRoot + ">" + dataStr + "</" + xmlSubRoot + ">\n";
      }
      s = s + "</" + xmlRoot + ">\n";
      return s;
   }

   public String toHTML(String labelFont) {
      String s = "<dl><dt>" + AHTML.getLabelStr(labelFont, label + ": ") + "<dt><ul type=\"disc\">";
      for (XListItem xItem : getSelected()) {
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

   protected void clearAll() {
      if (listList != null) listList.removeAll();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return getSelected();
   }
}