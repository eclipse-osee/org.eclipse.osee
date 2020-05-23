/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastMcdcCoverageCondition {

   private final int id;
   private final int mcdcId;
   private final int condIndex;
   private final int trueCount;
   private final int falseCount;
   private final int maxTrueCount;
   private final int maxFalseCount;
   private final String condVariable;
   private final String condExpr;

   public VCastMcdcCoverageCondition(int id, int mcdcId, int condIndex, int trueCount, int falseCount, int maxTrueCount, int maxFalseCount, String condVariable, String condExpr) {
      super();
      this.id = id;
      this.mcdcId = mcdcId;
      this.condIndex = condIndex;
      this.trueCount = trueCount;
      this.falseCount = falseCount;
      this.maxTrueCount = maxTrueCount;
      this.maxFalseCount = maxFalseCount;
      this.condVariable = condVariable;
      this.condExpr = condExpr;
   }

   public int getId() {
      return id;
   }

   public int getMcdcId() {
      return mcdcId;
   }

   public int getCondIndex() {
      return condIndex;
   }

   public int getTrueCount() {
      return trueCount;
   }

   public int getFalseCount() {
      return falseCount;
   }

   public int getMaxTrueCount() {
      return maxTrueCount;
   }

   public int getMaxFalseCount() {
      return maxFalseCount;
   }

   public String getCondVariable() {
      return condVariable;
   }

   public String getCondExpr() {
      return condExpr;
   }

}
