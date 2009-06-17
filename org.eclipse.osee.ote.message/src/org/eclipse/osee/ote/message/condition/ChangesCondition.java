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

import org.eclipse.osee.ote.message.elements.DiscreteElement;

/**
 * @author Ken J. Aguilar
 *
 */

public class ChangesCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private T lastValue = null;

   public ChangesCondition(DiscreteElement<T> element) {
      this.element = element;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.condition.IDiscreteElementCondition#getLastCheckValue()
    */
   @Override
   public T getLastCheckValue() {
      return lastValue;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.condition.ICondition#check(int)
    */
   @Override
   public boolean check() {
      T currentValue = element.getValue();
      if (lastValue == null) {
         lastValue = currentValue;
         return false;
      }
      boolean result = !currentValue.equals(lastValue);
      lastValue = currentValue;
      return result;
   }

   public DiscreteElement<T> getElement() {
      return element;
   }

}
