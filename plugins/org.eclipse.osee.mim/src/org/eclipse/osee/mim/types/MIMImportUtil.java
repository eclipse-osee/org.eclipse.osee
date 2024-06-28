/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.rest.model.transaction.AddRelation;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;

public class MIMImportUtil {

   public static TransactionBuilderData getTxBuilderDataFromImportSummary(BranchId branch, ApplicabilityId applicId,
      MimImportSummary summary) {
      TransactionBuilderData data = new TransactionBuilderData();
      data.setBranch(branch.getIdString());
      data.setTxComment(summary.getTxComment());
      data.setCreateArtifacts(new LinkedList<>());
      data.setAddRelations(new LinkedList<>());

      // TODO create the rest of the MIM artifacts here

      for (InterfaceElementImportToken element : summary.getElements()) {
         data.getCreateArtifacts().add(element.createArtifact(element.getIdString(), applicId));
      }
      for (PlatformTypeImportToken pType : summary.getPlatformTypes()) {
         data.getCreateArtifacts().add(pType.createArtifact(pType.getIdString(), applicId));
      }
      for (InterfaceEnumerationSet enumSet : summary.getEnumSets()) {
         data.getCreateArtifacts().add(enumSet.createArtifact(enumSet.getIdString(), applicId));
      }
      for (InterfaceEnumeration enumeration : summary.getEnums()) {
         data.getCreateArtifacts().add(enumeration.createArtifact(enumeration.getIdString(), applicId));
      }

      // TODO create the rest of the MIM relations here

      for (String structId : summary.getStructureElementRelations().keySet()) {
         for (String elementId : summary.getStructureElementRelations().get(structId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceStructureContent, structId, elementId));
         }
      }

      for (String elementId : summary.getElementPlatformTypeRelations().keySet()) {
         for (String pTypeId : summary.getElementPlatformTypeRelations().get(elementId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceElementPlatformType, elementId, pTypeId));
         }
      }
      for (String pTypeId : summary.getPlatformTypeEnumSetRelations().keySet()) {
         for (String enumSetId : summary.getPlatformTypeEnumSetRelations().get(pTypeId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfacePlatformTypeEnumeration, pTypeId, enumSetId));
         }
      }
      for (String enumSetId : summary.getEnumSetEnumRelations().keySet()) {
         for (String enumId : summary.getEnumSetEnumRelations().get(enumSetId)) {
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.InterfaceEnumeration, enumSetId, enumId));
         }
      }

      return data;
   }

   private static AddRelation createAddRelation(RelationTypeToken relType, String artAId, String artBId) {
      AddRelation rel = new AddRelation();
      rel.setTypeId(relType.getIdString());
      rel.setaArtId(artAId);
      rel.setbArtId(artBId);
      return rel;
   }
}
