/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.Comparator;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public final class AttributeRow extends TransactionRow {

   private final ArtifactId artId;
   private final String value;
   private final AttributeId attrId;
   private final AttributeTypeToken attributeType;
   private String uri = "";

   public AttributeRow(BranchId branch, GammaId gammaId, ArtifactId artId, ModificationType modType, String value, AttributeId attrId, AttributeTypeToken attributeType) {
      this.branch = branch;
      this.gammaId = gammaId;
      this.artId = artId;
      this.modType = modType;
      this.value = value;
      this.attrId = attrId;
      this.attributeType = attributeType;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public String getValue() {
      return value;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public String toString() {
      return "Attribute [attrId=" + attrId + ", type=" + attributeType + ", gammaId=" + gammaId + ", artId=" + artId + ", modType=" + modType + ", value=" + value + "]";
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   @Override
   public Id getItemId() {
      return getAttrId();
   }

   public static final class AttributeRowComparator implements Comparator<AttributeRow> {
      private static final Comparator<AttributeRow> COMPARATOR = Comparator.comparing( //
         (AttributeRow r) -> r.getAttributeType().getId()) //
         .thenComparing(r -> r.getAttrId().getId()) //
         .thenComparing(r -> r.getTx().getId()) //
         .thenComparing(r -> r.getGammaId().getId()) //
         .thenComparing(r -> r.getModType().getId());

      @Override
      public int compare(AttributeRow a, AttributeRow b) {
         return COMPARATOR.compare(a, b);
      }
   }

}
