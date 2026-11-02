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
package org.eclipse.osee.ats.rest.internal.wfe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Generates a complete HTML page for a workflow, resembling the WfeWorkflowSection in the Workflow Editor. Uses
 * XWidgetHtmlRenderer for state sections and a fixed header template for metadata.
 * <p>
 * Endpoint: called from AtsActionUiEndpointImpl for /ui/action/{id}/workflow
 *
 * @author Donald G. Dunne
 */
public class XWidgetHtmlPage {

   private final AtsApi atsApi;
   private final IAtsWorkItem workItem;

   public XWidgetHtmlPage(AtsApi atsApi, IAtsWorkItem workItem) {
      this.atsApi = atsApi;
      this.workItem = workItem;
   }

   /**
    * Generates the full HTML page string.
    */
   public String generate() {
      // Pre-load all attribute values for performance
      Map<AttributeTypeToken, List<String>> attrValues = loadAllAttributes();

      // Build renderer
      XWidgetHtmlRenderer renderer = new XWidgetHtmlRenderer(atsApi, workItem, attrValues, false);

      StringBuilder html = new StringBuilder();
      html.append(getHtmlHead());
      html.append("<body>\n");
      html.append("<div class=\"wfe-page\">\n");

      // Toolbar
      html.append(renderToolbar());

      // Header
      html.append(renderHeader());

      // Header widgets from WorkDef
      String headerWidgets = renderer.renderHeaderWidgets();
      if (Strings.isValid(headerWidgets)) {
         html.append("<div class=\"wfe-header-widgets\">\n");
         html.append(headerWidgets);
         html.append("</div>\n");
      }

      // States
      html.append("<div class=\"wfe-states\">\n");
      html.append(renderer.renderStates());
      html.append("</div>\n");

      html.append("</div>\n");
      html.append("</body></html>");
      return html.toString();
   }

   /**
    * Renders a small toolbar with Refresh, Expand All, Print, and Export buttons using SVG icons.
    */
   private String renderToolbar() {
      return "<div class=\"wfe-toolbar\">\n" + //
         "  <button onclick=\"location.reload()\" title=\"Refresh\">" + ICON_REFRESH + " Refresh</button>\n" + //
         "  <button onclick=\"document.querySelectorAll('.wfe-state').forEach(d=>d.open=true)\" title=\"Expand All\">" + ICON_EXPAND + " Expand All</button>\n" + //
         "  <button onclick=\"document.querySelectorAll('.wfe-state').forEach(d=>d.open=false)\" title=\"Collapse All\">" + ICON_COLLAPSE + " Collapse All</button>\n" + //
         "  <button onclick=\"window.print()\" title=\"Print\">" + ICON_PRINT + " Print</button>\n" + //
         "  <button onclick=\"exportHtml()\" title=\"Export as HTML\">" + ICON_EXPORT + " HTML</button>\n" + //
         "  <button onclick=\"exportPdf()\" title=\"Export as PDF (expands all states)\">" + ICON_PDF + " PDF</button>\n" + //
         "  <button onclick=\"copyLink()\" title=\"Copy link to clipboard\">" + ICON_LINK + " Link</button>\n" + //
         "  <button onclick=\"toggleReadability()\" title=\"Toggle compact/comfortable view\">" + ICON_VIEW + " View</button>\n" + //
         "</div>\n" + //
         "<script>\n" + //
         "function exportHtml() {\n" + //
         "  var blob = new Blob([document.documentElement.outerHTML], {type:'text/html'});\n" + //
         "  var a = document.createElement('a');\n" + //
         "  a.href = URL.createObjectURL(blob);\n" + //
         "  a.download = 'workflow-" + esc(workItem.getAtsId()) + ".html';\n" + //
         "  a.click();\n" + //
         "}\n" + //
         "function exportPdf() {\n" + //
         "  document.querySelectorAll('.wfe-state').forEach(function(d){d.open=true;});\n" + //
         "  setTimeout(function(){ window.print(); }, 300);\n" + //
         "}\n" + //
         "function copyLink() {\n" + //
         "  navigator.clipboard.writeText(window.location.href).then(function(){\n" + //
         "    var btn = event.target.closest('button');\n" + //
         "    var orig = btn.innerHTML;\n" + //
         "    btn.innerHTML = '" + ICON_LINK + " Copied!';\n" + //
         "    setTimeout(function(){ btn.innerHTML = orig; }, 1500);\n" + //
         "  });\n" + //
         "}\n" + //
         "var compact = true;\n" + //
         "function toggleReadability() {\n" + //
         "  compact = !compact;\n" + //
         "  document.querySelector('.wfe-states').style.fontSize = compact ? '14px' : '16px';\n" + //
         "  document.querySelectorAll('.wfe-widget').forEach(function(w){\n" + //
         "    w.style.marginBottom = compact ? '6px' : '12px';\n" + //
         "  });\n" + //
         "}\n" + //
         "</script>\n";
   }

