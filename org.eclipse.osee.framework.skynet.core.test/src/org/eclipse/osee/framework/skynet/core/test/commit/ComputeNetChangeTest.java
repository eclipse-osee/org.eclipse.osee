package org.eclipse.osee.framework.skynet.core.test.commit;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange.GammaKind;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Low-level Change Data Test - checks :
 * 
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
   }

   @Test
   public void testSomething() throws OseeCoreException {
      List<OseeChange> changes = new ArrayList<OseeChange>();
      IOperation operation = new ComputeNetChangeOperation(changes);
      operation.run(new NullProgressMonitor());
      OseeChange expectedChange = null;

      for (OseeChange actualChange : changes) {
         checkOseeChange(expectedChange, actualChange);
      }

      // Commit Responsibility:
      // Compute net changes between source branch to destination branch including any merge changes and add those results to a new transaction on the destination branch.
      /*
       * Do not assume any particular branch hierarchical relationship between the source and destination branches.
       * Load the changes from the source branch.
       * Handle case where destination branch is missing an artifact that was modified (not new) on the source branch.
       * Filter out all items that are both new/introduced and deleted on the source branch
       * Filter out all gammas that are already current on the destination branch.
       * Apply changes from merge branch
       * Compute artifact mod type for commit transaction:
       * 1) New and modified -> new
       * 2) Introduced and modified -> introduced
       * Update tx_current for items on destination branch that will no longer be current
       * Update merge table with commit transaction id
       * Manage the branch state of the source, destination, and merge branches
       * If there are no net changes then stop the commit.
       */
   }

   private OseeChange createChange(GammaKind kind, int itemId, ModificationType baseSourceMod, long sourceGamma, ModificationType sourceMod) {
      OseeChange change = new OseeChange(sourceGamma, sourceMod);
      change.setItemId(itemId);
      change.setKind(kind);
      change.setBaseSourceModType(baseSourceMod);

      //      change.setDesinationGammaId(desinationGammaId);
      //      change.setDesinationModType(desinationModType);
      //
      //      change.setResultantGammaId(resultantGammaId);
      //      change.setResultantModType(resultantModType);
      return change;
   }

   private void checkOseeChange(OseeChange expected, OseeChange actual) {
      Assert.assertEquals(expected.getItemId(), expected.getItemId());
      Assert.assertEquals(expected.getKind(), expected.getKind());
      Assert.assertEquals(expected.getBaseSourceModType(), expected.getBaseSourceModType());

      Assert.assertEquals(expected.getCurrentSourceGammaId(), expected.getCurrentSourceGammaId());
      Assert.assertEquals(expected.getCurrentSourceModType(), expected.getCurrentSourceModType());

      Assert.assertEquals(expected.getDestinationGammaId(), expected.getDestinationGammaId());
      Assert.assertEquals(expected.getDestinationModType(), expected.getDestinationModType());

      Assert.assertEquals(expected.getNetGammaId(), expected.getNetGammaId());
      Assert.assertEquals(expected.getNetModType(), expected.getNetModType());
   }
}
