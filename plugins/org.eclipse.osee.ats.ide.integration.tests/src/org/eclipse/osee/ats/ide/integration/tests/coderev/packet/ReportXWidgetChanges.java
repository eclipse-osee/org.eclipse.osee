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
 * THROWAWAY migration tool (sibling of {@code ReportBlamChanges}). Scans two workspaces (ggg = new, try = old) for
 * {@code X*Widget.java} files and generates the XWidget section of the review packet in {@code ~/CodeXWidgetReview/}: a
 * Rename report, a Diff report (old vs new source with portable highlighted copies + relative links), and a static
 * Testing report. Delete after the XWidget migration is reviewed and merged.
 *
 * @author Donald G. Dunne
 */
public class ReportXWidgetChanges {

   /** When true, scan ONLY the {@code org.eclipse.osee} repo under each workspace root (skip proprietary siblings). */
   private static final boolean onlyOpenSource = false;
   private static final String OPEN_SOURCE_REPO = "org.eclipse.osee";

   /** [baseDir, repoName] - each baseDir is scanned for subdirs containing a plugins folder. */
   private static final String[][] WORKSPACES = { //
      {"C:/Tools/git_ggg", "ggg"}, //
      {"C:/Tools/git_try", "try"} //
   };

   /** Matches a class declaration with an extends clause: group(1)=abstract?, group(2)=class, group(3)=parent. */
   private static final Pattern CLASS_DECL =
      Pattern.compile("(?:public\\s+|protected\\s+|private\\s+)?(abstract\\s+)?class\\s+(\\w+)\\s+extends\\s+(\\w+)");

   public static void main(String[] args) throws IOException {
      System.out.println("=== XWidget Change Report ===");
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
         Path outputPath = new File(jsonDir(), String.format("xwidget_classes_%s.json", repoName)).toPath();
         System.out.println("Repo: " + repoName + " (" + pluginsDirs.size() + " plugins dirs) -> " + outputPath);
         generateMetrics(pluginsDirs, repoName, outputPath);
      }

