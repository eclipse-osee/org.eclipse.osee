/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageUnitStore extends OseeCoverageStore {

   public static String ARTIFACT_NAME = "Coverage Unit";
   private final CoverageUnit coverageUnit;

   public OseeCoverageUnitStore(ICoverage parent, Artifact artifact) throws OseeCoreException {
      super(null, ARTIFACT_NAME);
      this.artifact = artifact;
      this.coverageUnit = new CoverageUnit(parent, artifact.getName(), "");
      load();
   }

   public OseeCoverageUnitStore(CoverageUnit coverageUnit) {
      super(coverageUnit, ARTIFACT_NAME);
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
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems(false)) {
         OseeCoverageStore.get(coverageItem).delete(transaction, purge);
      }
   }

   public void load() throws OseeCoreException {
      coverageUnit.clearCoverageUnits();
      coverageUnit.clearCoverageItems();
      Artifact artifact = getArtifact(false);
      if (artifact != null) {
         coverageUnit.setName(artifact.getName());
         coverageUnit.setGuid(artifact.getGuid());
         KeyValueArtifact keyValueArtifact =
               new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         for (String line : keyValueArtifact.getValues("cvgItem")) {
            coverageUnit.addCoverageItem(new CoverageItem(coverageUnit, line));
         }
         String text = keyValueArtifact.getValue("text");
         if (Strings.isValid(text)) {
            coverageUnit.setText(text);
         }
         String notes = keyValueArtifact.getValue("notes");
         if (Strings.isValid(notes)) {
            coverageUnit.setNotes(notes);
         }
         coverageUnit.setFolder((keyValueArtifact.getValue("folder") != null && keyValueArtifact.getValue("folder").equals(
               "true")));
         String assignees = keyValueArtifact.getValue("assignees");
         if (Strings.isValid(assignees)) {
            coverageUnit.setAssignees(assignees);
         }
         String namespace = keyValueArtifact.getValue("namespace");
         if (Strings.isValid(namespace)) {
            coverageUnit.setNamespace(namespace);
         }
         String location = keyValueArtifact.getValue("location");
         if (Strings.isValid(location)) {
            coverageUnit.setLocation(location);
         }
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.getArtifactTypeName().equals(ARTIFACT_NAME)) {
               coverageUnit.addCoverageUnit(OseeCoverageUnitStore.get(coverageUnit, childArt));
            }
         }
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      Artifact artifact = getArtifact(true);
      artifact.setName(coverageUnit.getName());

      KeyValueArtifact keyValueArtifact =
            new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
      List<String> items = new ArrayList<String>();
      for (CoverageItem coverageItem : coverageUnit.getCoverageItems()) {
         items.add(coverageItem.toXml());
         new OseeCoverageItemStore(coverageItem).save(transaction);
      }
      keyValueArtifact.setValues("cvgItem", items);
      if (coverageUnit.getNotes() != null) {
         keyValueArtifact.setValue("notes", coverageUnit.getNotes());
      }
      if (Strings.isValid(coverageUnit.getNamespace())) {
         keyValueArtifact.setValue("namespace", coverageUnit.getNamespace());
      }
      if (Strings.isValid(coverageUnit.getText())) {
         keyValueArtifact.setValue("text", coverageUnit.getText());
      }
      if (coverageUnit.isFolder()) {
         keyValueArtifact.setValue("folder", String.valueOf(coverageUnit.isFolder()));
      }
      if (Strings.isValid(coverageUnit.getAssignees())) {
         keyValueArtifact.setValue("assignees", coverageUnit.getAssignees());
      }
      if (Strings.isValid(coverageUnit.getLocation())) {
         keyValueArtifact.setValue("location", coverageUnit.getLocation());
      }
      keyValueArtifact.save();
      if (coverageUnit.getParent() != null) {
         Artifact parentArt = ArtifactQuery.getArtifactFromId(coverageUnit.getGuid(), CoverageUtil.getBranch());
         parentArt.addChild(artifact);
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

}
