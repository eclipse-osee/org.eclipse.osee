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
package org.eclipse.osee.executor.admin.internal;

import java.util.Map.Entry;
import java.util.TimerTask;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ThreadCleaner extends TimerTask {

   private final ExecutorCache cache;
   private final Log logger;

   public ThreadCleaner(Log logger, ExecutorCache cache) {
      super();
      this.logger = logger;
      this.cache = cache;
   }

   @Override
   public void run() {
      cleanUpThreadFactory();
      cleanUpExecutionCache();
   }

   private void cleanUpThreadFactory() {
      for (Entry<String, ExecutorThreadFactory> entry : cache.getThreadFactories().entrySet()) {
         try {
            ExecutorThreadFactory factory = entry.getValue();
            factory.cleanUp();
         } catch (Throwable ex) {
            logger.error(ex, "Error removing dead threads for [%s]", entry.getKey());
         }
      }
   }

   private void cleanUpExecutionCache() {
      for (Entry<String, ExecutorWorkCache> entry : cache.getWorkers().entrySet()) {
         try {
            ExecutorWorkCache workers = entry.getValue();
            workers.cleanUp();
         } catch (Throwable ex) {
            logger.error(ex, "Error removing dead workers for [%s]", entry.getKey());
         }
      }
   }
}