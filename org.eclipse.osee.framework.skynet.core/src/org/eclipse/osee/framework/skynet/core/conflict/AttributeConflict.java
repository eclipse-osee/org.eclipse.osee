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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MergeChangesInArtifactException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/*
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class AttributeConflict extends Conflict {
   public static final String EMPTY_XML = "<w:p><w:r><w:t></w:t></w:r></w:p>";
   public final static String NO_VALUE = "";
   public final static String STREAM_DATA = "Stream data";
   public final static String RESOLVE_MERGE_MARKUP =
         "Can not mark as resolved an attribute that has merge markup.  Finish merging the document to be able to resolve the conflict.";
   public final static String DIFF_MERGE_MARKUP =
         "Can not run a diff against an attribute that has merge markup.  Finish merging the document to be able to resolve the conflict.";
   private final int attrId;
   private final int attrTypeId;
   private Object sourceObject;
   private Object destObject;
   private Attribute<?> attribute = null;
   private Attribute<?> sourceAttribute = null;
   private Attribute<?> destAttribute = null;
   private AttributeType dynamicAttributeDescriptor;
   private boolean isWordAttribute;
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Merge"));

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param modType
    * @param changeType
    * @param sourceValue
    * @param destValue
    * @param sourceContent
    * @param destContent
    * @param image
    * @param attrId
    * @param attrTypeId
    * @throws OseeCoreException
    */
   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, String sourceValue, int attrId, int attrTypeId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) throws OseeCoreException {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.status = ConflictStatus.EDITED;
      this.isWordAttribute = getAttribute() instanceof WordAttribute;
   }

   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId commitTransaction, String sourceValue, int attrId, int attrTypeId, Branch mergeBranch, Branch destBranch) throws OseeCoreException {
      super(sourceGamma, destGamma, artId, commitTransaction, mergeBranch, destBranch);
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.status = ConflictStatus.EDITED;
      this.isWordAttribute = getAttribute() instanceof WordAttribute;
   }

   public Attribute<?> getAttribute() throws OseeCoreException {
      if (attribute != null) return attribute;
      Collection<Attribute<Object>> localAttributes =
            getArtifact().getAttributes(getDynamicAttributeDescriptor().getName());
      for (Attribute<Object> localAttribute : localAttributes) {
         if (localAttribute.getAttrId() == attrId) {
            attribute = localAttribute;
         }
      }
      if (attribute == null) {
         throw new AttributeDoesNotExist(
               "Attribute " + attrId + " could not be found on Artifact " + getArtId() + " on Branch " + mergeBranch.getBranchId());
      }
      return attribute;
   }

   public Attribute<?> getSourceAttribute() throws OseeCoreException {
      if (sourceAttribute != null) return sourceAttribute;
      Collection<Attribute<Object>> localAttributes =
            getSourceArtifact().getAttributes(getDynamicAttributeDescriptor().getName());
      for (Attribute<Object> localAttribute : localAttributes) {
         if (localAttribute.getAttrId() == attrId) {
            sourceAttribute = localAttribute;
         }
      }
      if (sourceAttribute == null) {
         throw new AttributeDoesNotExist(
               "Attribute " + attrId + " could not be found on Artifact " + getArtId() + " on Branch " + sourceBranch.getBranchId());
      }
      return sourceAttribute;
   }

   public Attribute<?> getDestAttribute() throws OseeCoreException {
      if (destAttribute != null) return destAttribute;
      Collection<Attribute<Object>> localAttributes =
            getDestArtifact().getAttributes(getDynamicAttributeDescriptor().getName());
      for (Attribute<Object> localAttribute : localAttributes) {
         if (localAttribute.getAttrId() == attrId) {
            destAttribute = localAttribute;
         }
      }
      if (destAttribute == null) {
         throw new AttributeDoesNotExist(
               "Attribute " + attrId + " could not be found on Artifact " + getArtId() + " on Branch " + destBranch.getBranchId());
      }
      return destAttribute;
   }

   private Attribute<?> getAttribute(Artifact artifact) throws OseeCoreException {
      Attribute<?> attribute = null;
      Collection<Attribute<Object>> localAttributes = artifact.getAttributes(getDynamicAttributeDescriptor().getName());
      for (Attribute<Object> localAttribute : localAttributes) {
         if (localAttribute.getAttrId() == attrId) {
            attribute = localAttribute;
         }
      }
      if (attribute == null) {
         throw new AttributeDoesNotExist(
               "Attribute " + attrId + " could not be found on Artifact " + artifact.getArtId() + " on Branch " + artifact.getBranch().getBranchId());
      }
      return attribute;
   }

   /**
    * @return the dynamicAttributeDescriptor
    * @throws OseeCoreException
    */
   public AttributeType getDynamicAttributeDescriptor() throws OseeCoreException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = AttributeTypeManager.getType(AttributeTypeManager.getType(attrTypeId).getName());
      }
      return dynamicAttributeDescriptor;
   }

   public Object getSourceObject() throws OseeCoreException {
      if (sourceObject != null) return sourceObject;
      try {
         sourceObject = getSourceAttribute().getValue();
      } catch (AttributeDoesNotExist ex) {
         sourceObject = new String("DELETED");
      }
      return sourceObject;
   }

   public Object getDestObject() throws OseeCoreException {
      if (destObject != null) return destObject;
      try {
         destObject = getDestAttribute().getValue();
      } catch (AttributeDoesNotExist ex) {
         destObject = new String("DELETED");
      }
      return destObject;
   }

   @Override
   public Image getImage() {
      return SkynetActivator.getInstance().getImage("molecule.gif");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }

      try {
         Artifact artifact;
         Attribute attribute = getSourceAttribute();
         if (adapter.isInstance(attribute)) {
            return attribute;
         }
         Branch defaultBranch = BranchManager.getDefaultBranch();
         if (defaultBranch.equals(sourceBranch)) {
            artifact = getSourceArtifact();
            if (adapter.isInstance(artifact)) {
               return artifact;
            }
         }
         if (defaultBranch.equals(destBranch)) {
            artifact = getDestArtifact();
            if (adapter.isInstance(artifact)) {
               return artifact;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }

      return null;
   }

   public int getAttrId() {
      return attrId;
   }

   public int getAttrTypeId() {
      return attrTypeId;
   }

   @Override
   public String getDestDisplayData() throws OseeCoreException {
      return isWordAttribute ? STREAM_DATA : getDestObject() == null ? "Null Value" : getDestObject().toString();
   }

   @Override
   public String getSourceDisplayData() throws OseeCoreException {
      return isWordAttribute ? STREAM_DATA : getSourceObject() == null ? "Null Value" : getSourceObject().toString();
   }

   @Override
   public boolean mergeEqualsSource() throws OseeCoreException {
      if (getMergeObject() == null || getSourceObject() == null) return false;
      return (getMergeObject().equals(getSourceObject()));
   }

   @Override
   public boolean mergeEqualsDestination() throws OseeCoreException {
      if (getMergeObject() == null || getDestObject() == null) return false;
      return (getMergeObject().equals(getDestObject()));
   }

   @Override
   public boolean sourceEqualsDestination() throws OseeCoreException {
      if (getSourceObject() == null || getDestObject() == null) return false;
      return (getSourceObject().equals(getDestObject()));
   }

   public Object getMergeObject() throws OseeCoreException {
      return getAttribute().getValue();
   }

   public TreeSet<String> getEnumerationAttributeValues() throws OseeCoreException {
      return new TreeSet<String>(AttributeTypeManager.getValidEnumerationAttributeValues(
            getDynamicAttributeDescriptor().getName(), getArtifact().getBranch()));
   }

   @SuppressWarnings("unchecked")
   public Class<? extends Attribute> getBaseAttributeClass() throws OseeCoreException {
      return getDynamicAttributeDescriptor().getBaseAttributeClass();
   }

   public boolean setStringAttributeValue(String value) throws OseeCoreException {
      if (!okToOverwriteMerge()) {
         if (DEBUG) {
            System.out.println(String.format("AttributeConflict: Failed setting the Merge Value for attr_id %d",
                  getAttrId()));
         }
         return false;
      }
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Set the Merge Value for attr_id %d", getAttrId()));
      }
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeFromString(getDynamicAttributeDescriptor().getName(), value);
      getArtifact().persistAttributes();
      return true;
   }

   public boolean setAttributeValue(Object value) throws OseeCoreException {
      if (!okToOverwriteMerge()) {
         if (DEBUG) {
            System.out.println(String.format("AttributeConflict: Failed setting the Merge Value for attr_id %d",
                  getAttrId()));
         }
         return false;
      }
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Set the Merge Value for attr_id %d", getAttrId()));
      }
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), value);
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean setToSource() throws OseeCoreException {
      if (!okToOverwriteMerge() || getSourceObject() == null) {
         if (DEBUG) {
            System.out.println(String.format(
                  "AttributeConflict: Failed setting the Merge Value to the Source Value for attr_id %d", getAttrId()));
         }
         return false;
      }
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Set the Merge Value to the Source Value for attr_id %d",
               getAttrId()));
      }
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getSourceObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean setToDest() throws OseeCoreException {
      if (!okToOverwriteMerge() || getDestObject() == null) {
         if (DEBUG) {
            System.out.println(String.format(
                  "AttributeConflict: Failed setting the Merge Value to the Dest Value for attr_id %d", getAttrId()));
         }
         return false;
      }
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Set the Merge Value to the Dest Value for attr_id %d",
               getAttrId()));
      }
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getDestObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean clearValue() throws OseeCoreException {
      if (!okToOverwriteMerge()) {
         if (DEBUG) {
            System.out.println(String.format("AttributeConflict: Failed to clear the Merge Value for attr_id %d",
                  getAttrId()));
         }
         return false;
      }
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Cleared the Merge Value for attr_id %d", getAttrId()));
      }
      setStatus(ConflictStatus.UNTOUCHED);
      if (isWordAttribute) {
         ((WordAttribute) getAttribute()).initializeToDefaultValue();
         getArtifact().persistAttributes();
      } else {
         getArtifact().setSoleAttributeFromString(getDynamicAttributeDescriptor().getName(), NO_VALUE);
         getArtifact().persistAttributes();
      }
      return true;
   }

   public void markStatusToReflectEdit() throws OseeCoreException {
      if ((status.equals(ConflictStatus.UNTOUCHED)) || (status.equals(ConflictStatus.OUT_OF_DATE_COMMITTED) || (status.equals(ConflictStatus.OUT_OF_DATE)))) setStatus(ConflictStatus.EDITED);
   }

   public ConflictStatus computeStatus() throws OseeCoreException {
      ConflictStatus passedStatus = ConflictStatus.UNTOUCHED;
      try {
         getSourceAttribute();
      } catch (AttributeDoesNotExist ex) {
         passedStatus = ConflictStatus.INFORMATIONAL;
      }
      try {
         getDestAttribute();
      } catch (AttributeDoesNotExist ex) {
         passedStatus = ConflictStatus.NOT_RESOLVABLE;
      }
      return super.computeStatus(attrId, passedStatus);
   }

   public int getObjectId() throws OseeCoreException {
      return attrId;
   }

   @Override
   public String getMergeDisplayData() throws OseeCoreException {
      if ((statusUntouched() && !(sourceEqualsDestination() && mergeEqualsSource())) || statusNotResolvable() || statusInformational()) {
         return NO_VALUE;
      }
      if (!isWordAttribute) {
         return getMergeObject() == null ? null : getMergeObject().toString();
      }
      return AttributeConflict.STREAM_DATA;
   }

   @Override
   public String getChangeItem() throws OseeCoreException {
      return getDynamicAttributeDescriptor().getName();
   }

   @Override
   public ConflictType getConflictType() {
      return ConflictType.ATTRIBUTE;
   }

   public int getMergeGammaId() throws OseeCoreException {
      return getAttribute().getGammaId();
   }

   public boolean isWordAttribute() {
      return isWordAttribute;
   }

   public void setStatus(ConflictStatus status) throws OseeCoreException {
      if (status.equals(ConflictStatus.RESOLVED) && isWordAttribute && ((WordAttribute) getAttribute()).mergeMarkupPresent()) {
         throw new MergeChangesInArtifactException(RESOLVE_MERGE_MARKUP);
      }
      super.setStatus(status);
   }

   public boolean wordMarkupPresent() throws OseeCoreException {
      if (isWordAttribute && ((WordAttribute) getAttribute()).mergeMarkupPresent()) {
         return true;
      }
      return false;
   }

   public void revertSourceAttribute() throws OseeCoreException {
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Reverting Attribute %d", getAttrId()));
      }
      getSourceAttribute().revert();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#applyPreviousMerge(int)
    */
   @Override
   public boolean applyPreviousMerge(int mergeBranchId) throws OseeCoreException {
      if (DEBUG) {
         System.out.println("Apply the merge using the merge branch value " + mergeBranchId);
      }
      Artifact artifact;
      try {
         artifact = ArtifactQuery.getArtifactFromId(getArtifact().getArtId(), BranchManager.getBranch(mergeBranchId));
      } catch (ArtifactDoesNotExist ex) {
         return false;
      }
      setAttributeValue(getAttribute(artifact).getValue());
      setStatus(ConflictStatus.OUT_OF_DATE);
      return true;
   }
}
