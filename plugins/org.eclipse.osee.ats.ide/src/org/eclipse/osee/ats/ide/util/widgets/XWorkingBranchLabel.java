/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchLabel extends XWorkingBranchWidgetAbstract {

   public static String WIDGET_NAME = "XWorkingBranchLabel";
   public static String NAME = "Working Branch";

   public XWorkingBranchLabel() {
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      Composite mainComp = new Composite(parent, SWT.NONE);
      mainComp.setLayout(new GridLayout(1, false));
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(mainComp);
      }
      setLabel(NAME);
      labelWidget = new Label(mainComp, SWT.NONE);
      labelWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      refreshWorkingBranchWidget();
   }

   @Override
   protected void refreshWorkingBranchWidget() {
      if (getTeamArt() == null || labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (labelWidget != null && Widgets.isAccessible(labelWidget) && !getLabel().equals("")) {
                     IOseeBranch workBranch = getWorkingBranch();
                     String labelStr =
                        getLabel() + ": " + getStatus().getDisplayName() + (workBranch != null && workBranch.isValid() ? " - " + workBranch.getShortName() : "");
                     labelWidget.setText(labelStr);
                     if (getToolTip() != null) {
                        labelWidget.setToolTipText(getToolTip());
                     }
                     labelWidget.getParent().redraw();
                     if (getManagedForm() != null) {
                        getManagedForm().reflow(true);
                     }
                  }
               }
            });
         }
      };
      Thread thread = new Thread(runnable);
      thread.start();
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

}
