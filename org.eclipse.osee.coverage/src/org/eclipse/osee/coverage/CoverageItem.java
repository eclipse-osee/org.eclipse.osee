/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem {

   private CoverageMethodEnum coverageMethod = CoverageMethodEnum.None;
   private final int executeNum;
   private int lineNum;

   public CoverageItem(CoverageMethodEnum coverageMethod, int executeNum) {
      super();
      this.coverageMethod = coverageMethod;
      this.executeNum = executeNum;
   }

   public CoverageMethodEnum getCoverageMethod() {
      return coverageMethod;
   }

   public void setCoverageMethod(CoverageMethodEnum coverageMethod) {
      this.coverageMethod = coverageMethod;
   }

   public int getExecuteNum() {
      return executeNum;
   }

   public int getLineNum() {
      return lineNum;
   }

   public void setLineNum(int lineNum) {
      this.lineNum = lineNum;
   }

}
