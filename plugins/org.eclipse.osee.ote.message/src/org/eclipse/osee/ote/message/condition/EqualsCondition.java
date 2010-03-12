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

public class EqualsCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T value;
   private final boolean notEquals;
   private T actualValue;

   public EqualsCondition(DiscreteElement<T> element, T value) {
      this(element, false, value);
   }

   /**
    * sets up a condition that only passes when the notEquals flag is set to false and actual value equals the expected
    * value or when the notEquals flag is true and the actual value does not equal the expected.
    * 
    * @param element
    * @param notEquals
    * @param value
    */
   public EqualsCondition(DiscreteElement<T> element, boolean notEquals, T value) {
      this.element = element;
      this.value = element.elementMask(value);
      this.notEquals = notEquals;
   }

   public boolean check() {
      actualValue = element.getValue();
      return actualValue.equals(value) ^ notEquals;
   }

   public T getLastCheckValue() {
      return actualValue;
   }
}
