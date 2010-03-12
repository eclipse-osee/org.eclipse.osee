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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public class CompareLists {

   public static void main(String[] args) throws IOException {

      if (args.length != 4 && args.length != 2) {
         System.out.println("Usage: java text.CompareLists <directory_1> <ext_1> <directory_2> <ext_2>");
         System.out.println("or   : java text.CompareLists <path1> <path2>");
         return;
      }

      if (args.length == 4) {
         writeDiff(Lib.readListFromDir(args[0], new MatchFilter(".*\\." + args[1])), Lib.readListFromDir(args[2],
               new MatchFilter(".*\\." + args[3])), new File("list_diff.txt"),
               "Directory \"" + args[0] + "\", files with extension \"" + args[1] + "\"",
               "Directory \"" + args[2] + "\", files with extension \"" + args[3] + "\"");
      } else {
         writeDiff(Lib.readListFromFile(args[0]), Lib.readListFromFile(args[1]), new File("list_diff.txt"), args[0],
               args[1]);
      }
   }

   @SuppressWarnings("unchecked")
   public static void writeDiff(Collection listA, Collection listB, File file, String listDescription1, String listDescription2) throws IOException {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      //Enforce uniqueness
      Set setA = Collections.toSet(listA);
      Set setB = Collections.toSet(listB);

      out.write("Items only in list A (" + listDescription1 + ")\n\n");
      List complement = Collections.setComplement(setA, setB);
      for (int i = 0; i < complement.size(); i++) {
         out.write(complement.get(i) + "\n");
      }

      out.write("\nItems only in list B (" + listDescription2 + ")\n\n");
      complement = Collections.setComplement(setB, setA);
      for (int i = 0; i < complement.size(); i++) {
         out.write(complement.get(i) + "\n");
      }

      //The intersection of two sets A and B is the set of elements common to A and B. 
      out.write("\nItems in both lists\n\n");
      ArrayList intersection = Collections.setIntersection(setA, setB);
      for (int i = 0; i < intersection.size(); i++) {
         out.write(intersection.get(i) + "\n");
      }
      out.close();
      System.out.println("Finished processing.  Output is in file \"list_diff.txt\"");
   }
}
