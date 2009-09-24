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
   private final String executeNum;
   private String lineNum;
   private String methodNum;
   private final CoverageUnit coverageUnit;

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, String executeNum) {
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

   public String getExecuteNum() {
      return executeNum;
   }

   public String getLineNum() {
      return lineNum;
   }

   public void setLineNum(String lineNum) {
      this.lineNum = lineNum;
   }

   public CoverageUnit getCoverageUnit() {
      return coverageUnit;
   }

   public String getMethodNum() {
      return methodNum;
   }

   public void setMethodNum(String methodNum) {
      this.methodNum = methodNum;
   }

}
