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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

/**
 * @author Donald G. Dunne
 */
public class AFile {

   public static String justFilename(String filename) {
      File file = new File(filename);
      return file.getName();
   }

   public static String justPath(String filename) {
      File file = new File(filename);
      filename = filename.replaceAll(file.getName(), "");
      return filename;
   }

   public static void writeFile(String filename, String data) {
      PrintWriter out2 = null;
      try {
         out2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
         out2.println(data);
      } catch (IOException e) {
         System.out.println("e *" + e + "*");
      } finally {
         if (out2 != null) out2.close();
      }
   }

   public static void writeFile(File file, String data) {
      writeFile(file.getAbsolutePath(), data);
   }

   public static String readFile(String filename) {
      File from_file = new File(filename);
      return readFile(from_file);
   }

   public static String readFile(File file) {
      try {
         FileInputStream fis = new FileInputStream(file);
         return readFile(fis);
      } catch (FileNotFoundException ex) {
         ex.printStackTrace();
         System.out.println("readFile ERROR: File Not Found" + ex);
         return null;
      }
   }

   public static String readFile(InputStream stream) {
      String line;
      BufferedInputStream bis = null;
      StringBuffer buffer = new StringBuffer();
      BufferedReader br = null;
      try {
         bis = new BufferedInputStream(stream);
         br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
      } catch (Throwable e) {
         System.out.println("readFile ERROR: Can't open file");
         return null;
      }
      try {
         while ((line = br.readLine()) != null) {
            buffer.append(line + "\n");
         }
         br.close();
      } catch (IOException e) {
         System.out.println("ERROR: users.xml: Can't read file");
         return null;
      }
      String orig = new String(buffer.toString());
      return orig;
   }

   public static boolean copy(String src, String dest) {
      try {
         // Create channel on the source
         FileChannel srcChannel = new FileInputStream(src).getChannel();
         // Create channel on the destination
         FileChannel dstChannel = new FileOutputStream(dest).getChannel();
         // Copy file contents from source to destination
         dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
         // Close the channels
         srcChannel.close();
         dstChannel.close();
      } catch (IOException e) {
         System.out.println("AFile:copy *" + e + "*");
         return false;
      }
      return true;
   }
}