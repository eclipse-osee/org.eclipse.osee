/*
 * Created on Apr 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class Resource implements IResource {
   private URI uri;
   private boolean isCompressed;

   public Resource(URI uri, boolean isCompressed) {
      this.uri = uri;
      this.isCompressed = isCompressed;
   }

   public InputStream getContent() throws IOException {
      return uri.toURL().openStream();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.resource.management.IResource#getLocation()
    */
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
