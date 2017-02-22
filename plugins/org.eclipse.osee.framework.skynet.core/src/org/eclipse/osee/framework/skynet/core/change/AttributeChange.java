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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Jeff C. Phillips
 */
public final class AttributeChange extends Change {
   private final static LoadChangeType changeType = LoadChangeType.attribute;

   private final String isValue;
   private final String wasValue;
   private final int attrId;
   private final AttributeType attributeType;
   private final ModificationType artModType;

   public AttributeChange(BranchId branch, long sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, String isValue, String wasValue, int attrId, AttributeType attributeType, ModificationType artModType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      super(branch, sourceGamma, artId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attributeType = attributeType;
      this.artModType = artModType;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeChange) {
         AttributeChange change = (AttributeChange) obj;
         return super.equals(obj) && //
            change.getArtId() == getArtId() && //
            change.getArtModType() == getArtModType() && //
            change.getAttrId() == getAttrId();
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 7 * super.hashCode();
      hashCode += getAttributeType() != null ? 7 * getAttributeType().hashCode() : 0;
      hashCode += getArtModType() != null ? 7 * getArtModType().hashCode() : 0;
      hashCode += 7 * getAttrId();
      return hashCode;
   }

   public int getAttrId() {
      return attrId;
   }

   public AttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getItemTypeName() {
      return getAttributeType().getName();
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

   public Attribute<?> getAttribute() throws OseeCoreException {
      List<Attribute<?>> attributes = getChangeArtifact().getAttributes(true);
      for (Attribute<?> attribute : attributes) {
         if (attribute.getId() == attrId) {
            return attribute;
         }
      }
      throw new AttributeDoesNotExist("Attribute %d could not be found on artifact %d on branch %d", attrId, getArtId(),
         getBranch().getGuid());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      T toReturn = super.getAdapter(type);
      if (toReturn == null) {
         try {
            Attribute<?> attr = getAttribute();
            if (type.isInstance(attr)) {
               toReturn = (T) attr;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return toReturn;
   }

   public ModificationType getArtModType() {
      return artModType;
   }

   @Override
   public long getItemTypeId() {
      return getAttributeType().getId();
   }

   @Override
   public int getItemId() {
      return attrId;
   }

   @Override
   public LoadChangeType getChangeType() {
      return changeType;
   }
}