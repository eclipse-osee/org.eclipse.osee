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
package org.eclipse.osee.ats.ide.integration.tests.coderev.packet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.integration.tests.coderev.CodeQualityUtil;

/**
 * THROWAWAY migration tool (sibling of {@code ReportBlamChanges} / {@code ReportXWidgetChanges}). Scans two workspaces
 * (ggg = new, try = old) for Work Definition source files - {@code WorkDef*.java} classes - and generates the Work
 * Definition section of the review packet in {@code ~/CodeXWidgetReview/}: a Rename report, a Diff report (old vs new
 * source with portable highlighted copies + relative links), and a static Testing report. Delete after the Work
 * Definition migration is reviewed and merged.
 *
 * @author Donald G. Dunne
 */
public class ReportWorkDefChange {

   /** When true, scan ONLY the {@code org.eclipse.osee} repo under each workspace root (skip proprietary siblings). */
   private static final boolean onlyOpenSource = false;
   private static final String OPEN_SOURCE_REPO = "org.eclipse.osee";

   /** [baseDir, repoName] - each baseDir is scanned for subdirs containing a plugins folder. */
   private static final String[][] WORKSPACES = { //
      {"C:/Tools/git_ggg", "ggg"}, //
      {"C:/Tools/git_try", "try"} //
   };

   private static final Pattern CLASS_DECL =
      Pattern.compile("(?:public\\s+|protected\\s+|private\\s+)?(abstract\\s+)?class\\s+(\\w+)\\s+extends\\s+(\\w+)");

   public static void main(String[] args) throws IOException {
      System.out.println("=== Work Definition Change Report ===");
      System.out.println("Output directory: " + jsonDir().getAbsolutePath());
      System.out.println();

      for (String[] workspace : WORKSPACES) {
         String baseDir = workspace[0];
         String repoName = workspace[1];
         Path basePath = Paths.get(baseDir);
         if (!Files.exists(basePath)) {
            System.err.println("WARNING: Workspace not found, skipping: " + baseDir);
            continue;
         }
         List<Path> pluginsDirs = findPluginsDirs(basePath);
         if (pluginsDirs.isEmpty()) {
            System.err.println("WARNING: No plugins directories found under: " + baseDir);
            continue;
         }
         Path outputPath = new File(jsonDir(), String.format("workdef_classes_%s.json", repoName)).toPath();
         System.out.println("Repo: " + repoName + " (" + pluginsDirs.size() + " plugins dirs) -> " + outputPath);
         generateMetrics(pluginsDirs, repoName, outputPath);
      }

      // No rename report for Work Definitions - they keep their names across the migration (nothing to show).
      System.out.println("Generating code diff report...");
      generateCodeDiff();
      System.out.println("Generating testing report...");
      generateTestingReport();
      System.out.println("All workspaces processed.");
   }

