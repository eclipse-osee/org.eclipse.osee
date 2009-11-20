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
package org.eclipse.osee.framework.core.test.mocks;

import java.util.Date;
import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {

   private MockDataFactory() {
   }

   public static IBasicArtifact<?> createArtifact(int index) {
      return new DefaultBasicArtifact(index * 37, GUID.create(), "user_" + index);
   }

   public static Branch createBranch(int index) {
      BranchState branchState = BranchState.values()[Math.abs(index % BranchState.values().length)];
      BranchType branchType = BranchType.values()[Math.abs(index % BranchType.values().length)];
      boolean isArchived = index % 2 == 0 ? true : false;
      return new Branch(GUID.create(), "branch_" + index, branchType, branchState, isArchived);
   }

   public static TransactionRecord createTransaction(int index, Branch branch) {
      TransactionDetailsType type =
            TransactionDetailsType.values()[Math.abs(index % TransactionDetailsType.values().length)];
      return new TransactionRecord(index * 47, branch, "comment_" + index, new Date(), index * 37, index * 42, type);
   }

   public static OseeEnumEntry createEnumEntry(int index) {
      return new OseeEnumEntry(GUID.create(), "entry_" + index, index * 37);
   }

   public static OseeEnumType createEnumType(int index) {
      return new OseeEnumType(GUID.create(), "enum_" + index);
   }

   public static AttributeType createAttributeType(int index, OseeEnumType oseeEnumType) {
      return new AttributeType(GUID.create(), "attrType_" + index, "baseClass_" + index, "providerId_" + index,
            "ext_" + index, "default_" + index, oseeEnumType, index * 2, index * 7, "description_" + index,
            "tag_" + index);
   }

   public static ArtifactType createArtifactType(int index) {
      return new ArtifactType(GUID.create(), "art_" + index, index % 2 == 0);
   }

   public static RelationType createRelationType(int index, ArtifactType artTypeA, ArtifactType artTypeB) {
      RelationTypeMultiplicity multiplicity =
            RelationTypeMultiplicity.values()[Math.abs(index % RelationTypeMultiplicity.values().length)];
      String order = RelationOrderBaseTypes.values()[index % RelationTypeMultiplicity.values().length].getGuid();
      return new RelationType(GUID.create(), "relType_" + index, "sideA_" + index, "sideB_" + index, artTypeA,
            artTypeB, multiplicity, order);
   }
}
