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
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSearchCallable<T> extends CancellableCallable<T> {

   protected final Log logger;
   protected final QueryEngine queryEngine;
   protected final OrcsObjectLoader objectLoader;

   protected final SessionContext sessionContext;
   protected final LoadLevel loadLevel;
   protected final CriteriaSet criteriaSet;
   protected final QueryOptions options;

   public AbstractSearchCallable(Log logger, QueryEngine queryEngine, OrcsObjectLoader objectLoader, SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
      this.sessionContext = sessionContext;
      this.loadLevel = loadLevel;
      this.criteriaSet = criteriaSet;
      this.options = options;
   }

   @Override
   public final T call() throws Exception {
      long startTime = 0;
      if (logger.isDebugEnabled()) {
         startTime = System.currentTimeMillis();
      }

      T result = innerCall();

      if (logger.isDebugEnabled()) {
         logger.debug("Search [%s] completed in [%s]\n\tCriteria - [%s]\n\tOptions  - [%s]",
            getClass().getSimpleName(), Lib.getElapseString(startTime), criteriaSet, options);
      }
      return result;
   }

   protected abstract T innerCall() throws Exception;
}
