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

   /**
    * @param thread
    * @param monitor
    */
   public CancelWatcher(Thread thread, IProgressMonitor monitor) {
      super();
      this.thread = thread;
      this.monitor = monitor;
      this.done = false;
      this.cancelled = false;

      setDaemon(true);
   }

   /* (non-Javadoc)
    * @see java.lang.Thread#run()
    */
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
