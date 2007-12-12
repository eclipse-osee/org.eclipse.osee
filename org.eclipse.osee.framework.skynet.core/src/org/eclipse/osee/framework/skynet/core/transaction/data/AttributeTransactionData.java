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
package org.eclipse.osee.framework.skynet.core.transaction.data;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public class AttributeTransactionData implements ITransactionData {
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO " + ATTRIBUTE_VERSION_TABLE + " (art_id, attr_id, attr_type_id, value, gamma_id, content, modification_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
   private static final int PRIME_NUMBER = 5;

   private int artId;
   private int attrId;
   private int attrTypeId;
   private String value;
   private int gammaId;
   private int transactionId;
   private InputStream content;
   private ModificationType modificationType;
   private List<Object> dataItems;

   public AttributeTransactionData(int artId, int attrId, int attrTypeId, String value, int gammaId, int transactionId, InputStream content, ModificationType modificationType) {
      super();
      this.artId = artId;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.value = value;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.content = content;
      this.modificationType = modificationType;
      this.dataItems = new LinkedList<Object>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_ATTRIBUTE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(attrId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(attrTypeId);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(value);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.BLOB);
      dataItems.add(content);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeTransactionData) {
         return ((AttributeTransactionData) obj).attrId == attrId;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return attrId * PRIME_NUMBER;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getGammaId()
    */
   public int getGammaId() {
      return gammaId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionId()
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * @return Returns the modificationType.
    */
   public ModificationType getModificationType() {
      return modificationType;
   }

   /**
    * @param modificationType The modificationType to set.
    */
   public void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   /**
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }
}
