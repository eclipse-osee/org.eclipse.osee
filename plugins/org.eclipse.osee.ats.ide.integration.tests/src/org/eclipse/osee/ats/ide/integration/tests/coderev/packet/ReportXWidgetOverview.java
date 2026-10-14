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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.osee.ats.ide.integration.tests.coderev.CodeQualityUtil;

/**
 * Generates {@code XWidget_Overview.html} - the single entry point into the XWidget -> XXWidget migration review
 * packet. It is a self-contained, offline page (no external libraries, no network) that explains the migration, its
 * safety/security posture, and the {@code XXAbstractBaseXWidget} base, then links to the nine per-area review reports
 * (XWidget / BLAM / Work Definition, each with Rename, Diff, and Testing).
 * <p>
 * All links are RELATIVE, so the whole {@code CodeXWidgetReview} folder can be zipped and opened anywhere. Written to
 * {@code ~/CodeXWidgetReview/XWidget_Overview.html} via the shared {@link CodeQualityUtil} output folder (created on
 * first use with a README).
 * <p>
 * This Overview supersedes the old {@code coderev/CodeReviewReadMe.md}: the durable "how the code-review tooling
 * works" content is folded into the Review Tooling section below and into the {@code ide-xwidget-to-xxwidget-migration}
 * steering doc.
 *
 * @author Donald G. Dunne
 */
public class ReportXWidgetOverview {

   // Report file names this Overview links to (siblings in the same folder). Some may not exist yet - the links
   // degrade gracefully (the browser simply reports a missing file if a report has not been generated).
   private static final String XWIDGET_RENAME = "XWidgets_XWidgetReview_Rename.html";
   private static final String XWIDGET_DIFF = "XWidgets_XWidgetReview_Diff.html";
   private static final String XWIDGET_TESTING = "XWidgets_XWidgetReview_Testing.html";
   private static final String BLAM_RENAME = "XWidgets_BlamReview_Renamed.html";
   private static final String BLAM_DIFF = "XWidgets_BlamReview_Diff.html";
   private static final String BLAM_TESTING = "XWidgets_BlamReview_Testing.html";
   private static final String WORKDEF_DIFF = "XWidgets_WorkDefReview_Diff.html";
   private static final String WORKDEF_TESTING = "XWidgets_WorkDefReview_Testing.html";

   /** Subfolder (beside the Overview) holding highlighted copies of referenced source files; portable relative links. */
   private static final String REF_SUBDIR = "reference_src";

   /** Workspace root to pull referenced source from (the "new"/ggg tree). Only used to copy files into the packet. */
   private static final String SRC_ROOT = "C:/Tools/git_ggg/org.eclipse.osee";

   /** The {@code ws_ggg} workspace root scanned by the report generators (parent of {@link #SRC_ROOT}). */
   private static final String WS_GGG_ROOT = "C:/Tools/git_ggg";

   /**
    * Reference source files to copy into the packet so the Overview can link to real source. Key = simple class name
    * (what the prose mentions); value = repo-relative path under {@link #SRC_ROOT}. Add a line here to make another
    * file linkable.
    */
   private static final Map<String, String> REFERENCE_SOURCES = new LinkedHashMap<>();
   static {
      REFERENCE_SOURCES.put("XOption",
         "plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/XOption.java");
      REFERENCE_SOURCES.put("XXAbstractBaseXWidget",
         "plugins/org.eclipse.osee.framework.ui.skynet/src/org/eclipse/osee/framework/ui/skynet/widgets/xx/"
            + "XXAbstractBaseXWidget.java");
      REFERENCE_SOURCES.put("WidgetId",
         "plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/WidgetId.java");
      REFERENCE_SOURCES.put("WidgetIdAts",
         "plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/util/WidgetIdAts.java");
      String coderev = "plugins/org.eclipse.osee.ats.ide.integration.tests/src/org/eclipse/osee/ats/ide/integration/"
         + "tests/coderev/";
      REFERENCE_SOURCES.put("CodeReviewAll", coderev + "CodeReviewAll.java");
      REFERENCE_SOURCES.put("CodeReviewGeneral", coderev + "CodeReviewGeneral.java");
      REFERENCE_SOURCES.put("CodeReviewXWidgets", coderev + "CodeReviewXWidgets.java");
      REFERENCE_SOURCES.put("CodeReviewBlams", coderev + "CodeReviewBlams.java");
      REFERENCE_SOURCES.put("CodeReviewOsgiXml", coderev + "CodeReviewOsgiXml.java");
      REFERENCE_SOURCES.put("CodeCleanAll", coderev + "CodeCleanAll.java");
      REFERENCE_SOURCES.put("CodeCleanOrganizeXWidgets", coderev + "CodeCleanOrganizeXWidgets.java");
      REFERENCE_SOURCES.put("CodeCleanOrganizeBlams", coderev + "CodeCleanOrganizeBlams.java");
      REFERENCE_SOURCES.put("CodeCleanFormatAndOrganize", coderev + "CodeCleanFormatAndOrganize.java");
      REFERENCE_SOURCES.put("CodeCleanRevertNonFunctional", coderev + "CodeCleanRevertNonFunctional.java");
      String pkt = coderev + "packet/";
      REFERENCE_SOURCES.put("GenerateFullReviewPacket", pkt + "GenerateFullReviewPacket.java");
      REFERENCE_SOURCES.put("ReportXWidgetChanges", pkt + "ReportXWidgetChanges.java");
      REFERENCE_SOURCES.put("ReportBlamChanges", pkt + "ReportBlamChanges.java");
      REFERENCE_SOURCES.put("ReportWorkDefChange", pkt + "ReportWorkDefChange.java");
      REFERENCE_SOURCES.put("ReportXWidgetOverview", pkt + "ReportXWidgetOverview.java");
      REFERENCE_SOURCES.put("ReportXWidgetMapping", pkt + "ReportXWidgetMapping.java");
   }

