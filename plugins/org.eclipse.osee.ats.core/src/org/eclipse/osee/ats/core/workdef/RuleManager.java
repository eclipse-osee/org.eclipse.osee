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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class RuleManager {

   static Map<String, RuleDefinition> ruleMap = new HashMap<String, RuleDefinition>(30);

   public static RuleDefinition getOrCreateRule(String ruleId) {
      // Remove after 0.9.9_SR4 release and database rules converted to new spelling
      if (ruleId != null) {
         ruleId = ruleId.replaceFirst("riviledged", "rivileged");
      }
      if (!ruleMap.containsKey(ruleId)) {
         RuleDefinitionOption ruleOption = RuleDefinitionOption.valueOf(ruleId);
         ruleMap.put(ruleId, new RuleDefinition(ruleOption));
      }
      return ruleMap.get(ruleId);
   }
}
