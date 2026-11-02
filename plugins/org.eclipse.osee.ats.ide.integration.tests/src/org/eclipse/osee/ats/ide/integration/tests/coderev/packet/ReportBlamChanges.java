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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Standalone utility that scans two workspaces (ggg and try) for Java source files and finds all classes that extend
 * AbstractBlam, including through inheritance. Outputs JSON files for both workspaces to the ggg root.
 * <p>
 * Each workspace root (e.g., C:/Tools/git_ggg) is scanned for subdirectories containing a "plugins" folder.
 * <p>
 * Output files:
 * <ul>
 * <li>blam_classes_ggg.json - from all plugins dirs under C:/Tools/git_ggg</li>
 * <li>blam_classes_try.json - from all plugins dirs under C:/Tools/git_try</li>
 * </ul>
 * Both files are written to C:/Tools/git_ggg
 *
 * @author Donald G. Dunne
 */
public class ReportBlamChanges {

   /** Set to false to skip the slow git history check for deleted BLAMs (useful for debugging) */
   private static final boolean CHECK_DELETED_BLAMS = false;

   /**
    * When true, scan ONLY the {@code org.eclipse.osee} repo under each workspace root (skip sibling proprietary repos
    * like com.boeing.osee.*). Much faster and lets you eyeball the resulting HTML on just the open-source BLAMs. When
    * false, scans every {@code <repo>/plugins} under each workspace root (full behavior).
    */
   private static final boolean onlyOpenSource = false;

   /** The repo directory name scanned when {@link #onlyOpenSource} is true. */
   private static final String OPEN_SOURCE_REPO = "org.eclipse.osee";

   /** Workspace configurations: [baseDir, repoName] - each baseDir is scanned for subdirs with plugins folders */
   private static final String[][] WORKSPACES = { //
      {"C:/Tools/git_ggg", "ggg"}, //
      {"C:/Tools/git_try", "try"} //
   };

   /**
    * Workspace root - only used to locate the curated blam_map.csv input. Generated JSON + HTML go to
    * {@link #jsonDir()} (~/CodeXWidgetReview).
    */
   private static final String OUTPUT_DIR = "C:/Tools/git_ggg";

   private static final Pattern CLASS_DECL =
      Pattern.compile("(?:public\\s+|protected\\s+|private\\s+)?(abstract\\s+)?class\\s+(\\w+)\\s+extends\\s+(\\w+)");

   public static void main(String[] args) throws IOException {
      System.out.println("=== BLAM Metrics Generator ===");
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

         // Find all plugins directories under the base directory
         List<Path> pluginsDirs = findPluginsDirs(basePath);
         if (pluginsDirs.isEmpty()) {
            System.err.println("WARNING: No plugins directories found under: " + baseDir);
            continue;
         }

         String outputFile = String.format("blam_classes_%s.json", repoName);
         Path outputPath = new File(jsonDir(), outputFile).toPath();

         System.out.println("----------------------------------------");
         System.out.println("Base directory: " + basePath);
         System.out.println("Repo: " + repoName);
         System.out.println("Found " + pluginsDirs.size() + " plugins directories:");
         for (Path dir : pluginsDirs) {
            System.out.println("  " + dir);
         }
         System.out.println("Output: " + outputPath);
         System.out.println();

         generateMetrics(pluginsDirs, repoName, outputPath);
      }

