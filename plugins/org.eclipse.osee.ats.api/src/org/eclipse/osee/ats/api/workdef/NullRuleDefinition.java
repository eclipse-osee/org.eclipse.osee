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
package org.eclipse.osee.ats.api.workdef;

import org.eclipse.osee.ats.api.workdef.model.RuleDefinition;

/**
 * @author Mark Joy
 */
public class NullRuleDefinition extends RuleDefinition {
   private static NullRuleDefinition instance = null;

   protected NullRuleDefinition() {
      // protected constructor to limit instantiations
   }

   public static NullRuleDefinition getInstance() {
      if (instance == null) {
         instance = new NullRuleDefinition();
      }
      return instance;
   }
}
