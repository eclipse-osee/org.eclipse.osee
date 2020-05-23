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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class StringOptionsRule extends AppendableRule<String> {
   private final String listId;
   private final List<String> options = new ArrayList<>();

   protected String getListId() {
      return listId;
   }

   public StringOptionsRule(String ruleName) {
      this(ruleName, null);
   }

   public StringOptionsRule(String ruleName, String listId) {
      super(ruleName);
      this.listId = listId;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      if (listId == null) {
         appendOptions(appendable);
      } else {
         appendable.append("\n<datalist id=\"");
         appendable.append(listId);
         appendable.append("\">\n");
         appendOptions(appendable);
         appendable.append("</datalist>\n");
      }
   }

   private void appendOptions(Appendable appendable) throws IOException {
      for (String option : getOptions()) {
         appendable.append("<option value=\"");
         appendable.append(option);
         appendable.append("\" guid=\"");
         appendable.append(option);
         appendable.append("\">\n");
      }
   }

   public List<String> getOptions() {
      return options;
   }
}