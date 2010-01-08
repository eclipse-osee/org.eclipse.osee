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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * Single code unit (file/procedure/function) that can contain other Coverage Unit or Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class CoverageUnit implements ICoverage, ICoverageUnitProvider, ICoverageItemProvider {

   String name;
   String namespace;
   boolean folder;
   String notes;
   String assignees;
   String guid = GUID.create();
   final List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
   String location;
   String orderNumber = "";
   final List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   ICoverage parent;
   ICoverageUnitFileContentsProvider fileContentsProvider;

   public CoverageUnit(ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      super();
      this.parent = parent;
      this.name = name;
      this.location = location;
      this.fileContentsProvider = coverageUnitFileContentsProvider;
      if (parent != null && parent instanceof ICoverageUnitProvider) {
         ((ICoverageUnitProvider) parent).addCoverageUnit(this);
      }
   }

   public void clearCoverageUnits() {
      coverageUnits.clear();
   }

   public void clearCoverageItems() {
      coverageItems.clear();
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnit.setParent(this);
      if (!coverageUnits.contains(coverageUnit)) {
         coverageUnits.add(coverageUnit);
      }
   }

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

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getFileContents() throws OseeCoreException {
      if (fileContentsProvider == null) {
         throw new OseeStateException("No File Contents Provider Specified");
      }
      return fileContentsProvider.getFileContents(this);
   }

   public void setFileContents(String fileContents) throws OseeStateException {
      if (fileContentsProvider == null) {
         throw new OseeStateException("No File Contents Provider Specified");
      }
      this.fileContentsProvider.setFileContents(this, fileContents);
   }

   public String getGuid() {
      return guid;
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
   public Result isEditable() {
      return Result.TrueResult;
   }

   @Override
   public OseeImage getOseeImage() {
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (guid == null ? 0 : guid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      CoverageUnit other = (CoverageUnit) obj;
      if (guid == null) {
         if (other.guid != null) {
            return false;
         }
      } else if (!guid.equals(other.guid)) {
         return false;
      }
      return true;
   }

   @Override
   public ICoverage getParent() {
      return parent;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

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

   public List<CoverageItem> getCoverageItemsCovered(boolean recurse) {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems(recurse)) {
         if (coverageItem.isCovered()) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   public List<CoverageItem> getCoverageItemsCovered(boolean recurse, CoverageOption... CoverageOption) {
      List<CoverageOption> coverageMethods = Collections.getAggregate(CoverageOption);
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems(recurse)) {
         if (coverageMethods.contains(coverageItem.getCoverageMethod())) {
            items.add(coverageItem);
         }
      }
      return items;
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
      CoverageUnit coverageUnit = new CoverageUnit(parent, name, location, fileContentsProvider);
      coverageUnit.setGuid(guid);
      coverageUnit.setNamespace(namespace);
      coverageUnit.setNotes(notes);
      coverageUnit.setOrderNumber(orderNumber);
      coverageUnit.setFolder(folder);
      coverageUnit.setAssignees(assignees);
      coverageUnit.setLocation(location);
      if (includeItems) {
         for (CoverageItem coverageItem : coverageItems) {
            CoverageItem newCoverageItem =
                  new CoverageItem(coverageUnit, coverageItem.toXml(), CoverageOptionManagerDefault.instance());
            coverageUnit.addCoverageItem(newCoverageItem);
         }
      }
      coverageUnit.setFileContentsProvider(fileContentsProvider);
      return coverageUnit;
   }

   @Override
   public String getCoveragePercentStr() {
      return CoverageUtil.getPercent(getCoverageItemsCovered(true).size(), getCoverageItems(true).size(), true).getSecond();
   }

   public int getCoveragePercent() {
      return CoverageUtil.getPercent(getCoverageItemsCovered(true).size(), getCoverageItems(true).size(), true).getFirst();
   }

   public boolean isFolder() {
      return folder;
   }

   public void setFolder(boolean folder) {
      this.folder = folder;
   }

   public List<CoverageItem> getCoverageItemsCovered(CoverageOption... CoverageOption) {
      return CoverageUtil.getCoverageItemsCovered(getCoverageItems(), CoverageOption);
   }

   public void updateAssigneesAndNotes(CoverageUnit coverageUnit) {
      setNotes(coverageUnit.getNotes());
      setAssignees(coverageUnit.getAssignees());
   }

   public void setParent(ICoverage parent) {
      this.parent = parent;
   }

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

}
