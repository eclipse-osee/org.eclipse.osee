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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single <coverage_data></coverage_data> unit as specified in the CoverageDataFile <code file>.xml
 * 
 * @author Donald G. Dunne
 */
public class CoverageDataUnit {

   public static enum CoverageDataType {
      STATEMENT,
      BRANCH
   };

   public String name;
   public int index;
   public CoverageDataType coverageType;
   public List<CoverageDataSubProgram> subPrograms = new ArrayList<>();

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
