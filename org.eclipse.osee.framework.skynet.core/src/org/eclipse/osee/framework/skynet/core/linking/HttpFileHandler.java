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

package org.eclipse.osee.framework.skynet.core.linking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class HttpFileHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpFileHandler.class);

   private String rootPath;

   protected HttpFileHandler(String rootPath) {
      this.rootPath = rootPath;
   }

   public Pair<File, Integer> getStorageLocation(String fileName) {
      File fileReceived = null;
      int result = isFileNameValid(fileName);
      if (result == HttpURLConnection.HTTP_OK) {
         fileReceived = getFile(fileName);
         File parent = fileReceived.getParentFile();
         if (parent != null && parent.exists() == false) {
            boolean wasNewlyCreated = parent.mkdirs();
            setPermissions(parent, wasNewlyCreated);
         }
         try {
            setPermissions(fileReceived, fileReceived.createNewFile());
            result = HttpURLConnection.HTTP_CREATED;
            logger.log(Level.INFO, String.format("Upload in Progress: [%s]", fileReceived));
         } catch (IOException ex) {
            result = HttpURLConnection.HTTP_INTERNAL_ERROR;
            logger.log(Level.INFO, String.format("Unable to create file: [%s]", fileReceived));
         }
      }
      return new Pair<File, Integer>(fileReceived, new Integer(result));
   }

   public String getLocation(File file) {
      String path = file.getAbsolutePath();
      path = path.replace(new File(rootPath).getAbsolutePath(), "");
      if (path.startsWith(File.separator)) {
         path = path.substring(1, path.length());
      }
      return path;
   }

   public int receivedUpload(File file, int totalBytes, HttpRequest httpRequest) throws Exception {
      int result = HttpURLConnection.HTTP_INTERNAL_ERROR;
      FileOutputStream destination = null;
      int amountToRead = 0;
      int count = 0;
      try {
         InputStream inputStream = httpRequest.getInputStream();
         destination = new FileOutputStream(file);
         httpRequest.getSocket().setSoTimeout(120000);
         long start = System.currentTimeMillis();
         while (count < totalBytes) {
            int size = amountToRead - 1;
            if (size <= 0) {
               size = 1;
            }
            byte[] buf = new byte[size];
            count += inputStream.read(buf);
            destination.write(buf);
            //logger.log(Level.INFO, String.format("Upload Received: [%s of %s]", count, totalBytes));
         }
         long elapsed = System.currentTimeMillis() - start;
         logger.log(Level.INFO, String.format("Upload Received: [%s of %s] in [%s] ms", count, totalBytes, elapsed));
         result = HttpURLConnection.HTTP_CREATED;
      } finally {
         if (destination != null) {
            try {
               destination.close();
               if (result == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                  file.delete();
               }
            } catch (IOException ex) {
            }
         }
      }
      return result;
   }

   private File getFile(String fileName) {
      String extension = Lib.getExtension(fileName);
      String name = fileName.substring(0, (fileName.length() - 1 - extension.length()));
      StringBuilder buffer = new StringBuilder(rootPath);
      buffer.append(File.separator);
      buffer.append(name);
      buffer.append("_");
      buffer.append(new Date().getTime());
      buffer.append(".");
      buffer.append(extension);
      return new File(buffer.toString());
   }

   private void setPermissions(File file, boolean wasNewlyCreated) {
      if (!Lib.isWindows() && wasNewlyCreated) {
         Lib.chmod777(file);
      }
   }

   private int isFileNameValid(String fileName) {
      int result = HttpURLConnection.HTTP_BAD_REQUEST;
      if (Strings.isValid(fileName)) {
         String value = new File(fileName).getPath();
         if (value.contains(".." + File.separator) == false) {
            result = HttpURLConnection.HTTP_OK;
         } else {
            result = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
         }
      }
      return result;
   }

   public static void main(String[] args) {
      String rootPath = System.getProperty("user.home");
      HttpFileHandler classToTest = new HttpFileHandler(rootPath);
      boolean testResult = true;
      System.out.println("\nTEST: isFileNameValid\n");
      Map<String, Integer> fileValidTests = new HashMap<String, Integer>();

      fileValidTests.put("hello.gif", HttpURLConnection.HTTP_OK);
      fileValidTests.put("\\x\\/hello.gif", HttpURLConnection.HTTP_OK);
      fileValidTests.put("../hello.gif", HttpURLConnection.HTTP_NOT_ACCEPTABLE);
      fileValidTests.put(null, HttpURLConnection.HTTP_BAD_REQUEST);
      fileValidTests.put("", HttpURLConnection.HTTP_BAD_REQUEST);
      fileValidTests.put("hello/../dude/file.gif", HttpURLConnection.HTTP_NOT_ACCEPTABLE);

      int index = 0;
      for (String test : fileValidTests.keySet()) {
         int expected = fileValidTests.get(test);
         int actual = classToTest.isFileNameValid(test);
         boolean result = expected == actual;
         if (actual == HttpURLConnection.HTTP_OK) {
            System.out.println("File: " + classToTest.getFile(test));
         }
         testResult &= result;
         System.out.println(String.format("Test [%s]: [%s] Expected: [%s] Actual: [%s] ----> %s", ++index, test,
               expected, actual, (result ? "PASS" : "FAIL")));
      }

      ///////////////////////////////////////

      System.out.println("\nTEST: getFileExtension\n");
      Map<String, String> fileNameTests = new HashMap<String, String>();

      fileNameTests.put("hello.dude.gif", "gif");
      fileNameTests.put("hello", "");
      fileNameTests.put("hello\\dude", "");
      fileNameTests.put("_.gif", "gif");
      fileNameTests.put("", "");

      index = 0;
      for (String test : fileNameTests.keySet()) {
         String expected = fileNameTests.get(test);
         String actual = Lib.getExtension(test);
         boolean result = expected.equals(actual);
         testResult &= result;
         System.out.println(String.format("Test [%s]: [%s] Expected: [%s] Actual: [%s] ----> %s", ++index, test,
               expected, actual, (result ? "PASS" : "FAIL")));
      }

      System.out.println("Results: " + testResult);
   }
}
