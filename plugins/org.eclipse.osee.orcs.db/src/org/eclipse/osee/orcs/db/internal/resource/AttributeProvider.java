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
import org.eclipse.osee.framework.resource.management.util.OptionsProcessor;
import org.eclipse.osee.orcs.SystemProperties;

/**
 * @author Roberto E. Escobar
 */
public class AttributeProvider implements IResourceProvider {

   private String binaryDataPath;
   private String attributeDataPath;
   private SystemProperties preferences;
   private boolean isInitialized;

   public AttributeProvider() {
      super();
      isInitialized = false;
   }

   public void setSystemProperties(SystemProperties preferences) {
      this.preferences = preferences;
   }

   public void start() {
      binaryDataPath = ResourceConstants.getBinaryDataPath(preferences);
      attributeDataPath = ResourceConstants.getAttributeDataPath(preferences);
      isInitialized = true;
   }

   public void stop() {
      binaryDataPath = null;
      attributeDataPath = null;
      isInitialized = false;
   }

   private synchronized void ensureInitialized() {
      Conditions.checkExpressionFailOnTrue(!isInitialized,
         "Osee Data Store - not initialized - ensure start() was called");
      Conditions.checkNotNull(binaryDataPath, "binary data path");
      Conditions.checkNotNull(attributeDataPath, "attribute data path");
   }

   public String getAttributeDataPath() {
      ensureInitialized();
      return attributeDataPath;
   }

   public String getBinaryDataPath() {
      ensureInitialized();
      return binaryDataPath;
   }

   private URI resolve(IResourceLocator locator) {
      StringBuilder builder = new StringBuilder(getAttributeDataPath());
      builder.append(locator.getRawPath());
      File file = new File(builder.toString());
      return file.toURI();
   }

   @Override
   public boolean isValid(IResourceLocator locator) {
      return locator != null && getSupportedProtocols().contains(locator.getProtocol());
   }

   @Override
   public int delete(IResourceLocator locator) {
      int toReturn = IResourceManager.FAIL;
      File file = new File(resolve(locator));
      if (!file.exists()) {
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
   public IResource acquire(IResourceLocator locator, PropertyStore options) {
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, null, options);
      return optionsProcessor.getResourceToServer();
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, PropertyStore options) {
      IResourceLocator toReturn = null;
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, resource, options);
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         File storageFile = optionsProcessor.getStorageFile();
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
      return Arrays.asList(ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL);
   }
}
