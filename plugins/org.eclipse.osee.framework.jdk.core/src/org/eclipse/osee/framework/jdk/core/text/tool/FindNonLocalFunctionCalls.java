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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class FindNonLocalFunctionCalls {
   public static final Pattern functionCallPattern = Pattern.compile("\\W(\\w+)\\s*\\([^;{]*?\\)\\s*;");
   public static final Pattern functionDefPattern = Pattern.compile("\\W(\\w+)\\s*\\([^;{]*?\\)\\s*\\{");
   private LinkedHashSet<String> set;
   private File[] files;

   public FindNonLocalFunctionCalls(File[] files) {
      this.files = files;
      set = new LinkedHashSet<String>();
   }

   public static void main(String[] args) {
      File[] files = new File[args.length];
      for (int i = 0; i < args.length; i++) {
         files[i] = new File(args[i]);
      }
      FindNonLocalFunctionCalls app = new FindNonLocalFunctionCalls(files);
      app.searchFiles();
      app.print();
   }

   public void searchFiles() {
      for (int i = 0; i < files.length; i++) {
         try {
            look(Lib.stripBlockComments(Lib.fileToCharBuffer(files[i])));
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   private void look(CharSequence seq) {
      ArrayList<String> localFunctions = new ArrayList<String>();
      Matcher functionDefM = functionDefPattern.matcher(seq);
      while (functionDefM.find()) {
         localFunctions.add(functionDefM.group(1));
      }

      Matcher functionCallM = functionCallPattern.matcher(seq);
      while (functionCallM.find()) {
         if (!localFunctions.contains(functionCallM.group(1))) {
            set.add(functionCallM.group(1));
         }
      }
   }

   public Set<String> getResultSet() {
      return set;
   }

   public void print() {
      ArrayList<String> list = new ArrayList<String>(set);
      Collections.sort(list);
      for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
         System.out.println(iter.next());
      }
   }
}