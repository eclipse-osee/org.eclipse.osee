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

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Shared base for the two code-quality families, {@link AbstractCodeReview} (read-only validators) and
 * {@link AbstractCodeClean} (mutating reorganizers/formatters). It owns the machinery both families need so the two
 * bases only add their family-specific behavior:
 * <ul>
 * <li>the {@link XResultData} result ({@link #rd}) and {@link #getResults()};</li>
 * <li>the {@link #breakAfterFile} early-stop hook;</li>
 * <li>file read + line-ending normalization ({@link #readFile(String)});</li>
 * <li>path shortening for reports ({@link #shortPath(String)});</li>
 * <li>compiled-once file-name pattern discovery ({@link #getFilePattern()} + the recursive walk);</li>
 * <li>workspace-root resolution anchored on the {@code plugins} folder;</li>
 * <li>a single canonical directory ignore list ({@link #getIgnoreDirs()}, overridable);</li>
 * <li>HTML report writing via {@link CodeQualityUtil} ({@link #writeAndOpenReport(XResultData)}).</li>
 * </ul>
 * <p>
 * <b>Discovery model.</b> {@link #getWorkspaceRoot()} resolves the directory that contains all the sibling repos
 * (two levels above the {@code plugins} folder this class runs from). {@link #findFiles()} then, for each sibling repo
 * under that root, descends into that repo's {@code plugins} directory only, matching file names against
 * {@link #getFilePattern()}. Root resolution is bounded to the {@code plugins} anchor and never depends on a repo or
 * container directory <i>name</i> (a developer may clone the repo under any name). Discovery, by contrast, is allowed
 * to fan out across all sibling repos under the resolved root.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractQual {

   protected final XResultData rd = new XResultData();
   protected Integer breakAfterFile = null;
   protected String basePath = "";

   /** The open-source repo directory name scanned when {@link #onlyOpenSource} is true. */
   protected static final String OPEN_SOURCE_REPO = "org.eclipse.osee";
   /**
    * When true (default), discovery scans ONLY the {@code org.eclipse.osee} repo under the workspace root, skipping
    * any sibling (e.g. proprietary) repos. Set false in a subclass to scan every sibling repo under the root.
    */
   protected boolean onlyOpenSource = true;

   /** Compiled once (lazily) from {@link #getFilePattern()} instead of recompiling per file during the tree walk. */
   private Pattern compiledFilePattern = null;
   /** Cached workspace root so it is resolved once per run, not on every call. */
   private File cachedWorkspaceRoot = null;

   /**
    * Canonical directories/path-substrings to skip during discovery. Anchoring discovery on each repo's
    * {@code plugins} folder already excludes most non-source trees (web, nebula, help, support, postgresql, etc.),
    * so this list only needs the noise that still lives under {@code plugins}. Subclasses that need a different set
    * (e.g. include the integration tests) override {@link #getIgnoreDirs()}.
    */
   private static final List<String> DEFAULT_IGNORE_DIRS = Arrays.asList( //
      ".git", "\\target\\", "\\bin", ".integration.tests." //
   );

   /** @return human-readable name for this run; used as the report title and {@code <name>.html} filename. */
   protected abstract String getName();

   // =========================================================================
   // Results
   // =========================================================================

   public XResultData getResults() {
      return rd;
   }

   // =========================================================================
   // File read / path helpers
   // =========================================================================

   /**
    * Read a file's contents as a string with line endings normalized to {@code \n} for consistent parsing. Returns
    * empty string (and logs an error into {@link #rd}) if the file cannot be read.
    */
   protected String readFile(String filePath) {
      try {
         String content = Lib.fileToString(new File(filePath));
         // Normalize line endings (literal replace - no regex compile).
         return content.replace("\r\n", "\n").replace("\r", "\n");
      } catch (Exception ex) {
         rd.errorf("Error reading file [%s]: %s\n", filePath, ex.getMessage());
         return "";
      }
   }

   /** @return a path shortened relative to {@link #basePath} (the resolved workspace root), when applicable. */
   protected String shortPath(String fullPath) {
      if (basePath != null && !basePath.isEmpty() && fullPath.startsWith(basePath)) {
         return fullPath.substring(basePath.length());
      }
      return fullPath;
   }

   /** @return just the file's simple name with the {@code .java} extension stripped. */
   protected String getClassName(String filePath) {
      return new File(filePath).getName().replace(".java", "");
   }

   // =========================================================================
   // Workspace-root resolution (anchored on the 'plugins' folder)
   // =========================================================================

   /**
    * Resolve the workspace root: the directory that contains all sibling repos. These classes live under
    * {@code <workspaceRoot>/<repoDir>/plugins/...}, so the root is the directory <b>two levels above</b> the
    * {@code plugins} folder this class was loaded from. The repo dir ({@code <repoDir>}) and the workspace dir may be
    * named anything - a developer can clone under any folder name - so resolution anchors purely on the structural
    * {@code plugins} folder and never matches a directory name. Returns {@code null} (and logs an error into
    * {@link #rd}) when no {@code plugins} ancestor is found, so callers degrade safely instead of scanning the wrong
    * tree.
    */
   protected File getWorkspaceRoot() {
      if (cachedWorkspaceRoot == null) {
         File location = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
         File pluginsDir = findPluginsAncestor(location);
         if (pluginsDir == null || pluginsDir.getParentFile() == null
            || pluginsDir.getParentFile().getParentFile() == null) {
            rd.errorf(
               "Could not resolve workspace root: no 'plugins' ancestor found above [%s]. Discovery cannot run.\n",
               location.getAbsolutePath());
            return null;
         }
         // plugins -> repoDir (1 up) -> workspaceRoot (2 up)
         cachedWorkspaceRoot = pluginsDir.getParentFile().getParentFile();
      }
      return cachedWorkspaceRoot;
   }

   /** Walk up from the given location to the nearest ancestor directory named {@code plugins}; null if none. */
   private File findPluginsAncestor(File from) {
      File dir = from.isDirectory() ? from : from.getParentFile();
      while (dir != null) {
         if (dir.getName().equals("plugins")) {
            return dir;
         }
         dir = dir.getParentFile();
      }
      return null;
   }

   /**
    * Derive the short name of the repo the given path lives in by stripping everything from the {@code org.eclipse.osee}
    * segment onward. This is name-based on purpose (used only for display/labeling, not for discovery) - guard the
    * result: if the strip leaves nothing or does not change the input, the expected repo layout was not present, so
    * this returns {@code null} and logs an error rather than handing back a misleading path.
    */
   protected String getRepoShortName(String fullPath) {
      String stripped = fullPath.replaceFirst("org.eclipse.osee.*$", "");
      if (stripped.isEmpty() || stripped.equals(fullPath)) {
         rd.errorf("Could not derive repo short name from [%s] (no 'org.eclipse.osee' segment).\n", fullPath);
         return null;
      }
      return stripped;
   }

   // =========================================================================
   // File discovery (pattern-based walk across sibling repos' plugins dirs)
   // =========================================================================

   /** @return regex the file's simple name must match to be discovered (e.g. {@code "XX.*Widget\\.java"}). */
   protected abstract String getFilePattern();

   /** @return the compiled {@link #getFilePattern()}, compiled once and reused across the whole tree walk. */
   protected Pattern filePattern() {
      if (compiledFilePattern == null) {
         compiledFilePattern = Pattern.compile(getFilePattern());
      }
      return compiledFilePattern;
   }

   /** @return path substrings that cause a directory to be skipped during discovery. */
   protected List<String> getIgnoreDirs() {
      return DEFAULT_IGNORE_DIRS;
   }

   /** Log the current {@link #onlyOpenSource} setting and what it means into {@link #rd}. */
   protected void logOnlyOpenSource() {
      if (onlyOpenSource) {
         rd.logf("onlyOpenSource = TRUE - scanning only the '%s' repo; sibling (e.g. proprietary) repos are "
            + "skipped. Set onlyOpenSource = false to scan every sibling repo under the workspace root.\n\n",
            OPEN_SOURCE_REPO);
      } else {
         rd.logf("onlyOpenSource = FALSE - scanning ALL sibling repos under the workspace root (including any "
            + "proprietary repos present).\n\n");
      }
   }

   /**
    * Default discovery: resolve the workspace root, then for each sibling repo under it descend into that repo's
    * {@code plugins} directory and collect files whose simple name matches {@link #getFilePattern()}. Sets
    * {@link #basePath} to the workspace root so {@link #shortPath(String)} works. Returns an empty list (with an error
    * already logged) when the root cannot be resolved.
    */
   protected List<String> findFiles() {
      File workspaceRoot = getWorkspaceRoot();
      if (workspaceRoot == null || !workspaceRoot.isDirectory()) {
         return new ArrayList<>();
      }
      basePath = workspaceRoot.getAbsolutePath();
      rd.logf("Workspace root: %s\n", basePath);

      List<String> results = new ArrayList<>();
      File[] repos = workspaceRoot.listFiles();
      if (repos == null) {
         return results;
      }
      for (File repo : repos) {
         if (!repo.isDirectory()) {
            continue;
         }
         // By default scan only the open-source repo; skip proprietary siblings unless onlyOpenSource is false.
         if (onlyOpenSource && !repo.getName().equals(OPEN_SOURCE_REPO)) {
            continue;
         }
         File pluginsDir = new File(repo, "plugins");
         if (pluginsDir.isDirectory()) {
            rd.logf("Scanning repo plugins dir: %s\n", pluginsDir.getAbsolutePath());
            findFilesByPattern(pluginsDir, results);
         }
      }
      return results;
   }

   /**
    * Recursively collect files under {@code dir} whose simple name matches {@link #filePattern()}, skipping any
    * directory whose absolute path contains one of {@link #getIgnoreDirs()}.
    */
   protected List<String> findFilesByPattern(File dir, List<String> results) {
      File[] fileList = dir.listFiles();
      if (fileList == null) {
         return results;
      }
      for (File file : fileList) {
         if (file.isDirectory()) {
            boolean skip = false;
            for (String ignore : getIgnoreDirs()) {
               if (file.getAbsolutePath().contains(ignore)) {
                  skip = true;
                  break;
               }
            }
            if (!skip) {
               findFilesByPattern(file, results);
            }
         } else if (filePattern().matcher(file.getName()).matches()) {
            results.add(file.getAbsolutePath());
         }
      }
      return results;
   }

   // =========================================================================
   // Reporting
   // =========================================================================

   /**
    * Write the given results as HTML to the family's output folder (via {@link CodeQualityUtil}) using {@link #getName()}
    * as the file name, then open it in the browser. Silently no-ops on failure (e.g. headless). Subclasses supply the
    * output-dir constant and tool label via {@link #getOutputDirName()} / {@link #getToolLabel()}.
    */
   protected void writeAndOpenReport(XResultData reportRd) {
      File outFile = CodeQualityUtil.getReportFile(getOutputDirName(), getToolLabel(), getName());
      try {
         Lib.writeStringToFile(reportRd.getHtml(), outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // Silently fail if the browser can't open (e.g. headless test).
      }
   }

   /** @return the {@link CodeQualityUtil} output-dir constant for this family (CodeClean vs CodeReview). */
   protected abstract String getOutputDirName();

   /** @return short label for this family used in the output folder's README (e.g. "CodeClean" / "CodeReview"). */
   protected abstract String getToolLabel();

}
