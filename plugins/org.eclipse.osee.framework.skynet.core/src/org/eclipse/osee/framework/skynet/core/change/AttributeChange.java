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

package org.eclipse.osee.framework.skynet.core.change;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public final class AttributeChange extends Change {
   private final String isValue;
   private String wasValue;
   private final int attrId;
   private final int attrTypeId;
   private AttributeType dynamicAttributeDescriptor;
   private final ModificationType artModType;

   public AttributeChange(IOseeBranch branch, ArtifactType artType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, String isValue, String wasValue, int attrId, int attrTypeId, ModificationType artModType, boolean isHistorical, Artifact toArtifact, Artifact fromArtifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super(branch, artType, sourceGamma, artId, txDelta, modType, isHistorical, toArtifact, fromArtifact);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.artModType = artModType;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeChange) {
         AttributeChange change = (AttributeChange) obj;
         return super.equals(obj) &&
         //
         change.getArtId() == attrTypeId &&
         //
         change.getArtModType() == artModType &&
         //
         change.getAttrId() == attrId;
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 7 * super.hashCode();
      hashCode += 7 * attrTypeId;
      hashCode += artModType != null ? 7 * artModType.hashCode() : 0;
      hashCode += 7 * attrId;
      return hashCode;
   }

   public int getAttrId() {
      return attrId;
   }

   public int getTypeId() {
      return attrTypeId;
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getItemTypeName() throws OseeCoreException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = AttributeTypeManager.getType(attrTypeId);
      }
      return dynamicAttributeDescriptor.getName();
   }

   @Override
   public String getItemKind() {
      return "Attribute";
   }

   @Override
   public String getIsValue() {
      return isValue != null ? isValue : "";
   }

   @Override
   public String getWasValue() {
      return wasValue;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   public Attribute<?> getAttribute() throws OseeCoreException {
      for (Attribute<?> attribute : getToArtifact().getAllAttributesIncludingHardDeleted()) {
         if (attribute.getId() == attrId) {
            return attribute;
         }
      }
      throw new AttributeDoesNotExist(String.format("Could not find Attribute %d on Artifact %d", attrId, getArtId()));
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      try {
         if (adapter.isInstance(getToArtifact())) {
            return getToArtifact();
         } else if (adapter.isInstance(getTxDelta().getEndTx()) && isHistorical()) {
            return getTxDelta().getEndTx();
         } else if (adapter.isInstance(this)) {
            return this;
         }
         try {
            if (adapter.isInstance(getAttribute())) {
               return getAttribute();
            }
         } catch (AttributeDoesNotExist ex) {
            return null;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public ModificationType getArtModType() {
      return artModType;
   }

   @Override
   public int getItemTypeId() {
      return attrTypeId;
   }

   @Override
   public int getItemId() {
      return attrId;
   }
}