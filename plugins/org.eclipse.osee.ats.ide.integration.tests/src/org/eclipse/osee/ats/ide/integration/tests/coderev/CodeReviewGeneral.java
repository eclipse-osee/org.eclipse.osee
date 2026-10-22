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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * General code review for all Java classes. Checks for common issues across the codebase:
 * <ul>
 * <li>System.out/err.print statements (warning)</li>
 * <li>TBD markers (warning)</li>
 * <li>Pattern.compile() should be a static final field, not compiled per call (warning)</li>
 * <li>Security: AuthenticationRequestFilter unauthorized call must not be commented out (error)</li>
 * <li>JUnit test suites must not have commented-out entries in their &#64;Suite.SuiteClasses list (error)</li>
 * </ul>
 *
 * @author Donald G. Dunne
 */
public class CodeReviewGeneral extends AbstractCodeReview {

   private static final String AUTH_FILTER_CLASS = "AuthenticationRequestFilter";

   /**
    * Matches a commented-out suite/test entry inside a &#64;Suite.SuiteClasses block, e.g.
    *
    * <pre>
    * //   AtsTest_AllAts_Suite.class,
    * // SkynetUiColumnTestSuite.class,
    * </pre>
    *
    * The line must start (after leading whitespace) with a // line comment and reference a *.class entry.
    */
   private static final Pattern COMMENTED_SUITE_ENTRY = Pattern.compile("^\\s*//\\s*\\w+\\.class\\b");

   /** Cached once; getIgnoreDirs() is called per directory during the tree walk. */
   private List<String> ignoreDirsCache = null;

   @Override
   protected String getReviewName() {
      return "CodeReviewGeneral";
   }

   @Override
   protected List<String> getChecksPerformed() {
      return Arrays.asList( //
         "No System.out.print or System.err.print statements (warning)", //
         "No TBD markers in non-comment code (warning)", //
         "Pattern.compile() should be a static final field, not compiled per call (warning)", //
         "AuthenticationRequestFilter.filter must have unauthorized() call active (error)", //
         "Test suites must not have commented-out entries in @Suite.SuiteClasses (error)");
   }

   @Override
   protected String getFilePattern() {
      return ".*\\.java";
   }

   /**
    * The default ignore list skips the {@code .integration.tests.} path, but the JUnit test suites this review must
    * inspect live there. Keep every other default ignore, but allow the integration tests package to be scanned.
    */
   @Override
   protected List<String> getIgnoreDirs() {
      if (ignoreDirsCache == null) {
         List<String> ignores = new ArrayList<>(super.getIgnoreDirs());
         ignores.remove(".integration.tests.");
         ignoreDirsCache = ignores;
      }
      return ignoreDirsCache;
   }

   @Override
   protected void performReview(List<String> files) {
      int x = 0;
      for (String javaFile : files) {
         x++;
         validateFile(javaFile);
         if (breakAfterFile != null && x == breakAfterFile) {
            break;
         }
      }
   }

