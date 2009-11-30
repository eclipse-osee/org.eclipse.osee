/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.Collection;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoveragePackageStore extends OseeCoverageStore implements ISaveable {
   public static String ARTIFACT_NAME = "Coverage Package";
   private final CoveragePackage coveragePackage;

   public OseeCoveragePackageStore(Artifact artifact) throws OseeCoreException {
      super(null, ARTIFACT_NAME);
      this.artifact = artifact;
      this.coveragePackage = new CoveragePackage(artifact.getName());
      load();
   }

   public OseeCoveragePackageStore(CoveragePackage coveragePackage) {
      super(coveragePackage, ARTIFACT_NAME);
      this.coveragePackage = coveragePackage;
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

   public static CoveragePackage get(Artifact artifact) throws OseeCoreException {
      OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
      return packageStore.getCoveragePackage();
   }

   public void load() throws OseeCoreException {
      coveragePackage.clearCoverageUnits();
      getArtifact(false);
      if (artifact != null) {
         coveragePackage.setGuid(artifact.getGuid());
         coveragePackage.setName(artifact.getName());
         coveragePackage.setEditable(artifact.getSoleAttributeValue(CoverageAttributes.ACTIVE.getStoreName(), true));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(OseeCoverageUnitStore.ARTIFACT_NAME) || childArt.isOfType(
                  OseeCoverageUnitStore.ARTIFACT_FOLDER_NAME)) {
               coveragePackage.addCoverageUnit(OseeCoverageUnitStore.get(coveragePackage, childArt));
            }
         }
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      System.out.println("save coveragePackage " + coveragePackage.getGuid());

      artifact.setName(coveragePackage.getName());
      artifact.setSoleAttributeValue(CoverageAttributes.ACTIVE.getStoreName(), coveragePackage.isEditable().isTrue());
      for (CoverageUnit coverageUnit : coveragePackage.getCoverageUnits()) {
         OseeCoverageStore store = OseeCoverageStore.get(coverageUnit);
         store.save(transaction);
         Artifact childArt = store.getArtifact(false);
         if (childArt.getParent() == null && !(artifact.getChildren().contains(childArt))) {
            artifact.addChild(store.getArtifact(false));
         }
      }
      artifact.persist(transaction);
      return Result.TrueResult;
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (CoverageUnit childCoverageUnit : coveragePackage.getCoverageUnits()) {
         OseeCoverageStore.get(childCoverageUnit).delete(transaction, purge);
      }
   }

   @Override
   public Result isEditable() {
      return coveragePackage.isEditable();
   }

   public static Collection<Artifact> getCoveragePackageArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromType(OseeCoveragePackageStore.ARTIFACT_NAME, CoverageUtil.getBranch());
   }

}
