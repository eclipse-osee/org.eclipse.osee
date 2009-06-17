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

public class TransitionCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T transitionFromValue;
   private final T transitionToValue;
   private T lastValue = null;

   public TransitionCondition(DiscreteElement<T> element, T transitionFromValue, T transitionToValue) {
      this.element = element;
      this.transitionToValue = transitionToValue;
      this.transitionFromValue = transitionFromValue;
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
      boolean result = transitionFromValue.equals(lastValue) && transitionToValue.equals(currentValue);
      lastValue = currentValue;
      return result;
   }

   public DiscreteElement<T> getElement() {
      return element;
   }

   public T getTransitionFromValue() {
      return transitionFromValue;
   }

   public T getTransitionToValue() {
      return transitionToValue;
   }

}
