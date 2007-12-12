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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Jeff C. Phillips
 */
public class RelationTransactionData implements ITransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO " + RELATION_LINK_VERSION_TABLE + " (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order_value, b_order_value, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final int PRIME_NUMBER = 7;

   private IRelationLink link;
   private int gammaId;
   private int transactionId;
   private ModificationType modificationType;
   private List<Object> dataItems;

   public RelationTransactionData(IRelationLink link, int gammaId, int transactionId, ModificationType modificationType) {
      super();
      this.link = link;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.dataItems = new LinkedList<Object>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_INTO_RELATION_TABLE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getPersistenceMemo().getLinkId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getLinkDescriptor().getPersistenceMemo().getLinkTypeId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getArtifactA().getArtId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getArtifactB().getArtId());
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(link.getRationale());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getAOrder());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getBOrder());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

      return dataItems;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationTransactionData) {
         return ((RelationTransactionData) obj).link.getPersistenceMemo().getLinkId() == link.getPersistenceMemo().getLinkId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return link.getPersistenceMemo().getLinkId() * PRIME_NUMBER;
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
