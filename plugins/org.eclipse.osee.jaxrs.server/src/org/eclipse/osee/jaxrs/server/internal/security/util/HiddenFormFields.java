/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.util;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class HiddenFormFields {

   private final Map<String, String> data = new HashMap<String, String>();

   public static HiddenFormFields newForm() {
      return new HiddenFormFields();
   }

   public HiddenFormFields add(String key, String value) {
      data.put(key, value);
      return this;
   }

   private void write(StringBuilder writer, String name, String value) {
      if (Strings.isValid(value)) {
         writer.append(String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\">\n", name, asTemplateValue(value)));
      }
   }

   public String build() {
      StringBuilder builder = new StringBuilder();
      builder.append("<div class=\"form-group\">\n");
      for (Entry<String, String> entry : data.entrySet()) {
         write(builder, entry.getKey(), entry.getValue());
      }
      builder.append("</div>\n");
      return builder.toString();
   }

}