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
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.panels.BranchSelectSimpleComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Roberto E. Escobar
 */
public class XBranchSelectComboWidget extends XWidget implements Listener {
   public static final String WIDGET_ID = XBranchSelectWidget.class.getSimpleName();

   private BranchSelectSimpleComposite selectComposite;
   private Composite composite;
   private int defaultBranch;

   private final List<Listener> listeners = new ArrayList<Listener>();

   public XBranchSelectComboWidget(String label) {
      super(label);
      this.defaultBranch = -1;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = null;

      if (!verticalLabel && (horizontalSpan < 2)) {
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
      selectComposite = BranchSelectSimpleComposite.createBranchSelectComposite(composite, SWT.NONE);
      if (defaultBranch != -1) {
         selectComposite.restoreWidgetValues(null, Integer.toString(defaultBranch));
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
      return composite;
   }

   @Override
   public Branch getData() {
      return selectComposite.getSelectedBranch();
   }

   @Override
   public String getReportData() {
      Branch branch = selectComposite.getSelectedBranch();
      return branch != null ? branch.getName() : "";
   }

   @Override
   public String getXmlData() {
      System.out.println("Get XML Data Called: ");
      return "";
   }

   @Override
   public IStatus isValid() {
      if (selectComposite.getSelectedBranch() == null) {
         return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Must select a Branch");
      }
      return Status.OK_STATUS;
   }

   @Override
   public void refresh() {
   }

   @Override
   public void setFocus() {
      selectComposite.setFocus();
   }

   @Override
   public void setXmlData(String str) {
      System.out.println("Set XML Data Called: " + str);
   }

   @Override
   public String toHTML(String labelFont) {
      System.out.println("Set to Html Called: " + labelFont);
      return "";
   }

   @Override
   public void setDisplayLabel(final String displayLabel) {
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            XBranchSelectComboWidget.super.setDisplayLabel(displayLabel);
            getLabelWidget().setText(displayLabel);
         }
      });
   }

   @Override
   public void setToolTip(final String toolTip) {
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            if (Strings.isValid(toolTip) != false) {
               XBranchSelectComboWidget.super.setToolTip(toolTip);
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

}
