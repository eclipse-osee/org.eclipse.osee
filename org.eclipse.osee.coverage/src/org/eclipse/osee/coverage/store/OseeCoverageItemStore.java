/*
 * Created on Oct 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageItemStore extends OseeCoverageStore {
   public static String ARTIFACT_NAME = "Test Case";
   private final CoverageItem coverageItem;

   public OseeCoverageItemStore(CoverageItem coverageItem) {
      super(coverageItem, ARTIFACT_NAME);
      this.coverageItem = coverageItem;
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (CoverageTestUnit testUnit : coverageItem.getTestUnits()) {
         OseeCoverageStore.get(testUnit).delete(transaction, purge);
      }
   }

   public Result save(SkynetTransaction transaction) throws OseeCoreException {
      for (CoverageTestUnit testUnit : coverageItem.getTestUnits()) {
         OseeCoverageStore.get(testUnit).save(transaction);
      }
      return Result.TrueResult;
   }

   @Override
   public void load() throws OseeCoreException {
   }

}
