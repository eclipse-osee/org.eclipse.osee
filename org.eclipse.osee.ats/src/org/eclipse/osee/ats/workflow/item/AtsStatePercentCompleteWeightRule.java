/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.item;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightRule extends WorkRuleDefinition {

   public static String ID = "atsStatePercentCompleteWeight";

   public AtsStatePercentCompleteWeightRule() {
      this(ID, ID);
   }

   public AtsStatePercentCompleteWeightRule(String name, String id) {
      super(name, id);
      setDescription("Work Flow Option: <state>=<percent> Work Data attributes specify weighting given to each state in percent complete calculations.  <state> is either state name (not id) and <percent> is number from 0..1");
   }

   public static Map<String, Double> getStateWeightMap(WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      Map<String, Double> stateToWeight = new HashMap<String, Double>();
      for (String stateName : workRuleDefinition.getWorkDataKeyValueMap().keySet()) {
         String value = workRuleDefinition.getWorkDataValue(stateName);
         try {
            double percent = new Double(value).doubleValue();
            if (percent < 0.0 || percent > 1) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     "Invalid percent value \"" + value + "\" (must be 0..1) for rule " + workRuleDefinition.getId(),
                     new IllegalArgumentException());
            } else {
               stateToWeight.put(stateName, percent);
            }
         } catch (Exception ex) {
            OseeLog.log(
                  AtsPlugin.class,
                  Level.SEVERE,
                  "Invalid percent value \"" + value + "\" (must be float 0..1) for rule " + workRuleDefinition.getId(),
                  new IllegalArgumentException());
         }
      }
      return stateToWeight;
   }
}