   /**
    * Supporting Information topics: class names that get a short description entry in the Supporting Information
    * section AND auto-link every inline <code>ClassName</code> mention to that entry. Value = {repo path under
    * SRC_ROOT, one-line description}. Add a class here (classes only - not methods/options) to make it a linked topic.
    * The source file is also copied into {@link #REFERENCE_SOURCES} so the entry can link to the real code.
    */
   private static final Map<String, String[]> SUPPORT_TOPICS = new LinkedHashMap<>();
   static {
      SUPPORT_TOPICS.put("WidgetId", new String[] {
         "plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/WidgetId.java",
         "A lightweight, compile-time token that names a widget - a typed, greppable identity used to reference and "
            + "register widgets instead of error-prone widget name strings. A widget's token has the <b>same name as "
            + "its class</b>, so find-references on the token locates every use. Because the tokens live in the "
            + "<b>API/core code</b> (not just the IDE client), the same widget references can be shared by other "
            + "interfaces - e.g. a future <b>Web</b> front end - rather than being tied to the Eclipse client."});
      SUPPORT_TOPICS.put("XWidgetData", new String[] {
         "plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/XWidgetData.java",
         "The data object that describes a widget instance - the attribute/relation it is bound to, its label, and "
            + "its resolved <code>XOption</code>s. Widgets are built from this data via builders rather than from XML "
            + "strings."});
      SUPPORT_TOPICS.put("WidgetDef", new String[] {
         "plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/WidgetDef.java",
         "A Work Definition's declaration of a single widget. The pre-migration form referenced widgets by string "
            + "name; the migration moves toward attribute-typed declarations."});
      SUPPORT_TOPICS.put("AttrWidgetDef", new String[] {
         "plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/AttrWidgetDef.java",
         "The attribute-type-driven Work Definition widget declaration that replaces string widget names - the widget "
            + "and its options resolve from the attribute type plus <code>XOption</code>s."});
   }

   /** className -> relative link to its highlighted copy (e.g. {@code reference_src/XOption.java.html}); set at run. */
   private final Map<String, String> refLinks = new LinkedHashMap<>();

   public static void main(String[] args) throws IOException {
      new ReportXWidgetOverview().run();
   }

