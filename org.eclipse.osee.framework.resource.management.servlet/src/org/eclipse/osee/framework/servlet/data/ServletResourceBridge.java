/*
 * Created on Apr 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.servlet.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public class ServletResourceBridge implements IResource {

   private HttpServletRequest request;
   private IResourceLocator locator;

   public ServletResourceBridge(HttpServletRequest request, IResourceLocator locator) {
      this.request = request;
      this.locator = locator;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResource#getContent()
    */
   @Override
   public InputStream getContent() throws IOException {
      return request.getInputStream();
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
      return HttpRequestDecoder.isDataCompressed(request);
   }

}