   @SuppressWarnings("null")
   private void validateFile(String filePath) {
      String contents = readFile(filePath);
      if (Strings.isInvalid(contents)) {
         return;
      }
      String className = getClassName(filePath);
      boolean hasIssues = false;
      FileResult fr = null;

      // Check System.out/err.print
      if (containsSystemPrint(contents)) {
         if (fr == null) {
            fr = startFile(filePath);
         }
         fr.warningf("Contains System.out/err.print statement [%s] - Use OseeLog or remove\n", className);
         hasIssues = true;
      }

      // Check TBD markers (only in code, not comments)
      if (containsTbd(contents)) {
         if (fr == null) {
            fr = startFile(filePath);
         }
         fr.warningf("Contains TBD marker [%s] - Resolve before release\n", className);
         hasIssues = true;
      }

      // Check for Pattern.compile() that is not a static final field (should be hoisted so it compiles once)
      if (containsNonStaticPatternCompile(contents)) {
         if (fr == null) {
            fr = startFile(filePath);
         }
         fr.warningf(
            "Pattern.compile() not in a static final field [%s] - hoist to a 'static final Pattern' so the regex "
               + "compiles once, not on every call\n",
            className);
         hasIssues = true;
      }

      // Security check: AuthenticationRequestFilter must have unauthorized() active
      if (className.equals(AUTH_FILTER_CLASS)) {
         checkAuthFilter(filePath, contents);
         hasIssues = true;
      }

      // Report each failing suite once (first commented entry + count). Guard so one unparseable file can't abort
      // the review of the rest.
      try {
         List<String> commented = getCommentedSuiteEntries(contents);
         if (!commented.isEmpty()) {
            if (fr == null) {
               fr = startFile(filePath);
            }
            String more = commented.size() > 1 ? String.format(" (+%d more)", commented.size() - 1) : "";
            fr.errorf("Commented-out test(s) or suite(s) in [%s]: %s%s - re-enable, delete, or mark '%s'\n",
               className, commented.get(0), more, IGNORE_LINE);
            hasIssues = true;
         }
      } catch (RuntimeException ex) {
         if (fr == null) {
            fr = startFile(filePath);
         }
         fr.warningf("Could not parse @Suite.SuiteClasses in [%s]: %s\n", className, ex.getMessage());
         hasIssues = true;
      }

      // Only create a FileResult for clean files if there were no issues (avoid cluttering)
      if (!hasIssues) {
         // Don't create FileResult for clean files to keep report concise
      }
   }

   private boolean containsSystemPrint(String contents) {
      String[] lines = contents.split("\n");
      for (String line : lines) {
         String trimmed = line.trim();
         if (trimmed.startsWith("//") || trimmed.startsWith("*")) {
            continue;
         }
         if (isIgnored(line)) {
            continue;
         }
         if (trimmed.contains("System.out.print") || trimmed.contains("System.err.print")) {
            return true;
         }
      }
      return false;
   }

