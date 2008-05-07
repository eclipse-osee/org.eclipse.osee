/*
 * Created on Apr 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.servlet.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import junit.framework.TestCase;
import org.eclipse.osee.framework.resource.common.io.Streams;

/**
 * @author Roberto E. Escobar
 */
public class HttpTestUtils {

   private HttpTestUtils() {
   }

   public static String decompressStream(InputStream inputStream, OutputStream outputStream) throws IOException {
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

   public static byte[] compressStream(InputStream in, String name) throws IOException {
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

   public static String sendData(String request, String contentType, String encoding, InputStream payload) throws Exception {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      OutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         URL url = new URL(request);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         TestCase.assertNotNull(connection);

         connection.setRequestProperty("Content-Length", Integer.toString(payload.available()));
         connection.setRequestProperty("Content-Type", contentType);
         connection.setRequestProperty("Content-Encoding", encoding);
         connection.setRequestMethod("PUT");
         connection.setAllowUserInteraction(true);
         connection.setDoOutput(true);
         connection.setDoInput(true);
         connection.connect();

         outputStream = connection.getOutputStream();
         TestCase.assertNotNull(outputStream);

         Streams.inputStreamToOutputStream(payload, outputStream);

         // Wait for response
         int code = connection.getResponseCode();
         TestCase.assertEquals(HttpURLConnection.HTTP_CREATED, code);
         TestCase.assertTrue(connection.getContentType().contains("text/plain"));

         inputStream = (InputStream) connection.getContent();
         Streams.inputStreamToOutputStream(inputStream, output);
         TestCase.assertTrue("Got Data", output.size() > 0);
      } finally {
         if (outputStream != null) {
            outputStream.close();
         }
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return output.toString();
   }

   public static byte[] acquireData(String request, String contentType) throws Exception {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      InputStream inputStream = null;
      try {
         URL url = new URL(request);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         TestCase.assertNotNull(connection);
         connection.connect();

         // Wait for response
         int code = connection.getResponseCode();
         TestCase.assertEquals(HttpURLConnection.HTTP_OK, code);
         TestCase.assertTrue(connection.getContentType().contains(contentType));
         inputStream = (InputStream) connection.getContent();
         Streams.inputStreamToOutputStream(inputStream, output);
         TestCase.assertTrue("Got Data", output.size() > 0);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return output.toByteArray();
   }

   public static int deleteData(String request) throws Exception {
      int response = -1;
      InputStream inputStream = null;
      try {
         URL url = new URL(request);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         TestCase.assertNotNull(connection);

         connection.setRequestMethod("DELETE");
         connection.setDoOutput(true);
         connection.setDoInput(true);
         connection.connect();

         // Wait for response
         response = connection.getResponseCode();
         TestCase.assertEquals(HttpURLConnection.HTTP_ACCEPTED, response);
         TestCase.assertTrue(connection.getContentType().contains("text/plain"));
         inputStream = (InputStream) connection.getContent();
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         String status = reader.readLine();
         TestCase.assertNotNull("Deleted: " + request.replace("uri=", ""), status);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return response;
   }
}
