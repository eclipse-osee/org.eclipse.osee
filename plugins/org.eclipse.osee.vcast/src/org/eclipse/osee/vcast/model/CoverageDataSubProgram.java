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
 * @author Donald G. Dunne
 */
public class CoverageDataSubProgram {

   private final String name;
   private int complexity;
   private int covered;
   private int total;
   private final List<LineNumToBranches> lineNumToBranches = new ArrayList<>();

   public CoverageDataSubProgram(String name) {
      super();
      this.name = name;
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

   public void addLineNumToBranches(LineNumToBranches lineToBranches) {
      lineNumToBranches.add(lineToBranches);
   }

   public List<LineNumToBranches> getLineNumToBranches() {
      return lineNumToBranches;
   }

}
