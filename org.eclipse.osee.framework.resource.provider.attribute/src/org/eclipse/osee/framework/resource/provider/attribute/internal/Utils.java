/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.provider.attribute.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public class Utils {

   public static void inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[10000];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
      inputStream.close();
   }

   private static String decompressStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      String zipEntryName = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         zipEntryName = entry.getName();
         // Transfer bytes from the ZIP file to the output file
         byte[] buf = new byte[1024];
         int len;
         while ((len = zipInputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
         }
      } finally {
         if (zipInputStream != null) {
            zipInputStream.close();
         }
      }
      return zipEntryName;
   }

   private static byte[] compressStream(InputStream in, String name) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(bos);
         // Add ZIP entry to output stream.
         out.putNextEntry(new ZipEntry(name));
         byte[] buf = new byte[1024];
         int count = -1;
         while ((count = in.read(buf)) > 0) {
            out.write(buf, 0, count);
         }
      } finally {
         if (out != null) {
            out.closeEntry();
            out.close();
         }
      }
      return bos.toByteArray();
   }

   public static boolean isCompressed(File file) {
      boolean toReturn = false;
      String ext = getExtension(file.getAbsolutePath());
      if (ext.equals("zip")) {
         toReturn = true;
      }
      return toReturn;
   }

   private static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   public static String getExtension(String filepath) {
      filepath = filepath.trim();
      String separatorRegEx = File.separator;
      if (isWindows() != false) {
         separatorRegEx = "\\\\";
      }
      String[] pathsArray = filepath.split(separatorRegEx);

      String fileName = pathsArray[0];
      if (pathsArray.length > 0) {
         fileName = pathsArray[pathsArray.length - 1];
      }

      int index = fileName.lastIndexOf('.');
      if (index >= 0 && index + 1 < fileName.length()) {
         return fileName.substring(index + 1);
      } else {
         return "";
      }
   }

   private static String removeExtension(String path) {
      String ext = getExtension(path);
      if (ext != null && ext.length() > 0) {
         path = path.substring(0, path.length() - ext.length());
      }
      return path;
   }

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
         buffer = compressStream(inputStream, resource.getName());
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
         fileName = decompressStream(resource.getContent(), outputStream);
         fileName = URLEncoder.encode(fileName, "UTF-8");
      } finally {
         outputStream.close();
      }
      if (fileName != null && fileName.length() > 0) {
         path = removeName(path) + fileName;
      } else {
         path = removeExtension(path);
      }
      return new CompressedResourceBridge(outputStream.toByteArray(), new URI(path), false);
   }

   /**
    * Delete the current file and all empty parents. The method will stop deleting empty parents once it reaches the
    * stopAt parent.
    * 
    * @param stopAt path of the parent file to stop deleting at
    * @param file to delete
    * @return status <b>true</b> if successful
    */
   public static boolean deleteFileAndEmptyParents(String stopAt, File file) {
      boolean result = true;
      if (file != null) {
         if (file.isDirectory() != false) {
            if (file.list().length == 0) {
               result &= file.delete();
            }
         } else {
            result &= file.delete();
         }
      }
      File parent = file.getParentFile();
      if (parent != null && parent.getAbsolutePath().equals(stopAt) != true) {
         result &= deleteFileAndEmptyParents(stopAt, parent);
      }
      return result;
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
