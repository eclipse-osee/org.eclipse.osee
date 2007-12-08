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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author Ryan D. Brooks
 */
public class RpcrSignalChanges {
   public String rpcrName;
   public ArrayList<String> signals;
   public boolean mux;

   public RpcrSignalChanges(File file) throws IOException {
      rpcrName = file.getName().substring(0, 4);
      this.signals = new ArrayList<String>(500);

      BufferedReader in = null;
      try {
         in = new BufferedReader(new FileReader(file));
      } catch (FileNotFoundException ex) {
         System.err.println(ex);
         throw new IllegalArgumentException(ex.toString());
      }

      String line = null;
      // if line 6 starts with "      Signal Name", then these are mux changes
      for (int j = 0; j < 6; j++) {
         line = in.readLine();
      }

      mux = line.startsWith("      Signal Name");

      while ((line = in.readLine()) != null) {
         String result = extractSignal(line);
         if (result != null) {
            signals.add(result);
         }
      }
      in.close();
   }

   protected String extractSignal(String line) {
      try {
         StringTokenizer tok = new StringTokenizer(line);
         if (!tok.hasMoreTokens()) {
            System.out.println("blank line");
            return null;
         }
         String changeSymbol = tok.nextToken();

         if (changeSymbol.equals("+") || changeSymbol.equals(">")) {
            if (!tok.nextToken().equals("is:")) {
               throw new IllegalArgumentException("Ryan --> you didn't think of everyting. Duh!");
            }
            if (line.charAt(6) != ' ') { // there is either a mux name or a fiber interface
               if (mux) {
                  return line.substring(6, 25).trim();
               } else {
                  tok.nextToken(); // skip interface name
                  String elementName = tok.nextToken();
                  String lmName = tok.nextToken();
                  return "l_" + lmName.toLowerCase() + "__" + elementName.toLowerCase();
               }
            }
         }
      } catch (NoSuchElementException ex) {
         System.out.println(ex + line);
         System.exit(1);
      }
      return null;
   }

   public boolean affectsLine(String line) {
      for (int i = 0; i < signals.size(); i++) {
         if (line.indexOf((String) signals.get(i)) != -1) {
            return true;
         }
      }
      return false;
   }
}
