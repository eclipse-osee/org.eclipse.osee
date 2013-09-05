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

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactBuilderFactoryImpl implements ArtifactBuilderFactory {

   private final Log logger;
   private final ExternalArtifactManager proxyFactory;

   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   public ArtifactBuilderFactoryImpl(Log logger, ExternalArtifactManager proxyFactory, ArtifactFactory artifactFactory, AttributeFactory attributeFactory) {
      super();
      this.logger = logger;
      this.proxyFactory = proxyFactory;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
   }

   @Override
   public ArtifactBuilder createArtifactBuilder(OrcsSession session) {
      return new ArtifactBuilderImpl(logger, proxyFactory, artifactFactory, attributeFactory, session);
   }

}