   private boolean containsTbd(String contents) {
      String[] lines = contents.split("\n");
      for (String line : lines) {
         String trimmed = line.trim();
         if (isIgnored(line)) {
            continue;
         }
         // Check comment lines for TBD markers
         if (trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*")) {
            if (trimmed.contains("TBD")) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * @return true if the file calls {@code Pattern.compile(...)} somewhere that is NOT a {@code static} field
    * declaration. A properly hoisted pattern is declared as a {@code static final Pattern NAME = Pattern.compile(...)}
    * so the regex is compiled once; compiling inside a method (or in a non-static field) recompiles on every call.
    * The declaration may span lines, so when the {@code Pattern.compile(} line itself has no {@code static}, the
    * lines above it (back to the previous statement boundary) are checked for the field's {@code static} modifier.
    */
   private boolean containsNonStaticPatternCompile(String contents) {
      String[] lines = contents.split("\n");
      for (int i = 0; i < lines.length; i++) {
         String line = lines[i];
         if (!line.contains("Pattern.compile(")) {
            continue;
         }
         String trimmed = line.trim();
         // Ignore comment lines and explicit opt-outs.
         if (trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*") || isIgnored(line)) {
            continue;
         }
         // Static field on this same line -> fine.
         if (line.contains("static")) {
            continue;
         }
         // Declaration may start on preceding lines; scan back to the previous statement/scope boundary.
         boolean staticDecl = false;
         for (int j = i - 1; j >= 0; j--) {
            String prev = lines[j].trim();
            if (prev.isEmpty() || prev.endsWith(";") || prev.endsWith("{") || prev.endsWith("}")) {
               break; // reached the boundary of this declaration
            }
            if (lines[j].contains("static")) {
               staticDecl = true;
               break;
            }
         }
         if (!staticDecl) {
            return true;
         }
      }
      return false;
   }

   /**
    * Verify that AuthenticationRequestFilter.filter() has the unauthorized call active (not commented out). The
    * expected pattern is:
    *
    * <pre>
    * if (!exceptionList && orcsApi.userService().getUser().isInvalid()) {
    *    unauthorized(requestContext);
    * }
    * </pre>
    */
   private void checkAuthFilter(String filePath, String contents) {
      FileResult fr = startFile(filePath);

      // Look for the filter method
      if (!contents.contains("void filter(ContainerRequestContext")) {
         fr.errorf("AuthenticationRequestFilter: filter(ContainerRequestContext) method not found\n");
         return;
      }

      // Check that unauthorized call exists and is NOT commented out
      boolean hasUnauthorizedCall = false;
      boolean unauthorizedCommentedOut = false;
      String[] lines = contents.split("\n");
      boolean inFilterMethod = false;
      int braceDepth = 0;

      for (String line : lines) {
         if (line.contains("void filter(ContainerRequestContext")) {
            inFilterMethod = true;
            braceDepth = 0;
         }
         if (inFilterMethod) {
            for (char c : line.toCharArray()) {
               if (c == '{') {
                  braceDepth++;
               }
               if (c == '}') {
                  braceDepth--;
               }
            }
            if (line.contains("unauthorized(requestContext)")) {
               String trimmed = line.trim();
               if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                  unauthorizedCommentedOut = true;
               } else {
                  hasUnauthorizedCall = true;
               }
            }
            if (braceDepth <= 0 && inFilterMethod && line.trim().equals("}")) {
               inFilterMethod = false;
            }
         }
      }

      if (unauthorizedCommentedOut && !hasUnauthorizedCall) {
         fr.errorf(
            "SECURITY: AuthenticationRequestFilter.filter() has unauthorized(requestContext) COMMENTED OUT - This disables authentication!\n");
      } else if (!hasUnauthorizedCall) {
         fr.errorf(
            "SECURITY: AuthenticationRequestFilter.filter() is missing unauthorized(requestContext) call - Authentication may be disabled!\n");
      }
   }

   /**
    * Scan a JUnit test suite's {@code @Suite.SuiteClasses({ ... })} block for commented-out entries. Developers often
    * comment out suites/tests to speed up local runs and forget to restore them before committing; those disabled tests
    * then silently stop running. Only files that declare {@code @Suite.SuiteClasses} are inspected, and only the
    * contents of that annotation block are considered so ordinary commented code elsewhere is not flagged.
    *
    * @return the list of offending commented entries (trimmed), empty if none or if the file is not a suite
    */
   private List<String> getCommentedSuiteEntries(String contents) {
      List<String> offenders = new ArrayList<>();
      int annotationStart = contents.indexOf("@Suite.SuiteClasses");
      if (annotationStart < 0) {
         return offenders;
      }
      // The SuiteClasses list is the '(' ... ')' immediately following the annotation. A brace form
      // @Suite.SuiteClasses({ ... }) and a bare form @Suite.SuiteClasses(Foo.class) are both valid; parse the
      // parenthesized argument so we never wander into the class body (which produced misparses/aborts before).
      int listStart = contents.indexOf('(', annotationStart);
      if (listStart < 0) {
         return offenders;
      }
      int depth = 0;
      int listEnd = -1;
      for (int i = listStart; i < contents.length(); i++) {
         char c = contents.charAt(i);
         if (c == '(') {
            depth++;
         } else if (c == ')') {
            depth--;
            if (depth == 0) {
               listEnd = i;
               break;
            }
         }
      }
      if (listEnd < 0) {
         // Malformed / unterminated annotation - do not attempt to parse (and never abort the overall scan).
         return offenders;
      }

      String block = contents.substring(listStart, listEnd);
      for (String line : block.split("\n")) {
         if (isIgnored(line)) {
            continue;
         }
         if (COMMENTED_SUITE_ENTRY.matcher(line).find()) {
            offenders.add(line.trim());
         }
      }
      return offenders;
   }

   public static void main(String[] args) {
      new CodeReviewGeneral().run();
   }

}
