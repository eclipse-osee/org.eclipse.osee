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

import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MergeChangesInArtifactException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/*
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class AttributeConflict extends Conflict {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AttributeConflict.class);
   public static final String EMPTY_XML = "<w:p><w:r><w:t></w:t></w:r></w:p>";
   public final static String NO_VALUE = "";
   public final static String STREAM_DATA = "Stream data";
   public final static String RESOLVE_MERGE_MARKUP =
         "Can not mark as resolved an attribute that has merge markup.  Finish merging the document to be able to resolve the conflict.";
   public final static String DIFF_MERGE_MARKUP =
         "Can not run a diff against an attribute that has merge markup.  Finish merging the document to be able to resolve the conflict.";
   private String sourceDestDiffFile = null;
   private final int attrId;
   private final int attrTypeId;
   private Object sourceObject;
   private Object destObject;
   private Attribute<?> attribute = null;
   private Attribute<?> sourceAttribute = null;
   private Attribute<?> destAttribute = null;
   private AttributeType dynamicAttributeDescriptor;
   private final boolean isWordAttribute;
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
    */
   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, String sourceValue, int attrId, int attrTypeId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) throws SQLException, OseeCoreException {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.status = Status.EDITED;
      isWordAttribute = sourceValue == null;
   }

   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId commitTransaction, String sourceValue, int attrId, int attrTypeId, Branch mergeBranch, Branch destBranch) {
      super(sourceGamma, destGamma, artId, commitTransaction, mergeBranch, destBranch);
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.status = Status.EDITED;
      isWordAttribute = sourceValue == null;
   }

   public Attribute<?> getAttribute() throws OseeCoreException, SQLException {
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
               "Attribute " + attrId + " could not be found on " + getArtId() + " on Branch " + mergeBranch.getBranchId());
      }
      return attribute;
   }

   public Attribute<?> getSourceAttribute() throws OseeCoreException, SQLException {
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
               "Attribute " + attrId + " could not be found on " + getArtId() + " on Branch " + sourceBranch.getBranchId());
      }
      return sourceAttribute;
   }

   public Attribute<?> getDestAttribute() throws OseeCoreException, SQLException {
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
               "Attribute " + attrId + " could not be found on " + getArtId() + " on Branch " + destBranch.getBranchId());
      }
      return destAttribute;
   }

   /**
    * @return the dynamicAttributeDescriptor
    * @throws SQLException
    */
   public AttributeType getDynamicAttributeDescriptor() throws OseeCoreException, SQLException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor =
               AttributeTypeManager.getTypeWithWordContentCheck(getArtifact(),
                     AttributeTypeManager.getType(attrTypeId).getName());
      }
      return dynamicAttributeDescriptor;
   }

   public Object getSourceObject() throws OseeCoreException, SQLException {
      if (sourceObject != null) return sourceObject;
      try {
         sourceObject = getSourceAttribute().getValue();
      } catch (AttributeDoesNotExist ex) {
         sourceObject = new String("DELETED");
      }
      return sourceObject;
   }

   public Object getDestObject() throws OseeCoreException, SQLException {
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
      //      return AttributeChangeIcons.getImage(getChangeType(), ModificationType.CHANGE);
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
         Branch defaultBranch = BranchPersistenceManager.getDefaultBranch();
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
         logger.log(Level.SEVERE, ex.toString(), ex);
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
   public String getDestDisplayData() throws OseeCoreException, SQLException {
      return isWordAttribute ? STREAM_DATA : getDestObject() == null ? "Null Value" : getDestObject().toString();
   }

   @Override
   public String getSourceDisplayData() throws OseeCoreException, SQLException {
      return isWordAttribute ? STREAM_DATA : getSourceObject() == null ? "Null Value" : getSourceObject().toString();
   }

   @Override
   public boolean mergeEqualsSource() throws OseeCoreException, SQLException {
      if (getMergeObject() == null || getSourceObject() == null) return false;
      return (getMergeObject().equals(getSourceObject()));
   }

   @Override
   public boolean mergeEqualsDestination() throws OseeCoreException, SQLException {
      if (getMergeObject() == null || getDestObject() == null) return false;
      return (getMergeObject().equals(getDestObject()));
   }

   @Override
   public boolean sourceEqualsDestination() throws OseeCoreException, SQLException {
      if (getSourceObject() == null || getDestObject() == null) return false;
      return (getSourceObject().equals(getDestObject()));
   }

   public Object getMergeObject() throws OseeCoreException, SQLException {
      return getAttribute().getValue();
   }

   public TreeSet<String> getEnumerationAttributeValues() throws OseeCoreException, SQLException {
      return new TreeSet<String>(ConfigurationPersistenceManager.getValidEnumerationAttributeValues(
            getDynamicAttributeDescriptor().getName(), getArtifact().getBranch()));
   }

   @SuppressWarnings("unchecked")
   public Class<? extends Attribute> getBaseAttributeClass() throws OseeCoreException, SQLException {
      return getDynamicAttributeDescriptor().getBaseAttributeClass();
   }

   public boolean setStringAttributeValue(String value) throws OseeCoreException, SQLException {
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

   public boolean setAttributeValue(Object value) throws OseeCoreException, SQLException {
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
   public boolean setToSource() throws OseeCoreException, SQLException {
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
   public boolean setToDest() throws OseeCoreException, SQLException {
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
   public boolean clearValue() throws OseeCoreException, SQLException {
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
      setStatus(Status.UNTOUCHED);
      if (isWordAttribute) {
         ((WordAttribute) getAttribute()).initializeToDefaultValue();
         getArtifact().persistAttributes();
      } else {
         getArtifact().setSoleAttributeFromString(getDynamicAttributeDescriptor().getName(), NO_VALUE);
         getArtifact().persistAttributes();
      }
      return true;
   }

   public void markStatusToReflectEdit() throws OseeCoreException, SQLException {
      if ((status.equals(Status.UNTOUCHED)) || (status.equals(Status.OUT_OF_DATE))) setStatus(Status.EDITED);
   }

   public Status computeStatus() throws OseeCoreException, SQLException {
      Status passedStatus = Status.UNTOUCHED;
      try {
         getSourceAttribute();
      } catch (AttributeDoesNotExist ex) {
         passedStatus = Status.INFORMATIONAL;
      }
      try {
         getDestAttribute();
      } catch (AttributeDoesNotExist ex) {
         passedStatus = Status.NOT_RESOLVABLE;
      }
      Status status = super.computeStatus(attrId, passedStatus);
      //      if (DEBUG) {
      //         System.out.println(String.format("Attribute Conflict: Computed Status Value for AttrId %d to %s", getAttrId(),
      //               status));
      //      }
      return status;
   }

   @Override
   public String getMergeDisplayData() throws OseeCoreException, SQLException {
      if ((statusUntouched() && !(sourceEqualsDestination() && mergeEqualsSource())) || statusNotResolvable() || statusInformational()) {
         return NO_VALUE;
      }
      if (!isWordAttribute) {
         return getMergeObject() == null ? null : getMergeObject().toString();
      }
      return AttributeConflict.STREAM_DATA;
   }

   @Override
   public String getChangeItem() throws OseeCoreException, SQLException {
      return getDynamicAttributeDescriptor().getName();
   }

   @Override
   public ConflictType getConflictType() {
      return ConflictType.ATTRIBUTE;
   }

   public int getMergeGammaId() throws OseeCoreException, SQLException {
      return getAttribute().getGammaId();
   }

   public boolean isWordAttribute() {
      return isWordAttribute;
   }

   public void setStatus(Status status) throws OseeCoreException, SQLException {
      if (status.equals(Status.RESOLVED) && isWordAttribute && ((WordAttribute) getAttribute()).mergeMarkupPresent()) {
         throw new MergeChangesInArtifactException(RESOLVE_MERGE_MARKUP);
      }
      super.setStatus(status);
   }

   public boolean wordMarkupPresent() throws OseeCoreException, SQLException {
      if (isWordAttribute() && ((WordAttribute) getAttribute()).mergeMarkupPresent()) {
         return true;
      }
      return false;
   }

   public void revertSourceAttribute() throws OseeCoreException, SQLException {
      if (DEBUG) {
         System.out.println(String.format("AttributeConflict: Reverting Attribute %d", getAttrId()));
      }
      ArtifactPersistenceManager.getInstance().revertAttribute(getSourceArtifact(), getSourceAttribute());
   }

}
