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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Looks at files modified across ALL unmerged commits on the branch (base ref = merge-base with the branch's configured
 * upstream) and reverts (via git checkout) any that contain ONLY non-functional changes. Non-functional changes are:
 * formatting, whitespace, import changes (reordering, collapse/expand, static conversion, unused removal), and blank
 * line additions/removals.
 * <p>
 * This does NOT amend any commit. It just does a local {@code git checkout <baseRef> -- file} to undo the working copy
 * change, leaving the file as an unstaged modification that can be excluded from the next commit.
 * <p>
 * CodeClean operation. Run as Java Application (Debug As > Java Application) or via {@link CodeCleanAll}. Mutates
 * files, so it is never run as part of the integration test suite. Produces the same {@code ~/<name>.html} report as
 * the other CodeClean operations (in addition to console output); the report lists the commits reviewed at the top.
 * <p>
 * How it works, per repo under the workspace root:
 * <ol>
 * <li>Resolves the base ref: merge-base of the branch's upstream ({@code @{u}}) and HEAD (falls back to HEAD~1)</li>
 * <li>One {@code git diff --raw --no-abbrev} lists every modified .java file with its old + new blob SHAs</li>
 * <li>A content-hash cache (keyed on the {@code (oldBlobSha, newBlobSha)} pair) is consulted first; on a hit the
 * classification is reused with no {@code git show}</li>
 * <li>On a cache miss, gets the old version (at baseRef) and new version (HEAD), normalizes both (strip imports and
 * blank lines, trim whitespace), and classifies: functional change (KEEP) or pure non-functional (REVERT). Import
 * changes (static conversion, sorting, wildcard collapse/expand, unused removal) count as non-functional and ARE
 * reverted - the codebase compiles, so any import form is valid.</li>
 * <li>The cache is persisted to {@code ~/CodeCleanOutput/revert-cache.txt}. Because blob SHAs are pure content hashes,
 * the cache survives commits, squashes, and rebases - an entry is stale only when a file's actual content at an
 * endpoint changes.</li>
 * </ol>
 *
 * @author Donald G. Dunne
 */
public class CodeCleanRevertNonFunctional extends AbstractCodeClean {

   /**
    * When true, also log the KEEP lines (files left untouched). Default off - KEEP lines are noise for the common case;
    * the summary still counts them. Flip to true when debugging why a file was not reverted.
    */
   private final boolean debug = false;

   public CodeCleanRevertNonFunctional() {
      // Report only by default; set true manually when ready to revert files.
      changeFiles = false;
   }

   private int revertedCount = 0;
   private int keptCount = 0;
   private int cacheHits = 0;

   /**
    * The classification of a file's change, computed from the pair of file contents. Cached by {@code (oldBlobSha,
    * newBlobSha)} so a repeat run over unchanged content skips the two {@code git show} spawns + normalization.
    */
   private enum Verdict {
      /** Only non-functional (whitespace/blank-line/formatting), no imports - safe to revert. */
      REVERT,
      /** Has functional changes - left alone. */
      KEEP;
   }

   /**
    * Content-hash cache: {@code oldBlobSha + " " + newBlobSha -> Verdict}. Keyed on git blob SHAs (pure content
    * hashes), so it survives commits, squashes, and rebases - an entry is only stale when the file's actual content at
    * an endpoint changes. Loaded from {@link #cacheFile()} at the start of a run and saved at the end.
    */
   private final Map<String, Verdict> verdictCache = new HashMap<>();

   public static void main(String[] args) {
      new CodeCleanRevertNonFunctional().run();
   }

   @Override
   protected String getCleanName() {
      return "CodeCleanRevertNonFunctional";
   }

   /**
    * Log an informational line into {@link #rd} so it appears in the HTML report (and the {@link CodeCleanAll}
    * aggregation).
    */
   private void log(String msg) {
      // rd.log() already appends a single newline; passing msg (not msg + "\n") avoids the double blank line
      // (double-spaced report) that an extra newline would render as.
      rd.log(msg);
   }

