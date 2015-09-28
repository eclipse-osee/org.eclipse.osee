/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.rule.IAtsRules;

/**
 * @author Donald G. Dunne
 */
public class RuleManager implements IAtsRules {

   public List<String> rules = new ArrayList<>();

   @Override
   public List<String> getRules() {
      return rules;
   }

   @Override
   public void addRule(String rule) {
      if (!rules.contains(rule)) {
         rules.add(rule);
      }
   }

   @Override
   public boolean hasRule(String rule) {
      return rules.contains(rule);
   }

   @Override
   public void removeRule(String rule) {
      rules.remove(rule);
   }

}