   public void run() throws IOException {
      File dir = CodeQualityUtil.getOutputDir(CodeQualityUtil.CODE_XWIDGET_REVIEW_DIR, "CodeXWidgetReview");
      copyReferenceSources(dir);

      StringBuilder h = new StringBuilder();
      h.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>XWidget Migration Review</title>");
      appendStyle(h);
      h.append("</head><body>");

      h.append("<h1>XWidget &rarr; XXWidget Migration Review</h1>");

      appendCuiWarning(h);

      h.append("<p class='lead'>This is the entry point for reviewing the OSEE widget-framework migration "
         + "(commit <code>feature[TW29349]</code>). It is a self-contained packet: open the linked reports to review "
         + "the renames, the before/after code, and the testing for each area.</p>");

      appendToc(h);
      appendSafetyAndSecurity(h);
      appendWhatChanged(h);
      appendArchitectureSection(h);
      appendXWidgetsSection(h);
      appendReviewToolingSection(h);
      appendAreaSection(h, "xwidgets", "XWidgets", String.format(
         "Widget classes: the <code>X...</code> family consolidated into the standardized "
            + "<code>XX...Widget</code> framework (%d old &rarr; %d new, %d deleted).",
         ReportXWidgetMapping.getOldWidgetCount(), ReportXWidgetMapping.getNewWidgetCount(),
         ReportXWidgetMapping.getDeletedWidgetCount()), XWIDGET_RENAME, "Consolidation Overview",
         "every new widget grouped by purpose, with the old widget(s) it replaced.", XWIDGET_DIFF, XWIDGET_TESTING,
         null);
      appendAreaSection(h, "blams", "BLAMs", "BLAM operations: converted from XML <code>getXWidgetsXml()</code> to the "
         + "type-safe <code>XWidgetBuilder</code> <code>getXWidgetItems()</code> pattern, standardized, and given a "
         + "test harness.", BLAM_RENAME, "Renames", "old &rarr; new name mapping.", BLAM_DIFF, BLAM_TESTING,
         "The BLAM migration changed <b>names</b>, <b>constructors</b>, and <b>how widgets are declared</b> "
            + "(XML &rarr; builder) - <b>it did not rewrite each BLAM's internal operation</b>. That is why "
            + "many of the diffs you will see are renames, reordering, and declaration changes rather than behavioral "
            + "change.");
      appendAreaSection(h, "workdefs", "Work Definitions", "Work Definitions simplified to define widgets by "
         + "<b>attribute type</b> (<code>AttrWidgetDef</code>) with <code>WidgetId</code> tokens and <code>XOption</code>s "
         + "instead of string widget names.", null, null, null, WORKDEF_DIFF, WORKDEF_TESTING, null);

      appendSupportingInformation(h);

      h.append("</body></html>");

      // Link every XOption mention across the board to the copied source (only if the copy succeeded). The plural
      // "XOptions" works too since the trailing 's' is outside the </code>. The <h2>/<b> heading uses include the
      // tag, so we replace the bare "<code>XOption</code>" token wherever it appears.
      String html = h.toString();
      String xoptionLink = refLinks.get("XOption");
      if (xoptionLink != null) {
         // Plural first so "XOptions" links as one word; then the singular. (Order matters - the plural replace must
         // run before the singular one touches the same <code>XOption</code> token.)
         html = html.replace("<code>XOption</code>s",
            "<a href='" + xoptionLink + "' target='_blank' rel='noopener'><code>XOptions</code></a>");
         html = html.replace("<code>XOption</code>",
            "<a href='" + xoptionLink + "' target='_blank' rel='noopener'><code>XOption</code></a>");
      }

      // Inline mentions of each Supporting Information class link to its entry (not straight to source). Each class is
      // wrapped in its own exact "<code>ClassName</code>" token, so e.g. "<code>WidgetId</code>" never matches
      // "<code>WidgetIdAts</code>" - WidgetIdAts is reachable via WidgetId's entry and is not auto-linked inline.
      for (String className : SUPPORT_TOPICS.keySet()) {
         html = html.replace("<code>" + className + "</code>",
            "<a href='#" + supportAnchor(className) + "'><code>" + className + "</code></a>");
      }

      File out = new File(dir, "XWidget_Overview.html");
      Files.writeString(out.toPath(), html);
      System.out.println("XWidget overview written to: " + out.getAbsolutePath());
      try {
         java.awt.Desktop.getDesktop().open(out);
      } catch (Exception ex) {
         // ignore (e.g. headless)
      }
   }

   private void appendToc(StringBuilder h) {
      h.append("<div class='toc'><b>Contents</b><ol>");
      h.append("<li><a href='#safety'>Safety &amp; Security</a></li>");
      h.append("<li><a href='#what'>What changed &amp; why</a></li>");
      h.append("<li><a href='#architecture'>NEW XXWidget Architecture / Major Features</a></li>");
      h.append("<li><a href='#xxbase'>One widget to rule them all: XXAbstractBaseXWidget</a></li>");
      h.append("<li><a href='#tooling'>Review &amp; Code-Quality Tooling</a></li>");
      h.append("<li><a href='#xwidgets'>XWidgets review</a></li>");
      h.append("<li><a href='#blams'>BLAMs review</a></li>");
      h.append("<li><a href='#workdefs'>Work Definitions review</a></li>");
      h.append("<li><a href='#support'>Supporting Information</a></li>");
      h.append("</ol></div>");
   }

   /**
    * Prominent banner stating the packet's data sensitivity and listing exactly which repositories were scanned (every
    * {@code ws_ggg/<repo>} that has a {@code plugins/} dir). The report generators scan all those repos, so the packet
    * can contain CUI from any of them.
    */
   private void appendCuiWarning(StringBuilder h) {
      List<String> repos = scannedRepos();
      h.append("<div class='cui'>");
      h.append("<b>CUI &mdash; CONTROLLED UNCLASSIFIED INFORMATION.</b> This review packet was generated across "
         + "<b>all repositories</b> listed below and <b>CONTAINS CUI</b>. Handle, store, and share it accordingly - "
         + "treat every page (and the copied source it embeds) as CUI.");
      if (repos.isEmpty()) {
         h.append("<p class='repos'>Repositories scanned: <i>none detected under <code>").append(
            escapeHtml(WS_GGG_ROOT)).append("</code></i>.</p>");
      } else {
         h.append("<p class='repos'>Repositories scanned (each has a <code>plugins/</code> dir under <code>").append(
            escapeHtml(WS_GGG_ROOT)).append("</code>):</p><ul class='repos'>");
         for (String r : repos) {
            h.append("<li><code>").append(escapeHtml(r)).append("</code></li>");
         }
         h.append("</ul>");
      }
      h.append("</div>");
   }

   /** @return sorted names of every immediate subdir of {@link #WS_GGG_ROOT} that contains a {@code plugins/} dir. */
   private List<String> scannedRepos() {
      List<String> repos = new ArrayList<>();
      File[] children = new File(WS_GGG_ROOT).listFiles();
      if (children != null) {
         for (File child : children) {
            if (child.isDirectory() && new File(child, "plugins").isDirectory()) {
               repos.add(child.getName());
            }
         }
      }
      repos.sort(String::compareTo);
      return repos;
   }

