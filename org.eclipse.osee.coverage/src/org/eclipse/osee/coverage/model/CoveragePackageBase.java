/*
 * Created on Oct 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.ICoverageEditorProvider;
import org.eclipse.osee.coverage.editor.ICoverageTabProvider;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.util.CoverageMetrics;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class CoveragePackageBase implements ICoverageEditorItem, ICoverageEditorProvider, ICoverageTabProvider {
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final List<TestUnit> testUnits = new ArrayList<TestUnit>();
   private final XResultData logResultData = new XResultData(false);
   private Artifact artifact;
   private String guid = GUID.create();
   private String name;
   private boolean editable = true;

   public CoveragePackageBase(String name) {
      this.name = name;
   }

   public CoveragePackageBase(Artifact artifact) {
      super();
      this.artifact = artifact;
      try {
         load();
         CoverageManager.cache(this);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
      }
   }

   public void addTestUnit(TestUnit testUnit) {
      testUnits.add(testUnit);
   }

   public List<TestUnit> getTestUnits() {
      return testUnits;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnit.setParentCoverageEditorItem(this);
      coverageUnits.add(coverageUnit);
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public List<CoverageItem> getCoverageItems() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
   }

   @Override
   public String getCoveragePercentStr() {
      return CoverageMetrics.getPercent(getCoverageItemsCovered().size(), getCoverageItems().size()).getSecond();
   }

   public int getCoveragePercent() {
      return CoverageMetrics.getPercent(getCoverageItemsCovered().size(), getCoverageItems().size()).getFirst();
   }

   public List<CoverageItem> getCoverageItemsCovered() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems()) {
         if (coverageItem.getCoverageMethod() != CoverageMethodEnum.Not_Covered) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   public List<CoverageItem> getCoverageItemsCovered(CoverageMethodEnum... coverageMethodEnum) {
      List<CoverageMethodEnum> coverageMethods = Collections.getAggregate(coverageMethodEnum);
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems()) {
         if (coverageMethods.contains(coverageItem.getCoverageMethod())) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   @Override
   public Collection<? extends ICoverageEditorItem> getCoverageEditorItems(boolean recurse) {
      Set<ICoverageEditorItem> items = new HashSet<ICoverageEditorItem>();
      for (CoverageUnit coverageUnit : getCoverageUnits()) {
         items.add(coverageUnit);
         if (recurse) {
            items.addAll(coverageUnit.getCoverageEditorItems(recurse));
         }
      }
      return items;
   }

   public CoverageUnit getOrCreateParent(String namespace) {
      // Look for already existing CU
      for (ICoverageEditorItem item : new CopyOnWriteArrayList<ICoverageEditorItem>(getCoverageEditorItems(true))) {
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
            CoverageUnit newCoverageUnit = new CoverageUnit(this, nameStr, "");
            newCoverageUnit.setFolder(true);
            newCoverageUnit.setNamespace(nameStr);
            addCoverageUnit(newCoverageUnit);
            if (nameStr.equals(namespace)) return newCoverageUnit;
            continue;
         }

         // Look for already existing CU
         boolean found = false;
         for (ICoverageEditorItem item : new CopyOnWriteArrayList<ICoverageEditorItem>(getCoverageEditorItems(true))) {
            if (!(item instanceof CoverageUnit)) continue;
            if (item.getName().equals(nameStr)) {
               found = true;
               break;
            }
         }
         if (found) continue;

         // Create one if not exists

         // Find parent
         ICoverageEditorItem parent = null;
         if (nameStr.equals(name)) {
            parent = this;
         } else {
            String parentNamespace = nameStr.replaceFirst("\\." + name + ".*$", "");
            parent = getOrCreateParent(parentNamespace);
         }
         // Create new coverage unit
         CoverageUnit newCoverageUnit = new CoverageUnit(parent, nameStr, "");
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

   @Override
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
   public Collection<? extends ICoverageEditorItem> getChildrenItems() {
      return coverageUnits;
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
   public Object[] getChildren() {
      Collection<?> children = getChildrenItems();
      return children.toArray(new Object[children.size()]);
   }

   @Override
   public Image getCoverageEditorImage(XViewerColumn xCol) {
      return null;
   }

   @Override
   public String getCoverageEditorValue(XViewerColumn xCol) {
      return null;
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getText() {
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
   public boolean isCompleted() {
      return false;
   }

   @Override
   public ICoverageEditorItem getParent() {
      return null;
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   public abstract void saveKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException;

   public abstract void loadKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException;

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null && create) {
         artifact =
               ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch(), guid, null);
      }
      return artifact;
   }

   public void load() throws OseeCoreException {
      coverageUnits.clear();
      getArtifact(false);
      if (artifact != null) {
         setName(artifact.getName());
         KeyValueArtifact keyValueArtifact =
               new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         loadKeyValues(keyValueArtifact);
         if (Strings.isValid(keyValueArtifact.getValue("editable"))) {
            setEditable(keyValueArtifact.getValue("editable").equals("true"));
         }
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.getArtifactTypeName().equals(CoverageUnit.ARTIFACT_NAME)) {
               addCoverageUnit(new CoverageUnit(childArt));
            }
         }
      }
   }

   public void save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      System.out.println("save coveragePackage " + guid);

      artifact.setName(getName());
      KeyValueArtifact keyValueArtifact =
            new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
      saveKeyValues(keyValueArtifact);
      keyValueArtifact.setValue("editable", String.valueOf(String.valueOf(editable)));
      keyValueArtifact.save();
      for (CoverageUnit coverageUnit : coverageUnits) {
         coverageUnit.save(transaction);
         artifact.addChild(artifact);
      }
      artifact.persist(transaction);
   }

   public Result save() {
      try {
         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "Coverage Package");
         save(transaction);
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (CoverageUnit coverageUnit : getCoverageUnits()) {
         coverageUnit.delete(transaction, purge);
      }
   }

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

}
