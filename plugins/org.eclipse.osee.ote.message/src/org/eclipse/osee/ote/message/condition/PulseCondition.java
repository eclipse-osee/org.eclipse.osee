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

public class PulseCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final int maxPulses;
   private final DiscreteElement<T> element;
   private final T pulsedValue;
   private final T nonPulsedValue;
   private int pulses = 0;
   private T lastValue;

   public PulseCondition(DiscreteElement<T> element, T pulsedValue, T nonPulsedValue, int numPulses) {
      this.element = element;
      this.pulsedValue = element.elementMask(pulsedValue);
      this.nonPulsedValue = element.elementMask(nonPulsedValue);
      this.maxPulses = numPulses;
   }

   public PulseCondition(DiscreteElement<T> element, T pulsedValue, T nonPulsedValue) {
      this(element, pulsedValue, nonPulsedValue, 2);
   }

   public T getLastCheckValue() {
      return lastValue;
   }

   public boolean check() {
      lastValue = element.getValue();
      if (lastValue.equals(pulsedValue)) {
         pulses++;
      } else if (pulses >= maxPulses && lastValue.equals(nonPulsedValue)) {
         return true;
      }
      return false;
   }

   public int getPulses() {
      return pulses;
   }

}
