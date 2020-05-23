/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.util;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;

/*
 * Created on May 5, 2020
 *
 * @author Baily Roberts
 */

public class ManifestTest {

   @Test
   public void ManifestVersionTest() {
      boolean hasVersion = false;
      int pluginIndex = System.getProperty("user.dir").indexOf("plugins");
      String pluginsPath = System.getProperty("user.dir").substring(0, pluginIndex + 8);

      File pluginsDir = new File(pluginsPath);
      File[] fileDir = pluginsDir.listFiles();
      File manifestDir;
      for (int i = 0; i < fileDir.length; i++) {
         if (hasVersion == true) {
            continue;
         }
         if (fileDir[i].getAbsolutePath().contains("jms")) {
            continue;
         }
         manifestDir = new File(fileDir[i] + "/META-INF/" + "MANIFEST.MF");
         hasVersion = checkManifestForVersion(manifestDir);
      }

      assertEquals("A Manifest file had version added. Please remove.", hasVersion, false);
   }

   private boolean checkManifestForVersion(File path) {

      if (path.isFile() == false) {
         return false;
      }
      try {
         BufferedReader br = new BufferedReader(new FileReader(path));
         String line = br.readLine();
         while (line != null) {
            if (line.contains(";version")) {
               return true;
            }

            line = br.readLine();
         }
         br.close();
      } catch (IOException ex) {
         System.out.println("Exception: " + ex);
      }
      return false;
   }
}
