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
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem implements ICoverage {

   CoverageMethodEnum coverageMethod = CoverageMethodEnum.Not_Covered;
   String coverageRationale;
   String executeNum;
   String lineNum;
   String methodNum;
   String text;
   private final CoverageUnit coverageUnit;
   final Set<String> testUnitNames = new HashSet<String>();
   String guid = GUID.create();
   private static String PROPERTY_STORE_ID = "coverage.item";

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, String executeNum) {
      super();
      this.coverageUnit = coverageUnit;
      this.coverageMethod = coverageMethod;
      this.executeNum = executeNum;
      if (this.coverageUnit instanceof ICoverageItemProvider) {
         ((ICoverageItemProvider) coverageUnit).addCoverageItem(this);
      }
   }

   public CoverageItem(CoverageUnit parentCoverageUnit, String xml) throws OseeCoreException {
      this(parentCoverageUnit, CoverageMethodEnum.Not_Covered, "0");
      fromXml(xml);
   }

   /**
    * Copies the coverage unit. Does not copy test units.
    */
   public CoverageItem copy(CoverageUnit parent) throws OseeCoreException {
      CoverageItem coverageitem = new CoverageItem(parent, coverageMethod, executeNum);
      coverageitem.setGuid(guid);
      coverageitem.setLineNum(lineNum);
      coverageitem.setText(text);
      coverageitem.setMethodNum(methodNum);
      coverageitem.setCoverageRationale(coverageRationale);
      return coverageitem;
   }

   public void fromXml(String xml) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      try {
         store.load(xml);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      if (!store.getId().equals(PROPERTY_STORE_ID)) {
         throw new OseeArgumentException(String.format("Invalid store id [%s] for CoverageItem", store.getId()));
      }
      setCoverageMethod(CoverageMethodEnum.valueOf(store.get("methodType")));
      this.executeNum = store.get("executeNum");
      setGuid(store.get("guid"));
      String lineNum = store.get("line");
      if (Strings.isValid(lineNum)) setLineNum(lineNum);
      String text = store.get("text");
      if (Strings.isValid(text)) setText(text);
      String rationale = store.get("rationale");
      if (Strings.isValid(rationale)) setCoverageRationale(rationale);
      String methodNum = store.get("methodNum");
      if (Strings.isValid(methodNum)) setMethodNum(methodNum);
      String testUnitNames = store.get("testUnits");
      if (Strings.isValid(testUnitNames)) {
         for (String name : testUnitNames.split(";"))
            addTestUnitName(name);
      }
   }

   public Set<String> getTestUnits() {
      return testUnitNames;
   }

   public void addTestUnitName(String testUnitName) {
      testUnitNames.add(testUnitName);
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
      return "[" + getMethodNum() + ", " + getExecuteNum() + "]";
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   public String getName() {
      return String.format("%s:%s [%s]", methodNum, executeNum, text);
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
      return getCoverageMethod() != CoverageMethodEnum.Not_Covered;
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
   public ICoverage getParent() {
      return coverageUnit;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String toXml() throws OseeCoreException {
      PropertyStore store = new PropertyStore(PROPERTY_STORE_ID);
      store.put("guid", guid);
      store.put("methodNum", methodNum);
      store.put("line", lineNum);
      if (Strings.isValid(getCoverageRationale())) {
         store.put("rationale", coverageRationale);
      }
      store.put("executeNum", executeNum);
      store.put("methodType", coverageMethod.toString());
      store.put("testUnits", Collections.toString(";", testUnitNames));
      store.put("text", text);
      try {
         return store.save();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public String getTestUnitNames(Collection<CoverageTestUnit> testUnits) {
      List<String> guids = new ArrayList<String>();
      for (CoverageTestUnit testUnit : testUnits) {
         guids.add(testUnit.getGuid());
      }
      return Collections.toString(guids, ",");
   }

   public String getCoverageRationale() {
      return coverageRationale;
   }

   public void setCoverageRationale(String rationale) {
      this.coverageRationale = rationale;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public int getCoveragePercent() {
      return isCovered() ? 100 : 0;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return java.util.Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return java.util.Collections.emptyList();
   }

   @Override
   public String getCoveragePercentStr() {
      return isCovered() ? "100" : "0";
   }

   @Override
   public boolean isFolder() {
      return false;
   }

}
