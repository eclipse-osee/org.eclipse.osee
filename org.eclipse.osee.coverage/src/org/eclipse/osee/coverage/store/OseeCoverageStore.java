/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public abstract class OseeCoverageStore extends CoverageStore {
   protected Artifact artifact;
   private final IArtifactType artifactType;
   private final ICoverage coverage;

   public OseeCoverageStore(ICoverage coverage, IArtifactType artifactType) {
      super(coverage);
      this.coverage = coverage;
      this.artifactType = artifactType;
   }

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null) {
         try {
            artifact = ArtifactQuery.getArtifactFromId(coverage.getGuid(), CoverageUtil.getBranch());
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
         if (artifact == null && create) {
            artifact =
                  ArtifactTypeManager.addArtifact(artifactType, CoverageUtil.getBranch(), coverage.getGuid(), null);
         }
      }
      return artifact;

   }

   public abstract void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException;

   @Override
   public abstract void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException;

   @Override
   public Result save() throws OseeCoreException {
      try {
         SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Save");
         save(transaction);
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   @Override
   public void delete(boolean purge) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Coverage Save");
      delete(transaction, purge);
      transaction.execute();
   }

   public abstract Result save(SkynetTransaction transaction) throws OseeCoreException;

}
