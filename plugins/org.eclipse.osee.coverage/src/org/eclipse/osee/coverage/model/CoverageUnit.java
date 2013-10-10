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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * Single code unit (file/procedure/function) that can contain other Coverage Unit or Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class CoverageUnit extends NamedIdentity<String> implements IWorkProductRelatable, ICoverage, ICoverageUnitProvider, ICoverageItemProvider {

   String namespace;
   boolean folder;
   String notes;
   String assignees;
   final List<CoverageItem> coverageItems = new CopyOnWriteArrayList<CoverageItem>();
   String location;
   String orderNumber = "";
   String workProductTaskGuid;
   WorkProductTask workProductTask;
   final List<CoverageUnit> coverageUnits = new CopyOnWriteArrayList<CoverageUnit>();
   ICoverage parent;
   ICoverageUnitFileContentsProvider fileContentsProvider;
   ICoverageUnitFileContentsLoader fileContentsLoader;

   protected CoverageUnit(String guid, ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      super(guid, Strings.intern(name));
      this.parent = parent;
      this.location = location;
      this.fileContentsProvider = coverageUnitFileContentsProvider;
   }

   public void clearCoverageUnits() {
      coverageUnits.clear();
   }

   public void clearCoverageItems() {
      coverageItems.clear();
   }

   @Override
   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnit.setParent(this);
      if (!coverageUnits.contains(coverageUnit)) {
         coverageUnits.add(coverageUnit);
      }
   }

   @Override
   public List<CoverageUnit> getCoverageUnits() {
      return getCoverageUnits(false);
   }

   public List<CoverageUnit> getCoverageUnits(boolean recurse) {
      if (!recurse) {
         return coverageUnits;
      }
      List<CoverageUnit> units = new ArrayList<CoverageUnit>(coverageUnits);
      for (CoverageUnit coverageUnit : coverageUnits) {
         units.addAll(coverageUnit.getCoverageUnits(recurse));
      }
      return units;
   }

   public int getCoverageUnitCount(boolean recurse) {
      int count = 1;
      if (!recurse) {
         count = coverageUnits.size();
      } else {
         for (CoverageUnit coverageUnit : coverageUnits) {
            count += coverageUnit.getCoverageUnitCount(true);
         }
      }
      return count;
   }

   @Override
   public void addCoverageItem(CoverageItem coverageItem) {
      if (!coverageItems.contains(coverageItem)) {
         coverageItems.add(coverageItem);
      }
   }

   public List<CoverageItem> getCoverageItems(boolean recurse) {
      if (!recurse) {
         return coverageItems;
      }
      List<CoverageItem> items = new ArrayList<CoverageItem>(coverageItems);
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
   }

   public List<CoverageItem> getCoverageItemsOrdered() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      items.addAll(coverageItems);
      Collections.sort(items, new Comparator<CoverageItem>() {

         @Override
         public int compare(CoverageItem o1, CoverageItem o2) {
            return getCompareForInteger(o1.getOrderNumber(), o2.getOrderNumber());
         }

      });

      return items;
   }

   public List<CoverageUnit> getCoverageUnitsOrdered() {
      List<CoverageUnit> items = new ArrayList<CoverageUnit>();
      items.addAll(coverageUnits);
      Collections.sort(items, new Comparator<CoverageUnit>() {

         @Override
         public int compare(CoverageUnit o1, CoverageUnit o2) {
            return getCompareForInteger(o1.getOrderNumber(), o2.getOrderNumber());
         }

      });

      return items;
   }

   private int getCompareForInteger(String int1Str, String int2Str) {
      int int1 = 0, int2 = 0;
      if (Strings.isValid(int1Str)) {
         int1 = Integer.parseInt(int1Str);
      }
      if (Strings.isValid(int2Str)) {
         int2 = Integer.parseInt(int2Str);
      }
      if (int1 == int2) {
         return 0;
      } else if (int1 < int2) {
         return -1;
      } else if (int2 < int1) {
         return 1;
      } else {
         return 0;
      }
   }

   public CoverageItem getCoverageItem(String childUnitOrderNum, String itemOrderNumber) {
      for (CoverageUnit coverageUnit : coverageUnits) {
         if (coverageUnit.getOrderNumber().equals(childUnitOrderNum)) {
            for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
               if (coverageItem.getOrderNumber().equals(itemOrderNumber)) {
                  return coverageItem;
               }
            }
         }
      }
      return null;
   }

   @Override
   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = Strings.intern(location);
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      if (fileContentsProvider == null && fileContentsLoader == null) {
         throw new OseeStateException("fileContentsProvider and fileContentsLoader are both null");
      }
      String fileContents = null;
      if (fileContentsProvider != null) {
         fileContents = fileContentsProvider.getFileContents(this);
      }
      if (!Strings.isValid(fileContents)) {
         if (fileContentsLoader != null) {
            fileContents = fileContentsLoader.getText();
         }
      }

      return fileContents;
   }

   public void setFileContents(String fileContents) throws OseeStateException {
      if (fileContentsProvider == null) {
         throw new OseeStateException("No File Contents Provider Specified");
      }
      this.fileContentsProvider.setFileContents(this, fileContents);
   }

   public CoverageUnit getParentCoverageUnit() {
      if (parent instanceof CoverageUnit) {
         return (CoverageUnit) parent;
      }
      return null;
   }

   @Override
   public String toString() {
      return String.format("[Unit [%s][M: %s][%s][Path: %s]]", getName(), getOrderNumber(), getGuid(),
         CoverageUtil.getFullPath(this));
   }

   @Override
   public String toStringNoPackage() {
      return String.format("[Unit [%s][M: %s][%s][Path: %s]]", getName(), getOrderNumber(), getGuid(),
         CoverageUtil.getFullPath(this, false));
   }

   @Override
   public Result isEditable() {
      return Result.TrueResult;
   }

   @Override
   public KeyedImage getOseeImage() {
      boolean covered = isCovered();
      if (isFolder()) {
         if (covered) {
            return CoverageImage.FOLDER_GREEN;
         } else {
            return CoverageImage.FOLDER_RED;
         }
      } else if (covered) {
         return CoverageImage.UNIT_GREEN;
      }
      return CoverageImage.UNIT_RED;
   }

   @Override
   public boolean isCovered() {
      for (CoverageItem coverageItem : getCoverageItems(true)) {
         if (!coverageItem.isCovered()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public ICoverage getParent() {
      return parent;
   }

   @Override
   public String getNamespace() {
      if (namespace == null) {
         return getParent() == null ? null : getParent().getNamespace();
      }
      return namespace;
   }

   public void setNamespace(String namespace) {
      if (namespace == null) {
         this.namespace = null;
      } else {
         this.namespace = Strings.intern(namespace);
      }
   }

   @Override
   public String getAssignees() {
      return assignees;
   }

   public void setAssignees(String assignees) {
      this.assignees = assignees;
   }

   @Override
   public boolean isAssignable() {
      return true;
   }

   @Override
   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public int getCoverageItemsCoveredCount(boolean recurse) {
      int count = 0;
      for (CoverageItem item : coverageItems) {
         if (item.isCovered()) {
            count++;
         }
      }
      if (recurse) {
         for (CoverageUnit coverageUnit : coverageUnits) {
            count += coverageUnit.getCoverageItemsCoveredCount(true);
         }
      }
      return count;
   }

   public int getCoverageItemsCount(boolean recurse, CoverageOption coverageOption) {
      int count = 0;
      for (CoverageItem item : coverageItems) {
         if (item.getCoverageMethod().equals(coverageOption)) {
            count++;
         }
      }
      if (recurse) {
         for (CoverageUnit coverageUnit : coverageUnits) {
            count += coverageUnit.getCoverageItemsCount(true, coverageOption);
         }
      }
      return count;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return getChildren(false);
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      Set<ICoverage> items = new HashSet<ICoverage>(coverageItems);
      for (CoverageUnit coverageUnit : getCoverageUnits()) {
         items.add(coverageUnit);
         if (recurse) {
            items.addAll(coverageUnit.getChildren(recurse));
         }
      }
      return items;
   }

   @Override
   public void removeCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.remove(coverageUnit);
   }

   @Override
   public List<CoverageItem> getCoverageItems() {
      return coverageItems;
   }

   @Override
   public void removeCoverageItem(CoverageItem coverageItem) {
      coverageItems.remove(coverageItem);
   }

   public CoverageUnit copy(boolean includeItems) throws OseeCoreException {
      CoverageUnit coverageUnit = new CoverageUnit(getGuid(), parent, getName(), location, fileContentsProvider);
      coverageUnit.setNamespace(namespace);
      coverageUnit.setNotes(notes);
      coverageUnit.setOrderNumber(orderNumber);
      coverageUnit.setFolder(folder);
      coverageUnit.setAssignees(assignees);
      coverageUnit.setWorkProductTaskGuid(workProductTaskGuid);
      coverageUnit.setLocation(location);
      if (includeItems) {
         for (CoverageItem coverageItem : coverageItems) {
            CoverageItem newCoverageItem =
               CoverageItem.createCoverageItem(coverageUnit, coverageItem.toXml(),
                  CoverageOptionManagerDefault.instance(), coverageItem.getTestUnitProvider());
            newCoverageItem.setTestUnitProvider(coverageItem.getTestUnitProvider());
            coverageUnit.addCoverageItem(newCoverageItem);
         }
      }
      coverageUnit.setFileContentsProvider(fileContentsProvider);
      return coverageUnit;
   }

   @Override
   public String getCoveragePercentStr() {
      return CoverageUtil.getPercent(getCoverageItemsCoveredCount(true), getCoverageItems(true).size(), true).getSecond();
   }

   @Override
   public Double getCoveragePercent() {
      return CoverageUtil.getPercent(getCoverageItemsCoveredCount(true), getCoverageItems(true).size(), true).getFirst();
   }

   @Override
   public boolean isFolder() {
      return folder;
   }

   public void setFolder(boolean folder) {
      this.folder = folder;
   }

   public void updateAssigneesAndNotes(CoverageUnit coverageUnit) {
      setNotes(coverageUnit.getNotes());
      setAssignees(coverageUnit.getAssignees());
   }

   public void setParent(ICoverage parent) {
      this.parent = parent;
   }

   @Override
   public String getOrderNumber() {
      return orderNumber;
   }

   public void setOrderNumber(String orderNumber) {
      this.orderNumber = Strings.intern(orderNumber);
   }

   public void setFileContentsProvider(ICoverageUnitFileContentsProvider fileContentsProvider) {
      this.fileContentsProvider = fileContentsProvider;
   }

   public ICoverageUnitFileContentsProvider getFileContentsProvider() {
      return fileContentsProvider;
   }

   @Override
   public String getWorkProductTaskStr() {
      if (getWorkProductTask() != null) {
         return getWorkProductTask().toString();
      }
      if (Strings.isValid(workProductTaskGuid)) {
         return "Task Not Found: " + workProductTaskGuid;
      }
      return "";
   }

   @Override
   public String getWorkProductTaskGuid() {
      return workProductTaskGuid;
   }

   @Override
   public void setWorkProductTaskGuid(String workProductTaskGuid) {
      this.workProductTaskGuid = workProductTaskGuid;
   }

   @Override
   public WorkProductTask getWorkProductTask() {
      return workProductTask;
   }

   @Override
   public void setWorkProductTask(WorkProductTask workProductTask) {
      this.workProductTask = workProductTask;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   public void setFileContentsLoader(ICoverageUnitFileContentsLoader fileContentsLoader) {
      this.fileContentsLoader = fileContentsLoader;
   }
}