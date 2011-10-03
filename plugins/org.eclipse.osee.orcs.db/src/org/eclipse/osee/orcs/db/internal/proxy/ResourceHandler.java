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
import java.net.URLConnection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Roberto E. Escobar
 */
public class ResourceHandler implements DataHandler {
   private static final PropertyStore DEFAULT_OPTIONS = new PropertyStore();

   private final IResourceManager resourceManager;
   private final IResourceLocatorManager resourceLocator;

   public ResourceHandler(IResourceManager resourceManager, IResourceLocatorManager resourceLocator) {
      super();
      this.resourceManager = resourceManager;
      this.resourceLocator = resourceLocator;
   }

   @Override
   public byte[] acquire(DataResource dataResource) throws OseeCoreException {
      String path = dataResource.getLocator();
      IResourceLocator locator = resourceLocator.getResourceLocator(path);
      IResource resource = resourceManager.acquire(locator, DEFAULT_OPTIONS);

      dataResource.setEncoding("ISO-8859-1");

      InputStream inputStream = resource.getContent();
      try {
         String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
         if (mimeType == null) {
            mimeType = URLConnection.guessContentTypeFromName(resource.getLocation().toString());
            if (mimeType == null) {
               mimeType = "application/*";
            }
         }
         dataResource.setContentType(mimeType);

         return Lib.inputStreamToBytes(inputStream);
      } catch (IOException ex) {
         throw OseeExceptions.wrap(ex);
      } finally {
         Lib.close(inputStream);
      }
   }

   @Override
   public void save(int storageId, DataResource dataResource, byte[] rawContent) throws OseeCoreException {
      //      Map<String, String> parameterMap = new HashMap<String, String>();
      //      parameterMap.put("seed", Integer.toString(gammaId));
      //      parameterMap.put("name", artifactGuid);
      //      if (Strings.isValid(extension) != false) {
      //         parameterMap.put("extension", extension);
      //      }

      int gammaId = -1;

      String name = "";
      String seed = Integer.toString(gammaId);
      IResourceLocator locatorHint = resourceLocator.generateResourceLocator("attr", seed, name);
      IResource resource = null;
      IResourceLocator locator = resourceManager.save(locatorHint, resource, DEFAULT_OPTIONS);
      dataResource.setLocator(locator.getLocation().toASCIIString());
   }

   @Override
   public void purge(DataResource dataResource) throws OseeCoreException {
      IResourceLocator locator = resourceLocator.getResourceLocator(dataResource.getLocator());
      resourceManager.delete(locator);
   }

   //   public static PropertyStore getOptions(HttpServletRequest request) {
   //      PropertyStore options = new PropertyStore();
   //      options.put(StandardOptions.CompressOnSave.name(), request.getParameter(COMPRESS_ON_SAVE));
   //      options.put(StandardOptions.CompressOnAcquire.name(), request.getParameter(COMPRESS_ON_ACQUIRE));
   //      options.put(StandardOptions.DecompressOnAquire.name(), request.getParameter(DECOMPRESS_ON_ACQUIRE));
   //      options.put(StandardOptions.Overwrite.name(), request.getParameter(IS_OVERWRITE_ALLOWED));
   //      return options;
   //   }

   //
   //   public void acquire(DataStore dataStore) throws OseeCoreException {
   //      URL url = getAcquireURL(dataStore);
   //      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
   //      try {
   //         AcquireResult result = HttpProcessor.acquire(url, outputStream);
   //         int code = result.getCode();
   //         if (code == HttpURLConnection.HTTP_OK) {
   //            dataStore.setContent(outputStream.toByteArray(), "", result.getContentType(), result.getEncoding());
   //         } else {
   //            throw new OseeDataStoreException("Error acquiring resource: [%s] - status code: [%s]; %s",
   //               dataStore.getLocator(), code, new String(outputStream.toByteArray()));
   //         }
   //      } catch (Exception ex) {
   //         OseeExceptions.wrapAndThrow(ex);
   //      }
   //   }

}