   private void appendSafetyAndSecurity(StringBuilder h) {
      h.append("<h2 id='safety'>Safety &amp; Security</h2>");
      h.append("<div class='box'><ul>");
      h.append("<li><b>This packet contains CUI.</b> It was generated across all repositories listed in the banner "
         + "above and embeds source from each, which may include non-open-source / CUI code. Handle it as CUI.</li>");
      h.append("<li>This is a <b>self-contained review packet</b>. No external libraries or scripts are pulled from "
         + "the internet while viewing it - every page runs entirely in your browser and no page makes any network "
         + "request, so the CUI it contains is not transmitted anywhere by the packet itself.</li>");
      h.append("<li>Every page in this packet was <b>generated by scripts</b> (the report generators listed in the "
         + "Review &amp; Code-Quality Tooling section), not hand-written - they scan the repositories and emit this "
         + "HTML.</li>");
      h.append("<li>The <b>refactors were likewise done by having Kiro (the AI) write refactor scripts against open "
         + "source only</b>. Those scripts and the generators were then run - by a person - locally across all "
         + "repositories. <b>Kiro never had access to any non-open-source repository</b>; it authored the tooling and "
         + "only ever saw open source. The CUI in this packet was produced by a person running that tooling, not by "
         + "the AI.</li>");
      h.append("</ul></div>");
   }

   private void appendWhatChanged(StringBuilder h) {
      h.append("<h2 id='what'>What changed &amp; why</h2>");
      h.append("<p>The entire OSEE widget framework was re-architected from the inconsistent <code>X...</code> family "
         + "into one standardized <code>XX...</code> framework built on <code>XXAbstractBaseXWidget&lt;T&gt;</code>, "
         + "driven by <code>XOption</code>/<code>XWidgetData</code> and referenced by <code>WidgetId</code> tokens.</p>");

      int oldW = ReportXWidgetMapping.getOldWidgetCount();
      int newW = ReportXWidgetMapping.getNewWidgetCount();
      int delW = ReportXWidgetMapping.getDeletedWidgetCount();
      double ratio = newW == 0 ? 0 : (double) oldW / newW;
      h.append(String.format("<p><b>Scale:</b> commit <code>feature[TW29349]</code> &mdash; ~1,424 files changed "
         + "(~155,413 insertions, ~52,070 deletions). Widget consolidation ~%.1f old &rarr; 1 new "
         + "(%d old names &rarr; %d new XXWidgets, %d deleted).</p>", ratio, oldW, newW, delW));

      h.append("<p><b>Why It Matters and Why We Did It:</b></p><ul>");
      h.append("<li><b>Consistency</b> - one widget per data type gives the whole UI a uniform look, feel, and "
         + "behavior, and far fewer widgets/tests to maintain.</li>");
      h.append("<li><b>Usability</b> - a common look, feel, and function greatly improves usability and "
         + "trainability.</li>");
      h.append("<li><b>Maintainability</b> - one shared implementation means a fix in one place fixes every widget "
         + "at once, instead of hunting down the same bug across dozens of classes.</li>");
      h.append("<li><b>Performance</b> - cleaning up and optimizing how each widget loads, changes, and refreshes its "
         + "data improves performance.</li>");
      h.append("<li><b>Open &rarr; edit &rarr; auto-persist</b> - every widget persists immediately, fixing lost-edit "
         + "and concurrent-save problems and removing the separate save step users forget.</li>");
      h.append("<li><b>Small common persistence</b> - a single <code>handleSelectedPersist()</code> on the "
         + "base (attribute + relation), a prerequisite for <b>server-side persist</b>.</li>");
      h.append("<li><b>One structure across the board</b> - a very simple, common structure shared by XWidgets, "
         + "BLAMs, and Work Definitions eases creating the needed REST calls and web interfaces, and <b>finally gets "
         + "the client to stop talking directly to the database</b>.</li>");
      h.append("<li><b>Type safety</b> - XML widget strings were eliminated; widgets are referenced by "
         + "<code>WidgetId</code> tokens and built from <code>XWidgetData</code> via builders (compile-time checked, "
         + "greppable with find-references).</li>");
      h.append("<li><b>A path to the web</b> - one widget contract can be rendered by multiple front ends, easing the "
         + "move of the Workflow Editor, BLAMs, and Artifact Editor to the web.</li>");
      h.append("</ul>");
   }

