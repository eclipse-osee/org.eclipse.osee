/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.template.engine;

import java.io.IOException;

/**
 * @author Ryan D. Brooks
 */
public class ListItemRule<T> extends AppendableRule<T> {
   private final AppendableRule<T> innerHtml;

   public ListItemRule(String ruleName, AppendableRule<T> innerHtml) {
      super(ruleName);
      this.innerHtml = innerHtml;
   }

   public ListItemRule(AppendableRule<T> innerHtml) {
      this.innerHtml = innerHtml;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      appendable.append("<li>");
      innerHtml.applyTo(appendable);
      appendable.append("</li>\n");
   }

   @Override
   public void applyTo(Appendable appendable, T data) throws IOException {
      appendable.append("<li>");
      innerHtml.applyTo(appendable, data);
      appendable.append("</li>");
   }

   public static void appendTo(Appendable appendable, CharSequence text) throws IOException {
      appendable.append("<li>");
      appendable.append(text);
      appendable.append("</li>\n");
   }
}