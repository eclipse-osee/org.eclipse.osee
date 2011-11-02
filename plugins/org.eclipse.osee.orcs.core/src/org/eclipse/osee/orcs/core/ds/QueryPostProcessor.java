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
package org.eclipse.osee.orcs.core.ds;

import java.util.List;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public abstract class QueryPostProcessor extends CancellableCallable<List<Match<ReadableArtifact, ReadableAttribute<?>>>> {

   private final Log logger;
   private List<ReadableArtifact> artifacts;

   protected QueryPostProcessor(Log logger) {
      this.logger = logger;
   }

   public void setItemsToProcess(List<ReadableArtifact> artifacts) {
      this.artifacts = artifacts;
   }

   protected List<ReadableArtifact> getItemsToProcess() {
      return artifacts;
   }

   protected Log getLogger() {
      return logger;
   }

   @Override
   public final List<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results = innerCall();
      checkForCancelled();
      if (logger.isTraceEnabled()) {
         logger.trace("Query post processor processed [%s] items in [%s]", getItemsToProcess().size(),
            Lib.getElapseString(startTime));
      }
      return results;
   }

   protected abstract List<Match<ReadableArtifact, ReadableAttribute<?>>> innerCall() throws Exception;
}
