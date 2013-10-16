/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan D. Brooks
 */
public final class CompositeRule extends AppendableRule {
   private final List<AppendableRule> rules = new ArrayList<AppendableRule>();

   public CompositeRule(String ruleName) {
      super(ruleName);
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      for (AppendableRule rule : rules) {
         rule.applyTo(appendable);
      }
   }

   public void addRule(AppendableRule rule) {
      rules.add(rule);
   }

   public boolean ruleExists(String ruleName) {
      for (AppendableRule rule : rules) {
         if (rule.getName().equals(ruleName)) {
            return true;
         }
      }
      return false;
   }
}