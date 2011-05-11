/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
