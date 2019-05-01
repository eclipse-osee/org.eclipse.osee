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
package org.eclipse.osee.framework.core.model.mocks;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {
   private MockDataFactory() {
      // Utility Class
   }

   public static <T> AccessDetail<T> createAccessDetails(T expAccessObject, PermissionEnum expPermission, String expReason, Scope scope) {
      AccessDetail<T> target;
      if (expReason != null) {
         target = new AccessDetail<>(expAccessObject, expPermission, scope, expReason);
      } else {
         target = new AccessDetail<>(expAccessObject, expPermission, scope);
      }
      return target;
   }

   public static TransactionRecord createTransaction(int index, long branchUuid) {
      TransactionDetailsType type =
         TransactionDetailsType.values[Math.abs(index % TransactionDetailsType.values.length)];
      int value = index;
      if (value == 0) {
         value++;
      }
      IOseeBranch branch = IOseeBranch.create(branchUuid, "fake test branch");
      return new TransactionRecord(value * 47L, branch, "comment_" + value, new Date(), DemoUsers.Joe_Smith, value * 42,
         type, 0L);
   }

   public static OseeEnumEntry createEnumEntry(int index) {
      return new OseeEnumEntry("entry_" + index, Math.abs(index * 37), "description");
   }

   public static OseeEnumType createEnumType(Long id, int index) {
      return new OseeEnumType(id, "enum_" + index);
   }

   public static AttributeType createAttributeType(int index, OseeEnumType oseeEnumType) {
      return createAttributeType(index, oseeEnumType, ThreadLocalRandom.current().nextLong());
   }

   public static AttributeType createAttributeType(int index, OseeEnumType oseeEnumType, Long id) {
      AttributeType type =
         new AttributeType(id, "attrType_" + index, "baseClass_" + index, "providerId_" + index, "ext_" + index,
            "default_" + index, index * 2, index * 7, "description_" + index, "tag_" + index, "mediaType_" + index);
      type.setOseeEnumType(oseeEnumType);
      return type;
   }

   public static ArtifactType createArtifactType(int index) {
      return createArtifactType(index, ThreadLocalRandom.current().nextLong());
   }

   public static ArtifactType createArtifactType(int index, Long id) {
      return new ArtifactType(id, "art_" + index, index % 2 == 0);
   }

   public static RelationType createRelationType(int index, ArtifactTypeToken artTypeA, ArtifactTypeToken artTypeB, Long id) {
      RelationTypeMultiplicity multiplicity =
         RelationTypeMultiplicity.values()[Math.abs(index % RelationTypeMultiplicity.values().length)];
      RelationSorter order = RelationSorter.values()[index % RelationTypeMultiplicity.values().length];
      return new RelationType(id, "relType_" + index, "sideA_" + index, "sideB_" + index, artTypeA, artTypeB,
         multiplicity, order);
   }
}