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
package org.eclipse.osee.framework.search.engine.internal;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;

/**
 * @author Roberto E. Escobar
 */
final class StartUpRunnable extends TimerTask {
   private final ISearchEngineTagger tagger;

   StartUpRunnable(ISearchEngineTagger tagger) {
      this.tagger = tagger;
   }

   @Override
   public void run() {
      try {
         if (OseeServerProperties.isCheckTagQueueOnStartupAllowed()) {
            List<Integer> queries = JoinUtility.getAllTagQueueQueryIds();
            OseeLog.log(SearchEngineTagger.class, Level.INFO, String.format(
                  "On Start-Up Tagging - [%d] tag queue items.", queries.size()));
            for (Integer queryId : queries) {
               tagger.tagByQueueQueryId(queryId);
            }
         } else {
            OseeLog.log(SearchEngineTagger.class, Level.INFO, "Tagging on Server Startup was not run.");
         }
      } catch (Exception ex) {
         OseeLog.log(SearchEngineTagger.class, Level.INFO, "Tagging on Server Startup was not run.");
      }
   }
}
