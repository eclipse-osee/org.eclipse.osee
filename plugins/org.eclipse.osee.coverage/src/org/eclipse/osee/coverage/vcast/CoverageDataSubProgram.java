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
   private final List<LineNumToBranches> lineNumToBranches = new ArrayList<LineNumToBranches>();

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

   public void addLineNumToBranches(int lineNum, int branches) {
      lineNumToBranches.add(new LineNumToBranches(lineNum, branches));
   }

   public List<LineNumToBranches> getLineNumToBranches() {
      return lineNumToBranches;
   }

}
