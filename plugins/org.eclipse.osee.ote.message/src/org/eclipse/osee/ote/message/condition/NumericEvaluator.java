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

/**
 * @author author Ken J. Aguilar
 *
 */
public interface NumericEvaluator<T extends Number & Comparable<T>> {
 
   public static final NumericEvaluator<Integer> IntegerEvaluator = new NumericEvaluator<Integer>() {
      @Override
      public boolean subtractAndCheckGreater(Integer left, Integer right, Integer expected) {
         return (left - right) > expected;
      }
   };
   
   public static final NumericEvaluator<Double> DoubleEvaluator = new NumericEvaluator<Double>() {
      @Override
      public boolean subtractAndCheckGreater(Double left, Double right, Double expected) {
         return (left - right) > expected;
      }
   };
   
   public static final NumericEvaluator<Long> LongEvaluator = new NumericEvaluator<Long>() {
      @Override
      public boolean subtractAndCheckGreater(Long left, Long right, Long expected) {
         return (left - right) > expected;
      }
   };
   
   public static final NumericEvaluator<Float> FloatEvaluator = new NumericEvaluator<Float>() {
      @Override
      public boolean subtractAndCheckGreater(Float left, Float right, Float expected) {
         return (left - right) > expected;
      }
   };
   
   boolean subtractAndCheckGreater(T left, T right, T expected);
}
