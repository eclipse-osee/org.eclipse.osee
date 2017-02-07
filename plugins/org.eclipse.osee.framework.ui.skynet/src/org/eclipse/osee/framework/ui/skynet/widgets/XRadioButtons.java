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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton.ButtonType;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Generic label and radiobutton field object for use by single entry artifact attributes
 *
 * @author Donald G. Dunne
 */
public class XRadioButtons extends GenericXWidget {

   private Composite comp;
   protected final ArrayList<XRadioButton> xButtons = new ArrayList<>();
   private boolean multiSelect;
   private boolean vertical;
   private int verticalColumns;
   private boolean sortNames;
   private Integer marginWidgth = null;

   public XRadioButtons(String displayLabel, String xmlRoot) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return comp;
   }

   public void addButtons(String items[]) {
      xButtons.clear();
      for (String item : items) {
         xButtons.add(new XRadioButton(item));
      }
   }

   public void addButton(String item, String toolTip) {
      XRadioButton rb = new XRadioButton(item);
      rb.setToolTip(toolTip);
      xButtons.add(rb);
   }

   public XRadioButton addButton(String item) {
      XRadioButton rb = new XRadioButton(item);
      xButtons.add(rb);
      return rb;
   }

   public void selectAll(boolean selected) {
      for (XRadioButton rb : xButtons) {
         rb.setSelected(selected);
      }
      refresh();
   }

   public XRadioButton getButton(String name) {
      for (XRadioButton button : xButtons) {
         if (button.getLabel().equals(name)) {
            return button;
         }
      }
      return null;
   }

   public void setSelected(String selected[]) {
      // First, clear out all previous selections
      selectAll(false);
      // Set, selected items sent in
      for (String name : selected) {
         XRadioButton rb = getButton(name);
         if (rb != null) {
            rb.setSelected(true);
         }
      }
      refresh();
   }

   public void setSelected(Collection<String> selected) {
      if (selected != null) {
         setSelected(selected.toArray(new String[selected.size()]));
      }
   }

   @Override
   public String toString() {
      return getLabel() + ": " + Collections.toString(",", getSelectedNames());
   }

   /**
    * Create radio Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Map<String, XRadioButton> nameToButton = new HashMap<>();
      String names[] = new String[xButtons.size()];
      int x = 0;
      for (XRadioButton rb : xButtons) {
         nameToButton.put(rb.getLabel().toLowerCase(), rb);
         names[x++] = rb.getLabel().toLowerCase();
      }

      int numColumns = names.length * 2 + (isDisplayLabel() ? 1 : 0);
      if (vertical && horizontalSpan == 1) {
         numColumns = 1;
      } else if (vertical) {
         numColumns = (isDisplayLabel() ? 1 : 0) + 1; // only need label an composite
      }
      // System.out.println("numColumns *" + numColumns + "*");
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(numColumns, false);
      if (marginWidgth != null) {
         layout.marginWidth = marginWidgth;
      }
      comp.setLayout(layout);
      GridData gd = new GridData();
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);
      // comp.setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));

      // Create Text Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }
      if (getToolTip() != null && isDisplayLabel()) {
         labelWidget.setToolTipText(getToolTip());
      }

      if (sortNames) {
         Arrays.sort(names);
      }

      int numRows = 1;
      if (vertical && xButtons.size() > verticalColumns) {
         numRows = xButtons.size() / verticalColumns;
         if (xButtons.size() / verticalColumns > 0) {
            numRows++;
            // System.out.println("numRows *" + numRows + "*");
         }
      }

      Composite c = comp;
      if (vertical) {
         // System.out.println("verticalColumns *" + verticalColumns + "*");
         c = new Composite(comp, SWT.NONE);
         c.setLayout(ALayout.getZeroMarginLayout(verticalColumns, false));
         // c.setBackground(Displays.getSystemColor(SWT.COLOR_RED));
      }

      Composite inComp = null;
      if (!vertical) {
         inComp = c;
      }
      for (int i = 0; i < names.length; i++) {
         if (vertical && i % numRows == 0) {
            inComp = new Composite(c, SWT.NONE);
            inComp.setLayout(new GridLayout(2, false));
            inComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
            // inComp.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN+i));
         }
         XRadioButton button = nameToButton.get(names[i]);
         if (multiSelect) {
            button.setButtonType(ButtonType.Check);
         }
         button.setLabelAfter(true);
         button.createWidgets(inComp, 2);
         // Since each button has it's own listeners, pass the notification on to anyone listening
         // to XRadioButtons:w
         button.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               notifyXModifiedListeners();
            }
         });
      }
      refresh();
   }

   @Override
   public void dispose() {
      for (XRadioButton rb : xButtons) {
         rb.dispose();
      }
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (comp != null && !comp.isDisposed()) {
         comp.dispose();
      }
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      validate();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      for (XRadioButton rb : xButtons) {
         rb.addSelectionListener(selectionListener);
      }
   }

   public void removeSelectionListener(SelectionListener selectionListener) {
      for (XRadioButton rb : xButtons) {
         rb.removeSelectionListener(selectionListener);
      }
   }

   public Set<String> getSelectedNames() {
      Set<String> names = new HashSet<>();
      for (XRadioButton rb : xButtons) {
         if (rb.isSelected()) {
            names.add(rb.getLabel());
         }
      }
      return names;
   }

   public boolean isSelected(String name) {
      XRadioButton rb = getButton(name);
      if (rb != null) {
         return rb.isSelected();
      }
      return false;
   }

   public boolean isSelected() {
      for (XRadioButton rb : xButtons) {
         if (rb.isSelected()) {
            return true;
         }
      }
      return false;
   }

   public void setSelected(String name) {
      setSelected(new String[] {name});
   }

   @Override
   public void refresh() {
      validate();
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must have at least one selection.");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return getSelectedNames().isEmpty();
   }

   @Override
   public String getReportData() {
      return "";
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ");
   }

   public boolean isMultiSelect() {
      return multiSelect;
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   public boolean isVertical() {
      return vertical;
   }

   public void setVertical(boolean vertical, int columns) {
      this.vertical = vertical;
      this.verticalColumns = columns;
   }

   public boolean isSortNames() {
      return sortNames;
   }

   public void setSortNames(boolean sortNames) {
      this.sortNames = sortNames;
   }

   @Override
   public Object getData() {
      return getSelectedNames();
   }

   public Integer getMarginWidgth() {
      return marginWidgth;
   }

   public void setMarginWidgth(Integer marginWidgth) {
      this.marginWidgth = marginWidgth;
   }

   @Override
   public Collection<? extends XWidget> getChildrenXWidgets() {
      return xButtons;
   }
}