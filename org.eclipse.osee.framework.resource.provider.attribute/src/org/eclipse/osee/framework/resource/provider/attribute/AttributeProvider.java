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
package org.eclipse.osee.framework.resource.provider.attribute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.provider.common.OptionsProcessor;

/**
 * @author Roberto E. Escobar
 */
public class AttributeProvider implements IResourceProvider {
   private static final String SUPPORTED_PROTOCOL = "attr";
   private static String BASE_PATH = null;

   public AttributeProvider() {
      BASE_PATH = OseeServerProperties.getOseeApplicationServerData();
   }

   public boolean isValid(IResourceLocator locator) {
      return locator != null && locator.getProtocol().equals(SUPPORTED_PROTOCOL);
   }

   public int delete(IResourceLocator locator) throws Exception {
      int toReturn = IResourceManager.FAIL;
      File file = new File(resolve(locator));
      if (file == null || file.exists() != true) {
         toReturn = IResourceManager.RESOURCE_NOT_FOUND;
      } else if (file.exists() == true && file.canWrite() == true) {
         boolean result = Lib.deleteFileAndEmptyParents(BASE_PATH, file);
         if (result) {
            toReturn = IResourceManager.OK;
         }
      }
      return toReturn;
   }

   private URI resolve(IResourceLocator locator) throws URISyntaxException {
      StringBuilder builder = new StringBuilder(BASE_PATH + File.separator + SUPPORTED_PROTOCOL + File.separator);
      builder.append(locator.getRawPath());
      return new File(builder.toString()).toURI();
   }

   @Override
   public IResource acquire(IResourceLocator locator, Options options) throws Exception {
      IResource toReturn = null;
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, null, options);
      toReturn = optionsProcessor.getResourceToServer();
      return toReturn;
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws Exception {
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
      } finally {
         if (outputStream != null) {
            outputStream.close();
         }
         if (inputStream != null) {
            inputStream.close();
         }
      }
      if (toReturn == null) {
         throw new IllegalStateException(String.format("We failed to save resource %s.", locator.getLocation()));
      }
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws Exception {
      URI uri = resolve(locator);
      File testFile = new File(uri);
      return testFile.exists();
   }
}
