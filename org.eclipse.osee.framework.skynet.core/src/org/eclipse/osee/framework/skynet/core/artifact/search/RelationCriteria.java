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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;

/**
 * @author Ryan D. Brooks
 */
public class RelationCriteria extends AbstractArtifactSearchCriteria {
   private IRelationEnumeration relationSide;
   private String txsAlias;
   private String txdAlias;
   private String relAlias;

   /**
    * Constructor for search criteria that follows the relation link ending on the given side
    * 
    * @param relationSide the side to start following the link from
    * @param value
    */
   public RelationCriteria(IRelationEnumeration relationSide) {
      super();
      this.relationSide = relationSide;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToTableSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToTableSql(ArtifactQueryBuilder builder) {
      relAlias = builder.appendAliasedTable("osee_define_rel_link");
      txsAlias = builder.appendAliasedTable("osee_define_txs");
      txdAlias = builder.appendAliasedTable("osee_define_tx_details");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToWhereSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToWhereSql(ArtifactQueryBuilder builder) throws SQLException {
      builder.append(relAlias);
      builder.append(".rel_link_type_id=? AND ");
      builder.addParameter(SQL3DataType.INTEGER, relationSide.getRelationType().getRelationTypeId());

      builder.append(relAlias);
      builder.append(".gamma_id=");
      builder.append(txsAlias);
      builder.append(".gamma_id AND ");

      builder.addCurrentTxSql(txsAlias, txdAlias);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addJoinArtId(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addJoinArtId(ArtifactQueryBuilder builder, boolean left) {
      boolean useArtA = relationSide.isSideA() ^ left;
      builder.append(relAlias);
      builder.append(useArtA ? ".a_art_id" : ".b_art_id");
   }
}