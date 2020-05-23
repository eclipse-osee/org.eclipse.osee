/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.utility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * Convert all OSEE Types remove guid .java and .osee files. Change directories below to point to your workspace/git
 * before running.
 *
 * @author Donald G. Dunne
 */
public class ConvertOseeTypesToRemoveGuid {

   public static void main(String[] args) {
      try {
         for (String dirNam : Arrays.asList("C:\\UserData\\git_fix\\org.eclipse.osee\\plugins\\",
            "C:\\UserData\\git_fix\\lba.osee\\plugins\\")) {
            File dir1 = new File(dirNam);
            for (String filename : Lib.readListFromDir(dir1, new MatchFilter(".*"), true)) {
               if (!Strings.isValid(filename)) {
                  continue;
               }
               System.out.println(String.format("Processing [%s]", filename));
               File file = new File(dir1 + "\\" + filename);
               try {
                  recurseAndFind(file);
               } catch (Exception ex) {
                  XConsoleLogger.err(ex.getLocalizedMessage());
               }
            }
         }
      } catch (Exception ex) {
         System.out.println(ex.getLocalizedMessage());
      }
   }

   private static void recurseAndFind(File file) throws IOException {
      try {
         if (file.isDirectory()) {
            for (String filename : Lib.readListFromDir(file, new MatchFilter(".*"), true)) {
               File childFile = new File(file.getAbsolutePath() + "\\" + filename);
               try {
                  recurseAndFind(childFile);
               } catch (Exception ex) {
                  XConsoleLogger.err(ex.getLocalizedMessage());
               }
            }
         }
      } catch (Exception ex) {
         XConsoleLogger.err(ex.getLocalizedMessage());
      }
      if (file.getAbsolutePath().endsWith(".osee")) {
         XConsoleLogger.err("File " + file.getName());
         String text = Lib.fileToString(file);
         text = text.replaceAll("guid +[0-9A-Za-z\\+_=]{20,22}", "");
         Lib.writeStringToFile(text, file);
      }
   }

}
