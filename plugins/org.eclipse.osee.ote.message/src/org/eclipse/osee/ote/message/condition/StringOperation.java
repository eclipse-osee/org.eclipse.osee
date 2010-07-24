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
 * @author Ken J. Aguilar
 *
 */
public enum StringOperation {
   LESS_THAN("<") {

      @Override
      public boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) < 0;
      }
   },
   LESS_THAN_OR_EQUAL("<=") {

      @Override
      public boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) <= 0;
      }
   },
   EQUAL("==") {

      @Override
      public boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) == 0;
      }
   },
   NOT_EQUAL("!=") {

      @Override
      public  boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) != 0;
      }
   },
   GREATER_THAN_OR_EQUAL(">=") {

      @Override
      public  boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) >= 0;
      }
   },
   GREATER_THAN(">") {

      @Override
      public boolean evaluate(String currentValue, String targetValue) {
         return currentValue.compareTo(targetValue) > 0;
      }
   },

   SUBSTRING("SUB-STRING OF") {

	   @Override
	   public boolean evaluate(String currentValue, String targetValue) {
		   return targetValue.contains(currentValue);
	   }
   },
   
   CONTAINS("CONTAINS") {

	   @Override
	   public boolean evaluate(String currentValue, String targetValue) {
		   return currentValue.contains(targetValue);
	   }
   };
	   
   private final String toString;
   
   StringOperation(String toString) {
      this.toString = toString;
   }

   @Override
   public String toString() {
      return toString;
   }

   public abstract boolean evaluate(String currentValue, String targetValue);
}
