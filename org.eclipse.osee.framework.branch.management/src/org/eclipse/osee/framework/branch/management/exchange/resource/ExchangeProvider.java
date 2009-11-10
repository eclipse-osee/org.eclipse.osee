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
package org.eclipse.osee.framework.branch.management.exchange.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.IResourceProvider;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.resource.management.util.OptionsProcessor;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeProvider implements IResourceProvider {
   private static final String BASE_PATH = OseeServerProperties.getOseeApplicationServerData();
   private static final String RESOLVED_PATH =
         BASE_PATH + File.separator + ExchangeLocatorProvider.PROTOCOL + File.separator;

   public ExchangeProvider() {
   }

   public static String getExchangeFilePath() {
      return RESOLVED_PATH;
   }

   private URI resolve(IResourceLocator locator) throws OseeCoreException {
      URI toReturn = null;
      StringBuilder builder = new StringBuilder();
      String rawPath = locator.getRawPath();
      if (!rawPath.startsWith("file:/")) {
         builder.append(RESOLVED_PATH);
         builder.append(rawPath);
         toReturn = new File(builder.toString()).toURI();
      } else {
         rawPath = rawPath.replaceAll(" ", "%20");
         try {
            toReturn = new URI(rawPath);
         } catch (URISyntaxException ex) {
            throw new MalformedLocatorException(rawPath, ex);
         }
      }
      return toReturn;
   }

   @Override
   public IResource acquire(IResourceLocator locator, Options options) throws OseeCoreException {
      IResource toReturn = null;
      OptionsProcessor optionsProcessor = new OptionsProcessor(resolve(locator), locator, null, options);
      toReturn = optionsProcessor.getResourceToServer();
      return toReturn;
   }

   @Override
   public int delete(IResourceLocator locator) throws OseeCoreException {
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

   @Override
   public boolean isValid(IResourceLocator locator) {
      return locator != null && getSupportedProtocols().contains(locator.getProtocol());
   }

   @Override
   public IResourceLocator save(IResourceLocator locator, IResource resource, Options options) throws OseeCoreException {
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
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(outputStream);
         Lib.close(inputStream);
      }
      if (toReturn == null) {
         throw new OseeStateException(String.format("We failed to save resource %s.", locator.getLocation()));
      }
      return toReturn;
   }

   @Override
   public boolean exists(IResourceLocator locator) throws OseeCoreException {
      URI uri = resolve(locator);
      File testFile = new File(uri);
      return testFile.exists();
   }

   @Override
   public Collection<String> getSupportedProtocols() {
      return Arrays.asList(ExchangeLocatorProvider.PROTOCOL);
   }
}
