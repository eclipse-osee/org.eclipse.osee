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
import java.util.List;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class CoverageItem extends NamedIdentity implements ICoverage, IWorkProductRelatable {

   CoverageOption coverageMethod = CoverageOptionManager.Not_Covered;
   String rationale;
   String orderNumber;
   String workProductGuid;
   WorkProductTask workProductTask;

   private final CoverageUnit coverageUnit;
   private static String PROPERTY_STORE_ID = "coverage.item";
   private ITestUnitProvider testUnitProvider;

   public CoverageItem(CoverageUnit coverageUnit, CoverageOption coverageMethod, String orderNumber) {
      this(null, coverageUnit, coverageMethod, orderNumber);
   }

   public CoverageItem(String guid, CoverageUnit coverageUnit, CoverageOption coverageMethod, String orderNumber) {
      super(guid, "");
      this.coverageUnit = coverageUnit;
      this.coverageMethod = coverageMethod;
      this.orderNumber = orderNumber;
      if (coverageUnit != null) {
         ((ICoverageItemProvider) coverageUnit).addCoverageItem(this);
      }
   }

   public static CoverageItem createCoverageItem(CoverageUnit parentCoverageUnit, String xml, CoverageOptionManager coverageOptionManager, ITestUnitProvider testUnitProvider) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      try {
         store.load(xml);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      if (!store.getId().equals(PROPERTY_STORE_ID)) {
         throw new OseeArgumentException("Invalid store id [%s] for CoverageItem", store.getId());
      }

      CoverageItem item =
         new CoverageItem(store.get("guid"), parentCoverageUnit, CoverageOptionManager.Not_Covered, "0");
      item.testUnitProvider = testUnitProvider;
      item.setFromPropertyStore(store, coverageOptionManager);

      return item;
   }

   /**
    * Copies the coverage unit. Does not copy test units.
    */
   public CoverageItem copy(CoverageUnit parent) throws OseeCoreException {
      CoverageItem coverageItem = new CoverageItem(getGuid(), parent, coverageMethod, orderNumber);
      copy(this, coverageItem);
      return coverageItem;
   }

   public void copy(CoverageItem fromItem, CoverageItem toItem) throws OseeCoreException {
      toItem.setName(fromItem.getName());
      toItem.setOrderNumber(fromItem.getOrderNumber());
      toItem.setRationale(fromItem.getRationale());
      toItem.setTestUnitProvider(fromItem.getTestUnitProvider());
      toItem.setWorkProductGuid(fromItem.getWorkProductGuid());
      toItem.setWorkProductTask(fromItem.getWorkProductTask());
   }

   public Collection<String> getTestUnits() throws OseeCoreException {
      if (testUnitProvider == null) {
         return java.util.Collections.emptyList();
      }
      return testUnitProvider.getTestUnits(this);
   }

   public void addTestUnitName(String testUnitName) throws OseeCoreException {
      if (testUnitProvider == null) {
         testUnitProvider = new SimpleTestUnitProvider();
      }
      testUnitProvider.addTestUnit(this, testUnitName);
   }

   public void setTestUnits(Collection<String> testUnitNames) throws OseeCoreException {
      if (testUnitProvider == null) {
         return;
      }
      testUnitProvider.setTestUnits(this, testUnitNames);
   }

   public CoverageOption getCoverageMethod() {
      return coverageMethod;
   }

   public void setCoverageMethod(CoverageOption coverageMethod) {
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
      return String.format("%s:%s [%s]", getCoverageUnit().getOrderNumber(), orderNumber, getName());
   }

   @Override
   public KeyedImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.ITEM_GREEN;
      }
      return CoverageImage.ITEM_RED;
   }

   @Override
   public boolean isCovered() {
      return !getCoverageMethod().getName().equals(CoverageOptionManager.Not_Covered.getName());
   }

   @Override
   public ICoverage getParent() {
      return coverageUnit;
   }

   public String getTestUnitNames(Collection<CoverageTestUnit> testUnits) {
      List<String> guids = new ArrayList<String>();
      for (CoverageTestUnit testUnit : testUnits) {
         guids.add(testUnit.getGuid());
      }
      return Collections.toString(",", guids);
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   @Override
   public String getFileContents() {
      return getName();
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
   public String getAssignees() {
      return "";
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public Double getCoveragePercent() {
      return isCovered() ? 100.0 : 0.0;
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
      this.orderNumber = Strings.intern(orderNumber);
   }

   /**
    * Provide test unit provider. SimpleTestUnitProvider will be used by default.
    */
   public void setTestUnitProvider(ITestUnitProvider testUnitProvider) {
      this.testUnitProvider = testUnitProvider;
   }

   public static Pair<String, String> getNameGuidFromStore(String string) throws Exception {
      PropertyStore store = new PropertyStore();
      store.load(string);
      return new Pair<String, String>(store.get("name"), store.get("guid"));
   }

   private void setFromPropertyStore(PropertyStore store, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      setCoverageMethod(coverageOptionManager.get(store.get("methodType")));
      if (Strings.isValid(store.get("order"))) {
         setOrderNumber(store.get("order"));
      }
      if (Strings.isValid(store.get("name"))) {
         setName(store.get("name"));
      }
      if (Strings.isValid(store.get("rationale"))) {
         setRationale(store.get("rationale"));
      }
      if (Strings.isValid(store.get("workProductGuid"))) {
         setWorkProductGuid(store.get("workProductGuid"));
      }
      if (testUnitProvider == null) {
         testUnitProvider = new SimpleTestUnitProvider();
      }
      String testUnitsStr = store.get("testUnits");
      if (Strings.isValid(testUnitsStr)) {
         testUnitProvider.fromXml(this, testUnitsStr);
      }
   }

   public String toXml() throws OseeCoreException {
      return toXml(testUnitProvider);
   }

   public String toXml(ITestUnitProvider testUnitProvider) throws OseeCoreException {
      PropertyStore store = new PropertyStore(PROPERTY_STORE_ID);
      store.put("guid", getGuid());
      if (Strings.isValid(getRationale())) {
         store.put("rationale", rationale);
      }
      if (Strings.isValid(orderNumber)) {
         store.put("order", orderNumber);
      }
      if (Strings.isValid(workProductGuid)) {
         store.put("workProductGuid", workProductGuid);
      }
      store.put("methodType", coverageMethod.getName());
      if (testUnitProvider != null) {
         if (Strings.isValid(testUnitProvider.toXml(this))) {
            store.put("testUnits", testUnitProvider.toXml(this));
         }
      }
      if (Strings.isValid(getName())) {
         store.put("name", getName());
      }
      String toReturn = null;
      try {
         toReturn = store.save();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   public ITestUnitProvider getTestUnitProvider() {
      return testUnitProvider;
   }

   @Override
   public String getWorkProductTaskStr() {
      if (getWorkProductTask() != null) {
         return getWorkProductTask().toString();
      }
      if (Strings.isValid(workProductGuid)) {
         return "Task Not Found: " + workProductGuid;
      }
      return "";
   }

   @Override
   public String getWorkProductGuid() {
      return workProductGuid;
   }

   @Override
   public void setWorkProductGuid(String workProductGuid) {
      this.workProductGuid = workProductGuid;
   }

   @Override
   public WorkProductTask getWorkProductTask() {
      return workProductTask;
   }

   @Override
   public void setWorkProductTask(WorkProductTask workProductTask) {
      this.workProductTask = workProductTask;
   }

}