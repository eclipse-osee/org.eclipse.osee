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
package org.eclipse.osee.orcs.core.internal.proxy.handler;

import java.lang.reflect.InvocationHandler;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.AttributeProxyFactory;
import org.eclipse.osee.orcs.core.internal.proxy.InvocationHandlerFactory;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactInvocationHandlerFactory implements InvocationHandlerFactory<Artifact> {

   private final ArtifactFactory artifactFactory;
   private final AttributeProxyFactory attributeProxyFactory;

   public ArtifactInvocationHandlerFactory(ArtifactFactory artifactFactory) {
      super();
      this.artifactFactory = artifactFactory;
      this.attributeProxyFactory = new AttributeProxyFactory(new AttributeInvocationHandlerFactory());
   }

   @Override
   public InvocationHandler createReadHandler(Artifact toProxy) {
      return new ReadableInvocationHandler<Artifact>(toProxy);
   }

   @Override
   public InvocationHandler createWriteHandler(Artifact toProxy) {
      return new ArtifactWriteableInvocationHandler(artifactFactory, attributeProxyFactory, toProxy);
   }

   @SuppressWarnings("rawtypes")
   private static class AttributeInvocationHandlerFactory implements InvocationHandlerFactory<Attribute> {

      @Override
      public InvocationHandler createReadHandler(Attribute toProxy) {
         return new ReadableInvocationHandler<Attribute>(toProxy);
      }

      @Override
      public InvocationHandler createWriteHandler(Attribute toProxy) {
         return new WriteableInvocationHandler<Attribute>(toProxy);
      }
   }
}
