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
package org.eclipse.osee.orcs.core.internal.artifact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class ArtifactBuilderImpl implements ArtifactBuilder, ArtifactCollector {

   private final Map<Integer, RelationContainer> relationContainers = new HashMap<Integer, RelationContainer>();;
   private final Map<Integer, AttributeManager> attributeContainers = new HashMap<Integer, AttributeManager>();
   private final List<ArtifactReadable> artifacts = new ArrayList<ArtifactReadable>();

   private final ArtifactProxyFactory proxyFactory;

   public ArtifactBuilderImpl(ArtifactProxyFactory proxyFactory) {
      super();
      this.proxyFactory = proxyFactory;
   }

   @Override
   public List<ArtifactReadable> getArtifacts() {
      return artifacts;
   }

   @Override
   public ArtifactDataHandler createArtifactDataHandler() {
      return createArtifactMapper(this);
   }

   @Override
   public AttributeDataHandler createAttributeDataHandler() {
      return createAttributeMapper(attributeContainers);
   }

   @Override
   public RelationDataHandler createRelationDataHandler() {
      return createRelationMapper(relationContainers);
   }

   @Override
   public void onArtifact(ArtifactReadable readable) {
      ArtifactImpl artifactImpl = proxyFactory.getProxiedObject(readable);
      ArtifactReadable proxyReadable = proxyFactory.createReadable(artifactImpl);
      artifacts.add(proxyReadable);

      RelationContainer relContainer = artifactImpl.getRelationContainer();

      attributeContainers.put(readable.getLocalId(), artifactImpl);
      relationContainers.put(readable.getLocalId(), relContainer);
   }

   protected abstract ArtifactDataHandler createArtifactMapper(ArtifactCollector collector);

   protected abstract AttributeDataHandler createAttributeMapper(Map<Integer, AttributeManager> attributeContainers);

   protected abstract RelationDataHandler createRelationMapper(Map<Integer, RelationContainer> relationContainers);

}