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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;

/**
 * @author Jeff C. Phillips
 */
public class AttributeTransactionData extends BaseTransactionData {
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri, modification_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

   private final int artId;
   private final int attrTypeId;
   private final String value;

   private final String uri;

   public AttributeTransactionData(int artId, int attrId, int attrTypeId, String value, int gammaId, TransactionId transactionId, String uri, ModificationType modificationType) {
      super(attrId, gammaId, transactionId, modificationType);
      this.artId = artId;
      this.attrTypeId = attrTypeId;
      this.value = value;
      this.uri = uri;
   }

   public void internalOnRollBack() throws OseeCoreException {
      if (Strings.isValid(uri)) {
         try {
            HttpProcessor.delete(AttributeURL.getDeleteURL(uri));
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return OseeSql.Transaction.SELECT_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertData()
    */
   @Override
   public Object[] getInsertData() {
      return new Object[] {artId, getItemId(), attrTypeId, value == null ? SQL3DataType.VARCHAR : value, getGammaId(),
            uri == null ? SQL3DataType.VARCHAR : uri, getModificationType().getValue()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertSql()
    */
   @Override
   public String getInsertSql() {
      return INSERT_ATTRIBUTE;
   }
}