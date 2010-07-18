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
