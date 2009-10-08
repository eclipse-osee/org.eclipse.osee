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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.swt.graphics.Image;

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
   private String guid = GUID.create();

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, String executeNum) {
      super();
      this.coverageUnit = coverageUnit;
      this.coverageMethod = coverageMethod;
      this.executeNum = executeNum;
   }

   public CoverageItem(CoverageUnit parentCoverageUnit, String xml) throws OseeCoreException {
      CoverageMethodEnum coverageMethod = CoverageMethodEnum.valueOf(AXml.getTagData(xml, "methodType"));
      String execNum = AXml.getTagData(xml, "execNum");
      this.coverageUnit = parentCoverageUnit;
      this.coverageMethod = coverageMethod;
      this.executeNum = execNum;
      setGuid(AXml.getTagData(xml, "guid"));
      String lineNum = AXml.getTagData(xml, "line");
      if (Strings.isValid(lineNum)) setLineNum(lineNum);
      String methodNum = AXml.getTagData(xml, "methodNum");
      if (Strings.isValid(methodNum)) setMethodNum(methodNum);
      String userId = AXml.getTagData(xml, "user");
      String location = AXml.getTagData(xml, "location");
      if (Strings.isValid(location)) setUser(UserManager.getUserByUserId(userId));
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
      return Result.FalseResult;
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
      return ArrayUtils.EMPTY_OBJECT_ARRAY;
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

   @Override
   public Image getCoverageEditorImage(XViewerColumn xCol) {
      return null;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CoverageItem other = (CoverageItem) obj;
      if (guid == null) {
         if (other.guid != null) return false;
      } else if (!guid.equals(other.guid)) return false;
      return true;
   }

   public String getGuid() {
      return guid;
   }

   @Override
   public ICoverageEditorItem getParent() {
      return coverageUnit;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String toXml() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(200);
      sb.append("<CvgItem>");
      sb.append(AXml.addTagData("guid", getGuid()));
      sb.append(AXml.addTagData("methodNum", getMethodNum()));
      sb.append(AXml.addTagData("line", getLineNum()));
      sb.append(AXml.addTagData("execNum", getExecuteNum()));
      if (getUser() != null) {
         sb.append(AXml.addTagData("user", getUser().getUserId()));
      }
      sb.append(AXml.addTagData("methodType", getCoverageMethod().toString()));
      sb.append(AXml.addTagData("testUnits", getTestUnitGuidList(getTestUnits())));
      sb.append("</CvgItem>");
      return sb.toString();
   }

   public String getTestUnitGuidList(Collection<TestUnit> testUnits) {
      List<String> guids = new ArrayList<String>();
      for (TestUnit testUnit : testUnits) {
         guids.add(testUnit.getGuid());
      }
      return Collections.toString(guids, ",");
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (TestUnit testUnit : testUnits) {
         testUnit.delete(transaction, purge);
      }
   }

   public void save(SkynetTransaction transaction) throws OseeCoreException {
      for (TestUnit testUnit : testUnits) {
         testUnit.save(transaction);
      }
   }

   @Override
   public Artifact getArtifact(boolean create) throws OseeCoreException {
      return null;
   }

}
