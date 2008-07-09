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
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/*
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class AttributeConflict extends Conflict {
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AttributeConflict.class);
   public static final String EMPTY_XML = "<w:p><w:r><w:t></w:t></w:r></w:p>";
   public final static String NO_VALUE = "";
   public final static String STREAM_DATA = "Stream data";
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
   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, String sourceValue, String destValue, int attrId, int attrTypeId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) throws SQLException, OseeCoreException {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, changeType, mergeBranch,
            sourceBranch, destBranch);
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
      sourceObject = getSourceAttribute().getValue();
      return sourceObject;
   }

   public Object getDestObject() throws OseeCoreException, SQLException {
      if (destObject != null) return destObject;
      destObject = getDestAttribute().getValue();
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

   public boolean setStringAttributeValue(String value) throws OseeCoreException, SQLException {
      if (!okToOverwriteMerge()) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeFromString(getDynamicAttributeDescriptor().getName(), value);
      getArtifact().persistAttributes();
      return true;
   }

   public boolean setAttributeValue(Object value) throws OseeCoreException, SQLException {
      if (!okToOverwriteMerge()) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), value);
      getArtifact().persistAttributes();
      return true;
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

   @Override
   public boolean setToSource() throws OseeCoreException, SQLException {
      if (!okToOverwriteMerge() || getSourceObject() == null) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getSourceObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean setToDest() throws OseeCoreException, SQLException {
      if (!okToOverwriteMerge() || getDestObject() == null) return false;
      markStatusToReflectEdit();
      getArtifact().setSoleAttributeValue(getDynamicAttributeDescriptor().getName(), getDestObject());
      getArtifact().persistAttributes();
      return true;
   }

   @Override
   public boolean clearValue() throws OseeCoreException, SQLException {
      if (!okToOverwriteMerge()) return false;
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

   @Override
   public Status computeStatus() throws OseeCoreException, SQLException {
      return super.computeStatus(attrId, Status.UNTOUCHED);
   }

   @Override
   public String getMergeDisplayData() throws OseeCoreException, SQLException {
      if ((status.equals(Status.UNTOUCHED)) && !(sourceEqualsDestination() && mergeEqualsSource())) {
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

   public int getMergeGammaId() throws OseeCoreException, SQLException {
      return getAttribute().getGammaId();
   }

   public boolean isWordAttribute() {
      return isWordAttribute;
   }

}
