package org.eclipse.osee.framework.skynet.core.test.commit;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MERGED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem.GammaKind;
import org.junit.Test;

/**
 * Low-level Change Data Test - checks :
 * 
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {

   @Test
   public void testChangeRemoval() {
      List<CommitItem> changes = new ArrayList<CommitItem>();
      for (GammaKind kind : GammaKind.values()) {
         changes.add(createChange(kind, 1, NEW, 1, DELETED, -1, null));
         changes.add(createChange(kind, 2, INTRODUCED, 2, ARTIFACT_DELETED, -1, null));
         changes.add(createChange(kind, 3, MODIFIED, 3, MODIFIED, 3, MODIFIED));

         changes.add(createChange(kind, 4, MODIFIED, 4, DELETED, 4, DELETED));
         changes.add(createChange(kind, 5, MODIFIED, 5, DELETED, 5, ARTIFACT_DELETED));
         changes.add(createChange(kind, 6, MODIFIED, 6, ARTIFACT_DELETED, 6, ARTIFACT_DELETED));
         changes.add(createChange(kind, 7, MODIFIED, 7, ARTIFACT_DELETED, 7, DELETED));

         changes.add(createChange(kind, 8, MODIFIED, 8, NEW, 8, NEW));
         changes.add(createChange(kind, 9, MODIFIED, 9, INTRODUCED, 9, INTRODUCED));
         changes.add(createChange(kind, 10, MODIFIED, 10, MERGED, 10, MERGED));

         computeNetChange(changes, IStatus.OK);
         Assert.assertTrue(changes.isEmpty());
      }
   }

   @Test
   public void testMergeChange() {
      CommitItem change = createChange(GammaKind.Artifact, 1, MODIFIED, 1, MODIFIED, 2, MODIFIED);
      change.setNetGammaId(3);
      change.setNetModType(MERGED);

      computeNetChange(Arrays.asList(change), IStatus.OK);

      Assert.assertEquals(3, change.getNetGammaId());
      Assert.assertEquals(MERGED, change.getNetModType());
   }

   @Test
   public void testDestinationMissingItem() {
      CommitItem change = createChange(GammaKind.Artifact, 1, MODIFIED, 1, MODIFIED, -1, null);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);
   }

   @Test
   public void testNewAndIntroduced() {
      List<CommitItem> changes = new ArrayList<CommitItem>();
      for (GammaKind kind : GammaKind.values()) {
         changes.clear();
         changes.add(createChange(kind, 1, NEW, 1, MODIFIED, -1, null));
         changes.add(createChange(kind, 2, NEW, 2, MERGED, -1, null));
         changes.add(createChange(kind, 3, NEW, 3, INTRODUCED, -1, null));
         changes.add(createChange(kind, 4, INTRODUCED, 4, MODIFIED, -1, null));
         changes.add(createChange(kind, 5, INTRODUCED, 5, MERGED, -1, null));
         changes.add(createChange(kind, 6, INTRODUCED, 6, NEW, -1, null));

         computeNetChange(changes, IStatus.OK);

         for (int index = 0; index < changes.size(); index++) {
            CommitItem change = changes.get(index);
            ModificationType base = change.getBaseSourceModType();
            ModificationType expected = null;
            if (base == NEW || base == INTRODUCED) {
               if (change.getCurrentSourceModType() == NEW) {
                  expected = NEW;
               } else {
                  expected = base;
               }
            } else {
               Assert.assertFalse(true);
            }
            Assert.assertEquals("Test: " + change.getItemId(), expected, change.getNetModType());
            Assert.assertEquals("Test: " + change.getItemId(), index + 1, change.getCurrentSourceGammaId());
         }
      }
   }

   @Test
   public void testErrorStates() {
      CommitItem change = createChange(GammaKind.Artifact, 1, NEW, 1, NEW, -1, MODIFIED);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);

      change = createChange(GammaKind.Artifact, 1, INTRODUCED, 1, INTRODUCED, -1, MODIFIED);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);

      //      createChange(kind, 5, null, 25, ModificationType.MODIFIED, 14, ModificationType.DELETED);
      //      createChange(kind, 6, null, 26, ModificationType.DELETED, 15, ModificationType.ARTIFACT_DELETED);
      //      createChange(kind, 7, null, 27, ModificationType.ARTIFACT_DELETED, 16, ModificationType.NEW);
      //      createChange(kind, 4, null, 24, ModificationType.NEW, 13, ModificationType.MODIFIED);

      //      // Commit Responsibility:
      //      // Compute net changes between source branch to destination branch including any merge changes and add those results to a new transaction on the destination branch.
      //       * Handle case where destination branch is missing an artifact that was modified (not new) on the source branch.
      //       * Filter out all items that are both new/introduced and deleted on the source branch
      //       * Filter out all gammas that are already current on the destination branch.
      //       * Apply changes from merge branch
      //       * Compute artifact mod type for commit transaction:
      //       * 1) New and modified -> new
      //       * 2) Introduced and modified -> introduced
   }

   @Test
   public void testAdditionalStates() throws OseeCoreException {
      List<CommitItem> changes = new ArrayList<CommitItem>();
      for (GammaKind kind : GammaKind.values()) {
         changes.clear();
         createChange(kind, 1, null, 21, ModificationType.MODIFIED, 10, ModificationType.MODIFIED);
         createChange(kind, 2, null, 22, ModificationType.DELETED, 11, ModificationType.MODIFIED);
         createChange(kind, 3, null, 23, ModificationType.ARTIFACT_DELETED, 12, ModificationType.MODIFIED);

         createChange(kind, 4, null, 24, ModificationType.INTRODUCED, 13, ModificationType.MODIFIED);

         computeNetChange(changes, IStatus.OK);

         for (int index = 0; index < changes.size(); index++) {
            CommitItem change = changes.get(index);
            Assert.assertEquals("Test: " + change.getItemId(), change.getCurrentSourceModType(), change.getNetModType());
            Assert.assertEquals("Test: " + change.getItemId(), change.getCurrentSourceGammaId(),
                  change.getCurrentSourceGammaId());
         }
      }
   }

   private void computeNetChange(List<CommitItem> changes, int status) {
      IOperation operation = new ComputeNetChangeOperation(changes);
      operation.run(new NullProgressMonitor());
      Assert.assertEquals(status, operation.getStatus().getSeverity());
   }

   private CommitItem createChange(GammaKind kind, int itemId, ModificationType baseSourceMod, long sourceGamma, ModificationType sourceMod, long destGamma, ModificationType destModType) {
      CommitItem change = new CommitItem(sourceGamma, sourceMod);
      change.setItemId(itemId);
      change.setKind(kind);
      change.setBaseSourceModType(baseSourceMod);

      change.setDestinationGammaId(destGamma);
      change.setDestinationModType(destModType);
      return change;
   }
}
