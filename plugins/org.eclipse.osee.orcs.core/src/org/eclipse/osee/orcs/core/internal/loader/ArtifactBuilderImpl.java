/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManager;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactBuilderImpl extends LoadDataHandlerAdapter implements ArtifactBuilder {

   private final Map<Integer, RelationContainer> relations = new HashMap<Integer, RelationContainer>();;
   private final Map<Integer, AttributeManager> attributes = new HashMap<Integer, AttributeManager>();

   private final Log logger;
   private final ArtifactProxyFactory proxyFactory;

   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   private final Map<Integer, Artifact> artifacts = new LinkedHashMap<Integer, Artifact>();
   private final Set<Artifact> created = new HashSet<Artifact>();
   private List<ArtifactReadable> readables;

   public ArtifactBuilderImpl(Log logger, ArtifactProxyFactory proxyFactory, ArtifactFactory artifactFactory, AttributeFactory attributeFactory) {
      super();
      this.logger = logger;
      this.proxyFactory = proxyFactory;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
   }

   @Override
   public List<ArtifactReadable> getArtifacts() {
      return readables != null ? readables : Collections.<ArtifactReadable> emptyList();
   }

   @Override
   public void onLoadDescription(LoadDescription data) {
      //
   };

   @Override
   public void onLoadStart() {
      artifacts.clear();
      created.clear();
      readables = null;
   }

   @Override
   public void onLoadEnd() {
      // Make artifacts available to others
      //      for (Artifact artifact : created) {
      //         cache.cache(artifact);
      //      }
      created.clear();

      readables = new ArrayList<ArtifactReadable>(artifacts.size());
      for (Artifact artifact : artifacts.values()) {
         ArtifactReadable readable = proxyFactory.createReadable(artifact);
         readables.add(readable);
      }
      artifacts.clear();
   }

   private Artifact getCachedArtifact(ArtifactData data) {
      Artifact toReturn = artifacts.get(data.getLocalId());
      if (toReturn == null) {
         //         toReturn = cache.get(data);
      }
      return toReturn;
   }

   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      Artifact artifact = getCachedArtifact(data);
      if (artifact == null) {
         artifact = artifactFactory.createArtifact(data);
         created.add(artifact);
      }
      artifacts.put(artifact.getLocalId(), artifact);
      attributes.put(artifact.getLocalId(), artifact);
      relations.put(artifact.getLocalId(), artifact.getRelationContainer());
   }

   @Override
   public void onData(AttributeData data) throws OseeCoreException {
      AttributeManager container = attributes.get(data.getArtifactId());
      if (container == null) {
         logger.warn("Orphaned attribute detected - [%s]", data);
      } else {
         attributeFactory.createAttribute(container, data);
      }
   }

   @Override
   public void onData(RelationData data) throws OseeCoreException {
      RelationContainer container = relations.get(data.getParentId());
      Conditions.checkNotNull(container, "RelationContainer",
         "Invalid relation data container not found - data[%s]. . ", data);
      container.add(data);
   }

}