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
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectComposite;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class XBranchSelectWidget extends GenericXWidget implements Listener {
   public static final String WIDGET_ID = XBranchSelectWidget.class.getSimpleName();

   protected BranchSelectComposite selectComposite;
   private Composite composite;
   private BranchId defaultBranch;

   private final List<Listener> listeners = new ArrayList<>();

   public XBranchSelectWidget(String label) {
      super(label);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = null;

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (isDisplayLabel() && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout gL = new GridLayout();
         gL.marginWidth = 0;
         gL.marginHeight = 0;
         composite.setLayout(gL);
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }
      selectComposite = BranchSelectComposite.createBranchSelectComposite(composite, SWT.NONE);
      if (defaultBranch != null) {
         selectComposite.setDefaultSelectedBranch(defaultBranch);
      }
      selectComposite.addListener(this);
   }

   @Override
   public void dispose() {
      if (selectComposite != null) {
         selectComposite.removeListener(this);
         selectComposite.dispose();
      }
   }

   @Override
   public Control getControl() {
      return selectComposite.getBranchSelectText();
   }

   public Control getButtonControl() {
      return selectComposite.getBranchSelectButton();
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (selectComposite != null) {
         if (getControl() != null && !getControl().isDisposed()) {
            getControl().setEnabled(editable);
         }
         if (getButtonControl() != null && !getButtonControl().isDisposed()) {
            getButtonControl().setEnabled(editable);
         }
      }
   }

   @Override
   public BranchId getData() {
      return getSelection();
   }

   public BranchId getSelection() {
      return selectComposite.getSelectedBranch();
   }

   @Override
   public String getReportData() {
      BranchId branch = selectComposite.getSelectedBranch();
      return branch == null ? "" : BranchManager.getBranchName(branch);
   }

   @Override
   public IStatus isValid() {
      if (isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must select a Branch");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return selectComposite.getSelectedBranch() == null;
   }

   @Override
   public void setFocus() {
      selectComposite.setFocus();
   }

   @Override
   public void setDisplayLabel(final String displayLabel) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XBranchSelectWidget.super.setDisplayLabel(displayLabel);
            getLabelWidget().setText(displayLabel);
         }
      });
   }

   @Override
   public void setToolTip(final String toolTip) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Strings.isValid(toolTip)) {
               XBranchSelectWidget.super.setToolTip(toolTip);
               if (selectComposite != null && selectComposite.isDisposed() != true) {
                  selectComposite.setToolTipText(toolTip);
                  for (Control control : selectComposite.getChildren()) {
                     control.setToolTipText(toolTip);
                  }
               }
            }
         }
      });
   }

   @Override
   public void handleEvent(Event event) {
      super.validate();
      notifyListeners(event);
      notifyXModifiedListeners();
   }

   public void addListener(Listener listener) {
      listeners.add(listener);
   }

   public void removeListener(Listener listener) {
      listeners.remove(listener);
   }

   private void notifyListeners(Event event) {
      for (Listener listener : listeners) {
         listener.handleEvent(event);
      }
   }

   public void setSelection(BranchId branch) {
      defaultBranch = branch;
      if (selectComposite != null) {
         selectComposite.setSelected(branch);
      }
   }

   public BranchSelectComposite getSelectComposite() {
      return selectComposite;
   }
}