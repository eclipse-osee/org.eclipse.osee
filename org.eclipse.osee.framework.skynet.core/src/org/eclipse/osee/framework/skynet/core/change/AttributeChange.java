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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
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

   public AttributeChange(Branch branch, ArtifactType artType, int sourceGamma, int artId, TransactionRecord toTransactionId, TransactionRecord fromTransactionId, ModificationType modType, ChangeType changeType, String isValue, String wasValue, int attrId, int attrTypeId, ModificationType artModType, boolean isHistorical, Artifact artifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super(branch, artType, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical, artifact);
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
      return super.hashCode() + attrTypeId + artModType.hashCode() + attrId;
   }

   public int getAttrId() {
      return attrId;
   }

   public int getTypeId() {
      return attrTypeId;
   }

   public AttributeType getDynamicAttributeDescriptor() throws Exception {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = AttributeTypeManager.getType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }

   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactName();
   }

   @Override
   public String getItemTypeName() throws Exception {
      return getDynamicAttributeDescriptor().getName();
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
      for (Attribute<?> attribute : getArtifact().getAllAttributesIncludingHardDeleted()) {
         if (attribute.getAttrId() == attrId) {
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
         if (adapter.isInstance(getArtifact())) {
            return getArtifact();
         } else if (adapter.isInstance(getToTransactionId()) && isHistorical()) {
            return getToTransactionId();
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