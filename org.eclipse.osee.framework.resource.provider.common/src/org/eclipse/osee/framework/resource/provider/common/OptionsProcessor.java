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
package org.eclipse.osee.framework.resource.provider.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.resource.common.io.Files;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.Resource;
import org.eclipse.osee.framework.resource.management.ResourceLocator;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.provider.common.resources.Resources;

/**
 * @author Andrew M. Finkbeiner
 */
public class OptionsProcessor {

   private URI fileuri;
   private URI locatoruri;
   private IResource resource;
   private String extension;
   private boolean deCompressOnSave;
   private boolean shouldCompress;
   private boolean decompressOnAcquire;
   private boolean compressOnAcquire;
   private boolean overwrite;

   /**
    * @param resource
    * @param uri
    * @param locator
    * @param options
    * @throws URISyntaxException
    */
   public OptionsProcessor(URI uri, IResourceLocator locator, IResource resource, Options options) throws URISyntaxException {
      this.resource = resource;
      decompressOnAcquire = options.getBoolean(StandardOptions.DecompressOnAquire.name());
      compressOnAcquire = options.getBoolean(StandardOptions.CompressOnAcquire.name());
      overwrite = options.getBoolean(StandardOptions.Overwrite.name());
      shouldCompress = options.getBoolean(StandardOptions.CompressOnSave.name());
      deCompressOnSave = options.getBoolean(StandardOptions.DecompressOnSave.name());
      extension = options.getString(StandardOptions.Extension.name());

      StringBuilder sb = new StringBuilder(uri.toString());
      StringBuilder sb2 = new StringBuilder(locator.toString());
      if (extension.length() > 0) {
         sb.append(".");
         sb.append(extension);
         sb2.append(".");
         sb2.append(extension);
      }
      if (shouldCompress) {
         sb.append(".");
         sb.append("zip");
         sb2.append(".");
         sb2.append("zip");
      }
      this.fileuri = new URI(sb.toString());
      this.locatoruri = new URI(sb2.toString());
   }

   /**
    * @return
    * @throws IOException
    */
   public File getStorageFile() throws IOException {
      File storageFile = new File(fileuri);
      if (!overwrite) {
         if (storageFile.exists()) {
            throw new IOException(String.format("The file [%s] already exists.", storageFile.getAbsolutePath()));
         }
      }
      File parent = storageFile.getParentFile();
      if (parent != null && !parent.exists()) {
         parent.mkdirs();
      }
      return storageFile;
   }

   /**
    * @return
    * @throws Exception
    */
   public IResource getResourceToStore() throws Exception {
      IResource resourceToReturn;
      if (shouldCompress && !resource.isCompressed()) {
         resourceToReturn = Resources.compressResource(resource);
      } else if (deCompressOnSave && resource.isCompressed()) {
         resourceToReturn = Resources.decompressResource(resource);
      } else {
         resourceToReturn = resource;
      }
      return resourceToReturn;
   }

   public IResource getResourceToServer() throws Exception {
      IResource toReturn = null;
      File testFile = new File(this.fileuri);
      if (testFile != null && testFile.exists() != false) {
         boolean isCompressed = Files.isCompressed(testFile);
         toReturn = new Resource(this.fileuri, isCompressed);

         if (compressOnAcquire && !isCompressed) {
            toReturn = Resources.compressResource(toReturn);
         } else if (decompressOnAcquire && isCompressed) {
            toReturn = Resources.decompressResource(toReturn);
         }
      }
      return toReturn;
   }

   /**
    * @return
    */
   public IResourceLocator getActualResouceLocator() {
      return new ResourceLocator(this.locatoruri);
   }

}
