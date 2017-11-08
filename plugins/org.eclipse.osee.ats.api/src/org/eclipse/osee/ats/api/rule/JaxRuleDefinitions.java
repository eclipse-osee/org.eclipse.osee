/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.rule;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class JaxRuleDefinitions {

   private List<IAtsRuleDefinition> rules;

   public List<IAtsRuleDefinition> getRules() {
      if (rules == null) {
         rules = new LinkedList<>();
      }
      return rules;
   }

   public void setRules(List<IAtsRuleDefinition> rules) {
      this.rules = rules;
   }

}
