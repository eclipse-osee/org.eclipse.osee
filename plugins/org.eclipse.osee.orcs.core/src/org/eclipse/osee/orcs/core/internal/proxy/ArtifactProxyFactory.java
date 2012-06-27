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
package org.eclipse.osee.orcs.core.internal.proxy;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.proxy.handler.ArtifactInvocationHandlerFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactProxyFactory extends ProxyFactory<ArtifactImpl, ArtifactReadable, ArtifactWriteable> {

   private final ArtifactFactory factory;

   public ArtifactProxyFactory(ArtifactFactory artifactFactory) {
      super(new ArtifactInvocationHandlerFactory(artifactFactory), ArtifactImpl.class, ArtifactReadable.class,
         ArtifactWriteable.class);
      this.factory = artifactFactory;
   }

   public ArtifactWriteable create(IOseeBranch branch, IArtifactType artifactType, String guid, String name) throws OseeCoreException {
      ArtifactImpl toProxy = factory.createArtifact(branch, artifactType, guid);
      if (name != null) {
         toProxy.setName(name);
      }
      return createWriteable(toProxy);
   }

   public ArtifactWriteable introduce(ArtifactReadable readable, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactImpl source = getProxiedObject(readable);
      ArtifactImpl toProxy = factory.introduceArtifact(source, ontoBranch);
      return createWriteable(toProxy);
   }

   public ArtifactWriteable copy(ArtifactReadable readable, Collection<? extends IAttributeType> types, IOseeBranch ontoBranch) throws OseeCoreException {
      ArtifactImpl source = getProxiedObject(readable);
      ArtifactImpl toProxy = factory.copyArtifact(source, types, ontoBranch);
      return createWriteable(toProxy);
   }

   public ArtifactWriteable asWriteable(ArtifactReadable readable) throws OseeCoreException {
      ArtifactWriteable toReturn = null;
      if (ProxyUtil.isProxy(readable) && readable instanceof ArtifactWriteable) {
         toReturn = (ArtifactWriteable) readable;
      } else {
         ArtifactImpl proxied = getProxiedObject(readable);
         if (proxied != null) {
            toReturn = createWriteable(proxied);
         }
      }
      if (toReturn == null) {
         throw new OseeArgumentException("Unable to convert from [%s] to Writeable",
            readable != null ? readable.getClass().getSimpleName() : "null");
      }
      return toReturn;
   }

}
