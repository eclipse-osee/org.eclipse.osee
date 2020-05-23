/*********************************************************************
 * Copyright (c) 2011 Boeing
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
