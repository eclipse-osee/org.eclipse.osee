/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.provider.common.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class CompressedResourceBridge implements IResource {
   private byte[] backing;
   private boolean isCompressed;
   private URI uri;

   public CompressedResourceBridge(byte[] backing, URI uri, boolean isCompressed) {
      this.backing = backing;
      this.isCompressed = isCompressed;
      this.uri = uri;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getContent()
    */
   @Override
   public InputStream getContent() throws IOException {
      return new ByteArrayInputStream(backing);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getLocation()
    */
   @Override
   public URI getLocation() {
      return uri;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getName()
    */
   @Override
   public String getName() {
      String value = uri.toASCIIString();
      return value.substring(value.lastIndexOf("/") + 1, value.length());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#isCompressed()
    */
   @Override
   public boolean isCompressed() {
      return isCompressed;
   }

}
