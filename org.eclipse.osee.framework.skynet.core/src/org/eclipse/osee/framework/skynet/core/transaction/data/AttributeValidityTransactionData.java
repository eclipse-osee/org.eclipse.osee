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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_ATTRIBUTES_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Objects;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class AttributeValidityTransactionData implements ITransactionData {
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO " + VALID_ATTRIBUTES_TABLE + " (art_type_id, attr_type_id, gamma_id) VALUES (? , ? , ?)";

   private final int artTypeId;
   private final int attrTypeId;
   private final int gammaId;
   private final int transactionId;
   private ModificationType modificationType;
   private List<Object> dataItems;

   /**
    * @param artTypeId
    * @param attrTypeId
    * @param gammaId
    * @param transactionId
    */
   public AttributeValidityTransactionData(int artTypeId, int attrTypeId, int gammaId, int transactionId) {
      this.artTypeId = artTypeId;
      this.attrTypeId = attrTypeId;
      this.gammaId = gammaId;
      this.transactionId = transactionId;

      // Modification Type is not stored for this yet
      this.modificationType = ModificationType.NEW;
      this.dataItems = new LinkedList<Object>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_VALID_ATTRIBUTE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artTypeId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(attrTypeId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeValidityTransactionData) {
         AttributeValidityTransactionData other = (AttributeValidityTransactionData) obj;
         return other.artTypeId == artTypeId && other.attrTypeId == attrTypeId;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(17, 19, artTypeId, attrTypeId);
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
}
