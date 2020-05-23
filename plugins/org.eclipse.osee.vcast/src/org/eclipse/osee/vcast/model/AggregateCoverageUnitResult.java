/*********************************************************************
 * Copyright (c) 2010 Boeing
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
 * @author Donald G. Dunne
 */
public class AggregateCoverageUnitResult {
   private final String name;
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

   @Override
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
