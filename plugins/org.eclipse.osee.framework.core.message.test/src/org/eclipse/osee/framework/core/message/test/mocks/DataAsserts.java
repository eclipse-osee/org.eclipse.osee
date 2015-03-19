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
package org.eclipse.osee.framework.core.message.test.mocks;

import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.TableData;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TypeUtil;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class DataAsserts {

   private DataAsserts() {
      // Utility Class
   }

   public static void assertEquals(ChangeItem expected, ChangeItem actual) {
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

   public static void assertEquals(AttributeType expected, AttributeType actual) {
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

   public static void assertEquals(Branch expected, Branch actual) throws OseeCoreException {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals((AbstractOseeType<Long>) expected, (AbstractOseeType<Long>) actual);
         Assert.assertEquals(expected.getShortName(), actual.getShortName());
         Assert.assertEquals(expected.getAccessControlBranch(), actual.getAccessControlBranch());
         Assert.assertEquals(expected.getAncestors(), actual.getAncestors());
         Assert.assertEquals(expected.getArchiveState(), actual.getArchiveState());
         Assert.assertEquals(expected.getAssociatedArtifactId(), actual.getAssociatedArtifactId());
         Assert.assertEquals(expected.getBaseTransaction(), actual.getBaseTransaction());
         Assert.assertEquals(expected.getSourceTransaction(), actual.getSourceTransaction());
         Assert.assertEquals(expected.getParentBranch(), actual.getParentBranch());
      }
   }

   public static void assertEquals(TransactionRecord expected, TransactionRecord actual) {
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

   public static void assertEquals(RelationType expected, RelationType actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         assertEquals((AbstractOseeType<Long>) expected, (AbstractOseeType<Long>) actual);
         Assert.assertEquals(expected.getSideAName(), actual.getSideAName());
         Assert.assertEquals(expected.getSideBName(), actual.getSideBName());
         Assert.assertEquals(expected.getMultiplicity(), actual.getMultiplicity());
         Assert.assertEquals(expected.getDefaultOrderTypeGuid(), actual.getDefaultOrderTypeGuid());
         Assert.assertEquals(expected.getArtifactTypeSideA(), actual.getArtifactTypeSideA());
         Assert.assertEquals(expected.getArtifactTypeSideB(), actual.getArtifactTypeSideB());
      }
   }

   public static void assertEquals(AbstractOseeType<?> expected, AbstractOseeType<?> actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(TypeUtil.getId(expected), TypeUtil.getId(actual));
         Assert.assertEquals(expected.getGuid(), actual.getGuid());
         Assert.assertEquals(expected.getName(), actual.getName());
         Assert.assertEquals(expected.getStorageState(), actual.getStorageState());
         Assert.assertEquals(expected.isDirty(), actual.isDirty());
      }
   }

   public static void assertEquals(ChangeVersion expected, ChangeVersion actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.isValid(), actual.isValid());
         Assert.assertEquals(expected.getGammaId(), actual.getGammaId());
         Assert.assertEquals(expected.getModType(), actual.getModType());
         Assert.assertEquals(expected.getValue(), actual.getValue());
      }
   }

   public static void assertEquals(TableData expected, TableData actual) {
      Assert.assertEquals(expected.getTitle(), actual.getTitle());
      Assert.assertFalse(Compare.isDifferent(expected.getColumns(), actual.getColumns()));
      Assert.assertFalse(Compare.isDifferent(expected.getRows(), actual.getRows()));
   }

   public static void assertEquals(DatastoreInitRequest expected, DatastoreInitRequest actual) {
      Assert.assertEquals(expected.getIndexDataSpace(), actual.getIndexDataSpace());
      Assert.assertEquals(expected.getTableDataSpace(), actual.getTableDataSpace());
      Assert.assertEquals(expected.isUseFileSpecifiedSchemas(), actual.isUseFileSpecifiedSchemas());
   }

}