   /** @return the shared ~/CodeXWidgetReview packet folder (created on first use with a README). */
   private static File jsonDir() {
      return CodeQualityUtil.getOutputDir(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReview");
   }

   private static List<Path> findPluginsDirs(Path baseDir) {
      List<Path> result = new ArrayList<>();
      File[] children = baseDir.toFile().listFiles();
      if (children == null) {
         return result;
      }
      for (File child : children) {
         if (child.isDirectory()) {
            if (onlyOpenSource && !child.getName().equals(OPEN_SOURCE_REPO)) {
               continue;
            }
            File pluginsDir = new File(child, "plugins");
            if (pluginsDir.exists() && pluginsDir.isDirectory()) {
               result.add(pluginsDir.toPath());
            }
         }
      }
      return result;
   }

   /**
    * Scan one workspace's plugins dirs for Work Definition source files, extract per-WorkDef data, write a portable
    * highlighted copy of each, and emit the {@code workdef_classes_<repo>.json} file. A file qualifies as a WorkDef if
    * it carries {@code @Component(service = IAtsWorkDefinitionBuilder.class)} (new, post-OSGI-migration signal),
    * extends an {@code Abstract*WorkDef} base, or calls {@code super(AtsWorkDefinitionTokens...)} - covering the new
    * tree, the old (pre-migration) tree, and proprietary repos. Abstract bases are excluded below.
    */
   private static void generateMetrics(List<Path> pluginsDirs, String repoName, Path outputPath) throws IOException {
      String copySubdir = "try".equals(repoName) ? "old_workdef" : "new_workdef";
      File copyDir = new File(jsonDir(), copySubdir);
      copyDir.mkdirs();

      Map<String, Path> classToFile = new HashMap<>();
      Map<String, Boolean> classIsAbstract = new HashMap<>();

      for (Path root : pluginsDirs) {
         Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               String fileName = file.getFileName().toString();
               if (!fileName.endsWith(".java")) {
                  return FileVisitResult.CONTINUE;
               }
               // Never count the coderev tooling itself: these scripts/reports contain the detection strings
               // (@Component(service = IAtsWorkDefinitionBuilder.class), super(AtsWorkDefinitionTokens, etc.) as
               // literals and would otherwise false-match.
               if (file.toString().replace('\\', '/').contains("/coderev/")) {
                  return FileVisitResult.CONTINUE;
               }
               String content = Files.readString(file);
               // Detect a real Work Definition across both eras and all repos:
               //  - NEW (post OSGI migration): carries @Component(service = IAtsWorkDefinitionBuilder.class)
               //  - OLD (pre migration) / proprietary: extends an Abstract*WorkDef base, or calls
               //    super(AtsWorkDefinitionTokens...) in its constructor.
               boolean isWorkDef = content.contains("@Component(service = IAtsWorkDefinitionBuilder.class")
                  || content.matches("(?s).*extends\\s+Abstract\\w*WorkDef(?![A-Za-z0-9_]).*")
                  || content.contains("super(AtsWorkDefinitionTokens");
               if (!isWorkDef) {
                  return FileVisitResult.CONTINUE;
               }
               String className = fileName.replace(".java", "");
               // Exclude the abstract bases themselves (e.g. AbstractWorkDef, AbstractDemoWorkDef) - they are not
               // registerable work defs, just parents in the extends chain.
               boolean isAbstract = className.contains("Abstract");
               if (!isAbstract) {
                  Matcher m = CLASS_DECL.matcher(content);
                  while (m.find()) {
                     if (m.group(2).equals(className)) {
                        isAbstract = m.group(1) != null;
                        break;
                     }
                  }
               }
               if (isAbstract) {
                  return FileVisitResult.CONTINUE;
               }
               classToFile.put(className, file);
               classIsAbstract.put(className, false);
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
               String dirName = dir.getFileName().toString();
               if (dirName.equals("bin") || dirName.equals("build") || dirName.equals(".git") || dirName.equals(
                  "target")) {
                  return FileVisitResult.SKIP_SUBTREE;
               }
               return FileVisitResult.CONTINUE;
            }
         });
      }

      List<String> sorted = new ArrayList<>(classToFile.keySet());
      sorted.sort(String::compareTo);

      try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
         writer.println("{");
         writer.println("  \"classes\": [");
         for (int i = 0; i < sorted.size(); i++) {
            String className = sorted.get(i);
            Path filePath = classToFile.get(className);
            String fullPath = filePath.toAbsolutePath().normalize().toString().replace("\\", "/");
            String sideLabel = "old_workdef".equals(copySubdir) ? "old" : "new";
            String outName = writeHighlightedCopy(filePath, copyDir, className, sideLabel);
            String copyPath = outName == null ? null : copySubdir + "/" + outName;
            boolean isAbstract = classIsAbstract.getOrDefault(className, false);
            long lineCount = countSourceLines(filePath);

            // WorkDef diff rows: constructors + the build() method (the heart of a WorkDef - where widgets/states are
            // declared) + an optional custom header helper. Any may be absent (null).
            String constructorCode = String.join("\n\n", extractConstructors(filePath, className));
            String buildCode = extractMethod(filePath, "build");

            writer.println("    {");
            writer.println(jsonField("name", className));
            writer.println(jsonField("path", fullPath));
            writer.println(jsonField("copyPath", copyPath));
            writer.println(String.format("      \"isAbstract\": %s,", isAbstract));
            writer.println(String.format("      \"lineCount\": %d,", lineCount));
            writer.println(jsonField("constructorCode", constructorCode.isEmpty() ? null : constructorCode));
            // last field: no trailing comma
            writer.println(
               String.format("      \"buildCode\": %s", buildCode == null ? "null" : jsonString(buildCode)));
            writer.print("    }");
            writer.println(i < sorted.size() - 1 ? "," : "");
         }
         writer.println("  ]");
         writer.println("}");
      }
      System.out.println("Found " + sorted.size() + " WorkDef classes in " + repoName + ".");
   }

   // =========================================================================
   // Rename / change report
   // =========================================================================

   /**
    * Resolve old-&gt;new WorkDef rename pairs. Uses the curated {@link ReportXWidgetMapping} map (which contains the
    * WorkDef model-class renames, e.g. WidgetDefinition-&gt;WidgetDef) first, then same-name survivors are handled by
    * the caller. Matched names are removed from the passed-in added/removed sets. Returns oldName -&gt; newName.
    */
   private static Map<String, String> resolveRenames(Set<String> added, Set<String> removed) {
      Map<String, String> renames = new HashMap<>();
      Set<String> renamedOld = new HashSet<>();
      Set<String> renamedNew = new HashSet<>();
      Map<String, String> curated = ReportXWidgetMapping.getMapping();
      for (Map.Entry<String, String> e : curated.entrySet()) {
         String oldName = e.getKey();
         String newName = e.getValue();
         if (removed.contains(oldName) && added.contains(newName)) {
            renames.put(oldName, newName);
            renamedOld.add(oldName);
            renamedNew.add(newName);
         } else if (removed.contains(oldName) && !renames.containsKey(oldName)) {
            renames.put(oldName, newName);
         }
      }
      removed.removeAll(renamedOld);
      added.removeAll(renamedNew);
      return renames;
   }

   // =========================================================================
   // Code diff report
   // =========================================================================

   private static void generateCodeDiff() throws IOException {
      File outDir = jsonDir();
      Path gggJson = new File(outDir, "workdef_classes_ggg.json").toPath();
      Path tryJson = new File(outDir, "workdef_classes_try.json").toPath();
      if (!Files.exists(gggJson) || !Files.exists(tryJson)) {
         System.err.println("Cannot generate code diff - one or both JSON files missing.");
         return;
      }
      Map<String, Map<String, String>> ggg = parseJsonClasses(Files.readString(gggJson));
      Map<String, Map<String, String>> old = parseJsonClasses(Files.readString(tryJson));

      Set<String> added = new HashSet<>(ggg.keySet());
      added.removeAll(old.keySet());
      Set<String> removed = new HashSet<>(old.keySet());
      removed.removeAll(ggg.keySet());
      Set<String> common = new HashSet<>(ggg.keySet());
      common.retainAll(old.keySet());
      Map<String, String> renames = resolveRenames(added, removed);

      java.util.TreeMap<String, String> newToOld = new java.util.TreeMap<>();
      for (Map.Entry<String, String> e : renames.entrySet()) {
         if (old.containsKey(e.getKey()) && ggg.containsKey(e.getValue())) {
            newToOld.put(e.getValue(), e.getKey());
         }
      }
      for (String name : common) {
         newToOld.put(name, name);
      }

      StringBuilder html = new StringBuilder();
      html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>WorkDef Code Diff</title>");
      html.append(PacketStyle.highlighterCss());
      html.append(PacketStyle.highlighterScript());
      html.append(diffReportHeadExtras());
      html.append("</head><body>");
      html.append("<h1>Work Definition Code Diff</h1>");
      html.append(diffHowToUse());

      html.append("<div class='summary'><b>Things to note</b><ol>");
      html.append(
         "<li>Each Work Definition now <b>self-registers via OSGi</b> - annotated "
            + "<code>@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)</code> with a default "
            + "(no-arg) constructor (abstract bases are not), replacing the old provider classes that enumerated "
            + "WorkDefs in code. Demo-only WorkDefs gate themselves with "
            + "<code>isApplicable()</code> (<code>DemoUtil.isDemoDatabase()</code>).</li>");
      html.append(
         "<li>Work Definitions now define widgets by <b>attribute type</b> (<code>AttrWidgetDef</code>) with " + "<code>WidgetId</code> tokens and <code>XOption</code>s instead of string widget names like " + "<code>\"XTextDam\"</code>; labels/options resolve from the attribute type, so most labels disappear.</li>");
      html.append(
         "<li>Shared header helpers (e.g. <code>addDemoCustomHeader(bld)</code>) replace per-workdef header " + "layout; <code>CompositeLayoutItem</code> handles multi-column rows.</li>");
      html.append(
         "<li>Verbose <code>...Definition</code> classes were replaced (e.g. " + "<code>SignByAndDateWidgetDefinition</code>&rarr;<code>SignDateWidgetDef</code>).</li>");
      html.append("<li>Everything is AUTO_SAVE - edits persist immediately; no dirty state.</li>");
      html.append("<li><b>Anything that can be auto-detected has been</b>, and is only specified when it differs "
         + "from the default:<ul>");
      html.append("<li><code>FILL_VERT</code> (fill vertically) and <code>HORZ_LABEL</code> (horizontal label) are "
         + "now the default, so those have been removed.</li>");
      html.append("<li>The <code>XWidget</code> name is almost always auto-detected.</li>");
      html.append("<li>Most entries are just the <code>AttributeType</code>, which specifies everything needed to "
         + "determine the widget and its <code>XOption</code>s.</li>");
      html.append("<li><code>andColor</code> is auto-set to the default and only specified when it differs.</li>");
      html.append("</ul></li>");
      html.append("</ol></div>");

      for (Map.Entry<String, String> pair : newToOld.entrySet()) {
         String newName = pair.getKey();
         String oldName = pair.getValue();
         Map<String, String> oldData = old.get(oldName);
         Map<String, String> newData = ggg.get(newName);
         String heading = oldName.equals(newName) ? newName : newName + "  (was: " + oldName + ")";
         html.append("<h2>").append(escapeHtml(heading)).append("</h2>");
         appendFileLink(html, "Old", oldData.get("copyPath"), oldData.get("path"));
         appendFileLink(html, "New", newData.get("copyPath"), newData.get("path"));
         if ("true".equals(newData.get("isAbstract"))) {
            html.append("<p class='detail'>(abstract base class)</p>");
            continue;
         }
         // Whole-file side by side: a WorkDef has no separate methods worth splitting out, so show the entire old and
         // new source next to each other.
         String oldSrc = readSource(oldData.get("path"));
         String newSrc = readSource(newData.get("path"));
         html.append("<table><tr><th>Old Code</th><th>New Code</th></tr>");
         html.append("<tr><td><pre><code class='language-java'>").append(escapeHtml(oldSrc))
            .append("</code></pre></td>");
         html.append("<td><pre><code class='language-java'>").append(escapeHtml(newSrc))
            .append("</code></pre></td></tr>");
         html.append("</table>");
      }
      html.append("</body></html>");

      File reportFile = new File(outDir, "XWidgets_WorkDefReview_Diff.html");
      Files.writeString(reportFile.toPath(), html.toString());
      System.out.println("Code diff written to: " + reportFile.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(reportFile);
      } catch (Exception ex) {
         // ignore
      }
   }

   private static String diffHowToUse() {
      return "<div class='summary'><b>How to use</b><ul>"
         + "<li>Each Work Definition shows its <b>entire old and new source</b> side by side - there are no separate "
         + "methods to break out, so just read the two files against each other.</li>"
         + "<li><b>Old</b>/<b>New</b> links open the full highlighted source (stored in <code>old_workdef/</code> / "
         + "<code>new_workdef/</code> beside this file - works wherever unzipped).</li>"
         + "<li>Runs locally in your browser - no data leaves this file.</li></ul></div>";
   }

   /** Read the full source text at the given absolute path, or an empty string if it is missing/unreadable. */
   private static String readSource(String path) {
      if (path == null) {
         return "";
      }
      try {
         return Files.readString(Paths.get(path));
      } catch (IOException ex) {
         return "";
      }
   }

   // =========================================================================
   // Testing report (static content)
   // =========================================================================

   private static void generateTestingReport() {
      StringBuilder h = new StringBuilder();
      h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      h.append("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
      h.append("<title>Work Definition Testing</title>");
      h.append(PacketStyle.pageCss());
      h.append("<style>.box{background:var(--soft);border-left:3px solid var(--agile);padding:10px 16px;"
         + "border-radius:4px;margin-bottom:20px;}</style>");
      h.append("</head><body><div class='wrap'>");
      h.append("<h1>Work Definition Testing</h1>");
      h.append(
         "<div class='box'>Work Definitions were simplified to define widgets by <b>attribute type</b> " + "(<code>AttrWidgetDef</code>) with <code>WidgetId</code> tokens and <code>XOption</code>s instead of string " + "widget names. The string widget form (<code>WidgetDef(attrType, \"XSomethingDam\", ...)</code>) was fully " + "eliminated. Everything is AUTO_SAVE. Each WorkDef now <b>self-registers via OSGi</b> "
            + "(<code>@Component(service = IAtsWorkDefinitionBuilder.class)</code>), replacing the old in-code "
            + "provider enumeration.</div>");

      h.append("<h2>Tests performed</h2><ul>");
      h.append(
         "<li><b><code>TestingOpenWorkflowSamples</code></b> (AtsAdmin nav) - iterates all Work Definitions, " + "finds one example workflow per WorkDef, and opens each in <b>both</b> the Workflow Editor and the " + "Artifact Editor, reporting per-editor errors into an HTML report (<code>OpenWorkDefExamples.html</code>). " + "The systematic \"every WorkDef renders\" smoke test.</li>");
      h.append(
         "<li><b><code>WorkDefTeamDemoXWidgetTest</code></b> - a Work Definition built to exercise every widget " + "type / <code>XOption</code> (widget-coverage showcase).</li>");
      h.append(
         "<li><b>Transition / required-widget fixtures</b> under <code>ats.core/.../demo/test/</code> " + "(<code>...ManagerTest*</code>) verify transition, required-widget, and review behavior.</li>");
      h.append("</ul>");

      h.append("<h2>Test / debug features added to the workbench</h2><ul>");
      h.append(
         "<li><b>Testing - Open Sample Workflows</b> (nav) - opens the Workflow Editor and Artifact Editor for " + "one example of each WorkDef in the database.</li>");
      h.append("<li><b>Testing - Validate Work Definitions</b> (nav) - runs a WorkDef validation report.</li>");
      h.append(
         "<li><b>Testing - Open XWidget Hierarchy View</b> (<code>TestingXWidgetHierarchyView</code>) - lists every "
            + "OSGi-registered widget with its attribute-type provider(s) and default <code>XOption</code>s, so you "
            + "can see what a WorkDef's <code>AttrWidgetDef</code> will resolve to.</li>");
      h.append(
         "<li><b>Testing - Create Action Using All Actionable Items</b> (Admin) - creates an action across all " + "actionable items, useful for generating sample workflows to inspect.</li>");
      h.append("</ul>");

      h.append("<p class='note'><a href='XWidget_Overview.html'>&larr; Back to the review overview</a></p>");
      h.append("</div></body></html>");

      File out = new File(jsonDir(), "XWidgets_WorkDefReview_Testing.html");
      try {
         Files.writeString(out.toPath(), h.toString());
         System.out.println("WorkDef testing report written to: " + out.getAbsolutePath());
         java.awt.Desktop.getDesktop().open(out);
      } catch (Exception ex) {
         // ignore
      }
   }

   // =========================================================================
   // Shared helpers (copied from ReportBlamChanges / ReportXWidgetChanges; throwaway migration tooling)
   // =========================================================================

   private static String extractMethod(Path filePath, String methodName) throws IOException {
      String content = Files.readString(filePath);
      Pattern methodDeclPattern =
         Pattern.compile("(?:@Override\\s+)?(?:public|protected|private)\\s+[\\w<>\\[\\],\\s]+\\s+" + Pattern.quote(
            methodName) + "\\s*\\(");
      Matcher declMatcher = methodDeclPattern.matcher(content);
      if (!declMatcher.find()) {
         return null;
      }
      int sigIndex = declMatcher.start();
      int methodStart = sigIndex;
      int lineStart = content.lastIndexOf('\n', sigIndex);
      if (lineStart == -1) {
         lineStart = 0;
      }
      String prefix = content.substring(Math.max(0, lineStart - 40), sigIndex).trim();
      if (prefix.endsWith("@Override")) {
         methodStart = content.lastIndexOf("@Override", sigIndex);
      }
      int braceStart = content.indexOf('{', sigIndex);
      if (braceStart == -1) {
         return null;
      }
      int depth = 0;
      int methodEnd = -1;
      for (int i = braceStart; i < content.length(); i++) {
         char c = content.charAt(i);
         if (c == '{') {
            depth++;
         } else if (c == '}') {
            depth--;
            if (depth == 0) {
               methodEnd = i + 1;
               break;
            }
         }
      }
      if (methodEnd == -1) {
         return null;
      }
      return content.substring(methodStart, methodEnd).strip();
   }

   private static List<String> extractConstructors(Path filePath, String className) throws IOException {
      String content = Files.readString(filePath);
      List<String> constructors = new ArrayList<>();
      String searchToken = className + "(";
      int searchFrom = 0;
      while (true) {
         int sigIndex = content.indexOf(searchToken, searchFrom);
         if (sigIndex == -1) {
            break;
         }
         int lineStart = content.lastIndexOf('\n', sigIndex);
         if (lineStart == -1) {
            lineStart = 0;
         }
         String linePrefix = content.substring(lineStart, sigIndex).trim();
         if (linePrefix.contains("class ") || linePrefix.contains("new ") || linePrefix.contains("return ")) {
            searchFrom = sigIndex + 1;
            continue;
         }
         if (linePrefix.equals("public") || linePrefix.equals("protected") || linePrefix.equals(
            "private") || linePrefix.isEmpty()) {
            int braceStart = content.indexOf('{', sigIndex);
            if (braceStart == -1) {
               searchFrom = sigIndex + 1;
               continue;
            }
            int depth = 0;
            int methodEnd = -1;
            for (int i = braceStart; i < content.length(); i++) {
               char c = content.charAt(i);
               if (c == '{') {
                  depth++;
               } else if (c == '}') {
                  depth--;
                  if (depth == 0) {
                     methodEnd = i + 1;
                     break;
                  }
               }
            }
            if (methodEnd != -1) {
               constructors.add(content.substring(lineStart + 1, methodEnd).strip());
            }
            searchFrom = methodEnd != -1 ? methodEnd : sigIndex + 1;
         } else {
            searchFrom = sigIndex + 1;
         }
      }
      return constructors;
   }

   private static long countSourceLines(Path filePath) throws IOException {
      long sloc = 0;
      boolean inBlockComment = false;
      for (String raw : Files.readAllLines(filePath)) {
         String line = raw.trim();
         if (line.isEmpty()) {
            continue;
         }
         boolean hasCode = false;
         int i = 0;
         while (i < line.length()) {
            if (inBlockComment) {
               int end = line.indexOf("*/", i);
               if (end < 0) {
                  i = line.length();
               } else {
                  inBlockComment = false;
                  i = end + 2;
               }
            } else {
               if (line.startsWith("//", i)) {
                  break;
               }
               if (line.startsWith("/*", i)) {
                  inBlockComment = true;
                  i += 2;
               } else {
                  if (!Character.isWhitespace(line.charAt(i))) {
                     hasCode = true;
                  }
                  i++;
               }
            }
         }
         if (hasCode) {
            sloc++;
         }
      }
      return sloc;
   }

   private static String writeHighlightedCopy(Path source, File copyDir, String className, String sideLabel) {
      String outName = className + ".java.html";
      try {
         String code = Files.readString(source);
         StringBuilder html = new StringBuilder();
         html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>").append(escapeHtml(className)).append(
            ".java</title>");
         html.append(PacketStyle.highlighterCss());
         html.append(
            "<style>body{font-family:-apple-system,'Segoe UI',sans-serif;margin:16px;color:#1f2328;} "
               + "h3{color:#1f6feb;} pre{font-size:12px;} "
               + "pre code{font-family:Consolas,'Courier New',monospace;white-space:pre;}</style>");
         html.append(PacketStyle.highlighterScript());
         html.append("</head><body>");
         html.append("<h3>").append(escapeHtml(className)).append(
            ".java <span style='color:#888;font-weight:normal'>(").append(escapeHtml(sideLabel)).append(
               ")</span></h3>");
         html.append("<pre><code class='language-java'>").append(escapeHtml(code)).append("</code></pre>");
         html.append("</body></html>");
         Files.writeString(new File(copyDir, outName).toPath(), html.toString());
         return outName;
      } catch (IOException ex) {
         return null;
      }
   }

   private static String jsonString(String value) {
      StringBuilder sb = new StringBuilder(value.length() + 20);
      sb.append('"');
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         switch (c) {
            case '"':
               sb.append("\\\"");
               break;
            case '\\':
               sb.append("\\\\");
               break;
            case '\n':
               sb.append("\\n");
               break;
            case '\r':
               sb.append("\\r");
               break;
            case '\t':
               sb.append("\\t");
               break;
            default:
               sb.append(c);
         }
      }
      sb.append('"');
      return sb.toString();
   }

   private static String jsonField(String key, String value) {
      return String.format("      \"%s\": %s,", key, value == null ? "null" : jsonString(value));
   }

   private static Map<String, Map<String, String>> parseJsonClasses(String json) throws IOException {
      Map<String, Map<String, String>> result = new HashMap<>();
      JsonNode root = new ObjectMapper().readTree(json);
      JsonNode classes = root.get("classes");
      if (classes == null || !classes.isArray()) {
         return result;
      }
      for (JsonNode entry : classes) {
         JsonNode nameNode = entry.get("name");
         if (nameNode == null || nameNode.isNull()) {
            continue;
         }
         Map<String, String> fields = new HashMap<>();
         entry.fields().forEachRemaining(field -> {
            JsonNode value = field.getValue();
            if (value != null && !value.isNull()) {
               fields.put(field.getKey(), value.asText());
            }
         });
         result.put(nameNode.asText(), fields);
      }
      return result;
   }

   private static long sumLines(Map<String, Map<String, String>> classes) {
      long total = 0;
      for (Map<String, String> data : classes.values()) {
         if (data == null || data.get("lineCount") == null) {
            continue;
         }
         try {
            total += Long.parseLong(data.get("lineCount"));
         } catch (NumberFormatException ex) {
            // ignore
         }
      }
      return total;
   }

   private static String nvl(String value) {
      return value == null ? "" : value;
   }

   private static String escapeHtml(String s) {
      return PacketStyle.escapeHtml(s);
   }

   private static String formatCode(String code) {
      String[] lines = code.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
      StringBuilder sb = new StringBuilder();
      int depth = 0;
      for (String raw : lines) {
         String line = raw.trim();
         if (line.isEmpty()) {
            sb.append("\n");
            continue;
         }
         if (line.startsWith("}")) {
            depth = Math.max(0, depth - 1);
         }
         for (int i = 0; i < depth; i++) {
            sb.append("   ");
         }
         sb.append(line).append("\n");
         int delta = count(line, '{') - count(line, '}');
         if (line.startsWith("}")) {
            delta += 1;
         }
         depth = Math.max(0, depth + delta);
      }
      return sb.toString().replaceAll("\n+$", "\n");
   }

   private static int count(String s, char c) {
      int n = 0;
      for (int i = 0; i < s.length(); i++) {
         if (s.charAt(i) == c) {
            n++;
         }
      }
      return n;
   }

   private static String normalizeCode(String code) {
      String[] lines = code.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line.replaceAll("\\s+$", "")).append("\n");
      }
      return sb.toString().replaceAll("\n+$", "\n");
   }

   private static void appendDiffRow(StringBuilder html, String label, String oldCode, String newCode) {
      html.append("<tr><td class='label'>").append(escapeHtml(label)).append("</td>");
      if (oldCode == null && newCode == null) {
         html.append("<td colspan='2' class='unchanged'>(none)</td></tr>");
         return;
      }
      html.append("<td><pre><code class='language-java'>").append(
         oldCode == null ? "" : escapeHtml(formatCode(oldCode))).append("</code></pre></td>");
      if (oldCode != null && newCode != null && normalizeCode(oldCode).equals(normalizeCode(newCode))) {
         html.append("<td class='unchanged'>NO_CHANGE</td></tr>");
         return;
      }
      html.append("<td><pre><code class='language-java'>").append(
         newCode == null ? "" : escapeHtml(formatCode(newCode))).append("</code></pre></td>");
      html.append("</tr>");
   }

   private static void appendFileLink(StringBuilder html, String label, String copyPath, String fullPath) {
      String href;
      String visible;
      if (copyPath != null) {
         href = copyPath.replace(" ", "%20");
         visible = "./" + CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR + "/" + copyPath;
      } else if (fullPath != null) {
         href = fileUrl(fullPath);
         visible = fullPath;
      } else {
         return;
      }
      html.append("<p class='detail'>").append(label).append(": <a href='").append(href).append(
         "' target='_blank' rel='noopener'>").append(escapeHtml(visible)).append("</a></p>");
   }

   private static String fileUrl(String fullPath) {
      String p = fullPath.replace("\\", "/").replace(" ", "%20");
      return p.startsWith("/") ? "file://" + p : "file:///" + p;
   }

   private static String diffReportHeadExtras() {
      StringBuilder h = new StringBuilder();
      h.append("<script>document.addEventListener('DOMContentLoaded', function() {");
      h.append("  document.querySelectorAll('table').forEach(function(table) {");
      h.append("    var firstTh = table.querySelector('th');");
      h.append("    if (firstTh) {");
      h.append("      table.style.tableLayout = 'auto';");
      h.append("      var labelW = firstTh.getBoundingClientRect().width;");
      h.append(
         "      table.querySelectorAll('td.label').forEach(function(td) { " + "labelW = Math.max(labelW, td.getBoundingClientRect().width); });");
      h.append("      table.style.tableLayout = 'fixed';");
      h.append("      firstTh.style.width = Math.ceil(labelW) + 'px';");
      h.append("    }");
      h.append("    table.querySelectorAll('th').forEach(function(th) {");
      h.append("      var handle = document.createElement('div'); handle.className = 'col-resizer';");
      h.append("      th.appendChild(handle);");
      h.append("      var startX, startW;");
      h.append("      handle.addEventListener('mousedown', function(e) {");
      h.append("        startX = e.pageX; startW = th.offsetWidth; e.preventDefault();");
      h.append(
         "        function onMove(ev) { var w = Math.max(30, startW + (ev.pageX - startX)); " + "th.style.width = w + 'px'; }");
      h.append(
         "        function onUp() { document.removeEventListener('mousemove', onMove); " + "document.removeEventListener('mouseup', onUp); }");
      h.append("        document.addEventListener('mousemove', onMove); document.addEventListener('mouseup', onUp);");
      h.append("      });");
      h.append("    });");
      h.append("  });");
      h.append("});</script>");
      // Shared packet palette (matches PacketStyle); this page keeps its own table-layout/resizer CSS on top.
      h.append("<style>");
      h.append(":root{--fw:#1f6feb;--ink:#1f2328;--muted:#656d76;--line:#d0d7de;--soft:#f6f8fa;}");
      h.append("body { font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; margin: 28px 24px 64px; "
         + "font-size: 14px; color: var(--ink); line-height: 1.5; }");
      h.append("h1 { color: var(--ink); font-size: 24px; }");
      h.append("h2 { color: var(--ink); font-size: 18px; margin-top: 28px; padding: 6px 0 6px 12px; "
         + "border-left: 5px solid var(--fw); }");
      h.append("table { border-collapse: collapse; width: 100%; margin-bottom: 10px; table-layout: fixed; }");
      h.append("th, td { border: 1px solid var(--line); padding: 6px 10px; text-align: left; vertical-align: top; }");
      h.append("th { background: var(--soft); position: relative; }");
      h.append("td.label { white-space: nowrap; font-weight: bold; background: var(--soft); }");
      h.append(
         ".col-resizer { position: absolute; top: 0; right: 0; width: 6px; height: 100%; cursor: col-resize; " + "user-select: none; }");
      h.append(".col-resizer:hover { background: var(--fw); }");
      h.append("pre { margin: 0; font-size: 12px; }");
      h.append(
         "pre code { padding: 0; background: transparent; white-space: pre-wrap; " + "overflow-wrap: anywhere; font-family: Consolas, 'Courier New', monospace; }");
      h.append(".unchanged { color: #888; font-style: italic; }");
      h.append(".detail { font-size: 12px; color: var(--muted); }");
      h.append("a { color: var(--fw); }");
      h.append(
         ".summary { background: var(--soft); border-left: 3px solid var(--fw); padding: 12px 16px; "
            + "border-radius: 4px; margin: 12px 0 20px; font-size: 13px; }");
      h.append(".summary ol, .summary ul { margin: 8px 0 0; padding-left: 22px; } .summary li { margin: 4px 0; }");
      h.append("code { background: var(--soft); padding: 1px 5px; border-radius: 4px; "
         + "font-family: Consolas, 'Courier New', monospace; font-size: 12.5px; }");
      h.append("</style>");
      return h.toString();
   }

}
