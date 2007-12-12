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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class AttributeTypeTransactionData implements ITransactionData {
   private static final String INSERT_ATTRIBUTE_TYPE =
         "INSERT INTO " + ATTRIBUTE_TYPE_TABLE + " (attr_type_id, attr_base_type_id, name, default_value, validity_xml, min_occurence, max_occurence, tip_text, gamma_id)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private final int attrTypeId;
   private final int attrBaseTypeId;
   private final String name;
   private final String defaultValue;
   private final String validityXml;
   private final int minOccurrences;
   private final int maxOccurrences;
   private final String tipText;
   private final int gammaId;
   private final int transactionId;
   private ModificationType modificationType;
   private List<Object> dataItems;

   /**
    * @param attrTypeId
    * @param attrBaseTypeId
    * @param name
    * @param defaultValue
    * @param validityXml
    * @param minOccurrences
    * @param maxOccurrences
    * @param tipText
    * @param gammaId
    * @param transactionId
    */
   public AttributeTypeTransactionData(int attrTypeId, int attrBaseTypeId, String name, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, int gammaId, int transactionId) {
      this.attrTypeId = attrTypeId;
      this.attrBaseTypeId = attrBaseTypeId;
      this.name = name;
      this.defaultValue = defaultValue;
      this.validityXml = validityXml;
      this.minOccurrences = minOccurrences;
      this.maxOccurrences = maxOccurrences;
      this.tipText = tipText;
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
      return INSERT_ATTRIBUTE_TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(attrTypeId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(attrBaseTypeId);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(name);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(defaultValue);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(validityXml);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(minOccurrences);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(maxOccurrences);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(tipText);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributeTypeTransactionData) {
         AttributeTypeTransactionData other = (AttributeTypeTransactionData) obj;
         return other.name.equals(name);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return name.hashCode();
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
