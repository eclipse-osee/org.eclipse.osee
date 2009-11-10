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
package org.eclipse.osee.framework.resource.management.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class ResourceLocatorManager implements IResourceLocatorManager {

   private final Collection<IResourceLocatorProvider> resourceLocatorProviders;

   public ResourceLocatorManager() {
      this.resourceLocatorProviders = new CopyOnWriteArraySet<IResourceLocatorProvider>();
   }

   @Override
   public Collection<String> getProtocols() {
      Set<String> protocols = new HashSet<String>();
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         protocols.add(provider.getSupportedProtocol());
      }
      return protocols;
   }

   @Override
   public boolean addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      return this.resourceLocatorProviders.add(resourceLocatorProvider);
   }

   @Override
   public boolean removeResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      return this.resourceLocatorProviders.remove(resourceLocatorProvider);
   }

   @Override
   public IResourceLocator generateResourceLocator(String protocol, String seed, String name) throws OseeCoreException {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(protocol);
      return resourceLocatorProvider.generateResourceLocator(seed, name);
   }

   @Override
   public IResourceLocator getResourceLocator(String path) throws OseeCoreException {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(path);
      return resourceLocatorProvider.getResourceLocator(path);
   }

   private IResourceLocatorProvider getProvider(String protocol) throws OseeCoreException {
      if (resourceLocatorProviders.isEmpty()) {
         throw new OseeStateException("Resource locator providers are not available");
      }
      IResourceLocatorProvider toReturn = null;
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         if (provider.isValid(protocol)) {
            toReturn = provider;
            break;
         }
      }
      if (toReturn == null) {
         throw new MalformedLocatorException(String.format("Error finding locator for [%s] in [%s]", protocol,
               resourceLocatorProviders));
      }
      return toReturn;
   }
}
