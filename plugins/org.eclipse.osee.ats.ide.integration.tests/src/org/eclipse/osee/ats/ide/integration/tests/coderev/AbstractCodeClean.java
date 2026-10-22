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
package org.eclipse.osee.ats.ide.integration.tests.coderev;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Base class for CodeClean operations. Unlike {@link AbstractCodeReview} (read-only), CodeClean operations MUTATE
 * source files, so none of them are wired into any JUnit suite; they are run manually via {@code main()} or through
 * {@link CodeCleanAll}.
 * <p>
 * Extends {@link AbstractQual} for the shared machinery (result, {@code plugins}-anchored file discovery, HTML report
 * writing). On top of that this base owns the clean-specific behavior:
 * <ul>
 * <li>The {@link #changeFiles} guard, the {@link #changedFiles} list, and the combined "FILES CHANGED" report
 * (prepends a files summary, then delegates the HTML write to {@link AbstractQual}).</li>
 * <li>The member-based reorganization pipeline: read file, strip old section headers, find the class body, parse it
 * into {@link Member}s, hand them to the subclass to rebuild, and write back only if changed.</li>
 * </ul>
 * Subclasses that reorganize members implement {@link #shouldProcess(String, String)} and
 * {@link #rebuildBody(String, List)}. Operations that do not reorganize members (e.g. a git-based formatter or
 * reverter) can instead override {@link #performClean()} directly and simply log into {@link #rd}.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractCodeClean extends AbstractQual {

   protected static final String SECTION_SEPARATOR = "   //////////////////////////////////////////";
   protected static final String SECTION_FORMAT = SECTION_SEPARATOR + "\n   // %s\n" + SECTION_SEPARATOR;
   /** Compiled once; matches a generated section-header block for stripping on idempotent re-runs. */
   private static final Pattern HEADER_STRIP_PATTERN = Pattern.compile(
      "   //////////////////////////////////////////\r?\n   // [^\n]+\r?\n   //////////////////////////////////////////\r?\n");

   protected final List<String> changedFiles = new ArrayList<>();
   /**
    * When true, changes are written to disk; when false, the op only reports what it would do. Subclasses declare
    * their own {@code changeFiles} at the top of the class and set it here so the switch is easy to find; default is
    * always false - flip to true manually only when ready to make changes.
    */
   protected boolean changeFiles = false;
   /** When true, also organize abstract base classes (subclasses decide what that means). */
   protected boolean includeAbstract = true;

   /** @return human-readable name for this clean op; used as the report title and {@code <name>.html} filename. */
   protected abstract String getCleanName();

   /** The report name is the clean-op name. */
   @Override
   protected String getName() {
      return getCleanName();
   }

   @Override
   protected String getOutputDirName() {
      return CodeQualityUtil.CODE_CLEAN_DIR;
   }

   @Override
   protected String getToolLabel() {
      return "CodeClean";
   }

   /**
    * Run the clean operation and write/open the combined report. The default drives the member-reorganization
    * pipeline over {@link #findFiles()}; operations that do their own thing override {@link #performClean()}.
    */
   public void run() {
      rd.logf("%s...\n\n", getCleanName());
      if (changeFiles) {
         rd.logf("changeFiles = TRUE - files WILL be modified on disk. Review with git before committing.\n\n");
      } else {
         rd.logf("changeFiles = FALSE - report only, no files modified. Set changeFiles=true to apply.\n\n");
      }
      logOnlyOpenSource();
      rd.addResultsTag();

      performClean();

      reportResults();
   }

   /**
    * The body of the clean. Default implementation walks {@link #findFiles()} and reorganizes each via
    * {@link #processFile(String)}. Override for non-member-based cleans (formatting, reverting, etc.).
    */
   protected void performClean() {
      Collection<String> files = findFiles();
      if (files.isEmpty()) {
         rd.logf("No files found\n");
         return;
      }
      int count = 0;
      for (String filePath : files) {
         count++;
         rd.logf("\n=================================================================\n");
         rd.logf("Processing [%s]\n", filePath);
         processFile(filePath);
         if (breakAfterFile != null && count >= breakAfterFile) {
            rd.logf("\nStopping early at count %d\n", count);
            break;
         }
      }
   }

   // =========================================================================
   // Member-reorganization pipeline (shared by the *Organize* clean ops)
   // =========================================================================

   /**
    * @return true if this file should be reorganized. Called with the raw file contents (headers not yet stripped)
    * and the file's simple name (no extension).
    */
   protected boolean shouldProcess(String contents, String justFilename) {
      return true;
   }

   /**
    * Rebuild the class body from the parsed members. Return the full file text (header + reordered members + closing
    * brace, using {@code \n} line endings), or {@code null} to skip the file. Subclasses categorize {@code members}
    * and emit sections using {@link #SECTION_FORMAT}.
    *
    * @param header everything up to and including the class declaration's opening brace line
    */
   protected String rebuildBody(String justFilename, String header, List<Member> members) {
      return null;
   }

   /**
    * Shared per-file reorganization: read, strip old section headers, locate the class body, parse members, ask the
    * subclass to rebuild, then write back only if the content actually changed. Never lets one bad file abort the run.
    */
   protected void processFile(String fullFilename) {
      try {
         File file = new File(fullFilename);
         String justFilename = file.getName().replaceFirst("\\.java", "");
         // Single disk read; keep the raw on-disk text for the final change comparison.
         String originalContents = Lib.fileToString(file);
         String contents = originalContents;

         if (!shouldProcess(contents, justFilename)) {
            return;
         }

         // If already has section headers, strip them so we can re-process (idempotent re-runs)
         if (contents.contains(SECTION_SEPARATOR)) {
            contents = HEADER_STRIP_PATTERN.matcher(contents).replaceAll("");
         }

         String normalized = contents.replace("\r\n", "\n").replace("\r", "\n");
         String[] lines = normalized.split("\n");

         int classBodyStart = findClassBodyStart(lines);
         int classBodyEnd = findClassBodyEnd(lines);
         if (classBodyStart < 0 || classBodyEnd < 0) {
            rd.errorf("Could not find class body for [%s]\n", justFilename);
            return;
         }

         StringBuilder header = new StringBuilder();
         for (int i = 0; i < classBodyStart; i++) {
            header.append(lines[i]).append("\n");
         }

         List<Member> members = parseMembers(lines, classBodyStart, classBodyEnd);
         String rebuilt = rebuildBody(justFilename, header.toString(), members);
         if (rebuilt == null) {
            return;
         }

         // Compare rebuilt output to what's on disk (already read above) so both live and dry-run report the true
         // set of changes.
         String output = rebuilt.replaceAll("\n", "\r\n");
         boolean wouldChange = !output.equals(originalContents);

         if (!wouldChange) {
            rd.logf("No changes needed [%s]\n", justFilename);
         } else if (changeFiles) {
            Lib.writeStringToFile(output, file);
            changedFiles.add(justFilename);
            rd.logf("Reorganized [%s] - %d members categorized\n", justFilename, members.size());
         } else {
            changedFiles.add(justFilename);
            rd.logf("Would reorganize [%s] - %d members\n", justFilename, members.size());
         }
      } catch (RuntimeException | java.io.IOException ex) {
         // Never let one unparseable file abort the whole multi-repo run.
         rd.errorf("Error processing [%s]: %s\n", fullFilename, Lib.exceptionToString(ex));
      }
   }

   private int findClassBodyStart(String[] lines) {
      for (int i = 0; i < lines.length; i++) {
         String trimmed = lines[i].trim();
         if (trimmed.startsWith("public class ") || trimmed.startsWith("public abstract class ")) {
            for (int j = i; j < lines.length; j++) {
               if (lines[j].contains("{")) {
                  return j + 1;
               }
            }
            break;
         }
      }
      return -1;
   }

   private int findClassBodyEnd(String[] lines) {
      for (int i = lines.length - 1; i >= 0; i--) {
         if (lines[i].trim().equals("}")) {
            return i;
         }
      }
      return -1;
   }

   /**
    * Parse the class body into individual members (fields, methods, inner classes). Each member includes any preceding
    * annotations, javadoc, and blank lines that belong to it.
    */
   protected List<Member> parseMembers(String[] lines, int start, int end) {
      List<Member> members = new ArrayList<>();
      int i = start;
      while (i < end) {
         String trimmed = lines[i].trim();
         if (trimmed.isEmpty()) {
            i++;
            continue;
         }
         StringBuilder memberText = new StringBuilder();

         // Leading javadoc, comments, annotations
         while (i < end) {
            trimmed = lines[i].trim();
            if (trimmed.startsWith("/**") || trimmed.startsWith("/*") || trimmed.startsWith("*") || trimmed.startsWith(
               "//") || trimmed.startsWith("@")) {
               memberText.append(lines[i]).append("\n");
               i++;
               if (trimmed.startsWith("/**") || trimmed.startsWith("/*")) {
                  while (i < end && !lines[i - 1].trim().endsWith("*/")) {
                     memberText.append(lines[i]).append("\n");
                     i++;
                  }
               }
            } else {
               break;
            }
         }
         if (i >= end) {
            break;
         }
         trimmed = lines[i].trim();

         if (isFieldLine(trimmed)) {
            memberText.append(lines[i]).append("\n");
            i++;
            if (!trimmed.endsWith(";")) {
               while (i < end && !lines[i - 1].trim().endsWith(";")) {
                  memberText.append(lines[i]).append("\n");
                  i++;
               }
            }
         } else {
            int braceDepth = 0;
            boolean foundBrace = false;
            while (i < end) {
               memberText.append(lines[i]).append("\n");
               for (char c : lines[i].toCharArray()) {
                  if (c == '{') {
                     braceDepth++;
                     foundBrace = true;
                  }
                  if (c == '}') {
                     braceDepth--;
                  }
               }
               i++;
               if (foundBrace && braceDepth <= 0) {
                  break;
               }
            }
         }

         String text = memberText.toString();
         if (!text.trim().isEmpty()) {
            members.add(new Member(text));
         }
      }
      return members;
   }

   private boolean isFieldLine(String trimmed) {
      if (trimmed.endsWith(";") && !containsParenOutsideStrings(trimmed) && !trimmed.contains("{")) {
         return true;
      }
      if (trimmed.endsWith(";") && trimmed.contains("=") && !trimmed.contains("{")) {
         int eqIdx = trimmed.indexOf('=');
         if (!trimmed.substring(0, eqIdx).contains("(")) {
            return true;
         }
      }
      if ((trimmed.startsWith("public ") || trimmed.startsWith("private ") || trimmed.startsWith(
         "protected ") || trimmed.startsWith("static ") || trimmed.startsWith("final ")) && trimmed.contains(
            "=") && !trimmed.contains("{")) {
         int eqIdx = trimmed.indexOf('=');
         if (!trimmed.substring(0, eqIdx).contains("(")) {
            return true;
         }
      }
      if (!trimmed.startsWith("public ") && !trimmed.startsWith("private ") && !trimmed.startsWith(
         "protected ") && !containsParenOutsideStrings(
            trimmed) && !trimmed.contains("{") && (trimmed.endsWith(";") || trimmed.contains("="))) {
         if (!trimmed.startsWith("return ") && !trimmed.startsWith("break") && !trimmed.startsWith(
            "continue") && !trimmed.startsWith("throw ") && !trimmed.startsWith("super") && !trimmed.startsWith(
               "this")) {
            return true;
         }
      }
      if ((trimmed.startsWith("public ") || trimmed.startsWith("private ") || trimmed.startsWith(
         "protected ")) && !trimmed.contains("{") && trimmed.endsWith(";") && trimmed.contains("=")) {
         return true;
      }
      return false;
   }

   protected boolean containsParenOutsideStrings(String line) {
      boolean inString = false;
      for (int i = 0; i < line.length(); i++) {
         char c = line.charAt(i);
         if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
            inString = !inString;
         }
         if (!inString && (c == '(' || c == ')')) {
            return true;
         }
      }
      return false;
   }

   /** @return true if the member text is a field declaration (no method body). */
   protected boolean isField(String text) {
      String firstCode = getFirstCodeLine(text);
      if (firstCode.endsWith(";") && !containsParenOutsideStrings(firstCode) && !firstCode.contains("{")) {
         return true;
      }
      if (firstCode.endsWith(";") && firstCode.contains("=") && !firstCode.contains("{")) {
         int eqIdx = firstCode.indexOf('=');
         if (!firstCode.substring(0, eqIdx).contains("(")) {
            return true;
         }
      }
      if (!firstCode.contains("(") && !firstCode.contains("{") && text.trim().endsWith(";")) {
         return true;
      }
      if ((firstCode.startsWith("public static") || firstCode.startsWith("private static") || firstCode.startsWith(
         "protected static") || firstCode.startsWith("public final") || firstCode.startsWith(
            "private final") || firstCode.startsWith("protected final")) && text.trim().endsWith(
               ";") && !text.contains("{")) {
         return true;
      }
      if (!firstCode.contains("{") && !firstCode.contains("class ") && !firstCode.contains(
         "interface ") && !firstCode.contains("enum ")) {
         if (text.trim().endsWith(";") && !text.contains("{")) {
            if (!firstCode.startsWith("return ") && !firstCode.startsWith("break") && !firstCode.startsWith(
               "continue") && !firstCode.startsWith("throw ") && !firstCode.startsWith("super") && !firstCode.startsWith(
                  "this")) {
               if (firstCode.contains("=") || (!firstCode.contains("(") && !firstCode.contains(")"))) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /** @return the first non-comment, non-annotation code line in the member text. */
   protected String getFirstCodeLine(String text) {
      for (String line : text.split("\n")) {
         String t = line.trim();
         if (!t.isEmpty() && !t.startsWith("//") && !t.startsWith("/*") && !t.startsWith("*") && !t.startsWith("@")) {
            return t;
         }
      }
      return "";
   }

   protected boolean matchesAny(String line, String... patterns) {
      for (String p : patterns) {
         if (line.contains(p)) {
            return true;
         }
      }
      return false;
   }

   // =========================================================================
   // File discovery
   // =========================================================================

   /**
    * Default file pattern for member-reorganization cleans: all {@code .java} files. The Organize subclasses narrow
    * this (e.g. {@code XX.*Widget\\.java}); the git-based cleans override {@link #performClean()} and never use it.
    * Discovery itself (workspace-root resolution anchored on {@code plugins}, per-repo {@code plugins} walk) lives in
    * {@link AbstractQual}.
    */
   @Override
   protected String getFilePattern() {
      return ".*\\.java";
   }

   // =========================================================================
   // Reporting
   // =========================================================================

   /**
    * Write the combined report to the CodeClean output folder and open it. Prepends a files summary whose label
    * reflects {@link #changeFiles}: "FILES CHANGED" when files were written, "FILES THAT WOULD CHANGE" for a
    * report-only run, then delegates the HTML write/open to {@link AbstractQual#writeAndOpenReport}.
    */
   protected void reportResults() {
      XResultData finalRd = new XResultData();
      String label = changeFiles ? "FILES CHANGED" : "FILES THAT WOULD CHANGE";
      finalRd.logf("=== %s (%d) ===\n\n", label, changedFiles.size());
      for (String f : changedFiles) {
         finalRd.logf("  %s\n", f);
      }
      finalRd.logf("\n\n");
      finalRd.combine(rd);

      writeAndOpenReport(finalRd);
   }

   // =========================================================================
   // Member holder
   // =========================================================================

   /** A single class-body member (field, method, or inner type) plus its leading javadoc/annotations. */
   protected static class Member {
      public final String text;

      public Member(String text) {
         // Remove trailing blank lines from member text
         this.text = text.replaceAll("\n+$", "");
      }
   }

}