   /**
    * Print a line to the console for live standalone progress. The trailing {@code CodeReview.IgnoreLine} marker keeps
    * this deliberate {@code System.out.println} from being flagged by the code-review checks (which otherwise warn on
    * stray print statements). Console-only - report content goes through {@link #log(String)} into {@link #rd}.
    */
   private void consolePrint(String msg) {
      System.out.println(msg); // CodeReview.IgnoreLine
   }

   @Override
   protected void performClean() {
      File wsDir = getWorkspaceRoot();
      if (wsDir == null) {
         // getWorkspaceRoot already logged the error into rd (the HTML report).
         return;
      }
      log("Workspace: " + wsDir.getAbsolutePath());
      log("Report only (changeFiles=false): " + !changeFiles);

      loadCache();
      log("Content cache: " + verdictCache.size() + " entry(ies) loaded from " + cacheFile().getAbsolutePath());
      log("");

      List<File> repos = findGitRepos(wsDir);
      if (repos.isEmpty()) {
         log("No git repos found in " + wsDir.getAbsolutePath());
         return;
      }

      log("Found " + repos.size() + " git repo(s):");
      for (File repo : repos) {
         log("  " + repo.getName());
      }
      log("");

      for (File repo : repos) {
         log("=== " + repo.getName() + " ===");
         // Compare against the branch's merge-base with its upstream so ALL unmerged commits are covered, not just
         // the last one. Falls back to HEAD~1 when no upstream is configured.
         String baseRef = resolveBaseRef(repo);
         log("  Base ref: " + baseRef);

         List<String> commits = getCommitsSince(repo, baseRef);
         if (commits.isEmpty()) {
            log("  No commits since base ref.");
            continue;
         }
         log("  Commits reviewed (" + commits.size() + "):");
         for (String c : commits) {
            log("    " + c);
         }

         // One `git diff --raw` per repo yields every modified .java file plus its old+new blob SHAs, so the
         // cache can be consulted without spawning any `git show`.
         List<DiffEntry> entries = getModifiedJavaEntries(repo, baseRef);
         if (entries.isEmpty()) {
            log("  No modified Java files since base ref.");
            continue;
         }
         log("  " + entries.size() + " modified Java file(s)");
         for (DiffEntry entry : entries) {
            processFile(repo, baseRef, entry);
         }
         log("");
      }

      saveCache();

      log("=== Summary ===");
      log("Reverted (non-functional only):    " + revertedCount);
      log("Kept (has functional changes):     " + keptCount);
      log("Cache hits (skipped git show):     " + cacheHits);
      log("Errors:                            " + rd.getNumErrors());
      log("");
      log("About the cache: each file's classification (revert / review / keep) is a pure function of its content");
      log("before and after, so it is cached by the pair of git blob SHAs (content hashes) for those two versions,");
      log("in " + cacheFile().getAbsolutePath() + ". A \"cache hit\" is a file whose exact before/after content was");
      log("classified on a prior run, so this run reused that result and skipped re-reading the file via git show -");
      log("the slow part. Because blob SHAs are pure content hashes, the cache stays valid across commits, squashes,");
      log("and rebases; an entry only becomes stale when a file's actual content changes. Delete the cache file to");
      log("force a full recompute.");
   }

   private List<File> findGitRepos(File wsDir) {
      List<File> repos = new ArrayList<>();
      File[] children = wsDir.listFiles();
      if (children == null) {
         return repos;
      }
      for (File child : children) {
         if (child.isDirectory() && new File(child, ".git").exists()) {
            repos.add(child);
         }
      }
      return repos;
   }

