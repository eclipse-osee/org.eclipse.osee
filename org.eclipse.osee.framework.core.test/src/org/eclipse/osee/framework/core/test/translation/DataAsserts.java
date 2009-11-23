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
package org.eclipse.osee.framework.core.test.translation;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Compare;

/**
 * @author Roberto E. Escobar
 */
public final class DataAsserts {

   private DataAsserts() {
   }

   public static void assertEquals(OseeEnumType expected, OseeEnumType actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getId(), actual.getId());
         Assert.assertEquals(expected.getGuid(), actual.getGuid());
         Assert.assertEquals(expected.getName(), actual.getName());
         OseeEnumEntry[] expList = expected.values();
         OseeEnumEntry[] actualList = actual.values();
         Assert.assertEquals(expList.length, actualList.length);
         for (int index = 0; index < expList.length; index++) {
            assertEquals(expList[index], actualList[index]);
         }
      }
   }

   public static void assertEquals(OseeEnumEntry expected, OseeEnumEntry actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getId(), actual.getId());
         Assert.assertEquals(expected.getGuid(), actual.getGuid());
         Assert.assertEquals(expected.getName(), actual.getName());
         Assert.assertEquals(expected.ordinal(), actual.ordinal());
      }
   }

   public static void assertEquals(Branch expected, Branch actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
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
   }

   public static void assertEquals(TransactionRecord expected, TransactionRecord actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getId(), actual.getId());
         Assert.assertEquals(expected.getAuthor(), actual.getAuthor());
         Assert.assertEquals(expected.getComment(), actual.getComment());
         Assert.assertEquals(expected.getCommit(), actual.getCommit());
         Assert.assertEquals(expected.getTimeStamp(), actual.getTimeStamp());
         Assert.assertEquals(expected.getTxType(), actual.getTxType());
         Assert.assertEquals(expected.getBranch(), actual.getBranch());
      }
   }

   public static void assertEquals(IBasicArtifact<?> expected, IBasicArtifact<?> actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getArtId(), actual.getArtId());
         Assert.assertEquals(expected.getGuid(), actual.getGuid());
         Assert.assertEquals(expected.getName(), actual.getName());
         Assert.assertEquals(expected.getFullArtifact(), actual.getFullArtifact());
      }
   }

   public static void assertEquals(BranchCommitRequest expected, BranchCommitRequest actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals(expected.getUser(), actual.getUser());
         Assert.assertEquals(expected.isArchiveAllowed(), actual.isArchiveAllowed());
         assertEquals(expected.getSourceBranch(), actual.getSourceBranch());
         assertEquals(expected.getDestinationBranch(), actual.getDestinationBranch());
      }
   }

   public static void assertEquals(BranchCommitResponse expected, BranchCommitResponse actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals(expected.getTransaction(), actual.getTransaction());
      }
   }

   public static void assertEquals(CacheUpdateRequest expected, CacheUpdateRequest actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getCacheId(), actual.getCacheId());
         Assert.assertFalse(Compare.isDifferent(expected.getGuids(), actual.getGuids()));
      }
   }
}
