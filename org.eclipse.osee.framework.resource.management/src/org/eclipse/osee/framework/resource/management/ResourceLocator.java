/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management;

import java.net.URI;

/**
 * @author Roberto E. Escobar
 */
public class ResourceLocator implements IResourceLocator {

   private URI uri;

   public ResourceLocator(URI uri) {
      if (uri == null) {
         throw new IllegalArgumentException("URI was null.");
      }
      this.uri = uri;
   }

   public URI getLocation() {
      return uri;
   }

   public String getProtocol() {
      return uri.getScheme();
   }

   public String toString() {
      return uri.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocator#getRawPath()
    */
   @Override
   public String getRawPath() {
      String toReturn = uri.getSchemeSpecificPart();
      if (toReturn.startsWith("//") != false) {
         toReturn = toReturn.substring(2, toReturn.length());
      }
      return toReturn;
   }
}