   private void processFile(File repoDir, String baseRef, DiffEntry entry) {
      String relativePath = entry.path;
      try {
         // Live console progress as each file is processed (report content still flows through log()/rd).
         consolePrint(shortName(relativePath));

         // New file (no old blob): nothing to revert - matches the old "KEEP (new file)" case.
         if (entry.isNewFile()) {
            if (debug) {
               log("  KEEP (new file): " + shortName(relativePath));
            }
            keptCount++;
            return;
         }

         // Cache is keyed on the (oldBlobSha, newBlobSha) content pair. A hit means we already classified this exact
         // pair of contents on a prior run - skip the two git show spawns + normalization entirely.
         String cacheKey = entry.cacheKey();
         Verdict verdict = verdictCache.get(cacheKey);
         String reason;
         if (verdict != null) {
            cacheHits++;
            // Reason text is not cached (cheap to omit); note that this was a cached classification.
            reason = "cached";
         } else {
            // Cache miss: read both versions and classify.
            String oldContent = gitShow(repoDir, baseRef, relativePath);
            String newContent = gitShow(repoDir, "HEAD", relativePath);
            if (oldContent == null || newContent == null) {
               rd.errorf("  ERROR (can't read old/new content): %s\n", relativePath);
               return;
            }
            verdict = classify(oldContent, newContent);
            reason = verdict == Verdict.KEEP ? "" : describeDifferences(oldContent, newContent);
            verdictCache.put(cacheKey, verdict);
         }

         applyVerdict(repoDir, baseRef, relativePath, verdict, reason);
      } catch (Exception ex) {
         rd.errorf("  ERROR processing %s: %s\n", relativePath, ex.getMessage());
      }
   }

   /**
    * Classify a change from its old and new content: KEEP (functional change) or REVERT (non-functional only). Pure
    * function of the two contents - safe to cache by content hash.
    * <p>
    * {@link #normalize(String)} strips imports and blank lines and trims each line, so if the two versions are equal
    * after normalization the only differences are imports (static conversion, sorting, wildcard collapse/expand, unused
    * removal), blank lines, and whitespace/formatting - all non-functional. Import changes are reverted here too: the
    * codebase compiles, so any import form is valid, and reverting keeps unrelated import churn out of the commit.
    */
   private Verdict classify(String oldContent, String newContent) {
      if (!normalize(oldContent).equals(normalize(newContent))) {
         return Verdict.KEEP;
      }
      return Verdict.REVERT;
   }

   /** Log/count the verdict and, for REVERT with changeFiles=true, perform the git checkout. */
   private void applyVerdict(File repoDir, String baseRef, String relativePath, Verdict verdict, String reason) {
      switch (verdict) {
         case KEEP:
            if (debug) {
               log("  KEEP (functional changes): " + shortName(relativePath));
            }
            keptCount++;
            return;
         case REVERT:
            if (!changeFiles) {
               log("  WOULD REVERT: " + shortName(relativePath) + " - Reason: " + reason);
               changedFiles.add(relativePath);
            } else {
               boolean success = gitCheckout(repoDir, baseRef, relativePath);
               if (success) {
                  log("  REVERTED: " + shortName(relativePath) + " - Reason: " + reason);
                  changedFiles.add(relativePath);
               } else {
                  rd.errorf("  ERROR reverting: %s\n", relativePath);
                  return;
               }
            }
            revertedCount++;
            return;
      }
   }

   /**
    * @return a human-readable reason describing what kind of non-functional differences were found (imports, blank
    * lines, whitespace/formatting), suitable to append to the revert log line.
    */
   private String describeDifferences(String oldContent, String newContent) {
      String[] oldLines = oldContent.split("\n");
      String[] newLines = newContent.split("\n");

      boolean hasWhitespace = false;
      boolean hasImportChange = false;
      boolean hasBlankLines = false;

      // Check if import sections differ
      String oldImports = extractImportBlock(oldLines);
      String newImports = extractImportBlock(newLines);
      if (!oldImports.equals(newImports)) {
         hasImportChange = true;
      }

      // Check blank line differences
      int oldBlanks = countBlankLines(oldLines);
      int newBlanks = countBlankLines(newLines);
      if (oldBlanks != newBlanks) {
         hasBlankLines = true;
      }

      // Whitespace/formatting: after removing imports, the raw (untrimmed) non-import content differs, but the
      // trimmed content is identical - so the residual delta is indentation / trailing whitespace / line endings.
      String oldNoImports = stripImports(oldContent);
      String newNoImports = stripImports(newContent);
      if (!oldNoImports.equals(newNoImports) && trimEachLine(oldNoImports).equals(trimEachLine(newNoImports))) {
         hasWhitespace = true;
      }

      List<String> reasons = new ArrayList<>();
      if (hasImportChange) {
         reasons.add("import changes");
      }
      if (hasBlankLines) {
         reasons.add("blank line changes");
      }
      if (hasWhitespace) {
         reasons.add("whitespace/formatting");
      }
      if (reasons.isEmpty()) {
         reasons.add("equivalent after normalization");
      }
      return String.join(", ", reasons);
   }

