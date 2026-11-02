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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.integration.tests.coderev.AbstractCodeClean;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * THROWAWAY refactor script. Converts ATS Work Definitions from the old two-tier "provider enumerates WorkDefs in code"
 * mechanism to per-class OSGI Declarative Services registration via
 * {@code @Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)} - the same pattern BLAMs and
 * XXWidgets already use. After this runs (and tests pass) the enumerating providers and their XML are deleted.
 * <p>
 * Modeled on the CodeClean tooling: extends {@link AbstractCodeClean}, overrides {@link #performClean()},
 * {@code changeFiles = false} (report-only) by default, and (via {@code AbstractQual}) scans only the
 * {@code org.eclipse.osee} repo unless {@code onlyOpenSource} is set false (then every repo under the workspace root).
 * <p>
 * A WorkDef is detected structurally: a class that {@code extends AbstractWorkDef} or {@code extends
 * AbstractDemoWorkDef} (the abstract bases themselves are skipped). This is robust across repos regardless of the token
 * class a WorkDef passes to {@code super(...)}.
 * <p>
 * Per WorkDef (every repo) it:
 * <ol>
 * <li>adds the {@code @Component} annotation above the class declaration (+ the two needed imports);</li>
 * <li>writes a hand-written {@code OSGI-INF/<fully.qualified.ClassName>.xml} descriptor into the owning bundle;</li>
 * <li>for a demo WorkDef ({@code extends AbstractDemoWorkDef}) adds an {@code isApplicable()} override returning
 * {@code DemoUtil.isDemoDatabase()}.</li>
 * </ol>
 * The one-time shared edits (modifying {@code IAtsWorkDefinitionBuilder}, rewriting the collector service, deleting the
 * old providers/XML/interface, adding {@code CodeReviewWorkDefs}) are intentionally NOT done here - they are single,
 * surgical edits to shared files that live only in {@code org.eclipse.osee} and are done/reviewed as normal hand edits.
 * This script does ONLY the bulk, repeatable, per-WorkDef work that must apply identically to every repo.
 *
 * @author Donald G. Dunne
 */
public class CodeRefactor_WorkDefsToOsgi extends AbstractCodeClean {

   private static final String COMPONENT_IMPORT = "import org.osgi.service.component.annotations.Component;";
   private static final String BUILDER_IMPORT = "import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;";
   private static final String DEMO_UTIL_IMPORT = "import org.eclipse.osee.ats.core.demo.DemoUtil;";
   private static final String COMPONENT_ANNOTATION =
      "@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)";

   /** Matches the concrete class declaration line and captures the class name. */
   private static final Pattern CLASS_DECL = Pattern.compile("^public (?:final )?class (\\w+)\\b", Pattern.MULTILINE);
   /** Matches the package declaration and captures the package name. */
   private static final Pattern PACKAGE_DECL = Pattern.compile("^package ([\\w.]+);", Pattern.MULTILINE);
   /** Captures (className, superClassName) from a concrete class declaration with an extends clause. */
   private static final Pattern CLASS_EXTENDS =
      Pattern.compile("^public (?:final )?class (\\w+)\\s+extends\\s+(\\w+)", Pattern.MULTILINE);
   /**
    * A class name that roots the extends-closure: any {@code Abstract...WorkDef} base (AbstractWorkDef,
    * AbstractDemoWorkDef, or a repo-specific AbstractXyzWorkDef). Anchored at the end so it does NOT match
    * {@code AbstractWorkDefItem} (the base of model classes WorkDefinition/StateDefinition/LayoutItem).
    */
   private static final Pattern WORKDEF_ROOT = Pattern.compile("^Abstract\\w*WorkDef$");

   /** Count of OSGI-INF descriptors that would be / were written. */
   private int xmlWritten = 0;

   public CodeRefactor_WorkDefsToOsgi() {
      // Report only by default; set changeFiles = true manually when ready to write.
      changeFiles = true;
      // false = scan ALL sibling repos under the workspace root (ws_ggg); true = only org.eclipse.osee.
      onlyOpenSource = false;
   }

   @Override
   protected String getCleanName() {
      return "CodeRefactor_WorkDefsToOsgi";
   }

   @Override
   protected String getFilePattern() {
      // Any .java; the real filter (extends a WorkDef base) is applied per file in processWorkDef.
      return ".*\\.java";
   }

   @Override
   protected void performClean() {
      List<String> files = findFiles();
      if (files.isEmpty()) {
         rd.logf("No .java files found\n");
         return;
      }
      rd.logf("Scanning %d .java candidate(s) for WorkDefs...\n", files.size());

      // PASS 1: build className -> superclass across all candidate files (excluding coderev tooling), then compute
      // the transitive closure of classes that extend AbstractWorkDef/AbstractDemoWorkDef directly or through another
      // WorkDef (e.g. WorkDefReviewPeerToPeerDemo extends WorkDefReviewPeerToPeer extends AbstractWorkDef).
      Map<String, String> classToSuper = new HashMap<>();
      Map<String, String> classToFile = new HashMap<>();
      for (String filePath : files) {
         if (filePath.replace('\\', '/').contains("/coderev/")) {
            continue;
         }
         try {
            String src = Lib.fileToString(new File(filePath));
            Matcher m = CLASS_EXTENDS.matcher(src);
            if (m.find()) {
               classToSuper.put(m.group(1), m.group(2));
               classToFile.put(m.group(1), filePath);
            }
         } catch (Exception ex) {
            // skip unreadable
         }
      }
      Set<String> workDefClasses = computeWorkDefClosure(classToSuper);
      rd.logf("Detected %d WorkDef class(es) via transitive extends closure.\n", workDefClasses.size());

      // PASS 2: edit each detected WorkDef.
      for (String className : workDefClasses) {
         String filePath = classToFile.get(className);
         if (filePath != null) {
            processWorkDef(filePath);
         }
      }
      rd.logf("\nOSGI-INF descriptors %s: %d\n", changeFiles ? "written" : "that would be written", xmlWritten);

      // One-time shared framework edits. These files exist ONLY in the org.eclipse.osee repo (ats.api/ats.core), so
      // this is a no-op in every other repo. Each step is idempotent (detect-already-done).
      rd.logf("\n=== Shared framework edits (org.eclipse.osee ats.api/ats.core only) ===\n");
      doSharedFrameworkEdits(files);
   }

   private void processWorkDef(String fullFilename) {
      try {
         File file = new File(fullFilename);
         String justFilename = file.getName().replaceFirst("\\.java", "");
         String original = Lib.fileToString(file);

         // The closure (pass 1) already determined this file is a concrete WorkDef; just extract package/class.
         Matcher pkgM = PACKAGE_DECL.matcher(original);
         Matcher clsM = CLASS_DECL.matcher(original);
         if (!pkgM.find() || !clsM.find()) {
            rd.errorf("Could not find package/class decl in [%s]\n", justFilename);
            return;
         }
         String packageName = pkgM.group(1);
         String className = clsM.group(1);
         String fqcn = packageName + "." + className;

         // Demo-gated WorkDefs: those in a .demo package OR whose class name contains "Demo" (covers the two demo
         // WorkDefs that live in non-demo packages). These previously loaded only via the demo provider under
         // DemoUtil.isDemoDatabase(), so they must gate the same way via isApplicable().
         boolean isDemo = packageName.contains(".demo.") || packageName.endsWith(".demo") || className.contains("Demo");

         String updated = original;
         boolean sourceChanged = false;

         // 1. Annotation (idempotent).
         if (!updated.contains(COMPONENT_ANNOTATION)) {
            updated = addComponentAnnotation(updated, className);
            sourceChanged = true;
         }
         // 2. Imports.
         String withImports = ensureImport(updated, COMPONENT_IMPORT);
         withImports = ensureImport(withImports, BUILDER_IMPORT);
         if (isDemo) {
            withImports = ensureImport(withImports, DEMO_UTIL_IMPORT);
         }
         if (!withImports.equals(updated)) {
            updated = withImports;
            sourceChanged = true;
         }
         // 3. Demo isApplicable() override (idempotent).
         if (isDemo && !updated.contains("boolean isApplicable()")) {
            updated = addIsApplicable(updated);
            sourceChanged = true;
         }

         // 4. OSGI-INF descriptor.
         File osgiXml = osgiXmlFileFor(file, fqcn);
         boolean xmlNeeded = osgiXml != null && !osgiXml.exists();

         if (!sourceChanged && !xmlNeeded) {
            return; // already fully converted
         }

         // Apply source edits.
         if (sourceChanged) {
            String output = updated.replace("\n", "\r\n").replace("\r\r\n", "\r\n");
            if (changeFiles) {
               Lib.writeStringToFile(output, file);
            }
            changedFiles.add(justFilename + (isDemo ? " (demo)" : ""));
            rd.logf("%s [%s]%s\n", changeFiles ? "Annotated" : "Would annotate", justFilename,
               isDemo ? " + isApplicable()" : "");
         }
         // Write the descriptor.
         if (xmlNeeded) {
            xmlWritten++;
            rd.logf("  %s OSGI-INF descriptor: %s\n", changeFiles ? "wrote" : "would write",
               shortPath(osgiXml.getAbsolutePath()));
            if (changeFiles) {
               Lib.writeStringToFile(buildOsgiXml(fqcn), osgiXml);
            }
         } else if (osgiXml == null) {
            rd.errorf("Could not resolve OSGI-INF dir (no bundle root with META-INF) for [%s]\n", fqcn);
         }
      } catch (Exception ex) {
         rd.errorf("Error processing [%s]: %s\n", fullFilename, Lib.exceptionToString(ex));
      }
   }

   /**
    * Compute the set of concrete WorkDef classes: every class whose extends-chain reaches an {@code Abstract*WorkDef}
    * root (see {@link #WORKDEF_ROOT}). The abstract bases themselves are excluded (not registerable). Classes whose
    * superclass isn't in the map (e.g. in another bundle/repo not scanned) terminate the chain; only chains that
    * actually reach a root are included.
    */
   private Set<String> computeWorkDefClosure(Map<String, String> classToSuper) {
      Set<String> workDefs = new HashSet<>();
      for (String cls : classToSuper.keySet()) {
         if (reachesRoot(cls, classToSuper, new HashSet<>())) {
            // Exclude the abstract bases themselves (an Abstract* class is never a registerable WorkDef).
            if (!WORKDEF_ROOT.matcher(cls).matches() && !cls.startsWith("Abstract")) {
               workDefs.add(cls);
            }
         }
      }
      return workDefs;
   }

   /** @return true if cls's extends-chain reaches an {@code Abstract*WorkDef} root. {@code seen} guards cycles. */
   private boolean reachesRoot(String cls, Map<String, String> classToSuper, Set<String> seen) {
      if (!seen.add(cls)) {
         return false;
      }
      String sup = classToSuper.get(cls);
      if (sup == null) {
         return false;
      }
      if (WORKDEF_ROOT.matcher(sup).matches()) {
         return true;
      }
      return reachesRoot(sup, classToSuper, seen);
   }

   /** Insert the @Component annotation line immediately above the class declaration line. */
   private String addComponentAnnotation(String src, String className) {
      Pattern p = Pattern.compile("^(public (?:final )?class " + Pattern.quote(className) + "\\b)", Pattern.MULTILINE);
      Matcher m = p.matcher(src);
      if (m.find()) {
         return src.substring(0, m.start()) + COMPONENT_ANNOTATION + "\n" + src.substring(m.start());
      }
      return src;
   }

   /** Add an import line (kept simple: insert after the last existing import) if not already present. */
   private String ensureImport(String src, String importLine) {
      if (src.contains(importLine)) {
         return src;
      }
      int lastImport = src.lastIndexOf("\nimport ");
      if (lastImport < 0) {
         // No imports; insert after the package line.
         Matcher pkg = PACKAGE_DECL.matcher(src);
         if (pkg.find()) {
            int end = src.indexOf('\n', pkg.end());
            return src.substring(0, end + 1) + "\n" + importLine + "\n" + src.substring(end + 1);
         }
         return src;
      }
      int eol = src.indexOf('\n', lastImport + 1);
      return src.substring(0, eol + 1) + importLine + "\n" + src.substring(eol + 1);
   }

   /** Insert an isApplicable() override just before the final closing brace of the class. */
   private String addIsApplicable(String src) {
      int lastBrace = src.lastIndexOf('}');
      if (lastBrace < 0) {
         return src;
      }
      String method =
         "\n   @Override\n   public boolean isApplicable() {\n" + "      return DemoUtil.isDemoDatabase();\n   }\n";
      return src.substring(0, lastBrace) + method + src.substring(lastBrace);
   }

   /**
    * Resolve the OSGI-INF descriptor file for a class: walk up from the .java file to the bundle root (the directory
    * that contains META-INF), then {@code <bundleRoot>/OSGI-INF/<fqcn>.xml}. Returns null if no bundle root found.
    */
   private File osgiXmlFileFor(File javaFile, String fqcn) {
      File dir = javaFile.getParentFile();
      while (dir != null) {
         if (new File(dir, "META-INF").isDirectory()) {
            File osgiInf = new File(dir, "OSGI-INF");
            return new File(osgiInf, fqcn + ".xml");
         }
         dir = dir.getParentFile();
      }
      return null;
   }

   private String buildOsgiXml(String fqcn) {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<scr:component xmlns:scr=\"http://www.osgi.org/xmlns/scr/v1.1.0\" immediate=\"true\" name=\"" + fqcn + "\">\r\n" //
         + "   <service>\r\n" //
         + "      <provide interface=\"org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder\"/>\r\n" //
         + "   </service>\r\n" //
         + "   <implementation class=\"" + fqcn + "\"/>\r\n" //
         + "</scr:component>\r\n";
   }

   // =========================================================================
   // One-time shared framework edits (only present in org.eclipse.osee)
   // =========================================================================

   /**
    * Apply the one-time framework changes. Each locates its target by simple file name among the discovered files, so
    * it fires only in the repo that actually contains that file (org.eclipse.osee). All steps are idempotent.
    */
   private void doSharedFrameworkEdits(List<String> files) {
      // 1. IAtsWorkDefinitionBuilder: add default isApplicable().
      File builderIface = findByName(files, "IAtsWorkDefinitionBuilder.java");
      if (builderIface != null) {
         addIsApplicableToInterface(builderIface);
      }
      // 2. Rewrite the collector to the @Component + @Reference(IAtsWorkDefinitionBuilder) model.
      File collector = findByName(files, "AtsWorkDefinitionProviderService.java");
      if (collector != null) {
         rewriteCollector(collector);
      }
      // 3. Drop addWorkDefinitionProvider from the service interface.
      File providerSvcIface = findByName(files, "IAtsWorkDefinitionProviderService.java");
      if (providerSvcIface != null) {
         trimProviderServiceInterface(providerSvcIface);
      }
      // 4. Delete the old providers, their XML, and the now-unused provider interface.
      deleteIfPresent(findByName(files, "AtsWorkDefinitionProvider.java"));
      deleteIfPresent(findByName(files, "AtsWorkDefinitionProviderDemo.java"));
      deleteIfPresent(findByName(files, "IAtsWorkDefinitionProvider.java"));
      deleteBundleFile(collector, "OSGI-INF/ats.work.definition.provider.service.xml");
      deleteBundleFile(collector, "OSGI-INF/ats.work.definition.provider.demo.xml");
   }

   private File findByName(List<String> files, String simpleName) {
      for (String f : files) {
         if (new File(f).getName().equals(simpleName)) {
            return new File(f);
         }
      }
      return null;
   }

   private void addIsApplicableToInterface(File ifaceFile) {
      try {
         String src = Lib.fileToString(ifaceFile);
         if (src.contains("boolean isApplicable()")) {
            return; // already done
         }
         // Insert the default method right after the build(); declaration.
         String marker = "public WorkDefinition build();";
         int idx = src.indexOf(marker);
         if (idx < 0) {
            rd.errorf("Could not find build() in IAtsWorkDefinitionBuilder - skipping isApplicable()\n");
            return;
         }
         int after = src.indexOf('\n', idx) + 1;
         String method =
            "\n   /**\n" + "    * @return true if this work definition should be registered in the current environment. Default\n" + "    * true; demo/test work defs override to gate on DemoUtil.isDemoDatabase().\n" + "    */\n" + "   default boolean isApplicable() {\n      return true;\n   }\n";
         String updated = src.substring(0, after) + method + src.substring(after);
         writeShared(ifaceFile, updated, "IAtsWorkDefinitionBuilder: added default isApplicable()");
      } catch (Exception ex) {
         rd.errorf("Error editing IAtsWorkDefinitionBuilder: %s\n", Lib.exceptionToString(ex));
      }
   }

   private void rewriteCollector(File collectorFile) {
      try {
         String src = Lib.fileToString(collectorFile);
         if (src.contains("@Reference") && src.contains("addWorkDefinitionBuilder")) {
            return; // already rewritten
         }
         writeShared(collectorFile, COLLECTOR_NEW_SOURCE,
            "AtsWorkDefinitionProviderService: rewritten to @Component + @Reference(IAtsWorkDefinitionBuilder)");
         // The collector is now a @Component - ensure a descriptor exists (the old provider.service.xml is deleted).
         File osgiXml = osgiXmlFileFor(collectorFile,
            "org.eclipse.osee.ats.core.workdef.internal.AtsWorkDefinitionProviderService");
         if (osgiXml != null && !osgiXml.exists()) {
            String xml =
               "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<scr:component xmlns:scr=\"http://www.osgi.org/xmlns/scr/v1.1.0\" immediate=\"true\" " + "name=\"org.eclipse.osee.ats.core.workdef.internal.AtsWorkDefinitionProviderService\">\r\n" + "   <implementation class=\"org.eclipse.osee.ats.core.workdef.internal.AtsWorkDefinitionProviderService\"/>\r\n" + "   <reference bind=\"addWorkDefinitionBuilder\" unbind=\"removeWorkDefinitionBuilder\" cardinality=\"0..n\" " + "interface=\"org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder\" " + "name=\"IAtsWorkDefinitionBuilder\" policy=\"dynamic\"/>\r\n" + "   <service>\r\n" + "      <provide interface=\"org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService\"/>\r\n" + "   </service>\r\n" + "</scr:component>\r\n";
            rd.logf("  %s collector OSGI-INF descriptor: %s\n", changeFiles ? "wrote" : "would write",
               shortPath(osgiXml.getAbsolutePath()));
            if (changeFiles) {
               Lib.writeStringToFile(xml, osgiXml);
            }
         }
      } catch (Exception ex) {
         rd.errorf("Error rewriting collector: %s\n", Lib.exceptionToString(ex));
      }
   }

   private void trimProviderServiceInterface(File ifaceFile) {
      try {
         String src = Lib.fileToString(ifaceFile);
         if (!src.contains("addWorkDefinitionProvider")) {
            return; // already trimmed
         }
         // Remove the addWorkDefinitionProvider(...) declaration line and the now-unused provider import.
         String updated = src.replaceAll(
            "\\s*void addWorkDefinitionProvider\\(IAtsWorkDefinitionProvider workDefProvider\\);\\r?\\n", "\n");
         updated = updated.replaceAll(
            "import org\\.eclipse\\.osee\\.ats\\.api\\.workdef\\.IAtsWorkDefinitionProvider;\\r?\\n", "");
         writeShared(ifaceFile, updated, "IAtsWorkDefinitionProviderService: removed addWorkDefinitionProvider(...)");
      } catch (Exception ex) {
         rd.errorf("Error trimming IAtsWorkDefinitionProviderService: %s\n", Lib.exceptionToString(ex));
      }
   }

   private void deleteIfPresent(File file) {
      if (file == null || !file.exists()) {
         return;
      }
      rd.logf("  %s source file: %s\n", changeFiles ? "deleted" : "would delete", shortPath(file.getAbsolutePath()));
      changedFiles.add("DELETE " + file.getName());
      if (changeFiles) {
         file.delete();
      }
   }

   /** Delete a bundle-relative file (e.g. an OSGI-INF descriptor), resolving the bundle root from a known file. */
   private void deleteBundleFile(File anchorInBundle, String bundleRelPath) {
      if (anchorInBundle == null) {
         return;
      }
      File dir = anchorInBundle.getParentFile();
      while (dir != null) {
         if (new File(dir, "META-INF").isDirectory()) {
            File target = new File(dir, bundleRelPath);
            deleteIfPresent(target);
            return;
         }
         dir = dir.getParentFile();
      }
   }

   private void writeShared(File file, String content, String desc) {
      String output = content.replace("\n", "\r\n").replace("\r\r\n", "\r\n");
      rd.logf("  %s %s\n", changeFiles ? "APPLIED:" : "WOULD APPLY:", desc);
      changedFiles.add(desc);
      if (changeFiles) {
         try {
            Lib.writeStringToFile(output, file);
         } catch (IOException ex) {
            System.err.println(Lib.exceptionToString(ex));
         }
      }
   }

   /** The full replacement source for the rewritten collector service. */
   private static final String COLLECTOR_NEW_SOURCE =
      "/*********************************************************************\n" + " * Copyright (c) 2019 Boeing\n" + " *\n" + " * This program and the accompanying materials are made\n" + " * available under the terms of the Eclipse Public License 2.0\n" + " * which is available at https://www.eclipse.org/legal/epl-2.0/\n" + " *\n" + " * SPDX-License-Identifier: EPL-2.0\n" + " *\n" + " * Contributors:\n" + " *     Boeing - initial API and implementation\n" + " **********************************************************************/\n" + "package org.eclipse.osee.ats.core.workdef.internal;\n" + "\n" + "import java.util.Collection;\n" + "import java.util.HashMap;\n" + "import java.util.Map;\n" + "import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;\n" + "import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;\n" + "import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;\n" + "import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;\n" + "import org.osgi.service.component.annotations.Component;\n" + "import org.osgi.service.component.annotations.Reference;\n" + "import org.osgi.service.component.annotations.ReferenceCardinality;\n" + "import org.osgi.service.component.annotations.ReferencePolicy;\n" + "import org.osgi.service.component.annotations.ReferencePolicyOption;\n" + "\n" + "/**\n" + " * Collects all OSGI-registered {@link IAtsWorkDefinitionBuilder} services, builds the applicable ones, and\n" + " * serves the merged set. Should not be used by applications, only by the WorkDefinitionService.\n" + " *\n" + " * @author Donald G. Dunne\n" + " */\n" + "@Component(service = IAtsWorkDefinitionProviderService.class, immediate = true)\n" + "public class AtsWorkDefinitionProviderService implements IAtsWorkDefinitionProviderService {\n" + "\n" + "   private static final Map<Long, WorkDefinition> idToWorkDef = new HashMap<>();\n" + "\n" + "   @Reference(service = //\n" + "   IAtsWorkDefinitionBuilder.class, //\n" + "      cardinality = ReferenceCardinality.MULTIPLE, //\n" + "      policy = ReferencePolicy.DYNAMIC, //\n" + "      policyOption = ReferencePolicyOption.GREEDY, //\n" + "      bind = \"addWorkDefinitionBuilder\" //\n" + "   )\n" + "   public void addWorkDefinitionBuilder(IAtsWorkDefinitionBuilder builder) {\n" + "      if (!builder.isApplicable()) {\n" + "         return;\n" + "      }\n" + "      WorkDefinition workDef = builder.build();\n" + "      if (workDef == null) {\n" + "         return;\n" + "      }\n" + "      WorkDefinition existing = idToWorkDef.get(workDef.getId());\n" + "      if (existing != null && !existing.getName().equals(workDef.getName())) {\n" + "         throw new OseeArgumentException(\"Work Definitions must have unique id; duplicate for [%s]-[%s]\",\n" + "            workDef.getId(), workDef.getName());\n" + "      }\n" + "      if (workDef.getResults().isErrors()) {\n" + "         throw new OseeArgumentException(\"Exception Building WorkDef [%s]: %s\", workDef.getName(),\n" + "            workDef.getResults().toString());\n" + "      }\n" + "      idToWorkDef.put(workDef.getId(), workDef);\n" + "   }\n" + "\n" + "   public AtsWorkDefinitionProviderService() {\n" + "      // for osgi\n" + "   }\n" + "\n" + "   @Override\n" + "   public WorkDefinition getWorkDefinition(Long id) {\n" + "      return idToWorkDef.get(id);\n" + "   }\n" + "\n" + "   @Override\n" + "   public Collection<WorkDefinition> getAll() {\n" + "      return idToWorkDef.values();\n" + "   }\n" + "\n" + "   @Override\n" + "   public void addWorkDefinition(WorkDefinition workDef) {\n" + "      idToWorkDef.put(workDef.getId(), workDef);\n" + "   }\n" + "\n" + "   @Override\n" + "   public void clearCaches() {\n" + "      // Intentional no-op. Work Definitions are immutable code, registered once as their @Component builders\n" + "      // bind at startup; DS never re-fires those binds, so a cleared cache could never be repopulated and\n" + "      // nothing about WorkDefs changes at runtime - there is nothing to clear.\n" + "   }\n" + "\n" + "}\n";

   public static void main(String[] args) {
      new CodeRefactor_WorkDefsToOsgi().run();
   }

}
