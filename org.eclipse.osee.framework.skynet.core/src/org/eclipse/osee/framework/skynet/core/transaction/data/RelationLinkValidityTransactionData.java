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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_RELATIONS_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Objects;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Robert A. Fisher
 */
public class RelationLinkValidityTransactionData implements ITransactionData {
   private static final String INSERT_VALID_RELATION =
         "INSERT INTO " + VALID_RELATIONS_TABLE + " (art_type_id, rel_link_type_id, side_a_max, side_b_max, gamma_id) VALUES (?, ?, ?, ?, ?)";

   private final int artTypeId;
   private final int relLinkTypeId;
   private final int sideAMax;
   private final int sideBMax;
   private final int gammaId;
   private final int transactionId;
   private ModificationType modificationType;
   private List<Object> dataItems;

   /**
    * @param artTypeId
    * @param relLinkTypeId
    * @param sideAMax
    * @param sideBMax
    * @param gammaId
    * @param transactionId
    */
   public RelationLinkValidityTransactionData(int artTypeId, int relLinkTypeId, int sideAMax, int sideBMax, int gammaId, int transactionId) {
      this.artTypeId = artTypeId;
      this.relLinkTypeId = relLinkTypeId;
      this.sideAMax = sideAMax;
      this.sideBMax = sideBMax;
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
      return INSERT_VALID_RELATION;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artTypeId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(relLinkTypeId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(sideAMax);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(sideBMax);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationLinkValidityTransactionData) {
         RelationLinkValidityTransactionData other = (RelationLinkValidityTransactionData) obj;
         return other.artTypeId == artTypeId && other.relLinkTypeId == relLinkTypeId;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(11, 13, artTypeId, relLinkTypeId);
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
