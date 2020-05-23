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

package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Robert A. Fisher
 */
public class CancelWatcher extends Thread {
   private final Thread thread;
   private final IProgressMonitor monitor;
   private boolean done;
   private boolean cancelled;

   public CancelWatcher(Thread thread, IProgressMonitor monitor) {
      super();
      this.thread = thread;
      this.monitor = monitor;
      this.done = false;
      this.cancelled = false;

      setDaemon(true);
   }

   @Override
   public void run() {
      while (!done) {
         try {
            sleep(500);
         } catch (InterruptedException e) {
            done = true;
         }
         if (monitor.isCanceled()) {
            cancelled = true;
            done = true;
            thread.interrupt();
         }
      }
   }

   public void done() {
      done = true;
   }

   /**
    * @return Returns the cancelled.
    */
   public boolean isCancelled() {
      return cancelled;
   }
}
