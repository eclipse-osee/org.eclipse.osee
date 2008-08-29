/*
 * Created on Aug 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public final class ZipBinaryResource implements IResource {

   private IResourceLocator locator;
   private ZipEntry entry;
   private ZipFile zipFile;

   public ZipBinaryResource(ZipFile zipFile, ZipEntry entry, IResourceLocator locator) {
      checkNotNull("ZipFile", zipFile);
      checkNotNull("ZipEntry", entry);
      checkNotNull("IResourceLocator", locator);
      this.entry = entry;
      this.locator = locator;
      this.zipFile = zipFile;
   }

   private void checkNotNull(String argName, Object object) {
      if (object == null) {
         throw new IllegalArgumentException(String.format("Argument was null - [%s]", argName));
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getContent()
    */
   @Override
   public InputStream getContent() throws IOException {
      return zipFile.getInputStream(entry);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getLocation()
    */
   @Override
   public URI getLocation() {
      return locator.getLocation();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getName()
    */
   @Override
   public String getName() {
      String path = locator.getLocation().toASCIIString();
      int index = path.lastIndexOf("/");
      if (index != -1 && index + 1 < path.length()) {
         path = path.substring(index + 1, path.length());
      }
      return path;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#isCompressed()
    */
   @Override
   public boolean isCompressed() {
      return Lib.getExtension(entry.toString()).equals("zip");
   }
}
