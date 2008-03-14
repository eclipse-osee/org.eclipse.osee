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

import java.io.InputStream;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.change.ChangeIcons;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AttributeConflict extends Conflict {
   private String sourceValue;
   private String destValue;
   private InputStream sourceContent;
   private InputStream destContent;
   private int attrId;
   private int attrTypeId;
   private DynamicAttributeDescriptor dynamicAttributeDescriptor;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    * @param sourceValue
    * @param destValue
    * @param sourceContent
    * @param destContent
    * @param image
    * @param attrId
    * @param attrTypeId
    */
   public AttributeConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, TransactionType transactionType, ChangeType changeType, String sourceValue, String destValue, InputStream sourceContent, InputStream destContent, int attrId, int attrTypeId, Branch mergeBranch) {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, transactionType, changeType, mergeBranch);
      this.sourceValue = sourceValue;
      this.destValue = destValue;
      this.sourceContent = sourceContent;
      this.destContent = destContent;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
   }

   /**
    * @return the dynamicAttributeDescriptor
    * @throws SQLException
    */
   public DynamicAttributeDescriptor getDynamicAttributeDescriptor() throws SQLException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }

   public Image getImage() {
      return ChangeIcons.getImage(getChangeType(),
            TransactionType.convertTransactionTypeToModificationType(getTransactionType()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

   /**
    * @return the sourceValue
    */
   public String getSourceValue() {
      return sourceValue;
   }

   /**
    * @return the destValue
    */
   public String getDestValue() {
      return destValue;
   }

   /**
    * @return the sourceContent
    */
   public InputStream getSourceContent() {
      return sourceContent;
   }

   /**
    * @return the destContent
    */
   public InputStream getDestContent() {
      return destContent;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.conflict.Conflict#getDestDisplayData()
    */
   @Override
   public String getDestDisplayData() {
      return getSourceValue() != null ? getSourceValue() : "Stream data";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.conflict.Conflict#getSourceDisplayData()
    */
   @Override
   public String getSourceDisplayData() {
      return getDestValue() != null ? getDestValue() : "Stream data";
   }
}
