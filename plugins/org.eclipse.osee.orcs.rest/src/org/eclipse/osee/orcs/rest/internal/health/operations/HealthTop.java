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

/**
 * @author Jaden W. Puckett
 */
public class HealthTop {

   public HealthTop() {

   }

   public String getTop() {
      StringBuilder top = new StringBuilder();
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
               top.append((char) value);
            }
            int exitCode = p.waitFor();
            top.append("Top command exited with ").append(exitCode);
         }
      } catch (Exception e) {
         top = new StringBuilder(e.getMessage());
      }
      return top.toString();
   }

}
