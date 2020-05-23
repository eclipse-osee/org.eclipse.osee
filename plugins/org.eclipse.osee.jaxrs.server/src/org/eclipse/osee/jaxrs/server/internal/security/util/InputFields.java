/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.security.util;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class InputFields {

   public static enum InputType {
      text,
      email,
      password,
      datetime,
      date,
      month,
      time,
      week,
      number,
      url,
      tel,
      color,
      checkbox;
   }

   public static enum ContainerType {
      LIST_GROUP("list-group"),
      FORM_GROUP("form-group");

      private String style;

      private ContainerType(String style) {
         this.style = style;
      }

      public String getStyle() {
         return style;
      }
   }

   public static InputFields newListGroup() {
      return new InputFields(ContainerType.LIST_GROUP, false);
   }

   public static InputFields newListGroupContainer() {
      return new InputFields(ContainerType.LIST_GROUP, true);
   }

   public static InputFields newForm() {
      return new InputFields(ContainerType.FORM_GROUP, false);
   }

   private final Map<String, Data> data = new LinkedHashMap<>();
   private final ContainerType containerType;
   private final boolean wrapInContainer;

   private InputFields(ContainerType containerType, boolean wrapInContainer) {
      this.containerType = containerType;
      this.wrapInContainer = wrapInContainer;
   }

   public InputFields add(String key, InputType type, String label, String tooltip, String placeholder) {
      return add(key, type, label, tooltip, placeholder, null, false, false);
   }

   public InputFields add(String key, InputType type, String label, String tooltip, String placeholder, String value) {
      return add(key, type, label, tooltip, placeholder, value, false, false);
   }

   public InputFields add(String key, InputType type, String label, String tooltip, String placeholder, String value, boolean hasError) {
      return add(key, type, label, tooltip, placeholder, value, false, hasError);
   }

   public InputFields add(String key, InputType type, String label, String tooltip, String placeholder, String value, boolean isDefault, boolean hasError) {
      Data input = new Data();
      input.label = label;
      input.placeholder = placeholder;
      input.type = type;
      input.value = value;
      input.tooltip = tooltip;
      input.isDefault = isDefault;
      input.hasError = hasError;
      data.put(key, input);
      return this;
   }

   private void write(StringBuilder writer, String msg, Object... data) {
      writer.append(String.format(msg, data));
   }

   private static String ifValid(String format, String value) {
      String toReturn = Strings.emptyString();
      if (Strings.isValid(value)) {
         toReturn = String.format(format, value);
      }
      return toReturn;
   }

   private static String ifTrue(String msg, boolean value) {
      String toReturn = Strings.emptyString();
      if (value) {
         toReturn = msg;
      }
      return toReturn;
   }

   private void write(StringBuilder writer, String key, Data data) {
      write(writer, "<div class=\"%s%s\">\n", containerType.getStyle(), ifTrue(" has-error", data.hasError));
      switch (data.type) {
         case checkbox:
            String value = Strings.isValid(data.value) ? data.value : "allow";

            writer.append("<label class=\"checkbox\">\n");
            write(writer, "<input type=\"checkbox\" checked=\"checked\" name=\"%s\" value=\"%s\" %s/>\n", key, value,
               ifTrue("disabled=\"disabled\"", data.isDefault));
            writer.append(data.label);
            if (data.isDefault) {
               write(writer, "<input type=\"hidden\" name=\"%s\" value=\"%s\" />\n", key, value);
            }
            write(writer, "<a href=\"#\" rel=\"tooltip\" title=\"%s\">\n", asTemplateValue(data.tooltip));
            writer.append("<span class=\"badge\">?</span>\n");
            writer.append("</a>\n");
            writer.append("</label>\n");
            break;
         default:
            write(writer, "<label class=\"control-label sr-only\" for=\"%s\">%s</label>\n", key,
               asTemplateValue(data.label));
            write(writer, "<input type=\"%s\" class=\"form-control\" name=\"%s\" %s%s%s>\n", data.type, key,
               ifValid(" placeholder=\"%s\"", data.placeholder), ifValid(" title=\"%s\"", data.tooltip),
               ifValid(" value=\"%s\"", data.value));
            break;
      }
      writer.append("</div>\n");
   }

   public String build() {
      StringBuilder builder = new StringBuilder();
      if (wrapInContainer) {
         builder.append("<div class=\"container\">\n");
      }
      for (Entry<String, Data> entry : data.entrySet()) {
         write(builder, entry.getKey(), entry.getValue());
      }
      if (wrapInContainer) {
         builder.append("</div>\n");
      }
      return builder.toString();
   }

   private class Data {
      InputType type;
      String label;
      String placeholder;
      String tooltip;
      String value;
      boolean isDefault;
      boolean hasError;
   }
}