   /**
    * Shorten a repo-relative path to just {@code package.ClassName} for concise log lines. Takes everything after the
    * source root ({@code src/}, {@code src-gen/}, or {@code test/}), swaps {@code /} for {@code .}, and drops the
    * {@code .java} extension - e.g.
    * {@code plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/data/ArtifactReadable.java}
    * becomes {@code org.eclipse.osee.framework.core.data.ArtifactReadable}. Falls back to the bare file name if no
    * source-root segment is found.
    */
   private String shortName(String relativePath) {
      String p = relativePath.replace('\\', '/');
      for (String srcRoot : new String[] {"/src/", "/src-gen/", "/test/"}) {
         int idx = p.indexOf(srcRoot);
         if (idx >= 0) {
            String afterSrc = p.substring(idx + srcRoot.length());
            return afterSrc.replaceFirst("\\.java$", "").replace('/', '.');
         }
      }
      // No recognizable source root - just use the file name.
      return p.substring(p.lastIndexOf('/') + 1);
   }

   /**
    * Normalize content for comparison: strip trailing whitespace, normalize line endings, drop ALL blank lines, strip
    * imports, strip pure formatting differences. Blank lines are dropped entirely (not just collapsed) so that adding
    * or removing a single blank line - a non-functional change this tool is meant to revert - does not read as a
    * content difference. Blank-line counts are still surfaced separately in the reason text.
    */
   private String normalize(String content) {
      // Normalize line endings (literal replace - no regex compile)
      content = content.replace("\r\n", "\n").replace("\r", "\n");

      String[] lines = content.split("\n");
      StringBuilder sb = new StringBuilder();

      for (String line : lines) {
         String trimmed = line.trim();

         // Skip all import lines entirely — import changes are always non-functional
         // (reordering, wildcard collapse/expand, unused removal)
         if (trimmed.startsWith("import ")) {
            continue;
         }

         // Drop every blank line — blank-line add/remove is non-functional, so it must not affect the comparison.
         if (trimmed.isEmpty()) {
            continue;
         }

         // Keep only the logical content (trimmed)
         sb.append(trimmed).append("\n");
      }

      return sb.toString();
   }

