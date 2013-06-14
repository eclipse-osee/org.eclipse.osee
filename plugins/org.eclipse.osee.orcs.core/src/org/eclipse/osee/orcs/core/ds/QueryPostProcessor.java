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
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public abstract class QueryPostProcessor extends CancellableCallable<List<Match<ArtifactReadable, AttributeReadable<?>>>> implements DataPostProcessor<List<Match<ArtifactReadable, AttributeReadable<?>>>> {

   private final Log logger;
   private List<ArtifactReadable> artifacts;
   private AttributeTypes types;

   protected QueryPostProcessor(Log logger) {
      this.logger = logger;
   }

   public void setItemsToProcess(List<ArtifactReadable> artifacts) {
      this.artifacts = artifacts;
   }

   protected List<ArtifactReadable> getItemsToProcess() {
      return artifacts;
   }

   protected Log getLogger() {
      return logger;
   }

   protected AttributeTypes getAttributeTypes() {
      return types;
   }

   public void setAttributeTypes(AttributeTypes types) {
      this.types = types;
   }

   @Override
   public final List<Match<ArtifactReadable, AttributeReadable<?>>> call() throws Exception {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }
      List<Match<ArtifactReadable, AttributeReadable<?>>> results = innerCall();
      checkForCancelled();
      if (logger.isTraceEnabled()) {
         logger.trace("Query post processor processed [%s] items in [%s]", getItemsToProcess().size(),
            Lib.getElapseString(startTime));
      }
      return results;
   }

   protected abstract List<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception;
}
