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

import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Roberto E. Escobar
 */
public final class DataAsserts {

   private DataAsserts() {
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
      Assert.assertEquals(expected.getTimeStamp(), actual.getTimeStamp());
      Assert.assertEquals(expected.getTxType(), actual.getTxType());
      Assert.assertEquals(expected.getBranch(), actual.getBranch());
   }

   public static void assertEquals(IBasicArtifact<?> expected, IBasicArtifact<?> actual) throws OseeCoreException {
      Assert.assertEquals(expected.getArtId(), actual.getArtId());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.getFullArtifact(), actual.getFullArtifact());
   }

   public static void assertEquals(BranchCommitRequest expected, BranchCommitRequest actual) {
      Assert.assertEquals(expected.getUser(), actual.getUser());
      Assert.assertEquals(expected.isArchiveAllowed(), actual.isArchiveAllowed());
      Assert.assertEquals(expected.getSourceBranch(), actual.getSourceBranch());
      Assert.assertEquals(expected.getDestinationBranch(), actual.getDestinationBranch());
   }
}