   private String extractImportBlock(String[] lines) {
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         if (line.trim().startsWith("import ")) {
            sb.append(line.trim()).append("\n");
         }
      }
      return sb.toString();
   }

   /** @return content with all import lines removed and line endings normalized (blank lines and indentation kept). */
   private String stripImports(String content) {
      String[] lines = content.replace("\r\n", "\n").replace("\r", "\n").split("\n");
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         if (!line.trim().startsWith("import ")) {
            sb.append(line).append("\n");
         }
      }
      return sb.toString();
   }

   /** @return content with every line trimmed (isolates indentation/trailing-whitespace differences). */
   private String trimEachLine(String content) {
      StringBuilder sb = new StringBuilder();
      for (String line : content.split("\n")) {
         sb.append(line.trim()).append("\n");
      }
      return sb.toString();
   }

   private int countBlankLines(String[] lines) {
      int count = 0;
      for (String line : lines) {
         if (line.trim().isEmpty()) {
            count++;
         }
      }
      return count;
   }

   // =========================================================================
   // Git operations
   // =========================================================================

   /**
    * Resolve the ref to compare against: the merge-base of this branch with its configured upstream (so all unmerged
    * commits on the branch are covered). Detected at runtime via {@code @{u}} - not hard-coded to any remote name.
    * Falls back to {@code HEAD~1} when no upstream is configured or the merge-base can't be found.
    */
   private String resolveBaseRef(File repoDir) {
      String upstream = runGitLine(repoDir, "rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{u}");
      if (upstream == null || upstream.isBlank()) {
         return "HEAD~1";
      }
      String mergeBase = runGitLine(repoDir, "merge-base", upstream.trim(), "HEAD");
      if (mergeBase == null || mergeBase.isBlank()) {
         return "HEAD~1";
      }
      return mergeBase.trim();
   }

   /** @return short one-line descriptions of the commits in {@code baseRef..HEAD} (the range being reviewed). */
   private List<String> getCommitsSince(File repoDir, String baseRef) {
      List<String> commits = new ArrayList<>();
      for (String line : runGitLines(repoDir, "log", "--oneline", "--no-decorate", baseRef + "..HEAD")) {
         if (!line.isBlank()) {
            commits.add(line);
         }
      }
      return commits;
   }

   /**
    * One {@code git diff --raw --no-abbrev} per repo yields, for every modified {@code .java} file, the old and new
    * blob SHAs plus the path - all in a single process, so per-file {@code git show} spawns are only needed on a cache
    * miss. The raw format is:
    *
    * <pre>
    * :&lt;oldmode&gt; &lt;newmode&gt; &lt;oldsha&gt; &lt;newsha&gt; &lt;status&gt;\t&lt;path&gt;
    * </pre>
    */
   private List<DiffEntry> getModifiedJavaEntries(File repoDir, String baseRef) {
      List<DiffEntry> entries = new ArrayList<>();
      for (String line : runGitLines(repoDir, "diff", "--raw", "--no-abbrev", "--diff-filter=M", baseRef + "..HEAD",
         "--", "*.java")) {
         if (line.isBlank() || !line.startsWith(":")) {
            continue;
         }
         // Split the metadata (before the tab) from the path (after it).
         int tab = line.indexOf('\t');
         if (tab < 0) {
            continue;
         }
         String meta = line.substring(1, tab); // drop leading ':'
         String path = line.substring(tab + 1).trim();
         if (!path.endsWith(".java")) {
            continue;
         }
         // meta = "<oldmode> <newmode> <oldsha> <newsha> <status>"
         String[] parts = meta.trim().split("\\s+");
         if (parts.length < 4) {
            continue;
         }
         String oldSha = parts[2];
         String newSha = parts[3];
         entries.add(new DiffEntry(oldSha, newSha, path));
      }
      return entries;
   }

   /** A modified .java file from {@code git diff --raw}: its old + new blob SHAs and repo-relative path. */
   private static final class DiffEntry {
      final String oldSha;
      final String newSha;
      final String path;

      DiffEntry(String oldSha, String newSha, String path) {
         this.oldSha = oldSha;
         this.newSha = newSha;
         this.path = path;
      }

      /** @return true if there is no old blob (all-zero SHA), i.e. a newly added file. */
      boolean isNewFile() {
         return oldSha == null || oldSha.chars().allMatch(c -> c == '0');
      }

      /** @return the content-hash cache key for this change. */
      String cacheKey() {
         return oldSha + " " + newSha;
      }
   }

   // =========================================================================
   // Content-hash cache (blob-SHA keyed; survives commits/squashes/rebases)
   // =========================================================================

   /** @return the cache file under the CodeClean output folder. */
   private File cacheFile() {
      return new File(CodeQualityUtil.getOutputDir(CodeQualityUtil.CODE_CLEAN_DIR, "CodeClean"), "revert-cache.txt");
   }

   /** Load prior verdicts from {@link #cacheFile()}. Each line is {@code oldSha newSha VERDICT}. Best effort. */
   private void loadCache() {
      File file = cacheFile();
      if (!file.isFile()) {
         return;
      }
      try {
         for (String line : Lib.fileToString(file).split("\n")) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 3) {
               try {
                  verdictCache.put(parts[0] + " " + parts[1], Verdict.valueOf(parts[2]));
               } catch (IllegalArgumentException ex) {
                  // Unknown verdict token (older/newer format) - skip that line.
               }
            }
         }
      } catch (Exception ex) {
         // A missing/corrupt cache is harmless - just recompute this run.
         rd.warningf("Could not read verdict cache [%s]: %s\n", file.getAbsolutePath(), ex.getMessage());
      }
   }

   /** Persist the (now-updated) verdicts back to {@link #cacheFile()}. Best effort. */
   private void saveCache() {
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, Verdict> e : verdictCache.entrySet()) {
         sb.append(e.getKey()).append(" ").append(e.getValue().name()).append("\n");
      }
      try {
         Lib.writeStringToFile(sb.toString(), cacheFile());
      } catch (Exception ex) {
         rd.warningf("Could not write verdict cache [%s]: %s\n", cacheFile().getAbsolutePath(), ex.getMessage());
      }
   }

   /** Run a git command and return its first stdout line (trimmed), or null on error/empty. */
   private String runGitLine(File repoDir, String... args) {
      List<String> out = runGitLines(repoDir, args);
      return out.isEmpty() ? null : out.get(0);
   }

   /** Run a git command and return its stdout lines. Errors are logged to the console and yield an empty list. */
   private List<String> runGitLines(File repoDir, String... args) {
      List<String> lines = new ArrayList<>();
      try {
         List<String> command = new ArrayList<>();
         command.add("git");
         for (String a : args) {
            command.add(a);
         }
         ProcessBuilder pb = new ProcessBuilder(command);
         pb.directory(repoDir);
         pb.redirectErrorStream(true);
         Process process = pb.start();
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               lines.add(line);
            }
         }
         boolean finished = process.waitFor(30, TimeUnit.SECONDS);
         if (!finished) {
            process.destroyForcibly();
            rd.errorf("  ERROR: git timed out in %s\n", repoDir.getName());
            return new ArrayList<>();
         }
         if (process.exitValue() != 0) {
            // Non-zero (e.g. no upstream) is expected in some cases; keep quiet, caller handles empty/null.
            return new ArrayList<>();
         }
      } catch (Exception ex) {
         rd.errorf("  ERROR running git in %s: %s\n", repoDir.getName(), ex.getMessage());
         return new ArrayList<>();
      }
      return lines;
   }

   private String gitShow(File repoDir, String ref, String relativePath) {
      try {
         ProcessBuilder pb = new ProcessBuilder("git", "show", ref + ":" + relativePath);
         pb.directory(repoDir);
         Process process = pb.start();

         StringBuilder stdout = new StringBuilder();

         Thread errThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
               // Drain stderr so its buffer can't fill and block the process; the content itself is discarded.
               while (reader.readLine() != null) {
                  // discard
               }
            } catch (Exception ex) {
               /* ignore */ }
         });
         errThread.setDaemon(true);
         errThread.start();

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               stdout.append(line).append("\n");
            }
         }

         boolean finished = process.waitFor(15, TimeUnit.SECONDS);
         if (!finished) {
            process.destroyForcibly();
            return null;
         }
         if (process.exitValue() != 0) {
            return null;
         }
         return stdout.toString();
      } catch (Exception ex) {
         return null;
      }
   }

   /**
    * Revert a file to its state at the base ref: git checkout &lt;baseRef&gt; -- file. Leaves the reverted content as
    * an unstaged working-copy change.
    */
   private boolean gitCheckout(File repoDir, String baseRef, String relativePath) {
      try {
         ProcessBuilder pb = new ProcessBuilder("git", "checkout", baseRef, "--", relativePath);
         pb.directory(repoDir);
         pb.redirectErrorStream(true);
         Process process = pb.start();

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               log("    [git] " + line);
            }
         }

         boolean finished = process.waitFor(10, TimeUnit.SECONDS);
         if (!finished) {
            process.destroyForcibly();
            return false;
         }
         return process.exitValue() == 0;
      } catch (Exception ex) {
         rd.errorf("    ERROR: %s\n", ex.getMessage());
         return false;
      }
   }

}
