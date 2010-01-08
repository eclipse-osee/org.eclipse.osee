/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ElapsedTime;

/**
 * @author Donald G. Dunne
 */
public class OseeCoveragePackageStore extends OseeCoverageStore implements ISaveable {
   private final CoveragePackage coveragePackage;
   private CoverageOptionManager coverageOptionManager = CoverageOptionManagerDefault.instance();

   public OseeCoveragePackageStore(Artifact artifact) throws OseeCoreException {
      super(null, CoverageArtifactTypes.CoveragePackage);

      String coverageOptions =
            artifact.getSoleAttributeValueAsString(CoverageAttributes.COVERAGE_OPTIONS.getStoreName(), null);
      if (Strings.isValid(coverageOptions)) {
         coverageOptionManager = new CoverageOptionManager(coverageOptions);
      }

      this.artifact = artifact;
      this.coveragePackage = new CoveragePackage(artifact.getName(), getCoverageOptionManager());
      load(coverageOptionManager);
   }

   public OseeCoveragePackageStore(CoveragePackage coveragePackage) {
      super(coveragePackage, CoverageArtifactTypes.CoveragePackage);
      this.coveragePackage = coveragePackage;
   }

   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

   public static OseeCoveragePackageStore get(CoveragePackage coveragePackage) {
      return new OseeCoveragePackageStore(coveragePackage);
   }

   public static CoveragePackage get(Artifact artifact) throws OseeCoreException {
      OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(artifact);
      return packageStore.getCoveragePackage();
   }

   public void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      coveragePackage.clearCoverageUnits();
      getArtifact(false);
      if (artifact != null) {
         coveragePackage.setGuid(artifact.getGuid());
         coveragePackage.setName(artifact.getName());
         coveragePackage.setEditable(artifact.getSoleAttributeValue(CoverageAttributes.ACTIVE.getStoreName(), true));
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoverageUnit) || childArt.isOfType(CoverageArtifactTypes.CoverageFolder)) {
               coveragePackage.addCoverageUnit(OseeCoverageUnitStore.get(coveragePackage, childArt,
                     coverageOptionManager));
            }
         }
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      ElapsedTime elapsedTime = new ElapsedTime(getClass().getSimpleName() + " - save");
      artifact.setName(coveragePackage.getName());
      artifact.setSoleAttributeValue(CoverageAttributes.ACTIVE.getStoreName(), coveragePackage.isEditable().isTrue());
      for (CoverageUnit coverageUnit : coveragePackage.getCoverageUnits()) {
         OseeCoverageStore store = new OseeCoverageUnitStore(coverageUnit);
         store.save(transaction);
         Artifact childArt = store.getArtifact(false);
         if (childArt.getParent() == null && !(artifact.getChildren().contains(childArt))) {
            artifact.addChild(store.getArtifact(false));
         }
      }
      artifact.persist(transaction);
      elapsedTime.end();
      return Result.TrueResult;
   }

   public Result save(SkynetTransaction transaction, Collection<ICoverage> coverages) throws OseeCoreException {
      ElapsedTime elapsedTime = new ElapsedTime(getClass().getSimpleName() + " - save(coverages)");
      for (ICoverage coverage : coverages) {
         CoverageUnit coverageUnit = null;
         if (coverage instanceof CoverageItem) {
            coverageUnit = ((CoverageItem) coverage).getCoverageUnit();
         } else if (coverage instanceof CoverageUnit) {
            coverageUnit = ((CoverageUnit) coverage);
         } else {
            throw new OseeArgumentException("Unhandled coverage type");
         }
         OseeCoverageUnitStore store = new OseeCoverageUnitStore(coverageUnit);
         store.save(transaction);
      }
      elapsedTime.end();
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
         new OseeCoverageUnitStore(childCoverageUnit).delete(transaction, purge);
      }
   }

   @Override
   public Result isEditable() {
      return coveragePackage.isEditable();
   }

   public static Collection<Artifact> getCoveragePackageArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromType(CoverageArtifactTypes.CoveragePackage, CoverageUtil.getBranch());
   }

   public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
      try {
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Save");
         save(transaction, coverages);
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;

   }

   public CoverageOptionManager getCoverageOptionManager() {
      return coverageOptionManager;
   }

   public void setCoverageOptionManager(CoverageOptionManager coverageOptionManager) {
      this.coverageOptionManager = coverageOptionManager;
   }

}
