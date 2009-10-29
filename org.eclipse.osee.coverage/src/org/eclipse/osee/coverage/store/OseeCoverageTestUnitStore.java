/*
 * Created on Oct 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageTestUnitStore extends OseeCoverageStore {
   public static String ARTIFACT_NAME = "Test Case";
   private final CoverageTestUnit coverageTestUnit;

   public OseeCoverageTestUnitStore(CoverageTestUnit coverageTestUnit) {
      super(coverageTestUnit, ARTIFACT_NAME);
      this.coverageTestUnit = coverageTestUnit;
   }

   public OseeCoverageTestUnitStore(Artifact artifact) throws OseeCoreException {
      super(null, ARTIFACT_NAME);
      this.artifact = artifact;
      this.coverageTestUnit = new CoverageTestUnit(artifact.getName());
      load();
   }

   public void load() throws OseeCoreException {
      getArtifact(false);
      if (artifact != null) {
         coverageTestUnit.setName(artifact.getName());
         coverageTestUnit.setGuid(artifact.getGuid());
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      artifact.setName(coverageTestUnit.getName());
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
   }

   public CoverageTestUnit getCoverageTestUnit() {
      return coverageTestUnit;
   }

}
