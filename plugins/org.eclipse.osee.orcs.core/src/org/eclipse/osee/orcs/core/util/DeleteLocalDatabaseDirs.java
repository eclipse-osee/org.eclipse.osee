/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.util;

import java.io.File;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * This will find and delete <homedir>/attr, <homedir>/hsql and "OSEE Dev Alpha/demo" database directories. Stop all
 * clients and services first. You will be prompted with what's going to be deleted. Right click > run as > java
 * application. Console will show results and prompt.
 *
 * @author Donald G. Dunne
 */
public class DeleteLocalDatabaseDirs {

   public DeleteLocalDatabaseDirs() {
   }

   public void run() {
      String homeDir = System.getProperty("user.home");
      File homeFile = new File(homeDir);
      if (!homeFile.exists()) {
         XConsoleLogger.out("User home [%s] doesn't exist; Aborting\n", homeDir);
         return;
      } else {
         XConsoleLogger.out("Using home dir [%s]\n", homeDir);
      }
      File hsqlDir = new File(homeDir + "/hsql");
      boolean hsqlExists = false;
      if (hsqlDir.exists()) {
         hsqlExists = true;
         XConsoleLogger.out("Using found hsql dir [%s]\n", hsqlDir.getAbsolutePath());
      } else {
         XConsoleLogger.out("No hsql dir [%s]\n", hsqlDir.getAbsolutePath());
      }
      File attrDir = new File(homeDir + "/attr");
      boolean attrExists = false;
      if (attrDir.exists()) {
         attrExists = true;
         XConsoleLogger.out("Using found attr dir [%s]\n", attrDir.getAbsolutePath());
      } else {
         XConsoleLogger.out("No attr dir [%s]\n", attrDir.getAbsolutePath());
      }
      File demoDir = new File(homeDir + "/AppData/Local/OSEE Development Alpha/demo");
      boolean demoExists = false;
      if (demoDir.exists()) {
         demoExists = true;
         XConsoleLogger.out("Using found demo dir [%s]\n", demoDir.getAbsolutePath());
      } else {
         XConsoleLogger.out("No demo dir [%s]\n", demoDir.getAbsolutePath());
      }

      if (!hsqlExists && !attrExists && !demoExists) {
         XConsoleLogger.out("No dirs to delete\nComplete");
         return;
      }

      Scanner scanner = null;
      try {
         scanner = new Scanner(System.in);
         XConsoleLogger.out("Delete local hsql/attr/demo database dirs\n(Stop servers/clients first)? (y/n): ");
         String inputString = scanner.nextLine();
         if (inputString.equals("y")) {
            if (hsqlExists) {
               deleteDirectory(hsqlDir);
            }
            if (attrExists) {
               deleteDirectory(attrDir);
            }
            if (demoExists) {
               deleteDirectory(demoDir);
            }
            XConsoleLogger.out("Complete");

         } else {
            XConsoleLogger.out("Aborted");
         }
      } finally {
         if (scanner != null) {
            scanner.close();
         }
      }
   }

   boolean deleteDirectory(File dir) {
      File[] contents = dir.listFiles();
      if (contents != null) {
         for (File file : contents) {
            deleteDirectory(file);
         }
      }
      return dir.delete();
   }

   public static void main(String[] args) {

      Scanner scanner = null;
      try {
         scanner = new Scanner(System.in);
         XConsoleLogger.out(
            "Delete local hsql/attr/demo database dirs\nNOTE: STOP ALL servers and clients first\nContinue? (y/n): ");
         String inputString = scanner.nextLine();
         if (inputString.equals("y")) {
            DeleteLocalDatabaseDirs op = new DeleteLocalDatabaseDirs();
            op.run();
         }
      } finally {
         if (scanner != null) {
            scanner.close();
         }
      }

   }

}
