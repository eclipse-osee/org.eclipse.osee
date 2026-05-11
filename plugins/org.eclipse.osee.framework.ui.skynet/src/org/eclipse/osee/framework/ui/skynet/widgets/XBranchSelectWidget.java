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
import org.eclipse.osee.framework.core.data.BranchToken;
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

   protected BranchSelectComposite branchSelComp;
   private Composite composite;

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
      branchSelComp = BranchSelectComposite.createBranchSelectComposite(composite, SWT.NONE);
      branchSelComp.addListener(this);
   }

   @Override
   public void dispose() {
      if (branchSelComp != null) {
         branchSelComp.removeListener(this);
         branchSelComp.dispose();
      }
   }

   @Override
   public Control getControl() {
      return branchSelComp.getBranchSelectText();
   }

   public Control getButtonControl() {
      return branchSelComp.getBranchSelectButton();
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (branchSelComp != null) {
         if (getControl() != null && !getControl().isDisposed()) {
            getControl().setEnabled(editable);
         }
         if (getButtonControl() != null && !getButtonControl().isDisposed()) {
            getButtonControl().setEnabled(editable);
         }
      }
   }

   @Override
   public BranchToken getData() {
      return getSelection();
   }

   public BranchToken getSelection() {
      return branchSelComp.getSelectedBranch();
   }

   @Override
   public String getReportData() {
      BranchId branch = branchSelComp.getSelectedBranch();
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
      return branchSelComp.getSelectedBranch() == null;
   }

   @Override
   public void setFocus() {
      branchSelComp.setFocus();
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
               if (branchSelComp != null && branchSelComp.isDisposed() != true) {
                  branchSelComp.setToolTipText(toolTip);
                  for (Control control : branchSelComp.getChildren()) {
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

   public void setSelection(BranchToken branch) {
      if (branchSelComp != null) {
         branchSelComp.setSelected(branch);
      }
   }

   public BranchSelectComposite getSelectComposite() {
      return branchSelComp;
   }

}