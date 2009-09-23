/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem {

   private CoverageMethodEnum coverageMethod = CoverageMethodEnum.None;
   private final int executeNum;
   private int lineNum;
   private int methodNum;
   private final CoverageUnit coverageUnit;

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, int executeNum) {
      super();
      this.coverageUnit = coverageUnit;
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

   public CoverageUnit getCoverageUnit() {
      return coverageUnit;
   }

   public int getMethodNum() {
      return methodNum;
   }

   public void setMethodNum(int methodNum) {
      this.methodNum = methodNum;
   }

}
