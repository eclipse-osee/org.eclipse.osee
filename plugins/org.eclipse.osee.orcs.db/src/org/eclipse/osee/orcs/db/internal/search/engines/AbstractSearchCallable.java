/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSearchCallable extends CancellableCallable<Integer> {

   private final Log logger;
   private final OrcsSession session;
   private final QueryData queryData;

   public AbstractSearchCallable(Log logger, OrcsSession session, QueryData queryData) {
      super();
      this.logger = logger;
      this.session = session;
      this.queryData = queryData;
   }

   protected OrcsSession getSession() {
      return session;
   }

   protected QueryData getQueryData() {
      return queryData;
   }

   @Override
   public final Integer call() throws Exception {
      long startTime = System.currentTimeMillis();
      long endTime = startTime;
      Integer result = null;
      try {
         if (logger.isTraceEnabled()) {
            logger.trace("%s [start] - [%s]", getClass().getSimpleName(), queryData);
         }
         result = innerCall();
      } finally {
         endTime = System.currentTimeMillis() - startTime;
      }
      if (logger.isTraceEnabled()) {
         logger.trace("%s [%s] - completed [%s]", getClass().getSimpleName(), Lib.asTimeString(endTime), queryData);
      }
      return result;
   }

   protected abstract Integer innerCall() throws Exception;

}