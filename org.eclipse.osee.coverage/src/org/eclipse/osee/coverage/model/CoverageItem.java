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
import org.eclipse.osee.coverage.util.CoverageUtil;
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
   String rationale;
   String orderNumber;
   String lineNum;
   String name;
   private final CoverageUnit coverageUnit;
   final Set<String> testUnitNames = new HashSet<String>();
   String guid = GUID.create();
   private static String PROPERTY_STORE_ID = "coverage.item";

   public CoverageItem(CoverageUnit coverageUnit, CoverageMethodEnum coverageMethod, String orderNumber) {
      super();
      this.coverageUnit = coverageUnit;
      this.coverageMethod = coverageMethod;
      this.orderNumber = orderNumber;
      if (coverageUnit != null) {
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
      CoverageItem coverageitem = new CoverageItem(parent, coverageMethod, orderNumber);
      coverageitem.setGuid(guid);
      coverageitem.setName(name);
      coverageitem.setOrderNumber(orderNumber);
      coverageitem.setRationale(rationale);

      return coverageitem;
   }

   public void fromXml(String xml) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      //      PropertyStoreRegEx store = new PropertyStoreRegEx();
      try {
         store.load(xml);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      if (!store.getId().equals(PROPERTY_STORE_ID)) {
         throw new OseeArgumentException(String.format("Invalid store id [%s] for CoverageItem", store.getId()));
      }
      setCoverageMethod(CoverageMethodEnum.valueOf(store.get("methodType")));
      setOrderNumber(store.get("order"));
      setGuid(store.get("guid"));
      setName(store.get("name"));
      String rationale = store.get("rationale");
      if (Strings.isValid(rationale)) setRationale(rationale);
      String testUnitNames = store.get("testUnits");
      if (Strings.isValid(testUnitNames)) {
         for (String testName : testUnitNames.split(";"))
            addTestUnitName(testName);
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

   public CoverageUnit getCoverageUnit() {
      return coverageUnit;
   }

   @Override
   public String toString() {
      return String.format("[Item : [%s][M: %s][E: %s][%s][Name: %s][Path: %s]]", getCoverageMethod(),
            getCoverageUnit().getOrderNumber(), getOrderNumber(), getGuid(), getName(), CoverageUtil.getFullPath(this));
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   public String getNameFull() {
      return String.format("%s:%s [%s]", getCoverageUnit().getOrderNumber(), orderNumber, name);
   }

   public String getName() {
      return name;
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
      store.put("line", lineNum);
      if (Strings.isValid(getRationale())) {
         store.put("rationale", rationale);
      }
      store.put("order", orderNumber);
      store.put("methodType", coverageMethod.toString());
      store.put("testUnits", Collections.toString(";", testUnitNames));
      store.put("name", name);
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

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public String getFileContents() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getNamespace() {
      if (getParent() != null && Strings.isValid(getParent().getNamespace())) {
         return getParent().getNamespace();
      }
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

   @Override
   public String getOrderNumber() {
      return orderNumber;
   }

   public void setOrderNumber(String orderNumber) {
      this.orderNumber = orderNumber;
   }

}
