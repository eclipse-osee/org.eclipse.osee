/*
 * Created on Sep 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class CoverageDataSubProgram {

   private final String name;
   private int executionLines;
   private int complexity;
   private int covered;
   private int total;
   private final List<LineNumToBranches> lineNumToBranches = new ArrayList<LineNumToBranches>();

   public CoverageDataSubProgram(String name) {
      super();
      this.name = name;
   }

   public int getExecutionLines() {
      return executionLines;
   }

   public void setExecutionLines(int executionLines) {
      this.executionLines = executionLines;
   }

   public int getComplexity() {
      return complexity;
   }

   public void setComplexity(int complexity) {
      this.complexity = complexity;
   }

   public int getCovered() {
      return covered;
   }

   public void setCovered(int covered) {
      this.covered = covered;
   }

   public int getTotal() {
      return total;
   }

   public void setTotal(int total) {
      this.total = total;
   }

   public String getName() {
      return name;
   }

   public void addLineNumToBranches(int lineNum, int branches) {
      lineNumToBranches.add(new LineNumToBranches(lineNum, branches));
   }

   public List<LineNumToBranches> getLineNumToBranches() {
      return lineNumToBranches;
   }

}
