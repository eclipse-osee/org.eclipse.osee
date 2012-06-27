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
package org.eclipse.osee.orcs.core.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public class OrcsObjectLoader {

   private final DataLoader dataLoader;
   private final Log logger;
   private final ArtifactBuilderFactory builderFactory;

   public OrcsObjectLoader(Log logger, DataLoader dataLoader, ArtifactBuilderFactory builderFactory) {
      super();
      this.logger = logger;
      this.dataLoader = dataLoader;
      this.builderFactory = builderFactory;
   }

   public int countObjects(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException {
      int count = -1;
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }

      count = dataLoader.countArtifacts(cancellation, queryContext);

      if (logger.isTraceEnabled()) {
         logger.trace("Counted objects in [%s]", Lib.getElapseString(startTime));
      }
      return count;
   }

   public List<ArtifactReadable> load(HasCancellation cancellation, IOseeBranch branch, Collection<Integer> ids, LoadOptions loadOptions, SessionContext sessionContext) throws OseeCoreException {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }

      ArtifactBuilder builder = builderFactory.createArtifactBuilder(sessionContext);
      dataLoader.loadArtifacts(cancellation, builder, branch, ids, loadOptions);
      if (logger.isTraceEnabled()) {
         logger.trace("Objects from ids loaded in [%s]", Lib.getElapseString(startTime));
      }
      return builder.getArtifacts();
   }

   public List<ArtifactReadable> load(HasCancellation cancellation, QueryContext queryContext, LoadOptions loadOptions, SessionContext sessionContext) throws OseeCoreException {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }
      ArtifactBuilder builder = builderFactory.createArtifactBuilder(sessionContext);
      dataLoader.loadArtifacts(cancellation, builder, queryContext, loadOptions);

      if (logger.isTraceEnabled()) {
         logger.trace("Objects from query loaded in [%s]", Lib.getElapseString(startTime));
      }
      return builder.getArtifacts();
   }

}
