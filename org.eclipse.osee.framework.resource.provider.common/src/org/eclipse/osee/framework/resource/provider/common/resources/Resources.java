/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.provider.common.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import org.eclipse.osee.framework.resource.common.io.Files;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class Resources {

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
}
