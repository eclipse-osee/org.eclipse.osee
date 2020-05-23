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
public class LineNumToBranches {

   private int lineNum;
   private int branches;

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
