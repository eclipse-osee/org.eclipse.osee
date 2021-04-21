/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public abstract class XCheckBoxes extends GenericXWidget {

   protected List<XCheckBoxData> checkBoxes;
   private Composite parent;
   protected Composite composite;
   private final int numColumns;
   protected Composite checkBoxesComp;

   public XCheckBoxes(String displayLabel, int numColumns) {
      super(displayLabel);
      this.numColumns = numColumns;
   }

   abstract protected List<XCheckBoxData> getCheckBoxes();

   @Override
   public Control getControl() {
      return composite;
   }

   public List<XCheckBoxData> getLoadedCheckboxes() {
      if (checkBoxes == null) {
         checkBoxes = getCheckBoxes();
      }
      return checkBoxes;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      for (XCheckBoxData cbd : getLoadedCheckboxes()) {
         XCheckBox cb = cbd.getCheckBox();
         if (cb != null && cb.getControl() != null && !cb.getControl().isDisposed()) {
            cb.getControl().setEnabled(editable);
         }
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData());

      labelWidget = new Label(composite, SWT.NONE);
      labelWidget.setText(getLabel());
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }

      checkBoxesComp = new Composite(composite, SWT.BORDER);
      layout = new GridLayout(numColumns, true);
      checkBoxesComp.setLayout(layout);
      checkBoxesComp.setLayoutData(new GridData());

      List<XCheckBoxData> loadedCheckboxes = getLoadedCheckboxes();
      for (XCheckBoxData cbd : loadedCheckboxes) {
         XCheckBox cb = new XCheckBox(getTaskLabel(cbd));
         cbd.setCheckBox(cb);
         cb.set(cbd.isChecked());
         cb.setLabelAfter(true);
         cb.createControls(checkBoxesComp, 2);
         cb.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
               cbd.setChecked(cb.isChecked());
               notifyXModifiedListeners();
            }
         });
      }
   }

   protected String getTaskLabel(XCheckBoxData cbd) {
      return cbd.getLabel();
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      for (XCheckBoxData cbd : getLoadedCheckboxes()) {
         if (cbd.getCheckBox() != null) {
            cbd.getCheckBox().dispose();
         }
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

}