   private void appendArchitectureSection(StringBuilder h) {
      h.append("<h2 id='architecture'>NEW XXWidget Architecture / Major Features</h2>");
      h.append("<ul>");
      h.append("<li><b>Same Widgets Used By All UIs:</b><ul>");
      h.append("<li><b>Artifact-Based UIs</b> - Artifact Editor, Workflow Editor.<ul>");
      h.append("<li>Majority of XWidgets chosen automatically based on the OSEE types definition.</li>");
      h.append("<li>Complex XWidgets can extend the base and easily register as the widget for a type.</li>");
      h.append("</ul></li>");
      h.append("<li><b>Non-Artifact UIs</b> - Dialogs, BLAMs, Create New Action.</li>");
      h.append("</ul></li>");
      h.append("<li><b>Auto-Persist Used By All</b> (where applicable) - every widget persists immediately; no "
         + "separate save step.</li>");
      h.append("<li><b>Driven By A Common, Full-Featured <code>XOption</code> Enum Set</b> - behavior is configured "
         + "by options rather than by subclassing. A few example sets:<ul>");
      h.append("<li><b>Value Type</b> - <code>VAL_SINGLE_TEXT</code> (e.g. Title), <code>VAL_MULTI_TEXT</code> "
         + "(e.g. Description), <code>VAL_SINGLE_SEL</code> (e.g. Targeted Version), <code>VAL_MULTI_SEL</code> "
         + "(e.g. Subsystem).</li>");
      h.append("<li><b>Editing / Persistence</b> - <code>REQUIRED</code> / <code>NO_REQUIRED</code>, "
         + "<code>SAVE</code> / <code>NO_SAVE</code>, <code>INLINE_EDIT</code> / <code>NO_INLINE_EDIT</code> "
         + "(each option automatically clears its opposite).</li>");
      h.append("</ul></li>");
      h.append("<li><b>Extreme Focus On Performance</b> - how each widget loads, changes, and refreshes its data "
         + "is optimized throughout.</li>");
      h.append("<li><b>Fix One, Fix Them All</b> - the common framework allows a fix and test to apply to all "
         + "widgets at once.</li>");
      h.append("<li><b>Same Filterable Dialogs Used By All</b> - driven by the common <code>XOption</code> values."
         + "<ul>");
      h.append("<li>Dialog automatically chosen by multi-select (checkboxes) vs single-select (list).</li>");
      h.append("<li>Dialog automatically chosen by the OSEE type's multiplicity where applicable.</li>");
      h.append("</ul></li>");
      h.append("<li><b>Registration Through OSGI @Annotations</b> rather than the many other legacy ways.<ul>");
      h.append("<li>OSGI xml files auto-generated and updated on save.</li>");
      h.append("<li>OSGI xmls are verified by automated tests.</li>");
      h.append("<li>XWidgets are verified to exist before the workbench is even started.</li>");
      h.append("</ul></li>");
      h.append("<li><b>Common Error Handling</b><ul>");
      h.append("<li>Error in one widget will not fail the full UI.</li>");
      h.append("<li>Descriptive errors to help in debugging.</li>");
      h.append("<li>Extensive auto-tests to verify bugs don't get released.</li>");
      h.append("</ul></li>");
      h.append("<li><b>Debug Features Integrated Into The UI</b> - e.g. Ctrl+click a widget label for a live debug "
         + "report of its resolved options, attribute type, and <code>WidgetId</code>.</li>");
      h.append("<li><b>Extensive Testing and Debug</b><ul>");
      h.append("<li>Automated code reviews.</li>");
      h.append("<li>Steering files.</li>");
      h.append("<li>Debug features.</li>");
      h.append("</ul></li>");
      h.append("</ul>");
   }

   private void appendXWidgetsSection(StringBuilder h) {
      h.append("<h2 id='xxbase'>One widget to rule them all: <code>XXAbstractBaseXWidget</code></h2>");
      h.append("<p>Most widgets no longer need their own class. <code>XXAbstractBaseXWidget&lt;T&gt;</code> provides "
         + "the behavior needed by nearly every use case - whether it lives in an artifact-backed editor (Artifact "
         + "Editor, Workflow Editor) or not (BLAM, Dialog). Behavior is driven by setting <code>XOption</code>s and "
         + "through the widget builders, not by subclassing.</p>");

      h.append("<p class='filelink'>Source: ").append(refLink("XXAbstractBaseXWidget",
         "XXAbstractBaseXWidget.java")).append("</p>");

      h.append("<p><b>Features provided by the base:</b></p><ul>");
      h.append("<li>All <a href='#architecture'>Major Features</a> above, plus:</li>");
      h.append("<li><b>Smart Label</b> - selectable hyperlink label when editable; plain bold label when "
         + "read-only.</li>");
      h.append("<li><b>Default Selection</b> - selectable items with a default; an enumerated attribute's values "
         + "default the selectable list.</li>");
      h.append("<li><b>Clear Button</b> - optional clear button; right-click clears by default.</li>");
      h.append("<li><b>Tooltip Dialog</b> - double-click shows a tooltip dialog when a tooltip is provided.</li>");
      h.append("<li><b>Sorted Items</b> - selectable items sorted by default (<code>setComparator</code> for "
         + "specialized sorting).</li>");
      h.append("<li><b>Built-In Validation</b> - most cases via <code>isValid()</code> (returns an "
         + "<code>XResultData</code>); usually no separate validator needed.</li>");
      h.append("<li><b>Optional Icon</b> - widget icon via <code>setOseeImage</code>.</li>");
      h.append("<li><b>Enable / Disable</b> - greys the label and blocks interaction.</li>");
      h.append("<li><b>Ctrl+Click Debug</b> - the label opens a widget debug report (resolved options, attribute "
         + "type, <code>WidgetId</code>) - easy to copy into an AI to diagnose a widget.</li>");
      h.append("<li><b>Shared Upside</b> - any new feature added to the base improves every widget at once.</li>");
      h.append("</ul>");

      h.append("<p><b>Explicit lifecycle</b> (one place for each concern). A single shared lifecycle - with distinct "
         + "load, paint, and persist phases - lets create and update be optimized once for every widget:</p><ul>");
      h.append("<li><code>createControls()</code> &rarr; <code>initializeSelected()</code> (LOAD) &rarr; "
         + "<code>refresh()</code> (PAINT).</li>");
      h.append("<li><code>initializeSelected()</code> - the one place a widget loads its value "
         + "(<code>setSelected(value, false)</code>).</li>");
      h.append("<li><code>refresh()</code> - repaints the value display only; no loading, no expensive work.</li>");
      h.append("<li><code>handleSelection()</code> - opens the dialog, sets the value, persists, returns true to "
         + "trigger a refresh.</li>");
      h.append("<li><code>handleSelectedPersist()</code> - the single persistence surface, handling both the "
         + "attribute and relation storage branches.</li>");
      h.append("<li><code>getValidator()</code> / <code>getSentinel()</code> - the only abstract hooks a concrete "
         + "widget typically supplies.</li>");
      h.append("</ul>");
   }

