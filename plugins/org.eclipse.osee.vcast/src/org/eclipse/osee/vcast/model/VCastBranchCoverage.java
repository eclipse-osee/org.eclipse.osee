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
public class VCastBranchCoverage {

   private final int id;
   private final int functionId;
   private final int line;
   private final int numConditions;
   private final int trueCount;
   private final int falseCount;
   private final int maxTrueCount;
   private final int maxFalseCount;

   public VCastBranchCoverage(int id, int functionId, int line, int numConditions, int trueCount, int falseCount, int maxTrueCount, int maxFalseCount) {
      super();
      this.id = id;
      this.functionId = functionId;
      this.line = line;
      this.numConditions = numConditions;
      this.trueCount = trueCount;
      this.falseCount = falseCount;
      this.maxTrueCount = maxTrueCount;
      this.maxFalseCount = maxFalseCount;
   }

   public int getId() {
      return id;
   }

   public int getFunctionId() {
      return functionId;
   }

   public int getLine() {
      return line;
   }

   public int getNumConditions() {
      return numConditions;
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

}
