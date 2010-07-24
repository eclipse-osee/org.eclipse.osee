/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.vcast;

public class AggregateCoverageUnitResult {
   private String name;
   private Integer numLines = null;
   private Integer numCovered = null;
   private String notes = "";

   public AggregateCoverageUnitResult(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Integer getNumLines() {
      return numLines;
   }

   public void setNumLines(Integer numLines) {
      this.numLines = numLines;
   }

   public Integer getNumCovered() {
      return numCovered;
   }

   public void setNumCovered(Integer numCovered) {
      this.numCovered = numCovered;
   }

   public String toString() {
      return String.format("Aggregate Result [%s] - [%d] of [%d] [%s]", name, numCovered, numLines, notes);
   }

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }
}
