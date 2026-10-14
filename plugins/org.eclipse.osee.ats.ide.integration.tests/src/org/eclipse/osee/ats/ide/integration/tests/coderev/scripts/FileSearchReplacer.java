/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.coderev.scripts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * THROWAWAY migration utility. <br/>
 * <br/>
 * A stand-alone java class with a main method that will <br/>
 * - Recursively go through directories and look for files using regular expression like "My*.java<br/>
 * - I want a list of directories or strings of directories to ignore like "bin" or "image" so it runs faster<br/>
 * - I want to search and replace strings in the files <br/>
 * - I want to provide a list of search/replaces to go through<br/>
 * - Each item in the list will specify a regular expression to search for an a replacement string<br/>
 * - I also want to specify if I want to just replace the first occurrence or all of them<br/>
 * - I want a boolean for debugging that will only do the first x files so I can review<br/>
 * <br/>
 * WARNING: This is an iterative process to get things right which is why the utility was created. Ability to keep
 * adding, running, modifying rules until you get it right. COMMIT ALL FILES BEFORE RUNNING SO YOU CAN REVERT. <br/>
 * <br/>
 * ALSO WARNING: Don't revert your changes till you commit <br/>
 * <br/>
 * To Run<br/>
 * - Edit configuration to match your setup, In Eclipse, Select this file > Right click > Debug-As > Java Application
 *
 * @author Donald G. Dunne
 */
public class FileSearchReplacer {

   // --- CONFIGURATION ---
   private static final String START_DIR = "C:\\Tools\\git_ggg";
   private static final String FILE_NAME_REGEX = "WorkDef.*\\.java"; // e.g., "MyClass.*\\.java"
   private static final List<String> IGNORE_DIRS = List.of("bin", "image", ".git", "target");
   private static final boolean DEBUG_MODE = false; // If true, stops after DEBUG_LIMIT files
   private static final int DEBUG_LIMIT = 25;

   // --- SEARCH & REPLACE RULES ---
   private static class ReplaceRule {
      public String search;
      public String replace;
      public boolean isRegex; // true = replaceAll, false = replace (literal)

      public ReplaceRule(String search, String replace, boolean isRegex) {
         this.search = search;
         this.replace = replace;
         this.isRegex = isRegex;
      }
   }

   private static final List<ReplaceRule> RULES = new ArrayList<>();
   static {
      // Add your rules here: (Regex, Replacement String, ReplaceAll boolean)
      // Multiple rules can be added and will run in order

      // @formatter:off
       RULES.add(new ReplaceRule("@SuppressWarnings\\(\"unused\"\\)", "", false));
      // @formatter:on

      // Example Rules
      // RULES.add(new ReplaceRule(", SAVE\\)", ")", true));
      // RULES.add(new ReplaceRule("@SuppressWarnings\\(\"unused\"\\)", "", false));
      // RULES.add(new ReplaceRule("(?i)todo:", "COMPLETED:", false)); // Replace first 'todo:' (case-insensitive)
   }

   private static int processedCount = 0;

   public static void main(String[] args) {
      Path startPath = Paths.get(START_DIR);
      Pattern fileMatcher = Pattern.compile(FILE_NAME_REGEX);

      try {
         Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
               // Skip ignored directories to improve performance
               if (IGNORE_DIRS.contains(dir.getFileName().toString())) {
                  return FileVisitResult.SKIP_SUBTREE;
               }
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               // Debugging limit check
               if (DEBUG_MODE && processedCount >= DEBUG_LIMIT) {
                  System.out.println("Debug limit reached. Terminating search.");
                  return FileVisitResult.TERMINATE;
               }

               // Check if file name matches the regex
               if (fileMatcher.matcher(file.getFileName().toString()).matches()) {
                  processFile(file);
               }
               return FileVisitResult.CONTINUE;
            }
         });
         System.out.println("\nExecution complete. Total files processed: " + processedCount);
      } catch (IOException e) {
         System.err.println("Error walking file tree: " + e.getMessage());
      }
   }

   private static void processFile(Path path) throws IOException {
      System.out.println("Reading: " + path.toAbsolutePath());
      String origContent = Files.readString(path, StandardCharsets.UTF_8);
      String content = origContent;
      boolean changed = false;

      for (ReplaceRule rule : RULES) {
         if (rule.isRegex) {
            content = content.replaceAll(rule.search, rule.replace);
         } else {
            content = content.replace(rule.search, rule.replace);
         }
      }
      changed = !origContent.equals(content);

      if (changed) {
         Files.writeString(path, content, StandardCharsets.UTF_8);
         System.out.println("  -> SUCCESS: File updated.");
      } else {
         System.out.println("  -> No matches found.");
      }
      processedCount++;
   }
}
