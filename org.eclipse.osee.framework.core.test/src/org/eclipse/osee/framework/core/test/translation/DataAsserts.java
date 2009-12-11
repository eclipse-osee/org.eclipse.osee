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

import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.data.BranchCreationResponse;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
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
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getArtId(), actual.getArtId());
         Assert.assertEquals(expected.getItemId(), actual.getItemId());

         assertEquals(expected.getBaselineVersion(), actual.getBaselineVersion());
         assertEquals(expected.getCurrentVersion(), actual.getCurrentVersion());
         assertEquals(expected.getDestinationVersion(), actual.getDestinationVersion());
         assertEquals(expected.getNetChange(), actual.getNetChange());
         assertEquals(expected.getFirstNonCurrentChange(), actual.getFirstNonCurrentChange());
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
         assertEquals((AbstractOseeType) expected, (AbstractOseeType) actual);
         Assert.assertEquals(expected.getShortName(), actual.getShortName());
         Assert.assertEquals(expected.getAccessControlBranch(), actual.getAccessControlBranch());
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
         Assert.assertFalse(Compare.isDifferent(expected.getItemsIds(), actual.getItemsIds()));
      }
   }

   public static void assertEquals(RelationType expected, RelationType actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals((AbstractOseeType) expected, (AbstractOseeType) actual);
         Assert.assertEquals(expected.getSideAName(), actual.getSideAName());
         Assert.assertEquals(expected.getSideBName(), actual.getSideBName());
         Assert.assertEquals(expected.getMultiplicity(), actual.getMultiplicity());
         Assert.assertEquals(expected.getDefaultOrderTypeGuid(), actual.getDefaultOrderTypeGuid());
         assertEquals(expected.getArtifactTypeSideA(), actual.getArtifactTypeSideA());
         assertEquals(expected.getArtifactTypeSideB(), actual.getArtifactTypeSideB());
      }
   }

   public static void assertEquals(AbstractOseeType expected, AbstractOseeType actual) {
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

   public static void assertEquals(BranchCreationRequest expected, BranchCreationRequest actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getAssociatedArtifactId(), actual.getAssociatedArtifactId());
         Assert.assertEquals(expected.getAuthorId(), actual.getAuthorId());
         Assert.assertEquals(expected.getBranchGuid(), actual.getBranchGuid());
         Assert.assertEquals(expected.getBranchName(), actual.getBranchName());
         Assert.assertEquals(expected.getCreationComment(), actual.getCreationComment());
         Assert.assertEquals(expected.getDestinationBranchId(), actual.getDestinationBranchId());
         Assert.assertEquals(expected.getParentBranchId(), actual.getParentBranchId());
         Assert.assertEquals(expected.getPopulateBaseTxFromAddressingQueryId(),
               actual.getPopulateBaseTxFromAddressingQueryId());
         Assert.assertEquals(expected.getSourceTransactionId(), actual.getSourceTransactionId());
         Assert.assertEquals(expected.getBranchType(), actual.getBranchType());
      }
   }

   public static void assertEquals(BranchCreationResponse expected, BranchCreationResponse actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getBranchId(), actual.getBranchId());
      }
   }

   public static void assertEquals(ChangeVersion expected, ChangeVersion actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.isValid(), actual.isValid());
         Assert.assertEquals(expected.getGammaId(), actual.getGammaId());
         Assert.assertEquals(expected.getModType(), actual.getModType());
         Assert.assertEquals(expected.getTransactionNumber(), actual.getTransactionNumber());
         Assert.assertEquals(expected.getValue(), actual.getValue());
      }
   }

   public static void assertEquals(OseeImportModelRequest expected, OseeImportModelRequest actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getModel(), actual.getModel());
         Assert.assertEquals(expected.getModelName(), actual.getModelName());
         Assert.assertEquals(expected.isCreateCompareReport(), actual.isCreateCompareReport());
         Assert.assertEquals(expected.isCreateTypeChangeReport(), actual.isCreateTypeChangeReport());
         Assert.assertEquals(expected.isPersistAllowed(), actual.isPersistAllowed());
      }
   }

   public static void assertEquals(OseeImportModelResponse expected, OseeImportModelResponse actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getComparisonSnapshotModelName(), actual.getComparisonSnapshotModelName());
         Assert.assertEquals(expected.getComparisonSnapshotModel(), actual.getComparisonSnapshotModel());
         List<TableData> expDatas = expected.getReportData();
         List<TableData> actualData = actual.getReportData();
         Assert.assertEquals(expDatas.size(), actualData.size());
         for (int index = 0; index < expDatas.size(); index++) {
            assertEquals(expDatas.get(index), actualData.get(index));
         }
      }
   }

   public static void assertEquals(PurgeBranchRequest expected, PurgeBranchRequest actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getBranchId(), actual.getBranchId());
      }
   }

   public static void assertEquals(TableData expected, TableData actual) {
      Assert.assertEquals(expected.getTitle(), actual.getTitle());
      Assert.assertFalse(Compare.isDifferent(expected.getColumns(), actual.getColumns()));
      Assert.assertFalse(Compare.isDifferent(expected.getRows(), actual.getRows()));
   }
}
