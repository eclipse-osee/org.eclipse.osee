/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.locator.snapshot;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorProvider;
import org.eclipse.osee.framework.resource.management.ResourceLocator;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
public class SnapshotLocatorProvider implements IResourceLocatorProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorProvider#generateResourceLocator(java.lang.String)
    */
   @Override
   public IResourceLocator generateResourceLocator(String seed, String name) throws MalformedLocatorException {
      URI uri = null;
      try {
         uri = new URI(generatePath(seed, name));
      } catch (Exception ex) {
         throw new MalformedLocatorException(ex);
      }
      return new ResourceLocator(uri);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorProvider#getResourceLocator(java.lang.String)
    */
   @Override
   public IResourceLocator getResourceLocator(String path) throws MalformedLocatorException {
      URI uri = null;
      if (isPathValid(path) != false) {
         try {
            uri = new URI(path);
         } catch (Exception ex) {
            throw new MalformedLocatorException(ex);
         }
      } else {
         throw new MalformedLocatorException(String.format("Invalid path hint: [%s]", path));
      }
      return new ResourceLocator(uri);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.resource.management.IResourceLocatorProvider#isValid(java.lang.String)
    */
   @Override
   public boolean isValid(String protocol) {
      return isArgValid(protocol) != false && protocol.startsWith("snapshot") != false;
   }

   private boolean isArgValid(String value) {
      return value != null && value.length() > 0;
   }

   private boolean isPathValid(String value) {
      return isArgValid(value) && value.startsWith("snapshot://");
   }

   private String generatePath(String seed, String name) throws MalformedLocatorException {
      StringBuilder builder = new StringBuilder("snapshot://");
      if (isArgValid(seed) != false && isArgValid(name) != false) {
         try {
            String[] values = seed.split("BRANCH");
            String struct = values[0];
            char[] buffer = new char[3];
            int cnt = -1;
            Reader in = new StringReader(struct);
            while ((cnt = in.read(buffer)) != -1) {
               builder.append(buffer, 0, cnt);
               builder.append("/");
            }

            if (values.length == 2) {
               builder.append(values[1]);
               builder.append("/");
            }
         } catch (IOException ex) {
            throw new MalformedLocatorException(ex);
         }
         builder.append(name);
      } else {
         throw new MalformedLocatorException("Invalid arguments during locator generation.");
      }
      return builder.toString();
   }
}
