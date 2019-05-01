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
package org.eclipse.osee.framework.jdk.core.text.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public class CountAscii {

   public static void main(String[] args) throws IOException {
      if (args.length < 1) {
         System.out.println("Usage: java text.CountAscii <directory of files>");
         return;
      }

      int[] letterCount = new int[128];

      File directory = new File(args[0]);
      File[] files = directory.listFiles(new MatchFilter(".*\\.c"));
      if (files != null) {
         System.out.println("Found " + files.length + " files.");

         for (int i = 0; i < files.length; i++) {
            BufferedReader in = new BufferedReader(new FileReader(files[i]));
            int c = 0;

            while ((c = in.read()) != -1) {
               letterCount[c]++;
            }
            in.close();
         }

         for (int i = 0; i < letterCount.length; i++) {
            System.out.println((char) i + ": " + letterCount[i]);
         }
         /*
          * int total = 0; for(int i=0; i<letterCount.length; i++) { total += letterCount[i]; }
          */
      }
   }
}
