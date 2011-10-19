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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandlerFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;
import org.eclipse.osee.orcs.core.ds.RelationRowHandlerFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactReciever;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactRowMapper;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeRowMapper;
import org.eclipse.osee.orcs.core.internal.relation.RelationRowMapper;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public class OrcsObjectLoader {

   private final DataLoader dataLoader;
   private final Log logger;
   private final ArtifactTypeCache artifactTypeCache;
   private final BranchCache branchCache;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   public OrcsObjectLoader(Log logger, DataLoader dataLoader, ArtifactFactory artifactFactory, AttributeFactory attributeFactory, ArtifactTypeCache artifactTypeCache, BranchCache branchCache) {
      super();
      this.logger = logger;
      this.dataLoader = dataLoader;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;

      this.artifactTypeCache = artifactTypeCache;
      this.branchCache = branchCache;
   }

   public int countObjects(QueryContext queryContext) throws OseeCoreException {
      return dataLoader.countArtifacts(queryContext);
   }

   public List<ReadableArtifact> load(QueryContext queryContext, LoadOptions loadOptions, SessionContext sessionContext) throws OseeCoreException {

      List<ReadableArtifact> artifacts = new ArrayList<ReadableArtifact>();

      ArtifactCollector artifactHandler = new ArtifactCollector(logger, attributeFactory, artifacts);

      ArtifactRowHandler artifactRowHandler =
         new ArtifactRowMapper(logger, sessionContext, branchCache, artifactTypeCache, artifactFactory, artifactHandler);

      dataLoader.loadArtifacts(artifactRowHandler, queryContext, loadOptions, artifactHandler, artifactHandler);

      return artifacts;
   }

   private static class ArtifactCollector implements ArtifactReciever, RelationRowHandlerFactory, AttributeRowHandlerFactory {

      private final Map<Integer, RelationContainer> relationContainers = new HashMap<Integer, RelationContainer>();;
      private final Map<Integer, AttributeContainer> attributeContainers = new HashMap<Integer, AttributeContainer>();

      private final List<ReadableArtifact> artifacts;

      private final Log logger;
      private final AttributeFactory attributeFactory;

      public ArtifactCollector(Log logger, AttributeFactory attributeFactory, List<ReadableArtifact> artifacts) {
         this.logger = logger;
         this.attributeFactory = attributeFactory;
         this.artifacts = artifacts;
      }

      @Override
      public AttributeRowHandler createAttributeRowHandler() {
         return new AttributeRowMapper(logger, attributeFactory, attributeContainers);
      }

      @Override
      public RelationRowHandler createRelationRowHandler() {
         return new RelationRowMapper(logger, relationContainers);
      }

      @Override
      public void onArtifact(ReadableArtifact artifact, boolean isArtifactAlreadyLoaded) {
         artifacts.add(artifact);
         relationContainers.put(artifact.getId(), ((Artifact) artifact).getRelationContainer());
         attributeContainers.put(artifact.getId(), ((Artifact) artifact).getAttributeContainer());
      }

   }

}
