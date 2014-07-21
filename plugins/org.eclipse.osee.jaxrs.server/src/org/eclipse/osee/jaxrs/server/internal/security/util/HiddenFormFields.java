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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.template.engine.AppendableRule;

/**
 * @author Roberto E. Escobar
 */
public final class HiddenFormFields extends AppendableRule<Object> {

   private final Map<String, String> data = new HashMap<String, String>();

   public static HiddenFormFields newForm(String tag) {
      return new HiddenFormFields(tag);
   }

   private HiddenFormFields(String ruleName) {
      super(ruleName);
   }

   public HiddenFormFields add(String key, String value) {
      data.put(key, value);
      return this;
   }

   private void write(Appendable writer, String name, String value) throws IOException {
      if (Strings.isValid(value)) {
         writer.append(String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\">\n", name, asTemplateValue(value)));
      }
   }

   @Override
   public void applyTo(Appendable writer) throws IOException {
      writer.append("<div class=\"form-group\">\n");
      for (Entry<String, String> entry : data.entrySet()) {
         write(writer, entry.getKey(), entry.getValue());
      }
      writer.append("</div>\n");
   }

}