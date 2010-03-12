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
 * Checks that a series of conditions are all true
 * @author Ken J. Aguilar
 *
 */
public class OrCondition extends AbstractCondition {

   private final ICondition[] conditions;

   public OrCondition(ICondition... conditions) {
      this.conditions = conditions;
   }

   public OrCondition(Collection<ICondition> conditions) {
      this.conditions = conditions.toArray(new ICondition[conditions.size()]);
   }

   public boolean check() {
      for (ICondition condition : conditions) {
         if (!condition.check()) {
            return false;
         }
      }
      return true;
   }

}
