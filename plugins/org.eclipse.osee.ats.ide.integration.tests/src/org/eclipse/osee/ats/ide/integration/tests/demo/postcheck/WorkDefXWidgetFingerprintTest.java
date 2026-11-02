/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.demo.postcheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.eclipse.osee.ats.ide.navigate.TestingReportWorkDefXWidgetFingerprints;
import org.eclipse.osee.framework.core.data.OseeData;
import org.junit.Assert;
import org.junit.Test;

/** Base test that verifies WorkDef/widget resolution against an approved regression baseline. */
public class WorkDefXWidgetFingerprintTest {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   @Test
   public void resolvedWidgetDataMatchesBaseline() throws IOException {
      Map<String, Object> expected = readBaseline();
      Map<String, Object> rawActual = readReport(TestingReportWorkDefXWidgetFingerprints.getFingerprintData());

      expected = normalize(expected);
      Map<String, Object> actual = normalize(rawActual);
      if (!expected.equals(actual)) {
         Path report = writeDifferenceReport(expected, actual, rawActual);
         openInBrowser(report);
         Assert.fail(
            "Resolved WorkDef XWidgetData changed. Review " + report + " and deliberately update " + getBaseline());
      }
   }

   protected String getBaseline() {
      return "OSEE-INF/workdef-fingerprints/workdef_fingerprints_core_demo.json";
   }

   protected String getBaselineWorkspacePath() {
      return "plugins/org.eclipse.osee.ats.ide.integration.tests/" + getBaseline();
   }

   protected String getReportName() {
      return "WorkDefXWidgetFingerprintDiff.html";
   }

   private Map<String, Object> readBaseline() throws IOException {
      try (InputStream stream = getClass().getClassLoader().getResourceAsStream(getBaseline())) {
         Assert.assertNotNull("Missing WorkDef fingerprint baseline: " + getBaseline(), stream);
         return MAPPER.readValue(stream, new TypeReference<Map<String, Object>>() {
            // do nothing
         });
      }
   }

   private static Map<String, Object> readReport(Map<String, Object> report) throws IOException {
      return MAPPER.readValue(MAPPER.writeValueAsString(report), new TypeReference<Map<String, Object>>() {
         // do nothing
      });
   }

   private static Map<String, Object> normalize(Map<String, Object> report) {
      Map<String, Object> normalized = new LinkedHashMap<>(report);
      normalized.remove("generatedAt");
      normalized.remove("branch");
      Object workDefs = normalized.get("workDefs");
      Assert.assertTrue("Fingerprint report is missing workDefs", workDefs instanceof List<?>);
      List<Map<String, Object>> contexts = new ArrayList<>();
      for (Object workDef : (List<?>) workDefs) {
         Assert.assertTrue("Fingerprint report contains an invalid workDef context", workDef instanceof Map<?, ?>);
         Map<String, Object> context = new LinkedHashMap<>();
         for (Map.Entry<?, ?> entry : ((Map<?, ?>) workDef).entrySet()) {
            context.put(String.valueOf(entry.getKey()), entry.getValue());
         }
         context.remove("workflowCount");
         contexts.add(context);
      }
      Comparator<Map<String, Object>> contextOrder =
         Comparator.comparingLong(WorkDefXWidgetFingerprintTest::workDefId).thenComparing(
            WorkDefXWidgetFingerprintTest::artifactType);
      contexts.sort(contextOrder);
      normalized.put("workDefs", contexts);
      return normalized;
   }

   private static long workDefId(Map<String, Object> context) {
      return ((Number) context.get("id")).longValue();
   }

   private static String artifactType(Map<String, Object> context) {
      return String.valueOf(context.get("artifactType"));
   }

   private Path writeDifferenceReport(Map<String, Object> expected, Map<String, Object> actual,
      Map<String, Object> replacementBaseline) throws IOException {
      Path report = OseeData.getPath().append(getReportName()).toFile().toPath();
      String actualJson = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(replacementBaseline);
      StringBuilder html = new StringBuilder();
      html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
      html.append("<title>WorkDef XWidget Fingerprint Difference</title><style>");
      html.append("body{font-family:Segoe UI,Arial,sans-serif;margin:2rem;max-width:110rem;color:#202124}");
      html.append("h1{color:#b3261e}.summary{background:#fce8e6;padding:1rem;border-radius:.4rem}");
      html.append("code{background:#f1f3f4;padding:.1rem .25rem}pre,textarea{box-sizing:border-box;width:100%;");
      html.append("font:12px Consolas,monospace;border:1px solid #dadce0;padding:1rem;white-space:pre-wrap}");
      html.append("textarea{height:32rem}button{padding:.5rem 1rem;font-size:1rem}</style></head><body>");
      html.append("<h1>WorkDef XWidget fingerprint changed</h1><div class='summary'>");
      html.append("<p>The live resolved widget data differs from the approved fingerprint baseline.</p>");
      html.append("<p><b>If this is expected:</b> review the differences below, then replace ");
      html.append("<code>").append(escapeHtml(getBaselineWorkspacePath())).append(
         "</code> with the copied JSON below. ");
      html.append("Commit that baseline update with the intentional renderer or WorkDef change.</p></div>");
      appendContextDifferences(html, expected, actual);
      html.append("<h2>Replacement baseline</h2><p>Copy this entire value into <code>");
      html.append(escapeHtml(getBaselineWorkspacePath())).append("</code>.</p>");
      html.append("<button onclick=\"const b=document.getElementById('baseline');b.focus();b.select();");
      html.append("navigator.clipboard.writeText(b.value)\">Copy baseline JSON</button>");
      html.append("<textarea id='baseline' readonly>").append(escapeHtml(actualJson)).append("</textarea>");
      html.append("</body></html>");
      Files.writeString(report, html.toString(), StandardCharsets.UTF_8);
      return report;
   }

