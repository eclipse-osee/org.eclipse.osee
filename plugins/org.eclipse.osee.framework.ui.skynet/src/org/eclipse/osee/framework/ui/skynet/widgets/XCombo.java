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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XCombo extends XButtonCommon {

   public CCombo dataCombo;
   private Composite parent, composite;
   protected String data = "";
   protected String[] inDataStrings; // Strings sent in for display
   private final Map<String, Integer> displayDataStrings = new LinkedHashMap<>();
   private String displayArray[];
   private boolean isDefaultSelectionAllowed;

   public XCombo(String displayLabel) {
      super(displayLabel);
      isDefaultSelectionAllowed = true;
   }

   public void setDefaultSelectionAllowed(boolean isAllowed) {
      isDefaultSelectionAllowed = isAllowed;
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      validate();
   }

   public boolean isDefaultSelectionAllowed() {
      return isDefaultSelectionAllowed;
   }

   @Override
   public Control getControl() {
      return dataCombo;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (getControl() != null && !getControl().isDisposed()) {
         getControl().setEnabled(editable);
      }
   }

   public void setEnabled(boolean enabled) {
      dataCombo.setEnabled(enabled);
   }

   public void createWidgets(Composite composite, int horizontalSpan, String inDataStrings[]) {
      this.inDataStrings = inDataStrings;
      createWidgets(composite, horizontalSpan);
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      if (inDataStrings == null) {
         inDataStrings = new String[] {"DATA NOT FOUND"};
      }
      setDisplayDataStrings();

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      this.parent = parent;
      if (fillVertically) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(1, false);
         composite.setLayout(layout);
         composite.setLayoutData(new GridData());
      } else {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(horizontalSpan, false);
         composite.setLayout(layout);
         GridData gd = new GridData();
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      }

      // Create Data Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      GridData gd;
      dataCombo = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL | SWT.FLAT | SWT.BORDER);
      dataCombo.setItems(displayArray);
      dataCombo.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      dataCombo.setVisibleItemCount(Math.min(displayArray.length, 45));
      dataCombo.addKeyListener(new KeyAdapter() {
         private String keySequence;
         private long timeout = 0;

         @Override
         public void keyPressed(KeyEvent keyEvent) {
            if (!Character.isLetterOrDigit(keyEvent.character)) {
               return;
            }
            long time = System.currentTimeMillis();
            if (time > timeout) {
               keySequence = "";
            }
            keySequence += Character.toLowerCase(keyEvent.character);
            timeout = time + 1000;
            int index = 0;
            for (String item : displayArray) {
               if (item.toLowerCase().startsWith(keySequence)) {
                  // Prevent the ordinary search
                  keyEvent.doit = false;
                  dataCombo.select(index);
                  return;
               }
               index++;
            }
            keySequence = "";
         }
      });
      gd = new GridData();
      if (fillHorizontally) {
         gd.grabExcessHorizontalSpace = true;
      }
      if (fillVertically) {
         gd.grabExcessVerticalSpace = true;
      }
      gd.horizontalSpan = horizontalSpan - 1;
      dataCombo.setLayoutData(gd);
      ModifyListener dataComboListener = new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            data = dataCombo.getText();
            if (data.compareTo("--select--") == 0) {
               data = "";
            }
            validate();
            notifyXModifiedListeners();
         }
      };
      dataCombo.addModifyListener(dataComboListener);

      refresh();
      dataCombo.setEnabled(isEditable());
   }

   public int getDisplayPosition(String str) {
      for (int i = 0; i < displayArray.length; i++) {
         if (str.equals(displayArray[i])) {
            return i;
         }
      }
      return 0;
   }

   public int getDisplayPosition() {
      for (int i = 0; i < displayArray.length; i++) {
         if (data.equals(displayArray[i])) {
            return i;
         }
      }
      return 0;
   }

   public void setDataStrings(Collection<String> inDataStrings) {
      setDataStrings(inDataStrings.toArray(new String[inDataStrings.size()]));
   }

   public void setDataStrings(String[] inDataStrings) {
      this.inDataStrings = inDataStrings;
      setDisplayDataStrings();
      if (dataCombo != null && !dataCombo.isDisposed()) {
         dataCombo.setItems(displayArray);
         if (displayArray.length < 15) {
            dataCombo.setVisibleItemCount(displayArray.length);
         }
      }
      updateComboWidget();
   }

   /**
    * Given the inDataStrings, create the mapping of all data strings including "--select--" and map them to their index
    * in the combo list.
    */
   private void setDisplayDataStrings() {
      displayDataStrings.clear();

      if (isDefaultSelectionAllowed()) {
         displayDataStrings.put("--select--", 0);
         displayArray = new String[inDataStrings.length + 1];
         displayArray[0] = "--select--";
         for (int i = 0; i < inDataStrings.length; i++) {
            displayDataStrings.put(inDataStrings[i], i + 1);
            displayArray[i + 1] = inDataStrings[i];
         }
      } else {
         displayArray = new String[inDataStrings.length];
         for (int i = 0; i < inDataStrings.length; i++) {
            displayDataStrings.put(inDataStrings[i], i);
            displayArray[i] = inDataStrings[i];
         }
      }
   }

   @Override
   public void refresh() {
      updateComboWidget();
   }

   public void addModifyListener(ModifyListener modifyListener) {
      if (dataCombo != null) {
         dataCombo.addModifyListener(modifyListener);
      }
   }

   public CCombo getComboBox() {
      return dataCombo;
   }

   /**
    * @return selected display value (eg. Donald Dunne)
    */
   public String get() {
      return data;
   }

   @Override
   public String getReportData() {
      return data;
   }

   private void updateComboWidget() {
      if (dataCombo != null && !dataCombo.isDisposed()) {
         if (displayDataStrings.containsKey(data)) {
            if (data.compareTo("") == 0) {
               dataCombo.select(0);
            } else {
               Integer pos = displayDataStrings.get(data);
               dataCombo.select(pos.intValue());
            }
         } else if (data.compareTo("") != 0) {
            // if not found, add it and select it
            displayDataStrings.put(data, displayDataStrings.size());
            dataCombo.add(data);
            dataCombo.select(displayDataStrings.size() - 1);
         } else {
            dataCombo.select(0);
         }
         if (displayDataStrings.size() < 15) {
            dataCombo.setVisibleItemCount(displayDataStrings.size());
         } else {
            dataCombo.setVisibleItemCount(15);
         }
         dataCombo.getParent().layout(true);
      }
      validate();
   }

   public void set(String data) {
      this.data = data;
      updateComboWidget();
   }

   public void set(int pos) {
      if (displayArray.length > pos) {
         this.data = displayArray[pos];
         updateComboWidget();
      }
   }

   public void remove(String data) {
      displayDataStrings.remove(data);
      if (dataCombo.indexOf(data) >= 0) {
         dataCombo.remove(data);
      }
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return data.equals("");
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + data;
   }

   public static void copy(XCombo from, XCombo to) throws IllegalStateException {
      to.set(from.get());
   }

   @Override
   public void dispose() {
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (dataCombo != null) {
         dataCombo.dispose();
      }
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
      super.dispose();
   }

   public String[] getDisplayArray() {
      return displayArray;
   }

   public String[] getInDataStrings() {
      return inDataStrings;
   }

   @Override
   public Object getData() {
      return dataCombo.getText();
   }

   public void setDefaultValue(String defaultValue) {
      this.data = defaultValue;
   }
}