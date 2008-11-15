/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.resource.management.servlet.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Roberto E. Escobar
 */
public class TestResourceManagerServlet extends TestCase {
   private String httpServiceURL;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      int port = OseeProperties.getOseeApplicationServerPort();
      httpServiceURL = String.format("http://localhost:%s/%s", port, OseeServerContext.RESOURCE_CONTEXT);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      this.httpServiceURL = null;
   }

   public void testServletAlive() throws Exception {
      URL url = new URL(httpServiceURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      assertNotNull(connection);
      connection.disconnect();
   }

   private String getRequest(String params) {
      StringBuilder builder = new StringBuilder();
      builder.append(httpServiceURL);
      builder.append("?");
      builder.append(params);
      return builder.toString();
   }

   public void testAcquireMalformedURL() throws IOException {
      HttpURLConnection connection = null;
      try {
         URL url = new URL(getRequest("protocol=attr&seed=1234567&name=ABCDEFGHIJK&extension=txt"));
         connection = (HttpURLConnection) url.openConnection();
         TestCase.assertNotNull(connection);
         connection.connect();

         int code = connection.getResponseCode();
         assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, code);
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   public void testAcquireFileNotFoundError() {
      HttpURLConnection connection = null;
      try {
         URL url = new URL(getRequest("uri=attr://1/4/somefile.txt.zip"));
         connection = (HttpURLConnection) url.openConnection();
         TestCase.assertNotNull(connection);
         connection.connect();

         // Wait for response
         int code = connection.getResponseCode();
         assertEquals(HttpURLConnection.HTTP_NOT_FOUND, code);
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
   }

   public void testAcquireInvalidLocator() {
   }

   public void testSaveAcquireDeleteTxt() throws Exception {
      String payload = "This is a test. Hello World!!!";
      String response =
            HttpTestUtils.sendData(getRequest("protocol=attr&seed=1234567&name=ABCDEFGHIJK&extension=txt"),
                  "text/plain", "UTF-8", new ByteArrayInputStream(payload.getBytes()));
      assertNotNull(response);
      assertEquals("Put Response", "attr://123/456/7/ABCDEFGHIJK.txt", response);

      byte[] result = HttpTestUtils.acquireData(getRequest("uri=" + response), "text/plain");
      assertEquals("Acquire Response", payload, new String(result));

      int code = HttpTestUtils.deleteData(getRequest("uri=" + response));
      assertEquals(HttpURLConnection.HTTP_ACCEPTED, code);
   }

   public void testSaveAcquireDeleteZip() throws Exception {
      String payload = "This is a test. Hello World!!!";

      byte[] compressed = HttpTestUtils.compressStream(new ByteArrayInputStream(payload.getBytes()), "ABCDEFGHIJK.txt");
      String response =
            HttpTestUtils.sendData(getRequest("protocol=attr&seed=1234567&name=ABCDEFGHIJK&extension=zip"),
                  "application/zip", "ISO-8859-1", new ByteArrayInputStream(compressed));
      assertNotNull(response);
      assertEquals("Put Response", "attr://123/456/7/ABCDEFGHIJK.zip", response);

      byte[] result = HttpTestUtils.acquireData(getRequest("uri=" + response), "application/zip");
      assertTrue("Acquire Response", Arrays.equals(compressed, result));

      int code = HttpTestUtils.deleteData(getRequest("uri=" + response));
      assertEquals(HttpURLConnection.HTTP_ACCEPTED, code);
   }

   public void testAcquireDataCompressed() throws Exception {
      String payload = "This is a test. Hello World!!!";
      String response =
            HttpTestUtils.sendData(getRequest("protocol=attr&seed=1234567&name=ABCDEFGHIJK&extension=txt"),
                  "text/plain", "UTF-8", new ByteArrayInputStream(payload.getBytes()));
      assertNotNull(response);
      assertEquals("Put Response", "attr://123/456/7/ABCDEFGHIJK.txt", response);

      byte[] result =
            HttpTestUtils.acquireData(getRequest("uri=" + response + "&compress.before.sending=true"),
                  "application/zip");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      String fileName = HttpTestUtils.decompressStream(new ByteArrayInputStream(result), output);
      assertEquals("ABCDEFGHIJK.txt", fileName);
      assertEquals("Acquire Response", payload, output.toString());

      int code = HttpTestUtils.deleteData(getRequest("uri=" + response));
      assertEquals(HttpURLConnection.HTTP_ACCEPTED, code);
   }

   public void testSaveCompressAtServer() throws Exception {
      String payload = "This is a test. Hello World!!!";
      // TODO: work here
      String response =
            HttpTestUtils.sendData(
                  getRequest("protocol=attr&seed=1234567&name=ABCDEFGHIJK&extension=txt&compress.before.saving=true"),
                  "text/plain", "UTF-8", new ByteArrayInputStream(payload.getBytes()));
      assertNotNull(response);
      assertEquals("Put Response", "attr://123/456/7/ABCDEFGHIJK.txt.zip", response);

      byte[] result = HttpTestUtils.acquireData(getRequest("uri=" + response), "application/zip");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      String fileName = HttpTestUtils.decompressStream(new ByteArrayInputStream(result), output);
      assertEquals("ABCDEFGHIJK.txt", fileName);
      assertEquals("Acquire Response", payload, output.toString());

      int code = HttpTestUtils.deleteData(getRequest("uri=" + response));
      assertEquals(HttpURLConnection.HTTP_ACCEPTED, code);
   }

}