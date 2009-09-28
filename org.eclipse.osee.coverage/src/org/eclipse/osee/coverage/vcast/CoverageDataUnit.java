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
public class CoverageDataUnit {

   public static enum CoverageDataType {
      STATEMENT, BRANCH
   };
   public String name;
   public int index;
   public CoverageDataType coverageType;
   public List<CoverageDataSubProgram> subPrograms = new ArrayList<CoverageDataSubProgram>();

   public CoverageDataUnit(String name) {
      super();
      this.name = name;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public CoverageDataType getCoverageType() {
      return coverageType;
   }

   public void setCoverageType(CoverageDataType coverageType) {
      this.coverageType = coverageType;
   }

   public void addSubProgram(CoverageDataSubProgram coverageDataSubProgram) {
      subPrograms.add(coverageDataSubProgram);
   }

   public List<CoverageDataSubProgram> getSubPrograms() {
      return subPrograms;
   }

   @Override
   public String toString() {
      return name;
   }

   public String getName() {
      return name;
   }
}
