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
package org.eclipse.osee.ote.core.framework.thread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public class OteThreadFactory implements ThreadFactory {

   private List<WeakReference<OteThread>> threads;
   private String threadName;

   protected OteThreadFactory(String threadName) {
      this.threadName = threadName;
      this.threads = new CopyOnWriteArrayList<WeakReference<OteThread>>();
   }

   /* (non-Javadoc)
    * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
    */
   public Thread newThread(Runnable runnable) {
      OteThread thread = new OteThread(runnable, threadName + ":" + threads.size());
      this.threads.add(new WeakReference<OteThread>(thread));
      return thread;
   }

   public List<OteThread> getThreads() {
      List<OteThread> toReturn = new ArrayList<OteThread>();
      for (WeakReference<OteThread> weak : threads) {
         OteThread thread = weak.get();
         if (thread != null) {
            toReturn.add(thread);
         }
      }
      return toReturn;
   }
}
