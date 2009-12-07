/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

   public static String ARTIFACT_NAME = "Coverage Unit";
   public static String ARTIFACT_FOLDER_NAME = "Coverage Folder";
   private final CoverageUnit coverageUnit;

   public OseeCoverageUnitStore(ICoverage parent, Artifact artifact) throws OseeCoreException {
      super(null, artifact.getArtifactTypeName());
      this.artifact = artifact;
      this.coverageUnit = new CoverageUnit(parent, artifact.getName(), "");
      load();
   }

   public OseeCoverageUnitStore(CoverageUnit coverageUnit) {
      super(coverageUnit, coverageUnit.isFolder() ? ARTIFACT_FOLDER_NAME : ARTIFACT_NAME);
      this.coverageUnit = coverageUnit;
   }

   public static CoverageUnit get(ICoverage parent, Artifact artifact) throws OseeCoreException {
      OseeCoverageUnitStore unitStore = new OseeCoverageUnitStore(parent, artifact);
      return unitStore.getCoverageUnit();
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         OseeCoverageStore.get(childCoverageUnit).delete(transaction, purge);
      }
   }

   public void load() throws OseeCoreException {
      coverageUnit.clearCoverageUnits();
      coverageUnit.clearCoverageItems();
      Artifact artifact = getArtifact(false);
      if (artifact != null) {
         coverageUnit.setName(artifact.getName());
         coverageUnit.setGuid(artifact.getGuid());
         for (String value : artifact.getAttributesToStringList(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            coverageUnit.addCoverageItem(new CoverageItem(coverageUnit, value));
         }
         coverageUnit.setFileContents(artifact.getSoleAttributeValueAsString(
               CoverageAttributes.FILE_CONTENTS.getStoreName(), ""));
         coverageUnit.setNotes(artifact.getSoleAttributeValueAsString(CoverageAttributes.NOTES.getStoreName(), ""));
         coverageUnit.setFolder(artifact.isOfType(ARTIFACT_FOLDER_NAME));
         coverageUnit.setAssignees(artifact.getSoleAttributeValueAsString(CoverageAttributes.ASSIGNEES.getStoreName(),
               ""));
         coverageUnit.setNamespace(artifact.getSoleAttributeValueAsString(CoverageAttributes.NAMESPACE.getStoreName(),
               ""));
         coverageUnit.setOrderNumber(artifact.getSoleAttributeValueAsString(CoverageAttributes.ORDER.getStoreName(), ""));
         coverageUnit.setLocation(artifact.getSoleAttributeValueAsString(CoverageAttributes.LOCATION.getStoreName(), ""));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(ARTIFACT_NAME) || childArt.isOfType(ARTIFACT_FOLDER_NAME)) {
               coverageUnit.addCoverageUnit(OseeCoverageUnitStore.get(coverageUnit, childArt));
            }
         }
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      Artifact artifact = getArtifact(true);
      artifact.setName(coverageUnit.getName());

      List<String> items = new ArrayList<String>();
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
         items.add(coverageItem.toXml());
      }
      artifact.setAttributeValues(CoverageAttributes.COVERAGE_ITEM.getStoreName(), items);
      if (coverageUnit.getNotes() != null) {
         artifact.setSoleAttributeFromString(CoverageAttributes.NOTES.getStoreName(), coverageUnit.getNotes());
      }
      if (Strings.isValid(coverageUnit.getNamespace())) {
         artifact.setSoleAttributeFromString(CoverageAttributes.NAMESPACE.getStoreName(), coverageUnit.getNamespace());
      }
      if (Strings.isValid(coverageUnit.getFileContents())) {
         artifact.setSoleAttributeFromString(CoverageAttributes.FILE_CONTENTS.getStoreName(),
               coverageUnit.getFileContents());
      }
      if (Strings.isValid(coverageUnit.getOrderNumber())) {
         artifact.setSoleAttributeFromString(CoverageAttributes.ORDER.getStoreName(), coverageUnit.getOrderNumber());
      }
      if (Strings.isValid(coverageUnit.getAssignees())) {
         artifact.setSoleAttributeFromString(CoverageAttributes.ASSIGNEES.getStoreName(), coverageUnit.getAssignees());
      }
      if (Strings.isValid(coverageUnit.getLocation())) {
         artifact.setSoleAttributeFromString(CoverageAttributes.LOCATION.getStoreName(), coverageUnit.getLocation());
      }
      if (coverageUnit.getParent() != null) {
         Artifact parentArt =
               ArtifactQuery.getArtifactFromId(coverageUnit.getParent().getGuid(), CoverageUtil.getBranch());
         if (artifact.getParent() == null && !parentArt.getChildren().contains(artifact)) {
            parentArt.addChild(artifact);
         }
      }
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         OseeCoverageStore.get(childCoverageUnit).save(transaction);
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

   public static Collection<User> getAssignees(CoverageUnit coverageUnit) throws OseeCoreException {
      return getAssigneesFromString(coverageUnit.getAssignees());
   }

   private static Collection<User> getAssigneesFromString(String string) throws OseeCoreException {
      if (!Strings.isValid(string)) {
         return Collections.emptyList();
      }
      return UsersByIds.getUsers(string);
   }
}
