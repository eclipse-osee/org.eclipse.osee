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

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * @author Baily Roberts
 */
public class ManifestTest {

   @Test
   public void ManifestVersionTest() {
      int pluginIndex = System.getProperty("user.dir").indexOf("plugins");
      String pluginsPath = System.getProperty("user.dir").substring(0, pluginIndex + 8);

      File pluginsRoot = new File(pluginsPath);
      File[] pluginDirs = pluginsRoot.listFiles();
      List<File> hasVersions = new ArrayList<>();
      for (File pluginDir : pluginDirs) {

         if (pluginDir.getAbsolutePath().contains("jms")) {
            continue;
         }
         File manifestDir = new File(pluginDir, "META-INF" + File.separator + "MANIFEST.MF");
         if (checkManifestForVersion(manifestDir)) {
            hasVersions.add(pluginDir);
         }
      }

      assertTrue("The Manifests for following plugins contain at least one version " + hasVersions,
         hasVersions.isEmpty());
   }

   private boolean checkManifestForVersion(File path) {
      if (!path.isFile()) {
         return false;
      }
      try (BufferedReader br = new BufferedReader(new FileReader(path))) {

         String line = br.readLine();
         while (line != null) {
            if (line.contains(";version")) {
               return true;
            }

            line = br.readLine();
         }
      } catch (IOException ex) {
         System.out.println("Exception: " + ex);
      }
      return false;
   }
}