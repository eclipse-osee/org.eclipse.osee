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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public class SignalChanges {

   public static void main(String[] args) throws IOException {
      if (args.length < 1) {
         System.out.println("Usage: java text.SignalChanges <.dat directory> <scirpt source directory>");
         return;
      }

      ArrayList<Object> list = loadList(args[0]);
      writeSignalChanges(list, new File(args[0], "cdb_changes.txt"), new File(args[0], "mux_changes.txt"));
      writeChangeVerification(args[1], list);
   }

   // return list of RpcrSignalChanges found in the dat files in the given directory
   public static ArrayList<Object> loadList(String path) throws IOException {
      ArrayList<Object> list = new ArrayList<Object>(20);

      File directory = new File(path);
      File[] files = directory.listFiles(new MatchFilter(".*\\.txt"));
      Arrays.sort(files);
      System.out.println("Found " + files.length + " dat files in " + path + ".");

      for (int i = 0; i < files.length; i++) {
         list.add(new RpcrSignalChanges(files[i]));
      }
      return list;
   }

   public static void writeChangeVerification(String scriptsPath, ArrayList<Object> changesList) throws IOException {
      BufferedWriter out = new BufferedWriter(new FileWriter("rpcr_list.txt"));

      File directory = new File(scriptsPath);
      File[] files = directory.listFiles(new MatchFilter(".*\\.c"));
      Arrays.sort(files);
      System.out.println("Found " + files.length + " script files in " + scriptsPath + ".");

      for (int i = 0; i < changesList.size(); i++) {
         RpcrSignalChanges rpCh = (RpcrSignalChanges) changesList.get(i);
         out.write(rpCh.rpcrName + "\n");

         for (int j = 0; j < files.length; j++) {
            BufferedReader in = new BufferedReader(new FileReader(files[j]));

            String line = null;
            while ((line = in.readLine()) != null) {
               if (rpCh.affectsLine(line)) {
                  out.write("\t" + files[j].getName() + "\n");
                  break;
               }
            }
            in.close();
         }
         out.flush();
      }
      out.close();
   }

   public static void writeSignalChanges(ArrayList<Object> changesList, File outputFile, File muxOutputFile) throws IOException {
      BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
      BufferedWriter muxOut = new BufferedWriter(new FileWriter(muxOutputFile));

      for (int i = 0; i < changesList.size(); i++) {
         RpcrSignalChanges rpcrCng = (RpcrSignalChanges) changesList.get(i);
         if (rpcrCng.mux) {
            for (int j = 0; j < rpcrCng.signals.size(); j++) {
               muxOut.write(rpcrCng.rpcrName + "," + rpcrCng.signals.get(j) + "\n");
            }
         } else {
            for (int j = 0; j < rpcrCng.signals.size(); j++) {
               out.write(rpcrCng.rpcrName + " " + rpcrCng.signals.get(j) + "\n");
            }
         }
      }
      out.close();
      muxOut.close();
   }
}
