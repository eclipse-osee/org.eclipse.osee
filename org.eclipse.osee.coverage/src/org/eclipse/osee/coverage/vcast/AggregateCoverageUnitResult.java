package org.eclipse.osee.coverage.vcast;

public class AggregateCoverageUnitResult {
   private String name;
   private Integer numLines;
   private Integer numCovered;

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
      return String.format("Aggregate Result [%s] - [%d] of [%d]", name, numCovered, numLines);
   }
}
