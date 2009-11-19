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
package org.eclipse.osee.framework.core.test.exchange;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.data.BranchImpl;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public class DataUtility {

   public static TransactionRecord createTx(int id) {
      TransactionDetailsType type = TransactionDetailsType.toEnum(id % TransactionDetailsType.values().length);
      return new TransactionRecord(id, createBranch(id), "comment:" + id, new Date(), id * 3, id * 7, type);
   }

   public static Branch createBranch(int id) {
      BranchState state = BranchState.getBranchState(id % BranchState.values().length);
      BranchType type = BranchType.getBranchType(id % BranchType.values().length);
      return new BranchImpl(GUID.create(), "branch: " + id, type, state, true);
   }

   public static void assertEquals(Branch expected, Branch actual) throws OseeCoreException {
      Assert.assertEquals(expected.getId(), actual.getId());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.getShortName(), actual.getShortName());
      Assert.assertEquals(expected.getAccessControlBranch(), actual.getAccessControlBranch());
      Assert.assertEquals(expected.getAliases(), actual.getAliases());
      Assert.assertEquals(expected.getAncestors(), actual.getAncestors());
      Assert.assertEquals(expected.getArchiveState(), actual.getArchiveState());
      Assert.assertEquals(expected.getAssociatedArtifact(), actual.getAssociatedArtifact());
      Assert.assertEquals(expected.getBaseTransaction(), actual.getBaseTransaction());
      Assert.assertEquals(expected.getSourceTransaction(), actual.getSourceTransaction());
      Assert.assertEquals(expected.getParentBranch(), actual.getParentBranch());
   }

   public static void assertEquals(TransactionRecord expected, TransactionRecord actual) throws OseeCoreException {
      Assert.assertEquals(expected.getId(), actual.getId());
      Assert.assertEquals(expected.getAuthor(), actual.getAuthor());
      Assert.assertEquals(expected.getComment(), actual.getComment());
      Assert.assertEquals(expected.getCommit(), actual.getCommit());
      Assert.assertEquals(expected.getDate(), actual.getDate());
      Assert.assertEquals(expected.getTxType(), actual.getTxType());
      Assert.assertEquals(expected.getBranch(), actual.getBranch());
   }

   public static void assertEquals(IBasicArtifact<?> expected, IBasicArtifact<?> actual) throws OseeCoreException {
      Assert.assertEquals(expected.getArtId(), actual.getArtId());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.getFullArtifact(), actual.getFullArtifact());
   }

   public static void assertEquals(BranchCommitData expected, BranchCommitData actual) {
      Assert.assertEquals(expected.getUser(), actual.getUser());
      Assert.assertEquals(expected.isArchiveAllowed(), actual.isArchiveAllowed());
      Assert.assertEquals(expected.getSourceBranch(), actual.getSourceBranch());
      Assert.assertEquals(expected.getDestinationBranch(), actual.getDestinationBranch());
   }
}
