/*
 * Created on Oct 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public abstract class CoveragePackageBase implements ICoverage, ICoverageUnitProvider {
   List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   final XResultData logResultData = new XResultData(false);
   String guid = GUID.create();
   String name;
   boolean editable = true;
   protected final CoverageOptionManager coverageOptionManager;
   protected ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider;

   public CoveragePackageBase(String name, CoverageOptionManager coverageOptionManager) {
      this.name = name;
      this.coverageOptionManager = coverageOptionManager;
   }

   public abstract Date getDate();

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnit.setParent(this);
      if (!coverageUnits.contains(coverageUnit)) {
         coverageUnits.add(coverageUnit);
      }
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public abstract void getOverviewHtmlHeader(XResultData xResultData);

   public List<CoverageItem> getCoverageItems() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
   }

   @Override
   public String getCoveragePercentStr() {
      return CoverageUtil.getPercent(getCoverageItemsCovered().size(), getCoverageItems().size(), true).getSecond();
   }

   public int getCoveragePercent() {
      return CoverageUtil.getPercent(getCoverageItemsCovered().size(), getCoverageItems().size(), true).getFirst();
   }

   public List<CoverageItem> getCoverageItemsCovered() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems()) {
         if (!coverageItem.getCoverageMethod().getName().equals(CoverageOptionManager.Not_Covered.name)) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   public List<CoverageItem> getCoverageItemsCovered(CoverageOption... CoverageOption) {
      return CoverageUtil.getCoverageItemsCovered(getCoverageItems(), CoverageOption);
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      Set<ICoverage> items = new HashSet<ICoverage>();
      for (CoverageUnit coverageUnit : getCoverageUnits()) {
         items.add(coverageUnit);
         if (recurse) {
            items.addAll(coverageUnit.getChildren(recurse));
         }
      }
      return items;
   }

   public CoverageUnit getOrCreateParent(String namespace) {
      // Look for already existing CU
      for (ICoverage item : new CopyOnWriteArrayList<ICoverage>(getChildren(true))) {
         if (!(item instanceof CoverageUnit)) continue;
         CoverageUnit coverageUnit = (CoverageUnit) item;
         if (coverageUnit.getName().equals(namespace)) {
            return coverageUnit;
         }
      }
      // Create 
      String[] names = namespace.split("\\.");
      String nameStr = "";
      for (String name : names) {
         if (nameStr.equals("")) {
            nameStr = name;
         } else {
            nameStr = nameStr + "." + name;
         }
         if (getCoverageUnits().size() == 0) {
            CoverageUnit newCoverageUnit = new CoverageUnit(this, nameStr, "", coverageUnitFileContentsProvider);
            newCoverageUnit.setFolder(true);
            newCoverageUnit.setNamespace(nameStr);
            addCoverageUnit(newCoverageUnit);
            if (nameStr.equals(namespace)) return newCoverageUnit;
            continue;
         }

         // Look for already existing CU
         boolean found = false;
         for (ICoverage item : new CopyOnWriteArrayList<ICoverage>(getChildren(true))) {
            if (!(item instanceof CoverageUnit)) continue;
            if (item.getName().equals(nameStr)) {
               found = true;
               break;
            }
         }
         if (found) continue;

         // Create one if not exists

         // Find parent
         ICoverage parent = null;
         if (nameStr.equals(name)) {
            parent = this;
         } else {
            String parentNamespace = nameStr.replaceFirst("\\." + name + ".*$", "");
            parent = getOrCreateParent(parentNamespace);
         }
         // Create new coverage unit
         CoverageUnit newCoverageUnit = new CoverageUnit(parent, nameStr, "", coverageUnitFileContentsProvider);
         newCoverageUnit.setNamespace(nameStr);
         newCoverageUnit.setFolder(true);
         // Add to parent
         ((ICoverageUnitProvider) parent).addCoverageUnit(newCoverageUnit);
         // Return if this is our coverage unit
         if (nameStr.equals(namespace)) return newCoverageUnit;
      }
      return null;
   }

   public String getGuid() {
      return guid;
   }

   public void setCoverageUnits(List<CoverageUnit> coverageUnits) {
      this.coverageUnits = coverageUnits;
   }

   public String getName() {
      return name;
   }

   public XResultData getLog() {
      return logResultData;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isImportAllowed() {
      return isEditable().isTrue();
   }

   @Override
   public boolean isAssignable() {
      return isEditable().isTrue();
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return getChildren(false);
   }

   @Override
   public boolean isCovered() {
      for (CoverageUnit coverageUnit : coverageUnits) {
         if (!coverageUnit.isCovered()) return false;
      }
      return true;
   }

   @Override
   public Result isEditable() {
      if (!editable) return new Result("CoveragePackage locked for edits.");
      return Result.TrueResult;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public void removeCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.remove(coverageUnit);
   }

   @Override
   public OseeImage getOseeImage() {
      return null;
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public ICoverage getParent() {
      return null;
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   public abstract void saveKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException;

   public abstract void loadKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CoveragePackageBase other = (CoveragePackageBase) obj;
      if (getGuid() == null) {
         if (other.getGuid() != null) return false;
      } else if (!getGuid().equals(other.getGuid())) return false;
      return true;
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   @Override
   public String getOrderNumber() {
      return "";
   }

   public CoverageOptionManager getCoverageOptionManager() {
      return coverageOptionManager;
   }

   public ICoverageUnitFileContentsProvider getCoverageUnitFileContentsProvider() {
      return coverageUnitFileContentsProvider;
   }

   public void setCoverageUnitFileContentsProvider(ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      this.coverageUnitFileContentsProvider = coverageUnitFileContentsProvider;
   }

   public CoverageUnit createCoverageUnit(ICoverage parent, String name, String location) {
      return new CoverageUnit(parent, name, location, coverageUnitFileContentsProvider);
   }

}
