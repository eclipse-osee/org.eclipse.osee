/*
 * Created on Feb 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.transactionChange;

import java.io.InputStream;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
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
    * @param transactionType
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
   public TransactionAttributeChange(TransactionType transactionType, ChangeType changeType, int attrId, int sourceGammaId, String sourceValue, InputStream sourceContent, int destGammaId, String destValue, InputStream destContent, int attrTypeId, Branch sourceBranch) {

      super(transactionType, changeType, null, null);
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
   public DynamicAttributeDescriptor getDynamicAttributeDescriptor() throws SQLException {
      if (dynamicAttributeDescriptor == null) {
         dynamicAttributeDescriptor = ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attrTypeId);
      }
      return dynamicAttributeDescriptor;
   }
}
