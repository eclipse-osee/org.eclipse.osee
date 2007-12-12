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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class RelationLinkTypeTransactionData implements ITransactionData {
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO " + RELATION_LINK_TYPE_TABLE + "(rel_link_type_id, type_name, a_name, b_name, ab_phrasing, ba_phrasing, short_name, gamma_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

   private final int relLinkTypeId;
   private final String linkTypeName;
   private final String sideAName;
   private final String sideBName;
   private final String abPhrasing;
   private final String baPhrasing;
   private final String shortName;
   private final int gammaId;
   private final int transactionId;
   private ModificationType modificationType;
   private List<Object> dataItems;

   /**
    * @param relLinkTypeId
    * @param linkTypeName
    * @param sideAName
    * @param sideBName
    * @param abPhrasing
    * @param baPhrasing
    * @param shortName
    * @param gammaId
    * @param transactionId
    */
   public RelationLinkTypeTransactionData(int relLinkTypeId, String linkTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName, int gammaId, int transactionId) {
      this.relLinkTypeId = relLinkTypeId;
      this.linkTypeName = linkTypeName;
      this.sideAName = sideAName;
      this.sideBName = sideBName;
      this.abPhrasing = abPhrasing;
      this.baPhrasing = baPhrasing;
      this.shortName = shortName;
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
      return INSERT_RELATION_LINK_TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(relLinkTypeId);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(linkTypeName);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(sideAName);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(sideBName);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(abPhrasing);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(baPhrasing);
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(shortName);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationLinkTypeTransactionData) {
         RelationLinkTypeTransactionData other = (RelationLinkTypeTransactionData) obj;
         return other.linkTypeName.equals(linkTypeName);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return linkTypeName.hashCode();
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
