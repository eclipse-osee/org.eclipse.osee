/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author David W. Miller
 */
public class TraceAccumulator {

   private final SetMultimap<CaseInsensitiveString, String> traceMarkToFiles = HashMultimap.create();
   private final SetMultimap<String, String> fileToMalformeddMarks = HashMultimap.create();
   private final LinkedList<String> noTraceFiles = new LinkedList<>();
   private final Pattern filePattern;
   private final Iterable<TraceMatch> traceMatches;
   private SetMultimap<String, CaseInsensitiveString> fileToTraceMarks;
   private String relativePath;

   public TraceAccumulator(String filePattern, Iterable<TraceMatch> traceMatches) {
      this.filePattern = Pattern.compile(filePattern);
      this.traceMatches = traceMatches;
   }

   public TraceAccumulator(String filePattern, TraceMatch traceMatch) {
      this(filePattern, Collections.singletonList(traceMatch));
   }

   public void extractTraces(File root) throws IOException {
      if (root == null || root.getParentFile() == null) {
         throw new OseeArgumentException("The path [%s] is invalid.", root);
      }
      checkDirectory(root);
      if (root.isFile()) {
         for (String path : Lib.readListFromFile(root, true)) {
            traceFile(new File(path));
         }
      } else if (root.isDirectory()) {
         traceFile(root);
      } else {
         throw new OseeArgumentException("Invalid directory path [%s]", root.getCanonicalPath());
      }

      fileToTraceMarks = Multimaps.invertFrom(traceMarkToFiles, HashMultimap.<String, CaseInsensitiveString> create());
   }

   private void traceFile(File root) throws IOException {
      String canonicalPath = root.getCanonicalPath();
      int prefixLength = canonicalPath.length();
      int index = canonicalPath.indexOf(".ss\\");

      String virtualPrefix =
         index == -1 ? "" : canonicalPath.substring(canonicalPath.lastIndexOf('\\', index) + 1, index + 4);

      for (File sourceFile : Lib.recursivelyListFilesAndDirectories(new ArrayList<File>(400), root, filePattern,
         false)) {
         /**
          * use prefixLength + 1 to account for trailing file separator which is not included in the root canonical path
          */
         relativePath = virtualPrefix + sourceFile.toString().substring(prefixLength + 1);
         int traceCount = parseInputStream(new FileInputStream(sourceFile));

         if (traceCount == 0) {
            noTraceFiles.add(relativePath);
         }
      }
   }

   public void addInvalidTrace(String invalidTrace) {
      fileToMalformeddMarks.put(relativePath, invalidTrace);
   }

   public void addValidTrace(String traceMark) {
      traceMarkToFiles.put(new CaseInsensitiveString(traceMark), relativePath);
   }

   public int parseInputStream(InputStream providedStream) {
      int numTracesInStream = 0;
      Scanner scanner = new Scanner(providedStream, "UTF-8");
      try {
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (TraceMatch traceMatch : traceMatches) {
               int numTracesInLine = traceMatch.processLine(line, this);
               numTracesInStream += numTracesInLine;
               if (numTracesInLine > 0) {
                  break;
               }
            }
         }
      } finally {
         scanner.close();
      }
      return numTracesInStream;
   }

   private void checkDirectory(File file) throws IOException {
      if (!file.exists()) {
         throw new OseeArgumentException("Input file does not exist: %s", file.getPath());
      }
      if (file.isFile()) {
         for (String path : Lib.readListFromFile(file, true)) {
            File embeddedPath = new File(path);
            if (!embeddedPath.exists()) {
               throw new OseeCoreException("Bad path embedded in file: %s", path);
            }
         }
      }
   }

   public Set<String> getFiles(String requirement) {
      return traceMarkToFiles.get(new CaseInsensitiveString(requirement));
   }

   public Set<String> getFiles() {
      return fileToTraceMarks.keySet();
   }

   public Set<CaseInsensitiveString> getTraceMarks(String codeUnit) {
      return fileToTraceMarks.get(codeUnit);
   }

   public Set<CaseInsensitiveString> getTraceMarks() {
      return traceMarkToFiles.keySet();
   }

   public LinkedList<String> getNoTraceFiles() {
      return noTraceFiles;
   }

   public Set<String> getMalformedMarks(String file) {
      return fileToMalformeddMarks.get(file);
   }

   public Set<String> getFilesWithMalformedMarks() {
      return fileToMalformeddMarks.keySet();
   }
}