   private static void appendContextDifferences(StringBuilder html, Map<String, Object> expected,
      Map<String, Object> actual) {
      Map<String, Map<String, Object>> expectedContexts = contextsByKey(expected);
      Map<String, Map<String, Object>> actualContexts = contextsByKey(actual);
      TreeSet<String> keys = new TreeSet<>();
      keys.addAll(expectedContexts.keySet());
      keys.addAll(actualContexts.keySet());
      html.append("<h2>Differences</h2><ul>");
      for (String key : keys) {
         Map<String, Object> oldContext = expectedContexts.get(key);
         Map<String, Object> newContext = actualContexts.get(key);
         if (oldContext == null) {
            html.append("<li><b>Added context:</b> <code>").append(escapeHtml(key)).append("</code></li>");
         } else if (newContext == null) {
            html.append("<li><b>Removed context:</b> <code>").append(escapeHtml(key)).append("</code></li>");
         } else if (!oldContext.equals(newContext)) {
            html.append("<li><b>Changed context:</b> <code>").append(escapeHtml(key)).append("</code><ul>");
            appendWorkDefDifferences(html, oldContext, newContext);
            boolean widgetsReported = appendWidgetDifferences(html, oldContext, newContext);
            if (!widgetsReported) {
               html.append("<li>Section data:<pre>");
               appendValueDifferences(html, oldContext.get("sections"), newContext.get("sections"), "sections");
               html.append("</pre></li>");
            }
            html.append("</ul></li>");
         }
      }
      html.append("</ul>");
   }

   /** Reports WorkDef metadata even when widget-level differences are also present. */
   private static void appendWorkDefDifferences(StringBuilder html, Map<String, Object> expected,
      Map<String, Object> actual) {
      Map<String, Object> expectedWorkDef = new LinkedHashMap<>(expected);
      Map<String, Object> actualWorkDef = new LinkedHashMap<>(actual);
      expectedWorkDef.remove("sections");
      actualWorkDef.remove("sections");
      if (!expectedWorkDef.equals(actualWorkDef)) {
         html.append("<li>Work Definition data:<pre>");
         appendValueDifferences(html, expectedWorkDef, actualWorkDef, "workDef");
         html.append("</pre></li>");
      }
   }

   private static boolean appendWidgetDifferences(StringBuilder html, Map<String, Object> expected,
      Map<String, Object> actual) {
      if (!(expected.get("sections") instanceof List<?>) || !(actual.get("sections") instanceof List<?>)) {
         return false;
      }
      List<?> expectedSections = (List<?>) expected.get("sections");
      List<?> actualSections = (List<?>) actual.get("sections");
      if (expectedSections.size() != actualSections.size()) {
         return false;
      }
      boolean reported = false;
      for (int sectionIndex = 0; sectionIndex < expectedSections.size(); sectionIndex++) {
         Object oldSection = expectedSections.get(sectionIndex);
         Object newSection = actualSections.get(sectionIndex);
         if (!(oldSection instanceof Map<?, ?>) || !(newSection instanceof Map<?, ?>)) {
            return false;
         }
         Map<?, ?> oldSectionData = (Map<?, ?>) oldSection;
         Map<?, ?> newSectionData = (Map<?, ?>) newSection;
         if (!sectionName(oldSectionData).equals(sectionName(newSectionData))) {
            return false;
         }
         Object oldWidgets = oldSectionData.get("widgets");
         Object newWidgets = newSectionData.get("widgets");
         if (!(oldWidgets instanceof List<?>) || !(newWidgets instanceof List<?>)) {
            return false;
         }
         List<?> expectedWidgets = (List<?>) oldWidgets;
         List<?> actualWidgets = (List<?>) newWidgets;
         if (expectedWidgets.size() != actualWidgets.size()) {
            return false;
         }
         for (int widgetIndex = 0; widgetIndex < expectedWidgets.size(); widgetIndex++) {
            Object oldWidget = expectedWidgets.get(widgetIndex);
            Object newWidget = actualWidgets.get(widgetIndex);
            if (!(oldWidget instanceof Map<?, ?>) || !(newWidget instanceof Map<?, ?>)) {
               return false;
            }
            if (!oldWidget.equals(newWidget)) {
               appendWidgetDifference(html, sectionName(oldSectionData), (Map<?, ?>) oldWidget, (Map<?, ?>) newWidget);
               reported = true;
            }
         }
      }
      return reported;
   }

