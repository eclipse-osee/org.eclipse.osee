/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.provider.attribute.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import org.eclipse.osee.framework.resource.common.io.Files;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class Utils {

   private static String removeName(String path) {
      int index = path.lastIndexOf("/");
      if (index != -1) {
         path = path.substring(0, index + 1);
      }
      return path;
   }

   public static IResource compressResource(IResource resource) throws Exception {
      InputStream inputStream = null;
      byte[] buffer = new byte[0];
      try {
         inputStream = resource.getContent();
         buffer = Streams.compressStream(inputStream, resource.getName());
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return new CompressedResourceBridge(buffer, new URI(resource.getLocation() + ".zip"), true);
   }

   public static IResource decompressResource(IResource resource) throws Exception {
      String path = resource.getLocation().toASCIIString();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      String fileName = null;
      try {
         fileName = Streams.decompressStream(resource.getContent(), outputStream);
         fileName = URLEncoder.encode(fileName, "UTF-8");
      } finally {
         outputStream.close();
      }
      if (fileName != null && fileName.length() > 0) {
         path = removeName(path) + fileName;
      } else {
         path = Files.removeExtension(path);
      }
      return new CompressedResourceBridge(outputStream.toByteArray(), new URI(path), false);
   }

   private final static class CompressedResourceBridge implements IResource {
      private byte[] backing;
      private boolean isCompressed;
      private URI uri;

      private CompressedResourceBridge(byte[] backing, URI uri, boolean isCompressed) {
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
}
