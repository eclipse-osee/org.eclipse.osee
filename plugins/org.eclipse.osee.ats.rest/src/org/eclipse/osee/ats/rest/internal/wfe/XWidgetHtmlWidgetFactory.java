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

import java.util.Collection;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Maps a resolved WidgetId + label + current value(s) to an HTML form element. Centralized so widgets don't render
 * themselves — all HTML generation is in one place for consistency and future edit-mode support.
 *
 * @author Donald G. Dunne
 */
public class XWidgetHtmlWidgetFactory {

   /**
    * Renders a single widget as HTML.
    *
    * @param widgetId the resolved WidgetId for this widget
    * @param label the display label
    * @param values current attribute values (may be empty)
    * @param widData merged widget data with options
    * @param editable whether the widget should be rendered as editable (future)
    * @return HTML string for this widget
    */
   public static String render(WidgetId widgetId, String label, Collection<String> values, XWidgetData widData,
      boolean editable) {
      String widgetName = widgetId != null ? widgetId.getName() : "";
      String value = values.isEmpty() ? "" : values.iterator().next();
      String allValues = String.join("; ", values);

      StringBuilder html = new StringBuilder();
      html.append("<div class=\"wfe-widget\" data-widget-id=\"").append(esc(widgetName)).append("\">");

      // Use XOptions from widData to determine rendering (mirrors client-side widget behavior)
      if (widgetName.contains("LabelWidget") || widgetName.contains("LabelValue")) {
         renderLabel(html, label, allValues);
      } else if (widgetName.contains("CheckBox") || widgetName.contains(
         "Boolean") || (widData != null && widData.is(XOption.VAL_NO_SEL) && widgetName.contains("Boolean"))) {
         renderCheckbox(html, label, value, editable);
      } else if (widgetName.contains("Date")) {
         renderDate(html, label, value, editable);
      } else if (widgetName.contains("Integer") || widgetName.contains("Double") || widgetName.contains("Percent")) {
         renderNumber(html, label, value, editable);
      } else if (isMultiLineText(widData, widgetName)) {
         renderTextarea(html, label, allValues, editable);
      } else if (isSingleLineText(widData, widgetName)) {
         renderTextInput(html, label, value, editable);
      } else if (isSelectFromList(widData, widgetName)) {
         renderSelect(html, label, allValues, editable);
      } else if (widgetName.contains("Button")) {
         renderButton(html, label);
      } else if (widgetName.contains("WorkingBranch") || widgetName.contains("CommitManager")) {
         renderNotImplemented(html, label);
      } else {
         // Default fallback: label + bold value
         renderDefault(html, label, allValues);
      }

      html.append("</div>");
      return html.toString();
   }

   private static boolean isMultiLineText(XWidgetData widData, String widgetName) {
      if (widgetName.contains("LongSText") || widgetName.contains("Stacked")) {
         return true;
      }
      return widData != null && (widData.is(XOption.VAL_MULTI_TEXT) || widData.is(XOption.FILL_VERT));
   }

   private static boolean isSingleLineText(XWidgetData widData, String widgetName) {
      if (widgetName.contains("Text") || widgetName.contains("String")) {
         return true;
      }
      return widData != null && widData.is(XOption.VAL_SINGLE_TEXT);
   }

   private static boolean isSelectFromList(XWidgetData widData, String widgetName) {
      if (widgetName.contains("List") || widgetName.contains("Combo") || widgetName.contains(
         "Strings") || widgetName.contains("Enum")) {
         return true;
      }
      return widData != null && (widData.is(XOption.VAL_MULTI_SEL) || widData.is(XOption.VAL_SINGLE_SEL));
   }

   private static void renderLabel(StringBuilder html, String label, String value) {
      html.append("<span class=\"wfe-label-widget\">");
      if (Strings.isValid(label)) {
         html.append("<b>").append(esc(label)).append("</b>");
      }
      if (Strings.isValid(value)) {
         html.append(" ").append(esc(value));
      }
      html.append("</span>");
   }

   private static void renderCheckbox(StringBuilder html, String label, String value, boolean editable) {
      boolean checked = "true".equalsIgnoreCase(value);
      html.append("<label class=\"wfe-checkbox\">");
      html.append("<input type=\"checkbox\"");
      if (checked) {
         html.append(" checked");
      }
      if (!editable) {
         html.append(" disabled");
      }
      html.append("> ");
      html.append(esc(label));
      html.append("</label>");
   }

   private static void renderDate(StringBuilder html, String label, String value, boolean editable) {
      html.append("<label class=\"wfe-date\"><b>").append(esc(label)).append(":</b> ");
      if (editable) {
         html.append("<input type=\"date\" value=\"").append(esc(value)).append("\">");
      } else {
         html.append("<span>").append(esc(value)).append("</span>");
      }
      html.append("</label>");
   }

   private static void renderNumber(StringBuilder html, String label, String value, boolean editable) {
      html.append("<label class=\"wfe-number\"><b>").append(esc(label)).append(":</b> ");
      if (editable) {
         html.append("<input type=\"number\" value=\"").append(esc(value)).append("\">");
      } else {
         html.append("<span>").append(esc(value)).append("</span>");
      }
      html.append("</label>");
   }

   private static void renderTextarea(StringBuilder html, String label, String value, boolean editable) {
      int rows = Strings.isValid(value) ? Math.max(4, value.split("\n").length + 1) : 2;
      html.append("<div class=\"wfe-textarea\">");
      html.append("<label><b>").append(esc(label)).append(":</b></label>");
      html.append("<textarea rows=\"").append(rows).append("\"");
      if (!editable) {
         html.append(" readonly");
      }
      html.append(">").append(esc(value)).append("</textarea>");
      html.append("</div>");
   }

   private static void renderTextInput(StringBuilder html, String label, String value, boolean editable) {
      html.append("<label class=\"wfe-text\"><b>").append(esc(label)).append(":</b> ");
      if (editable) {
         html.append("<input type=\"text\" value=\"").append(esc(value)).append("\">");
      } else {
         html.append("<span>").append(esc(value)).append("</span>");
      }
      html.append("</label>");
   }

   private static void renderSelect(StringBuilder html, String label, String value, boolean editable) {
      html.append("<label class=\"wfe-select\"><b>").append(esc(label)).append(":</b> ");
      html.append("<span>").append(esc(value)).append("</span>");
      // TBD: render actual <select> with options when editable
      html.append("</label>");
   }

   private static void renderButton(StringBuilder html, String label) {
      html.append("<button class=\"wfe-button\" disabled>").append(esc(label)).append("</button>");
   }

   private static void renderNotImplemented(StringBuilder html, String label) {
      html.append("<span class=\"wfe-default\">");
      html.append("<b>").append(esc(label)).append(":</b> ");
      html.append("<em style=\"color:#999\">&lt;not implemented yet&gt;</em>");
      html.append("</span>");
   }

   private static void renderDefault(StringBuilder html, String label, String value) {
      html.append("<span class=\"wfe-default\">");
      html.append("<b>").append(esc(label)).append(":</b> ");
      html.append(Strings.isValid(value) ? AHTML.textToHtml(value) : "");
      html.append("</span>");
   }

   private static String esc(String text) {
      if (text == null) {
         return "";
      }
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
   }

}
