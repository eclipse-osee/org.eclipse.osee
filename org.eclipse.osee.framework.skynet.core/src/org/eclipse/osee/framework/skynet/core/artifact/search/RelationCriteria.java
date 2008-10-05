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

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Ryan D. Brooks
 */
public class RelationCriteria extends AbstractArtifactSearchCriteria {
   private final RelationType relationType;
   private final RelationSide relationSide;
   private String txsAlias;
   private String txdAlias;
   private String relAlias;
   private int artifactId;

   /**
    * Constructor for search criteria that follows the relation link ending on the given side
    * 
    * @param relationEnum the side to start following the link from
    * @param value
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public RelationCriteria(IRelationEnumeration relationEnum) throws OseeTypeDoesNotExist, OseeDataStoreException {
      this(relationEnum.getRelationType(), relationEnum.getSide());
   }

   public RelationCriteria(RelationType relationType, RelationSide relationSide) {
      this(0, relationType, relationSide);
   }

   public RelationCriteria(int artifactId, RelationType relationType, RelationSide relationSide) {
      this.artifactId = artifactId;
      this.relationType = relationType;
      this.relationSide = relationSide;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToTableSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToTableSql(ArtifactQueryBuilder builder) {
      relAlias = builder.appendAliasedTable("osee_relation_link");
      txsAlias = builder.appendAliasedTable("osee_txs");
      txdAlias = builder.appendAliasedTable("osee_tx_details");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToWhereSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToWhereSql(ArtifactQueryBuilder builder) {
      if (artifactId > 0) {
         builder.append(relAlias);
         builder.append(relationSide.isSideA() ? ".b_art_id" : ".a_art_id");
         builder.append("=? AND ");
         builder.addParameter(artifactId);
      }
      if (relationType != null) {
         builder.append(relAlias);
         builder.append(".rel_link_type_id=? AND ");
         builder.addParameter(relationType.getRelationTypeId());
      }

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
      boolean useArtA = (relationSide == RelationSide.SIDE_A) ^ left;
      builder.append(relAlias);
      builder.append(useArtA ? ".a_art_id" : ".b_art_id");
   }
}