   private void appendReviewToolingSection(StringBuilder h) {
      h.append("<h2 id='tooling'>Review &amp; Code-Quality Tooling</h2>");
      h.append("<p>Two families of tooling support this review, both in "
         + "<code>org.eclipse.osee.ats.ide.integration.tests...coderev</code>.</p>");

      h.append("<h3>CodeReview - read-only standards checks</h3>");
      h.append("<ul>");
      h.append("<li><b>Manually</b> - in Eclipse, select the class &rarr; right-click &rarr; Run As &rarr; Java "
         + "Application.</li>");
      h.append("<li><b>Automatically</b> - run as part of the integration test suite; a violation fails the "
         + "test.</li>");
      h.append("</ul>");
      h.append("<ul>");
      h.append("<li>").append(refLink("CodeReviewAll", "CodeReviewAll"))
         .append(" - runs all of the below and merges the results.</li>");
      h.append("<li>").append(refLink("CodeReviewGeneral", "CodeReviewGeneral"))
         .append(" - cross-cutting checks over all Java (no stray prints, no TBD, hoisted "
            + "<code>Pattern.compile()</code>, auth filter not disabled, no commented-out suite entries).</li>");
      h.append("<li>").append(refLink("CodeReviewXWidgets", "CodeReviewXWidgets"))
         .append(" - validates <code>XX*Widget.java</code> standards.</li>");
      h.append("<li>").append(refLink("CodeReviewBlams", "CodeReviewBlams"))
         .append(" - validates <code>*Blam.java</code> standards.</li>");
      h.append("<li>").append(refLink("CodeReviewOsgiXml", "CodeReviewOsgiXml"))
         .append(" - validates OSGI-INF component XML (bind/unbind, class resolves, correct bundle).</li>");
      h.append("</ul>");
      h.append("<p class='note'>Suppress a single intentional violation with a trailing "
         + "<code>// CodeReview.IgnoreLine</code> comment. This should be used in only corner cases.</p>");

      h.append("<h3>CodeClean - manual checks and optional automatic code fixes</h3>");
      h.append("<ul>");
      h.append("<li><b>Manually</b> - in Eclipse, select the class &rarr; right-click &rarr; Run As &rarr; Java "
         + "Application.</li>");
      h.append("<li><b>Automatically</b> - not currently part of a CI/CD pipeline or integration test.</li>");
      h.append("</ul>");
      h.append("<ul>");
      h.append("<li>").append(refLink("CodeCleanAll", "CodeCleanAll"))
         .append(" - runs them in pipeline order. Every clean op defaults <code>changeFiles = false</code> "
            + "(report-only) until flipped manually.</li>");
      h.append("<li>").append(refLink("CodeCleanOrganizeXWidgets", "CodeCleanOrganizeXWidgets")).append(" / ")
         .append(refLink("CodeCleanOrganizeBlams", "CodeCleanOrganizeBlams"))
         .append(" - reorganize members into the standard section order.</li>");
      h.append("<li>").append(refLink("CodeCleanFormatAndOrganize", "CodeCleanFormatAndOrganize"))
         .append(" - runs the Eclipse JDT formatter on changed files.</li>");
      h.append("<li>").append(refLink("CodeCleanRevertNonFunctional", "CodeCleanRevertNonFunctional"))
         .append(" - reverts files whose only changes are whitespace/formatting (imports flagged for review).</li>");
      h.append("</ul>");
      h.append("<p class='note'>CodeClean is report-only by default; you must toggle the <code>changeFiles</code> "
         + "flag to <code>true</code> for it to actually modify files. Recommended: commit all changes first, so the "
         + "auto-changes can be reviewed, accepted, or reverted easily.</p>");

      h.append("<h3>Migration Report Generators (One-Time, Produced This Packet)</h3>");
      h.append("<ul>");
      h.append("<li>").append(refLink("GenerateFullReviewPacket", "GenerateFullReviewPacket"))
         .append(" - runs all generators below in order, then verifies the output.</li>");
      h.append("<li>").append(refLink("ReportXWidgetChanges", "ReportXWidgetChanges"))
         .append(" - old vs new widget comparison.</li>");
      h.append("<li>").append(refLink("ReportBlamChanges", "ReportBlamChanges"))
         .append(" - old vs new BLAM comparison (rename + code diff).</li>");
      h.append("<li>").append(refLink("ReportWorkDefChange", "ReportWorkDefChange"))
         .append(" - old vs new Work Definition comparison.</li>");
      h.append("<li>").append(refLink("ReportXWidgetOverview", "ReportXWidgetOverview"))
         .append(" - this page.</li>");
      h.append("<li>").append(refLink("ReportXWidgetMapping", "ReportXWidgetMapping"))
         .append(" - the curated old &rarr; new widget/class name map used by the generators.</li>");
      h.append("</ul>");
      h.append("<p class='note'>Reports and copied source files are written under <code>~/CodeXWidgetReview/</code>; "
         + "that folder is temporary and may be deleted after this change is reviewed and merged. The generator source "
         + "files themselves (the <code>coderev/packet</code> package) can also be deleted from the codebase once the "
         + "migration is reviewed and merged.</p>");
   }

