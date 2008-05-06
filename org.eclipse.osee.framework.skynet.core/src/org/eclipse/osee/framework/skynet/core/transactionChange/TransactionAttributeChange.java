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

package org.eclipse.osee.framework.skynet.core.transactionChange;

import java.io.InputStream;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class TransactionAttributeChange extends TransactionChange {

   private int attrId;
   private int sourceGammaId;
   private String sourceValue;
   private InputStream sourceContent;
   private int destGammaId;
   private String destValue;
   private InputStream destContent;
   private int attrTypeId;
   private Branch sourceBranch;
   private DynamicAttributeDescriptor dynamicAttributeDescriptor;

   /**
    * @param modType
    * @param changeType
    * @param toTransactionId
    * @param fromTransactionId
    * @param attrId
    * @param sourceGammaId
    * @param sourceValue
    * @param sourceContent
    * @param destGammaId
    * @param destValue
    * @param destContent
    * @param attrTypeId
    */
   public TransactionAttributeChange(ModificationType modType, ChangeType changeType, int attrId, int sourceGammaId, String sourceValue, InputStream sourceContent, int destGammaId, String destValue, InputStream destContent, int attrTypeId, Branch sourceBranch) {
      super(modType, changeType, null, null);
      this.attrId = attrId;
      this.sourceGammaId = sourceGammaId;
      this.sourceValue = sourceValue;
      this.sourceContent = sourceContent;
      this.destGammaId = destGammaId;
      this.destValue = destValue;
      this.destContent = destContent;
      this.attrTypeId = attrTypeId;
      this.sourceBranch = sourceBranch;
   }

   /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.transactionChange.TransactionChange#getImage()
       */
   @Override
   public Image getImage() {
      return null;
   }

   /**
    * @return the attrId
    */
   public int getAttrId() {
      return attrId;
   }

   /**
    * @return the sourceGammaId
    */
   public int getSourceGammaId() {
      return sourceGammaId;
   }

   /**
    * @return the sourceValue
    */
   public String getSourceValue() {
      return sourceValue;
   }

   /**
    * @return the sourceContent
    */
   public InputStream getSourceContent() {
      return sourceContent;
   }

   /**
    * @return the destGammaId
    */
   public int getDestGammaId() {
      return destGammaId;
   }

   /**
    * @return the destValue
    */
   public String getDestValue() {
      return destValue;
   }

   /**
    * @return the destContent
    */
   public InputStream getDestContent() {
      return destContent;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

   /**
    * @return the dynamicAttributeDescriptor
    */
   public int getAttrTypeID() {
      return attrTypeId;
   }

   /**
    * @return the dynamicAttributeDescriptor
    * @throws SQLException
    */
   public DynamicAttributeDescriptor getDynamicAttributeDescriptor() throws Exception {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }
}
