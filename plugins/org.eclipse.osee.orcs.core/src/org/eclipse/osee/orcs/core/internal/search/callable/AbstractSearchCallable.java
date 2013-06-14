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
package org.eclipse.osee.orcs.core.internal.search.callable;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.search.QueryCollector;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSearchCallable<T> extends CancellableCallable<T> {

   protected final Log logger;
   protected final QueryEngine queryEngine;
   protected final SessionContext sessionContext;
   protected final LoadLevel loadLevel;
   protected final QueryData queryData;
   private final QueryCollector collector;
   protected final AttributeTypes types;

   public AbstractSearchCallable(Log logger, QueryEngine queryEngine, QueryCollector collector, SessionContext sessionContext, LoadLevel loadLevel, QueryData queryData, AttributeTypes types) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.collector = collector;
      this.sessionContext = sessionContext;
      this.loadLevel = loadLevel;
      this.queryData = queryData;
      this.types = types;
   }

   protected Log getLogger() {
      return logger;
   }

   @Override
   public final T call() throws Exception {
      long startTime = System.currentTimeMillis();
      long endTime = startTime;
      T result = null;
      try {
         if (logger.isTraceEnabled()) {
            logger.trace("%s [start] - [%s]", getClass().getSimpleName(), queryData);
         }
         result = innerCall();
      } finally {
         endTime = System.currentTimeMillis() - startTime;
      }
      if (result != null) {
         notifyStats(result, endTime);
      }
      if (logger.isTraceEnabled()) {
         logger.trace("%s [%s] - completed [%s]", getClass().getSimpleName(), Lib.asTimeString(endTime), queryData);
      }
      return result;
   }

   private void notifyStats(T result, long processingTime) {
      if (collector != null) {
         try {
            int itemsFound = getCount(result);
            collector.collect(sessionContext.getSessionId(), itemsFound, processingTime, queryData);
         } catch (Exception ex) {
            logger.error(ex, "Error reporting search to search collector\n%s", queryData);
         }
      }
   }

   protected abstract int getCount(T results) throws Exception;

   protected abstract T innerCall() throws Exception;

}
