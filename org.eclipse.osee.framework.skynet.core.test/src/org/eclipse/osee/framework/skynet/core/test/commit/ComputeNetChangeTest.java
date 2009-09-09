/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.framework.skynet.core.commit.CommitItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
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

         changes.add(createChange(kind, 11, null, 20, NEW, 20, NEW));
         changes.add(createChange(kind, 12, NEW, 21, NEW, 21, NEW));

         changes.add(createChange(kind, 428968, null, 7693330, INTRODUCED, 7693330, NEW));

         computeNetChange(changes, IStatus.OK);
         Assert.assertTrue(changes.isEmpty());
      }
   }

   @Test
   public void testMergeChange() {
      CommitItem change = createChange(GammaKind.Artifact, 1, MODIFIED, 1, MODIFIED, 2, MODIFIED);
      change.getNet().setGammaId(3L);
      change.getNet().setModType(MERGED);

      computeNetChange(Arrays.asList(change), IStatus.OK);

      Assert.assertEquals(3L, (long) change.getNet().getGammaId());
      Assert.assertEquals(MERGED, change.getNet().getModType());
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
            ModificationType base = change.getBase().getModType();
            ModificationType expected = null;
            if (base == NEW || base == INTRODUCED) {
               if (change.getCurrent().getModType() == NEW) {
                  expected = NEW;
               } else {
                  expected = base;
               }
            } else {
               Assert.assertFalse(true);
            }
            Assert.assertEquals("Test: " + change.getItemId(), expected, change.getNet().getModType());
            Assert.assertEquals("Test: " + change.getItemId(), index + 1L, (long) change.getCurrent().getGammaId());
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
            Assert.assertEquals("Test: " + change.getItemId(), change.getCurrent().getModType(),
                  change.getNet().getModType());
            Assert.assertEquals("Test: " + change.getItemId(), change.getCurrent().getGammaId(),
                  change.getCurrent().getGammaId());
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
      change.getBase().setModType(baseSourceMod);

      change.getDestination().setGammaId(destGamma);
      change.getDestination().setModType(destModType);
      return change;
   }
}
