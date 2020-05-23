/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Shawn F. Cook
 */
public abstract class XWorkingBranchButtonAbstract extends XWorkingBranchWidgetAbstract {
   private Button button;

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      button = createNewButton(parent);
      initButton(button);
      refreshEnablement(button);
   }

   @Override
   protected void refreshWorkingBranchWidget() {
      if (getTeamArt() == null) {
         return;
      }
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (Widgets.isAccessible(button)) {
                     refreshEnablement(button);
                  }
               }
            });
         }
      };
      Thread thread = new Thread(runnable);
      thread.start();
   }

   protected abstract void initButton(Button button);

   protected abstract void refreshEnablement(Button button);

   private Button createNewButton(Composite comp) {
      if (toolkit != null) {
         return toolkit.createButton(comp, null, SWT.PUSH);
      }
      return new Button(comp, SWT.PUSH);
   }
}
