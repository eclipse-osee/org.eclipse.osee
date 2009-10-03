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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.commit.ArtifactChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.AttributeChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.LoadChangeDataOperation;
import org.eclipse.osee.framework.skynet.core.commit.RelationChangeItem;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.Test;

/**
 * Checks commit load operation this test is dependent on PopulateDemo :
 * 
 * @author Roberto E. Escobar
 */
public class LoadCommitItemsFromDbTest {

   @Test
   public void testCommitItemsNonConflicting() throws OseeCoreException {
      final String branchName =
            "SAW (uncommitted) More Reqt Changes for Diagram View - SAW (uncommitted) More Reqt Changes for...";
      Branch destination = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2.name());
      Branch source = findASourceBranch(destination, branchName);

      Assert.assertNotNull(source);
      Assert.assertNotNull(destination);

      List<ChangeItem> items = new ArrayList<ChangeItem>();

      processItems(items, source, destination, null, 41, 41);

      checkNetItems(items, ModificationType.NEW, 2, 18, 4);
      checkNetItems(items, ModificationType.INTRODUCED, 0, 0, 0);
      checkNetItems(items, ModificationType.MODIFIED, 2, 4, 0);
      checkNetItems(items, ModificationType.MERGED, 0, 0, 0);
      checkNetItems(items, ModificationType.ARTIFACT_DELETED, 0, 9, 1);
      checkNetItems(items, ModificationType.DELETED, 1, 0, 0);
   }

   @Test
   public void testCommitItemsConflicting() throws OseeCoreException {
      final String branchName =
            "SAW (uncommitted-conflicted) More Requirement Changes for Diagram View - SAW (uncommitted-conflicted) More Requi...";
      Branch destination = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2.name());
      Branch source = findASourceBranch(destination, branchName);

      Assert.assertNotNull(source);
      Assert.assertNotNull(destination);

      List<ChangeItem> items = new ArrayList<ChangeItem>();

      processItems(items, source, destination, null, 5, 5);

      checkNetItems(items, ModificationType.NEW, 0, 1, 1);
      checkNetItems(items, ModificationType.INTRODUCED, 0, 0, 0);
      checkNetItems(items, ModificationType.MODIFIED, 1, 2, 0);
      checkNetItems(items, ModificationType.MERGED, 0, 0, 0);
      checkNetItems(items, ModificationType.ARTIFACT_DELETED, 0, 0, 0);
      checkNetItems(items, ModificationType.DELETED, 0, 0, 0);
   }

   private Branch findASourceBranch(Branch destination, String branchName) throws OseeCoreException {
      Branch source = null;
      for (Branch branch : destination.getWorkingBranches()) {
         if (branch.getName().contains(branchName)) {
            source = branch;
         }
      }
      return source;
   }

   private void processItems(Collection<ChangeItem> items, Branch source, Branch destination, Branch mergeBranch, int loadedItems, int netItems) {
      IOperation operation = new LoadChangeDataOperation(source, destination, mergeBranch, items);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      Assert.assertEquals(IStatus.OK, operation.getStatus().getSeverity());
      Assert.assertEquals(loadedItems, items.size());

      operation = new ComputeNetChangeOperation(items);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      Assert.assertEquals(Lib.exceptionToString(operation.getStatus().getException()), IStatus.OK,
            operation.getStatus().getSeverity());
      Assert.assertEquals(netItems, items.size());
   }

   private void checkNetItems(Collection<ChangeItem> items, ModificationType modType, int artifacts, int attributes, int relations) {
      Assert.assertEquals(artifacts, getCount(items, ArtifactChangeItem.class, modType));
      Assert.assertEquals(attributes, getCount(items, AttributeChangeItem.class, modType));
      Assert.assertEquals(relations, getCount(items, RelationChangeItem.class, modType));
   }

   private int getCount(Collection<ChangeItem> items, Class<? extends ChangeItem> changeType, ModificationType type) {
      int count = 0;
      for (ChangeItem item : items) {
         if (item.getClass().isAssignableFrom(changeType) && item.getNet().getModType() == type) {
            count++;
         }
      }
      return count;
   }
}
