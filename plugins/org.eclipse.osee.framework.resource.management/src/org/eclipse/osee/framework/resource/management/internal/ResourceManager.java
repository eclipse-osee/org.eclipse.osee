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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ResourceManager implements IResourceManager {

   private final Collection<IResourceProvider> resourceProviders;
   private final Collection<IResourceLocatorProvider> resourceLocatorProviders;

   public ResourceManager() {
      this.resourceProviders = new CopyOnWriteArraySet<>();
      this.resourceLocatorProviders = new CopyOnWriteArraySet<>();
   }

   private IResourceProvider getProvider(IResourceLocator locator) throws OseeCoreException {
      for (IResourceProvider provider : resourceProviders) {
         if (provider.isValid(locator)) {
            return provider;
         }
      }

      throw new OseeNotFoundException("No resource provider found for Locator: [%s].  Searched providers: [%s]",
         locator, Arrays.deepToString(resourceProviders.toArray()));
   }

   public boolean addResourceProvider(IResourceProvider resourceProvider) {
      return resourceProviders.add(resourceProvider);
   }

   public boolean removeResourceProvider(IResourceProvider resourceProvider) {
      return resourceProviders.remove(resourceProvider);
   }

   @Override
   public IResource acquire(IResourceLocator locator, PropertyStore options) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      IResource toReturn = provider.acquire(locator, options);
      return toReturn;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      IResourceLocator actualLocator = provider.save(locator, resource, options);
      return actualLocator;
   }

   @Override
   public int delete(IResourceLocator locator) throws OseeCoreException {
      int toReturn = IResourceManager.FAIL;
      IResourceProvider provider = getProvider(locator);
      toReturn = provider.delete(locator);
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      return provider.exists(locator);
   }

   @Override
   public Collection<String> getProtocols() {
      Set<String> protocols = new HashSet<>();
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         protocols.add(provider.getSupportedProtocol());
      }
      return protocols;
   }

   public boolean addResourceLocatorProvider(IResourceLocatorProvider resourceLocatorProvider) {
      return this.resourceLocatorProviders.add(resourceLocatorProvider);
   }

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
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         if (provider.isValid(protocol)) {
            return provider;
         }
      }

      throw new OseeNotFoundException("No locator proivder found for [%s].  Searched providers: [%s]", protocol,
         Arrays.deepToString(resourceLocatorProviders.toArray()));
   }
}
