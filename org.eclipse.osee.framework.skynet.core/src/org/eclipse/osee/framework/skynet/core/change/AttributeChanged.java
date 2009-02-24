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
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AttributeChanged extends Change {
   private final String isValue;
   private String wasValue;
   private final int attrId;
   private final int attrTypeId;
   private AttributeType dynamicAttributeDescriptor;
   private ArtifactChange artifactChange;
   private final ModificationType artModType;

   /**
    * @param sourceGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    * @param isValue
    * @param sourceContent
    * @param attrId
    * @param attrTypeId
    */
   public AttributeChanged(Branch branch, int artTypeId, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, String isValue, String wasValue, int attrId, int attrTypeId, ModificationType artModType, boolean isHistorical) {
      super(branch, artTypeId, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.artModType = artModType;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeChanged) {
         AttributeChanged change = (AttributeChanged) obj;
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

   /**
    * @return the attrId
    */
   public int getAttrId() {
      return attrId;
   }

   /**
    * @return the attrTypeId
    */
   public int getAttrTypeId() {
      return attrTypeId;
   }

   /**
    * @return the dynamicAttributeDescriptor
    */
   public AttributeType getDynamicAttributeDescriptor() throws Exception {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = AttributeTypeManager.getType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }

   @Override
   public Image getItemTypeImage() {
      return AttributeChangeIcons.getImage(getChangeType(), getModificationType());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getName()
    */
   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getTypeName()
    */
   @Override
   public String getItemTypeName() throws Exception {
      return getDynamicAttributeDescriptor().getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getItemKind()
    */
   @Override
   public String getItemKind() {
      return "Attribute";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getValue()
    */
   @Override
   public String getIsValue() {
      return isValue != null ? isValue : "";
   }

   /**
    * @return the wasValue
    */
   @Override
   public String getWasValue() {
      return wasValue;
   }

   /**
    * @param wasValue the wasValue to set
    */
   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

   private ArtifactChange getArtifactChange() throws ArtifactDoesNotExist {
      if (artifactChange == null) {
         artifactChange =
               new ArtifactChange(getChangeType(), getArtModType(), getArtifact(), null, null, getFromTransactionId(),
                     getFromTransactionId(), getToTransactionId(), getGamma());
      }
      return artifactChange;
   }

   public Attribute<?> getAttribute() throws OseeCoreException {
      for (Attribute<?> attribute : getArtifact().getAttributes(true)) {
         if (attribute.getAttrId() == attrId) {
            return attribute;
         }
      }
      throw new AttributeDoesNotExist(String.format("Could not find Attribute %d on Artifact %d", attrId, getArtId()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      try {
         // this is a temporary fix until the old change report goes away.
         if (adapter.isInstance(getArtifactChange())) {
            return getArtifactChange();
         }
         if (adapter.isInstance(getArtifact())) {
            return getArtifactChange().getArtifact();
         }
         try {
            if (adapter.isInstance(getAttribute())) {
               return getAttribute();
            }
         } catch (AttributeDoesNotExist ex) {
            return null;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.change.Change#getObjectImage()
    */
   @Override
   public Image getItemKindImage() throws OseeCoreException {
      return ArtifactTypeManager.getType(artTypeId).getImage(getChangeType(), getModificationType());
   }

   /**
    * @return the artModType
    */
   public ModificationType getArtModType() {
      return artModType;
   }
}
