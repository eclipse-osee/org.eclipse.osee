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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.annotations.ReadAttributes;
import org.eclipse.osee.orcs.annotations.WriteAttributes;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.AttributeProxyFactory;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeWriteable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactWriteableInvocationHandler extends WriteableInvocationHandler<Artifact> {

   private final ArtifactFactory artifactFactory;
   private final AttributeProxyFactory proxyFactory;

   public ArtifactWriteableInvocationHandler(ArtifactFactory artifactFactory, AttributeProxyFactory proxyFactory, Artifact proxied) {
      super(proxied);
      this.artifactFactory = artifactFactory;
      this.proxyFactory = proxyFactory;
   }

   @Override
   protected Artifact createCopyForWrite(Artifact original) throws OseeCoreException {
      return artifactFactory.clone(original);
   }

   @Override
   protected Object invokeOnDelegate(Artifact target, Method method, Object[] args) throws Throwable {
      Object toReturn = super.invokeOnDelegate(target, method, args);
      if (toReturn != null) {
         toReturn = toProxy(method, toReturn);
      }
      return toReturn;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   private boolean needsProxy(Method method, Class annotation) {
      return method.getAnnotation(annotation) != null;
   }

   private Object toProxy(Method method, Object object) {
      Object toReturn = object;
      if (needsProxy(method, ReadAttributes.class)) {
         toReturn = createForRead(object);
      } else if (needsProxy(method, WriteAttributes.class)) {
         toReturn = createForWrite(object);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   private Object createForRead(Object object) {
      Object toReturn = null;
      if (object instanceof List) {
         List<AttributeReadable<?>> proxied = new ArrayList<AttributeReadable<?>>();
         List<Attribute<?>> source = ((List<Attribute<?>>) object);
         for (Attribute<?> attribute : source) {
            AttributeReadable<?> proxy = proxyFactory.createReadable(attribute);
            proxied.add(proxy);
         }
         toReturn = proxied;
      } else if (object instanceof Attribute) {
         Attribute<?> attribute = (Attribute<?>) object;
         toReturn = proxyFactory.createReadable(attribute);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   private Object createForWrite(Object object) {
      Object toReturn = null;
      if (object instanceof List) {
         List<AttributeWriteable<?>> proxied = new ArrayList<AttributeWriteable<?>>();
         List<Attribute<?>> source = ((List<Attribute<?>>) object);
         for (Attribute<?> attribute : source) {
            AttributeWriteable<?> proxy = proxyFactory.createWriteable(attribute);
            proxied.add(proxy);
         }
         toReturn = proxied;
      } else if (object instanceof Attribute) {
         Attribute<?> attribute = (Attribute<?>) object;
         toReturn = proxyFactory.createWriteable(attribute);
      }
      return toReturn;
   }
}