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
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IOseeStorableType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Compare;

/**
 * @author Roberto E. Escobar
 */
public final class DataAsserts {

   private DataAsserts() {
   }

   public static void assertEquals(ChangeItem expected, ChangeItem actual) throws OseeCoreException {
      Assert.assertEquals(expected.getArtId(), actual.getArtId());
      Assert.assertEquals(expected.getItemId(), actual.getItemId());

      checkChangeVersion(expected.getBaselineVersion(), actual.getBaselineVersion());
      checkChangeVersion(expected.getCurrentVersion(), actual.getCurrentVersion());
      checkChangeVersion(expected.getDestinationVersion(), actual.getDestinationVersion());
      checkChangeVersion(expected.getNetChange(), actual.getNetChange());
      checkChangeVersion(expected.getFirstNonCurrentChange(), actual.getFirstNonCurrentChange());
   }

   private static void checkChangeVersion(ChangeVersion expected, ChangeVersion actual) {
      if (actual.isValid()) {
         Assert.assertEquals(expected.getGammaId(), actual.getGammaId());
         Assert.assertEquals(expected.getTransactionNumber(), actual.getTransactionNumber());
         Assert.assertEquals(expected.getModType(), actual.getModType());
      }
   }
   
   public static void assertEquals(AttributeType expected, AttributeType actual) throws OseeCoreException {
      Assert.assertEquals(expected.getAttributeProviderId(), actual.getAttributeProviderId());
      Assert.assertEquals(expected.getBaseAttributeTypeId(), actual.getBaseAttributeTypeId());
      Assert.assertEquals(expected.getDefaultValue(), actual.getDefaultValue());
      Assert.assertEquals(expected.getDescription(), actual.getDescription());
      Assert.assertEquals(expected.getFileTypeExtension(), actual.getFileTypeExtension());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
      Assert.assertEquals(expected.getId(), actual.getId());
      Assert.assertEquals(expected.getMaxOccurrences(), actual.getMaxOccurrences());
      Assert.assertEquals(expected.getMinOccurrences(), actual.getMinOccurrences());
      Assert.assertEquals(expected.getTaggerId(), actual.getTaggerId());
      Assert.assertEquals(expected.getOseeEnumType(), actual.getOseeEnumType());
   }

   public static void assertEquals(ArtifactType expected, ArtifactType actual) throws OseeCoreException {
      Assert.assertEquals(expected.getId(), actual.getId());
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.isAbstract(), actual.isAbstract());
   }

   public static void assertEquals(Branch expected, Branch actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals((IOseeStorableType) expected, (IOseeStorableType) actual);
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
         Assert.assertEquals(expected.getBranchId(), actual.getBranchId());
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
         Assert.assertEquals(expected.getUserArtId(), actual.getUserArtId());
         Assert.assertEquals(expected.isArchiveAllowed(), actual.isArchiveAllowed());
         Assert.assertEquals(expected.getSourceBranchId(), actual.getSourceBranchId());
         Assert.assertEquals(expected.getDestinationBranchId(), actual.getDestinationBranchId());
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

   public static void assertEquals(RelationType expected, RelationType actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals((IOseeStorableType) expected, (IOseeStorableType) actual);
         Assert.assertEquals(expected.getSideAName(), actual.getSideAName());
         Assert.assertEquals(expected.getSideBName(), actual.getSideBName());
         Assert.assertEquals(expected.getMultiplicity(), actual.getMultiplicity());
         Assert.assertEquals(expected.getDefaultOrderTypeGuid(), actual.getDefaultOrderTypeGuid());
         assertEquals(expected.getArtifactTypeSideA(), actual.getArtifactTypeSideA());
         assertEquals(expected.getArtifactTypeSideB(), actual.getArtifactTypeSideB());
      }
   }

   public static void assertEquals(IOseeStorableType expected, IOseeStorableType actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getId(), actual.getId());
         Assert.assertEquals(expected.getGuid(), actual.getGuid());
         Assert.assertEquals(expected.getName(), actual.getName());
         Assert.assertEquals(expected.getModificationType(), actual.getModificationType());
         Assert.assertEquals(expected.isDirty(), actual.isDirty());
      }
   }
}
