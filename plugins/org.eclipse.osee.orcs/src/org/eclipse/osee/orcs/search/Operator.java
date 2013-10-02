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
package org.eclipse.osee.orcs.search;

/**
 * @author Ryan D. Brooks
 */
public enum Operator {
   EQUAL("="), // Exact Match as in Strings.equals
   NOT_EQUAL("<>"), // inverse of exact match - !Strings.equals
   LESS_THAN("<"),
   LESS_THAN_EQ("<="),
   GREATER_THAN(">"),
   GREATER_THAN_EQ(">=");

   private String expression;

   private Operator(String expression) {
      this.expression = expression;
   }

   public boolean isEquals() {
      return EQUAL == this;
   }

   public boolean isNotEquals() {
      return NOT_EQUAL == this;
   }

   public boolean isGreaterThan() {
      return GREATER_THAN == this;
   }

   public boolean isLessThan() {
      return LESS_THAN == this;
   }

   public boolean isGreaterThanEq() {
      return GREATER_THAN_EQ == this;
   }

   public boolean isLessThanEq() {
      return LESS_THAN_EQ == this;
   }

   @Override
   public String toString() {
      return expression;
   }
}
