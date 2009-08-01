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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

   private List<IResourceListener> listeners;
   private List<IResourceProvider> resourceProviders;

   public ResourceManager() {
      this.listeners = new CopyOnWriteArrayList<IResourceListener>();
      this.resourceProviders = new CopyOnWriteArrayList<IResourceProvider>();
   }

   public void addResourceListener(IResourceListener listener) {
      this.listeners.add(listener);
   }

   public void removeResourceListener(IResourceListener listener) {
      this.listeners.remove(listener);
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

   private IResourceProvider getProvider(IResourceLocator locator) throws InvalidLocatorException {
      IResourceProvider toReturn = null;
      for (IResourceProvider provider : resourceProviders) {
         if (provider.isValid(locator) != false) {
            toReturn = provider;
            break;
         }
      }
      if (toReturn == null) {
         throw new InvalidLocatorException();
      }
      return toReturn;
   }

   /**
    * @see org.eclipse.osee.framework.resource.management.IResourceManager#addResourceProvider(org.eclipse.osee.framework.resource.management.IResourceProvider)
    */
   @Override
   public void addResourceProvider(IResourceProvider resourceProvider) {
      resourceProviders.add(resourceProvider);
   }

   /**
    * @see org.eclipse.osee.framework.resource.management.IResourceManager#removeResourceProvider(org.eclipse.osee.framework.resource.management.IResourceProvider)
    */
   @Override
   public void removeResourceProvider(IResourceProvider resourceProvider) {
      resourceProviders.remove(resourceProvider);
   }

   public IResource acquire(IResourceLocator locator, Options options) throws Exception {
      IResourceProvider provider = getProvider(locator);
      notifyPreOnAcquire(locator);
      IResource toReturn = provider.acquire(locator, options);
      notifyPostOnAcquire(toReturn);
      return toReturn;
   }

   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws Exception {
      IResourceProvider provider = getProvider(locator);
      notifyPreOnSave(locator, resource, options);
      IResourceLocator actualLocator = provider.save(locator, resource, options);
      notifyPostOnSave(locator, resource, options);
      return actualLocator;
   }

   public int delete(IResourceLocator locator) throws Exception {
      int toReturn = IResourceManager.FAIL;
      IResourceProvider provider = getProvider(locator);
      notifyPreOnDelete(locator);
      toReturn = provider.delete(locator);
      notifyPostOnDelete(locator);
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws Exception {
      IResourceProvider provider = getProvider(locator);
      return provider.exists(locator);
   }
}
