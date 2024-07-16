/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.change;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public final class AttributeChange extends Change {
   private final static ChangeType changeType = ChangeType.Attribute;

   private String isValue;

   private final String isUri;
   private String wasValue;
   private final String wasUri;
   private final AttributeId attrId;
   private final AttributeTypeToken attributeType;
   private final ModificationType artModType;
   private final ArtifactWasIsLazyProvider wasIsProvider;

   public AttributeChange(BranchToken branch, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, String isValue, String isUri, String wasValue, String wasUri, AttributeId attrId, AttributeTypeToken attributeType, ModificationType artModType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      this(branch, sourceGamma, artId, txDelta, modType, isValue, isUri, wasValue, wasUri, null, attrId, attributeType,
         artModType, isHistorical, changeArtifact, artifactDelta);
   }

   public AttributeChange(BranchToken branch, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, ArtifactWasIsLazyProvider wasIsProvider, AttributeId attrId, AttributeTypeToken attributeType, ModificationType artModType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      this(branch, sourceGamma, artId, txDelta, modType, null, null, null, null, wasIsProvider, attrId, attributeType,
         artModType, isHistorical, changeArtifact, artifactDelta);
   }

   public AttributeChange(BranchToken branch, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, String isValue, String isUri, String wasValue, String wasUri, ArtifactWasIsLazyProvider wasIsProvider, AttributeId attrId, AttributeTypeToken attributeType, ModificationType artModType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      super(branch, sourceGamma, artId, txDelta, modType, isHistorical, changeArtifact, artifactDelta);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.wasIsProvider = wasIsProvider;
      this.attrId = attrId;
      this.attributeType = attributeType;
      this.artModType = artModType;
      this.isUri = isUri;
      this.wasUri = wasUri;
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
      hashCode += 7 * getAttrId().hashCode();
      return hashCode;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getNameOrToken() {
      return getArtifactNameOrToken();
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
      if (isValue == null && wasIsProvider != null) {
         isValue = wasIsProvider.getIsValue();
      }
      return isValue != null ? isValue : "";
   }

   @Override
   public String getWasValue() {
      if (wasValue == null && wasIsProvider != null) {
         wasValue = wasIsProvider.getWasValue();
      }
      return wasValue != null ? wasValue : "";
   }

   public Attribute<?> getAttribute() {
      List<Attribute<?>> attributes = getChangeArtifact().getAttributes(true);
      for (Attribute<?> attribute : attributes) {
         if (attribute.getId().equals(attrId.getId())) {
            return attribute;
         }
      }
      throw new AttributeDoesNotExist("Attribute %s could not be found on artifact %s on branch %s", attrId, getArtId(),
         getBranch());
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
   public Id getItemTypeId() {
      return getAttributeType();
   }

   @Override
   public AttributeId getItemId() {
      return attrId;
   }

   @Override
   public ChangeType getChangeType() {
      return changeType;
   }

   public String getWasUri() {
      return wasUri;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   public String getIsUri() {
      return isUri;
   }
}