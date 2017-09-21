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
package org.eclipse.osee.framework.resource.management;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResourceManager implements IResourceManager {
   private static final PropertyStore DEFAULT_OPTIONS = new PropertyStore();
   private final Collection<IResourceProvider> resourceProviders;
   private final Collection<IResourceLocatorProvider> resourceLocatorProviders;

   public ResourceManager() {
      this.resourceProviders = new CopyOnWriteArraySet<>();
      this.resourceLocatorProviders = new CopyOnWriteArraySet<>();
   }

   private IResourceProvider getProvider(IResourceLocator locator) {
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
   public IResource acquire(IResourceLocator locator, PropertyStore options) {
      IResourceProvider provider = getProvider(locator);
      IResource toReturn = provider.acquire(locator, options);
      return toReturn;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) {
      IResourceProvider provider = getProvider(locator);
      IResourceLocator actualLocator = provider.save(locator, resource, options);
      return actualLocator;
   }

   @Override
   public int delete(IResourceLocator locator) {
      int toReturn = IResourceManager.FAIL;
      IResourceProvider provider = getProvider(locator);
      toReturn = provider.delete(locator);
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) {
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
   public IResourceLocator generateResourceLocator(String protocol, String seed, String name) {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(protocol);
      return resourceLocatorProvider.generateResourceLocator(seed, name);
   }

   @Override
   public IResourceLocator getResourceLocator(String path) {
      IResourceLocatorProvider resourceLocatorProvider = getProvider(path);
      return resourceLocatorProvider.getResourceLocator(path);
   }

   private IResourceLocatorProvider getProvider(String protocol) {
      for (IResourceLocatorProvider provider : resourceLocatorProviders) {
         if (provider.isValid(protocol)) {
            return provider;
         }
      }

      throw new OseeNotFoundException("No locator proivder found for [%s].  Searched providers: [%s]", protocol,
         Arrays.deepToString(resourceLocatorProviders.toArray()));
   }

   @Override
   public byte[] acquire(DataResource dataResource) {
      String path = dataResource.getLocator();
      Conditions.checkNotNull(path, "resource path");

      IResourceLocator locator = getResourceLocator(path);
      Conditions.checkNotNull(locator, "resource locator", "Unable to locate resource: [%s]", path);

      IResource resource = acquire(locator, DEFAULT_OPTIONS);
      String mimeType = getContentType(resource);

      byte[] data = null;
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         data = Lib.inputStreamToBytes(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error acquiring resource - [%s]", dataResource);
      } finally {
         Lib.close(inputStream);
      }
      String extension = Lib.getExtension(resource.getName());
      if (Strings.isValid(extension)) {
         dataResource.setExtension(extension);
      }
      dataResource.setContentType(mimeType);
      dataResource.setEncoding("ISO-8859-1");
      return data;
   }

   @Override
   public void save(long storageId, String storageName, DataResource dataResource, byte[] rawContent) {
      StringBuilder fullName = new StringBuilder();

      fullName.append(storageName);
      String extension = dataResource.getExtension();
      if (Strings.isValid(extension)) {
         fullName.append(".");
         fullName.append(extension);
      }

      String seed = String.valueOf(storageId);
      IResourceLocator locatorHint = generateResourceLocator("attr", seed, fullName.toString());

      String contentType = dataResource.getContentType();
      boolean isCompressed = Strings.isValid(contentType) && contentType.contains("zip");

      IResource resource = new ByteStreamResource(locatorHint, rawContent, isCompressed);
      IResourceLocator locator = save(locatorHint, resource, DEFAULT_OPTIONS);
      Conditions.checkNotNull(locator, "locator", "Error saving resource [%s]", locatorHint.getRawPath());

      dataResource.setLocator(locator.getLocation().toASCIIString());
   }

   @Override
   public void purge(DataResource dataResource) {
      String path = dataResource.getLocator();
      Conditions.checkNotNull(path, "resource path");

      IResourceLocator locator = getResourceLocator(path);
      Conditions.checkNotNull(locator, "resource locator", "Unable to locate resource [%s]", dataResource);

      int result = delete(locator);
      if (IResourceManager.OK != result) {
         throw new OseeDataStoreException("Error deleting resource located at [%s]", dataResource.getLocator());
      }
   }

   private static String getContentType(IResource resource) {
      String mimeType;
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         mimeType = URLConnection.guessContentTypeFromStream(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error determining mime type for - [%s]", resource.getName());
      } finally {
         Lib.close(inputStream);
      }
      if (mimeType == null) {
         mimeType = URLConnection.guessContentTypeFromName(resource.getLocation().toASCIIString());
         if (mimeType == null) {
            mimeType = "application/*";
         }
      }
      return mimeType;
   }
}