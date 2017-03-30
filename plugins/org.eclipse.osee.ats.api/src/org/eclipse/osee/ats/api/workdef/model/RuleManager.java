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
package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class RuleManager {

   public List<String> rules = new ArrayList<>();

   public List<String> getRules() {
      return rules;
   }

   public void addRule(String rule) {
      if (!rules.contains(rule)) {
         rules.add(rule);
      }
   }

   public boolean hasRule(String rule) {
      return rules.contains(rule);
   }

   public void removeRule(String rule) {
      rules.remove(rule);
   }

}
