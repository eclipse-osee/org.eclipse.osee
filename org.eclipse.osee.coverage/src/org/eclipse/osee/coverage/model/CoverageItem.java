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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.jdk.core.util.ArrayUtil;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem implements ICoverageEditorItem {

   private CoverageMethodEnum coverageMethod = CoverageMethodEnum.None;
   private final String executeNum;
   private String lineNum;
   private String methodNum;
   private final CoverageUnit coverageUnit;
   private final Set<TestUnit> testUnits = new HashSet<TestUnit>();

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, String executeNum) {
      super();
      this.coverageUnit = coverageUnit;
      this.coverageMethod = coverageMethod;
      this.executeNum = executeNum;
   }

   public Set<TestUnit> getTestUnits() {
      return testUnits;
   }

   public void addTestUnit(TestUnit testUnit) {
      testUnits.add(testUnit);
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

   @Override
   public String toString() {
      return getMethodNum() + ", " + getExecuteNum();
   }

   @Override
   public User getUser() {
      return null;
   }

   @Override
   public Result isEditable() {
      return null;
   }

   @Override
   public void setUser(User user) {
   }

   @Override
   public boolean isCompleted() {
      return false;
   }

   public String getName() {
      return methodNum + ":" + executeNum;
   }

   @Override
   public String getCoverageEditorValue(XViewerColumn xCol) {
      if (xCol.equals(CoverageXViewerFactory.Line_Number)) return getLineNum();
      if (xCol.equals(CoverageXViewerFactory.Method_Number)) return getMethodNum();
      if (xCol.equals(CoverageXViewerFactory.Execution_Number)) return getExecuteNum();
      if (xCol.equals(CoverageXViewerFactory.Coverage_Method)) return getCoverageMethod().toString();
      if (xCol.equals(CoverageXViewerFactory.Parent_Coverage_Unit)) return getCoverageUnit().getName();
      if (xCol.equals(CoverageXViewerFactory.Test_Units)) return Collections.toString(", ", getTestUnits());
      return "";
   }

   @Override
   public Object[] getChildren() {
      return ArrayUtil.EMPTY_ARRAY;
   }

   @Override
   public OseeImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.ITEM_GREEN;
      }
      return CoverageImage.ITEM_RED;
   }

   @Override
   public boolean isCovered() {
      return getCoverageMethod() != CoverageMethodEnum.None && getCoverageMethod() != CoverageMethodEnum.Unknown;
   }
}
