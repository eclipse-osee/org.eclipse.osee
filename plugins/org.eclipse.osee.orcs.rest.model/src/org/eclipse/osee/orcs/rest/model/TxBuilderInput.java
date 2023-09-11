/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Ryan T. Baldwin
 */
public class TxBuilderInput {

   private final String branch;
   private final String txComment;
   private final List<TxBuilderArtifact> createArtifacts;

   public TxBuilderInput(BranchId branch, List<ArtifactReadable> arts) {
      this.branch = branch.getIdString();
      this.txComment = "Create " + (arts.size() > 0 ? arts.get(0).getArtifactType().getName() + "s" : "artifacts");
      this.createArtifacts = arts.stream().map(art -> new TxBuilderArtifact(art)).collect(Collectors.toList());
   }

   public String getBranch() {
      return branch;
   }

   public String getTxComment() {
      return txComment;
   }

   public List<TxBuilderArtifact> getCreateArtifacts() {
      return createArtifacts;
   }

   private class TxBuilderArtifact {
      private final String typeId;
      private final String name;
      private final List<TxBuilderAttribute> attributes;

      public TxBuilderArtifact(ArtifactReadable art) {
         this.typeId = art.getArtifactType().getIdString();
         this.name = art.getName();
         this.attributes = art.getExistingAttributeTypes().stream().filter(attr -> !attr.getName().equals("Name")).map(
            attr -> new TxBuilderAttribute(attr.getName(), art.getSoleAttributeValue(attr, null))).filter(
               attr -> attr.getValue() != null).collect(Collectors.toList());
      }

      @SuppressWarnings("unused")
      public String getTypeId() {
         return typeId;
      }

      @SuppressWarnings("unused")
      public String getName() {
         return name;
      }

      @SuppressWarnings("unused")
      public List<TxBuilderAttribute> getAttributes() {
         return attributes;
      }

   }

   private class TxBuilderAttribute {
      private final String typeName;
      private final Object value;

      public TxBuilderAttribute(String typeName, Object value) {
         this.typeName = typeName;
         this.value = value;
      }

      @SuppressWarnings("unused")
      public String getTypeName() {
         return typeName;
      }

      public Object getValue() {
         return value;
      }

   }

}