      System.out.println("Generating rename report...");
      generateChangeReport();
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
    * Scan one workspace's plugins dirs for {@code X*Widget.java} files, extract per-widget data, write a portable
    * highlighted copy of each, and emit the {@code xwidget_classes_<repo>.json} file.
    */
   private static void generateMetrics(List<Path> pluginsDirs, String repoName, Path outputPath) throws IOException {
      String copySubdir = "try".equals(repoName) ? "old_xwidget" : "new_xwidget";
      File copyDir = new File(jsonDir(), copySubdir);
      copyDir.mkdirs();

      // widgetName -> file; and widgetName -> isAbstract. A widget qualifies by filename (X*Widget.java). Abstract is
      // detected from the class declaration (or an "Abstract" in the name).
      Map<String, Path> classToFile = new HashMap<>();
      Map<String, Boolean> classIsAbstract = new HashMap<>();

      for (Path root : pluginsDirs) {
         Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               String fileName = file.getFileName().toString();
               if (!fileName.matches("X.*Widget\\.java")) {
                  return FileVisitResult.CONTINUE;
               }
               String className = fileName.replace(".java", "");
               classToFile.put(className, file);
               // Abstract if the file name says so, or the class decl is abstract.
               boolean isAbstract = className.contains("Abstract");
               if (!isAbstract) {
                  String content = Files.readString(file);
                  Matcher m = CLASS_DECL.matcher(content);
                  while (m.find()) {
                     if (m.group(2).equals(className)) {
                        isAbstract = m.group(1) != null;
                        break;
                     }
                  }
               }
               classIsAbstract.put(className, isAbstract);
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
            String sideLabel = "old_xwidget".equals(copySubdir) ? "old" : "new";
            String outName = writeHighlightedCopy(filePath, copyDir, className, sideLabel);
            String copyPath = outName == null ? null : copySubdir + "/" + outName;
            boolean isAbstract = classIsAbstract.getOrDefault(className, false);
            long lineCount = countSourceLines(filePath);

            // Widget diff rows: constructors + the XX lifecycle/behavior methods. Any may be absent (null).
            String constructorCode = String.join("\n\n", extractConstructors(filePath, className));
            String initializeSelected = extractMethod(filePath, "initializeSelected");
            String refreshCode = extractMethod(filePath, "refresh");
            String handleSelection = extractMethod(filePath, "handleSelection");
            String handleSelectedPersist = extractMethod(filePath, "handleSelectedPersist");
            String getValidator = extractMethod(filePath, "getValidator");
            String getSentinel = extractMethod(filePath, "getSentinel");

            writer.println("    {");
            writer.println(jsonField("name", className));
            writer.println(jsonField("path", fullPath));
            writer.println(jsonField("copyPath", copyPath));
            writer.println(String.format("      \"isAbstract\": %s,", isAbstract));
            writer.println(String.format("      \"lineCount\": %d,", lineCount));
            writer.println(jsonField("constructorCode", constructorCode.isEmpty() ? null : constructorCode));
            writer.println(jsonField("initializeSelected", initializeSelected));
            writer.println(jsonField("refreshCode", refreshCode));
            writer.println(jsonField("handleSelection", handleSelection));
            writer.println(jsonField("handleSelectedPersist", handleSelectedPersist));
            writer.println(jsonField("getValidator", getValidator));
            // last field: no trailing comma
            writer.println(
               String.format("      \"getSentinel\": %s", getSentinel == null ? "null" : jsonString(getSentinel)));
            writer.print("    }");
            writer.println(i < sorted.size() - 1 ? "," : "");
         }
         writer.println("  ]");
         writer.println("}");
      }
      System.out.println("Found " + sorted.size() + " X*Widget classes in " + repoName + ".");
   }

   // =========================================================================
   // Rename / change report
   // =========================================================================

   /**
    * Resolve old-&gt;new widget rename pairs. Uses the curated {@link ReportXWidgetMapping} map first (authoritative),
    * then heuristics (OldName -&gt; OldNameWidget, X... -&gt; XX...Widget). Matched names are removed from the
    * passed-in added/removed sets. Returns oldName -&gt; newName.
    */
   private static Map<String, String> resolveRenames(Set<String> added, Set<String> removed) {
      Map<String, String> renames = new HashMap<>();
      Set<String> renamedOld = new HashSet<>();
      Set<String> renamedNew = new HashSet<>();

      // Curated mapping first (authoritative).
      Map<String, String> curated = ReportXWidgetMapping.getMapping();
      for (Map.Entry<String, String> e : curated.entrySet()) {
         String oldName = e.getKey();
         String newName = e.getValue();
         if (removed.contains(oldName) && added.contains(newName)) {
            renames.put(oldName, newName);
            renamedOld.add(oldName);
            renamedNew.add(newName);
         } else if (removed.contains(oldName) && !renames.containsKey(oldName)) {
            // Show the curated pair even if the new side wasn't matched in the scan (e.g. consolidated).
            renames.put(oldName, newName);
         }
      }
      // Heuristic: X<name> -> XX<name>Widget.
      for (String oldName : new ArrayList<>(removed)) {
         if (renamedOld.contains(oldName)) {
            continue;
         }
         String expectedNew = "XX" + oldName.replaceFirst("^X", "") + "Widget";
         if (!expectedNew.equals(oldName) && added.contains(expectedNew) && !renamedNew.contains(expectedNew)) {
            renames.put(oldName, expectedNew);
            renamedOld.add(oldName);
            renamedNew.add(expectedNew);
         }
      }
      // Heuristic: OldName -> OldNameWidget.
      for (String oldName : new ArrayList<>(removed)) {
         if (renamedOld.contains(oldName)) {
            continue;
         }
         String withWidget = oldName + "Widget";
         if (added.contains(withWidget) && !renamedNew.contains(withWidget)) {
            renames.put(oldName, withWidget);
            renamedOld.add(oldName);
            renamedNew.add(withWidget);
         }
      }

      removed.removeAll(renamedOld);
      added.removeAll(renamedNew);
      return renames;
   }

   /**
    * Generate the <b>Consolidation Overview</b> page directly from the curated {@link ReportXWidgetMapping} (the
    * single source of truth), grouped by {@link ReportXWidgetMapping.Category.Band Band} then {@link
    * ReportXWidgetMapping.Category Category}. Each row is the new widget name followed by the old name(s) it replaced
    * ("was: ..."), or a "new widget" / "kept name" marker when there are no old names. Categories with 2 or fewer
    * entries collapse into a per-band "Misc" block so the page is not a wall of tiny sections. No JSON scan needed -
    * this view is purely the mapping.
    */
   private static void generateChangeReport() throws IOException {
      File outDir = jsonDir();

      List<ReportXWidgetMapping.WidgetMapping> all = ReportXWidgetMapping.getWidgetMappings();

      // Counts for the summary cards (one source of truth: ReportXWidgetMapping).
      int newCount = ReportXWidgetMapping.getNewWidgetCount();
      int oldCount = ReportXWidgetMapping.getOldWidgetCount();
      int deletedCount = ReportXWidgetMapping.getDeletedWidgetCount();

      StringBuilder h = new StringBuilder();
      h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      h.append("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
      h.append("<title>XWidget &rarr; XXWidget Consolidation Overview</title>");
      h.append(PacketStyle.pageCss());
      h.append("</head><body><div class='wrap'>");
      h.append("<h1>XWidget &rarr; XXWidget Consolidation Overview</h1>");
      h.append("<p class='sub'>Every migrated widget, grouped by purpose. New name first, with the old widget(s) "
         + "it replaced in parentheses.</p>");

      // Summary cards.
      h.append("<div class='summary'>");
      h.append(card("", String.valueOf(oldCount), "Old widgets"));
      h.append(card("arrow", "&rarr;", "consolidated into"));
      h.append(card("new", String.valueOf(newCount), "New XXWidgets"));
      h.append(card("del", String.valueOf(deletedCount), "Deleted outright"));
      h.append("</div>");

      h.append(String.format("<div class='note'><b>How to read this</b> &mdash; each row is a new "
         + "<code>XX...Widget</code> and the old widget(s) it replaced. &quot;new widget&quot; means no direct "
         + "predecessor; &quot;kept name&quot; means a complex widget kept its original name. %d old &rarr; %d new "
         + "(%d deleted).</div>", ReportXWidgetMapping.getOldWidgetCount(),
         ReportXWidgetMapping.getNewWidgetCount(), ReportXWidgetMapping.getDeletedWidgetCount()));

      // Render each band in order; collapse small categories into a band "Misc".
      for (ReportXWidgetMapping.Category.Band band : ReportXWidgetMapping.Category.Band.values()) {
         if (band == ReportXWidgetMapping.Category.Band.DELETED) {
            continue; // handled separately below
         }
         appendBand(h, all, band);
      }
      appendDeletedBand(h, all);

      h.append("<p class='note'><a href='XWidget_Overview.html'>&larr; Back to the review overview</a></p>");
      h.append("</div></body></html>");

      File reportFile = new File(outDir, "XWidgets_XWidgetReview_Rename.html");
      Files.writeString(reportFile.toPath(), h.toString());
      System.out.println("Consolidation Overview written to: " + reportFile.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(reportFile);
      } catch (Exception ex) {
         // ignore
      }
   }

   private static String card(String cls, String num, String label) {
      String c = cls.isEmpty() ? "stat" : "stat " + cls;
      return String.format("<div class='%s'><div class='num'>%s</div><div class='lbl'>%s</div></div>", c, num, label);
   }

   /** Band label as shown in the section heading. */
   private static String bandLabel(ReportXWidgetMapping.Category.Band band) {
      switch (band) {
         case FRAMEWORK:
            return "Framework Widgets";
         case ATS:
            return "ATS Widgets";
         case AGILE:
            return "Agile Widgets";
         default:
            return band.name();
      }
   }

   /** Lower-case band css class used in {@code <section class='band framework'>}. */
   private static String bandCss(ReportXWidgetMapping.Category.Band band) {
      return band.name().toLowerCase();
   }

   /**
    * Append one band: a heading with its widget count, then each category with 3+ entries as its own sub-section, and
    * all categories with 2 or fewer entries merged into a trailing "{Band} Misc" sub-section.
    */
   private static void appendBand(StringBuilder h, List<ReportXWidgetMapping.WidgetMapping> all,
      ReportXWidgetMapping.Category.Band band) {
      // Preserve declaration order of categories within the band.
      java.util.LinkedHashMap<ReportXWidgetMapping.Category, List<ReportXWidgetMapping.WidgetMapping>> byCat =
         new java.util.LinkedHashMap<>();
      int bandTotal = 0;
      for (ReportXWidgetMapping.Category cat : ReportXWidgetMapping.Category.values()) {
         if (cat.getBand() != band) {
            continue;
         }
         byCat.put(cat, new ArrayList<>());
      }
      for (ReportXWidgetMapping.WidgetMapping m : all) {
         if (m.category.getBand() == band) {
            byCat.get(m.category).add(m);
            bandTotal++;
         }
      }
      if (bandTotal == 0) {
         return;
      }

      h.append("<section class='band ").append(bandCss(band)).append("'>");
      h.append("<h2>").append(bandLabel(band)).append(" <span class='count'>&mdash; ").append(bandTotal).append(
         " widgets</span></h2>");

      List<ReportXWidgetMapping.WidgetMapping> misc = new ArrayList<>();
      List<String> miscCatLabels = new ArrayList<>();
      for (Map.Entry<ReportXWidgetMapping.Category, List<ReportXWidgetMapping.WidgetMapping>> e : byCat.entrySet()) {
         List<ReportXWidgetMapping.WidgetMapping> items = e.getValue();
         if (items.isEmpty()) {
            continue;
         }
         if (items.size() <= 2) {
            misc.addAll(items);
            miscCatLabels.add(e.getKey().getLabel());
         } else {
            appendCategory(h, e.getKey().getLabel(), null, items);
         }
      }
      if (!misc.isEmpty()) {
         String plain = miscCatLabels.isEmpty() ? "(small categories)"
            : "(small categories: " + String.join(", ", miscCatLabels) + ")";
         appendCategory(h, bandLabel(band).replace(" Widgets", "") + " Misc", plain, misc);
      }
      h.append("</section>");
   }

   /** One sub-category: a tinted chip heading (with optional {@code plain} parenthetical) and the widget rows. */
   private static void appendCategory(StringBuilder h, String label, String plain,
      List<ReportXWidgetMapping.WidgetMapping> items) {
      h.append("<div class='cat'><h3>").append(PacketStyle.escapeHtml(label));
      if (plain != null) {
         h.append(" <span class='plain'>").append(PacketStyle.escapeHtml(plain)).append("</span>");
      }
      h.append("</h3><table>");
      for (ReportXWidgetMapping.WidgetMapping m : items) {
         h.append("<tr><td class='newname'><code>").append(PacketStyle.escapeHtml(m.newName)).append(
            "</code></td><td class='was'>");
         if (m.oldNames.isEmpty()) {
            boolean keptName = !m.newName.startsWith("XX");
            h.append("<span class='brandnew'>").append(keptName ? "kept name (complex widget)" : "new widget").append(
               "</span>");
         } else {
            h.append("(was: ");
            for (int i = 0; i < m.oldNames.size(); i++) {
               if (i > 0) {
                  h.append(", ");
               }
               h.append("<code>").append(PacketStyle.escapeHtml(m.oldNames.get(i))).append("</code>");
            }
            h.append(")");
         }
         h.append("</td></tr>");
      }
      h.append("</table></div>");
   }

   /** The Deleted band: a single red list of old widgets removed with no replacement. */
   private static void appendDeletedBand(StringBuilder h, List<ReportXWidgetMapping.WidgetMapping> all) {
      List<String> deleted = new ArrayList<>();
      for (ReportXWidgetMapping.WidgetMapping m : all) {
         if (m.category == ReportXWidgetMapping.Category.DELETED) {
            deleted.addAll(m.oldNames);
         }
      }
      if (deleted.isEmpty()) {
         return;
      }
      deleted.sort(String::compareTo);
      h.append("<section class='band deleted'>");
      h.append("<h2>Deleted <span class='count'>&mdash; ").append(deleted.size()).append(
         " widgets, no replacement</span></h2>");
      h.append("<div class='cat deleted-list'><table>");
      for (String name : deleted) {
         h.append("<tr><td class='was'><code>").append(PacketStyle.escapeHtml(name)).append("</code></td></tr>");
      }
      h.append("</table></div></section>");
   }

   // =========================================================================
   // Code diff report
   // =========================================================================

   private static void generateCodeDiff() throws IOException {
      File outDir = jsonDir();
      Path gggJson = new File(outDir, "xwidget_classes_ggg.json").toPath();
      Path tryJson = new File(outDir, "xwidget_classes_try.json").toPath();
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

      // old->new pairs present in both trees (renamed pairs + same-name survivors), keyed/sorted by new name.
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
      html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>XWidget Code Diff</title>");
      html.append(PacketStyle.highlighterCss());
      html.append(PacketStyle.highlighterScript());
      html.append(diffReportHeadExtras());
      html.append("</head><body>");
      html.append("<h1>XWidget Code Diff</h1>");
      html.append(diffHowToUse());

      html.append("<div class='summary'><b>Things to note</b><ol>");
      html.append(
         "<li>Most widgets now extend <code>XXAbstractBaseXWidget</code>; the migration changed widget " + "<b>names</b>, <b>constructors</b> (added a <code>WidgetId</code>), and <b>where options live</b> " + "(<code>XOption</code>/<code>XWidgetData</code>) - it generally did <b>not</b> rewrite each widget's " + "internals, so many diffs are renames/reordering/option moves.</li>");
      html.append(
         "<li>Each concrete widget is <code>@Component(service = XWidget.class, immediate = true)</code> with " + "a no-arg constructor and a <code>WidgetId ID</code> matching the class name.</li>");
      html.append(
         "<li>Lifecycle is explicit: <code>initializeSelected()</code> loads, <code>refresh()</code> paints, " + "<code>handleSelection()</code> edits, <code>handleSelectedPersist()</code> persists; " + "<code>getValidator()</code>/<code>getSentinel()</code> are the usual overrides.</li>");
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
            html.append("<p class='detail'>(abstract base class - no method diff)</p>");
            continue;
         }
         html.append("<table><tr><th></th><th>Old Code</th><th>New Code</th></tr>");
         appendDiffRow(html, "Constructors", oldData.get("constructorCode"), newData.get("constructorCode"));
         appendDiffRow(html, "initializeSelected", oldData.get("initializeSelected"),
            newData.get("initializeSelected"));
         appendDiffRow(html, "refresh", oldData.get("refreshCode"), newData.get("refreshCode"));
         appendDiffRow(html, "handleSelection", oldData.get("handleSelection"), newData.get("handleSelection"));
         appendDiffRow(html, "handleSelectedPersist", oldData.get("handleSelectedPersist"),
            newData.get("handleSelectedPersist"));
         appendDiffRow(html, "getValidator", oldData.get("getValidator"), newData.get("getValidator"));
         appendDiffRow(html, "getSentinel", oldData.get("getSentinel"), newData.get("getSentinel"));
         html.append("</table>");
      }
      html.append("</body></html>");

      File reportFile = new File(outDir, "XWidgets_XWidgetReview_Diff.html");
      Files.writeString(reportFile.toPath(), html.toString());
      System.out.println("Code diff written to: " + reportFile.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(reportFile);
      } catch (Exception ex) {
         // ignore
      }
   }

   private static String diffHowToUse() {
      return "<div class='summary'><b>How to use</b><ul>" + "<li>Per-widget <b>old vs new</b> diffs for quick review (incl. renamed classes).</li>" + "<li><b>Old</b>/<b>New</b> links open the full highlighted source (stored in <code>old_xwidget/</code> / " + "<code>new_xwidget/</code> beside this file - works wherever unzipped).</li>" + "<li><code>NO_CHANGE</code> = that section is identical (whitespace ignored); old is still shown.</li>" + "<li>Code is re-indented, so some diffs are just formatting/reordering.</li>" + "<li><b>Deleted and new-only widgets are not shown.</b></li>" + "<li>Runs locally in your browser - no data leaves this file.</li></ul></div>";
   }

   // =========================================================================
   // Testing report (static content)
   // =========================================================================

   private static void generateTestingReport() {
      StringBuilder h = new StringBuilder();
      h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      h.append("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
      h.append("<title>XWidget Testing</title>");
      h.append(PacketStyle.pageCss());
      h.append("<style>.box{background:var(--soft);border-left:3px solid var(--agile);padding:10px 16px;"
         + "border-radius:4px;margin-bottom:20px;}</style>");
      h.append("</head><body><div class='wrap'>");
      h.append("<h1>XWidget Testing</h1>");
      h.append(String.format(
         "<div class='box'>The migration consolidated %d old widget names into %d new "
            + "<code>XX...Widget</code> classes (%d deleted). Because most UI now uses one widget per data type, each "
            + "data type generally needs only a single widget test instead of many bespoke widget tests.</div>",
         ReportXWidgetMapping.getOldWidgetCount(), ReportXWidgetMapping.getNewWidgetCount(),
         ReportXWidgetMapping.getDeletedWidgetCount()));

      h.append("<h2>Tests performed</h2><ul>");
      h.append(
         "<li><b><code>CodeReviewXWidgets</code></b> (coderev suite) - durable standards check over every " + "<code>X.*Widget.java</code>: <code>@Component(service = XWidget.class)</code>; <code>WidgetId ID</code> " + "matches the class name; default no-arg constructor; only one constructor calls <code>super()</code> " + "(others chain <code>this()</code>); ATS widgets have <code>xxWid</code>; <code>setArtifact()</code> does " + "not call <code>refresh()</code>/<code>setSelected()</code>; <code>initializeSelected()</code> uses " + "<code>setSelected(value,false)</code>; no <code>validate()</code>/<code>getSelected()</code> override; no " + "stray prints; no TBD/TODO; copyright header; matching OSGI-INF XML.</li>");
      h.append(
         "<li><b>Runtime widget hierarchy report</b> (<code>TestingXWidgetHierarchyView</code>) - instantiates "
            + "every OSGi-registered widget and shows it in a hierarchy/flat results editor with its "
            + "<code>WidgetId</code>, attribute-type provider(s), value type, and default <code>XOption</code>s, so a "
            + "reviewer can confirm each widget resolves and is wired correctly.</li>");
      h.append(
         "<li><b>Demo widget gallery</b> (<code>WorkDefTeamDemoXWidgetTest</code>) - a Work Definition that " + "exercises nearly every XX widget / <code>XOption</code> combination for visual and contract " + "verification.</li>");
      h.append("</ul>");

      h.append("<h2>Test / debug features added to the workbench</h2><ul>");
      h.append(
         "<li><b>Ctrl+click a widget label</b> &rarr; widget debug report (resolved <code>XOption</code>s, " + "attribute type, <code>WidgetId</code>, work def), wired in " + "<code>XXAbstractBaseXWidget.createLabelWidget()</code>; easy to copy/paste into an AI to diagnose a " + "widget.</li>");
      h.append(
         "<li><b>Testing - Open XWidget Hierarchy View</b> (OSEE Navigator, Admin) - "
            + "<code>TestingXWidgetHierarchyView</code> opens a results editor over every OSGi-registered widget "
            + "(<code>XWidgetService.getRegisteredWidgetClasses()</code>) with two tabs: a <b>Hierarchy</b> tab "
            + "showing the <code>XWidget</code> class tree, and a <b>Flat</b> tab. Columns: Widget / "
            + "<code>WidgetId</code>, attribute-type provider(s), value type, default <code>XOption</code>s, package, "
            + "and registered.</li>");
      h.append(
         "<li><b>Testing - Validate OSEE Types</b> (<code>TestingValidateOseeTypes</code>) - reports types " + "defined in code but unused, and types in the database that are not in code.</li>");
      h.append(
         "<li><b><code>XWidget.debugXWidgets</code></b> - a static flag whose <code>.syserr(...)</code> prints " + "widget refresh/create lifecycle events.</li>");
      h.append(
         "<li><b>Layout debug colors</b> (<code>XWidgetUtility.debugLayout</code>) - colorizes nested " + "composites (topLevel=yellow, group=cyan, etc.) to diagnose layout issues.</li>");
      h.append("</ul>");

      h.append("<h2>Good to know</h2><ul>");
      h.append("<li><b>No new widgets should be necessary</b> - unless it is a composite widget or a complex "
         + "widget.</li>");
      h.append(
         "<li>A widget's <code>WidgetId</code>/<code>WidgetIdAts</code> token has the <b>same name as its " + "class</b>, so <code>Ctrl-Shift-T</code> on the token jumps to the widget class and find-references on the " + "token locates every use.</li>");
      h.append(
         "<li>A widget can be the default for an attribute type via " + "<code>setAsProviderFor(AttributeTypeToken)</code>.</li>");
      h.append("</ul>");

      h.append("<p class='note'><a href='XWidget_Overview.html'>&larr; Back to the review overview</a></p>");
      h.append("</div></body></html>");

      File out = new File(jsonDir(), "XWidgets_XWidgetReview_Testing.html");
      try {
         Files.writeString(out.toPath(), h.toString());
         System.out.println("XWidget testing report written to: " + out.getAbsolutePath());
         java.awt.Desktop.getDesktop().open(out);
      } catch (Exception ex) {
         // ignore
      }
   }

   // =========================================================================
   // Shared helpers (copied from ReportBlamChanges; throwaway migration tooling)
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
