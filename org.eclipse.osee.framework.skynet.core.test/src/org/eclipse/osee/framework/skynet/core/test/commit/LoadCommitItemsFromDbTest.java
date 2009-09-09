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
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.LoadChangeDataOperation;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem.GammaKind;
import org.junit.Test;

/**
 * Checks commit load operation this test is dependent on PopulateDemo :
 * 
 * @author Roberto E. Escobar
 */
public class LoadCommitItemsFromDbTest {

   @Test
   public void checkLoad() throws OseeCoreException {
      Branch sourceBranch = BranchManager.getBranch(1001);
      Branch destinationBranch = BranchManager.getBranch(2567);
      Branch mergeBranch = BranchManager.getMergeBranch(sourceBranch, destinationBranch);

      Assert.assertNotNull(sourceBranch);
      Assert.assertNotNull(destinationBranch);
      Assert.assertNotNull(mergeBranch);

      List<CommitItem> changeData = new ArrayList<CommitItem>();
      long startTime = System.currentTimeMillis();
      IOperation operation = new LoadChangeDataOperation(sourceBranch, destinationBranch, mergeBranch, changeData);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      Assert.assertEquals(IStatus.OK, operation.getStatus().getSeverity());

      System.out.println("Time to load: " + Lib.getElapseString(startTime));

      System.out.println("Size after load: " + changeData.size());
      Assert.assertEquals(47482, changeData.size());

      boolean wasFound = false;
      for (CommitItem item : changeData) {
         if (item.getItemId() == 428968) {
            if (item.getKind() == GammaKind.Artifact) {
               wasFound = true;
            }
         }
      }
      Assert.assertTrue(wasFound);

      startTime = System.currentTimeMillis();
      operation = new ComputeNetChangeOperation(changeData);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      Assert.assertEquals(IStatus.OK, operation.getStatus().getSeverity());
      System.out.println("Time to compute changes: " + Lib.getElapseString(startTime));

      System.out.println("Size after net changes: " + changeData.size());
      Assert.assertEquals(363, changeData.size());
      wasFound = false;
      for (CommitItem item : changeData) {
         System.out.println(item);
         //         if (item.getItemId() == 428968) {
         //            if (item.getKind() == GammaKind.Artifact) {
         //               wasFound = true;
         //            }
         //         }
      }
      //      Assert.assertFalse("Item Should not be found", wasFound);
   }
}
