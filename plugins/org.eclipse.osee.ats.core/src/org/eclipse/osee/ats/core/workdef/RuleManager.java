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
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class RuleManager {

   static Map<String, RuleDefinition> ruleMap = new HashMap<String, RuleDefinition>(30);

   public static RuleDefinition getOrCreateRule(String ruleId) {
      if (!ruleMap.containsKey(ruleId)) {
         try {
            RuleDefinitionOption ruleOption = RuleDefinitionOption.valueOf(ruleId);
            ruleMap.put(ruleId, new RuleDefinition(ruleOption));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, null, "Unrecognized rule definition [%s]", ruleId);
         }
      }
      return ruleMap.get(ruleId);
   }
}
