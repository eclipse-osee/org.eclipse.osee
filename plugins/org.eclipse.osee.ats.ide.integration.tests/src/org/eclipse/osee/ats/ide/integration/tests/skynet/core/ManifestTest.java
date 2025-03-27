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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.junit.Test;

/**
 * Test that manifest files do not have bundle version specified.<br/>
 * <br/>
 * NOTE: Test had to be duplicated to multiple integration suites. See other MaifestTest if changes are needed.
 *
 * @author Baily Roberts
 * @author Donald G. Dunne
 */

public class ManifestTest {

   @Test
   public void ManifestVersionTest() {
      int pluginIndex = System.getProperty("user.dir").indexOf("plugins");
      String pluginsPath = System.getProperty("user.dir").substring(0, pluginIndex + 8);

      File pluginsRoot = new File(pluginsPath);
      // Will look for runtime plugins dir if user.dir wasn't found
      if (!pluginsRoot.exists()) {
         pluginsRoot = OsgiUtil.getGitRepoPluginsDir(ManifestTest.class);
      }

      File[] pluginDirs = pluginsRoot.listFiles();
      List<File> hasVersions = new ArrayList<>();
      if (pluginDirs != null) {
         for (File pluginDir : pluginDirs) {

            if (pluginDir.getAbsolutePath().contains("jms")) {
               continue;
            }
            File manifestDir = new File(pluginDir, "META-INF" + File.separator + "MANIFEST.MF");
            if (manifestHasVersion(manifestDir)) {
               hasVersions.add(pluginDir);
            }
         }
      }

      assertTrue("Manifests should not have version specified.  Found version in " + hasVersions,
         hasVersions.isEmpty());
   }

   private boolean manifestHasVersion(File path) {
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