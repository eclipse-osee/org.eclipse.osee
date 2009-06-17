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
package org.eclipse.osee.ote.message.condition;

import java.util.Collection;

/**
 * Checks that atleast one condition of a series of conditions is true
 * @author Ken J. Aguilar
 *
 */
public class AndCondition extends AbstractCondition {

   private final ICondition[] conditions;

   public AndCondition(ICondition... conditions) {
      this.conditions = conditions;
   }

   public AndCondition(Collection<ICondition> conditions) {
      this.conditions = conditions.toArray(new ICondition[conditions.size()]);
   }

   public boolean check() {
      for (ICondition condition : conditions) {
         if (condition.check()) {
            return true;
         }
      }
      return false;
   }

}
