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
package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
public class MaxMatchCountConfirmer implements ISearchConfirmer {
   private static final int MAX_RESULTS = 2000;

   public boolean canProceed(final int count) {
      if (count < MAX_RESULTS) return true;

      final MutableBoolean result = new MutableBoolean(false);
      final MutableBoolean done = new MutableBoolean(false);

      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            result.setValue(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirm Search",
                  "The search returned " + count + " results and may take a long time to load, continue?"));
            synchronized (done) {
               done.setValue(true);
               done.notifyAll();
            }
         }
      });

      synchronized (done) {
         while (!done.getValue()) {
            try {
               done.wait();
            } catch (InterruptedException e) {
               break;
            }
         }
      }

      return result.getValue();
   }
}