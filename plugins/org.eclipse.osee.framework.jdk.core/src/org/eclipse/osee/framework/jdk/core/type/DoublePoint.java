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
package org.eclipse.osee.framework.jdk.core.type;

import java.io.Serializable;

/**
 * @author Charles Shaw
 */
public class DoublePoint implements Serializable {

   private static final long serialVersionUID = 2417895993917844086L;
   protected double x;
   protected double y;

   /**
    * @param x The x coordinate of the point.
    * @param y The y coordinate of the point.
    */
   public DoublePoint(double x, double y) {
      super();
      this.x = x;
      this.y = y;
   }

   public DoublePoint() {
      super();
      this.x = 0.0;
      this.y = 0.0;
   }

   public double getX() {
      return x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return y;
   }

   public void setY(double y) {
      this.y = y;
   }

   @Override
   public boolean equals(Object object) {
      boolean matches = false;

      if (object instanceof DoublePoint) {
         DoublePoint point = (DoublePoint) object;
         matches = point.x == this.x && point.y == this.y;
      }

      return matches;
   }

   @Override
   public int hashCode() {
      int result = 17;
      int prime = 31;
      result = result * prime + ((Double) x).hashCode();
      result = result * prime + ((Double) y).hashCode();
      return result;
   }

   @Override
   public String toString() {
      return "(" + x + ", " + y + ")";
   }
}
