/*
 * Created on Apr 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.provider.attribute;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.ResourceLocator;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.resource.provider.attribute.internal.Utils;

/**
 * @author b1528444
 */
public class OptionsProcessor {

   private URI fileuri;
   private URI locatoruri;
   private IResource resource;
   private String extension;
   private boolean deCompressOnSave;
   private boolean shouldCompress;
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
         resourceToReturn = Utils.compressResource(resource);
      } else if (deCompressOnSave && resource.isCompressed()) {
         resourceToReturn = Utils.decompressResource(resource);
      } else {
         resourceToReturn = resource;
      }
      return resourceToReturn;
   }

   /**
    * @return
    */
   public IResourceLocator getActualResouceLocator() {
      return new ResourceLocator(this.locatoruri);
   }

}
