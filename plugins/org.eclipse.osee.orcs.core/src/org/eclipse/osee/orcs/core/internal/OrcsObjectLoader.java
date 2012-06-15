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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandlerFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationDataHandlerFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactCollector;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactRowMapper;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.artifact.RelationContainer;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeRowMapper;
import org.eclipse.osee.orcs.core.internal.relation.RelationRowMapper;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public class OrcsObjectLoader {

   private final DataLoader dataLoader;
   private final Log logger;
   private final BranchCache branchCache;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   public OrcsObjectLoader(Log logger, DataLoader dataLoader, ArtifactFactory artifactFactory, AttributeFactory attributeFactory, BranchCache branchCache) {
      super();
      this.logger = logger;
      this.dataLoader = dataLoader;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;

      this.branchCache = branchCache;
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

      List<ArtifactReadable> artifacts = new ArrayList<ArtifactReadable>();

      ArtifactCollectorImpl artifactHandler =
         new ArtifactCollectorImpl(logger, artifactFactory, attributeFactory, artifacts);

      ArtifactDataHandler artifactRowHandler = new ArtifactRowMapper(sessionContext, artifactFactory, artifactHandler);

      dataLoader.loadArtifacts(cancellation, artifactRowHandler, branchCache.getLocalId(branch), ids, loadOptions,
         artifactHandler, artifactHandler);
      if (logger.isTraceEnabled()) {
         logger.trace("Objects from ids loaded in [%s]", Lib.getElapseString(startTime));
      }
      return artifacts;
   }

   public List<ArtifactReadable> load(HasCancellation cancellation, QueryContext queryContext, LoadOptions loadOptions, SessionContext sessionContext) throws OseeCoreException {
      long startTime = 0;
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
      }

      List<ArtifactReadable> artifacts = new ArrayList<ArtifactReadable>();

      ArtifactCollectorImpl artifactHandler =
         new ArtifactCollectorImpl(logger, artifactFactory, attributeFactory, artifacts);

      ArtifactDataHandler artifactRowHandler = new ArtifactRowMapper(sessionContext, artifactFactory, artifactHandler);

      dataLoader.loadArtifacts(cancellation, artifactRowHandler, queryContext, loadOptions, artifactHandler,
         artifactHandler);

      if (logger.isTraceEnabled()) {
         logger.trace("Objects from query loaded in [%s]", Lib.getElapseString(startTime));
      }
      return artifacts;
   }

   private static class ArtifactCollectorImpl implements ArtifactCollector, RelationDataHandlerFactory, AttributeDataHandlerFactory {

      private final Map<Integer, RelationContainer> relationContainers = new HashMap<Integer, RelationContainer>();;
      private final Map<Integer, AttributeContainer> attributeContainers = new HashMap<Integer, AttributeContainer>();

      private final List<ArtifactReadable> artifacts;

      private final Log logger;
      private final AttributeFactory attributeFactory;
      private final ArtifactFactory artifactFactory;

      public ArtifactCollectorImpl(Log logger, ArtifactFactory artifactFactory, AttributeFactory attributeFactory, List<ArtifactReadable> artifacts) {
         this.logger = logger;
         this.artifactFactory = artifactFactory;
         this.attributeFactory = attributeFactory;
         this.artifacts = artifacts;
      }

      @Override
      public AttributeDataHandler createAttributeDataHandler() {
         return new AttributeRowMapper(logger, attributeFactory, attributeContainers);
      }

      @Override
      public RelationDataHandler createRelationDataHandler() {
         return new RelationRowMapper(relationContainers);
      }

      @Override
      public void onArtifact(ArtifactReadable artifact, LoadSourceType loadSourceType) {
         artifacts.add(artifact);

         ArtifactImpl artifactImpl = artifactFactory.asArtifactImpl(artifact);
         AttributeContainer attrContainer = artifactImpl.getAttributeContainer();
         RelationContainer relContainer = artifactImpl.getRelationContainer();

         attributeContainers.put(artifact.getLocalId(), attrContainer);
         relationContainers.put(artifact.getLocalId(), relContainer);
      }
   }

}
