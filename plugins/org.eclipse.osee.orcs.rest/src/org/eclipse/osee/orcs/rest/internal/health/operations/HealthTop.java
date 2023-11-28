/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Captures the top for Linux OS
 *
 * @author Jaden W. Puckett
 */
public class HealthTop {
   private String top = "";

   public HealthTop() {

   }

   public void setTop() {
      StringBuilder topToBuild = new StringBuilder();
      topToBuild.append("cmd [top -b -n 1]\n\n");
      if (!Lib.isWindows()) {
         // Run top in batch mode for single iteration
         ProcessBuilder pb = new ProcessBuilder("top", "-b", "-n", "1");
         // Merge error stream with the input stream
         pb.redirectErrorStream(true);
         try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream();
               BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
               int value;
               while ((value = reader.read()) != -1) {
                  topToBuild.append((char) value);
               }
               int exitCode = p.waitFor();
               topToBuild.append("Top command exited with ").append(exitCode);
            }
         } catch (Exception e) {
            topToBuild = new StringBuilder(e.getMessage());
         }
      } else {
         topToBuild.append("Can not fetch top for Windows OS");
      }
      top = topToBuild.toString();
   }

   public String getTop() {
      return top;
   }
}
