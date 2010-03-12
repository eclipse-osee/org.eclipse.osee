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

import org.eclipse.osee.ote.message.elements.NumericElement;

/**
 * @author Ken J. Aguilar
 *
 */

public class DifferenceCondition<T extends Number & Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final NumericElement<T> element;
   private final T differenceThreshold;
   private T lastValue;
   private final NumericEvaluator<T> evaluator;
   
   public DifferenceCondition(NumericElement<T> element, T differenceThreshold) {
      this.element = element;
      this.differenceThreshold = differenceThreshold;
      Class<?> targetClass = differenceThreshold.getClass();
      if (targetClass.equals(Integer.class)) {
         evaluator = (NumericEvaluator<T>) NumericEvaluator.IntegerEvaluator;
      } else if (targetClass.equals(Double.class)) {
         evaluator = (NumericEvaluator<T>) NumericEvaluator.DoubleEvaluator;
      } else if (targetClass.equals(Long.class)) {
         evaluator = (NumericEvaluator<T>) NumericEvaluator.LongEvaluator;
      } else if (targetClass.equals(Float.class)) {
         evaluator = (NumericEvaluator<T>) NumericEvaluator.FloatEvaluator;
      } else {
         throw new IllegalArgumentException("");
      }
   }

   
   public NumericElement<T> getElement() {
      return element;
   }

   public T getDifferenceThreshold() {
      return differenceThreshold;
   }


   @Override
   public T getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      T value = element.getValue();
      if (lastValue == null) {
         lastValue = value;
         return false;
      }

      boolean result = evaluator.subtractAndCheckGreater(value, lastValue, differenceThreshold);
      lastValue = value;
      return result;
   }

}
