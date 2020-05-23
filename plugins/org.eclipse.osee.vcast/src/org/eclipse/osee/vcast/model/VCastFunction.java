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
public class VCastFunction {

   private final int id;
   private final int instrumentedFileId;
   private final int findex;
   private final String name;
   private final String canonicalName;
   private final int totalLines;
   private final int complexity;
   private final int numPairsOrPaths;

   public VCastFunction(int id, int instrumentedFileId, int findex, String name, String canonicalName, int totalLines, int complexity, int numPairsOrPaths) {
      this.id = id;
      this.instrumentedFileId = instrumentedFileId;
      this.findex = findex;
      this.name = name;
      this.canonicalName = canonicalName;
      this.totalLines = totalLines;
      this.complexity = complexity;
      this.numPairsOrPaths = numPairsOrPaths;
   }

   public int getId() {
      return id;
   }

   public int getInstrumentedFileId() {
      return instrumentedFileId;
   }

   public int getFindex() {
      return findex;
   }

   public String getName() {
      return name;
   }

   public String getCanonicalName() {
      return canonicalName;
   }

   public int getTotalLines() {
      return totalLines;
   }

   public int getComplexity() {
      return complexity;
   }

   public int getNumPairsOrPaths() {
      return numPairsOrPaths;
   }

}
