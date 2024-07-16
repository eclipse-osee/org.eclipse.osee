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

import java.util.ArrayList;

/**
 * @author Shawn F. Cook
 */
public class VCastStatementCoverage {

   private final int id;
   private final int functionId;
   private final int line;
   private final int hitCount;
   private final int maxHitCount;
   private final boolean isMCDCPair;
   private final String abbrevCondition;
   private final String fullCondition;
   private final int numConditions;
   private final int condIndex;
   private final ArrayList<VCastMcdcCoveragePairRow> coverageRows;

   public VCastStatementCoverage(int id, int functionId, int line, int hitCount, int maxHitCount) {
      this(id, functionId, line, hitCount, maxHitCount, false, "", "", -1, -1, null);
   }

   public VCastStatementCoverage(int id, int functionId, int line, int hitCount, int maxHitCount, int numConditions) {
      this(id, functionId, line, hitCount, maxHitCount, false, "", "", numConditions, -1, null);
   }

   public VCastStatementCoverage(int id, int functionId, int line, int hitCount, int maxHitCount, boolean isMCDCPair, String abbrevCondition, String fullCondition, int numConditions, int condIndex, ArrayList<VCastMcdcCoveragePairRow> coverageRows) {
      this.id = id;
      this.functionId = functionId;
      this.line = line;
      this.hitCount = hitCount;
      this.maxHitCount = maxHitCount;
      this.isMCDCPair = isMCDCPair;
      this.abbrevCondition = abbrevCondition;
      this.fullCondition = fullCondition;
      this.numConditions = numConditions;
      this.condIndex = condIndex;
      this.coverageRows = coverageRows;
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

   public int getHitCount() {
      return hitCount;
   }

   public int getMaxHitCount() {
      return maxHitCount;
   }

   public boolean getIsMCDCPair() {
      return isMCDCPair;
   }

   public String getAbbrevCondition() {
      return abbrevCondition;
   }

   public String getFullCondition() {
      return fullCondition;
   }

   public int getNumConditions() {
      return numConditions;
   }

   public int getCondIndex() {
      return condIndex;
   }

   public ArrayList<VCastMcdcCoveragePairRow> getCoverageRows() {
      return coverageRows;
   }
}