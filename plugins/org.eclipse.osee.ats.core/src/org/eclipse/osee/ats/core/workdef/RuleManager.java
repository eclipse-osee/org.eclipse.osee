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
package org.eclipse.osee.ats.core.workdef;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.core.model.IAtsRules;

/**
 * @author Donald G. Dunne
 */
public class RuleManager implements IAtsRules {

   public Map<String, RuleDefinition> ruleMap = new HashMap<String, RuleDefinition>(30);

   public RuleDefinition getOrCreateRule(String ruleId) {
      if (!ruleMap.containsKey(ruleId)) {
         try {
            RuleDefinitionOption ruleOption = RuleDefinitionOption.valueOf(ruleId);
            ruleMap.put(ruleId, new RuleDefinition(ruleOption));
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
      }
      return ruleMap.get(ruleId);
   }

   @Override
   public Collection<RuleDefinition> getRules() {
      return ruleMap.values();
   }

   @Override
   public RuleDefinition addRule(RuleDefinitionOption option) {
      return getOrCreateRule(option.toString());
   }

   @Override
   public RuleDefinition addRule(String ruleId) {
      return getOrCreateRule(ruleId);
   }

   @Override
   public boolean hasRule(RuleDefinitionOption option) {
      return ruleMap.containsKey(option.toString());
   }

   @Override
   public boolean hasRule(String ruleId) {
      return ruleMap.containsKey(ruleId);
   }
}
