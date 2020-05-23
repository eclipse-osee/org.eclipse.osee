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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Donald G. Dunne
 */
public final class AttributeRow {

   private final BranchId branch;
   private final GammaId gammaId;
   private final Integer artId;
   private final ModificationType modType;
   private final String value;
   private final AttributeId attrId;
   private final AttributeTypeId attributeType;

   public AttributeRow(BranchId branch, GammaId gammaId, Integer artId, ModificationType modType, String value, AttributeId attrId, AttributeTypeId attributeType) {
      this.branch = branch;
      this.gammaId = gammaId;
      this.artId = artId;
      this.modType = modType;
      this.value = value;
      this.attrId = attrId;
      this.attributeType = attributeType;
   }

   public BranchId getBranch() {
      return branch;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public Integer getArtId() {
      return artId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public String getValue() {
      return value;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public AttributeTypeId getAttributeType() {
      return attributeType;
   }

   @Override
   public String toString() {
      return "Attribute [attrId=" + attrId + ", type=" + attributeType + ", gammaId=" + gammaId + ", artId=" + artId + ", modType=" + modType + ", value=" + value + "]";
   }

}