   /**
    * One review area: a heading, a one-line description, the three report links (Rename / Diff / Testing), and an
    * optional trailing note ({@code null} to omit).
    */
   private void appendAreaSection(StringBuilder h, String anchor, String title, String desc, String renameFile,
      String renameLabel, String renameDesc, String diffFile, String testingFile, String note) {
      h.append("<h2 id='").append(anchor).append("'>").append(title).append(" review</h2>");
      h.append("<p>").append(desc).append("</p>");
      if (note != null) {
         h.append("<p class='note'>").append(note).append("</p>");
      }
      h.append("<ul class='reports'>");
      if (renameFile != null) {
         h.append("<li><a href='").append(renameFile).append("'>").append(renameLabel).append("</a> - ").append(
            renameDesc).append("</li>");
      } else {
         h.append("<li><b>Renames</b> - none; these kept their names across the migration (no rename page).</li>");
      }
      h.append("<li><a href='").append(diffFile).append("'>Code Diff</a> - old vs new source, side by side.</li>");
      h.append("<li><a href='").append(testingFile).append("'>Testing</a> - tests performed and the test/debug "
         + "features added to the workbench.</li>");
      h.append("</ul>");
   }

   /**
    * Supporting Information: concepts referenced throughout the Overview, each with a short description and links to
    * the copied source. Inline mentions elsewhere (e.g. <code>WidgetId</code>) link here rather than straight to the
    * source file.
    */
   private void appendSupportingInformation(StringBuilder h) {
      h.append("<h2 id='support'>Supporting Information</h2>");
      h.append("<p>Background on the core types referenced throughout this review. Each links to a copied, "
         + "highlighted source file so you can read the real code without leaving the packet.</p>");

      for (Map.Entry<String, String[]> e : SUPPORT_TOPICS.entrySet()) {
         String className = e.getKey();
         String description = e.getValue()[1];
         h.append("<h3 id='").append(supportAnchor(className)).append("'>").append(className).append("</h3>");
         h.append("<p>").append(description).append("</p>");
         h.append("<p class='filelink'>Source: ").append(refLink(className, className + ".java"));
         // WidgetId also carries the ATS-level token as a related source.
         if ("WidgetId".equals(className)) {
            h.append(" &middot; ").append(refLink("WidgetIdAts", "WidgetIdAts.java"));
         }
         h.append("</p>");
      }
   }

   /** @return the in-page anchor id for a Supporting Information topic (e.g. {@code support-widgetid}). */
   private static String supportAnchor(String className) {
      return "support-" + className.toLowerCase();
   }

