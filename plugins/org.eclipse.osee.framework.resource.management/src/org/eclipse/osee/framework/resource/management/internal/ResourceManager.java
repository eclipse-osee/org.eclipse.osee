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
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceListener;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.InvalidLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class ResourceManager implements IResourceManager {

   private final Collection<IResourceListener> listeners;
   private final Collection<IResourceProvider> resourceProviders;

   public ResourceManager() {
      this.listeners = new CopyOnWriteArraySet<IResourceListener>();
      this.resourceProviders = new CopyOnWriteArraySet<IResourceProvider>();
   }

   @Override
   public boolean addResourceListener(IResourceListener listener) {
      return listeners.add(listener);
   }

   public boolean removeResourceListener(IResourceListener listener) {
      return listeners.remove(listener);
   }

   private void notifyPreOnDelete(final IResourceLocator locator) {
      for (IResourceListener listener : listeners) {
         listener.onPreDelete(locator);
      }
   }

   private void notifyPostOnDelete(final IResourceLocator locator) {
      for (IResourceListener listener : listeners) {
         listener.onPostDelete(locator);
      }
   }

   private void notifyPreOnSave(final IResourceLocator locator, IResource resource, Options options) {
      for (IResourceListener listener : listeners) {
         listener.onPreSave(locator, resource, options);
      }
   }

   private void notifyPostOnSave(IResourceLocator locator, final IResource resource, Options options) {
      for (IResourceListener listener : listeners) {
         listener.onPostSave(locator, resource, options);
      }
   }

   private void notifyPreOnAcquire(final IResourceLocator locator) {
      for (IResourceListener listener : listeners) {
         listener.onPreAcquire(locator);
      }
   }

   private void notifyPostOnAcquire(final IResource resource) {
      for (IResourceListener listener : listeners) {
         listener.onPostAcquire(resource);
      }
   }

   private IResourceProvider getProvider(IResourceLocator locator) throws OseeCoreException {
      IResourceProvider toReturn = null;
      for (IResourceProvider provider : resourceProviders) {
         if (provider.isValid(locator) != false) {
            toReturn = provider;
            break;
         }
      }
      if (toReturn == null) {
         throw new InvalidLocatorException(String.format("Invalid Locator: [%s]", locator));
      }
      return toReturn;
   }

   @Override
   public boolean addResourceProvider(IResourceProvider resourceProvider) {
      return resourceProviders.add(resourceProvider);
   }

   @Override
   public boolean removeResourceProvider(IResourceProvider resourceProvider) {
      return resourceProviders.remove(resourceProvider);
   }

   public IResource acquire(IResourceLocator locator, Options options) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      notifyPreOnAcquire(locator);
      IResource toReturn = provider.acquire(locator, options);
      notifyPostOnAcquire(toReturn);
      return toReturn;
   }

   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      notifyPreOnSave(locator, resource, options);
      IResourceLocator actualLocator = provider.save(locator, resource, options);
      notifyPostOnSave(locator, resource, options);
      return actualLocator;
   }

   public int delete(IResourceLocator locator) throws OseeCoreException {
      int toReturn = IResourceManager.FAIL;
      IResourceProvider provider = getProvider(locator);
      notifyPreOnDelete(locator);
      toReturn = provider.delete(locator);
      notifyPostOnDelete(locator);
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws OseeCoreException {
      IResourceProvider provider = getProvider(locator);
      return provider.exists(locator);
   }
}
