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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeObjectConverter;
import org.eclipse.osee.framework.skynet.core.change.AttributeChangeIcons;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class AttributeConflict extends Conflict {
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AttributeConflict.class);
   public static final String EMPTY_XML = "<w:p><w:r><w:t></w:t></w:r></w:p>";
   public final static String NO_VALUE = "";
   public final static String STREAM_DATA = "Stream data";
   private String sourceDiffFile = null;
   private String destDiffFile = null;
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
   private final String sourceValue;
   private final String destValue;

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
   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, String sourceValue, String destValue, int attrId, int attrTypeId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) throws SQLException, IOException, Exception {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, changeType, mergeBranch,
            sourceBranch, destBranch);
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.status = Status.EDITED;
      this.sourceValue = sourceValue;
      this.destValue = destValue;
      isWordAttribute = sourceValue == null;
   }

   public Attribute<?> getAttribute() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
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

   public Attribute<?> getSourceAttribute() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
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

   public Attribute<?> getDestAttribute() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
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
   public AttributeType getDynamicAttributeDescriptor() throws Exception {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = AttributeTypeManager.getType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }

   public Object getSourceObject() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if (sourceObject != null) return sourceObject;
      if (isWordAttribute) {
         sourceObject = getSourceAttribute().getValue();
      } else {
         sourceObject =
               AttributeObjectConverter.stringToObject(getDynamicAttributeDescriptor().getBaseAttributeClass(),
                     sourceValue);
      }
      return sourceObject;
   }

   public Object getDestObject() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if (destObject != null) return destObject;
      if (isWordAttribute) {
         destObject = getDestAttribute().getValue();
      } else {
         destObject =
               AttributeObjectConverter.stringToObject(getDynamicAttributeDescriptor().getBaseAttributeClass(),
                     destValue);
      }
      return destObject;
   }

   @Override
   public Image getImage() {
      return AttributeChangeIcons.getImage(getChangeType(), getModificationType());
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
         Branch defaultBranch = branchPersistenceManager.getDefaultBranch();
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
   public String getDestDisplayData() {
      return isWordAttribute ? STREAM_DATA : destObject.toString();
   }

   @Override
   public String getSourceDisplayData() {
      return isWordAttribute ? STREAM_DATA : sourceObject.toString();
   }

   @Override
   public boolean mergeEqualsSource() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      return (getMergeObject().equals(getSourceObject()));
   }

   @Override
   public boolean mergeEqualsDestination() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      return (getMergeObject().equals(getDestObject()));
   }

   @Override
   public boolean sourceEqualsDestination() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      return (getSourceObject().equals(getDestObject()));
   }

   public boolean setAttributeValue(Object value) throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      if (!okToOverwriteMerge()) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), value);
      getArtifact().persistAttributes();
      return true;
   }

   public Object getMergeObject() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      return getAttribute().getValue();
   }

   public TreeSet<String> getEnumerationAttributeValues() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      return new TreeSet<String>(ConfigurationPersistenceManager.getInstance().getValidEnumerationAttributeValues(
            getDynamicAttributeDescriptor().getName(), getArtifact().getBranch()));
   }

   @SuppressWarnings("unchecked")
   public Class<? extends Attribute> getBaseAttributeClass() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      return getDynamicAttributeDescriptor().getBaseAttributeClass();
   }

   @Override
   public boolean setToSource() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if (!okToOverwriteMerge()) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getSourceObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean setToDest() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if (!okToOverwriteMerge()) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getDestObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean clearValue() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if (!okToOverwriteMerge()) return false;
      setStatus(Status.UNTOUCHED);
      if (isWordAttribute) {
         ((WordAttribute) getAttribute()).initializeToDefaultValue();

      } else {
         getArtifact().setSoleXAttributeValue(getDynamicAttributeDescriptor().getName(), NO_VALUE);
         getArtifact().persistAttributes();
      }
      return true;
   }

   protected void markStatusToReflectEdit() throws SQLException {
      if ((status.equals(Status.UNTOUCHED)) || (status.equals(Status.OUT_OF_DATE))) setStatus(Status.EDITED);
   }

   @Override
   public Status computeStatus() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      return super.computeStatus(attrId, Status.UNTOUCHED);
   }

   @Override
   public String getMergeDisplayData() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, AttributeDoesNotExist, Exception {
      if ((status.equals(Status.UNTOUCHED)) && !(sourceEqualsDestination() && mergeEqualsSource())) {
         return NO_VALUE;
      }
      if (!isWordAttribute) {
         return getMergeObject().toString();
      }
      return AttributeConflict.STREAM_DATA;
   }

   @Override
   public String getChangeItem() throws SQLException, Exception {
      return getDynamicAttributeDescriptor().getName();
   }

   @Override
   public ConflictType getConflictType() {
      return ConflictType.ATTRIBUTE;
   }

   /**
    * @return the sourceDiffFile
    */
   public String getSourceDiffFile() {
      return sourceDiffFile;
   }

   /**
    * @param sourceDiffFile the sourceDiffFile to set
    */
   public void setSourceDiffFile(String sourceDiffFile) {
      this.sourceDiffFile = sourceDiffFile;
   }

   /**
    * @return the destDiffFile
    */
   public String getDestDiffFile() {
      return destDiffFile;
   }

   /**
    * @param destDiffFile the destDiffFile to set
    */
   public void setDestDiffFile(String destDiffFile) {
      this.destDiffFile = destDiffFile;
   }

   /**
    * @return the sourceDestDiffFile
    */
   public String getSourceDestDiffFile() {
      return sourceDestDiffFile;
   }

   /**
    * @param sourceDestDiffFile the sourceDestDiffFile to set
    */
   public void setSourceDestDiffFile(String sourceDestDiffFile) {
      this.sourceDestDiffFile = sourceDestDiffFile;
   }

   public int getMergeGammaId() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      return getAttribute().getGammaId();
   }

}
