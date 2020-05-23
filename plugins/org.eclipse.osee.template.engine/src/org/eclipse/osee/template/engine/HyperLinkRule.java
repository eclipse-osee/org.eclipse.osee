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
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 */
public class HyperLinkRule extends AppendableRule<Pair<CharSequence, CharSequence>> {
   private CharSequence uri;
   private CharSequence text;

   public HyperLinkRule() {
      super();
   }

   public HyperLinkRule(String ruleName) {
      super(ruleName);
   }

   public HyperLinkRule(String ruleName, CharSequence uri, CharSequence text) {
      super(ruleName);
      this.uri = uri;
      this.text = text;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      appendTo(appendable, uri, text);
   }

   @Override
   public void applyTo(Appendable appendable, Pair<CharSequence, CharSequence> data) throws IOException {
      appendTo(appendable, data.getFirst(), data.getSecond());
   }

   public static void appendTo(Appendable appendable, CharSequence uri, CharSequence text) throws IOException {
      appendable.append("<a href=\"");
      appendable.append(uri);
      appendable.append("\">");
      appendable.append(text);
      appendable.append("</a>");
   }
}