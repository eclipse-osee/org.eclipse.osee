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
package org.eclipse.osee.framework.resource.management.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Andrew M. Finkbeiner
 */
public class OptionsProcessor {

   private final URI fileuri;
   private final URI locatoruri;
   private final IResource resource;
   private final String extension;
   private final boolean deCompressOnSave;
   private final boolean shouldCompress;
   private final boolean decompressOnAcquire;
   private final boolean compressOnAcquire;
   private final boolean overwrite;

   public OptionsProcessor(URI uri, IResourceLocator locator, IResource resource, PropertyStore options) throws MalformedLocatorException {
      this.resource = resource;
      decompressOnAcquire = options.getBoolean(StandardOptions.DecompressOnAquire.name());
      compressOnAcquire = options.getBoolean(StandardOptions.CompressOnAcquire.name());
      overwrite = options.getBoolean(StandardOptions.Overwrite.name());
      shouldCompress = options.getBoolean(StandardOptions.CompressOnSave.name());
      deCompressOnSave = options.getBoolean(StandardOptions.DecompressOnSave.name());
      extension = options.get(StandardOptions.Extension.name());

      StringBuilder sb = new StringBuilder(uri.toString());
      StringBuilder sb2 = new StringBuilder(locator.toString());
      if (Strings.isValid(extension)) {
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
      try {
         this.fileuri = new URI(sb.toString());
      } catch (URISyntaxException ex) {
         throw new MalformedLocatorException(sb.toString(), ex);
      }
      try {
         this.locatoruri = new URI(sb2.toString());
      } catch (URISyntaxException ex) {
         throw new MalformedLocatorException(sb2.toString(), ex);
      }
   }

   public File getStorageFile()  {
      File storageFile = new File(fileuri);
      if (!overwrite) {
         if (storageFile.exists()) {
            throw new OseeStateException("The file [%s] already exists.", storageFile.getAbsolutePath());
         }
      }
      File parent = storageFile.getParentFile();
      if (parent != null && !parent.exists()) {
         if (!parent.mkdirs()) {
            throw new OseeCoreException("The path [%s] could not be created.  Check permissions.", parent);
         }
      }
      return storageFile;
   }

   public IResource getResourceToStore()  {
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

   public IResource getResourceToServer()  {
      IResource toReturn = null;
      File testFile = new File(this.fileuri);
      if (testFile.exists()) {
         boolean isCompressed = Lib.isCompressed(testFile);
         toReturn = new Resource(this.fileuri, isCompressed);

         if (compressOnAcquire && !isCompressed) {
            toReturn = Resources.compressResource(toReturn);
         } else if (decompressOnAcquire && isCompressed) {
            toReturn = Resources.decompressResource(toReturn);
         }
      }
      return toReturn;
   }

   public IResourceLocator getActualResouceLocator()  {
      return new ResourceLocator(this.locatoruri);
   }

}
