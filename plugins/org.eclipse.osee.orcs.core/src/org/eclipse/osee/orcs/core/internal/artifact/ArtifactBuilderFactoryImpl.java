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

import java.util.Map;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeRowMapper;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.core.internal.relation.RelationRowMapper;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactBuilderFactoryImpl implements ArtifactBuilderFactory {

   private final Log logger;
   private final ArtifactProxyFactory proxyFactory;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   public ArtifactBuilderFactoryImpl(Log logger, ArtifactProxyFactory proxyFactory, ArtifactFactory artifactFactory, AttributeFactory attributeFactory) {
      super();
      this.logger = logger;
      this.proxyFactory = proxyFactory;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
   }

   @Override
   public ArtifactBuilder createArtifactBuilder(final SessionContext context) {
      return new ArtifactBuilderImpl(proxyFactory) {

         @Override
         protected AttributeDataHandler createAttributeMapper(Map<Integer, AttributeManager> attributeContainers) {
            return new AttributeRowMapper(logger, attributeFactory, attributeContainers);
         }

         @Override
         protected RelationDataHandler createRelationMapper(Map<Integer, RelationContainer> relationContainers) {
            return new RelationRowMapper(relationContainers);
         }

         @Override
         protected ArtifactDataHandler createArtifactMapper(ArtifactCollector collector) {
            return new ArtifactRowMapper(context, artifactFactory, collector);
         }
      };
   }
}
