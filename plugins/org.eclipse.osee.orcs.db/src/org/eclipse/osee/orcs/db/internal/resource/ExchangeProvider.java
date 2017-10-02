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
package org.eclipse.osee.orcs.db.internal.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.resource.management.util.OptionsProcessor;
import org.eclipse.osee.orcs.SystemPreferences;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeProvider implements IResourceProvider {

   private String binaryDataPath;
   private String exchangeDataPath;
   private SystemPreferences preferences;
   private boolean isInitialized;

   public ExchangeProvider() {
      super();
      isInitialized = false;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void start() {
      binaryDataPath = ResourceConstants.getBinaryDataPath(preferences);
      exchangeDataPath = ResourceConstants.getExchangeDataPath(preferences);
      isInitialized = true;
   }

   public void stop() {
      binaryDataPath = null;
      exchangeDataPath = null;
      isInitialized = false;
   }

   private synchronized void ensureInitialized() {
      Conditions.checkExpressionFailOnTrue(!isInitialized,
         "Exchange Data Path - not initialized - ensure start() was called");
      Conditions.checkNotNull(binaryDataPath, "binary data path");
      Conditions.checkNotNull(exchangeDataPath, "exchange data path");
   }

   public String getExchangeDataPath() {
      ensureInitialized();
      return exchangeDataPath;
   }

   public String getBinaryDataPath() {
      ensureInitialized();
      return binaryDataPath;
   }

   private URI resolve(IResourceLocator locator) {
      URI toReturn = null;
      StringBuilder builder = new StringBuilder();
      String rawPath = locator.getRawPath();
      if (!rawPath.startsWith("file:/")) {
         builder.append(getExchangeDataPath());
         builder.append(rawPath);
         File file = new File(builder.toString());
         toReturn = file.toURI();
      } else {
         rawPath = rawPath.replaceAll(" ", "%20");
         try {
            toReturn = new URI(rawPath);
         } catch (URISyntaxException ex) {
            throw new MalformedLocatorException(ex);
         }
      }
      return toReturn;
   }

   @Override
   public IResource acquire(IResourceLocator locator, PropertyStore options) {
      IResource toReturn = null;
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, null, options);
      toReturn = optionsProcessor.getResourceToServer();
      return toReturn;
   }

   @Override
   public int delete(IResourceLocator locator) {
      int toReturn = IResourceManager.FAIL;
      File file = new File(resolve(locator));
      if (file.exists() != true) {
         toReturn = IResourceManager.RESOURCE_NOT_FOUND;
      } else if (file.exists() == true && file.canWrite() == true) {
         boolean result = Lib.deleteFileAndEmptyParents(getBinaryDataPath(), file);
         if (result) {
            toReturn = IResourceManager.OK;
         }
      }
      return toReturn;
   }

   @Override
   public boolean isValid(IResourceLocator locator) {
      return locator != null && getSupportedProtocols().contains(locator.getProtocol());
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) {
      IResourceLocator toReturn = null;
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, resource, options);
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         File storageFile = optionsProcessor.getStorageFile();
         // Remove all other files from this folder
         File parent = storageFile.getParentFile();
         if (parent != null) {
            Lib.emptyDirectory(parent);
         }
         IResource resourceToStore = optionsProcessor.getResourceToStore();

         outputStream = new FileOutputStream(storageFile);
         inputStream = resourceToStore.getContent();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
         toReturn = optionsProcessor.getActualResouceLocator();
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(outputStream);
         Lib.close(inputStream);
      }
      if (toReturn == null) {
         throw new OseeStateException("We failed to save resource %s.", locator.getLocation());
      }
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) {
      URI uri = resolve(locator);
      File testFile = new File(uri);
      return testFile.exists();
   }

   @Override
   public Collection<String> getSupportedProtocols() {
      return Arrays.asList(ResourceConstants.EXCHANGE_RESOURCE_PROTOCOL);
   }
}