   private static void appendWidgetDifference(StringBuilder html, String section, Map<?, ?> expected,
      Map<?, ?> actual) {
      String widgetId = String.valueOf(expected.get("widgetId"));
      html.append("<li><b>").append(escapeHtml(section)).append(" / Widget Def");
      html.append(" (").append(escapeHtml(widgetId)).append(")</b><ul>");
      TreeSet<String> removedOptions = optionDifference(expected.get("options"), actual.get("options"));
      TreeSet<String> addedOptions = optionDifference(actual.get("options"), expected.get("options"));
      if (!removedOptions.isEmpty()) {
         html.append("<li>Options removed: <code>");
         html.append(escapeHtml(String.join(", ", removedOptions))).append("</code></li>");
      }
      if (!addedOptions.isEmpty()) {
         html.append("<li>Options added: <code>");
         html.append(escapeHtml(String.join(", ", addedOptions))).append("</code></li>");
      }
      Map<Object, Object> expectedOther = new LinkedHashMap<>(expected);
      Map<Object, Object> actualOther = new LinkedHashMap<>(actual);
      expectedOther.remove("options");
      actualOther.remove("options");
      if (!expectedOther.equals(actualOther)) {
         html.append("<li>Other widget data:<pre>");
         appendValueDifferences(html, expectedOther, actualOther, "widget");
         html.append("</pre></li>");
      }
      html.append("</ul></li>");
   }

   private static TreeSet<String> optionDifference(Object expected, Object actual) {
      TreeSet<String> difference = optionSet(expected);
      difference.removeAll(optionSet(actual));
      return difference;
   }

   private static TreeSet<String> optionSet(Object value) {
      TreeSet<String> options = new TreeSet<>();
      if (value instanceof List<?>) {
         for (Object option : (List<?>) value) {
            options.add(String.valueOf(option));
         }
      }
      return options;
   }

   private static String sectionName(Map<?, ?> section) {
      String type = String.valueOf(section.get("type"));
      return "state".equals(type) ? "State " + section.get("name") : "Header";
   }

   private static Map<String, Map<String, Object>> contextsByKey(Map<String, Object> report) {
      Map<String, Map<String, Object>> contexts = new LinkedHashMap<>();
      for (Object value : (List<?>) report.get("workDefs")) {
         Map<String, Object> context = new LinkedHashMap<>();
         for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            context.put(String.valueOf(entry.getKey()), entry.getValue());
         }
         contexts.put(workDefToStringWithId(context) + " / " + artifactType(context), context);
      }
      return contexts;
   }

   private static String workDefToStringWithId(Map<String, Object> context) {
      return "[" + context.get("name") + "]-[" + workDefId(context) + "]";
   }

   private static void appendValueDifferences(StringBuilder html, Object expected, Object actual, String path) {
      if (expected instanceof Map<?, ?> && actual instanceof Map<?, ?>) {
         TreeSet<String> keys = new TreeSet<>();
         for (Object key : ((Map<?, ?>) expected).keySet()) {
            keys.add(String.valueOf(key));
         }
         for (Object key : ((Map<?, ?>) actual).keySet()) {
            keys.add(String.valueOf(key));
         }
         for (String key : keys) {
            Object expectedValue = ((Map<?, ?>) expected).get(key);
            Object actualValue = ((Map<?, ?>) actual).get(key);
            appendValueDifferences(html, expectedValue, actualValue, path + "." + key);
         }
      } else if (expected instanceof List<?> && actual instanceof List<?>) {
         int size = Math.max(((List<?>) expected).size(), ((List<?>) actual).size());
         for (int index = 0; index < size; index++) {
            Object oldValue = index < ((List<?>) expected).size() ? ((List<?>) expected).get(index) : null;
            Object newValue = index < ((List<?>) actual).size() ? ((List<?>) actual).get(index) : null;
            appendValueDifferences(html, oldValue, newValue, path + "[" + index + "]");
         }
      } else if (!java.util.Objects.equals(expected, actual)) {
         html.append(escapeHtml(path)).append("\n  expected: ").append(escapeHtml(String.valueOf(expected)));
         html.append("\n  actual:   ").append(escapeHtml(String.valueOf(actual))).append("\n\n");
      }
   }

   private static void openInBrowser(Path report) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(report.toUri());
         }
      } catch (Exception ex) {
         // The report path remains in the test failure when the environment cannot launch a browser.
      }
   }

   private static String escapeHtml(String text) {
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
   }
}
