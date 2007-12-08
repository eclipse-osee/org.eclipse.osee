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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class MultipleLinks {

   public static void main(String[] args) throws IOException {
      if (args.length < 3) {
         System.out.println("Usage: java tc.MultipleLinks <file to link> <directory for links> <# of links>");
         return;
      }
      link(new File(args[0]), new File(args[1]), Integer.parseInt(args[2]));
   }

   public static void link(File fileToLink, File directory, int linkCount) throws IOException {
      if (!fileToLink.isFile()) {
         throw new IllegalArgumentException(fileToLink + " is not a file.");
      }

      fileToLink = fileToLink.getCanonicalFile();
      directory = directory.getCanonicalFile();
      if (directory.mkdir()) {
         System.out.println("Created " + directory);
      }

      //separate exstension and file name
      String fileName = fileToLink.getName();
      String extension = "";
      int pos = fileName.lastIndexOf('.');
      if (pos != -1) {
         extension = fileName.substring(pos);
      }
      fileName = Lib.stripExtension(fileToLink.getName());

      String command = "ln -s " + fileToLink.getPath() + " " + directory.getPath() + File.separator + fileName;
      for (int i = 0; i < linkCount; i++) {
         Lib.handleProcess(Runtime.getRuntime().exec(command + i + extension));
      }
   }
}