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

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class RelationCriteria extends AbstractArtifactSearchCriteria {
   private final IRelationType relationType;
   private final RelationSide relationSide;
   private String txsAlias;
   private String relAlias;
   private final int artifactId;

   /**
    * Constructor for search criteria that follows the relation link ending on the given side
    * 
    * @param relationEnum the side to start following the link from
    */
   public RelationCriteria(IRelationEnumeration relationEnum) {
      this(relationEnum, relationEnum.getSide());
   }

   public RelationCriteria(IRelationType relationType, RelationSide relationSide) {
      this(0, relationType, relationSide);
   }

   public RelationCriteria(int artifactId, IRelationType relationType, RelationSide relationSide) {
      this.artifactId = artifactId;
      this.relationType = relationType;
      this.relationSide = relationSide;
   }

   @Override
   public void addToTableSql(ArtifactQueryBuilder builder) {
      relAlias = builder.appendAliasedTable("osee_relation_link");
      txsAlias = builder.appendAliasedTable("osee_txs");
   }

   @Override
   public void addToWhereSql(ArtifactQueryBuilder builder) throws OseeCoreException {
      if (artifactId > 0) {
         builder.append(relAlias);
         builder.append(relationSide.isSideA() ? ".b_art_id" : ".a_art_id");
         builder.append("=? AND ");
         builder.addParameter(artifactId);
      }
      if (relationType != null) {
         builder.append(relAlias);
         builder.append(".rel_link_type_id=? AND ");
         builder.addParameter(RelationTypeManager.getTypeId(relationType));
      }

      builder.append(relAlias);
      builder.append(".gamma_id=");
      builder.append(txsAlias);
      builder.append(".gamma_id AND ");

      builder.addTxSql(txsAlias, false);
   }

   @Override
   public void addJoinArtId(ArtifactQueryBuilder builder, boolean left) {
      boolean useArtA = relationSide == RelationSide.SIDE_A ^ left;
      builder.append(relAlias);
      builder.append(useArtA ? ".a_art_id" : ".b_art_id");
   }
}