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
package org.eclipse.osee.orcs.db.internal.proxy;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.db.internal.util.BinaryContentUtils;
import org.eclipse.osee.orcs.db.internal.util.ByteStreamResource;

/**
 * @author Roberto E. Escobar
 */
public class ResourceHandler implements DataHandler {
   private static final PropertyStore DEFAULT_OPTIONS = new PropertyStore();

   private final IResourceManager resourceManager;

   public ResourceHandler(IResourceManager resourceManager) {
      super();
      this.resourceManager = resourceManager;
   }

   @Override
   public byte[] acquire(DataResource dataResource) throws OseeCoreException {
      String path = dataResource.getLocator();
      Conditions.checkNotNull(path, "resource path");

      IResourceLocator locator = resourceManager.getResourceLocator(path);
      Conditions.checkNotNull(locator, "resource locator", "Unable to locate resource: [%s]", path);

      IResource resource = resourceManager.acquire(locator, DEFAULT_OPTIONS);
      String mimeType = BinaryContentUtils.getContentType(resource);

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
   public void save(long storageId, DataResource dataResource, byte[] rawContent) throws OseeCoreException {
      StringBuilder storageName = new StringBuilder();

      ResourceNameResolver resolver = dataResource.getResolver();
      Conditions.checkNotNull(resolver, "resource name resolver");
      storageName.append(resolver.getStorageName());
      String extension = dataResource.getExtension();
      if (Strings.isValid(extension)) {
         storageName.append(".");
         storageName.append(extension);
      }

      String seed = String.valueOf(storageId);
      IResourceLocator locatorHint = resourceManager.generateResourceLocator("attr", seed, storageName.toString());

      String contentType = dataResource.getContentType();
      boolean isCompressed = Strings.isValid(contentType) && contentType.contains("zip");

      IResource resource = new ByteStreamResource(locatorHint, rawContent, isCompressed);
      IResourceLocator locator = resourceManager.save(locatorHint, resource, DEFAULT_OPTIONS);
      Conditions.checkNotNull(locator, "locator", "Error saving resource [%s]", locatorHint.getRawPath());

      dataResource.setLocator(locator.getLocation().toASCIIString());
   }

   @Override
   public void delete(DataResource dataResource) throws OseeCoreException {
      String path = dataResource.getLocator();
      Conditions.checkNotNull(path, "resource path");

      IResourceLocator locator = resourceManager.getResourceLocator(path);
      Conditions.checkNotNull(locator, "resource locator", "Unable to locate resource [%s]", dataResource);

      int result = resourceManager.delete(locator);
      if (IResourceManager.OK != result) {
         throw new OseeDataStoreException("Error deleting resource located at [%s]", dataResource.getLocator());
      }
   }
}
