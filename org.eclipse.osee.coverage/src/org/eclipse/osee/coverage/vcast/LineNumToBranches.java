/*
 * Created on Sep 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;


/**
 * @author Donald G. Dunne
 */
public class LineNumToBranches {

   private int lineNum;
   private int branches;

   public LineNumToBranches() {
   }

   public LineNumToBranches(int lineNum, int branches) {
      this.lineNum = lineNum;
      this.branches = branches;
   }

   public int getLineNum() {
      return lineNum;
   }

   public void setLineNum(int lineNum) {
      this.lineNum = lineNum;
   }

   public int getBranches() {
      return branches;
   }

   public void setBranches(int branches) {
      this.branches = branches;
   }

}
