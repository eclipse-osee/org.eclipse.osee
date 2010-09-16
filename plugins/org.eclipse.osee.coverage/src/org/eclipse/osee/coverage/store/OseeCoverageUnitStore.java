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
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageUnitStore extends OseeCoverageStore {

   private final CoverageUnit coverageUnit;

   public OseeCoverageUnitStore(ICoverage parent, Artifact artifact, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      super(null, artifact.getArtifactType(), artifact.getBranch());
      this.artifact = artifact;
      this.coverageUnit =
         new CoverageUnit(artifact.getGuid(), parent, artifact.getName(), "",
            OseeCoverageUnitFileContentsProvider.getInstance(branch));
      load(coverageOptionManager);
   }

   public OseeCoverageUnitStore(CoverageUnit coverageUnit, Branch branch) {
      super(coverageUnit,
         coverageUnit.isFolder() ? CoverageArtifactTypes.CoverageFolder : CoverageArtifactTypes.CoverageUnit, branch);
      this.coverageUnit = coverageUnit;
   }

   public static CoverageUnit get(ICoverage parent, Artifact artifact, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      OseeCoverageUnitStore unitStore = new OseeCoverageUnitStore(parent, artifact, coverageOptionManager);
      return unitStore.getCoverageUnit();
   }

   public static OseeCoverageUnitStore get(CoverageUnit coverageUnit, Branch branch) {
      return new OseeCoverageUnitStore(coverageUnit, branch);
   }

   @Override
   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge) {
            getArtifact(false).purgeFromBranch();
         } else {
            getArtifact(false).deleteAndPersist(transaction);
         }
      }
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         new OseeCoverageUnitStore(childCoverageUnit, branch).delete(transaction, purge);
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
                  DbTestUnitProvider.instance());
            coverageUnit.addCoverageItem(item);
         }
         // Don't load file contents until needed
         coverageUnit.setFileContentsProvider(OseeCoverageUnitFileContentsProvider.getInstance(branch));
         coverageUnit.setNotes(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Notes, ""));
         coverageUnit.setFolder(artifact.isOfType(CoverageArtifactTypes.CoverageFolder));
         coverageUnit.setAssignees(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Assignees, ""));
         coverageUnit.setWorkProductGuid(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.WorkProductGuid,
            ""));
         coverageUnit.setNamespace(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Namespace, ""));
         coverageUnit.setOrderNumber(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Order, ""));
         coverageUnit.setLocation(artifact.getSoleAttributeValueAsString(CoverageAttributeTypes.Location, ""));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoverageUnit, CoverageArtifactTypes.CoverageFolder)) {
               coverageUnit.addCoverageUnit(OseeCoverageUnitStore.get(coverageUnit, childArt, coverageOptionManager));
            }
         }
      }
   }

   @Override
   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      Artifact artifact = getArtifact(true);
      artifact.setName(coverageUnit.getName());

      List<String> items = new ArrayList<String>();
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
         if (!(coverageItem.getTestUnitProvider() instanceof DbTestUnitProvider)) {
            // Get test names from coverageItem
            Collection<String> testUnitNames = coverageItem.getTestUnits();
            // Set provider to db provider
            coverageItem.setTestUnitProvider(DbTestUnitProvider.instance());
            // store off testUnitNames; this will add to db and replace names with db nameId
            coverageItem.setTestUnits(testUnitNames);
         }
         items.add(coverageItem.toXml());
      }
      artifact.setAttributeValues(CoverageAttributeTypes.Item, items);
      if (Strings.isValid(coverageUnit.getNotes())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Notes, coverageUnit.getNotes());
      }
      if (Strings.isValid(coverageUnit.getNamespace())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Namespace, coverageUnit.getNamespace());
      }
      if (Strings.isValid(coverageUnit.getWorkProductGuid())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.WorkProductGuid, coverageUnit.getWorkProductGuid());
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
      }
      if (Strings.isValid(coverageUnit.getAssignees())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Assignees, coverageUnit.getAssignees());
      }
      if (Strings.isValid(coverageUnit.getLocation())) {
         artifact.setSoleAttributeFromString(CoverageAttributeTypes.Location, coverageUnit.getLocation());
      }
      if (coverageUnit.getParent() != null) {
         Artifact parentArt = ArtifactQuery.getArtifactFromId(coverageUnit.getParent().getGuid(), branch);
         if (artifact.getParent() == null && !parentArt.getChildren().contains(artifact)) {
            parentArt.addChild(artifact);
         }
      }
      // Save current/new coverage items
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         new OseeCoverageUnitStore(childCoverageUnit, branch).save(transaction);
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
               new OseeCoverageUnitStore(coverageUnit, childArt, CoverageOptionManagerDefault.instance()).delete(
                  transaction, false);
            }
         }
      }

      artifact.persist(transaction);
      return Result.TrueResult;
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
}
