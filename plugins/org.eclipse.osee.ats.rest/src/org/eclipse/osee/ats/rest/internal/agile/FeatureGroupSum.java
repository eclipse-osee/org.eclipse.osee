/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.agile;

/**
 * @author David.W.Miller
 */
public class FeatureGroupSum {
   private double sum = 0;
   private final String name;
   private final String description;

   FeatureGroupSum(String name, String description) {
      this.name = name;
      this.description = description;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public double getSum() {
      return sum;
   }

   public void addToSum(double amount) {
      sum += amount;
   }

   public String getHTML() {
      StringBuilder sb = new StringBuilder();
      sb.append("<tr>");
      sb.append("<td>" + name + "</td>");
      sb.append("<td>" + description + "</td>");
      sb.append("<td>" + Integer.valueOf(Double.valueOf(sum).intValue()).toString() + "</td>");
      sb.append("</tr>");
      return sb.toString();
   }

}