   private void appendStyle(StringBuilder h) {
      // Shared packet palette (matches PacketStyle: framework blue, soft/line neutrals, agile green accent).
      h.append("<style>");
      h.append(":root{--fw:#1f6feb;--agile:#1a7f37;--del:#cf222e;--ink:#1f2328;--muted:#656d76;--line:#d0d7de;"
         + "--soft:#f6f8fa;}");
      h.append("body { font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; margin: 28px 24px 64px; "
         + "max-width: 1000px; line-height: 1.5; color: var(--ink); }");
      h.append("h1 { color: var(--ink); font-size: 24px; }");
      h.append("h2 { color: var(--ink); font-size: 18px; margin-top: 32px; padding: 6px 0 6px 12px; "
         + "border-left: 5px solid var(--fw); }");
      h.append("h3 { color: var(--ink); margin-top: 20px; }");
      h.append(".lead { font-size: 15px; }");
      h.append(".cui { border: 2px solid var(--del); background: #fff5f5; color: var(--ink); border-radius: 6px; "
         + "padding: 14px 18px; margin: 0 0 20px; font-size: 14px; }");
      h.append(".cui b { color: var(--del); }");
      h.append(".cui .repos { margin: 10px 0 0; font-size: 13px; color: var(--ink); }");
      h.append(".cui ul.repos { margin: 6px 0 0; columns: 3; -webkit-columns: 3; }");
      h.append(".cui ul.repos li { font-size: 12.5px; }");
      h.append(".box { background: var(--soft); border-left: 3px solid var(--agile); padding: 12px 18px; "
         + "border-radius: 4px; }");
      h.append(".toc { background: var(--soft); border: 1px solid var(--line); border-radius: 4px; "
         + "padding: 10px 18px; display: inline-block; }");
      h.append(".note { font-size: 13px; color: var(--muted); background: var(--soft); "
         + "border-left: 3px solid var(--fw); border-radius: 4px; padding: 8px 12px; "
         + "margin-left: 24px; margin-right: 24px; }");
      h.append(".filelink { font-size: 12px; color: var(--muted); }");
      h.append("code { background: var(--soft); padding: 1px 5px; border-radius: 4px; "
         + "font-family: Consolas, 'Courier New', monospace; font-size: 12.5px; }");
      h.append("ul.reports { font-size: 15px; }");
      h.append("ul.reports a { font-weight: bold; }");
      h.append("a { color: var(--fw); }");
      h.append("</style>");
   }

   // =========================================================================
   // Reference-source copying + linking
   // =========================================================================

   /**
    * Copy each {@link #REFERENCE_SOURCES} file into {@code <dir>/reference_src/} as a highlighted {@code .java.html}
    * and record its relative link in {@link #refLinks}. Missing files are skipped (link simply won't be generated).
    */
   private void copyReferenceSources(File dir) {
      File refDir = new File(dir, REF_SUBDIR);
      refDir.mkdirs();
      // Direct reference sources (linked straight to source, e.g. XOption).
      for (Map.Entry<String, String> e : REFERENCE_SOURCES.entrySet()) {
         copyOneReferenceSource(refDir, e.getKey(), e.getValue());
      }
      // Supporting Information topic sources (the entry + inline mentions link here).
      for (Map.Entry<String, String[]> e : SUPPORT_TOPICS.entrySet()) {
         copyOneReferenceSource(refDir, e.getKey(), e.getValue()[0]);
      }
   }

   /** Copy one .java into refDir as a highlighted copy and record its relative link in {@link #refLinks}. */
   private void copyOneReferenceSource(File refDir, String className, String repoRelPath) {
      if (refLinks.containsKey(className)) {
         return; // already copied (a class may appear in both maps)
      }
      Path source = Paths.get(SRC_ROOT, repoRelPath);
      if (!Files.exists(source)) {
         System.err.println("WARNING: reference source not found, skipping link: " + source);
         return;
      }
      String outName = writeHighlightedCopy(source, refDir, className);
      if (outName != null) {
         refLinks.put(className, REF_SUBDIR + "/" + outName);
      }
   }

   /**
    * @return an anchor linking the given class's copied source (opens in a new tab), or just the plain
    * {@code visibleText} if the copy was not available.
    */
   private String refLink(String className, String visibleText) {
      String link = refLinks.get(className);
      if (link == null) {
         return "<code>" + escapeHtml(visibleText) + "</code>";
      }
      return "<a href='" + link + "' target='_blank' rel='noopener'><code>" + escapeHtml(visibleText)
         + "</code></a>";
   }

   /** Write a portable, inline-highlighted HTML copy of a .java file; returns the file name or null on failure. */
   private String writeHighlightedCopy(Path source, File copyDir, String className) {
      String outName = className + ".java.html";
      try {
         String code = Files.readString(source);
         StringBuilder html = new StringBuilder();
         html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>").append(escapeHtml(className)).append(
            ".java</title>");
         html.append(PacketStyle.highlighterCss());
         html.append("<style>body{font-family:-apple-system,'Segoe UI',sans-serif;margin:16px;color:#1f2328;} "
            + "h3{color:#1f6feb;} pre{font-size:12px;} "
            + "pre code{font-family:Consolas,'Courier New',monospace;white-space:pre;}</style>");
         html.append(PacketStyle.highlighterScript());
         html.append("</head><body>");
         html.append("<h3>").append(escapeHtml(className)).append(".java</h3>");
         html.append("<pre><code class='language-java'>").append(escapeHtml(code)).append("</code></pre>");
         html.append("</body></html>");
         Files.writeString(new File(copyDir, outName).toPath(), html.toString());
         return outName;
      } catch (IOException ex) {
         System.err.println("WARNING: could not copy reference source " + source + ": " + ex.getMessage());
         return null;
      }
   }

   private static String escapeHtml(String s) {
      return PacketStyle.escapeHtml(s);
   }

}
