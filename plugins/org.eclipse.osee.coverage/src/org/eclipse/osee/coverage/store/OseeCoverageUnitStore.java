/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.event.CoverageChange;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.CoverageUnitFactory;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.ITestUnitProvider;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageUnitStore extends OseeCoverageStore {
   private final CoverageUnit coverageUnit;
   private final Artifact coveragePackage;
   private final Artifact readOnlyTestUnitNames;
   private final static Map<Artifact, ITestUnitProvider> testUnitProviderMap =
      new HashMap<Artifact, ITestUnitProvider>();

   public OseeCoverageUnitStore(ICoverage parent, Artifact artifact, CoverageOptionManager coverageOptionManager, Artifact coveragePackage) throws OseeCoreException {
      super(null, artifact.getArtifactType(), artifact.getBranch());
      this.artifact = artifact;
      this.coveragePackage = coveragePackage;
      this.readOnlyTestUnitNames = null;
      this.coverageUnit =
         CoverageUnitFactory.createCoverageUnit(artifact.getGuid(), parent, artifact.getName(), "",
            OseeCoverageUnitFileContentsProvider.getInstance(branch), false);
      load(coverageOptionManager);
   }

   public OseeCoverageUnitStore(CoverageUnit coverageUnit, IOseeBranch branch) {
      super(coverageUnit,
         coverageUnit.isFolder() ? CoverageArtifactTypes.CoverageFolder : CoverageArtifactTypes.CoverageUnit, branch);
      this.coverageUnit = coverageUnit;
      this.coveragePackage = null;
      this.readOnlyTestUnitNames = null;
   }

   public OseeCoverageUnitStore(CoverageUnit coverageUnit, IOseeBranch branch, Artifact coveragePackage, Artifact testUnitNames) {
      super(coverageUnit,
         coverageUnit.isFolder() ? CoverageArtifactTypes.CoverageFolder : CoverageArtifactTypes.CoverageUnit, branch);
      this.coverageUnit = coverageUnit;
      this.coveragePackage = coveragePackage;
      this.readOnlyTestUnitNames = testUnitNames;
   }

   public static CoverageUnit get(ICoverage parent, Artifact artifact, CoverageOptionManager coverageOptionManager, Artifact coveragePackage) throws OseeCoreException {
      OseeCoverageUnitStore unitStore =
         new OseeCoverageUnitStore(parent, artifact, coverageOptionManager, coveragePackage);
      return unitStore.getCoverageUnit();
   }

   public static OseeCoverageUnitStore get(CoverageUnit coverageUnit, IOseeBranch branch) {
      return new OseeCoverageUnitStore(coverageUnit, branch);
   }

   @Override
   public void delete(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge) {
            getArtifact(false).purgeFromBranch();
         } else {
            getArtifact(false).deleteAndPersist(transaction);
         }
      }
      coverageEvent.getCoverages().add(new CoverageChange(coverageUnit, CoverageEventType.Deleted));
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         new OseeCoverageUnitStore(childCoverageUnit, branch).delete(transaction, coverageEvent, purge);
      }
   }

   public void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      coverageUnit.clearCoverageUnits();
      coverageUnit.clearCoverageItems();
      Artifact artifact = getArtifact(false);
      if (artifact != null) {
         for (String value : artifact.getAttributesToStringList(CoverageAttributeTypes.Item)) {
            CoverageItem item =
               CoverageItem.createCoverageItem(coverageUnit, value, coverageOptionManager,
                  getTestUnitProvider(coveragePackage, readOnlyTestUnitNames));
            coverageUnit.addCoverageItem(item);
         }
         // Don't load file contents until needed
         coverageUnit.setFileContentsProvider(OseeCoverageUnitFileContentsProvider.getInstance(branch));
         coverageUnit.setNotes(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Notes, ""));
         coverageUnit.setFolder(artifact.isOfType(CoverageArtifactTypes.CoverageFolder));
         coverageUnit.setAssignees(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Assignees, ""));
         coverageUnit.setWorkProductTaskGuid(artifact.getSoleAttributeValueAsString(
            CoverageAttributeTypes.WorkProductTaskGuid, ""));
         coverageUnit.setNamespace(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Namespace, ""));
         coverageUnit.setOrderNumber(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Order, ""));
         coverageUnit.setLocation(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Location, ""));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoverageUnit, CoverageArtifactTypes.CoverageFolder)) {
               coverageUnit.addCoverageUnit(OseeCoverageUnitStore.get(coverageUnit, childArt, coverageOptionManager,
                  coveragePackage));
            }
         }
      }
   }

   public static ITestUnitProvider getTestUnitProvider(Artifact coveragePkg, Artifact readOnly) {
      ITestUnitProvider testUnitProvider = testUnitProviderMap.get(coveragePkg);

      if (testUnitProvider == null) {
         ArtifactTestUnitStore store = new ArtifactTestUnitStore(coveragePkg, readOnly);
         testUnitProvider = new TestUnitCache(store);
         testUnitProviderMap.put(coveragePkg, testUnitProvider);
      }
      return testUnitProvider;
   }

   public void reloadItem(CoverageEventType eventType, CoverageItem currentCoverageItem, CoverageChange change, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      Artifact artifact = getArtifact(false);

      if (artifact == null) {
         return;
      }
      if (eventType == CoverageEventType.Modified) {
         for (String value : artifact.getAttributesToStringList(CoverageAttributeTypes.Item)) {
            CoverageItem dbChangedItem =
               CoverageItem.createCoverageItem(coverageUnit, value, coverageOptionManager,
                  getTestUnitProvider(coveragePackage, readOnlyTestUnitNames));
            if (currentCoverageItem.getGuid().equals(dbChangedItem.getGuid())) {
               currentCoverageItem.copy(currentCoverageItem, dbChangedItem);
            }
         }
      } else if (eventType == CoverageEventType.Deleted) {
         coverageUnit.removeCoverageItem(currentCoverageItem);
      } else if (eventType == CoverageEventType.Added) {
         // do nothing; full coverage unit needs reload
      }
   }

   @Override
   public Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, CoverageOptionManager coverageOptionManager, Artifact parentArt) throws OseeCoreException {
      Artifact artifact = getArtifact(true);
      artifact.setName(coverageUnit.getName());

      List<String> items = new ArrayList<String>();
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
         // Get test names from coverageItem
         Collection<String> testUnitNames = coverageItem.getTestUnits();
         // Set provider to db provider
         coverageItem.setTestUnitProvider(getTestUnitProvider(coveragePackage, readOnlyTestUnitNames));
         // store off testUnitNames; this will add to db and replace names with db nameId
         coverageItem.setTestUnits(testUnitNames);
         items.add(coverageItem.toXml());
      }
      artifact.setAttributeValues(CoverageAttributeTypes.Item, items);
      // Determine which items have changed and log for event
      for (Attribute<Object> attr : artifact.getAttributes(CoverageAttributeTypes.Item)) {
         if (attr.isDirty()) {
            try {
               Pair<String, String> nameGuid = CoverageItem.getNameGuidFromStore((String) attr.getValue());
               CoverageChange change =
                  new CoverageChange(nameGuid.getFirst(), nameGuid.getSecond(), CoverageEventType.Modified);

               switch (attr.getModificationType()) {
                  case NEW:
                  case UNDELETED:
                  case INTRODUCED:
                     change.setEventType(CoverageEventType.Added);
                     break;
                  case DELETED:
                     change.setEventType(CoverageEventType.Deleted);
                     break;
               }

               coverageEvent.getCoverages().add(change);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      if (Strings.isValid(coverageUnit.getNotes())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Notes, coverageUnit.getNotes());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.Notes);
      }
      if (Strings.isValid(coverageUnit.getNamespace())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Namespace, coverageUnit.getNamespace());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.Namespace);
      }
      if (Strings.isValid(coverageUnit.getWorkProductTaskGuid())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.WorkProductTaskGuid,
            coverageUnit.getWorkProductTaskGuid());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.WorkProductTaskGuid);
      }
      if (coverageUnit.getFileContentsProvider() != null && coverageUnit.getFileContentsProvider() != OseeCoverageUnitFileContentsProvider.getInstance(branch)) {
         String fileContents = coverageUnit.getFileContents();
         if (Strings.isValid(fileContents)) {
            coverageUnit.setFileContentsProvider(OseeCoverageUnitFileContentsProvider.getInstance(branch));
            coverageUnit.setFileContents(fileContents);
         }
      }
      if (Strings.isValid(coverageUnit.getOrderNumber())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Order, coverageUnit.getOrderNumber());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.Order);
      }
      if (Strings.isValid(coverageUnit.getAssignees())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Assignees, coverageUnit.getAssignees());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.Assignees);
      }
      if (Strings.isValid(coverageUnit.getLocation())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Location, coverageUnit.getLocation());
      } else {
         artifact.deleteAttributes(CoverageAttributeTypes.Location);
      }
      // For increased performance, parent Coverage unit calls save operation by passing in itself as the parent instead of having to query 
      if (coverageUnit.getParent() != null) {
         Artifact parentArtifact;
         if (parentArt == null) {
            parentArtifact = ArtifactQuery.getArtifactFromId(coverageUnit.getParent().getGuid(), branch);
         } else {
            parentArtifact = parentArt;
         }
         if (artifact.getParent() == null && !parentArtifact.getChildren().contains(artifact)) {
            parentArtifact.addChild(artifact);
         }
      }
      // Save current/new coverage items
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         new OseeCoverageUnitStore(childCoverageUnit, branch, coveragePackage, null).save(transaction, coverageEvent,
            coverageOptionManager, artifact);
      }
      // Delete removed coverage units and folders
      for (Artifact childArt : artifact.getChildren()) {
         if (childArt.isOfType(CoverageArtifactTypes.CoverageUnit, CoverageArtifactTypes.CoverageFolder)) {
            boolean found = false;
            for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
               if (childCoverageUnit.getGuid().equals(childArt.getGuid())) {
                  found = true;
                  break;
               }
            }
            if (!found) {
               new OseeCoverageUnitStore(coverageUnit, childArt, coverageOptionManager, coveragePackage).delete(
                  transaction, coverageEvent, false);
            }
         }
      }

      artifact.persist(transaction);
      if (artifact.isDirty()) {
         CoverageChange change = new CoverageChange(coverageUnit, CoverageEventType.Modified);
         if (artifact.getModType() == ModificationType.NEW) {
            change.setEventType(CoverageEventType.Modified);
         }
         coverageEvent.getCoverages().add(change);
      }

      return Result.TrueResult;
   }

   @Override
   public void saveTestUnitNames(SkynetTransaction transaction) throws OseeCoreException {
      getTestUnitProvider(coveragePackage, readOnlyTestUnitNames).save(transaction);
   }

   public CoverageUnit getCoverageUnit() {
      return coverageUnit;
   }

   public static void setAssignees(CoverageUnit coverageUnit, User user) throws OseeCoreException {
      setAssignees(coverageUnit, Collections.singleton(user));
   }

   public static void setAssignees(CoverageUnit coverageUnit, Collection<User> users) throws OseeCoreException {
      coverageUnit.setAssignees(getAssigneesToString(users));
   }

   private static String getAssigneesToString(Collection<User> users) throws OseeCoreException {
      return UsersByIds.getStorageString(users);
   }

   public static Collection<User> getAssignees(CoverageUnit coverageUnit) {
      return getAssigneesFromString(coverageUnit.getAssignees());
   }

   private static Collection<User> getAssigneesFromString(String string) {
      if (!Strings.isValid(string)) {
         return Collections.emptyList();
      }
      return UsersByIds.getUsers(string);
   }

   @Override
   public CoveragePackageEvent getBaseCoveragePackageEvent(CoverageEventType coverageEventType) {
      throw new IllegalArgumentException("Should never be called");
   }

   @Override
   public Result save(SkynetTransaction transaction, CoveragePackageEvent coverageEvent, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      save(transaction, coverageEvent, coverageOptionManager, null);
      return null;
   }
}