   // SVG icons (inline, no external dependencies)
   private static final String ICON_REFRESH =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M23 4v6h-6M1 20v-6h6\"/><path d=\"M3.51 9a9 9 0 0114.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0020.49 15\"/></svg>";
   private static final String ICON_EXPAND =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7\"/></svg>";
   private static final String ICON_COLLAPSE =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M4 14h6v6M14 10h6V4M10 14l-7 7M14 10l7-7\"/></svg>";
   private static final String ICON_PRINT =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M6 9V2h12v7M6 18H4a2 2 0 01-2-2v-5a2 2 0 012-2h16a2 2 0 012 2v5a2 2 0 01-2 2h-2\"/><rect x=\"6\" y=\"14\" width=\"12\" height=\"8\"/></svg>";
   private static final String ICON_EXPORT =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3\"/></svg>";
   private static final String ICON_PDF =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z\"/><polyline points=\"14 2 14 8 20 8\"/><path d=\"M9 15h2M9 12h6\"/></svg>";
   private static final String ICON_LINK =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M10 13a5 5 0 007.54.54l3-3a5 5 0 00-7.07-7.07l-1.72 1.71\"/><path d=\"M14 11a5 5 0 00-7.54-.54l-3 3a5 5 0 007.07 7.07l1.71-1.71\"/></svg>";
   private static final String ICON_VIEW =
      "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/></svg>";

   /**
    * Renders the fixed header metadata (same for all WorkDefs).
    */
   private String renderHeader() {
      StringBuilder h = new StringBuilder();
      String title = getAttr(AtsAttributeTypes.Title);
      String currentState = atsApi.getWorkItemService().getCurrentStateName(workItem);
      String assignees = workItem.getAssigneesStr();
      String atsId = workItem.getAtsId();
      String createdDate = workItem.getCreatedDate() != null ? workItem.getCreatedDate().toString() : "";
      String originator = workItem.getCreatedBy() != null ? workItem.getCreatedBy().getName() : "";
      String priority = getAttr(AtsAttributeTypes.Priority);
      String changeType = getAttr(AtsAttributeTypes.ChangeType);
      String needBy = getAttr(AtsAttributeTypes.NeedBy);
      String workDefName = workItem.getWorkDefinition().getName();
      String version = getVersion();
      String team = getTeam();

      h.append("<div class=\"wfe-header\">\n");
      h.append("<table class=\"wfe-header-table\">\n");

      // Row 1: Title
      h.append(String.format("<tr><td colspan=\"3\"><b>Title:</b> %s</td></tr>\n", esc(title)));

      // Row 2: State, Created, Originator
      h.append(String.format(
         "<tr><td><b>Current State:</b> %s</td><td><b>Created:</b> %s</td><td><b>Originator:</b> %s</td></tr>\n",
         esc(currentState), esc(createdDate), esc(originator)));

      // Row 3: Team, ATS Id, Action Id
      h.append(
         String.format("<tr><td><b>Team:</b> %s</td><td><b>ATS Id:</b> %s</td><td><b>Action Id:</b> %s</td></tr>\n",
            esc(team), esc(atsId), workItem.getId()));

      // Row 4: Version, Assignees
      h.append(
         String.format("<tr><td><b>Target Version:</b> %s</td><td colspan=\"2\"><b>Assignee(s):</b> %s</td></tr>\n",
            esc(version), esc(assignees)));

      // Row 5: ChangeType, Priority, NeedBy
      h.append(String.format(
         "<tr><td><b>Change Type:</b> %s</td><td><b>Priority:</b> %s</td><td><b>Need By:</b> %s</td></tr>\n",
         esc(changeType), esc(priority), esc(needBy)));

      // Row 6: Work Definition
      h.append(String.format("<tr><td colspan=\"3\"><b>Work Definition:</b> %s</td></tr>\n", esc(workDefName)));

      h.append("</table>\n");
      h.append("</div>\n");
      return h.toString();
   }

   private String getTeam() {
      try {
         org.eclipse.osee.framework.core.data.ArtifactId teamId =
            atsApi.getAttributeResolver().getSoleArtifactIdReference(workItem,
               AtsAttributeTypes.TeamDefinitionReference, org.eclipse.osee.framework.core.data.ArtifactId.SENTINEL);
         if (teamId.isValid()) {
            return atsApi.getQueryService().getArtifact(teamId).getName();
         }
      } catch (Exception ex) {
         // fallback
      }
      return "";
   }

