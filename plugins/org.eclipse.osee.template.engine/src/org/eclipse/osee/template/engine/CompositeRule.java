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
 * @author Ryan D. Brooks
 */
public final class CompositeRule<T> extends AppendableRule<T> {
   private final List<AppendableRule<T>> rules = new ArrayList<>();

   public CompositeRule(String ruleName) {
      super(ruleName);
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      for (AppendableRule<T> rule : rules) {
         rule.applyTo(appendable);
      }
   }

   @Override
   public void applyTo(Appendable appendable, T data) throws IOException {
      for (AppendableRule<T> rule : rules) {
         rule.applyTo(appendable, data);
      }
   }

   public void addRule(AppendableRule<T> rule) {
      rules.add(rule);
   }

   public boolean ruleExists(String ruleName) {
      for (AppendableRule<T> rule : rules) {
         if (rule.getName().equals(ruleName)) {
            return true;
         }
      }
      return false;
   }
}