      System.out.println("========================================");
      System.out.println("Generating comparison report...");
      generateChangeReport();
      System.out.println("Generating code diff report...");
      generateCodeDiff();
      System.out.println("Generating testing report...");
      generateTestingReport();
      System.out.println("All workspaces processed.");
   }

   /**
    * Generate {@code XWidgets_BlamReview_Testing.html}: the testing done for BLAMs plus the test/debug features added
    * to the workbench during the migration. Static content (no scan needed), written to the same output folder as the
    * other BLAM reports so it is part of the portable packet.
    */
   private static void generateTestingReport() throws IOException {
      StringBuilder h = new StringBuilder();
      h.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      h.append("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
      h.append("<title>BLAM Testing</title>");
      h.append(PacketStyle.pageCss());
      h.append("<style>.box{background:var(--soft);border-left:3px solid var(--agile);padding:10px 16px;"
         + "border-radius:4px;margin-bottom:20px;}</style>");
      h.append("</head><body><div class='wrap'>");
      h.append("<h1>BLAM Testing</h1>");
      h.append(
         "<div class='box'>Counts (new): 76 <code>*Blam.java</code> (4 abstract + 72 concrete), 75 concrete " + "<code>*BlamTest.java</code>. Every widget-bearing BLAM was converted from XML to the builder style " + "(<code>getXWidgetsXml</code> 98 &rarr; 0).</div>");

      h.append("<h2>Tests performed</h2><ul>");
      h.append(
         "<li><b><code>AbstractBlamTest</code> harness</b> - every non-abstract BLAM has a " + "<code>&lt;BlamName&gt;Test</code> in the same package (discovered by reflection via " + "<code>BlamService.getBlamTest()</code>). It verifies the editor opens, the title/description are " + "non-empty, all widgets render, dummy data can be set on each widget " + "(<code>setDummyData(XWidget)</code>, special-casing branch/view widgets), and the data round-trips " + "through the <code>VariableMap</code>.</li>");
      h.append(
         "<li><b><code>TestAllRegisteredBlamsIntegrationTest</code></b> " + "(<code>@RunWith(Parameterized.class)</code>) - iterates <code>BlamService.getBlams()</code> and " + "<b>fails any BLAM lacking a <code>&lt;Name&gt;Test</code></b>. Confirmed: 0 non-abstract BLAMs are " + "missing a test.</li>");
      h.append(
         "<li><b>Demo-DB guard</b> - <code>isDemoDbBlamTest()</code> lets BLAMs run/demo in a demo DB while " + "short-circuiting only during the BLAM test.</li>");
      h.append(
         "<li><b><code>CodeReviewBlams</code></b> (coderev suite) - standards check over every " + "<code>*Blam.java</code>: <code>@Component(service = AbstractBlam.class)</code>, builder " + "(<code>getXWidgetItems</code>) not XML, <code>BLAM_NAME</code> constant, default constructor, " + "<code>getDescriptionUsage()</code> present, shared label constants.</li>");
      h.append("</ul>");

      h.append("<h2>Test / debug features added to the workbench</h2><ul>");
      h.append(
         "<li><b>Testing - Open All Registered BLAMs</b> (OSEE Navigator, Admin) - opens each BLAM to view/" + "review its widgets and layout. The demo runtime opens the BLAM, checks title/description/widgets, and " + "runs the operation test if available; production runtime does the same except it does not run the " + "operation test. Uncheck \"Close BLAM Editors\" to spot-check layouts. Run automatically in the " + "integration tests.</li>");
      h.append(
         "<li><b>BLAM Details twistie</b> (<code>DetailsBlamSection</code>, collapsed \"BLAM Details\") - shows " + "Name / Class (simple name) / User Groups / Categories. The class name is copyable for a quick " + "<code>Ctrl-Shift-T</code> jump to the code.</li>");
      h.append(
         "<li><b>User-group access label</b> (<code>BlamUsageSection</code>) - always shown (not just for " + "admin-gated BLAMs), inside the \"Description and Usage\" twistie above the description box; reads " + "\"User Group Access: &lt;groups&gt;\" or \"Everyone\" when none, in DARK_YELLOW.</li>");
      h.append(
         "<li><b>Ctrl+click a widget label</b> - the shared <code>XXAbstractBaseXWidget</code> widget debug " + "report also works for widgets inside a BLAM.</li>");
      h.append("</ul>");

      h.append("<p class='note'><a href='XWidget_Overview.html'>&larr; Back to the review overview</a></p>");
      h.append("</div></body></html>");

      File out = new File(jsonDir(), "XWidgets_BlamReview_Testing.html");
      Files.writeString(out.toPath(), h.toString());
      System.out.println("BLAM testing report written to: " + out.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(out);
      } catch (Exception ex) {
         // ignore
      }
   }

   /**
    * @return the ~/CodeXWidgetReview output folder (created on first use with a README, via the shared
    * {@link CodeQualityUtil} machinery). The interim blam_classes_*.json files and the HTML report all live here.
    */
   private static File jsonDir() {
      return CodeQualityUtil.getOutputDir(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReview");
   }

   /**
    * Find all "plugins" directories under the base directory (pattern: baseDir/&lt;repo&gt;/plugins). When
    * {@link #onlyOpenSource} is true, restrict to just {@code baseDir/org.eclipse.osee/plugins} so the run skips
    * sibling proprietary repos (faster, open-source-only review).
    */
   private static List<Path> findPluginsDirs(Path baseDir) {
      List<Path> result = new ArrayList<>();
      java.io.File[] children = baseDir.toFile().listFiles();
      if (children == null) {
         return result;
      }
      for (java.io.File child : children) {
         if (child.isDirectory()) {
            if (onlyOpenSource && !child.getName().equals(OPEN_SOURCE_REPO)) {
               continue;
            }
            java.io.File pluginsDir = new java.io.File(child, "plugins");
            if (pluginsDir.exists() && pluginsDir.isDirectory()) {
               result.add(pluginsDir.toPath());
            }
         }
      }
      return result;
   }

   private static void generateMetrics(List<Path> pluginsDirs, String repoName, Path outputPath) throws IOException {
      // Copies of each BLAM source go under the output dir so the report links to a portable local copy, not an
      // absolute path on the author's machine: try -> old_blam/, ggg -> new_blam/.
      String copySubdir = "try".equals(repoName) ? "old_blam" : "new_blam";
      File copyDir = new File(jsonDir(), copySubdir);
      copyDir.mkdirs();
      Map<String, String> childToParent = new HashMap<>();
      Map<String, Path> classToFile = new HashMap<>();
      Map<String, Boolean> classIsAbstract = new HashMap<>();

      for (Path root : pluginsDirs) {
         Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               String simpleName = file.getFileName().toString();
               // Ignore *BlamTest.java - these are test classes, not production BLAMs, and should not be counted.
               if (simpleName.endsWith("BlamTest.java")) {
                  return FileVisitResult.CONTINUE;
               }
               if (file.toString().endsWith(".java")) {
                  String content = Files.readString(file);
                  Matcher matcher = CLASS_DECL.matcher(content);
                  while (matcher.find()) {
                     String className = matcher.group(2);
                     String parentName = matcher.group(3);
                     boolean isAbstract = matcher.group(1) != null;
                     childToParent.put(className, parentName);
                     classToFile.put(className, file);
                     classIsAbstract.put(className, isAbstract);
                  }
                  // Also capture Abstract*Blam classes that may not have extends in CLASS_DECL pattern
                  String fileName = file.getFileName().toString().replace(".java", "");
                  if (fileName.startsWith("Abstract") && fileName.endsWith(
                     "Blam") && !classToFile.containsKey(fileName)) {
                     classToFile.put(fileName, file);
                     classIsAbstract.put(fileName, true);
                  }
               }
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

      Set<String> blamClasses = new HashSet<>();
      for (String className : childToParent.keySet()) {
         if (extendsAbstractBlam(className, childToParent, new HashSet<>())) {
            blamClasses.add(className);
         }
      }
      // Also include AbstractBlam and any Abstract*Blam classes found in the scan
      for (String className : classToFile.keySet()) {
         if (className.equals("AbstractBlam") || (className.startsWith("Abstract") && className.endsWith("Blam"))) {
            blamClasses.add(className);
         }
      }

      List<String> sorted = new ArrayList<>(blamClasses);
      sorted.sort(String::compareTo);

      // Write JSON output
      long totalLineCount = 0;
      try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
         writer.println("{");
         writer.println("  \"classes\": [");
         for (int i = 0; i < sorted.size(); i++) {
            String className = sorted.get(i);
            Path filePath = classToFile.get(className);
            String fullPath = filePath.toAbsolutePath().normalize().toString().replace("\\", "/");
            // Write a self-contained, colorized HTML copy of the source into the output dir (old_blam/ or new_blam/)
            // and record its relative path so the report can link to a portable, syntax-highlighted view. Class names
            // are unique per tree, so <ClassName>.java.html won't collide within a copy folder.
            String sideLabel = "old_blam".equals(copySubdir) ? "old" : "new";
            String outName = writeHighlightedCopy(filePath, copyDir, className, sideLabel);
            String copyPath = outName == null ? null : copySubdir + "/" + outName;
            boolean isAbstract = classIsAbstract.getOrDefault(className, false);
            long lineCount = countSourceLines(filePath);
            totalLineCount += lineCount;
            String blamName = extractBlamName(filePath);
            String description = extractMethod(filePath, "getDescriptionUsage");
            List<String> constructors = extractConstructors(filePath, className);
            boolean hasDescriptionInConstructor = hasDescriptionInConstructor(constructors);
            // Constructor source, joined into one block (blank line between multiple constructors) for the diff report.
            String constructorCode = String.join("\n\n", constructors);
            // Raw widget-declaration method body (old getXWidgetsXml or new getXWidgetItems), for the diff report.
            String widgetCode = extractMethod(filePath, "getXWidgetsXml");
            if (widgetCode == null) {
               widgetCode = extractMethod(filePath, "getXWidgetItems");
            }
            // static final String label constants referenced by the widget method (for the new BLAMs' Widget Labels
            // row). Only meaningful/displayed for the new side.
            String widgetLabels = extractWidgetLabels(filePath, widgetCode);
            // Accessible-method bodies for the diff report's Name/Description/UserGroups/Categories rows. Any may be
            // absent (returns null), especially in the old tree where these were not always overridden.
            String nameCode = extractMethod(filePath, "getName");
            String userGroupsCode = extractMethod(filePath, "getUserGroups");
            String categoriesCode = extractMethod(filePath, "getCategories");
            writer.println("    {");
            writer.println(String.format("      \"name\": \"%s\",", className));
            writer.println(String.format("      \"blamName\": %s,", blamName == null ? "null" : jsonString(blamName)));
            writer.println(String.format("      \"path\": \"%s\",", fullPath));
            writer.println(String.format("      \"copyPath\": %s,", copyPath == null ? "null" : jsonString(copyPath)));
            writer.println(String.format("      \"isAbstract\": %s,", isAbstract));
            writer.println(String.format("      \"lineCount\": %d,", lineCount));
            writer.println(String.format("      \"hasDescriptionInConstructor\": %s,", hasDescriptionInConstructor));
            writer.println(
               String.format("      \"description\": %s,", description == null ? "null" : jsonString(description)));
            writer.println(String.format("      \"constructorCode\": %s,",
               constructorCode.isEmpty() ? "null" : jsonString(constructorCode)));
            writer.println(String.format("      \"nameCode\": %s,", nameCode == null ? "null" : jsonString(nameCode)));
            writer.println(String.format("      \"userGroupsCode\": %s,",
               userGroupsCode == null ? "null" : jsonString(userGroupsCode)));
            writer.println(String.format("      \"categoriesCode\": %s,",
               categoriesCode == null ? "null" : jsonString(categoriesCode)));
            writer.println(
               String.format("      \"widgetCode\": %s,", widgetCode == null ? "null" : jsonString(widgetCode)));
            writer.println(
               String.format("      \"widgetLabels\": %s", widgetLabels == null ? "null" : jsonString(widgetLabels)));
            writer.print("    }");
            if (i < sorted.size() - 1) {
               writer.println(",");
            } else {
               writer.println();
            }
         }
         writer.println("  ],");

         // Deleted BLAMs from git history (ignoring renames)
         List<String[]> deletedBlams;
         long deletedTotalLines = 0;
         if (CHECK_DELETED_BLAMS) {
            deletedBlams = findDeletedBlams(pluginsDirs.get(0));
            writer.println("  \"deletedBlams\": [");
            for (int i = 0; i < deletedBlams.size(); i++) {
               String[] entry = deletedBlams.get(i);
               String deletedFile = entry[0];
               String deletedDate = entry[1];
               long deletedLines = Long.parseLong(entry[2]);
               deletedTotalLines += deletedLines;
               writer.print(String.format("    { \"file\": \"%s\", \"deletedDate\": \"%s\", \"lineCount\": %d }",
                  deletedFile.replace("\\", "/"), deletedDate, deletedLines));
               if (i < deletedBlams.size() - 1) {
                  writer.println(",");
               } else {
                  writer.println();
               }
            }
            writer.println("  ],");
         } else {
            deletedBlams = new ArrayList<>();
            writer.println("  \"deletedBlams\": [],");
            System.out.println("(Skipping deleted BLAM check - CHECK_DELETED_BLAMS=false)");
         }

         writer.println(String.format(
            "  \"summary\": { \"classCount\": %d, \"totalLineCount\": %d, \"deletedBlamCount\": %d, \"deletedTotalLineCount\": %d }",
            sorted.size(), totalLineCount, deletedBlams.size(), deletedTotalLines));
         writer.println("}");

         // Check for write errors
         if (writer.checkError()) {
            System.err.println("ERROR: Failed to write to output file: " + outputPath);
            System.err.println("The file may be open in another application. Please close it and try again.");
            System.exit(1);
         }
      } catch (IOException e) {
         System.err.println("ERROR: Cannot write to output file: " + outputPath);
         System.err.println("Reason: " + e.getMessage());
         System.err.println("The file may be open in another application. Please close it and try again.");
         System.exit(1);
      }

      // Verify the file was actually written
      if (!Files.exists(outputPath)) {
         System.err.println("ERROR: Output file was not created: " + outputPath);
         System.exit(1);
      }

      System.out.println("Found " + sorted.size() + " classes extending AbstractBlam.");
      System.out.println("Output written to: " + outputPath.toAbsolutePath().normalize());
      System.out.println();
   }

   private static boolean extendsAbstractBlam(String className, Map<String, String> childToParent,
      Set<String> visited) {
      if (className == null) {
         return false;
      }
      if (className.equals("AbstractBlam")) {
         return true;
      }
      if (visited.contains(className)) {
         return false;
      }
      visited.add(className);
      String parent = childToParent.get(className);
      return extendsAbstractBlam(parent, childToParent, visited);
   }

   /**
    * Extracts a full method body by matching the method signature and tracking brace depth. Returns null if the method
    * is not found.
    */
   private static String extractMethod(Path filePath, String methodName) throws IOException {
      String content = Files.readString(filePath);
      // Use regex to find the method declaration (not just any call to it)
      // Matches: optional @Override + access modifier + return type + methodName(
      Pattern methodDeclPattern =
         Pattern.compile("(?:@Override\\s+)?(?:public|protected|private)\\s+[\\w<>\\[\\],\\s]+\\s+" + Pattern.quote(
            methodName) + "\\s*\\(");
      Matcher declMatcher = methodDeclPattern.matcher(content);
      if (!declMatcher.find()) {
         return null;
      }

      int sigIndex = declMatcher.start();

      // Walk back to include @Override if present
      int methodStart = sigIndex;
      int lineStart = content.lastIndexOf('\n', sigIndex);
      if (lineStart == -1) {
         lineStart = 0;
      }
      String prefix = content.substring(Math.max(0, lineStart - 40), sigIndex).trim();
      if (prefix.endsWith("@Override")) {
         methodStart = content.lastIndexOf("@Override", sigIndex);
      }

      // Find the opening brace
      int braceStart = content.indexOf('{', sigIndex);
      if (braceStart == -1) {
         return null;
      }

      // Track brace depth to find the end
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

   /**
    * Collect the {@code static final String} label-constant declarations referenced by the widget-builder method. For
    * each identifier used in {@code widgetCode} that resolves to a {@code static final String NAME = "..."} field in
    * the same file, return that declaration (deduped, in source order). These are the widget labels the new BLAMs hoist
    * to the top of the class per the BLAM standard. Returns null if none found.
    */
   private static String extractWidgetLabels(Path filePath, String widgetCode) throws IOException {
      if (widgetCode == null) {
         return null;
      }
      String content = Files.readString(filePath);
      // All identifiers referenced in the widget method (candidates for label constants).
      Set<String> referenced = new HashSet<>();
      Matcher idm = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b").matcher(widgetCode);
      while (idm.find()) {
         referenced.add(idm.group());
      }
      // static final String NAME = "..."; declarations, in source order.
      Matcher decl = Pattern.compile(
         "(?:public|protected|private)?\\s*static\\s+final\\s+String\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*\"(?:\\\\.|[^\"\\\\])*\"\\s*;").matcher(
            content);
      java.util.LinkedHashSet<String> out = new java.util.LinkedHashSet<>();
      while (decl.find()) {
         if (referenced.contains(decl.group(1))) {
            out.add(decl.group().trim());
         }
      }
      return out.isEmpty() ? null : String.join("\n", out);
   }

   /**
    * Extracts the BLAM display name. First checks for a BLAM_NAME constant, then falls back to getName() method.
    */
   private static String extractBlamName(Path filePath) throws IOException {
      String content = Files.readString(filePath);
      // First: look for BLAM_NAME = "..."
      Pattern blamNamePattern = Pattern.compile("BLAM_NAME\\s*=\\s*\"([^\"]+)\"");
      Matcher bm = blamNamePattern.matcher(content);
      if (bm.find()) {
         return bm.group(1);
      }
      // Fallback: look for getName() method
      String method = extractMethod(filePath, "getName");
      if (method == null) {
         return null;
      }
      // Look for return "...";
      Pattern returnString = Pattern.compile("return\\s+\"([^\"]+)\"");
      Matcher m = returnString.matcher(method);
      if (m.find()) {
         return m.group(1);
      }
      // Look for return CONSTANT; and resolve it from the file
      Pattern returnConst = Pattern.compile("return\\s+(\\w+)\\s*;");
      m = returnConst.matcher(method);
      if (m.find()) {
         String constName = m.group(1);
         // Look for the constant definition: ... CONST_NAME = "value"
         Pattern constDef = Pattern.compile(constName + "\\s*=\\s*\"([^\"]+)\"");
         Matcher cm = constDef.matcher(content);
         if (cm.find()) {
            return cm.group(1);
         }
      }
      return null;
   }

   /**
    * Extracts all constructors for the given class name from the file.
    */
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

         // Make sure this is a constructor (preceded by access modifier or whitespace, not "class " or "new ")
         int lineStart = content.lastIndexOf('\n', sigIndex);
         if (lineStart == -1) {
            lineStart = 0;
         }
         String linePrefix = content.substring(lineStart, sigIndex).trim();
         // Skip if this is the class declaration or a new instance
         if (linePrefix.contains("class ") || linePrefix.contains("new ") || linePrefix.contains("return ")) {
            searchFrom = sigIndex + 1;
            continue;
         }
         // Should be public/protected/private or empty (default access)
         if (linePrefix.equals("public") || linePrefix.equals("protected") || linePrefix.equals(
            "private") || linePrefix.isEmpty()) {
            // Find opening brace
            int braceStart = content.indexOf('{', sigIndex);
            if (braceStart == -1) {
               searchFrom = sigIndex + 1;
               continue;
            }
            // Track brace depth
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
               int constructorStart = lineStart + 1;
               constructors.add(content.substring(constructorStart, methodEnd).strip());
            }
            searchFrom = methodEnd != -1 ? methodEnd : sigIndex + 1;
         } else {
            searchFrom = sigIndex + 1;
         }
      }
      return constructors;
   }

   /**
    * Uses git log to find deleted Java files that contained "extends AbstractBlam" or extended a known subclass.
    * Ignores renames (only pure deletes). Returns list of [filePath, deleteDate, lineCount].
    */
   private static List<String[]> findDeletedBlams(Path root) {
      List<String[]> results = new ArrayList<>();
      try {
         // Find all deleted .java files (not renames) in last 10 commits
         ProcessBuilder pb = new ProcessBuilder("git", "log", "-10", "--diff-filter=D", "--no-renames", "--name-only",
            "--pretty=format:%H %ai", "--", "*.java");
         pb.directory(root.toFile());
         pb.redirectErrorStream(true);
         Process process = pb.start();

         List<String> lines = new ArrayList<>();
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               lines.add(line);
            }
         }
         process.waitFor();

         // Parse output: alternating commit info lines and file name lines
         String currentCommitHash = null;
         String currentDate = null;
         Set<String> processedFiles = new HashSet<>();

         for (String line : lines) {
            if (line.isEmpty()) {
               continue;
            }
            // Commit line format: <hash> <date>
            if (line.matches("^[0-9a-f]{40} .*")) {
               String[] parts = line.split(" ", 3);
               currentCommitHash = parts[0];
               currentDate = parts[1]; // YYYY-MM-DD
            } else if (line.endsWith(".java") && currentCommitHash != null) {
               // Skip if we already processed this file (only count first deletion)
               if (processedFiles.contains(line)) {
                  continue;
               }

               // Check if the deleted file contained AbstractBlam reference
               long lineCount = getDeletedFileLineCount(root, currentCommitHash, line);
               if (lineCount > 0 && wasAbstractBlamSubclass(root, currentCommitHash, line)) {
                  processedFiles.add(line);
                  results.add(new String[] {line, currentDate, String.valueOf(lineCount)});
               }
            }
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      } catch (Exception e) {
         System.err.println("WARNING: Could not query git history for deleted BLAMs: " + e.getMessage());
      }
      return results;
   }

   private static boolean wasAbstractBlamSubclass(Path root, String commitHash, String filePath) {
      try {
         // Get file content at the commit before deletion (parent commit)
         ProcessBuilder pb = new ProcessBuilder("git", "show", commitHash + "^:" + filePath);
         pb.directory(root.toFile());
         pb.redirectErrorStream(true);
         Process process = pb.start();
         String content;
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
               sb.append(line).append("\n");
            }
            content = sb.toString();
         }
         process.waitFor();
         // Check if it extends AbstractBlam or a known subclass pattern
         return content.contains(
            "extends AbstractBlam") || content.contains("extends Abstract") && content.contains("Blam");
      } catch (Exception e) {
         return false;
      }
   }

   private static long getDeletedFileLineCount(Path root, String commitHash, String filePath) {
      try {
         ProcessBuilder pb = new ProcessBuilder("git", "show", commitHash + "^:" + filePath);
         pb.directory(root.toFile());
         pb.redirectErrorStream(true);
         Process process = pb.start();
         long count = 0;
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
               count++;
            }
         }
         process.waitFor();
         return count;
      } catch (Exception e) {
         return 0;
      }
   }

   /**
    * Checks if any constructor passes a description to super() (3rd parameter or via BlamUiSource).
    */
   private static boolean hasDescriptionInConstructor(List<String> constructors) {
      for (String ctor : constructors) {
         // Check for super(name, description, ...) or super(name, image, description) patterns
         // Also check for BlamUiSource which indicates description comes from file
         if (ctor.contains("BlamUiSource") || ctor.contains("DESCRIPTION") || ctor.contains(
            "description") || ctor.contains("blamDescription")) {
            return true;
         }
         // Check for 3+ parameter super() call (name, image/desc, desc/source)
         java.util.regex.Pattern superPattern = java.util.regex.Pattern.compile("super\\s*\\([^)]*,[^)]*,[^)]+\\)");
         if (superPattern.matcher(ctor).find()) {
            return true;
         }
      }
      return false;
   }

   /**
    * Escapes a string for JSON output.
    */
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

   // =========================================================================
   // Change Report Generation
   // =========================================================================

   /**
    * Load explicit {@code oldName,newName} BLAM rename pairs from {@code blam_map.csv} in {@link #OUTPUT_DIR}. These
    * are curated/authoritative and are applied before any heuristic matching. Format: one pair per line,
    * comma-separated; blank lines and lines starting with {@code #} are ignored, and a leading {@code old,new} header
    * row (if present) is skipped. Missing or unreadable file yields an empty map (no pairs, heuristics still run).
    */
   private static Map<String, String> loadBlamMapCsv() {
      Map<String, String> pairs = new HashMap<>();
      Path csv = Paths.get(OUTPUT_DIR, "blam_map.csv");
      if (!Files.exists(csv)) {
         System.out.println("No blam_map.csv found at " + csv + " - using heuristic matching only.");
         return pairs;
      }
      try {
         for (String line : Files.readAllLines(csv)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
               continue;
            }
            String[] parts = trimmed.split(",", 2);
            if (parts.length != 2) {
               continue;
            }
            String oldName = parts[0].trim();
            String newName = parts[1].trim();
            // Skip an optional header row like "old,new" / "oldName,newName".
            if (oldName.equalsIgnoreCase("old") || oldName.equalsIgnoreCase("oldName")) {
               continue;
            }
            if (!oldName.isEmpty() && !newName.isEmpty()) {
               pairs.put(oldName, newName);
            }
         }
         System.out.println("Loaded " + pairs.size() + " BLAM rename pair(s) from " + csv);
      } catch (IOException ex) {
         System.err.println("WARNING: Could not read " + csv + ": " + ex.getMessage());
      }
      return pairs;
   }

   /**
    * Resolve old-&gt;new BLAM rename pairs from the {@code removed}/{@code added} sets, in precedence order: curated
    * blam_map.csv, exact BLAM display name, display name minus "Blam", trivial {@code Name -> NameBlam}, and manual
    * typo fixes. Matched names are REMOVED from the passed-in {@code added}/{@code removed} sets (so the caller is left
    * with the true added/deleted remainders). Returns the {@code oldName -> newName} map. Shared by both the change
    * report and the code-diff report so the pairing is identical.
    */
   private static Map<String, String> resolveRenames(Set<String> added, Set<String> removed,
      Map<String, Map<String, String>> tryBlams, Map<String, Map<String, String>> gggBlams) {
      Map<String, String> renames = new HashMap<>();
      Set<String> renamedOld = new HashSet<>();
      Set<String> renamedNew = new HashSet<>();

      // FIRST: curated old,new pairs from blam_map.csv - authoritative, applied before any heuristic.
      Map<String, String> csvRenames = loadBlamMapCsv();
      for (Map.Entry<String, String> entry : csvRenames.entrySet()) {
         String oldName = entry.getKey();
         String newName = entry.getValue();
         renames.put(oldName, newName);
         if (removed.contains(oldName) && added.contains(newName)) {
            renamedOld.add(oldName);
            renamedNew.add(newName);
         }
      }

      // Exact BLAM display-name match.
      for (String oldName : new ArrayList<>(removed)) {
         if (renamedOld.contains(oldName)) {
            continue;
         }
         String oldBlamName = tryBlams.get(oldName).get("blamName");
         if (oldBlamName != null) {
            for (String newName : new ArrayList<>(added)) {
               if (renamedNew.contains(newName)) {
                  continue;
               }
               if (oldBlamName.equals(gggBlams.get(newName).get("blamName"))) {
                  renames.put(oldName, newName);
                  renamedOld.add(oldName);
                  renamedNew.add(newName);
                  break;
               }
            }
         }
      }
      // Display name equal after removing the word "Blam".
      for (String oldName : new ArrayList<>(removed)) {
         if (renamedOld.contains(oldName)) {
            continue;
         }
         String oldBlamName = tryBlams.get(oldName).get("blamName");
         if (oldBlamName == null) {
            continue;
         }
         String oldNorm = normalizeBlamName(oldBlamName);
         if (oldNorm.isEmpty()) {
            continue;
         }
         for (String newName : new ArrayList<>(added)) {
            if (renamedNew.contains(newName)) {
               continue;
            }
            String newBlamName = gggBlams.get(newName).get("blamName");
            if (newBlamName != null && oldNorm.equals(normalizeBlamName(newBlamName))) {
               renames.put(oldName, newName);
               renamedOld.add(oldName);
               renamedNew.add(newName);
               break;
            }
         }
      }
      // Trivial class rename: OldName -> OldNameBlam.
      for (String oldName : new ArrayList<>(removed)) {
         if (renamedOld.contains(oldName)) {
            continue;
         }
         String expectedNew = oldName + "Blam";
         if (added.contains(expectedNew)) {
            renames.put(oldName, expectedNew);
            renamedOld.add(oldName);
            renamedNew.add(expectedNew);
         }
      }
      // Manual renames (typo fixes, etc.).
      Map<String, String> manualRenames = new HashMap<>();
      manualRenames.put("CreateNewActionAtLocactionBlam", "CreateNewActionAtLocationBlam");
      for (Map.Entry<String, String> entry : manualRenames.entrySet()) {
         String oldName = entry.getKey();
         String newName = entry.getValue();
         if (removed.contains(oldName) && added.contains(newName)) {
            renames.put(oldName, newName);
            renamedOld.add(oldName);
            renamedNew.add(newName);
         } else if (!renames.containsKey(oldName)) {
            renames.put(oldName, newName);
         }
      }

      removed.removeAll(renamedOld);
      added.removeAll(renamedNew);
      return renames;
   }

   /**
    * Generate the code-diff report ({@code XWidgets_BlamReview_Diff.html}): one small table per BLAM showing OLD vs NEW
    * constructor source and widget-method source, side by side. Covers every BLAM present in both trees - renamed pairs
    * AND same-name survivors - since the code was reorganized/changed regardless of a rename. Deleted and added BLAMs
    * have no counterpart to diff, so they are excluded.
    */
   private static void generateCodeDiff() throws IOException {
      File outDir = jsonDir();
      Path gggJson = new File(outDir, "blam_classes_ggg.json").toPath();
      Path tryJson = new File(outDir, "blam_classes_try.json").toPath();
      if (!Files.exists(gggJson) || !Files.exists(tryJson)) {
         System.err.println("Cannot generate code diff - one or both JSON files missing.");
         return;
      }
      Map<String, Map<String, String>> gggBlams = parseJsonClasses(Files.readString(gggJson));
      Map<String, Map<String, String>> tryBlams = parseJsonClasses(Files.readString(tryJson));

      Set<String> added = new HashSet<>(gggBlams.keySet());
      added.removeAll(tryBlams.keySet());
      Set<String> removed = new HashSet<>(tryBlams.keySet());
      removed.removeAll(gggBlams.keySet());
      Set<String> common = new HashSet<>(gggBlams.keySet());
      common.retainAll(tryBlams.keySet());

      Map<String, String> renames = resolveRenames(added, removed, tryBlams, gggBlams);

      // Build old->new pairs for every BLAM present in both trees: renamed pairs + same-name survivors.
      // Sorted by the NEW name (the row identity used in the report).
      java.util.TreeMap<String, String> newToOld = new java.util.TreeMap<>();
      for (Map.Entry<String, String> e : renames.entrySet()) {
         if (tryBlams.containsKey(e.getKey()) && gggBlams.containsKey(e.getValue())) {
            newToOld.put(e.getValue(), e.getKey());
         }
      }
      for (String name : common) {
         newToOld.put(name, name); // survivor: old name == new name
      }

      StringBuilder html = new StringBuilder();
      html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>BLAM Code Diff</title>");
      // Java syntax highlighting is done by a small, self-contained inline script (below) - NO external library and
      // NO network request. This matters because real runs contain non-open-source code; nothing may be fetched from
      // or sent to any third party. The script only reads the code already in the page and wraps tokens in colored
      // spans (pure DOM manipulation).
      html.append(PacketStyle.highlighterCss());
      html.append(PacketStyle.highlighterScript());
      // Make every table's columns user-resizable: add a drag handle to each header cell that adjusts that column's
      // width live (plain JS, no library). Widths are set on the <th> so table-layout:fixed lays out the columns.
      html.append("<script>document.addEventListener('DOMContentLoaded', function() {");
      html.append("  document.querySelectorAll('table').forEach(function(table) {");
      // Size the first (label) column to just fit its content: measure under auto layout, then lock it in for the
      // fixed layout so the two code columns take the rest.
      html.append("    var firstTh = table.querySelector('th');");
      html.append("    if (firstTh) {");
      html.append("      table.style.tableLayout = 'auto';");
      html.append("      var labelW = firstTh.getBoundingClientRect().width;");
      html.append(
         "      table.querySelectorAll('td.label').forEach(function(td) { " + "labelW = Math.max(labelW, td.getBoundingClientRect().width); });");
      html.append("      table.style.tableLayout = 'fixed';");
      html.append("      firstTh.style.width = Math.ceil(labelW) + 'px';");
      html.append("    }");
      html.append("    table.querySelectorAll('th').forEach(function(th) {");
      html.append("      var handle = document.createElement('div'); handle.className = 'col-resizer';");
      html.append("      th.appendChild(handle);");
      html.append("      var startX, startW;");
      html.append("      handle.addEventListener('mousedown', function(e) {");
      html.append("        startX = e.pageX; startW = th.offsetWidth; e.preventDefault();");
      html.append(
         "        function onMove(ev) { var w = Math.max(30, startW + (ev.pageX - startX)); " + "th.style.width = w + 'px'; }");
      html.append(
         "        function onUp() { document.removeEventListener('mousemove', onMove); " + "document.removeEventListener('mouseup', onUp); }");
      html.append(
         "        document.addEventListener('mousemove', onMove); document.addEventListener('mouseup', onUp);");
      html.append("      });");
      html.append("    });");
      html.append("  });");
      html.append("});</script>");
      // Shared packet palette (matches PacketStyle); this page keeps its own table-layout/resizer CSS on top.
      html.append("<style>");
      html.append(":root{--fw:#1f6feb;--ink:#1f2328;--muted:#656d76;--line:#d0d7de;--soft:#f6f8fa;}");
      html.append("body { font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; margin: 28px 24px 64px; "
         + "font-size: 14px; color: var(--ink); line-height: 1.5; }");
      html.append("h1 { color: var(--ink); font-size: 24px; }");
      html.append("h2 { color: var(--ink); font-size: 18px; margin-top: 28px; padding: 6px 0 6px 12px; "
         + "border-left: 5px solid var(--fw); }");
      // Fixed layout + per-column widths; columns are user-resizable via the drag handles added by JS below. Long
      // code lines wrap inside their cell so the table stays within the browser width by default.
      html.append("table { border-collapse: collapse; width: 100%; margin-bottom: 10px; table-layout: fixed; }");
      html.append(
         "th, td { border: 1px solid var(--line); padding: 6px 10px; text-align: left; vertical-align: top; }");
      html.append("th { background: var(--soft); position: relative; }");
      html.append("td.label { white-space: nowrap; font-weight: bold; background: var(--soft); }");
      // Drag handle on the right edge of each header cell.
      html.append(
         ".col-resizer { position: absolute; top: 0; right: 0; width: 6px; height: 100%; cursor: col-resize; " + "user-select: none; }");
      html.append(".col-resizer:hover { background: var(--fw); }");
      html.append("pre { margin: 0; font-size: 12px; }");
      // Code wraps inside the fixed-width cell (no horizontal scroll); highlighter spans inherit these.
      html.append(
         "pre code { padding: 0; background: transparent; white-space: pre-wrap; " + "overflow-wrap: anywhere; font-family: Consolas, 'Courier New', monospace; }");
      html.append(".unchanged { color: #888; font-style: italic; }");
      html.append(".detail { font-size: 12px; color: var(--muted); }");
      html.append("a { color: var(--fw); }");
      html.append(
         ".summary { background: var(--soft); border-left: 3px solid var(--fw); padding: 12px 16px; "
            + "border-radius: 4px; margin: 12px 0 20px; font-size: 13px; }");
      html.append(".summary ol, .summary ul { margin: 8px 0 0; padding-left: 22px; } .summary li { margin: 4px 0; }");
      html.append("code { background: var(--soft); padding: 1px 5px; border-radius: 4px; "
         + "font-family: Consolas, 'Courier New', monospace; font-size: 12.5px; }");
      html.append("</style></head><body>");
      html.append("<h1>BLAM Code Diff</h1>");
      html.append("<div class='summary'><b>How to use</b><ul>");
      html.append("<li>Per-BLAM <b>old vs new</b> diffs for quick review (incl. renamed classes).</li>");
      html.append(
         "<li><b>Old</b>/<b>New</b> links open the full highlighted source (stored in <code>old_blam/</code> " + "/ <code>new_blam/</code> beside this file - works wherever unzipped).</li>");
      html.append(
         "<li><code>NO_CHANGE</code> = that section is identical (whitespace ignored); old is still shown.</li>");
      html.append(
         "<li>Code is re-indented, so some diffs are just formatting/reordering (see \"Things to note\").</li>");
      html.append("<li><b>Deleted and new-only BLAMs are not shown.</b></li>");
      html.append("<li>Runs locally in your browser - no data leaves this file.</li>");
      html.append("</ul></div>");

      // Things to note - migration-wide changes a reviewer should keep in mind while reading the per-BLAM diffs.
      html.append("<div class='summary'><b>Things to note</b><ol>");
      html.append(
         "<li>Categories are no longer \"hacked\" to denote Admin - that access control moved to " + "<b>User Groups</b>. All BLAMs now use <code>getUserGroups()</code> for access instead of encoding it in " + "categories.</li>");
      html.append(
         "<li>Every BLAM now has a <b>default (no-arg) constructor</b> so it can be loaded by OSGi for " + "registration.</li>");
      html.append(
         "<li><b>Default descriptions</b> were added where missing, generated with AI, so every BLAM has a " + "non-empty description/usage.</li>");
      html.append(
         "<li>Class names were standardized to end in <b>Blam</b>; each concrete BLAM is annotated " + "<code>@Component(service = AbstractBlam.class, immediate = true)</code> (abstract bases are not).</li>");
      html.append(
         "<li>Widget declarations were converted from the old XML <code>getXWidgetsXml()</code> to the " + "<code>XWidgetBuilder</code> <code>getXWidgetItems()</code> pattern (see the Widgets row).</li>");
      html.append(
         "<li><code>getName()</code>/image are no longer overridden - the display name and image are set " + "via the constructor (<code>super(BLAM_NAME, ...)</code>).</li>");
      html.append(
         "<li>Class members were reorganized into the standard order (name, description, labels, fields, " + "constructor, getXWidgetItems, runOperation, helpers, getCategories, getDescriptionUsage), which is why " + "many diffs show pure reordering.</li>");
      html.append("</ol></div>");

      for (Map.Entry<String, String> pair : newToOld.entrySet()) {
         String newName = pair.getKey();
         String oldName = pair.getValue();
         Map<String, String> oldData = tryBlams.get(oldName);
         Map<String, String> newData = gggBlams.get(newName);

         String heading = oldName.equals(newName) ? newName : newName + "  (was: " + oldName + ")";
         html.append("<h2>").append(escapeHtml(heading)).append("</h2>");
         // Old and new file links. Prefer the portable copy under the output dir (old_blam/ | new_blam/) so the
         // report is self-contained; fall back to the original absolute path if no copy was made. Show the original
         // path as the visible text either way (it tells the reviewer where the file lives).
         appendFileLink(html, "Old", oldData.get("copyPath"), oldData.get("path"), outDir);
         appendFileLink(html, "New", newData.get("copyPath"), newData.get("path"), outDir);
         // Abstract base classes: just the heading + file link, no constructor/widget diff table.
         if ("true".equals(newData.get("isAbstract"))) {
            html.append("<p class='detail'>(abstract base class - no widget/constructor diff)</p>");
            continue;
         }
         html.append("<table>");
         html.append("<tr><th></th><th>Old Code</th><th>New Code</th></tr>");
         appendDiffRow(html, "Name", oldData.get("nameCode"), newData.get("nameCode"));
         appendDiffRow(html, "Constructors", oldData.get("constructorCode"), newData.get("constructorCode"));
         appendNewOnlyRow(html, "Widget Labels", newData.get("widgetLabels"));
         appendDiffRow(html, "Widgets", oldData.get("widgetCode"), newData.get("widgetCode"));
         appendDiffRow(html, "Description", oldData.get("description"), newData.get("description"));
         appendDiffRow(html, "User Groups", oldData.get("userGroupsCode"), newData.get("userGroupsCode"));
         appendDiffRow(html, "Categories", oldData.get("categoriesCode"), newData.get("categoriesCode"));
         html.append("</table>");
      }

      html.append("</body></html>");

      File reportFile = CodeQualityUtil.getReportFile(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReview",
         "XWidgets_BlamReview_Diff");
      Files.writeString(reportFile.toPath(), html.toString());
      System.out.println("Code diff written to: " + reportFile.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(reportFile);
      } catch (Exception ex) {
         // ignore
      }
   }

   /**
    * Lightweight re-indenter for display: discards the source's original leading whitespace and re-derives indentation
    * purely from brace depth, so {@code @Override} and the {@code public ...} signature sit at column 0 and each
    * {@code { }} block indents one level (3 spaces, OSEE style). Not a full formatter - it does not re-wrap lines or
    * move code between lines; it only fixes indentation. A line that starts with {@code }} or {@code )} dedents first
    * so closers line up with their opener.
    */
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
         // A line beginning with a closing brace dedents before it is printed, so } lines up with its opener.
         if (line.startsWith("}")) {
            depth = Math.max(0, depth - 1);
         }
         for (int i = 0; i < depth; i++) {
            sb.append("   ");
         }
         sb.append(line).append("\n");
         // Indentation tracks brace depth only (reliable for block structure; parens are ignored). The leading
         // closer, if any, was already accounted for above.
         int delta = count(line, '{') - count(line, '}');
         if (line.startsWith("}")) {
            delta += 1; // undo the pre-dedent for the leading closer so it is not double-counted
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

   /**
    * Normalize code for an "is it really changed?" comparison: unify line endings to \n and strip trailing whitespace
    * on each line (and trailing blank lines). This ignores CRLF-vs-LF and trailing-space differences between the old
    * (try) and new (ggg) trees, so cosmetically-identical method bodies compare equal.
    */
   private static String normalizeCode(String code) {
      String[] lines = code.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line.replaceAll("\\s+$", "")).append("\n");
      }
      // Drop trailing blank lines.
      return sb.toString().replaceAll("\n+$", "\n");
   }

   /**
    * Append one diff row: bold label cell, then the OLD code and NEW code in monospaced blocks. When both are absent
    * shows "(none)"; when the code is identical the OLD cell still shows the code and the NEW cell shows NO_CHANGE.
    */
   private static void appendDiffRow(StringBuilder html, String label, String oldCode, String newCode) {
      html.append("<tr><td class='label'>").append(escapeHtml(label)).append("</td>");
      if (oldCode == null && newCode == null) {
         html.append("<td colspan='2' class='unchanged'>(none)</td></tr>");
         return;
      }
      // Re-indent for display (brace-depth based) so @Override/public line up regardless of the source's leading
      // indentation; compare with normalizeCode (ignores EOL/trailing-whitespace) to decide NO_CHANGE.
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

   /**
    * Append a row that only has New-side content (Old cell is n/a). Used for the Widget Labels row, which is derived
    * solely from the new BLAM's widget method. Emits nothing when there is no content.
    */
   private static void appendNewOnlyRow(StringBuilder html, String label, String newCode) {
      if (newCode == null) {
         return;
      }
      html.append("<tr><td class='label'>").append(escapeHtml(label)).append("</td>");
      html.append("<td class='unchanged'>(n/a)</td>");
      html.append("<td><pre><code class='language-java'>").append(escapeHtml(formatCode(newCode))).append(
         "</code></pre></td></tr>");
   }

   /**
    * Append an "Old:/New:" file link paragraph. Links to the portable copy under {@code outDir} ({@code copyPath}) when
    * available, else the original absolute {@code fullPath}. The visible text is always the original path so the
    * reviewer sees where the file actually lives. Emits nothing when both are null.
    */
   private static void appendFileLink(StringBuilder html, String label, String copyPath, String fullPath, File outDir) {
      String href;
      String visible;
      if (copyPath != null) {
         // RELATIVE href (no file://, no absolute path): the report HTML sits in the same output folder as the
         // old_blam/ and new_blam/ copies, so a relative URL resolves against wherever the folder is unzipped/opened.
         // Portable - links keep working after the bundle is moved or shared.
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

   /**
    * Build a {@code file://} URL for a full path so the browser can open the source file when the link is clicked.
    * Paths are stored forward-slashed; a leading drive letter (Windows) gets the extra {@code /} (file:///C:/...).
    * Spaces are percent-encoded so the href is valid.
    */
   private static String fileUrl(String fullPath) {
      String p = fullPath.replace("\\", "/").replace(" ", "%20");
      return p.startsWith("/") ? "file://" + p : "file:///" + p;
   }

   /**
    * Write a small, self-contained colorized HTML page wrapping the given source file, to {@code <copyDir>/<name>.html}
    * (the raw source is embedded, escaped, in a highlighted code block - the original .java is not copied). Uses the
    * same inline highlighter as the diff report (no external library, no network). A small header shows the file name
    * and which side (old/new) it is.
    *
    * @return the file name written (e.g. {@code Foo.java.html}), or null on failure.
    */
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

   /** Escape a string for safe display inside HTML text/pre content. */
   private static String escapeHtml(String s) {
      return PacketStyle.escapeHtml(s);
   }

   /**
    * Normalize a BLAM display name for rename matching: drop the word "Blam" (any case) and collapse/trim whitespace.
    * Lets a BLAM whose display name only changed by removing "Blam" still match its old counterpart (e.g. "Create New
    * Action Blam" and "Create New Action" both normalize to "create new action").
    */
   private static String normalizeBlamName(String blamName) {
      if (blamName == null) {
         return "";
      }
      // Remove the standalone word "Blam" (case-insensitive), then collapse whitespace and lowercase.
      String stripped = blamName.replaceAll("(?i)\\bBlam\\b", " ");
      return stripped.replaceAll("\\s+", " ").trim().toLowerCase();
   }

   /**
    * Reads the generated JSON files for both workspaces and produces an HTML change report showing: added, removed,
    * renamed, and modified Blams between old (try) and new (ggg).
    */
   private static void generateChangeReport() throws IOException {
      Path gggJson = new File(jsonDir(), "blam_classes_ggg.json").toPath();
      Path tryJson = new File(jsonDir(), "blam_classes_try.json").toPath();

      if (!Files.exists(gggJson) || !Files.exists(tryJson)) {
         System.err.println("Cannot generate change report - one or both JSON files missing.");
         return;
      }

      Map<String, Map<String, String>> gggBlams = parseJsonClasses(Files.readString(gggJson));
      Map<String, Map<String, String>> tryBlams = parseJsonClasses(Files.readString(tryJson));

      // Determine added, removed, common
      Set<String> added = new HashSet<>(gggBlams.keySet());
      added.removeAll(tryBlams.keySet());

      Set<String> removed = new HashSet<>(tryBlams.keySet());
      removed.removeAll(gggBlams.keySet());

      Set<String> common = new HashSet<>(gggBlams.keySet());
      common.retainAll(tryBlams.keySet());

      // Resolve old->new rename pairs (CSV + heuristics). This consumes matched names from added/removed.
      Map<String, String> renames = resolveRenames(added, removed, tryBlams, gggBlams);

      // Filter abstract classes from Added (they're base classes, not user-facing Blams)
      added.removeIf(name -> {
         Map<String, String> data = gggBlams.get(name);
         return data != null && "true".equals(data.get("isAbstract"));
      });

      // Build HTML report
      StringBuilder html = new StringBuilder();
      html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      html.append("<meta name='viewport' content='width=device-width, initial-scale=1'/>");
      html.append("<title>BLAM Change Report</title>");
      html.append("<style>");
      html.append(":root{--fw:#1f6feb;--ink:#1f2328;--muted:#656d76;--line:#d0d7de;--soft:#f6f8fa;}");
      html.append("body { font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; margin: 28px 24px 64px; "
         + "font-size: 14px; color: var(--ink); line-height: 1.5; }");
      html.append("h1 { color: var(--ink); font-size: 24px; }");
      html.append("h2 { color: var(--ink); font-size: 18px; margin-top: 28px; padding: 6px 0 6px 12px; "
         + "border-left: 5px solid var(--fw); }");
      html.append("h3 { color: var(--ink); }");
      html.append(".summary { background: var(--soft); border-left: 3px solid var(--fw); padding: 15px; "
         + "border-radius: 4px; margin-bottom: 20px; }");
      html.append(".added { color: #1a7f37; }");
      html.append(".removed { color: #cf222e; }");
      html.append(".renamed { color: #bc4c00; }");
      html.append(".modified { color: var(--fw); }");
      html.append("a { color: var(--fw); }");
      html.append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }");
      html.append("th, td { border: 1px solid var(--line); padding: 6px 10px; text-align: left; }");
      html.append("th { background: var(--soft); }");
      html.append("tr:nth-child(even) { background: var(--soft); }");
      html.append("code { background: var(--soft); padding: 1px 5px; border-radius: 4px; "
         + "font-family: Consolas, 'Courier New', monospace; font-size: 12.5px; }");
      html.append(".detail { font-size: 12px; color: var(--muted); }");
      html.append("</style></head><body>");

      html.append("<h1>BLAM Change Report</h1>");
      html.append("<div class='summary'>");
      long oldTotalLines = sumLines(tryBlams);
      long newTotalLines = sumLines(gggBlams);

      // Source lines reduced across RENAMED BLAMs: for each old->new pair, oldSLOC - newSLOC. Positive means the
      // renamed BLAM shrank; negative means it grew (added lines) - shown as-is so the direction is visible.
      long renamedLinesReduced = 0;
      for (Map.Entry<String, String> pair : renames.entrySet()) {
         renamedLinesReduced += blamLines(tryBlams, pair.getKey()) - blamLines(gggBlams, pair.getValue());
      }
      // All source lines from fully-DELETED BLAMs (in old, gone in new after rename resolution) - every line removed.
      long deletedBlamLines = 0;
      for (String oldName : removed) {
         deletedBlamLines += blamLines(tryBlams, oldName);
      }
      // Total reduction we care about = reduction from renamed BLAMs + all lines from deleted BLAMs.
      long totalLinesRemoved = renamedLinesReduced + deletedBlamLines;

      html.append(String.format("<b>Old (try):</b> %d blams, %,d source lines<br/>", tryBlams.size(), oldTotalLines));
      html.append(String.format("<b>New (ggg):</b> %d blams, %,d source lines<br/>", gggBlams.size(), newTotalLines));
      html.append(String.format(
         "<b class='removed'>Source lines removed:</b> renamed BLAMs %,d + deleted BLAMs %,d = %,d<br/><br/>",
         renamedLinesReduced, deletedBlamLines, totalLinesRemoved));
      html.append(String.format("<b class='added'>Added:</b> %d<br/>", added.size()));
      html.append(String.format("<b class='removed'>Removed:</b> %d<br/>", removed.size()));
      html.append(String.format("<b class='renamed'>Renamed:</b> %d<br/>", renames.size()));
      html.append(String.format("<b>Names Unchanged:</b> %d<br/>", common.size()));
      html.append("</div>");

      // Renamed
      if (!renames.isEmpty()) {
         html.append("<h2 class='renamed'>Renamed (" + renames.size() + ")</h2>");
         html.append("<table><tr><th>Old Name</th><th>New Name</th><th>BLAM Display Name</th></tr>");
         List<String> sortedRenames = new ArrayList<>(renames.keySet());
         sortedRenames.sort(String::compareTo);
         for (String oldName : sortedRenames) {
            String newName = renames.get(oldName);
            String displayName = gggBlams.containsKey(newName) ? gggBlams.get(newName).get("blamName") : "";
            html.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", oldName, newName,
               displayName != null ? displayName : ""));
         }
         html.append("</table>");
      }

      // Added
      if (!added.isEmpty()) {
         html.append("<h2 class='added'>Added (" + added.size() + ")</h2>");
         html.append("<table><tr><th>Class Name</th><th>BLAM Name</th><th>Lines</th></tr>");
         List<String> sortedAdded = new ArrayList<>(added);
         sortedAdded.sort(String::compareTo);
         for (String name : sortedAdded) {
            Map<String, String> data = gggBlams.get(name);
            html.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", name, nvl(data.get("blamName")),
               nvl(data.get("lineCount"))));
         }
         html.append("</table>");
      }

      // Removed
      if (!removed.isEmpty()) {
         html.append("<h2 class='removed'>Removed (" + removed.size() + ")</h2>");
         html.append("<table><tr><th>Class Name</th><th>BLAM Name</th><th>Lines</th></tr>");
         List<String> sortedRemoved = new ArrayList<>(removed);
         sortedRemoved.sort(String::compareTo);
         for (String name : sortedRemoved) {
            Map<String, String> data = tryBlams.get(name);
            html.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", name, nvl(data.get("blamName")),
               nvl(data.get("lineCount"))));
         }
         html.append("</table>");
      }

      // Unchanged (just names) - BLAMs present in both trees under the same class name.
      List<String> unchanged = new ArrayList<>(common);
      unchanged.sort(String::compareTo);
      html.append("<h2>Unchanged (" + unchanged.size() + ")</h2>");
      html.append("<p class='detail'>");
      html.append(String.join(", ", unchanged));
      html.append("</p>");

      html.append("</body></html>");

      // Write into the dedicated ~/CodeXWidgetReview folder (created on first use with a README), reusing the same
      // output-folder machinery as the CodeClean/CodeReview tooling.
      File reportFile = CodeQualityUtil.getReportFile(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReview",
         "XWidgets_BlamReview_Renamed");
      Files.writeString(reportFile.toPath(), html.toString());
      System.out.println("Change report written to: " + reportFile.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(reportFile);
      } catch (Exception ex) {
         // ignore
      }
   }

   /**
    * Count source lines of code in a Java file: non-blank lines that are not comments. Skips blank lines, whole-line
    * {@code //} comments, and block comments ({@code /* ... *}{@code /}, including Javadoc), tracking block-comment
    * state across lines. A line with code before a trailing {@code //} still counts; a line with code before/after a
    * block comment still counts.
    */
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
                  i = line.length(); // rest of line is inside the block comment
               } else {
                  inBlockComment = false;
                  i = end + 2;
               }
            } else {
               if (line.startsWith("//", i)) {
                  break; // line comment - rest of line is comment
               }
               if (line.startsWith("/*", i)) {
                  inBlockComment = true;
                  i += 2;
               } else {
                  if (!Character.isWhitespace(line.charAt(i))) {
                     hasCode = true; // a real non-comment, non-whitespace character
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

   /** @return the source-line count for a single BLAM by class name, or 0 if unknown/unparseable. */
   private static long blamLines(Map<String, Map<String, String>> blams, String className) {
      Map<String, String> data = blams.get(className);
      if (data == null || data.get("lineCount") == null) {
         return 0;
      }
      try {
         return Long.parseLong(data.get("lineCount"));
      } catch (NumberFormatException ex) {
         return 0;
      }
   }

   private static long sumLines(Map<String, Map<String, String>> blams) {
      long total = 0;
      for (Map<String, String> data : blams.values()) {
         String lineCount = data.get("lineCount");
         if (lineCount != null) {
            try {
               total += Long.parseLong(lineCount);
            } catch (NumberFormatException ex) {
               // skip
            }
         }
      }
      return total;
   }

   private static String nvl(String value) {
      return value == null ? "" : value;
   }

   /**
    * Parse the generated JSON (via Jackson) into a map of className -&gt; field map. Each field value is stored as a
    * string (text for strings, {@code toString()} for numbers/booleans; JSON null -&gt; absent). Using a real JSON
    * parser (not the old split-on-'{' regex) is required so multi-line code fields (constructorCode, widgetCode) that
    * contain braces/quotes/newlines round-trip correctly.
    */
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
}