   private String getVersion() {
      try {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            String ver = atsApi.getWorkItemService().getTargetedVersionStr(teamWf);
            if (Strings.isValid(ver)) {
               return ver;
            }
         }
      } catch (Exception ex) {
         // fallback
      }
      return "";
   }

   private String getAttr(AttributeTypeToken attrType) {
      return atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, attrType, "");
   }

   /**
    * Pre-loads all attribute values for the work item into a map for fast lookup by widgets.
    */
   private Map<AttributeTypeToken, List<String>> loadAllAttributes() {
      Map<AttributeTypeToken, List<String>> map = new HashMap<>();
      try {
         for (org.eclipse.osee.framework.core.data.IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(
            workItem)) {
            AttributeTypeToken attrType = attr.getAttributeType();
            String value = String.valueOf(attr.getValue());
            map.computeIfAbsent(attrType, k -> new ArrayList<>()).add(value);
         }
      } catch (Exception ex) {
         // Gracefully handle — widgets will show empty values
      }
      return map;
   }

   private String getHtmlHead() {
      return "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n" + //
         "<meta charset=\"UTF-8\">\n" + //
         "<title>Workflow - " + esc(workItem.getAtsId()) + "</title>\n" + //
         "<style>\n" + getStyles() + "</style>\n" + //
         "</head>\n";
   }

   private String getStyles() {
      return "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; font-size: 14px; margin: 0; padding: 16px; background: #f5f5f5; }\n" + //
         ".wfe-page { max-width: 1200px; margin: 0 auto; background: #fff; border: 1px solid #ddd; border-radius: 4px; }\n" + //
         ".wfe-toolbar { display: flex; gap: 4px; padding: 6px 16px; background: #f8f9fa; border-bottom: 1px solid #e0e0e0; border-radius: 4px 4px 0 0; }\n" + //
         ".wfe-toolbar button { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border: 1px solid #bdbdbd; border-radius: 3px; background: #fff; font-size: 12px; cursor: pointer; color: #333; }\n" + //
         ".wfe-toolbar button:hover { background: #e3f2fd; border-color: #1565c0; color: #1565c0; }\n" + //
         ".wfe-export-group { position: relative; }\n" + //
         ".wfe-export-menu { position: absolute; top: 100%; left: 0; margin-top: 2px; background: #fff; border: 1px solid #bdbdbd; border-radius: 3px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); z-index: 100; min-width: 140px; }\n" + //
         ".wfe-export-menu a { display: block; padding: 6px 12px; text-decoration: none; color: #333; font-size: 12px; }\n" + //
         ".wfe-export-menu a:hover { background: #e3f2fd; color: #1565c0; }\n" + //
         "@media print { .wfe-toolbar { display: none; } }\n" + //
         ".wfe-header { padding: 12px 16px; background: #e3f2fd; border-bottom: 2px solid #1565c0; }\n" + //
         ".wfe-header-table { width: 100%; border-collapse: collapse; font-size: 14px; }\n" + //
         ".wfe-header-table td { padding: 4px 10px; }\n" + //
         ".wfe-header-table b { color: #0d47a1; }\n" + //
         ".wfe-header-widgets { padding: 8px 16px; border-bottom: 1px solid #e0e0e0; }\n" + //
         ".wfe-states { padding: 8px 16px; }\n" + //
         ".wfe-state { margin-bottom: 8px; border: 1px solid #90caf9; border-radius: 4px; }\n" + //
         ".wfe-state-current { border-color: #1565c0; }\n" + //
         ".wfe-state-title { padding: 8px 12px; font-weight: bold; font-size: 14px; cursor: pointer; background: #bbdefb; color: #0d47a1; border-radius: 4px; }\n" + //
         ".wfe-state-current > .wfe-state-title { background: #1565c0; color: #fff; }\n" + //
         ".wfe-state-body { padding: 12px 16px; }\n" + //
         ".wfe-state-desc { font-style: italic; color: #666; margin-bottom: 8px; }\n" + //
         ".wfe-widget { margin-bottom: 6px; }\n" + //
         ".wfe-widget label { display: inline; }\n" + //
         ".wfe-widget .wfe-default { display: inline; }\n" + //
         ".wfe-widget .wfe-label-widget { display: block; }\n" + //
         ".wfe-textarea { display: block; }\n" + //
         ".wfe-textarea label { display: block; }\n" + //
         ".wfe-textarea textarea { width: 100%; box-sizing: border-box; font-family: inherit; font-size: 13px; }\n" + //
         ".wfe-composite { margin: 4px 0; }\n" + //
         ".wfe-composite .wfe-widget { white-space: nowrap; }\n" + //
         ".wfe-checkbox { white-space: nowrap; }\n" + //
         "details summary::-webkit-details-marker { color: #1565c0; }\n" + //
         "details summary { list-style-type: disclosure-closed; }\n" + //
         "details[open] summary { list-style-type: disclosure-open; }\n";
   }

   private static String esc(String text) {
      if (text == null) {
         return "";
      }
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }

}
